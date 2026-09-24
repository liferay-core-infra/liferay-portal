/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.journal.constants.JournalConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionService;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jiefeng Wu
 */
@RunWith(Arquillian.class)
public class ResourcePermissionServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_user = UserTestUtil.addGroupAdminUser(_group);

		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));
	}

	@After
	public void tearDown() {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
	}

	@Test
	public void testSetIndividualResourcePermissionsOnCompanyResource()
		throws Exception {

		_assertSetIndividualResourcePermissionsFails(
			_group.getGroupId(), PortletKeys.SERVER_ADMIN,
			PortletKeys.SERVER_ADMIN);
	}

	@Test
	public void testSetIndividualResourcePermissionsOnCompositeModelResource()
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		_assertSetIndividualResourcePermissions(
			_group.getGroupId(),
			ResourceActionsUtil.getCompositeModelName(
				DDMStructure.class.getName(), JournalArticle.class.getName()),
			String.valueOf(ddmStructure.getStructureId()));
	}

	@Test
	public void testSetIndividualResourcePermissionsOnGroupResource()
		throws Exception {

		_assertSetIndividualResourcePermissions(
			_group.getGroupId(), JournalConstants.RESOURCE_NAME,
			String.valueOf(_group.getGroupId()));
	}

	@Test
	public void testSetIndividualResourcePermissionsOnLayoutScopeGroupResource()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		Group scopeGroup = GroupTestUtil.addGroup(_user.getUserId(), layout);

		_assertSetIndividualResourcePermissions(
			scopeGroup.getGroupId(), JournalConstants.RESOURCE_NAME,
			String.valueOf(_group.getGroupId()));
	}

	@Test
	public void testSetIndividualResourcePermissionsOnOtherGroupPortletResource()
		throws Exception {

		_otherGroup = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(_otherGroup);

		_assertSetIndividualResourcePermissionsFails(
			_group.getGroupId(), PortletKeys.LOGIN,
			PortletPermissionUtil.getPrimaryKey(
				layout.getPlid(), PortletKeys.LOGIN));
	}

	@Test
	public void testSetIndividualResourcePermissionsOnPortletResource()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		_assertSetIndividualResourcePermissions(
			_group.getGroupId(), PortletKeys.LOGIN,
			PortletPermissionUtil.getPrimaryKey(
				layout.getPlid(), PortletKeys.LOGIN));
	}

	private void _assertSetIndividualResourcePermissions(
			long groupId, String name, String primKey)
		throws Exception {

		_resourcePermissionService.setIndividualResourcePermissions(
			groupId, _group.getCompanyId(), name, primKey,
			HashMapBuilder.put(
				_role.getRoleId(), new String[] {ActionKeys.VIEW}
			).build());

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				_group.getCompanyId(), name, ResourceConstants.SCOPE_INDIVIDUAL,
				primKey, _role.getRoleId(), ActionKeys.VIEW));
	}

	private void _assertSetIndividualResourcePermissionsFails(
			long groupId, String name, String primKey)
		throws Exception {

		try {
			_resourcePermissionService.setIndividualResourcePermissions(
				groupId, _group.getCompanyId(), name, primKey,
				_role.getRoleId(), new String[] {ActionKeys.VIEW});

			Assert.fail();
		}
		catch (Exception exception) {
		}

		try {
			_resourcePermissionService.setIndividualResourcePermissions(
				groupId, _group.getCompanyId(), name, primKey,
				HashMapBuilder.put(
					_role.getRoleId(), new String[] {ActionKeys.VIEW}
				).build());

			Assert.fail();
		}
		catch (Exception exception) {
		}

		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				_group.getCompanyId(), name, ResourceConstants.SCOPE_INDIVIDUAL,
				primKey, _role.getRoleId(), ActionKeys.VIEW));
	}

	@DeleteAfterTestRun
	private Group _group;

	private PermissionChecker _originalPermissionChecker;

	@DeleteAfterTestRun
	private Group _otherGroup;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private ResourcePermissionService _resourcePermissionService;

	@DeleteAfterTestRun
	private Role _role;

	@DeleteAfterTestRun
	private User _user;

}
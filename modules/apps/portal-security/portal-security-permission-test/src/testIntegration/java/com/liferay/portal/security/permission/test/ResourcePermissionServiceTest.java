/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.constants.JournalConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionService;
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

		try {
			_resourcePermissionService.setIndividualResourcePermissions(
				_group.getGroupId(), _group.getCompanyId(),
				PortletKeys.SERVER_ADMIN, PortletKeys.SERVER_ADMIN,
				_role.getRoleId(), new String[] {ActionKeys.VIEW});

			Assert.fail();
		}
		catch (Exception exception) {
		}

		try {
			_resourcePermissionService.setIndividualResourcePermissions(
				_group.getGroupId(), _group.getCompanyId(),
				PortletKeys.SERVER_ADMIN, PortletKeys.SERVER_ADMIN,
				HashMapBuilder.put(
					_role.getRoleId(), new String[] {ActionKeys.VIEW}
				).build());

			Assert.fail();
		}
		catch (Exception exception) {
		}

		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				_group.getCompanyId(), PortletKeys.SERVER_ADMIN,
				ResourceConstants.SCOPE_INDIVIDUAL, PortletKeys.SERVER_ADMIN,
				_role.getRoleId(), ActionKeys.VIEW));
	}

	@Test
	public void testSetIndividualResourcePermissionsOnGroupResource()
		throws Exception {

		String primKey = String.valueOf(_group.getGroupId());

		_resourcePermissionService.setIndividualResourcePermissions(
			_group.getGroupId(), _group.getCompanyId(),
			JournalConstants.RESOURCE_NAME, primKey,
			HashMapBuilder.put(
				_role.getRoleId(), new String[] {ActionKeys.VIEW}
			).build());

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				_group.getCompanyId(), JournalConstants.RESOURCE_NAME,
				ResourceConstants.SCOPE_INDIVIDUAL, primKey, _role.getRoleId(),
				ActionKeys.VIEW));
	}

	@DeleteAfterTestRun
	private Group _group;

	private PermissionChecker _originalPermissionChecker;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private ResourcePermissionService _resourcePermissionService;

	@DeleteAfterTestRun
	private Role _role;

	@DeleteAfterTestRun
	private User _user;

}
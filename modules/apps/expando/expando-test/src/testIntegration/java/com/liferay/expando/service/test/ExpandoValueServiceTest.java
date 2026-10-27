/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.expando.kernel.service.ExpandoValueService;
import com.liferay.expando.test.util.ExpandoTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Debora Buriti
 */
@RunWith(Arquillian.class)
public class ExpandoValueServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_expandoTable = ExpandoTestUtil.addTable(
			PortalUtil.getClassNameId(User.class),
			RandomTestUtil.randomString());

		_expandoColumn = ExpandoTestUtil.addColumn(
			_expandoTable, RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING);

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.USER);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), ExpandoColumn.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_expandoColumn.getColumnId()), role.getRoleId(),
			new String[] {ActionKeys.UPDATE, ActionKeys.VIEW});

		_user1 = UserTestUtil.addUser();
		_user2 = UserTestUtil.addUser();
	}

	@Test
	public void testAddValue() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user1));

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			_getMessage(ActionKeys.UPDATE),
			() -> _expandoValueService.addValue(
				TestPropsValues.getCompanyId(), User.class.getName(),
				_expandoTable.getName(), _expandoColumn.getName(),
				_user2.getUserId(), (Object)RandomTestUtil.randomString()));

		String data = RandomTestUtil.randomString();

		_expandoValueService.addValue(
			TestPropsValues.getCompanyId(), User.class.getName(),
			_expandoTable.getName(), _expandoColumn.getName(),
			_user1.getUserId(), (Object)data);

		Assert.assertEquals(
			data,
			_expandoValueLocalService.getData(
				TestPropsValues.getCompanyId(), User.class.getName(),
				_expandoTable.getName(), _expandoColumn.getName(),
				_user1.getUserId()));
	}

	@Test
	public void testGetData() throws Exception {
		String data = RandomTestUtil.randomString();

		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn, _user1.getUserId(), data);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user1));

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			_getMessage(ActionKeys.VIEW),
			() -> _expandoValueService.getData(
				TestPropsValues.getCompanyId(), User.class.getName(),
				_expandoTable.getName(),
				Collections.singletonList(_expandoColumn.getName()),
				_user2.getUserId()));
		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			_getMessage(ActionKeys.VIEW),
			() -> _expandoValueService.getData(
				TestPropsValues.getCompanyId(), User.class.getName(),
				_expandoTable.getName(), _expandoColumn.getName(),
				_user2.getUserId()));

		Assert.assertEquals(
			Collections.singletonMap(_expandoColumn.getName(), data),
			_expandoValueService.getData(
				TestPropsValues.getCompanyId(), User.class.getName(),
				_expandoTable.getName(),
				Collections.singletonList(_expandoColumn.getName()),
				_user1.getUserId()));
		Assert.assertEquals(
			data,
			_expandoValueService.getData(
				TestPropsValues.getCompanyId(), User.class.getName(),
				_expandoTable.getName(), _expandoColumn.getName(),
				_user1.getUserId()));
	}

	@Test
	public void testGetJSONData() throws Exception {
		String data = RandomTestUtil.randomString();

		ExpandoTestUtil.addValue(
			_expandoTable, _expandoColumn, _user1.getUserId(), data);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user1));

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			_getMessage(ActionKeys.VIEW),
			() -> _expandoValueService.getJSONData(
				TestPropsValues.getCompanyId(), User.class.getName(),
				_expandoTable.getName(), _expandoColumn.getName(),
				_user2.getUserId()));

		JSONObject jsonObject = _expandoValueService.getJSONData(
			TestPropsValues.getCompanyId(), User.class.getName(),
			_expandoTable.getName(), _expandoColumn.getName(),
			_user1.getUserId());

		Assert.assertEquals(data, jsonObject.getString("data"));
	}

	private String _getMessage(String actionId) {
		return StringBundler.concat(
			"User ", _user1.getUserId(), " must have ", actionId,
			" permission for ", User.class.getName(), StringPool.SPACE,
			_user2.getUserId());
	}

	private ExpandoColumn _expandoColumn;

	@DeleteAfterTestRun
	private ExpandoTable _expandoTable;

	@Inject
	private ExpandoValueLocalService _expandoValueLocalService;

	@Inject
	private ExpandoValueService _expandoValueService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private User _user1;

	@DeleteAfterTestRun
	private User _user2;

}
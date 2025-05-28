/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.configuration.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.test.performance.PerformanceTimer;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.configuration.manager.MenuAccessConfigurationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Evan Thibodeau
 */
@RunWith(Arquillian.class)
public class MenuAccessConfigurationManagerPerformanceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Class<?> clazz = MenuAccessConfigurationManagerPerformanceTest.class;

		_properties = PropertiesUtil.load(
			clazz.getResourceAsStream(
				"dependencies/menu-access-configuration-manager-perfor" +
					"mance.properties"),
			"UTF-8");

		_groupsCount = GetterUtil.getInteger(
			_properties.getProperty("groups.count"));

		if (_log.isInfoEnabled()) {
			_log.info(StringBundler.concat("Setup ", _groupsCount, " groups"));
		}

		for (int i = 0; i < _groupsCount; i++) {
			_groups.add(GroupTestUtil.addGroup());
		}
	}

	@Test
	public void testDeleteRoleAccessToControlMenu() throws Exception {
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		try (PerformanceTimer performanceTimer = new PerformanceTimer(
				GetterUtil.getInteger(
					_properties.getProperty("roles.delete.max.time")))) {

			_menuAccessConfigurationManager.deleteRoleAccessToControlMenu(role);
		}
	}

	@Test
	public void testDeleteRoleAccessToControlMenuWithMenuAccessEnabled()
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		for (int i = 0; i < (_groupsCount / 5); i++) {
			Group group = _groups.get(i);

			_menuAccessConfigurationManager.updateMenuAccessConfiguration(
				group.getGroupId(),
				new String[] {String.valueOf(role.getRoleId())}, true);
		}

		try (PerformanceTimer performanceTimer = new PerformanceTimer(
				GetterUtil.getInteger(
					_properties.getProperty("roles.delete.max.time")))) {

			_menuAccessConfigurationManager.deleteRoleAccessToControlMenu(role);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MenuAccessConfigurationManagerPerformanceTest.class);

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

	private int _groupsCount;

	@Inject
	private MenuAccessConfigurationManager _menuAccessConfigurationManager;

	private Properties _properties;

}
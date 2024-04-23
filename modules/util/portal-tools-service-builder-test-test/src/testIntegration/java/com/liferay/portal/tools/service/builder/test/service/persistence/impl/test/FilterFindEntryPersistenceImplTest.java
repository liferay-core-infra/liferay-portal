/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.model.FilterFindEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.FilterFindEntryPersistence;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kevin Lee
 */
@RunWith(Arquillian.class)
public class FilterFindEntryPersistenceImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@BeforeClass
	public static void setUpClass() throws Exception {
		_user = UserTestUtil.addUser();

		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);

		UserLocalServiceUtil.deleteUser(_user);
	}

	@Before
	public void setUp() throws Exception {
		_filterFindEntry = _filterFindEntryPersistence.create(
			RandomTestUtil.randomLong());

		_filterFindEntry.setInteger(RandomTestUtil.randomInt());

		_filterFindEntry = _filterFindEntryPersistence.update(_filterFindEntry);
	}

	@Test
	public void testFilterCountByAsDifferentUser() {
		Assert.assertEquals(
			0,
			_filterFindEntryPersistence.filterCountByI_T(
				_filterFindEntry.getInteger(), _filterFindEntry.getType()));
	}

	@Test
	public void testFilterFindByAsDifferentUser() {
		List<FilterFindEntry> badColumnNameEntryList =
			_filterFindEntryPersistence.filterFindByI_T(
				_filterFindEntry.getInteger(), _filterFindEntry.getType());

		Assert.assertTrue(badColumnNameEntryList.isEmpty());
	}

	private static PermissionChecker _originalPermissionChecker;
	private static User _user;

	@DeleteAfterTestRun
	private FilterFindEntry _filterFindEntry;

	@Inject
	private FilterFindEntryPersistence _filterFindEntryPersistence;

}
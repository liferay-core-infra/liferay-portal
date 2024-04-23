/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
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
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser(_group.getGroupId());

		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);

		UserLocalServiceUtil.deleteUser(_user);

		GroupLocalServiceUtil.deleteGroup(_group);
	}

	@Before
	public void setUp() throws Exception {
		_filterFindEntry = _filterFindEntryPersistence.create(
			RandomTestUtil.randomLong());

		_filterFindEntry.setGroupId(_group.getGroupId());
		_filterFindEntry.setInteger(RandomTestUtil.randomInt());
		_filterFindEntry.setType(RandomTestUtil.randomString());

		_filterFindEntry = _filterFindEntryPersistence.update(_filterFindEntry);
	}

	@Test
	public void testFilterCountByAsDifferentUser() {
		Assert.assertEquals(
			0,
			_filterFindEntryPersistence.filterCountByG_I_T(
				_filterFindEntry.getGroupId(), _filterFindEntry.getInteger(),
				_filterFindEntry.getType()));

		Assert.assertEquals(
			0,
			_filterFindEntryPersistence.filterCountByG_I_T(
				_filterFindEntry.getGroupId(), _filterFindEntry.getInteger(),
				new String[] {_filterFindEntry.getType()}));
	}

	@Test
	public void testFilterFindByAsDifferentUser() {
		List<FilterFindEntry> filterFindEntriesList =
			_filterFindEntryPersistence.filterFindByG_I_T(
				_filterFindEntry.getGroupId(), _filterFindEntry.getInteger(),
				_filterFindEntry.getType());

		Assert.assertTrue(filterFindEntriesList.isEmpty());

		filterFindEntriesList = _filterFindEntryPersistence.filterFindByG_I_T(
			_filterFindEntry.getGroupId(), _filterFindEntry.getInteger(),
			new String[] {_filterFindEntry.getType()});

		Assert.assertTrue(filterFindEntriesList.isEmpty());
	}

	@Test
	public void testFilterFindByPrevAndNextAsDifferentUser() throws Exception {
		FilterFindEntry prevFilterFindEntry =
			_filterFindEntryPersistence.create(
				_filterFindEntry.getFilterFindEntryId() - 1);

		prevFilterFindEntry.setGroupId(_filterFindEntry.getGroupId());
		prevFilterFindEntry.setInteger(_filterFindEntry.getInteger());
		prevFilterFindEntry.setType(_filterFindEntry.getType());

		prevFilterFindEntry = _filterFindEntryPersistence.update(
			prevFilterFindEntry);

		FilterFindEntry nextFilterFindEntry =
			_filterFindEntryPersistence.create(
				_filterFindEntry.getFilterFindEntryId() + 1);

		nextFilterFindEntry.setGroupId(_filterFindEntry.getGroupId());
		nextFilterFindEntry.setInteger(_filterFindEntry.getInteger());
		nextFilterFindEntry.setType(_filterFindEntry.getType());

		nextFilterFindEntry = _filterFindEntryPersistence.update(
			nextFilterFindEntry);

		try {
			FilterFindEntry[] filterFindEntries =
				_filterFindEntryPersistence.filterFindByG_I_T_PrevAndNext(
					_filterFindEntry.getFilterFindEntryId(),
					_filterFindEntry.getGroupId(),
					_filterFindEntry.getInteger(), _filterFindEntry.getType(),
					null);

			Assert.assertEquals(_filterFindEntry, filterFindEntries[1]);

			Assert.assertNull(filterFindEntries[0]);
			Assert.assertNull(filterFindEntries[2]);
		}
		finally {
			_filterFindEntryPersistence.remove(
				prevFilterFindEntry.getFilterFindEntryId());
			_filterFindEntryPersistence.remove(
				nextFilterFindEntry.getFilterFindEntryId());
		}
	}

	private static Group _group;
	private static PermissionChecker _originalPermissionChecker;
	private static User _user;

	@DeleteAfterTestRun
	private FilterFindEntry _filterFindEntry;

	@Inject
	private FilterFindEntryPersistence _filterFindEntryPersistence;

}
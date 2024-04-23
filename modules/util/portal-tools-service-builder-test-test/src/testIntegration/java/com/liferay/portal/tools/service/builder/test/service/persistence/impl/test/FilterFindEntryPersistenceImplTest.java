/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.model.FilterFindEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.FilterFindEntryPersistence;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
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
			PermissionCheckerMethodTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@BeforeClass
	public static void setUpClass() throws Exception {
		_group = GroupTestUtil.addGroup();

		_userWithPermission = UserTestUtil.addUser(_group.getGroupId());
		_userWithoutPermission = UserTestUtil.addUser(_group.getGroupId());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		UserLocalServiceUtil.deleteUser(_userWithPermission);
		UserLocalServiceUtil.deleteUser(_userWithoutPermission);

		GroupLocalServiceUtil.deleteGroup(_group);
	}

	@Test
	public void testFilterCountByAsDifferentUsers() throws Exception {
		FilterFindEntry filterFindEntry = _addFilterFindEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomInt());

		try {
			UserTestUtil.setUser(_userWithPermission);

			Assert.assertEquals(
				1,
				_filterFindEntryPersistence.filterCountByG_I_T(
					filterFindEntry.getGroupId(), filterFindEntry.getInteger(),
					filterFindEntry.getType()));

			Assert.assertEquals(
				1,
				_filterFindEntryPersistence.filterCountByG_I_T(
					filterFindEntry.getGroupId(), filterFindEntry.getInteger(),
					new String[] {filterFindEntry.getType()}));

			UserTestUtil.setUser(_userWithoutPermission);

			Assert.assertEquals(
				0,
				_filterFindEntryPersistence.filterCountByG_I_T(
					filterFindEntry.getGroupId(), filterFindEntry.getInteger(),
					filterFindEntry.getType()));

			Assert.assertEquals(
				0,
				_filterFindEntryPersistence.filterCountByG_I_T(
					filterFindEntry.getGroupId(), filterFindEntry.getInteger(),
					new String[] {filterFindEntry.getType()}));
		}
		finally {
			_removeFilterFindEntry(filterFindEntry);
		}
	}

	@Test
	public void testFilterFindByAsDifferentUsers() throws Exception {
		FilterFindEntry filterFindEntry = _addFilterFindEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomInt());

		try {
			UserTestUtil.setUser(_userWithPermission);

			List<FilterFindEntry> filterFindEntryList =
				_filterFindEntryPersistence.filterFindByG_I_T(
					filterFindEntry.getGroupId(), filterFindEntry.getInteger(),
					filterFindEntry.getType());

			Assert.assertEquals(
				filterFindEntryList.toString(), 1, filterFindEntryList.size());

			filterFindEntryList = _filterFindEntryPersistence.filterFindByG_I_T(
				filterFindEntry.getGroupId(), filterFindEntry.getInteger(),
				new String[] {filterFindEntry.getType()});

			Assert.assertEquals(
				filterFindEntryList.toString(), 1, filterFindEntryList.size());

			UserTestUtil.setUser(_userWithoutPermission);

			filterFindEntryList = _filterFindEntryPersistence.filterFindByG_I_T(
				filterFindEntry.getGroupId(), filterFindEntry.getInteger(),
				filterFindEntry.getType());

			Assert.assertEquals(
				filterFindEntryList.toString(), 0, filterFindEntryList.size());

			filterFindEntryList = _filterFindEntryPersistence.filterFindByG_I_T(
				filterFindEntry.getGroupId(), filterFindEntry.getInteger(),
				new String[] {filterFindEntry.getType()});

			Assert.assertEquals(
				filterFindEntryList.toString(), 0, filterFindEntryList.size());
		}
		finally {
			_removeFilterFindEntry(filterFindEntry);
		}
	}

	@Test
	public void testFilterFindByPrevAndNextAsDifferentUsers() throws Exception {
		int integer = 1;

		FilterFindEntry filterFindEntry1 = _addFilterFindEntry(
			RandomTestUtil.randomString(), integer);

		FilterFindEntry filterFindEntry2 = _addFilterFindEntry(
			filterFindEntry1.getType(), integer);
		FilterFindEntry filterFindEntry3 = _addFilterFindEntry(
			filterFindEntry1.getType(), integer);

		try {
			UserTestUtil.setUser(_userWithPermission);

			FilterFindEntry[] filterFindEntries =
				_filterFindEntryPersistence.filterFindByG_I_T_PrevAndNext(
					filterFindEntry1.getFilterFindEntryId(),
					filterFindEntry1.getGroupId(), integer,
					filterFindEntry1.getType(), null);

			Assert.assertNotNull(filterFindEntries[0]);
			Assert.assertNotNull(filterFindEntries[2]);

			UserTestUtil.setUser(_userWithoutPermission);

			filterFindEntries =
				_filterFindEntryPersistence.filterFindByG_I_T_PrevAndNext(
					filterFindEntry1.getFilterFindEntryId(),
					filterFindEntry1.getGroupId(), integer,
					filterFindEntry1.getType(), null);

			Assert.assertNull(filterFindEntries[0]);
			Assert.assertNull(filterFindEntries[2]);
		}
		finally {
			_removeFilterFindEntry(filterFindEntry1);
			_removeFilterFindEntry(filterFindEntry2);
			_removeFilterFindEntry(filterFindEntry3);
		}
	}

	private FilterFindEntry _addFilterFindEntry(String type, int integer)
		throws Exception {

		FilterFindEntry filterFindEntry = _filterFindEntryPersistence.create(
			RandomTestUtil.randomLong());

		filterFindEntry.setGroupId(_group.getGroupId());
		filterFindEntry.setType(type);
		filterFindEntry.setInteger(integer);

		filterFindEntry = _filterFindEntryPersistence.update(filterFindEntry);

		ResourcePermissionLocalServiceUtil.addModelResourcePermissions(
			_group.getCompanyId(), _group.getGroupId(),
			_userWithPermission.getUserId(), FilterFindEntry.class.getName(),
			String.valueOf(filterFindEntry.getFilterFindEntryId()),
			ModelPermissionsFactory.create(new String[0], new String[0]));

		return filterFindEntry;
	}

	private void _removeFilterFindEntry(FilterFindEntry filterFindEntry)
		throws Exception {

		_filterFindEntryPersistence.remove(filterFindEntry);

		ResourcePermissionLocalServiceUtil.deleteResourcePermissions(
			_group.getCompanyId(), FilterFindEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(filterFindEntry.getFilterFindEntryId()));
	}

	private static Group _group;
	private static User _userWithoutPermission;
	private static User _userWithPermission;

	@Inject
	private FilterFindEntryPersistence _filterFindEntryPersistence;

}
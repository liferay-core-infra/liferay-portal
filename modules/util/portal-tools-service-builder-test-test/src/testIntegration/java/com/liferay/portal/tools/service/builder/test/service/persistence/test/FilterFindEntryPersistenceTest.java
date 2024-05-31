/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.security.permission.SimplePermissionChecker;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchFilterFindEntryException;
import com.liferay.portal.tools.service.builder.test.model.FilterFindEntry;
import com.liferay.portal.tools.service.builder.test.service.FilterFindEntryLocalServiceUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.FilterFindEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.FilterFindEntryUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @generated
 */
@RunWith(Arquillian.class)
public class FilterFindEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Before
	public void setUp() {
		_persistence = FilterFindEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<FilterFindEntry> iterator = _filterFindEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FilterFindEntry filterFindEntry = _persistence.create(pk);

		Assert.assertNotNull(filterFindEntry);

		Assert.assertEquals(filterFindEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		FilterFindEntry newFilterFindEntry = addFilterFindEntry();

		_persistence.remove(newFilterFindEntry);

		FilterFindEntry existingFilterFindEntry =
			_persistence.fetchByPrimaryKey(newFilterFindEntry.getPrimaryKey());

		Assert.assertNull(existingFilterFindEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addFilterFindEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FilterFindEntry newFilterFindEntry = _persistence.create(pk);

		newFilterFindEntry.setGroupId(RandomTestUtil.nextLong());

		newFilterFindEntry.setType(RandomTestUtil.randomString());

		newFilterFindEntry.setInteger(RandomTestUtil.nextInt());

		_filterFindEntries.add(_persistence.update(newFilterFindEntry));

		FilterFindEntry existingFilterFindEntry = _persistence.findByPrimaryKey(
			newFilterFindEntry.getPrimaryKey());

		Assert.assertEquals(
			existingFilterFindEntry.getFilterFindEntryId(),
			newFilterFindEntry.getFilterFindEntryId());
		Assert.assertEquals(
			existingFilterFindEntry.getGroupId(),
			newFilterFindEntry.getGroupId());
		Assert.assertEquals(
			existingFilterFindEntry.getType(), newFilterFindEntry.getType());
		Assert.assertEquals(
			existingFilterFindEntry.getInteger(),
			newFilterFindEntry.getInteger());
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		FilterFindEntry newFilterFindEntry = addFilterFindEntry();

		FilterFindEntry existingFilterFindEntry = _persistence.findByPrimaryKey(
			newFilterFindEntry.getPrimaryKey());

		Assert.assertEquals(existingFilterFindEntry, newFilterFindEntry);
	}

	@Test(expected = NoSuchFilterFindEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	@Test
	public void testFilterFindByGroupId() throws Exception {
		try (AutoCloseable autoCloseable = _useNonAdminPermissionChecker()) {
			Assert.assertTrue(InlineSQLHelperUtil.isEnabled(0));

			_persistence.filterFindByGroupId(
				0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			_persistence.filterFindByGroupId(
				0, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				getOrderByComparator());
		}
	}

	@Test
	public void testFilterFindByGroupId_PrevAndNext() throws Exception {
		FilterFindEntry newFilterFindEntry = addFilterFindEntry();

		try (AutoCloseable autoCloseable = _useNonAdminPermissionChecker()) {
			Assert.assertTrue(
				InlineSQLHelperUtil.isEnabled(newFilterFindEntry.getGroupId()));

			_persistence.filterFindByGroupId_PrevAndNext(
				newFilterFindEntry.getFilterFindEntryId(),
				newFilterFindEntry.getGroupId(), null);

			_persistence.filterFindByGroupId_PrevAndNext(
				newFilterFindEntry.getFilterFindEntryId(),
				newFilterFindEntry.getGroupId(), getOrderByComparator());
		}
	}

	private AutoCloseable _useNonAdminPermissionChecker() throws Exception {
		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			new SimplePermissionChecker() {
				{
					init(TestPropsValues.getUser());
				}

				@Override
				public boolean isCompanyAdmin(long companyId) {
					return false;
				}

				@Override
				public boolean isGroupAdmin(long groupId) {
					return false;
				}

				@Override
				public boolean isGroupOwner(long groupId) {
					return false;
				}

				@Override
				public boolean isOmniadmin() {
					return false;
				}

			});

		return () -> {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		};
	}

	protected OrderByComparator<FilterFindEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"FilterFindEntry", "filterFindEntryId", true, "groupId", true,
			"type", true, "integer", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		FilterFindEntry newFilterFindEntry = addFilterFindEntry();

		FilterFindEntry existingFilterFindEntry =
			_persistence.fetchByPrimaryKey(newFilterFindEntry.getPrimaryKey());

		Assert.assertEquals(existingFilterFindEntry, newFilterFindEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FilterFindEntry missingFilterFindEntry = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingFilterFindEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		FilterFindEntry newFilterFindEntry1 = addFilterFindEntry();
		FilterFindEntry newFilterFindEntry2 = addFilterFindEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFilterFindEntry1.getPrimaryKey());
		primaryKeys.add(newFilterFindEntry2.getPrimaryKey());

		Map<Serializable, FilterFindEntry> filterFindEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, filterFindEntries.size());
		Assert.assertEquals(
			newFilterFindEntry1,
			filterFindEntries.get(newFilterFindEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newFilterFindEntry2,
			filterFindEntries.get(newFilterFindEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, FilterFindEntry> filterFindEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(filterFindEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		FilterFindEntry newFilterFindEntry = addFilterFindEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFilterFindEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, FilterFindEntry> filterFindEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, filterFindEntries.size());
		Assert.assertEquals(
			newFilterFindEntry,
			filterFindEntries.get(newFilterFindEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, FilterFindEntry> filterFindEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(filterFindEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		FilterFindEntry newFilterFindEntry = addFilterFindEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFilterFindEntry.getPrimaryKey());

		Map<Serializable, FilterFindEntry> filterFindEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, filterFindEntries.size());
		Assert.assertEquals(
			newFilterFindEntry,
			filterFindEntries.get(newFilterFindEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			FilterFindEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<FilterFindEntry>() {

				@Override
				public void performAction(FilterFindEntry filterFindEntry) {
					Assert.assertNotNull(filterFindEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		FilterFindEntry newFilterFindEntry = addFilterFindEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			FilterFindEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"filterFindEntryId",
				newFilterFindEntry.getFilterFindEntryId()));

		List<FilterFindEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		FilterFindEntry existingFilterFindEntry = result.get(0);

		Assert.assertEquals(existingFilterFindEntry, newFilterFindEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			FilterFindEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"filterFindEntryId", RandomTestUtil.nextLong()));

		List<FilterFindEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		FilterFindEntry newFilterFindEntry = addFilterFindEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			FilterFindEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("filterFindEntryId"));

		Object newFilterFindEntryId = newFilterFindEntry.getFilterFindEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"filterFindEntryId", new Object[] {newFilterFindEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingFilterFindEntryId = result.get(0);

		Assert.assertEquals(existingFilterFindEntryId, newFilterFindEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			FilterFindEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("filterFindEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"filterFindEntryId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected FilterFindEntry addFilterFindEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FilterFindEntry filterFindEntry = _persistence.create(pk);

		filterFindEntry.setGroupId(RandomTestUtil.nextLong());

		filterFindEntry.setType(RandomTestUtil.randomString());

		filterFindEntry.setInteger(RandomTestUtil.nextInt());

		_filterFindEntries.add(_persistence.update(filterFindEntry));

		return filterFindEntry;
	}

	private List<FilterFindEntry> _filterFindEntries =
		new ArrayList<FilterFindEntry>();
	private FilterFindEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
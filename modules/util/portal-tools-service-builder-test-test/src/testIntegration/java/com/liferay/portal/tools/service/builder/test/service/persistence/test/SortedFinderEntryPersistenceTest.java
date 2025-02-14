/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
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
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchSortedFinderEntryException;
import com.liferay.portal.tools.service.builder.test.model.SortedFinderEntry;
import com.liferay.portal.tools.service.builder.test.service.SortedFinderEntryLocalServiceUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.SortedFinderEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.SortedFinderEntryUtil;

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
public class SortedFinderEntryPersistenceTest {

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
		_persistence = SortedFinderEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SortedFinderEntry> iterator = _sortedFinderEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SortedFinderEntry sortedFinderEntry = _persistence.create(pk);

		Assert.assertNotNull(sortedFinderEntry);

		Assert.assertEquals(sortedFinderEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SortedFinderEntry newSortedFinderEntry = addSortedFinderEntry();

		_persistence.remove(newSortedFinderEntry);

		SortedFinderEntry existingSortedFinderEntry =
			_persistence.fetchByPrimaryKey(
				newSortedFinderEntry.getPrimaryKey());

		Assert.assertNull(existingSortedFinderEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSortedFinderEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SortedFinderEntry newSortedFinderEntry = _persistence.create(pk);

		newSortedFinderEntry.setName(RandomTestUtil.randomString());

		newSortedFinderEntry.setGroupId(RandomTestUtil.nextLong());

		_sortedFinderEntries.add(_persistence.update(newSortedFinderEntry));

		SortedFinderEntry existingSortedFinderEntry =
			_persistence.findByPrimaryKey(newSortedFinderEntry.getPrimaryKey());

		Assert.assertEquals(
			existingSortedFinderEntry.getSortedFinderEntryId(),
			newSortedFinderEntry.getSortedFinderEntryId());
		Assert.assertEquals(
			existingSortedFinderEntry.getName(),
			newSortedFinderEntry.getName());
		Assert.assertEquals(
			existingSortedFinderEntry.getGroupId(),
			newSortedFinderEntry.getGroupId());
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SortedFinderEntry newSortedFinderEntry = addSortedFinderEntry();

		SortedFinderEntry existingSortedFinderEntry =
			_persistence.findByPrimaryKey(newSortedFinderEntry.getPrimaryKey());

		Assert.assertEquals(existingSortedFinderEntry, newSortedFinderEntry);
	}

	@Test(expected = NoSuchSortedFinderEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SortedFinderEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"SortedFinderEntry", "sortedFinderEntryId", true, "name", true,
			"groupId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SortedFinderEntry newSortedFinderEntry = addSortedFinderEntry();

		SortedFinderEntry existingSortedFinderEntry =
			_persistence.fetchByPrimaryKey(
				newSortedFinderEntry.getPrimaryKey());

		Assert.assertEquals(existingSortedFinderEntry, newSortedFinderEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SortedFinderEntry missingSortedFinderEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSortedFinderEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SortedFinderEntry newSortedFinderEntry1 = addSortedFinderEntry();
		SortedFinderEntry newSortedFinderEntry2 = addSortedFinderEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSortedFinderEntry1.getPrimaryKey());
		primaryKeys.add(newSortedFinderEntry2.getPrimaryKey());

		Map<Serializable, SortedFinderEntry> sortedFinderEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, sortedFinderEntries.size());
		Assert.assertEquals(
			newSortedFinderEntry1,
			sortedFinderEntries.get(newSortedFinderEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newSortedFinderEntry2,
			sortedFinderEntries.get(newSortedFinderEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SortedFinderEntry> sortedFinderEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(sortedFinderEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SortedFinderEntry newSortedFinderEntry = addSortedFinderEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSortedFinderEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SortedFinderEntry> sortedFinderEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, sortedFinderEntries.size());
		Assert.assertEquals(
			newSortedFinderEntry,
			sortedFinderEntries.get(newSortedFinderEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SortedFinderEntry> sortedFinderEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(sortedFinderEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SortedFinderEntry newSortedFinderEntry = addSortedFinderEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSortedFinderEntry.getPrimaryKey());

		Map<Serializable, SortedFinderEntry> sortedFinderEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, sortedFinderEntries.size());
		Assert.assertEquals(
			newSortedFinderEntry,
			sortedFinderEntries.get(newSortedFinderEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			SortedFinderEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<SortedFinderEntry>() {

				@Override
				public void performAction(SortedFinderEntry sortedFinderEntry) {
					Assert.assertNotNull(sortedFinderEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		SortedFinderEntry newSortedFinderEntry = addSortedFinderEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SortedFinderEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"sortedFinderEntryId",
				newSortedFinderEntry.getSortedFinderEntryId()));

		List<SortedFinderEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		SortedFinderEntry existingSortedFinderEntry = result.get(0);

		Assert.assertEquals(existingSortedFinderEntry, newSortedFinderEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SortedFinderEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"sortedFinderEntryId", RandomTestUtil.nextLong()));

		List<SortedFinderEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		SortedFinderEntry newSortedFinderEntry = addSortedFinderEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SortedFinderEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("sortedFinderEntryId"));

		Object newSortedFinderEntryId =
			newSortedFinderEntry.getSortedFinderEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"sortedFinderEntryId", new Object[] {newSortedFinderEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingSortedFinderEntryId = result.get(0);

		Assert.assertEquals(
			existingSortedFinderEntryId, newSortedFinderEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SortedFinderEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("sortedFinderEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"sortedFinderEntryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected SortedFinderEntry addSortedFinderEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SortedFinderEntry sortedFinderEntry = _persistence.create(pk);

		sortedFinderEntry.setName(RandomTestUtil.randomString());

		sortedFinderEntry.setGroupId(RandomTestUtil.nextLong());

		_sortedFinderEntries.add(_persistence.update(sortedFinderEntry));

		return sortedFinderEntry;
	}

	private List<SortedFinderEntry> _sortedFinderEntries =
		new ArrayList<SortedFinderEntry>();
	private SortedFinderEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
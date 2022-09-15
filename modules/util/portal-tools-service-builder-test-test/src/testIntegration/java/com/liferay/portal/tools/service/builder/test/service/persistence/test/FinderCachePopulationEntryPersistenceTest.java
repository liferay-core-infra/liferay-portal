/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchFinderCachePopulationEntryException;
import com.liferay.portal.tools.service.builder.test.model.FinderCachePopulationEntry;
import com.liferay.portal.tools.service.builder.test.service.FinderCachePopulationEntryLocalServiceUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.FinderCachePopulationEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.FinderCachePopulationEntryUtil;

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
public class FinderCachePopulationEntryPersistenceTest {

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
		_persistence = FinderCachePopulationEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<FinderCachePopulationEntry> iterator =
			_finderCachePopulationEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FinderCachePopulationEntry finderCachePopulationEntry =
			_persistence.create(pk);

		Assert.assertNotNull(finderCachePopulationEntry);

		Assert.assertEquals(finderCachePopulationEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		FinderCachePopulationEntry newFinderCachePopulationEntry =
			addFinderCachePopulationEntry();

		_persistence.remove(newFinderCachePopulationEntry);

		FinderCachePopulationEntry existingFinderCachePopulationEntry =
			_persistence.fetchByPrimaryKey(
				newFinderCachePopulationEntry.getPrimaryKey());

		Assert.assertNull(existingFinderCachePopulationEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addFinderCachePopulationEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FinderCachePopulationEntry newFinderCachePopulationEntry =
			_persistence.create(pk);

		newFinderCachePopulationEntry.setGroupId(RandomTestUtil.nextLong());

		newFinderCachePopulationEntry.setCompanyId(RandomTestUtil.nextLong());

		newFinderCachePopulationEntry.setUniqueName(
			RandomTestUtil.randomString());

		_finderCachePopulationEntries.add(
			_persistence.update(newFinderCachePopulationEntry));

		FinderCachePopulationEntry existingFinderCachePopulationEntry =
			_persistence.findByPrimaryKey(
				newFinderCachePopulationEntry.getPrimaryKey());

		Assert.assertEquals(
			existingFinderCachePopulationEntry.
				getPinderCachePopulationEntryId(),
			newFinderCachePopulationEntry.getPinderCachePopulationEntryId());
		Assert.assertEquals(
			existingFinderCachePopulationEntry.getGroupId(),
			newFinderCachePopulationEntry.getGroupId());
		Assert.assertEquals(
			existingFinderCachePopulationEntry.getCompanyId(),
			newFinderCachePopulationEntry.getCompanyId());
		Assert.assertEquals(
			existingFinderCachePopulationEntry.getUniqueName(),
			newFinderCachePopulationEntry.getUniqueName());
	}

	@Test
	public void testCountByUniqueName() throws Exception {
		_persistence.countByUniqueName("");

		_persistence.countByUniqueName("null");

		_persistence.countByUniqueName((String)null);
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByC_G() throws Exception {
		_persistence.countByC_G(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_G(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		FinderCachePopulationEntry newFinderCachePopulationEntry =
			addFinderCachePopulationEntry();

		FinderCachePopulationEntry existingFinderCachePopulationEntry =
			_persistence.findByPrimaryKey(
				newFinderCachePopulationEntry.getPrimaryKey());

		Assert.assertEquals(
			existingFinderCachePopulationEntry, newFinderCachePopulationEntry);
	}

	@Test(expected = NoSuchFinderCachePopulationEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<FinderCachePopulationEntry>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"FinderCachePopulationEntry", "pinderCachePopulationEntryId", true,
			"groupId", true, "companyId", true, "uniqueName", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		FinderCachePopulationEntry newFinderCachePopulationEntry =
			addFinderCachePopulationEntry();

		FinderCachePopulationEntry existingFinderCachePopulationEntry =
			_persistence.fetchByPrimaryKey(
				newFinderCachePopulationEntry.getPrimaryKey());

		Assert.assertEquals(
			existingFinderCachePopulationEntry, newFinderCachePopulationEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FinderCachePopulationEntry missingFinderCachePopulationEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingFinderCachePopulationEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		FinderCachePopulationEntry newFinderCachePopulationEntry1 =
			addFinderCachePopulationEntry();
		FinderCachePopulationEntry newFinderCachePopulationEntry2 =
			addFinderCachePopulationEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFinderCachePopulationEntry1.getPrimaryKey());
		primaryKeys.add(newFinderCachePopulationEntry2.getPrimaryKey());

		Map<Serializable, FinderCachePopulationEntry>
			finderCachePopulationEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, finderCachePopulationEntries.size());
		Assert.assertEquals(
			newFinderCachePopulationEntry1,
			finderCachePopulationEntries.get(
				newFinderCachePopulationEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newFinderCachePopulationEntry2,
			finderCachePopulationEntries.get(
				newFinderCachePopulationEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, FinderCachePopulationEntry>
			finderCachePopulationEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(finderCachePopulationEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		FinderCachePopulationEntry newFinderCachePopulationEntry =
			addFinderCachePopulationEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFinderCachePopulationEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, FinderCachePopulationEntry>
			finderCachePopulationEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, finderCachePopulationEntries.size());
		Assert.assertEquals(
			newFinderCachePopulationEntry,
			finderCachePopulationEntries.get(
				newFinderCachePopulationEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, FinderCachePopulationEntry>
			finderCachePopulationEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(finderCachePopulationEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		FinderCachePopulationEntry newFinderCachePopulationEntry =
			addFinderCachePopulationEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFinderCachePopulationEntry.getPrimaryKey());

		Map<Serializable, FinderCachePopulationEntry>
			finderCachePopulationEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, finderCachePopulationEntries.size());
		Assert.assertEquals(
			newFinderCachePopulationEntry,
			finderCachePopulationEntries.get(
				newFinderCachePopulationEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			FinderCachePopulationEntryLocalServiceUtil.
				getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<FinderCachePopulationEntry>() {

				@Override
				public void performAction(
					FinderCachePopulationEntry finderCachePopulationEntry) {

					Assert.assertNotNull(finderCachePopulationEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		FinderCachePopulationEntry newFinderCachePopulationEntry =
			addFinderCachePopulationEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			FinderCachePopulationEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"pinderCachePopulationEntryId",
				newFinderCachePopulationEntry.
					getPinderCachePopulationEntryId()));

		List<FinderCachePopulationEntry> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		FinderCachePopulationEntry existingFinderCachePopulationEntry =
			result.get(0);

		Assert.assertEquals(
			existingFinderCachePopulationEntry, newFinderCachePopulationEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			FinderCachePopulationEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"pinderCachePopulationEntryId", RandomTestUtil.nextLong()));

		List<FinderCachePopulationEntry> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		FinderCachePopulationEntry newFinderCachePopulationEntry =
			addFinderCachePopulationEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			FinderCachePopulationEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("pinderCachePopulationEntryId"));

		Object newPinderCachePopulationEntryId =
			newFinderCachePopulationEntry.getPinderCachePopulationEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"pinderCachePopulationEntryId",
				new Object[] {newPinderCachePopulationEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingPinderCachePopulationEntryId = result.get(0);

		Assert.assertEquals(
			existingPinderCachePopulationEntryId,
			newPinderCachePopulationEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			FinderCachePopulationEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("pinderCachePopulationEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"pinderCachePopulationEntryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		FinderCachePopulationEntry newFinderCachePopulationEntry =
			addFinderCachePopulationEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newFinderCachePopulationEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromDatabase()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(true);
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromSession()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(false);
	}

	private void _testResetOriginalValuesWithDynamicQuery(boolean clearSession)
		throws Exception {

		FinderCachePopulationEntry newFinderCachePopulationEntry =
			addFinderCachePopulationEntry();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			FinderCachePopulationEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"pinderCachePopulationEntryId",
				newFinderCachePopulationEntry.
					getPinderCachePopulationEntryId()));

		List<FinderCachePopulationEntry> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		FinderCachePopulationEntry finderCachePopulationEntry) {

		Assert.assertEquals(
			finderCachePopulationEntry.getUniqueName(),
			ReflectionTestUtil.invoke(
				finderCachePopulationEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uniqueName"));
	}

	protected FinderCachePopulationEntry addFinderCachePopulationEntry()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		FinderCachePopulationEntry finderCachePopulationEntry =
			_persistence.create(pk);

		finderCachePopulationEntry.setGroupId(RandomTestUtil.nextLong());

		finderCachePopulationEntry.setCompanyId(RandomTestUtil.nextLong());

		finderCachePopulationEntry.setUniqueName(RandomTestUtil.randomString());

		_finderCachePopulationEntries.add(
			_persistence.update(finderCachePopulationEntry));

		return finderCachePopulationEntry;
	}

	private List<FinderCachePopulationEntry> _finderCachePopulationEntries =
		new ArrayList<FinderCachePopulationEntry>();
	private FinderCachePopulationEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
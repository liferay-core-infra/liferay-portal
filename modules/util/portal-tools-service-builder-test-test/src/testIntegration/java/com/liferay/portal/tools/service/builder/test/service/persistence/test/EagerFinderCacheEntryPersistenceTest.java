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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchEagerFinderCacheEntryException;
import com.liferay.portal.tools.service.builder.test.model.EagerFinderCacheEntry;
import com.liferay.portal.tools.service.builder.test.service.EagerFinderCacheEntryLocalServiceUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.EagerFinderCacheEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.EagerFinderCacheEntryUtil;

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
public class EagerFinderCacheEntryPersistenceTest {

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
		_persistence = EagerFinderCacheEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<EagerFinderCacheEntry> iterator =
			_eagerFinderCacheEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		EagerFinderCacheEntry eagerFinderCacheEntry = _persistence.create(pk);

		Assert.assertNotNull(eagerFinderCacheEntry);

		Assert.assertEquals(eagerFinderCacheEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		EagerFinderCacheEntry newEagerFinderCacheEntry =
			addEagerFinderCacheEntry();

		_persistence.remove(newEagerFinderCacheEntry);

		EagerFinderCacheEntry existingEagerFinderCacheEntry =
			_persistence.fetchByPrimaryKey(
				newEagerFinderCacheEntry.getPrimaryKey());

		Assert.assertNull(existingEagerFinderCacheEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addEagerFinderCacheEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		EagerFinderCacheEntry newEagerFinderCacheEntry = _persistence.create(
			pk);

		newEagerFinderCacheEntry.setGroupId(RandomTestUtil.nextLong());

		newEagerFinderCacheEntry.setCompanyId(RandomTestUtil.nextLong());

		newEagerFinderCacheEntry.setUniqueName(RandomTestUtil.randomString());

		_eagerFinderCacheEntries.add(
			_persistence.update(newEagerFinderCacheEntry));

		EagerFinderCacheEntry existingEagerFinderCacheEntry =
			_persistence.findByPrimaryKey(
				newEagerFinderCacheEntry.getPrimaryKey());

		Assert.assertEquals(
			existingEagerFinderCacheEntry.getEagerFinderCacheEntryId(),
			newEagerFinderCacheEntry.getEagerFinderCacheEntryId());
		Assert.assertEquals(
			existingEagerFinderCacheEntry.getGroupId(),
			newEagerFinderCacheEntry.getGroupId());
		Assert.assertEquals(
			existingEagerFinderCacheEntry.getCompanyId(),
			newEagerFinderCacheEntry.getCompanyId());
		Assert.assertEquals(
			existingEagerFinderCacheEntry.getUniqueName(),
			newEagerFinderCacheEntry.getUniqueName());
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
		EagerFinderCacheEntry newEagerFinderCacheEntry =
			addEagerFinderCacheEntry();

		EagerFinderCacheEntry existingEagerFinderCacheEntry =
			_persistence.findByPrimaryKey(
				newEagerFinderCacheEntry.getPrimaryKey());

		Assert.assertEquals(
			existingEagerFinderCacheEntry, newEagerFinderCacheEntry);
	}

	@Test(expected = NoSuchEagerFinderCacheEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<EagerFinderCacheEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"EagerFinderCacheEntry", "eagerFinderCacheEntryId", true, "groupId",
			true, "companyId", true, "uniqueName", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		EagerFinderCacheEntry newEagerFinderCacheEntry =
			addEagerFinderCacheEntry();

		EagerFinderCacheEntry existingEagerFinderCacheEntry =
			_persistence.fetchByPrimaryKey(
				newEagerFinderCacheEntry.getPrimaryKey());

		Assert.assertEquals(
			existingEagerFinderCacheEntry, newEagerFinderCacheEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		EagerFinderCacheEntry missingEagerFinderCacheEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingEagerFinderCacheEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		EagerFinderCacheEntry newEagerFinderCacheEntry1 =
			addEagerFinderCacheEntry();
		EagerFinderCacheEntry newEagerFinderCacheEntry2 =
			addEagerFinderCacheEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newEagerFinderCacheEntry1.getPrimaryKey());
		primaryKeys.add(newEagerFinderCacheEntry2.getPrimaryKey());

		Map<Serializable, EagerFinderCacheEntry> eagerFinderCacheEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, eagerFinderCacheEntries.size());
		Assert.assertEquals(
			newEagerFinderCacheEntry1,
			eagerFinderCacheEntries.get(
				newEagerFinderCacheEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newEagerFinderCacheEntry2,
			eagerFinderCacheEntries.get(
				newEagerFinderCacheEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, EagerFinderCacheEntry> eagerFinderCacheEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(eagerFinderCacheEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		EagerFinderCacheEntry newEagerFinderCacheEntry =
			addEagerFinderCacheEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newEagerFinderCacheEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, EagerFinderCacheEntry> eagerFinderCacheEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, eagerFinderCacheEntries.size());
		Assert.assertEquals(
			newEagerFinderCacheEntry,
			eagerFinderCacheEntries.get(
				newEagerFinderCacheEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, EagerFinderCacheEntry> eagerFinderCacheEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(eagerFinderCacheEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		EagerFinderCacheEntry newEagerFinderCacheEntry =
			addEagerFinderCacheEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newEagerFinderCacheEntry.getPrimaryKey());

		Map<Serializable, EagerFinderCacheEntry> eagerFinderCacheEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, eagerFinderCacheEntries.size());
		Assert.assertEquals(
			newEagerFinderCacheEntry,
			eagerFinderCacheEntries.get(
				newEagerFinderCacheEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			EagerFinderCacheEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<EagerFinderCacheEntry>() {

				@Override
				public void performAction(
					EagerFinderCacheEntry eagerFinderCacheEntry) {

					Assert.assertNotNull(eagerFinderCacheEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		EagerFinderCacheEntry newEagerFinderCacheEntry =
			addEagerFinderCacheEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			EagerFinderCacheEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"eagerFinderCacheEntryId",
				newEagerFinderCacheEntry.getEagerFinderCacheEntryId()));

		List<EagerFinderCacheEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		EagerFinderCacheEntry existingEagerFinderCacheEntry = result.get(0);

		Assert.assertEquals(
			existingEagerFinderCacheEntry, newEagerFinderCacheEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			EagerFinderCacheEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"eagerFinderCacheEntryId", RandomTestUtil.nextLong()));

		List<EagerFinderCacheEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		EagerFinderCacheEntry newEagerFinderCacheEntry =
			addEagerFinderCacheEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			EagerFinderCacheEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("eagerFinderCacheEntryId"));

		Object newEagerFinderCacheEntryId =
			newEagerFinderCacheEntry.getEagerFinderCacheEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"eagerFinderCacheEntryId",
				new Object[] {newEagerFinderCacheEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingEagerFinderCacheEntryId = result.get(0);

		Assert.assertEquals(
			existingEagerFinderCacheEntryId, newEagerFinderCacheEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			EagerFinderCacheEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("eagerFinderCacheEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"eagerFinderCacheEntryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		EagerFinderCacheEntry newEagerFinderCacheEntry =
			addEagerFinderCacheEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newEagerFinderCacheEntry.getPrimaryKey()));
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

		EagerFinderCacheEntry newEagerFinderCacheEntry =
			addEagerFinderCacheEntry();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			EagerFinderCacheEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"eagerFinderCacheEntryId",
				newEagerFinderCacheEntry.getEagerFinderCacheEntryId()));

		List<EagerFinderCacheEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		EagerFinderCacheEntry eagerFinderCacheEntry) {

		Assert.assertEquals(
			eagerFinderCacheEntry.getUniqueName(),
			ReflectionTestUtil.invoke(
				eagerFinderCacheEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uniqueName"));
	}

	protected EagerFinderCacheEntry addEagerFinderCacheEntry()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		EagerFinderCacheEntry eagerFinderCacheEntry = _persistence.create(pk);

		eagerFinderCacheEntry.setGroupId(RandomTestUtil.nextLong());

		eagerFinderCacheEntry.setCompanyId(RandomTestUtil.nextLong());

		eagerFinderCacheEntry.setUniqueName(RandomTestUtil.randomString());

		_eagerFinderCacheEntries.add(
			_persistence.update(eagerFinderCacheEntry));

		return eagerFinderCacheEntry;
	}

	private List<EagerFinderCacheEntry> _eagerFinderCacheEntries =
		new ArrayList<EagerFinderCacheEntry>();
	private EagerFinderCacheEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
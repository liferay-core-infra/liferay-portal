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
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchSequenceEntryException;
import com.liferay.portal.tools.service.builder.test.model.SequenceEntry;
import com.liferay.portal.tools.service.builder.test.service.SequenceEntryLocalServiceUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.SequenceEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.SequenceEntryUtil;

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
public class SequenceEntryPersistenceTest {

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
		_persistence = SequenceEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SequenceEntry> iterator = _sequenceEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SequenceEntry sequenceEntry = _persistence.create(pk);

		Assert.assertNotNull(sequenceEntry);

		Assert.assertEquals(sequenceEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SequenceEntry newSequenceEntry = addSequenceEntry();

		_persistence.remove(newSequenceEntry);

		SequenceEntry existingSequenceEntry = _persistence.fetchByPrimaryKey(
			newSequenceEntry.getPrimaryKey());

		Assert.assertNull(existingSequenceEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSequenceEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SequenceEntry newSequenceEntry = _persistence.create(pk);

		newSequenceEntry.setUuid(RandomTestUtil.randomString());

		_sequenceEntries.add(_persistence.update(newSequenceEntry));

		SequenceEntry existingSequenceEntry = _persistence.findByPrimaryKey(
			newSequenceEntry.getPrimaryKey());

		Assert.assertEquals(
			existingSequenceEntry.getUuid(), newSequenceEntry.getUuid());
		Assert.assertEquals(
			existingSequenceEntry.getSequenceEntryId(),
			newSequenceEntry.getSequenceEntryId());
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SequenceEntry newSequenceEntry = addSequenceEntry();

		SequenceEntry existingSequenceEntry = _persistence.findByPrimaryKey(
			newSequenceEntry.getPrimaryKey());

		Assert.assertEquals(existingSequenceEntry, newSequenceEntry);
	}

	@Test(expected = NoSuchSequenceEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SequenceEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"SequenceEntry", "uuid", true, "sequenceEntryId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SequenceEntry newSequenceEntry = addSequenceEntry();

		SequenceEntry existingSequenceEntry = _persistence.fetchByPrimaryKey(
			newSequenceEntry.getPrimaryKey());

		Assert.assertEquals(existingSequenceEntry, newSequenceEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SequenceEntry missingSequenceEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSequenceEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SequenceEntry newSequenceEntry1 = addSequenceEntry();
		SequenceEntry newSequenceEntry2 = addSequenceEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSequenceEntry1.getPrimaryKey());
		primaryKeys.add(newSequenceEntry2.getPrimaryKey());

		Map<Serializable, SequenceEntry> sequenceEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, sequenceEntries.size());
		Assert.assertEquals(
			newSequenceEntry1,
			sequenceEntries.get(newSequenceEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newSequenceEntry2,
			sequenceEntries.get(newSequenceEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SequenceEntry> sequenceEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(sequenceEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SequenceEntry newSequenceEntry = addSequenceEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSequenceEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SequenceEntry> sequenceEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, sequenceEntries.size());
		Assert.assertEquals(
			newSequenceEntry,
			sequenceEntries.get(newSequenceEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SequenceEntry> sequenceEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(sequenceEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SequenceEntry newSequenceEntry = addSequenceEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSequenceEntry.getPrimaryKey());

		Map<Serializable, SequenceEntry> sequenceEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, sequenceEntries.size());
		Assert.assertEquals(
			newSequenceEntry,
			sequenceEntries.get(newSequenceEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			SequenceEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<SequenceEntry>() {

				@Override
				public void performAction(SequenceEntry sequenceEntry) {
					Assert.assertNotNull(sequenceEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		SequenceEntry newSequenceEntry = addSequenceEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SequenceEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"sequenceEntryId", newSequenceEntry.getSequenceEntryId()));

		List<SequenceEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		SequenceEntry existingSequenceEntry = result.get(0);

		Assert.assertEquals(existingSequenceEntry, newSequenceEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SequenceEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"sequenceEntryId", RandomTestUtil.nextLong()));

		List<SequenceEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		SequenceEntry newSequenceEntry = addSequenceEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SequenceEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("sequenceEntryId"));

		Object newSequenceEntryId = newSequenceEntry.getSequenceEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"sequenceEntryId", new Object[] {newSequenceEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingSequenceEntryId = result.get(0);

		Assert.assertEquals(existingSequenceEntryId, newSequenceEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SequenceEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("sequenceEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"sequenceEntryId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected SequenceEntry addSequenceEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SequenceEntry sequenceEntry = _persistence.create(pk);

		sequenceEntry.setUuid(RandomTestUtil.randomString());

		_sequenceEntries.add(_persistence.update(sequenceEntry));

		return sequenceEntry;
	}

	private List<SequenceEntry> _sequenceEntries =
		new ArrayList<SequenceEntry>();
	private SequenceEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
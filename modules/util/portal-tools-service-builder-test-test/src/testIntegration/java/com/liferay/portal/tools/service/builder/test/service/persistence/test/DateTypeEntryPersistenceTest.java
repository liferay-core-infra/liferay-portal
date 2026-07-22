/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
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
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchDateTypeEntryException;
import com.liferay.portal.tools.service.builder.test.model.DateTypeEntry;
import com.liferay.portal.tools.service.builder.test.service.DateTypeEntryLocalServiceUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.DateTypeEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.DateTypeEntryUtil;

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
public class DateTypeEntryPersistenceTest {

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
		_persistence = DateTypeEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DateTypeEntry> iterator = _dateTypeEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DateTypeEntry dateTypeEntry = _persistence.create(pk);

		Assert.assertNotNull(dateTypeEntry);

		Assert.assertEquals(dateTypeEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DateTypeEntry newDateTypeEntry = addDateTypeEntry();

		_persistence.remove(newDateTypeEntry);

		DateTypeEntry existingDateTypeEntry = _persistence.fetchByPrimaryKey(
			newDateTypeEntry.getPrimaryKey());

		Assert.assertNull(existingDateTypeEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDateTypeEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		DateTypeEntry newDateTypeEntry = addDateTypeEntry();

		newDateTypeEntry.setDateValue(RandomTestUtil.nextDate());

		newDateTypeEntry = _persistence.update(newDateTypeEntry);

		_dateTypeEntries.add(newDateTypeEntry);

		DateTypeEntry existingDateTypeEntry = _persistence.findByPrimaryKey(
			newDateTypeEntry.getPrimaryKey());

		Assert.assertEquals(
			existingDateTypeEntry.getDateTypeEntryId(),
			newDateTypeEntry.getDateTypeEntryId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDateTypeEntry.getDateValue()),
			Time.getShortTimestamp(newDateTypeEntry.getDateValue()));
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DateTypeEntry newDateTypeEntry = addDateTypeEntry();

		DateTypeEntry existingDateTypeEntry = _persistence.findByPrimaryKey(
			newDateTypeEntry.getPrimaryKey());

		Assert.assertEquals(existingDateTypeEntry, newDateTypeEntry);
	}

	@Test(expected = NoSuchDateTypeEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DateTypeEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DateTypeEntry", "dateTypeEntryId", true, "dateValue", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DateTypeEntry newDateTypeEntry = addDateTypeEntry();

		DateTypeEntry existingDateTypeEntry = _persistence.fetchByPrimaryKey(
			newDateTypeEntry.getPrimaryKey());

		Assert.assertEquals(existingDateTypeEntry, newDateTypeEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DateTypeEntry missingDateTypeEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDateTypeEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DateTypeEntry newDateTypeEntry1 = addDateTypeEntry();
		DateTypeEntry newDateTypeEntry2 = addDateTypeEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDateTypeEntry1.getPrimaryKey());
		primaryKeys.add(newDateTypeEntry2.getPrimaryKey());

		Map<Serializable, DateTypeEntry> dateTypeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, dateTypeEntries.size());
		Assert.assertEquals(
			newDateTypeEntry1,
			dateTypeEntries.get(newDateTypeEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newDateTypeEntry2,
			dateTypeEntries.get(newDateTypeEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DateTypeEntry> dateTypeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dateTypeEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DateTypeEntry newDateTypeEntry = addDateTypeEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDateTypeEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DateTypeEntry> dateTypeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dateTypeEntries.size());
		Assert.assertEquals(
			newDateTypeEntry,
			dateTypeEntries.get(newDateTypeEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DateTypeEntry> dateTypeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dateTypeEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DateTypeEntry newDateTypeEntry = addDateTypeEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDateTypeEntry.getPrimaryKey());

		Map<Serializable, DateTypeEntry> dateTypeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dateTypeEntries.size());
		Assert.assertEquals(
			newDateTypeEntry,
			dateTypeEntries.get(newDateTypeEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			DateTypeEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<DateTypeEntry>() {

				@Override
				public void performAction(DateTypeEntry dateTypeEntry) {
					Assert.assertNotNull(dateTypeEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		DateTypeEntry newDateTypeEntry = addDateTypeEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DateTypeEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"dateTypeEntryId", newDateTypeEntry.getDateTypeEntryId()));

		List<DateTypeEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		DateTypeEntry existingDateTypeEntry = result.get(0);

		Assert.assertEquals(existingDateTypeEntry, newDateTypeEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DateTypeEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"dateTypeEntryId", RandomTestUtil.nextLong()));

		List<DateTypeEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		DateTypeEntry newDateTypeEntry = addDateTypeEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DateTypeEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("dateTypeEntryId"));

		Object newDateTypeEntryId = newDateTypeEntry.getDateTypeEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"dateTypeEntryId", new Object[] {newDateTypeEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingDateTypeEntryId = result.get(0);

		Assert.assertEquals(existingDateTypeEntryId, newDateTypeEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DateTypeEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("dateTypeEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"dateTypeEntryId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected DateTypeEntry addDateTypeEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DateTypeEntry dateTypeEntry = _persistence.create(pk);

		dateTypeEntry.setDateValue(RandomTestUtil.nextDate());

		_dateTypeEntries.add(_persistence.update(dateTypeEntry));

		return dateTypeEntry;
	}

	private List<DateTypeEntry> _dateTypeEntries =
		new ArrayList<DateTypeEntry>();
	private DateTypeEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1261530637
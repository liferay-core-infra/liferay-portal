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
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchBadColumnNameEntryException;
import com.liferay.portal.tools.service.builder.test.model.BadColumnNameEntry;
import com.liferay.portal.tools.service.builder.test.service.BadColumnNameEntryLocalServiceUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.BadColumnNameEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.BadColumnNameEntryUtil;

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
public class BadColumnNameEntryPersistenceTest {

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
		_persistence = BadColumnNameEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<BadColumnNameEntry> iterator =
			_badColumnNameEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BadColumnNameEntry badColumnNameEntry = _persistence.create(pk);

		Assert.assertNotNull(badColumnNameEntry);

		Assert.assertEquals(badColumnNameEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		BadColumnNameEntry newBadColumnNameEntry = addBadColumnNameEntry();

		_persistence.remove(newBadColumnNameEntry);

		BadColumnNameEntry existingBadColumnNameEntry =
			_persistence.fetchByPrimaryKey(
				newBadColumnNameEntry.getPrimaryKey());

		Assert.assertNull(existingBadColumnNameEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addBadColumnNameEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BadColumnNameEntry newBadColumnNameEntry = _persistence.create(pk);

		newBadColumnNameEntry.setType(RandomTestUtil.randomString());

		_badColumnNameEntries.add(_persistence.update(newBadColumnNameEntry));

		BadColumnNameEntry existingBadColumnNameEntry =
			_persistence.findByPrimaryKey(
				newBadColumnNameEntry.getPrimaryKey());

		Assert.assertEquals(
			existingBadColumnNameEntry.getBadColumnNameEntryId(),
			newBadColumnNameEntry.getBadColumnNameEntryId());
		Assert.assertEquals(
			existingBadColumnNameEntry.getType(),
			newBadColumnNameEntry.getType());
	}

	@Test
	public void testCountByType() throws Exception {
		_persistence.countByType("");

		_persistence.countByType("null");

		_persistence.countByType((String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		BadColumnNameEntry newBadColumnNameEntry = addBadColumnNameEntry();

		BadColumnNameEntry existingBadColumnNameEntry =
			_persistence.findByPrimaryKey(
				newBadColumnNameEntry.getPrimaryKey());

		Assert.assertEquals(existingBadColumnNameEntry, newBadColumnNameEntry);
	}

	@Test(expected = NoSuchBadColumnNameEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<BadColumnNameEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"BadColumnNameEntry", "badColumnNameEntryId", true, "type", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		BadColumnNameEntry newBadColumnNameEntry = addBadColumnNameEntry();

		BadColumnNameEntry existingBadColumnNameEntry =
			_persistence.fetchByPrimaryKey(
				newBadColumnNameEntry.getPrimaryKey());

		Assert.assertEquals(existingBadColumnNameEntry, newBadColumnNameEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BadColumnNameEntry missingBadColumnNameEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingBadColumnNameEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		BadColumnNameEntry newBadColumnNameEntry1 = addBadColumnNameEntry();
		BadColumnNameEntry newBadColumnNameEntry2 = addBadColumnNameEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newBadColumnNameEntry1.getPrimaryKey());
		primaryKeys.add(newBadColumnNameEntry2.getPrimaryKey());

		Map<Serializable, BadColumnNameEntry> badColumnNameEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, badColumnNameEntries.size());
		Assert.assertEquals(
			newBadColumnNameEntry1,
			badColumnNameEntries.get(newBadColumnNameEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newBadColumnNameEntry2,
			badColumnNameEntries.get(newBadColumnNameEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, BadColumnNameEntry> badColumnNameEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(badColumnNameEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		BadColumnNameEntry newBadColumnNameEntry = addBadColumnNameEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newBadColumnNameEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, BadColumnNameEntry> badColumnNameEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, badColumnNameEntries.size());
		Assert.assertEquals(
			newBadColumnNameEntry,
			badColumnNameEntries.get(newBadColumnNameEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, BadColumnNameEntry> badColumnNameEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(badColumnNameEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		BadColumnNameEntry newBadColumnNameEntry = addBadColumnNameEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newBadColumnNameEntry.getPrimaryKey());

		Map<Serializable, BadColumnNameEntry> badColumnNameEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, badColumnNameEntries.size());
		Assert.assertEquals(
			newBadColumnNameEntry,
			badColumnNameEntries.get(newBadColumnNameEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			BadColumnNameEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<BadColumnNameEntry>() {

				@Override
				public void performAction(
					BadColumnNameEntry badColumnNameEntry) {

					Assert.assertNotNull(badColumnNameEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		BadColumnNameEntry newBadColumnNameEntry = addBadColumnNameEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			BadColumnNameEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"badColumnNameEntryId",
				newBadColumnNameEntry.getBadColumnNameEntryId()));

		List<BadColumnNameEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		BadColumnNameEntry existingBadColumnNameEntry = result.get(0);

		Assert.assertEquals(existingBadColumnNameEntry, newBadColumnNameEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			BadColumnNameEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"badColumnNameEntryId", RandomTestUtil.nextLong()));

		List<BadColumnNameEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		BadColumnNameEntry newBadColumnNameEntry = addBadColumnNameEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			BadColumnNameEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("badColumnNameEntryId"));

		Object newBadColumnNameEntryId =
			newBadColumnNameEntry.getBadColumnNameEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"badColumnNameEntryId",
				new Object[] {newBadColumnNameEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingBadColumnNameEntryId = result.get(0);

		Assert.assertEquals(
			existingBadColumnNameEntryId, newBadColumnNameEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			BadColumnNameEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("badColumnNameEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"badColumnNameEntryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected BadColumnNameEntry addBadColumnNameEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BadColumnNameEntry badColumnNameEntry = _persistence.create(pk);

		badColumnNameEntry.setType(RandomTestUtil.randomString());

		_badColumnNameEntries.add(_persistence.update(badColumnNameEntry));

		return badColumnNameEntry;
	}

	private List<BadColumnNameEntry> _badColumnNameEntries =
		new ArrayList<BadColumnNameEntry>();
	private BadColumnNameEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
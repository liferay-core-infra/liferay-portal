/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.service.persistence.test;

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
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.production.readiness.ignore.exception.NoSuchIgnoredRuleException;
import com.liferay.production.readiness.ignore.model.IgnoredRule;
import com.liferay.production.readiness.ignore.service.IgnoredRuleLocalServiceUtil;
import com.liferay.production.readiness.ignore.service.persistence.IgnoredRulePersistence;
import com.liferay.production.readiness.ignore.service.persistence.IgnoredRuleUtil;

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
public class IgnoredRulePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.production.readiness.ignore.service"));

	@Before
	public void setUp() {
		_persistence = IgnoredRuleUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<IgnoredRule> iterator = _ignoredRules.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		IgnoredRule ignoredRule = _persistence.create(pk);

		Assert.assertNotNull(ignoredRule);

		Assert.assertEquals(ignoredRule.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		IgnoredRule newIgnoredRule = addIgnoredRule();

		_persistence.remove(newIgnoredRule);

		IgnoredRule existingIgnoredRule = _persistence.fetchByPrimaryKey(
			newIgnoredRule.getPrimaryKey());

		Assert.assertNull(existingIgnoredRule);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addIgnoredRule();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		IgnoredRule newIgnoredRule = _persistence.create(pk);

		newIgnoredRule.setMvccVersion(RandomTestUtil.nextLong());

		newIgnoredRule.setCompanyId(RandomTestUtil.nextLong());

		newIgnoredRule.setUserId(RandomTestUtil.nextLong());

		newIgnoredRule.setUserName(RandomTestUtil.randomString());

		newIgnoredRule.setCreateDate(RandomTestUtil.nextDate());

		newIgnoredRule.setModifiedDate(RandomTestUtil.nextDate());

		newIgnoredRule.setRuleKey(RandomTestUtil.randomString());

		newIgnoredRule.setReason(RandomTestUtil.randomString());

		_ignoredRules.add(_persistence.update(newIgnoredRule));

		IgnoredRule existingIgnoredRule = _persistence.findByPrimaryKey(
			newIgnoredRule.getPrimaryKey());

		Assert.assertEquals(
			existingIgnoredRule.getMvccVersion(),
			newIgnoredRule.getMvccVersion());
		Assert.assertEquals(
			existingIgnoredRule.getIgnoredRuleId(),
			newIgnoredRule.getIgnoredRuleId());
		Assert.assertEquals(
			existingIgnoredRule.getCompanyId(), newIgnoredRule.getCompanyId());
		Assert.assertEquals(
			existingIgnoredRule.getUserId(), newIgnoredRule.getUserId());
		Assert.assertEquals(
			existingIgnoredRule.getUserName(), newIgnoredRule.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingIgnoredRule.getCreateDate()),
			Time.getShortTimestamp(newIgnoredRule.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingIgnoredRule.getModifiedDate()),
			Time.getShortTimestamp(newIgnoredRule.getModifiedDate()));
		Assert.assertEquals(
			existingIgnoredRule.getRuleKey(), newIgnoredRule.getRuleKey());
		Assert.assertEquals(
			existingIgnoredRule.getReason(), newIgnoredRule.getReason());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByC_R() throws Exception {
		_persistence.countByC_R(RandomTestUtil.nextLong(), "");

		_persistence.countByC_R(0L, "null");

		_persistence.countByC_R(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		IgnoredRule newIgnoredRule = addIgnoredRule();

		IgnoredRule existingIgnoredRule = _persistence.findByPrimaryKey(
			newIgnoredRule.getPrimaryKey());

		Assert.assertEquals(existingIgnoredRule, newIgnoredRule);
	}

	@Test(expected = NoSuchIgnoredRuleException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<IgnoredRule> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"IgnoredRule", "mvccVersion", true, "ignoredRuleId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "ruleKey", true, "reason", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		IgnoredRule newIgnoredRule = addIgnoredRule();

		IgnoredRule existingIgnoredRule = _persistence.fetchByPrimaryKey(
			newIgnoredRule.getPrimaryKey());

		Assert.assertEquals(existingIgnoredRule, newIgnoredRule);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		IgnoredRule missingIgnoredRule = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingIgnoredRule);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		IgnoredRule newIgnoredRule1 = addIgnoredRule();
		IgnoredRule newIgnoredRule2 = addIgnoredRule();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newIgnoredRule1.getPrimaryKey());
		primaryKeys.add(newIgnoredRule2.getPrimaryKey());

		Map<Serializable, IgnoredRule> ignoredRules =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ignoredRules.size());
		Assert.assertEquals(
			newIgnoredRule1, ignoredRules.get(newIgnoredRule1.getPrimaryKey()));
		Assert.assertEquals(
			newIgnoredRule2, ignoredRules.get(newIgnoredRule2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, IgnoredRule> ignoredRules =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ignoredRules.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		IgnoredRule newIgnoredRule = addIgnoredRule();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newIgnoredRule.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, IgnoredRule> ignoredRules =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ignoredRules.size());
		Assert.assertEquals(
			newIgnoredRule, ignoredRules.get(newIgnoredRule.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, IgnoredRule> ignoredRules =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ignoredRules.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		IgnoredRule newIgnoredRule = addIgnoredRule();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newIgnoredRule.getPrimaryKey());

		Map<Serializable, IgnoredRule> ignoredRules =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ignoredRules.size());
		Assert.assertEquals(
			newIgnoredRule, ignoredRules.get(newIgnoredRule.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			IgnoredRuleLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<IgnoredRule>() {

				@Override
				public void performAction(IgnoredRule ignoredRule) {
					Assert.assertNotNull(ignoredRule);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		IgnoredRule newIgnoredRule = addIgnoredRule();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			IgnoredRule.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ignoredRuleId", newIgnoredRule.getIgnoredRuleId()));

		List<IgnoredRule> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		IgnoredRule existingIgnoredRule = result.get(0);

		Assert.assertEquals(existingIgnoredRule, newIgnoredRule);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			IgnoredRule.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ignoredRuleId", RandomTestUtil.nextLong()));

		List<IgnoredRule> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		IgnoredRule newIgnoredRule = addIgnoredRule();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			IgnoredRule.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("ignoredRuleId"));

		Object newIgnoredRuleId = newIgnoredRule.getIgnoredRuleId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"ignoredRuleId", new Object[] {newIgnoredRuleId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingIgnoredRuleId = result.get(0);

		Assert.assertEquals(existingIgnoredRuleId, newIgnoredRuleId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			IgnoredRule.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("ignoredRuleId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"ignoredRuleId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		IgnoredRule newIgnoredRule = addIgnoredRule();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newIgnoredRule.getPrimaryKey()));
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

		IgnoredRule newIgnoredRule = addIgnoredRule();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			IgnoredRule.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ignoredRuleId", newIgnoredRule.getIgnoredRuleId()));

		List<IgnoredRule> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(IgnoredRule ignoredRule) {
		Assert.assertEquals(
			Long.valueOf(ignoredRule.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				ignoredRule, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			ignoredRule.getRuleKey(),
			ReflectionTestUtil.invoke(
				ignoredRule, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "ruleKey"));
	}

	protected IgnoredRule addIgnoredRule() throws Exception {
		long pk = RandomTestUtil.nextLong();

		IgnoredRule ignoredRule = _persistence.create(pk);

		ignoredRule.setMvccVersion(RandomTestUtil.nextLong());

		ignoredRule.setCompanyId(RandomTestUtil.nextLong());

		ignoredRule.setUserId(RandomTestUtil.nextLong());

		ignoredRule.setUserName(RandomTestUtil.randomString());

		ignoredRule.setCreateDate(RandomTestUtil.nextDate());

		ignoredRule.setModifiedDate(RandomTestUtil.nextDate());

		ignoredRule.setRuleKey(RandomTestUtil.randomString());

		ignoredRule.setReason(RandomTestUtil.randomString());

		_ignoredRules.add(_persistence.update(ignoredRule));

		return ignoredRule;
	}

	private List<IgnoredRule> _ignoredRules = new ArrayList<IgnoredRule>();
	private IgnoredRulePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1065331664
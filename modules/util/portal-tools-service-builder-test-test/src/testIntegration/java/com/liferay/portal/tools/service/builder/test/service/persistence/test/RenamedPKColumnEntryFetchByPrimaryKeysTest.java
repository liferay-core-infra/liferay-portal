/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.Dialect;
import com.liferay.portal.kernel.dao.orm.ORMException;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.dao.orm.SessionWrapper;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.model.RenamedPKColumnEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.RenamedPKColumnEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.RenamedPKColumnEntryUtil;

import java.io.Serializable;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jeff Wu
 */
@RunWith(Arquillian.class)
public class RenamedPKColumnEntryFetchByPrimaryKeysTest {

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
		_persistence = RenamedPKColumnEntryUtil.getPersistence();
	}

	@Test
	public void testFetchByPrimaryKeys() throws Exception {
		RenamedPKColumnEntry renamedPKColumnEntry1 = _addRenamedPKColumnEntry();
		RenamedPKColumnEntry renamedPKColumnEntry2 = _addRenamedPKColumnEntry();

		Set<Serializable> primaryKeys = new HashSet<>();

		primaryKeys.add(renamedPKColumnEntry1.getPrimaryKey());
		primaryKeys.add(renamedPKColumnEntry2.getPrimaryKey());

		List<String> queryStrings = new ArrayList<>();

		Map<Serializable, RenamedPKColumnEntry> renamedPKColumnEntries =
			_fetchByPrimaryKeys(primaryKeys, queryStrings);

		Assert.assertEquals(queryStrings.toString(), 1, queryStrings.size());

		String queryString = queryStrings.get(0);

		Assert.assertTrue(queryString.contains("renamedPKColumnEntryId"));
		Assert.assertFalse(queryString.contains("rPKColumnEntryId"));

		Assert.assertEquals(
			renamedPKColumnEntries.toString(), 2,
			renamedPKColumnEntries.size());
		Assert.assertEquals(
			renamedPKColumnEntry1,
			renamedPKColumnEntries.get(renamedPKColumnEntry1.getPrimaryKey()));
		Assert.assertEquals(
			renamedPKColumnEntry2,
			renamedPKColumnEntries.get(renamedPKColumnEntry2.getPrimaryKey()));
	}

	private RenamedPKColumnEntry _addRenamedPKColumnEntry() throws Exception {
		RenamedPKColumnEntry renamedPKColumnEntry = _persistence.create(
			RandomTestUtil.nextLong());

		renamedPKColumnEntry.setName(RandomTestUtil.randomString());

		return _persistence.update(renamedPKColumnEntry);
	}

	private Map<Serializable, RenamedPKColumnEntry> _fetchByPrimaryKeys(
		Set<Serializable> primaryKeys, List<String> queryStrings) {

		SessionFactory sessionFactory = ReflectionTestUtil.getFieldValue(
			_persistence, "_sessionFactory");

		ReflectionTestUtil.setFieldValue(
			_persistence, "_sessionFactory",
			new CapturingSessionFactory(sessionFactory, queryStrings));

		try {
			return _persistence.fetchByPrimaryKeys(primaryKeys);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_persistence, "_sessionFactory", sessionFactory);
		}
	}

	private RenamedPKColumnEntryPersistence _persistence;

	private static class CapturingSession extends SessionWrapper {

		public CapturingSession(Session session, List<String> queryStrings) {
			super(session);

			_session = session;
			_queryStrings = queryStrings;
		}

		@Override
		public Query createQuery(String queryString) throws ORMException {
			_queryStrings.add(queryString);

			return super.createQuery(queryString);
		}

		public Session getWrappedSession() {
			return _session;
		}

		private final List<String> _queryStrings;
		private final Session _session;

	}

	private static class CapturingSessionFactory implements SessionFactory {

		public CapturingSessionFactory(
			SessionFactory sessionFactory, List<String> queryStrings) {

			_sessionFactory = sessionFactory;
			_queryStrings = queryStrings;
		}

		@Override
		public void closeSession(Session session) throws ORMException {
			if (session instanceof CapturingSession) {
				CapturingSession capturingSession = (CapturingSession)session;

				session = capturingSession.getWrappedSession();
			}

			_sessionFactory.closeSession(session);
		}

		@Override
		public Session getCurrentSession() throws ORMException {
			return _sessionFactory.getCurrentSession();
		}

		@Override
		public Dialect getDialect() throws ORMException {
			return _sessionFactory.getDialect();
		}

		@Override
		public Session openNewSession(Connection connection)
			throws ORMException {

			return new CapturingSession(
				_sessionFactory.openNewSession(connection), _queryStrings);
		}

		@Override
		public Session openSession() throws ORMException {
			return new CapturingSession(
				_sessionFactory.openSession(), _queryStrings);
		}

		private final List<String> _queryStrings;
		private final SessionFactory _sessionFactory;

	}

}
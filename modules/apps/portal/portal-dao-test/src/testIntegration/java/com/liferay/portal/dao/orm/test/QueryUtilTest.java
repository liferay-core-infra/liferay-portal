/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Sampsa Sohlman
 */
@RunWith(Arquillian.class)
public class QueryUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), TransactionalTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_db = DBManagerUtil.getDB();

		_db.runSQL(_SQL_CREATE_TABLE);
		_db.runSQL(createInserts(_SIZE));
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_db.runSQL(_SQL_DROP_TABLE);
	}

	@Before
	public void setUp() {
		_sessionFactory = ReflectionTestUtil.getFieldValue(
			_userPersistence, "_sessionFactory");
	}

	@Test
	public void testListModifiableAllPos() throws Exception {
		testList(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, false, _SIZE, 0, _SIZE - 1);
	}

	@Test
	public void testListModifiableFaultyParameters1() throws Exception {
		testList(-1, 0, false, 0, 0, 0);
	}

	@Test
	public void testListModifiableFaultyParameters2() throws Exception {
		testList(0, -1, false, 0, 0, 0);
	}

	@Test
	public void testListModifiableFaultyParameters3() throws Exception {
		testList(-2, -2, false, 0, 0, 0);
	}

	@Test
	public void testListModifiableFaultyParameters4() throws Exception {
		testList(-1, 10, false, 10, 0, 9);
	}

	@Test
	public void testListModifiableFaultyParameters5() throws Exception {
		testList(10, 8, false, 0, 0, 0);
	}

	@Test
	public void testListModifiableFirstTen() throws Exception {
		testList(0, 10, false, 10, 0, 9);
	}

	@Test
	public void testListModifiableFiveAfterFive() throws Exception {
		testList(5, 10, false, 5, 5, 9);
	}

	@Test
	public void testListModifiableTooBigRange() throws Exception {
		testList(_SIZE + 1, _SIZE + 21, false, 0, 0, 0);
	}

	@Test
	public void testListUnmodifiableAllPos() throws Exception {
		testList(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, true, _SIZE, 0, _SIZE - 1);
	}

	@Test
	public void testListUnmodifiableFaultyParameters1() throws Exception {
		testList(-1, 0, true, 0, 0, 0);
	}

	@Test
	public void testListUnmodifiableFaultyParameters2() throws Exception {
		testList(0, -1, true, 0, 0, 0);
	}

	@Test
	public void testListUnmodifiableFaultyParameters3() throws Exception {
		testList(-2, -2, true, 0, 0, 0);
	}

	@Test
	public void testListUnmodifiableFaultyParameters4() throws Exception {
		testList(-1, 10, true, 10, 0, 9);
	}

	@Test
	public void testListUnmodifiableFaultyParameters5() throws Exception {
		testList(10, 8, true, 0, 0, 0);
	}

	@Test
	public void testListUnmodifiableFirstTen() throws Exception {
		testList(0, 10, true, 10, 0, 9);
	}

	@Test
	public void testListUnmodifiableFiveAfterFive() throws Exception {
		testList(5, 10, true, 5, 5, 9);
	}

	@Test
	public void testListUnmodifiableTooBigRange() throws Exception {
		testList(_SIZE + 1, _SIZE + 21, true, 0, 0, 0);
	}

	@Test
	public void testSQLWithLimitWithDistinct() throws Exception {
		Session session = null;

		try {
			session = _sessionFactory.openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(
				"SELECT DISTINCT id FROM (SELECT id FROM QueryUtilTest UNION " +
					"ALL SELECT id FROM QueryUtilTest) unionTable ORDER BY " +
						"id DESC");

			sqlQuery.addScalar("id", Type.INTEGER);

			Assert.assertEquals(
				Arrays.asList(14, 13, 12, 11, 10),
				QueryUtil.list(
					sqlQuery, _sessionFactory.getDialect(), 5, 10, true));
		}
		finally {
			_sessionFactory.closeSession(session);
		}
	}

	@Test
	public void testSQLWithLimitWithOrderByParameter() throws Exception {
		Session session = null;

		try {
			session = _sessionFactory.openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(
				"SELECT id FROM QueryUtilTest ORDER BY CASE WHEN value < ? " +
					"THEN 1 ELSE 0 END ASC, id ASC");

			sqlQuery.addScalar("id", Type.INTEGER);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(10);

			Assert.assertEquals(
				Arrays.asList(10, 11, 12, 13, 14),
				QueryUtil.list(
					sqlQuery, _sessionFactory.getDialect(), 0, 5, true));
		}
		finally {
			_sessionFactory.closeSession(session);
		}
	}

	@Test
	public void testSQLWithLimitWithOuterOrder1Ascending() throws Exception {
		_testSQLWithLimitWithOuterOrder(
			"value ASC", 0, 10, Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
	}

	@Test
	public void testSQLWithLimitWithOuterOrder1Descending() throws Exception {
		_testSQLWithLimitWithOuterOrder(
			"value DESC", 0, 10,
			Arrays.asList(19, 18, 17, 16, 15, 14, 13, 12, 11, 10));
	}

	@Test
	public void testSQLWithLimitWithOuterOrder2Ascending() throws Exception {
		_testSQLWithLimitWithOuterOrder(
			"value ASC", 5, 15,
			Arrays.asList(5, 6, 7, 8, 9, 10, 11, 12, 13, 14));
	}

	@Test
	public void testSQLWithLimitWithOuterOrder2Descending() throws Exception {
		_testSQLWithLimitWithOuterOrder(
			"value DESC", 5, 15,
			Arrays.asList(14, 13, 12, 11, 10, 9, 8, 7, 6, 5));
	}

	@Test
	public void testSQLWithLimitWithOuterOrder3Ascending() throws Exception {
		_testSQLWithLimitWithOuterOrder(
			"value ASC", 10, 20,
			Arrays.asList(10, 11, 12, 13, 14, 15, 16, 17, 18, 19));
	}

	@Test
	public void testSQLWithLimitWithOuterOrder3Descending() throws Exception {
		_testSQLWithLimitWithOuterOrder(
			"value DESC", 10, 20, Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0));
	}

	@Test
	public void testSQLWithLimitWithOuterOrderMultipleColumns()
		throws Exception {

		_testSQLWithLimitWithOuterOrder(
			"id ASC, value DESC", 0, 10,
			Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
	}

	@Test
	public void testSQLWithLimitWithOuterOrderMultipleColumnsIncludingCastClobText()
		throws Exception {

		_testSQLWithLimitWithOuterOrder(
			"id ASC, CAST_CLOB_TEXT(name) DESC", 0, 10,
			Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
	}

	@Test
	public void testSQLWithLimitWithOuterOrderParenthesisInLiteral()
		throws Exception {

		_testSQLWithLimitWithOuterOrder(
			"CASE WHEN id < 10 THEN 'b)' ELSE 'a)' END ASC, id ASC", 0, 5,
			Arrays.asList(10, 11, 12, 13, 14));
	}

	@Test
	public void testUnionSQL1() throws Exception {
		testUnionSQL(
			"ASC", _SIZE / 2, _SIZE + (_SIZE / 2), _SIZE, "id", "value");
	}

	@Test
	public void testUnionSQL2() throws Exception {
		testUnionSQL(
			"DESC", _SIZE / 2, _SIZE + (_SIZE / 2), _SIZE, "value", "id");
	}

	@Test
	public void testUnionSQL3() throws Exception {
		testUnionSQL("ASC", 0, _SIZE, _SIZE, "id", "id");
	}

	@Test
	public void testUnionSQL4() throws Exception {
		testUnionSQL("DESC", 0, _SIZE, _SIZE, "value", "value");
	}

	@Test
	public void testUnionSQL5() throws Exception {
		testUnionSQL("ASC", _SIZE, _SIZE * 2, _SIZE, "value", "value");
	}

	@Test
	public void testUnionSQL6() throws Exception {
		testUnionSQL("DESC", _SIZE, _SIZE * 2, _SIZE, "id", "id");
	}

	@Test
	public void testUnionSQLWithoutOrderBy() throws Exception {
		Session session = null;

		try {
			session = _sessionFactory.openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(
				_SQL_UNION_SELECT);

			List<Object[]> result = (List<Object[]>)QueryUtil.list(
				sqlQuery, _sessionFactory.getDialect(), _SIZE / 2,
				_SIZE + (_SIZE / 2), true);

			Assert.assertEquals(result.toString(), _SIZE, result.size());
		}
		finally {
			_sessionFactory.closeSession(session);
		}
	}

	protected static String[] createInserts(int amount) {
		String[] sqls = new String[amount];

		for (int i = 0; i < amount; i++) {
			sqls[i] = StringUtil.replace(
				_SQL_INSERT, new String[] {"[$ID$]", "[$VALUE$]"},
				new String[] {String.valueOf(i), String.valueOf(i)});
		}

		return sqls;
	}

	protected void testList(
			int start, int end, boolean unmodifiable, int expectedSize,
			int expectedFirstValue, int expectedLastValue)
		throws Exception {

		Session session = null;

		try {
			session = _sessionFactory.openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(_SQL_SELECT);

			List<Object[]> result = (List<Object[]>)QueryUtil.list(
				sqlQuery, _sessionFactory.getDialect(), start, end,
				unmodifiable);

			Assert.assertNotNull(result);
			Assert.assertEquals(result.toString(), expectedSize, result.size());

			if (expectedSize > 0) {
				Object[] firstRow = result.get(0);
				Object[] lastRow = result.get(result.size() - 1);

				Number firstId = (Number)firstRow[0];
				Number lastId = (Number)lastRow[0];

				Assert.assertEquals(expectedFirstValue, firstId.intValue());
				Assert.assertEquals(expectedLastValue, lastId.intValue());
			}

			try {
				result.add(new Object[0]);

				expectedSize++;

				Assert.assertFalse(unmodifiable);
			}
			catch (UnsupportedOperationException
						unsupportedOperationException) {

				Assert.assertTrue(unmodifiable);
			}

			Assert.assertEquals(result.toString(), expectedSize, result.size());
		}
		finally {
			_sessionFactory.closeSession(session);
		}
	}

	protected void testUnionSQL(
			String order, int start, int end, int size, String firstType,
			String lastType)
		throws Exception {

		Session session = null;

		try {
			session = _sessionFactory.openSession();

			String sql = _SQL_UNION_SELECT;

			if (order != null) {
				sql += " ORDER BY type ".concat(order);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			List<Object[]> result = (List<Object[]>)QueryUtil.list(
				sqlQuery, _sessionFactory.getDialect(), start, end, true);

			Assert.assertEquals(result.toString(), size, result.size());

			Object[] firstRow = result.get(0);
			Object[] lastRow = result.get(result.size() - 1);

			Assert.assertEquals(
				firstType, StringUtil.trim((String)firstRow[0]));
			Assert.assertEquals(lastType, StringUtil.trim((String)lastRow[0]));
		}
		finally {
			_sessionFactory.closeSession(session);
		}
	}

	private void _testSQLWithLimitWithOuterOrder(
			String orderBy, int start, int end, List<Integer> expectedIds)
		throws Exception {

		Session session = null;

		try {
			session = _sessionFactory.openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(
				"SELECT id FROM QueryUtilTest ORDER BY ".concat(orderBy));

			sqlQuery.addScalar("id", Type.INTEGER);

			Assert.assertEquals(
				expectedIds,
				QueryUtil.list(
					sqlQuery, _sessionFactory.getDialect(), start, end, true));
		}
		finally {
			_sessionFactory.closeSession(session);
		}
	}

	private static final int _SIZE = 20;

	private static final String _SQL_CREATE_TABLE =
		"CREATE TABLE QueryUtilTest (id INTEGER NOT NULL PRIMARY KEY, value " +
			"INTEGER, name TEXT)";

	private static final String _SQL_DROP_TABLE = "DROP TABLE QueryUtilTest";

	private static final String _SQL_INSERT =
		"INSERT INTO QueryUtilTest VALUES ([$ID$], [$VALUE$], '[$VALUE$]')";

	private static final String _SQL_SELECT =
		"SELECT id, value FROM QueryUtilTest ORDER BY id ASC";

	private static final String _SQL_UNION_SELECT =
		"( SELECT 'value' AS type, id as value from QueryUtilTest ) UNION " +
			"ALL ( SELECT 'id' AS type, id as value from QueryUtilTest )";

	private static DB _db;

	private SessionFactory _sessionFactory;

	@Inject
	private UserPersistence _userPersistence;

}
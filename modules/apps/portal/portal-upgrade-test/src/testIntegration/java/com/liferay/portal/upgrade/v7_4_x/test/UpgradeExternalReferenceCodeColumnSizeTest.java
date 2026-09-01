/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.v7_4_x.UpgradeExternalReferenceCodeColumnSize;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class UpgradeExternalReferenceCodeColumnSizeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_db.runSQL(
			"create table TestTable1 (testTable1Id INT not null primary key, " +
				"externalReferenceCode VARCHAR(75) null, testValue " +
					"VARCHAR(75) null)");
		_db.runSQL(
			"create table TestTable2 (testTable2Id INT not null primary key, " +
				"testEntryERC VARCHAR(75) null)");
		_db.runSQL(
			"create table TestTable3 (testTable3Id INT not null primary key, " +
				"testEntryExternalReferenceCode VARCHAR(75) null)");
	}

	@After
	public void tearDown() throws Exception {
		_db.runSQL("DROP_TABLE_IF_EXISTS(TestTable1)");
		_db.runSQL("DROP_TABLE_IF_EXISTS(TestTable2)");
		_db.runSQL("DROP_TABLE_IF_EXISTS(TestTable3)");
	}

	@Test
	public void testUpgrade() throws Exception {
		UpgradeExternalReferenceCodeColumnSize
			upgradeExternalReferenceCodeColumnSize =
				new UpgradeExternalReferenceCodeColumnSize();

		upgradeExternalReferenceCodeColumnSize.upgrade();

		try (Connection connection = DataAccess.getConnection()) {
			_assertColumnSize(
				connection, "TestTable1", "externalReferenceCode", 500);
			_assertColumnSize(connection, "TestTable1", "testValue", 75);
			_assertColumnSize(connection, "TestTable2", "testEntryERC", 500);
			_assertColumnSize(
				connection, "TestTable3", "testEntryExternalReferenceCode",
				500);
		}
	}

	private void _assertColumnSize(
			Connection connection, String tableName, String columnName,
			int expectedColumnSize)
		throws Exception {

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		DBInspector dbInspector = new DBInspector(connection);

		try (ResultSet resultSet = databaseMetaData.getColumns(
				dbInspector.getCatalog(), dbInspector.getSchema(),
				dbInspector.normalizeName(tableName),
				dbInspector.normalizeName(columnName))) {

			Assert.assertTrue(resultSet.next());

			Assert.assertEquals(
				expectedColumnSize, resultSet.getInt("COLUMN_SIZE"));

			Assert.assertFalse(resultSet.next());
		}
	}

	private final DB _db = DBManagerUtil.getDB();

}
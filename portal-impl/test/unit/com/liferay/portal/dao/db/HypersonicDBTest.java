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

package com.liferay.portal.dao.db;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.test.BaseDBTestCase;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PropsUtil;

import com.zaxxer.hikari.HikariDataSource;

import java.io.File;

import java.net.URL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jodd.io.FileUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Miguel Pastor
 * @author Alberto Chaparro
 */
public class HypersonicDBTest extends BaseDBTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testDatabaseURL() throws Exception {
		String jdbcURL = PropsUtil.get(PropsKeys.JDBC_DEFAULT_URL);

		Assert.assertNotNull(
			"Unable to read jdbc URL from portal properties", jdbcURL);

		if (!jdbcURL.contains("hsqldb")) {
			return;
		}

		URL baseURL = getClass().getResource("");

		File testDatabaseDir = new File(baseURL.getPath(), "dependencies");

		String liferayHome = PropsUtil.get(PropsKeys.LIFERAY_HOME);

		PropsUtil.set(PropsKeys.LIFERAY_HOME, testDatabaseDir.getPath());

		jdbcURL = PropsUtil.get(PropsKeys.JDBC_DEFAULT_URL);

		try (HikariDataSource hikariDataSource = new HikariDataSource()) {
			hikariDataSource.setUsername("sa");
			hikariDataSource.setPassword("");
			hikariDataSource.setJdbcUrl(jdbcURL);

			try (Connection connection = hikariDataSource.getConnection()) {
				Statement statement = connection.createStatement();

				Map<String, String> databasePropertiesMap = new HashMap<>();

				ResultSet resultSet = statement.executeQuery(
					"SELECT * FROM INFORMATION_SCHEMA.SYSTEM_PROPERTIES");

				while (resultSet.next()) {
					String key = resultSet.getString("PROPERTY_NAME");
					String value = resultSet.getString("PROPERTY_VALUE");

					if ((key != null) && (value != null)) {
						databasePropertiesMap.put(key, value);
					}
				}

				Map<String, String> jdbcURLPropertiesMap =
					_extractJdbcURLProperties(jdbcURL);

				for (Map.Entry<String, String> jdbcURLPropertyEntry :
						jdbcURLPropertiesMap.entrySet()) {

					Assert.assertEquals(
						"Unable to find property \"" + jdbcURLPropertyEntry +
							"\" in HSQL database properties",
						jdbcURLPropertyEntry.getValue(),
						databasePropertiesMap.get(
							jdbcURLPropertyEntry.getKey()));
				}
			}
		}
		finally {
			FileUtil.deleteDir(testDatabaseDir);

			PropsUtil.set(PropsKeys.LIFERAY_HOME, liferayHome);
		}
	}

	@Test
	public void testRewordAlterColumnType() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder alter column userName varchar(75);\n",
			buildSQL("alter_column_type DLFolder userName VARCHAR(75);"));
	}

	@Test
	public void testRewordAlterColumnTypeNoSemicolon() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder alter column userName varchar(75);\n",
			buildSQL("alter_column_type DLFolder userName VARCHAR(75)"));
	}

	@Test
	public void testRewordAlterColumnTypeNotNull() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder alter column userName varchar(75);alter " +
				"table DLFolder alter column userName set not null;\n",
			buildSQL(
				"alter_column_type DLFolder userName VARCHAR(75) not null;"));
	}

	@Test
	public void testRewordAlterColumnTypeNull() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder alter column userName varchar(75);alter " +
				"table DLFolder alter column userName set null;\n",
			buildSQL("alter_column_type DLFolder userName VARCHAR(75) null;"));
	}

	@Test
	public void testRewordRenameTable() throws Exception {
		Assert.assertEquals(
			"alter table a rename to b;\n", buildSQL(RENAME_TABLE_QUERY));
	}

	@Override
	protected DB getDB() {
		return new HypersonicDB(0, 0);
	}

	private Map<String, String> _extractJdbcURLProperties(String jdbcURL) {
		int pos = jdbcURL.indexOf(CharPool.SEMICOLON);

		if (pos < 0) {
			return Collections.emptyMap();
		}

		Map<String, String> map = new HashMap<>();

		for (String property :
				StringUtil.split(jdbcURL.substring(pos), CharPool.SEMICOLON)) {

			List<String> keyValue = StringUtil.split(property, CharPool.EQUAL);

			Assert.assertEquals(
				"Malformed property: " + keyValue, keyValue.size(), 2);

			map.put(keyValue.get(0), keyValue.get(1));
		}

		return map;
	}

}
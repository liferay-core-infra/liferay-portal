/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Sampsa Sohlman
 */
public class SQLServerLimitStringUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testDistinct() throws Exception {
		String sql =
			"SELECT DISTINCT JournalArticle.* FROM JournalArticle ORDER BY " +
				"userName ASC";

		Assert.assertEquals(
			sql + " offset 10 rows fetch next 20 rows only",
			SQLServerLimitStringUtil.getLimitString(sql, 10, 30));
	}

	@Test
	public void testOrderByInSubquery() throws Exception {
		String sql =
			"select t.articleId from (select top 10 articleId from " +
				"JournalArticle order by modifiedDate DESC) t";

		Assert.assertEquals(
			StringBundler.concat(
				"select * from (select *, row_number() over ( order by ",
				"CURRENT_TIMESTAMP) as _page_row_num from (", sql,
				" ) _temp_table_1 ) _temp_table_2 where _page_row_num between ",
				"1 and 5 order by _page_row_num"),
			SQLServerLimitStringUtil.getLimitString(sql, 0, 5));
	}

	@Test
	public void testOrderByWithColumnInSelectList() throws Exception {
		String sql =
			"SELECT articleId, userName FROM JournalArticle ORDER BY " +
				"userName ASC";

		Assert.assertEquals(
			sql + " offset 10 rows fetch next 20 rows only",
			SQLServerLimitStringUtil.getLimitString(sql, 10, 30));
	}

	@Test
	public void testOrderByWithColumnNotInSelectList() throws Exception {
		String sql =
			"SELECT articleId, userName FROM JournalArticle ORDER BY " +
				"modifiedDate ASC";

		Assert.assertEquals(
			sql + " offset 10 rows fetch next 20 rows only",
			SQLServerLimitStringUtil.getLimitString(sql, 10, 30));
	}

	@Test
	public void testOrderByWithFunctionExpression() throws Exception {
		String sql = SQLServerLimitStringUtil.getLimitString(
			StringBundler.concat(
				"select tempgroup.groupid from (select distinct ",
				"group_.groupid, group_.name from group_ where ",
				"group_.companyid = ?) tempgroup order by ",
				"replace(tempgroup.name, ?, ?) asc"),
			0, 20);

		Assert.assertTrue(
			sql, sql.contains("replace(tempgroup.name, ?, ?) asc"));
	}

	@Test
	public void testOrderByWithMultipleColumnsIncludingTextCast()
		throws Exception {

		String sql = SQLServerLimitStringUtil.getLimitString(
			"select Layout.priority, Layout.name from Layout order by " +
				"Layout.priority ASC, CAST(Layout.name AS NVARCHAR(MAX)) ASC",
			20, 40);

		Assert.assertTrue(
			sql.contains(
				"order by Layout.priority ASC, CAST(Layout.name AS " +
					"NVARCHAR(MAX)) ASC"));
	}

	@Test
	public void testOrderByWithParenthesisInLiteral() throws Exception {
		String sql =
			"SELECT articleId, userName FROM JournalArticle ORDER BY CASE " +
				"WHEN userName = ')' THEN 0 ELSE 1 END ASC";

		Assert.assertEquals(
			sql + " offset 10 rows fetch next 20 rows only",
			SQLServerLimitStringUtil.getLimitString(sql, 10, 30));
	}

	@Test
	public void testOrderByWithTextCast() throws Exception {
		String sql = StringBundler.concat(
			"select DDMStructure.* from DDMStructure where ",
			"DDMStructure.companyId = ? order by CAST(DDMStructure.name AS ",
			"NVARCHAR(MAX)) ASC");

		Assert.assertEquals(
			sql + " offset 20 rows fetch next 20 rows only",
			SQLServerLimitStringUtil.getLimitString(sql, 20, 40));
	}

	@Test
	public void testUnionWithFieldsQuery() throws Exception {
		String sql = SQLServerLimitStringUtil.getLimitString(
			"( SELECT articleId, userName FROM JournalArticle ) UNION ALL ( " +
				"SELECT articleId, userName FROM JournalArticle )",
			10, 30);

		Assert.assertTrue(sql.indexOf("30") > 0);
		Assert.assertTrue(sql.indexOf("11") > 0);
		Assert.assertTrue(sql.indexOf("top") != 0);
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Tuple;

import java.io.InputStream;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Eric Yan
 */
public class IndexEntryTest {

	@Before
	public void setUp() throws Exception {
		Class<?> clazz = getClass();

		try (InputStream inputStream = clazz.getResourceAsStream(
				"/META-INF/sql/indexes.sql")) {

			_indexSQLs = ListUtil.filter(
				ListUtil.fromString(StringUtil.read(inputStream)),
				sql -> sql.contains(" on IndexEntry "));
		}
	}

	@Test
	public void testBTreeOptimization() throws Exception {
		_assertIndexes(
			Arrays.asList(
				new Tuple(
					Arrays.asList(
						"companyId", "ctCollectionId", "externalReferenceCode"),
					true),
				new Tuple(Arrays.asList("ownerId"), false),
				new Tuple(Arrays.asList("ownerType", "ownerId", "plid"), false),
				new Tuple(Arrays.asList("plid"), false),
				new Tuple(
					Arrays.asList(
						"portletId", "ownerType", "ownerId", "companyId"),
					false),
				new Tuple(
					Arrays.asList(
						"portletId", "ownerType", "ownerId", "plid",
						"ctCollectionId"),
					true),
				new Tuple(
					Arrays.asList("portletId", "ownerType", "plid"), false),
				new Tuple(Arrays.asList("portletId", "plid"), false)),
			_indexSQLs);
	}

	@Test
	public void testCtCollectionId() throws Exception {
		List<String> indexSQLsWithCtCollectionId = ListUtil.filter(
			_indexSQLs,
			sql -> ListUtil.exists(
				_getTrimmedIndexColumnNames(sql),
				columnName -> columnName.equals("ctCollectionId")));

		_assertIndexes(
			Arrays.asList(
				new Tuple(
					Arrays.asList(
						"companyId", "ctCollectionId", "externalReferenceCode"),
					true),
				new Tuple(
					Arrays.asList(
						"portletId", "ownerType", "ownerId", "plid",
						"ctCollectionId"),
					true)),
			indexSQLsWithCtCollectionId);
	}

	@Test
	public void testExternalReferenceCode() throws Exception {
		List<String> indexSQLsWithExternalReferenceCode = ListUtil.filter(
			_indexSQLs,
			sql -> ListUtil.exists(
				_getTrimmedIndexColumnNames(sql),
				columnName -> columnName.equals("externalReferenceCode")));

		_assertIndexes(
			Arrays.asList(
				new Tuple(
					Arrays.asList(
						"companyId", "ctCollectionId", "externalReferenceCode"),
					true)),
			indexSQLsWithExternalReferenceCode);
	}

	private void _assertIndexes(List<Tuple> expectedTuples, List<String> sqls) {
		Assert.assertEquals(
			sqls.toString(), expectedTuples.size(), sqls.size());

		for (int i = 0; i < expectedTuples.size(); i++) {
			Tuple expectedTuple = expectedTuples.get(i);

			List<String> expectedColumnNames =
				(List<String>)expectedTuple.getObject(0);
			boolean unique = (boolean)expectedTuple.getObject(1);

			String sql = sqls.get(i);

			Assert.assertEquals(
				expectedColumnNames, _getTrimmedIndexColumnNames(sql));

			if (unique) {
				Assert.assertTrue(sql.startsWith("create unique index"));
			}
			else {
				Assert.assertTrue(sql.startsWith("create index"));
			}
		}
	}

	private List<String> _getTrimmedIndexColumnNames(String sql) {
		return TransformUtil.transform(
			StringUtil.split(
				sql.substring(
					sql.indexOf(CharPool.OPEN_PARENTHESIS) + 1,
					sql.indexOf(CharPool.CLOSE_PARENTHESIS))),
			columnName -> {
				columnName = columnName.trim();

				int index = columnName.indexOf("[$COLUMN_LENGTH:");

				if (index > 0) {
					columnName = columnName.substring(0, index);
				}

				return columnName;
			});
	}

	private List<String> _indexSQLs;

}
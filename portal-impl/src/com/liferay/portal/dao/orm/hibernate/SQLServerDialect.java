/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.portal.kernel.util.StringUtil;

import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.SQLServer2012LimitHandler;
import org.hibernate.query.spi.Limit;
import org.hibernate.query.spi.QueryOptions;
import org.hibernate.sql.ast.spi.ParameterMarkerStrategy;

/**
 * @author Jiefeng Wu
 */
public class SQLServerDialect extends org.hibernate.dialect.SQLServerDialect {

	@Override
	public LimitHandler getLimitHandler() {
		return _limitHandler;
	}

	private static final LimitHandler _limitHandler =
		new SQLServer2012LimitHandler() {

			@Override
			public String processSql(
				String sql, int parameterCount,
				ParameterMarkerStrategy parameterMarkerStrategy,
				QueryOptions queryOptions) {

				return _replaceDummyOrderBy(
					sql,
					super.processSql(
						sql, parameterCount, parameterMarkerStrategy,
						queryOptions));
			}

			@Override
			public String processSql(String sql, Limit limit) {
				return _replaceDummyOrderBy(sql, super.processSql(sql, limit));
			}

			private String _replaceDummyOrderBy(String sql, String limitSql) {
				if (!limitSql.contains(_DUMMY_ORDER_BY)) {
					return limitSql;
				}

				String lowerCaseSQL = StringUtil.toLowerCase(sql);

				if (!lowerCaseSQL.contains("distinct")) {
					return limitSql;
				}

				return StringUtil.replace(
					limitSql, _DUMMY_ORDER_BY, " order by 1");
			}

			private static final String _DUMMY_ORDER_BY = " order by @@version";

		};

}
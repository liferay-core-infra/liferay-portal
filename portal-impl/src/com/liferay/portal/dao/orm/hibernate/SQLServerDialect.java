/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.portal.kernel.util.StringUtil;

import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.SQLServer2012LimitHandler;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.query.spi.Limit;
import org.hibernate.query.spi.QueryOptions;
import org.hibernate.sql.ast.spi.ParameterMarkerStrategy;

/**
 * @author Jiefeng Wu
 */
public class SQLServerDialect extends org.hibernate.dialect.SQLServerDialect {

	public SQLServerDialect(DialectResolutionInfo dialectResolutionInfo) {
		super(dialectResolutionInfo);
	}

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
					super.processSql(
						sql, parameterCount, parameterMarkerStrategy,
						queryOptions));
			}

			@Override
			public String processSql(String sql, Limit limit) {
				return _replaceDummyOrderBy(super.processSql(sql, limit));
			}

			private String _replaceDummyOrderBy(String limitSql) {
				String lowerCaseLimitSql = StringUtil.toLowerCase(limitSql);

				if (lowerCaseLimitSql.contains("distinct")) {
					return StringUtil.replace(
						limitSql, " order by @@version", " order by 1");
				}

				return limitSql;
			}

		};

}
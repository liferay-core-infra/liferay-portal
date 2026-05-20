/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;IgnoredRule&quot; database table.
 *
 * @author Lily Chi
 * @see IgnoredRule
 * @generated
 */
public class IgnoredRuleTable extends BaseTable<IgnoredRuleTable> {

	public static final IgnoredRuleTable INSTANCE = new IgnoredRuleTable();

	public final Column<IgnoredRuleTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<IgnoredRuleTable, Long> ignoredRuleId = createColumn(
		"ignoredRuleId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<IgnoredRuleTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<IgnoredRuleTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<IgnoredRuleTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<IgnoredRuleTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<IgnoredRuleTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<IgnoredRuleTable, String> ruleKey = createColumn(
		"ruleKey", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<IgnoredRuleTable, String> reason = createColumn(
		"reason", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private IgnoredRuleTable() {
		super("IgnoredRule", IgnoredRuleTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2007934392
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;FilterFindEntry&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see FilterFindEntry
 * @generated
 */
public class FilterFindEntryTable extends BaseTable<FilterFindEntryTable> {

	public static final FilterFindEntryTable INSTANCE =
		new FilterFindEntryTable();

	public final Column<FilterFindEntryTable, Long> filterFindEntryId =
		createColumn(
			"filterFindEntryId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<FilterFindEntryTable, Long> groupId = createColumn(
		"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FilterFindEntryTable, String> type = createColumn(
		"type_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<FilterFindEntryTable, Integer> integer = createColumn(
		"integer_", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);

	private FilterFindEntryTable() {
		super("FilterFindEntry", FilterFindEntryTable::new);
	}

}
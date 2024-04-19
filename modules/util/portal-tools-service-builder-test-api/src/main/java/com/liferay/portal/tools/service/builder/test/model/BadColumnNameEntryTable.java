/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;BadColumnNameEntry&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see BadColumnNameEntry
 * @generated
 */
public class BadColumnNameEntryTable
	extends BaseTable<BadColumnNameEntryTable> {

	public static final BadColumnNameEntryTable INSTANCE =
		new BadColumnNameEntryTable();

	public final Column<BadColumnNameEntryTable, Long> badColumnNameEntryId =
		createColumn(
			"badColumnNameEntryId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<BadColumnNameEntryTable, String> type = createColumn(
		"type_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private BadColumnNameEntryTable() {
		super("BadColumnNameEntry", BadColumnNameEntryTable::new);
	}

}
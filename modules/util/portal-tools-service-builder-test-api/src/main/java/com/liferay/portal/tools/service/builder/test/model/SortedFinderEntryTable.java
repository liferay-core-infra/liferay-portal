/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;SortedFinderEntry&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see SortedFinderEntry
 * @generated
 */
public class SortedFinderEntryTable extends BaseTable<SortedFinderEntryTable> {

	public static final SortedFinderEntryTable INSTANCE =
		new SortedFinderEntryTable();

	public final Column<SortedFinderEntryTable, Long> sortedFinderEntryId =
		createColumn(
			"sortedFinderEntryId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<SortedFinderEntryTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SortedFinderEntryTable, Long> groupId = createColumn(
		"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);

	private SortedFinderEntryTable() {
		super("SortedFinderEntry", SortedFinderEntryTable::new);
	}

}
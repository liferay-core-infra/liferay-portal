/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.util.comparator;

import com.liferay.dynamic.data.mapping.model.DDMStructureLayout;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Marcela Cunha
 */
public class StructureLayoutNameComparator
	extends OrderByComparator<DDMStructureLayout> {

	public static StructureLayoutNameComparator getInstance(boolean ascending) {
		if (ascending) {
			return _ASCENDING;
		}

		return _DESCENDING;
	}

	@Override
	public int compare(
		DDMStructureLayout ddmStructureLayout1,
		DDMStructureLayout ddmStructureLayout2) {

		String name1 = StringUtil.toLowerCase(ddmStructureLayout1.getName());
		String name2 = StringUtil.toLowerCase(ddmStructureLayout2.getName());

		int value = name1.compareTo(name2);

		if (_ascending) {
			return value;
		}

		return -value;
	}

	@Override
	public String getOrderBy() {
		if (_ascending) {
			return _ORDER_BY_ASC;
		}

		return _ORDER_BY_DESC;
	}

	@Override
	public String[] getOrderByFields() {
		return _ORDER_BY_FIELDS;
	}

	@Override
	public boolean isAscending() {
		return _ascending;
	}

	private StructureLayoutNameComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final StructureLayoutNameComparator _ASCENDING =
		new StructureLayoutNameComparator(true);

	private static final StructureLayoutNameComparator _DESCENDING =
		new StructureLayoutNameComparator(false);

	private static final String _ORDER_BY_ASC = "DDMStructureLayout.name ASC";

	private static final String _ORDER_BY_DESC = "DDMStructureLayout.name DESC";

	private static final String[] _ORDER_BY_FIELDS = {"name"};

	private final boolean _ascending;

}
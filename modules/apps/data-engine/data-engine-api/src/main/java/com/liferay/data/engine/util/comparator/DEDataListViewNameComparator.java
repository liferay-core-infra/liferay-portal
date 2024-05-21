/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.util.comparator;

import com.liferay.data.engine.model.DEDataListView;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Gabriel Albuquerque
 */
public class DEDataListViewNameComparator
	extends OrderByComparator<DEDataListView> {

	public static DEDataListViewNameComparator get(boolean ascending) {
		if (ascending) {
			return _ASCENDING;
		}

		return _DESCENDING;
	}

	@Override
	public int compare(
		DEDataListView deDataListView1, DEDataListView deDataListView2) {

		String name1 = StringUtil.toLowerCase(deDataListView1.getName());
		String name2 = StringUtil.toLowerCase(deDataListView2.getName());

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

	private DEDataListViewNameComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final DEDataListViewNameComparator _ASCENDING =
		new DEDataListViewNameComparator(true);

	private static final DEDataListViewNameComparator _DESCENDING =
		new DEDataListViewNameComparator(false);

	private static final String _ORDER_BY_ASC = "DEDataListView.name ASC";

	private static final String _ORDER_BY_DESC = "DEDataListView.name DESC";

	private static final String[] _ORDER_BY_FIELDS = {"name"};

	private final boolean _ascending;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.util.comparator;

import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.reports.engine.console.model.Definition;

/**
 * @author Rafael Praxedes
 */
public class DefinitionCreateDateComparator
	extends OrderByComparator<Definition> {

	public static DefinitionCreateDateComparator getInstance(
		boolean ascending) {

		if (ascending) {
			return _ASCENDING;
		}

		return _DESCENDING;
	}

	@Override
	public int compare(Definition definition1, Definition definition2) {
		int value = DateUtil.compareTo(
			definition1.getCreateDate(), definition2.getCreateDate());

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

	private DefinitionCreateDateComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final DefinitionCreateDateComparator _ASCENDING =
		new DefinitionCreateDateComparator(true);

	private static final DefinitionCreateDateComparator _DESCENDING =
		new DefinitionCreateDateComparator(false);

	private static final String _ORDER_BY_ASC =
		"Reports_Definition.createDate ASC";

	private static final String _ORDER_BY_DESC =
		"Reports_Definition.createDate DESC";

	private static final String[] _ORDER_BY_FIELDS = {"createDate"};

	private final boolean _ascending;

}
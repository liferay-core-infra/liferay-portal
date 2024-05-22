/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.util.comparator;

import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.reports.engine.console.model.Source;

/**
 * @author Rafael Praxedes
 */
public class SourceCreateDateComparator extends OrderByComparator<Source> {

	public static SourceCreateDateComparator getInstance(boolean ascending) {
		if (ascending) {
			return _ASCENDING;
		}

		return _DESCENDING;
	}

	@Override
	public int compare(Source source1, Source source2) {
		int value = DateUtil.compareTo(
			source1.getCreateDate(), source2.getCreateDate());

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

	private SourceCreateDateComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final SourceCreateDateComparator _ASCENDING =
		new SourceCreateDateComparator(true);

	private static final SourceCreateDateComparator _DESCENDING =
		new SourceCreateDateComparator(false);

	private static final String _ORDER_BY_ASC = "Reports_Source.createDate ASC";

	private static final String _ORDER_BY_DESC =
		"Reports_Source.createDate DESC";

	private static final String[] _ORDER_BY_FIELDS = {"createDate"};

	private final boolean _ascending;

}
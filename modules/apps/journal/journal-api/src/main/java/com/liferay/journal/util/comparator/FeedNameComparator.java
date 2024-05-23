/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.util.comparator;

import com.liferay.journal.model.JournalFeed;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Brian Wing Shun Chan
 */
public class FeedNameComparator extends OrderByComparator<JournalFeed> {

	public static FeedNameComparator getInstance(boolean ascending) {
		if (ascending) {
			return _ASCENDING;
		}

		return _DESCENDING;
	}

	@Override
	public int compare(JournalFeed feed1, JournalFeed feed2) {
		String name1 = feed1.getName();
		String name2 = feed2.getName();

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

	private FeedNameComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final FeedNameComparator _ASCENDING = new FeedNameComparator(
		true);

	private static final FeedNameComparator _DESCENDING =
		new FeedNameComparator(false);

	private static final String _ORDER_BY_ASC = "JournalFeed.name ASC";

	private static final String _ORDER_BY_DESC = "JournalFeed.name DESC";

	private static final String[] _ORDER_BY_FIELDS = {"name"};

	private final boolean _ascending;

}
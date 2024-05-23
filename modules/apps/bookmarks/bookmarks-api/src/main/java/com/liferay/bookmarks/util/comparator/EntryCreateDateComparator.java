/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.util.comparator;

import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Brian Wing Shun Chan
 */
public class EntryCreateDateComparator
	extends OrderByComparator<BookmarksEntry> {

	public static EntryCreateDateComparator getInstance(boolean ascending) {
		if (ascending) {
			return _ASCENDING;
		}

		return _DESCENDING;
	}

	@Override
	public int compare(BookmarksEntry entry1, BookmarksEntry entry2) {
		int value = DateUtil.compareTo(
			entry1.getCreateDate(), entry2.getCreateDate());

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

	private EntryCreateDateComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final EntryCreateDateComparator _ASCENDING =
		new EntryCreateDateComparator(true);

	private static final EntryCreateDateComparator _DESCENDING =
		new EntryCreateDateComparator(false);

	private static final String _ORDER_BY_ASC = "BookmarksEntry.createDate ASC";

	private static final String _ORDER_BY_DESC =
		"BookmarksEntry.createDate DESC";

	private static final String[] _ORDER_BY_FIELDS = {"createDate"};

	private final boolean _ascending;

}
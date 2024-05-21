/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.util.comparator;

import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.wiki.model.WikiPage;

/**
 * @author Samuel Liu
 */
public class PageTitleComparator extends OrderByComparator<WikiPage> {

	public static PageTitleComparator getInstance(boolean ascending) {
		if (ascending) {
			return _ASCENDING;
		}

		return _DESCENDING;
	}

	@Override
	public int compare(WikiPage page1, WikiPage page2) {
		String title1 = new String(page1.getTitle());
		String title2 = new String(page2.getTitle());

		int value = title1.compareTo(title2);

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

	private PageTitleComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final PageTitleComparator _ASCENDING =
		new PageTitleComparator(true);

	private static final PageTitleComparator _DESCENDING =
		new PageTitleComparator(false);

	private static final String _ORDER_BY_ASC = "WikiPage.title ASC";

	private static final String _ORDER_BY_DESC = "WikiPage.title DESC";

	private static final String[] _ORDER_BY_FIELDS = {"title"};

	private final boolean _ascending;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.util.comparator;

import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Brian Wing Shun Chan
 */
public class ArticleVersionComparator
	extends OrderByComparator<JournalArticle> {

	public static ArticleVersionComparator getInstance(boolean ascending) {
		if (ascending) {
			return _ASCENDING;
		}

		return _DESCENDING;
	}

	@Override
	public int compare(JournalArticle article1, JournalArticle article2) {
		int value = 0;

		if (article1.getVersion() < article2.getVersion()) {
			value = -1;
		}
		else if (article1.getVersion() > article2.getVersion()) {
			value = 1;
		}

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

	private ArticleVersionComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final ArticleVersionComparator _ASCENDING =
		new ArticleVersionComparator(true);

	private static final ArticleVersionComparator _DESCENDING =
		new ArticleVersionComparator(false);

	private static final String _ORDER_BY_ASC = "JournalArticle.version ASC";

	private static final String _ORDER_BY_DESC = "JournalArticle.version DESC";

	private static final String[] _ORDER_BY_FIELDS = {"version"};

	private final boolean _ascending;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.util.comparator;

import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Brian Wing Shun Chan
 */
public class ArticleCreateDateComparator
	extends OrderByComparator<JournalArticle> {

	public static ArticleCreateDateComparator getInstance(boolean ascending) {
		if (ascending) {
			return _ASCENDING;
		}

		return _DESCENDING;
	}

	@Override
	public int compare(JournalArticle article1, JournalArticle article2) {
		int value = DateUtil.compareTo(
			article1.getCreateDate(), article2.getCreateDate());

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

	private ArticleCreateDateComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final ArticleCreateDateComparator _ASCENDING =
		new ArticleCreateDateComparator(true);

	private static final ArticleCreateDateComparator _DESCENDING =
		new ArticleCreateDateComparator(false);

	private static final String _ORDER_BY_ASC = "JournalArticle.createDate ASC";

	private static final String _ORDER_BY_DESC =
		"JournalArticle.createDate DESC";

	private static final String[] _ORDER_BY_FIELDS = {"createDate"};

	private final boolean _ascending;

}
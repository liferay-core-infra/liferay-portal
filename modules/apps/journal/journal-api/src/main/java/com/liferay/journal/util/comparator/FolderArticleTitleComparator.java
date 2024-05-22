/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.util.comparator;

import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Jürgen Kappler
 */
public class FolderArticleTitleComparator extends OrderByComparator<Object> {

	public FolderArticleTitleComparator(boolean ascending) {
		_ascending = ascending;
	}

	@Override
	public int compare(Object object1, Object object2) {
		int value = 0;

		if ((object1 instanceof JournalArticle) &&
			(object2 instanceof JournalArticle)) {

			JournalArticle journalArticle1 = (JournalArticle)object1;

			String name1 = journalArticle1.getTitle();

			JournalArticle journalArticle2 = (JournalArticle)object2;

			String name2 = journalArticle2.getTitle();

			value = name1.compareTo(name2);
		}
		else if (object1 instanceof JournalArticle) {
			value = -1;
		}
		else if (object2 instanceof JournalArticle) {
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

	private static final String _ORDER_BY_ASC = "title ASC";

	private static final String _ORDER_BY_DESC = "title DESC";

	private static final String[] _ORDER_BY_FIELDS = {"title"};

	private final boolean _ascending;

}
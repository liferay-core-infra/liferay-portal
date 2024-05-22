/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.util.comparator;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.Date;

/**
 * @author Brian Wing Shun Chan
 */
public class FolderArticleDisplayDateComparator
	extends OrderByComparator<Object> {

	public FolderArticleDisplayDateComparator(boolean ascending) {
		_ascending = ascending;
	}

	@Override
	public int compare(Object object1, Object object2) {
		int value = 0;

		if ((object1 instanceof JournalFolder) &&
			(object2 instanceof JournalFolder)) {

			value = DateUtil.compareTo(
				getDisplayDate(object1), getDisplayDate(object2));
		}
		else if (object1 instanceof JournalFolder) {
			value = -1;
		}
		else if (object2 instanceof JournalFolder) {
			value = 1;
		}
		else {
			value = DateUtil.compareTo(
				getDisplayDate(object1), getDisplayDate(object2));
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

	protected Date getDisplayDate(Object object) {
		if (object instanceof JournalArticle) {
			JournalArticle article = (JournalArticle)object;

			return article.getModifiedDate();
		}

		JournalFolder folder = (JournalFolder)object;

		return folder.getCreateDate();
	}

	private static final String _ORDER_BY_ASC =
		"modelFolder DESC, displayDate ASC";

	private static final String _ORDER_BY_DESC =
		"modelFolder DESC, displayDate DESC";

	private static final String[] _ORDER_BY_FIELDS = {"displayDate"};

	private final boolean _ascending;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.util.comparator;

import com.liferay.exportimport.kernel.lar.StagedModelModifiedDateComparator;
import com.liferay.journal.model.JournalArticle;

/**
 * @author Brian Wing Shun Chan
 */
public class ArticleModifiedDateComparator
	extends StagedModelModifiedDateComparator<JournalArticle> {

	public ArticleModifiedDateComparator(boolean ascending) {
		super(ascending);
	}

	@Override
	public String getOrderBy() {
		if (isAscending()) {
			return _ORDER_BY_ASC;
		}

		return _ORDER_BY_DESC;
	}

	private static final String _ORDER_BY_ASC =
		"JournalArticle.modifiedDate ASC";

	private static final String _ORDER_BY_DESC =
		"JournalArticle.modifiedDate DESC";

}
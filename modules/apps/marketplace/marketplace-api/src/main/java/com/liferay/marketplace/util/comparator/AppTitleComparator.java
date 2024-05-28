/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.util.comparator;

import com.liferay.marketplace.model.App;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Ryan Park
 */
public class AppTitleComparator extends OrderByComparator<App> {

	public static final String ORDER_BY_ASC = "title ASC";

	public static final String ORDER_BY_DESC = "title DESC";

	public static final String[] ORDER_BY_FIELDS = {"title"};

	public static AppTitleComparator getInstance(boolean ascending) {
		if (ascending) {
			return _INSTANCE_ASCENDING;
		}

		return _INSTANCE_DESCENDING;
	}

	@Override
	public int compare(App app1, App app2) {
		String lowerCaseTitle1 = StringUtil.toLowerCase(app1.getTitle());
		String lowerCaseTitle2 = StringUtil.toLowerCase(app2.getTitle());

		return lowerCaseTitle1.compareTo(lowerCaseTitle2);
	}

	private AppTitleComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final AppTitleComparator _INSTANCE_ASCENDING =
		new AppTitleComparator(true);

	private static final AppTitleComparator _INSTANCE_DESCENDING =
		new AppTitleComparator(false);

	private final boolean _ascending;

}
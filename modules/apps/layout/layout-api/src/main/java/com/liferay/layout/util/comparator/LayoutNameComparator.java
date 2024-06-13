/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util.comparator;

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.util.CollatorUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.text.Collator;

import java.util.Locale;

/**
 * @author Mikel Lorza
 */
public class LayoutNameComparator extends OrderByComparator<Layout> {

	public static final String ORDER_BY_ASC = "Layout.name ASC";

	public static final String ORDER_BY_DESC = "Layout.name DESC";

	public static final String[] ORDER_BY_FIELDS = {"name"};

	public static LayoutNameComparator getInstance(boolean ascending) {
		if (ascending) {
			return _INSTANCE_ASCENDING;
		}

		return _INSTANCE_DESCENDING;
	}

	@Override
	public int compare(Layout layout1, Layout layout2) {
		String name1 = layout1.getName(_locale);
		String name2 = layout2.getName(_locale);

		int value = _collator.compare(name1, name2);

		if (_ascending) {
			return value;
		}

		return -value;
	}

	@Override
	public String getOrderBy() {
		if (_ascending) {
			return ORDER_BY_ASC;
		}

		return ORDER_BY_DESC;
	}

	@Override
	public String[] getOrderByFields() {
		return ORDER_BY_FIELDS;
	}

	@Override
	public boolean isAscending() {
		return _ascending;
	}

	private LayoutNameComparator(boolean ascending) {
		this(ascending, LocaleUtil.getDefault());
	}

	private LayoutNameComparator(boolean ascending, Locale locale) {
		_ascending = ascending;
		_locale = locale;

		_collator = CollatorUtil.getInstance(locale);
	}

	private static final LayoutNameComparator _INSTANCE_ASCENDING =
		new LayoutNameComparator(true);

	private static final LayoutNameComparator _INSTANCE_DESCENDING =
		new LayoutNameComparator(false);

	private final boolean _ascending;
	private final Collator _collator;
	private final Locale _locale;

}
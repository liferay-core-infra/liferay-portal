/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.util.comparator;

import com.liferay.fragment.model.FragmentCollection;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Jürgen Kappler
 */
public class FragmentCollectionNameComparator
	extends OrderByComparator<FragmentCollection> {

	public static FragmentCollectionNameComparator getInstance(
		boolean ascending) {

		if (ascending) {
			return _ASCENDING;
		}

		return _DESCENDING;
	}

	@Override
	public int compare(
		FragmentCollection fragmentCollection1,
		FragmentCollection fragmentCollection2) {

		String name1 = StringUtil.toLowerCase(fragmentCollection1.getName());
		String name2 = StringUtil.toLowerCase(fragmentCollection2.getName());

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

	private FragmentCollectionNameComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final FragmentCollectionNameComparator _ASCENDING =
		new FragmentCollectionNameComparator(true);

	private static final FragmentCollectionNameComparator _DESCENDING =
		new FragmentCollectionNameComparator(false);

	private static final String _ORDER_BY_ASC = "FragmentCollection.name ASC";

	private static final String _ORDER_BY_DESC = "FragmentCollection.name DESC";

	private static final String[] _ORDER_BY_FIELDS = {"name"};

	private final boolean _ascending;

}
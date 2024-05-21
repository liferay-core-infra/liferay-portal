/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.util.comparator;

import com.liferay.fragment.model.FragmentCollection;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Jürgen Kappler
 */
public class FragmentCollectionCreateDateComparator
	extends OrderByComparator<FragmentCollection> {

	public static FragmentCollectionCreateDateComparator getInstance(
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

		int value = DateUtil.compareTo(
			fragmentCollection1.getCreateDate(),
			fragmentCollection2.getCreateDate());

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

	private FragmentCollectionCreateDateComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final FragmentCollectionCreateDateComparator _ASCENDING =
		new FragmentCollectionCreateDateComparator(true);

	private static final FragmentCollectionCreateDateComparator _DESCENDING =
		new FragmentCollectionCreateDateComparator(false);

	private static final String _ORDER_BY_ASC =
		"FragmentCollection.createDate ASC";

	private static final String _ORDER_BY_DESC =
		"FragmentCollection.createDate DESC";

	private static final String[] _ORDER_BY_FIELDS = {"createDate"};

	private final boolean _ascending;

}
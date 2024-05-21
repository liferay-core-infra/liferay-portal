/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.util.comparator;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Eudaldo Alonso
 */
public class FragmentEntryLinkLastPropagationDateComparator
	extends OrderByComparator<FragmentEntryLink> {

	public static FragmentEntryLinkLastPropagationDateComparator getInstance(
		boolean ascending) {

		if (ascending) {
			return _ASCENDING;
		}

		return _DESCENDING;
	}

	@Override
	public int compare(
		FragmentEntryLink fragmentEntryLink1,
		FragmentEntryLink fragmentEntryLink2) {

		int value = DateUtil.compareTo(
			fragmentEntryLink1.getLastPropagationDate(),
			fragmentEntryLink2.getLastPropagationDate());

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

	private FragmentEntryLinkLastPropagationDateComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final FragmentEntryLinkLastPropagationDateComparator
		_ASCENDING = new FragmentEntryLinkLastPropagationDateComparator(true);

	private static final FragmentEntryLinkLastPropagationDateComparator
		_DESCENDING = new FragmentEntryLinkLastPropagationDateComparator(false);

	private static final String _ORDER_BY_ASC =
		"FragmentEntryLink.lastPropagationDate ASC";

	private static final String _ORDER_BY_DESC =
		"FragmentEntryLink.lastPropagationDate DESC";

	private static final String[] _ORDER_BY_FIELDS = {"lastPropagationDate"};

	private final boolean _ascending;

}
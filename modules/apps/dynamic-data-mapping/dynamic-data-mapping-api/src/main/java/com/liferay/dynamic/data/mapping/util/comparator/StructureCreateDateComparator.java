/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.util.comparator;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Eudaldo Alonso
 */
public class StructureCreateDateComparator
	extends OrderByComparator<DDMStructure> {

	public static StructureCreateDateComparator getInstance(boolean ascending) {
		if (ascending) {
			return _ASCENDING;
		}

		return _DESCENDING;
	}

	@Override
	public int compare(DDMStructure structure1, DDMStructure structure2) {
		int value = DateUtil.compareTo(
			structure1.getCreateDate(), structure2.getCreateDate());

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

	private StructureCreateDateComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final StructureCreateDateComparator _ASCENDING =
		new StructureCreateDateComparator(true);

	private static final StructureCreateDateComparator _DESCENDING =
		new StructureCreateDateComparator(false);

	private static final String _ORDER_BY_ASC = "DDMStructure.createDate ASC";

	private static final String _ORDER_BY_DESC = "DDMStructure.createDate DESC";

	private static final String[] _ORDER_BY_FIELDS = {"createDate"};

	private final boolean _ascending;

}
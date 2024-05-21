/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.util.comparator;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Eduardo García
 */
public class StructureIdComparator extends OrderByComparator<DDMStructure> {

	public static StructureIdComparator getInstance(boolean ascending) {
		if (ascending) {
			return _ASCENDING;
		}

		return _DESCENDING;
	}

	@Override
	public int compare(DDMStructure structure1, DDMStructure structure2) {
		long structureId1 = structure1.getStructureId();
		long structureId2 = structure2.getStructureId();

		int value = 0;

		if (structureId1 < structureId2) {
			value = -1;
		}
		else if (structureId1 > structureId2) {
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

	private StructureIdComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final StructureIdComparator _ASCENDING =
		new StructureIdComparator(true);

	private static final StructureIdComparator _DESCENDING =
		new StructureIdComparator(false);

	private static final String _ORDER_BY_ASC = "DDMStructure.structureId ASC";

	private static final String _ORDER_BY_DESC =
		"DDMStructure.structureId DESC";

	private static final String[] _ORDER_BY_FIELDS = {"structureId"};

	private final boolean _ascending;

}
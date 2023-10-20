/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.internal;

import com.liferay.expando.kernel.model.ExpandoTable;

/**
 * @author Lily Chi
 */
public class ExpandoTableImpl implements ExpandoTable {

	public ExpandoTableImpl(
		com.liferay.expando.model.ExpandoTable expandoTable) {

		_expandoTable = expandoTable;
	}

	@Override
	public long getTableId() {
		return _expandoTable.getTableId();
	}

	private final com.liferay.expando.model.ExpandoTable _expandoTable;

}
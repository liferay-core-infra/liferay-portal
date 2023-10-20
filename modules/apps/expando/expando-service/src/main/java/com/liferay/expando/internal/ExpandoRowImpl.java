/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.internal;

import com.liferay.expando.kernel.model.ExpandoRow;

import java.util.Date;

/**
 * @author Lily Chi
 */
public class ExpandoRowImpl implements ExpandoRow {

	public ExpandoRowImpl(com.liferay.expando.model.ExpandoRow expandoRow) {
		_expandoRow = expandoRow;
	}

	@Override
	public Object getExpandoRowInstance() {
		return _expandoRow;
	}

	@Override
	public Date getModifiedDate() {
		return _expandoRow.getModifiedDate();
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		_expandoRow.setModifiedDate(modifiedDate);
	}

	private final com.liferay.expando.model.ExpandoRow _expandoRow;

}
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.manager;

import com.liferay.expando.kernel.model.ExpandoRow;

/**
 * @author Lily Chi
 */
public interface ExpandoManager {

	public void deleteRows(long classPK);

	public void deleteRows(long companyId, long classNameId, long classPK);

	public ExpandoRow fetchRow(long tableId, long classPK);

	public void updateExpandoRow(ExpandoRow expandoRow);

}
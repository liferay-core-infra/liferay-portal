/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.kernel.util;

import com.liferay.expando.kernel.model.ExpandoRow;
import com.liferay.expando.manager.ExpandoManager;
import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * @author Lily Chi
 */
public class ExpandoManagerUtil {

	public static void deleteRows(long classPK) {
		ExpandoManager expandoManager = _expandoManagerSnapshot.get();

		expandoManager.deleteRows(classPK);
	}

	public static void deleteRows(
		long companyId, long classNameId, long classPK) {

		ExpandoManager expandoManager = _expandoManagerSnapshot.get();

		expandoManager.deleteRows(companyId, classNameId, classPK);
	}

	public static ExpandoRow fetchRow(long tableId, long classPK) {
		ExpandoManager expandoManager = _expandoManagerSnapshot.get();

		return expandoManager.fetchRow(tableId, classPK);
	}

	public static void updateExpandoRow(ExpandoRow expandoRow) {
		ExpandoManager expandoManager = _expandoManagerSnapshot.get();

		expandoManager.updateExpandoRow(expandoRow);
	}

	private static final Snapshot<ExpandoManager> _expandoManagerSnapshot =
		new Snapshot<>(ExpandoManagerUtil.class, ExpandoManager.class);

}
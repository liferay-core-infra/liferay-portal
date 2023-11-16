/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.kernel.util;

import com.liferay.expando.kernel.model.ExpandoRow;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.manager.ExpandoManager;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * @author Lily Chi
 */
public class ExpandoManagerUtil {

	public static void deleteExpandoTable(Object object)
		throws PortalException {

		ExpandoManager expandoManager = _expandoManagerSnapshot.get();

		expandoManager.deleteExpandoTable(object);
	}

	public static void deleteRows(long classPK) {
		ExpandoManager expandoManager = _expandoManagerSnapshot.get();

		expandoManager.deleteRows(classPK);
	}

	public static void deleteRows(
		long companyId, long classNameId, long classPK) {

		ExpandoManager expandoManager = _expandoManagerSnapshot.get();

		expandoManager.deleteRows(companyId, classNameId, classPK);
	}

	public static ExpandoTable fetchDefaultTable(
		long companyId, String className) {

		ExpandoManager expandoManager = _expandoManagerSnapshot.get();

		return expandoManager.fetchDefaultTable(companyId, className);
	}

	public static ExpandoRow fetchRow(long tableId, long classPK) {
		ExpandoManager expandoManager = _expandoManagerSnapshot.get();

		return expandoManager.fetchRow(tableId, classPK);
	}

	public static ActionableDynamicQuery
		getExpandoTableActionableDynamicQuery() {

		ExpandoManager expandoManager = _expandoManagerSnapshot.get();

		return expandoManager.getExpandoTableActionableDynamicQuery();
	}

	public static void updateExpandoRow(ExpandoRow expandoRow) {
		ExpandoManager expandoManager = _expandoManagerSnapshot.get();

		expandoManager.updateExpandoRow(expandoRow);
	}

	private static final Snapshot<ExpandoManager> _expandoManagerSnapshot =
		new Snapshot<>(ExpandoManagerUtil.class, ExpandoManager.class);

}
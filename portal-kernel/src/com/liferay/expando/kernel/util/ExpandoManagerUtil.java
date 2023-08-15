/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.kernel.util;

import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoRow;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.manager.ExpandoManager;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ServiceProxyFactory;

/**
 * @author Lily Chi
 */
public class ExpandoManagerUtil {

	public static void deleteColumn(ExpandoColumn column)
		throws PortalException {

		_expandoManager.deleteColumn(column);
	}

	public static ExpandoTable deleteExpandoTable(ExpandoTable expandoTable)
		throws PortalException {

		return (ExpandoTable)_expandoManager.deleteExpandoTable(expandoTable);
	}

	public static void deleteRows(long classPK) {
		_expandoManager.deleteRows(classPK);
	}

	public static void deleteRows(
		long companyId, long classNameId, long classPK) {

		_expandoManager.deleteRows(companyId, classNameId, classPK);
	}

	public static void deleteValues(String className, long classPK) {
		_expandoManager.deleteValues(className, classPK);
	}

	public static ExpandoTable fetchDefaultTable(
		long companyId, String className) {

		return (ExpandoTable)_expandoManager.fetchDefaultTable(
			companyId, className);
	}

	public static ExpandoRow fetchRow(long tableId, long classPK) {
		return (ExpandoRow)_expandoManager.fetchRow(tableId, classPK);
	}

	public static ActionableDynamicQuery
		getExpandColumnActionableDynamicQuery() {

		return _expandoManager.getExpandColumnActionableDynamicQuery();
	}

	public static ActionableDynamicQuery
		getExpandTableActionableDynamicQuery() {

		return _expandoManager.getExpandTableActionableDynamicQuery();
	}

	public static ExpandoRow updateExpandoRow(ExpandoRow expandoRow) {
		return (ExpandoRow)_expandoManager.updateExpandoRow(expandoRow);
	}

	private static volatile ExpandoManager _expandoManager =
		ServiceProxyFactory.newServiceTrackedInstance(
			ExpandoManager.class, ExpandoManagerUtil.class, "_expandoManager",
			false);

}
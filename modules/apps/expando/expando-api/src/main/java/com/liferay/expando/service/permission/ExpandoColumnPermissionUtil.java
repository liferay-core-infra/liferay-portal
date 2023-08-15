/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.service.permission;

import com.liferay.expando.model.ExpandoColumn;
import com.liferay.osgi.util.service.Snapshot;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

/**
 * @author Michael C. Han
 */
public class ExpandoColumnPermissionUtil {

	public static void check(
			PermissionChecker permissionChecker, ExpandoColumn column,
			String actionId)
		throws PortalException {

		ExpandoColumnPermission expandoColumnPermission =
			_expandoColumnPermissionSnapshot.get();

		expandoColumnPermission.check(permissionChecker, column, actionId);
	}

	public static void check(
			PermissionChecker permissionChecker, long columnId, String actionId)
		throws PortalException {

		ExpandoColumnPermission expandoColumnPermission =
			_expandoColumnPermissionSnapshot.get();

		expandoColumnPermission.check(permissionChecker, columnId, actionId);
	}

	public static boolean contains(
		PermissionChecker permissionChecker, ExpandoColumn column,
		String actionId) {

		ExpandoColumnPermission expandoColumnPermission =
			_expandoColumnPermissionSnapshot.get();

		return expandoColumnPermission.contains(
			permissionChecker, column, actionId);
	}

	public static boolean contains(
		PermissionChecker permissionChecker, long companyId, String className,
		String tableName, String columnName, String actionId) {

		ExpandoColumnPermission expandoColumnPermission =
			_expandoColumnPermissionSnapshot.get();

		return expandoColumnPermission.contains(
			permissionChecker, companyId, className, tableName, columnName,
			actionId);
	}

	private static final Snapshot<ExpandoColumnPermission>
		_expandoColumnPermissionSnapshot = new Snapshot<>(
			ExpandoColumnPermissionUtil.class, ExpandoColumnPermission.class);

}
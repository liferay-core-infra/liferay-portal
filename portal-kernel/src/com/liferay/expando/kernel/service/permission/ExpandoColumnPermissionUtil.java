/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.kernel.service.permission;

import com.liferay.expando.kernel.exception.NoSuchColumnException;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.service.ExpandoColumnLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

/**
 * @author Michael C. Han
 */
public class ExpandoColumnPermissionUtil {

	public static void check(
			PermissionChecker permissionChecker, ExpandoColumn column,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, column, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, ExpandoColumn.class.getName(),
				column.getColumnId(), actionId);
		}
	}

	public static void check(
			PermissionChecker permissionChecker, long columnId, String actionId)
		throws PortalException {

		if (!contains(permissionChecker, columnId, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, ExpandoColumn.class.getName(), columnId,
				actionId);
		}
	}

	public static void check(
			PermissionChecker permissionChecker, long companyId,
			String className, String tableName, String columnName,
			String actionId)
		throws PortalException {

		ExpandoColumn expandoColumn = ExpandoColumnLocalServiceUtil.getColumn(
			companyId, className, tableName, columnName);

		if (expandoColumn == null) {
			StringBundler sb = new StringBundler(6);

			sb.append("No ExpandoColumn exists with the key {");

			sb.append("tableName=");
			sb.append(ExpandoTableConstants.DEFAULT_TABLE_NAME);

			sb.append(", name=");
			sb.append(columnName);

			sb.append("}");

			throw new NoSuchColumnException(sb.toString());
		}

		check(permissionChecker, expandoColumn, actionId);
	}

	public static boolean contains(
		PermissionChecker permissionChecker, ExpandoColumn column,
		String actionId) {

		return permissionChecker.hasPermission(
			null, ExpandoColumn.class.getName(), column.getColumnId(),
			actionId);
	}

	public static boolean contains(
			PermissionChecker permissionChecker, long columnId, String actionId)
		throws PortalException {

		return contains(
			permissionChecker,
			ExpandoColumnLocalServiceUtil.getColumn(columnId), actionId);
	}

	public static boolean contains(
		PermissionChecker permissionChecker, long companyId, String className,
		String tableName, String columnName, String actionId) {

		ExpandoColumn expandoColumn = ExpandoColumnLocalServiceUtil.getColumn(
			companyId, className, tableName, columnName);

		if (expandoColumn == null) {
			StringBundler sb = new StringBundler(6);

			sb.append("No ExpandoColumn exists with the key {");

			sb.append("tableName=");
			sb.append(ExpandoTableConstants.DEFAULT_TABLE_NAME);

			sb.append(", name=");
			sb.append(columnName);

			sb.append("}");

			try {
				throw new NoSuchColumnException(sb.toString());
			}
			catch (NoSuchColumnException noSuchColumnException) {
				throw new RuntimeException(noSuchColumnException);
			}
		}

		return contains(permissionChecker, expandoColumn, actionId);
	}

}
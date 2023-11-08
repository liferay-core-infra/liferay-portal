/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutBranch;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.LayoutBranchLocalServiceUtil;

/**
 * @author Brian Wing Shun Chan
 * @author Julio Camarero
 */
public class LayoutBranchPermissionUtil {

	public static void check(
			PermissionChecker permissionChecker, LayoutBranch layoutBranch,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, layoutBranch, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, LayoutBranch.class.getName(),
				layoutBranch.getLayoutBranchId(), actionId);
		}
	}

	public static void check(
			PermissionChecker permissionChecker, long layoutBranchId,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, layoutBranchId, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, LayoutBranch.class.getName(), layoutBranchId,
				actionId);
		}
	}

	public static boolean contains(
		PermissionChecker permissionChecker, LayoutBranch layoutBranch,
		String actionId) {

		return permissionChecker.hasPermission(
			layoutBranch.getGroupId(), LayoutBranch.class.getName(),
			layoutBranch.getLayoutBranchId(), actionId);
	}

	public static boolean contains(
			PermissionChecker permissionChecker, long layoutBranchId,
			String actionId)
		throws PortalException {

		return contains(
			permissionChecker,
			LayoutBranchLocalServiceUtil.getLayoutBranch(layoutBranchId),
			actionId);
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutBranch;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

/**
 * @author Brian Wing Shun Chan
 * @author Julio Camarero
 */
public class LayoutBranchPermissionUtil {

	public static void check(
			PermissionChecker permissionChecker, LayoutBranch layoutBranch,
			String actionId)
		throws PortalException {

		LayoutBranchPermission layoutBranchPermission =
			_layoutBranchPermissionSnapshot.get();

		layoutBranchPermission.check(permissionChecker, layoutBranch, actionId);
	}

	public static void check(
			PermissionChecker permissionChecker, long layoutBranchId,
			String actionId)
		throws PortalException {

		LayoutBranchPermission layoutBranchPermission =
			_layoutBranchPermissionSnapshot.get();

		layoutBranchPermission.check(
			permissionChecker, layoutBranchId, actionId);
	}

	public static boolean contains(
		PermissionChecker permissionChecker, LayoutBranch layoutBranch,
		String actionId) {

		LayoutBranchPermission layoutBranchPermission =
			_layoutBranchPermissionSnapshot.get();

		return layoutBranchPermission.contains(
			permissionChecker, layoutBranch, actionId);
	}

	public static boolean contains(
			PermissionChecker permissionChecker, long layoutBranchId,
			String actionId)
		throws PortalException {

		LayoutBranchPermission layoutBranchPermission =
			_layoutBranchPermissionSnapshot.get();

		return layoutBranchPermission.contains(
			permissionChecker, layoutBranchId, actionId);
	}

	public static LayoutBranchPermission getLayoutBranchPermission() {
		return _layoutBranchPermissionSnapshot.get();
	}

	private static final Snapshot<LayoutBranchPermission>
		_layoutBranchPermissionSnapshot = new Snapshot<>(
			LayoutBranchPermissionUtil.class, LayoutBranchPermission.class);

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

/**
 * @author Brian Wing Shun Chan
 */
public class LayoutSetBranchPermissionUtil {

	public static void check(
			PermissionChecker permissionChecker,
			LayoutSetBranch layoutSetBranch, String actionId)
		throws PortalException {

		LayoutSetBranchPermission layoutSetBranchPermission =
			_layoutSetBranchPermissionSnapshot.get();

		layoutSetBranchPermission.check(
			permissionChecker, layoutSetBranch, actionId);
	}

	public static void check(
			PermissionChecker permissionChecker, long layoutSetBranchId,
			String actionId)
		throws PortalException {

		LayoutSetBranchPermission layoutSetBranchPermission =
			_layoutSetBranchPermissionSnapshot.get();

		layoutSetBranchPermission.check(
			permissionChecker, layoutSetBranchId, actionId);
	}

	public static boolean contains(
		PermissionChecker permissionChecker, LayoutSetBranch layoutSetBranch,
		String actionId) {

		LayoutSetBranchPermission layoutSetBranchPermission =
			_layoutSetBranchPermissionSnapshot.get();

		return layoutSetBranchPermission.contains(
			permissionChecker, layoutSetBranch, actionId);
	}

	public static boolean contains(
			PermissionChecker permissionChecker, long layoutSetBranchId,
			String actionId)
		throws PortalException {

		LayoutSetBranchPermission layoutSetBranchPermission =
			_layoutSetBranchPermissionSnapshot.get();

		return layoutSetBranchPermission.contains(
			permissionChecker, layoutSetBranchId, actionId);
	}

	public static LayoutSetBranchPermission getLayoutSetBranchPermission() {
		return _layoutSetBranchPermissionSnapshot.get();
	}

	private static final Snapshot<LayoutSetBranchPermission>
		_layoutSetBranchPermissionSnapshot = new Snapshot<>(
			LayoutSetBranchPermissionUtil.class,
			LayoutSetBranchPermission.class);

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.security.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutBranch;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.LayoutBranchLocalService;
import com.liferay.portal.kernel.service.permission.LayoutBranchPermission;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(service = LayoutBranchPermission.class)
public class LayoutBranchPermissionImpl implements LayoutBranchPermission {

	@Override
	public void check(
			PermissionChecker permissionChecker, LayoutBranch layoutBranch,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, layoutBranch, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, LayoutBranch.class.getName(),
				layoutBranch.getLayoutBranchId(), actionId);
		}
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, long layoutBranchId,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, layoutBranchId, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, LayoutBranch.class.getName(), layoutBranchId,
				actionId);
		}
	}

	@Override
	public boolean contains(
		PermissionChecker permissionChecker, LayoutBranch layoutBranch,
		String actionId) {

		return permissionChecker.hasPermission(
			layoutBranch.getGroupId(), LayoutBranch.class.getName(),
			layoutBranch.getLayoutBranchId(), actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, long layoutBranchId,
			String actionId)
		throws PortalException {

		return contains(
			permissionChecker,
			_layoutBranchLocalService.getLayoutBranch(layoutBranchId),
			actionId);
	}

	@Reference
	private LayoutBranchLocalService _layoutBranchLocalService;

}
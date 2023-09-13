/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.permission;

import com.liferay.petra.lang.HashUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.Map;
import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 */
public class GroupPermissionUtil_IW {
	public static GroupPermissionUtil_IW getInstance() {
		return _instance;
	}

	public static void check(
		PermissionChecker permissionChecker, Group group, String actionId)
		throws PortalException {

		GroupPermissionUtil.check(permissionChecker, group, actionId);
	}

	public static void check(
		PermissionChecker permissionChecker, long groupId, String actionId)
		throws PortalException {

		GroupPermissionUtil.check(permissionChecker, groupId, actionId);
	}

	public static void check(
		PermissionChecker permissionChecker, String actionId)
		throws PortalException {

		GroupPermissionUtil.check(permissionChecker, actionId);
	}

	public static boolean contains(
		PermissionChecker permissionChecker, Group group, String actionId)
		throws PortalException {

		return GroupPermissionUtil.contains(permissionChecker, group, actionId);
	}

	public static boolean contains(
		PermissionChecker permissionChecker, long groupId, String actionId)
		throws PortalException {

		return GroupPermissionUtil.contains(permissionChecker, groupId, actionId);
	}

	public static boolean contains(
		PermissionChecker permissionChecker, String actionId) {

		return GroupPermissionUtil.contains(permissionChecker, actionId);
	}




	private GroupPermissionUtil_IW() {
	}

	private static GroupPermissionUtil_IW _instance = new GroupPermissionUtil_IW();
}

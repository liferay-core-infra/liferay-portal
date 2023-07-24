/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;

/**
 * @author Roberto Díaz
 */
public class BaseModelPermissionCheckerUtil {

	public static boolean containsBaseModelPermission(
		ModelResourcePermission<?> modelResourcePermission,
		PermissionChecker permissionChecker, long groupId, long classPK,
		String actionId) {

		try {
			return ModelResourcePermissionUtil.contains(
				modelResourcePermission, permissionChecker, groupId, classPK,
				actionId);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseModelPermissionCheckerUtil.class);

}
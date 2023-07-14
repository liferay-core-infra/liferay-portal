/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.kernel.security.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;

/**
 * @author Roberto Díaz
 */
public class BaseModelPermissionCheckerUtil {

	public static Boolean containsBaseModelPermission(
		PermissionChecker permissionChecker, long groupId, String className,
		long classPK, String actionId) {

		ModelResourcePermission<?> modelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				className);

		if (modelResourcePermission != null) {
			try {
				PortletResourcePermission portletResourcePermission =
					modelResourcePermission.getPortletResourcePermission();

				if (portletResourcePermission == null) {
					return modelResourcePermission.contains(
						permissionChecker, classPK, actionId);
				}

				return ModelResourcePermissionUtil.contains(
					modelResourcePermission, permissionChecker, groupId,
					classPK, actionId);
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(portalException);
				}

				return false;
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseModelPermissionCheckerUtil.class);

}
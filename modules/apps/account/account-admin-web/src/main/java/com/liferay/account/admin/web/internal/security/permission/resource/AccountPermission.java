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

package com.liferay.account.admin.web.internal.security.permission.resource;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.OrganizationLocalService;

import java.util.List;

/**
 * @author Pei-Jung Lan
 */
public class AccountPermission {

	public static boolean contains(
		PermissionChecker permissionChecker, long groupId, String actionId) {

		return _portletResourcePermission.contains(
			permissionChecker, groupId, actionId);
	}

	public static boolean contains(
		PermissionChecker permissionChecker, String portletId,
		String actionId) {

		try {
			List<Organization> organizations =
				_organizationLocalService.getUserOrganizations(
					permissionChecker.getUserId(), true);

			for (Organization organization : organizations) {
				if (permissionChecker.hasPermission(
						organization.getGroupId(), portletId, 0, actionId)) {

					return true;
				}
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return false;
	}

	public static void setOrganizationLocalService(
		OrganizationLocalService organizationLocalService) {

		_organizationLocalService = organizationLocalService;
	}

	public static void setPortletResourcePermission(
		PortletResourcePermission portletResourcePermission) {

		_portletResourcePermission = portletResourcePermission;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AccountPermission.class);

	private static OrganizationLocalService _organizationLocalService;
	private static PortletResourcePermission _portletResourcePermission;

}
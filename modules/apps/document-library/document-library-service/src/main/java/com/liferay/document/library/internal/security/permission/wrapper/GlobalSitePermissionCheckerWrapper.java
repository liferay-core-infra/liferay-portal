/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.security.permission.wrapper;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.wrapper.PermissionCheckerWrapper;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portlet.documentlibrary.constants.DLConstants;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Tina Tian
 */
public class GlobalSitePermissionCheckerWrapper
	extends PermissionCheckerWrapper {

	public GlobalSitePermissionCheckerWrapper(
		PermissionChecker permissionChecker,
		GroupLocalService groupLocalService) {

		super(permissionChecker);

		_groupLocalService = groupLocalService;
	}

	@Override
	public boolean hasPermission(
		Group group, String name, long primKey, String actionId) {

		return _hasPermission(
			name, primKey, actionId,
			() -> super.hasPermission(group, name, primKey, actionId));
	}

	@Override
	public boolean hasPermission(
		Group group, String name, String primKey, String actionId) {

		return _hasPermission(
			name, GetterUtil.getLong(primKey), actionId,
			() -> super.hasPermission(group, name, primKey, actionId));
	}

	@Override
	public boolean hasPermission(
		long groupId, String name, long primKey, String actionId) {

		return _hasPermission(
			name, primKey, actionId,
			() -> super.hasPermission(groupId, name, primKey, actionId));
	}

	@Override
	public boolean hasPermission(
		long groupId, String name, String primKey, String actionId) {

		return _hasPermission(
			name, GetterUtil.getLong(primKey), actionId,
			() -> super.hasPermission(groupId, name, primKey, actionId));
	}

	private boolean _hasPermission(
		String name, long primKey, String actionId,
		Supplier<Boolean> hasPermissionSupplier) {

		if (!StringUtil.equals(name, DLConstants.RESOURCE_NAME) ||
			StringUtil.equals(actionId, ActionKeys.VIEW)) {

			return hasPermissionSupplier.get();
		}

		Group group = _groupLocalService.fetchGroup(primKey);

		if ((group != null) &&
			Objects.equals(
				group.getFriendlyURL(), GroupConstants.GLOBAL_FRIENDLY_URL)) {

			return isCompanyAdmin(group.getCompanyId());
		}

		return hasPermissionSupplier.get();
	}

	private final GroupLocalService _groupLocalService;

}
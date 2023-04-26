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

package com.liferay.message.boards.service.permission;

import com.liferay.message.boards.model.MBDiscussion;
import com.liferay.message.boards.service.MBDiscussionLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.BaseModelPermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Charles May
 * @author Roberto Díaz
 * @author Sergio González
 */
@Component(
	property = "model.class.name=com.liferay.message.boards.model.MBDiscussion",
	service = BaseModelPermissionChecker.class
)
public class MBDiscussionPermissionChecker
	implements BaseModelPermissionChecker {

	@Override
	public void checkBaseModel(
			PermissionChecker permissionChecker, long groupId, long primaryKey,
			String actionId)
		throws PortalException {

		MBDiscussion mbDiscussion = _mbDiscussionLocalService.getMBDiscussion(
			primaryKey);

		MBDiscussionPermission.check(
			permissionChecker, mbDiscussion.getCompanyId(), groupId,
			mbDiscussion.getClassName(), mbDiscussion.getClassPK(), actionId);
	}

	@Reference
	private MBDiscussionLocalService _mbDiscussionLocalService;

}
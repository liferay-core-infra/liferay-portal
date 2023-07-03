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

package com.liferay.message.boards.comment.internal;

import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.permission.MBDiscussionPermission;
import com.liferay.portal.kernel.comment.BaseDiscussionPermission;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 * @author Sergio González
 */
@Component(service = DiscussionPermission.class)
public class MBDiscussionPermissionImpl extends BaseDiscussionPermission {

	@Override
	public boolean hasAddPermission(
		long companyId, long groupId, String className, long classPK,
		PermissionChecker permissionChecker) {

		return hasPermission(
			ActionKeys.ADD_DISCUSSION, className, classPK, companyId, groupId,
			permissionChecker);
	}

	@Override
	public boolean hasPermission(
			Comment comment, String actionId,
			PermissionChecker permissionChecker)
		throws PortalException {

		if (comment instanceof MBCommentImpl) {
			MBCommentImpl mbCommentImpl = (MBCommentImpl)comment;

			MBMessage mbMessage = mbCommentImpl.getMessage();

			return MBDiscussionPermission.contains(
				permissionChecker, mbMessage, actionId);
		}

		return hasPermission(
			comment.getCommentId(), actionId, permissionChecker);
	}

	@Override
	public boolean hasPermission(
			long commentId, String actionId,
			PermissionChecker permissionChecker)
		throws PortalException {

		return MBDiscussionPermission.contains(
			permissionChecker, commentId, actionId);
	}

	@Override
	public boolean hasPermission(
		String actionId, String className, long classPK, long companyId,
		long groupId, PermissionChecker permissionChecker) {

		return MBDiscussionPermission.contains(
			permissionChecker, companyId, groupId, className, classPK,
			actionId);
	}

	@Override
	public boolean hasSubscribePermission(
			long companyId, long groupId, String className, long classPK,
			PermissionChecker permissionChecker)
		throws PortalException {

		return hasViewPermission(
			companyId, groupId, className, classPK, permissionChecker);
	}

	@Override
	public boolean hasViewPermission(
		long companyId, long groupId, String className, long classPK,
		PermissionChecker permissionChecker) {

		return hasPermission(
			ActionKeys.VIEW, className, classPK, companyId, groupId,
			permissionChecker);
	}

}
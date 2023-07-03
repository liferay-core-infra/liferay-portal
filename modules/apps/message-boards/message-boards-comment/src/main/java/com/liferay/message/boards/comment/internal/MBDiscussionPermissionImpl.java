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
import com.liferay.message.boards.service.MBBanLocalService;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.portal.kernel.comment.BaseDiscussionPermission;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.BaseModelPermissionCheckerUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.permission.WorkflowPermission;
import com.liferay.portal.util.PropsValues;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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

			return _contains(permissionChecker, mbMessage, actionId);
		}

		return hasPermission(
			comment.getCommentId(), actionId, permissionChecker);
	}

	@Override
	public boolean hasPermission(
			long commentId, String actionId,
			PermissionChecker permissionChecker)
		throws PortalException {

		return _contains(
			permissionChecker, _mbMessageLocalService.getMessage(commentId),
			actionId);
	}

	@Override
	public boolean hasPermission(
		String actionId, String className, long classPK, long companyId,
		long groupId, PermissionChecker permissionChecker) {

		if (_mbBanLocalService.hasBan(groupId, permissionChecker.getUserId())) {
			return false;
		}

		List<String> resourceActions = ResourceActionsUtil.getResourceActions(
			className);

		if (!resourceActions.contains(actionId)) {
			return true;
		}

		Boolean hasPermission =
			BaseModelPermissionCheckerUtil.containsBaseModelPermission(
				permissionChecker, groupId, className, classPK, actionId);

		if (hasPermission != null) {
			return hasPermission.booleanValue();
		}

		return permissionChecker.hasPermission(
			groupId, className, classPK, actionId);
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

	private boolean _contains(
			PermissionChecker permissionChecker, MBMessage message,
			String actionId)
		throws PortalException {

		String className = message.getClassName();

		if (className.equals(WorkflowInstance.class.getName())) {
			return permissionChecker.hasPermission(
				message.getGroupId(), PortletKeys.WORKFLOW_DEFINITION,
				message.getGroupId(), ActionKeys.VIEW);
		}

		if (PropsValues.DISCUSSION_COMMENTS_ALWAYS_EDITABLE_BY_OWNER &&
			(permissionChecker.getUserId() == message.getUserId())) {

			return true;
		}

		if (message.isPending()) {
			Boolean hasPermission = _workflowPermission.hasPermission(
				permissionChecker, message.getGroupId(),
				message.getWorkflowClassName(), message.getMessageId(),
				actionId);

			if (hasPermission != null) {
				return hasPermission.booleanValue();
			}
		}

		return hasPermission(
			actionId, className, message.getClassPK(), message.getCompanyId(),
			message.getGroupId(), permissionChecker);
	}

	@Reference
	private MBBanLocalService _mbBanLocalService;

	@Reference
	private MBMessageLocalService _mbMessageLocalService;

	@Reference
	private WorkflowPermission _workflowPermission;

}
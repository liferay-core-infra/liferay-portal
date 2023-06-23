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

package com.liferay.message.boards.comment.internal.security.permission.resource;

import com.liferay.message.boards.constants.MBConstants;
import com.liferay.message.boards.model.MBDiscussion;
import com.liferay.message.boards.service.MBDiscussionLocalService;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jiaxu Wei
 */
@Component(
	property = "model.class.name=com.liferay.message.boards.model.MBDiscussion",
	service = ModelResourcePermission.class
)
public class MBDiscussionModelResourcePermission
	implements ModelResourcePermission<MBDiscussion> {

	@Override
	public void check(
			PermissionChecker permissionChecker, long discussionId,
			String actionId)
		throws PortalException {

		check(
			permissionChecker,
			_mbDiscussionLocalService.getMBDiscussion(discussionId), actionId);
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, MBDiscussion mbDiscussion,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, mbDiscussion, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, MBDiscussion.class.getName(),
				mbDiscussion.getDiscussionId(), actionId);
		}
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, long discussionId,
			String actionId)
		throws PortalException {

		return contains(
			permissionChecker,
			_mbDiscussionLocalService.getMBDiscussion(discussionId), actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, MBDiscussion mbDiscussion,
			String actionId)
		throws PortalException {

		return _discussionPermission.hasPermission(
			permissionChecker, mbDiscussion.getCompanyId(),
			mbDiscussion.getGroupId(), mbDiscussion.getClassName(),
			mbDiscussion.getClassPK(), actionId);
	}

	@Override
	public String getModelName() {
		return MBDiscussion.class.getName();
	}

	@Override
	public PortletResourcePermission getPortletResourcePermission() {
		return _portletResourcePermission;
	}

	@Reference
	private DiscussionPermission _discussionPermission;

	@Reference
	private MBDiscussionLocalService _mbDiscussionLocalService;

	@Reference(target = "(resource.name=" + MBConstants.RESOURCE_NAME + ")")
	private PortletResourcePermission _portletResourcePermission;

}
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

package com.liferay.portal.security.permission.resource;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.permission.GroupPermission;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;

/**
 * @author Jiaxu Wei
 */
@OSGiBeanProperties(
	property = "model.class.name=com.liferay.portal.kernel.model.Group"
)
public class GroupModelResourcePermission
	implements ModelResourcePermission<Group> {

	@Override
	public void check(
			PermissionChecker permissionChecker, Group group, String actionId)
		throws PortalException {

		groupPermission.check(permissionChecker, group, actionId);
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, long groupId, String actionId)
		throws PortalException {

		groupPermission.check(permissionChecker, groupId, actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, Group group, String actionId)
		throws PortalException {

		return groupPermission.contains(permissionChecker, group, actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, long groupId, String actionId)
		throws PortalException {

		return groupPermission.contains(permissionChecker, groupId, actionId);
	}

	@Override
	public String getModelName() {
		return Group.class.getName();
	}

	@Override
	public PortletResourcePermission getPortletResourcePermission() {
		return null;
	}

	@BeanReference(type = GroupPermission.class)
	protected GroupPermission groupPermission;

}
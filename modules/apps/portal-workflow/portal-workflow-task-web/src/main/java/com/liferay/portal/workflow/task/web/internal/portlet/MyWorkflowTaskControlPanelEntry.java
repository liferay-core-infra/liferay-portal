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

package com.liferay.portal.workflow.task.web.internal.portlet;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.workflow.manager.WorkflowTaskManager;
import com.liferay.portal.workflow.WorkflowControlPanelEntry;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(
	property = "javax.portlet.name=" + PortletKeys.MY_WORKFLOW_TASK,
	service = ControlPanelEntry.class
)
public class MyWorkflowTaskControlPanelEntry extends WorkflowControlPanelEntry {

	@Override
	protected boolean hasPermissionImplicitlyGranted(
			PermissionChecker permissionChecker, Group group, Portlet portlet)
		throws Exception {

		int count = _workflowTaskManager.getWorkflowTaskCountByUser(
			permissionChecker.getCompanyId(), permissionChecker.getUserId(),
			null);

		if (count > 0) {
			return true;
		}

		count = _workflowTaskManager.getWorkflowTaskCountByUserRoles(
			permissionChecker.getCompanyId(), permissionChecker.getUserId(),
			null);

		if (count > 0) {
			return true;
		}

		return super.hasPermissionImplicitlyGranted(
			permissionChecker, group, portlet);
	}

	@Reference
	private WorkflowTaskManager _workflowTaskManager;

}
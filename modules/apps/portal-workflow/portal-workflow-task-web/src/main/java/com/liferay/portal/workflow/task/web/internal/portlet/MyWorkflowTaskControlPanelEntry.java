/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.task.web.internal.portlet;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
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
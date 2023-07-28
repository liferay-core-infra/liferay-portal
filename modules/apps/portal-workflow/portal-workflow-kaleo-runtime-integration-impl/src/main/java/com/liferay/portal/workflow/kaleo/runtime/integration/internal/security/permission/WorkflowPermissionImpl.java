/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.integration.internal.security.permission;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.kernel.workflow.permission.WorkflowPermission;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(service = WorkflowPermission.class)
public class WorkflowPermissionImpl implements WorkflowPermission {

	@Override
	public Boolean hasPermission(
		PermissionChecker permissionChecker, long groupId, String className,
		long classPK, String actionId) {

		try {
			return doHasPermission(
				permissionChecker, groupId, className, classPK, actionId);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return null;
	}

	protected Boolean doHasPermission(
			PermissionChecker permissionChecker, long groupId, String className,
			long classPK, String actionId)
		throws Exception {

		long companyId = permissionChecker.getCompanyId();

		if (permissionChecker.isContentReviewer(companyId, groupId)) {
			return Boolean.TRUE;
		}

		if (_workflowInstanceLinkLocalService.hasWorkflowInstanceLink(
				companyId, groupId, className, classPK)) {

			WorkflowInstanceLink workflowInstanceLink =
				_workflowInstanceLinkLocalService.getWorkflowInstanceLink(
					companyId, groupId, className, classPK);

			if (Objects.equals(actionId, ActionKeys.VIEW) &&
				(permissionChecker.getUserId() ==
					workflowInstanceLink.getUserId())) {

				return Boolean.TRUE;
			}

			WorkflowInstance workflowInstance =
				_workflowInstanceManager.getWorkflowInstance(
					companyId, workflowInstanceLink.getWorkflowInstanceId());

			if (workflowInstance.isComplete()) {
				return null;
			}

			boolean hasPermission = hasImplicitPermission(
				permissionChecker, workflowInstance);

			if (!hasPermission && actionId.equals(ActionKeys.VIEW)) {
				return null;
			}

			return hasPermission;
		}

		return null;
	}

	protected boolean hasImplicitPermission(
			PermissionChecker permissionChecker,
			WorkflowInstance workflowInstance)
		throws WorkflowException {

		int count = _workflowTaskManager.getWorkflowTaskCountByWorkflowInstance(
			permissionChecker.getCompanyId(), permissionChecker.getUserId(),
			workflowInstance.getWorkflowInstanceId(), Boolean.FALSE);

		if (count > 0) {
			return true;
		}

		count = _workflowTaskManager.getWorkflowTaskCountByUserRoles(
			permissionChecker.getCompanyId(), permissionChecker.getUserId(),
			workflowInstance.getWorkflowInstanceId(), Boolean.FALSE);

		if (count > 0) {
			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowPermissionImpl.class);

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

	@Reference
	private WorkflowInstanceManager _workflowInstanceManager;

	@Reference
	private WorkflowTaskManager _workflowTaskManager;

}
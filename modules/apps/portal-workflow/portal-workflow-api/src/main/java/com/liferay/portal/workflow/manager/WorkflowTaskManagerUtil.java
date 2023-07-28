/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.manager;

import com.liferay.osgi.util.service.Snapshot;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Micha Kiener
 * @author Shuyang Zhou
 * @author Brian Wing Shun Chan
 * @author Marcellus Tavares
 * @author Raymond Augé
 */
public class WorkflowTaskManagerUtil {

	public static WorkflowTask assignWorkflowTaskToUser(
			long companyId, long userId, long workflowTaskId,
			long assigneeUserId, String comment, Date dueDate,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.assignWorkflowTaskToUser(
			companyId, userId, workflowTaskId, assigneeUserId, comment, dueDate,
			workflowContext);
	}

	public static WorkflowTask getWorkflowTask(long workflowTaskId)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.getWorkflowTask(workflowTaskId);
	}

	public static List<WorkflowTask> getWorkflowTasksByUser(
			long companyId, long userId, Boolean completed, int start, int end,
			OrderByComparator<WorkflowTask> orderByComparator)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.getWorkflowTasksByUser(
			companyId, userId, completed, start, end, orderByComparator);
	}

	public static List<WorkflowTask> getWorkflowTasksByUserRoles(
			long companyId, long userId, Boolean completed, int start, int end,
			OrderByComparator<WorkflowTask> orderByComparator)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.getWorkflowTasksByUserRoles(
			companyId, userId, completed, start, end, orderByComparator);
	}

	private static final Snapshot<WorkflowTaskManager>
		_workflowTaskManagerSnapshot = new Snapshot<>(
			WorkflowTaskManagerUtil.class, WorkflowTaskManager.class);

}
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

package com.liferay.portal.workflow.kaleo.runtime.internal.node;

import com.liferay.osgi.util.service.Snapshot;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.workflow.kaleo.definition.ExecutionType;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignment;
import com.liferay.portal.workflow.kaleo.model.KaleoTimer;
import com.liferay.portal.workflow.kaleo.model.KaleoTimerInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.action.KaleoActionExecutor;
import com.liferay.portal.workflow.kaleo.runtime.internal.assignment.helper.TaskAssignerHelper;
import com.liferay.portal.workflow.kaleo.runtime.notification.NotificationHelper;
import com.liferay.portal.workflow.kaleo.service.KaleoTimerInstanceTokenLocalService;

import java.util.List;

/**
 * @author Joao Victor Alves
 */
public class TaskNodeExecutorUtil {

	public static void executeTimer(ExecutionContext executionContext)
		throws PortalException {

		KaleoTimerInstanceToken kaleoTimerInstanceToken =
			executionContext.getKaleoTimerInstanceToken();

		KaleoTimer kaleoTimer = kaleoTimerInstanceToken.getKaleoTimer();

		KaleoActionExecutor kaleoActionExecutor =
			_kaleoActionExecutorSnapshot.get();

		kaleoActionExecutor.executeKaleoActions(
			KaleoTimer.class.getName(), kaleoTimer.getKaleoTimerId(),
			ExecutionType.ON_TIMER, executionContext);

		List<KaleoTaskAssignment> kaleoTaskReassignments =
			kaleoTimer.getKaleoTaskReassignments();

		if (ListUtil.isNotEmpty(kaleoTaskReassignments)) {
			TaskAssignerHelper taskAssignerHelper =
				_taskAssignerHelperSnapshot.get();

			taskAssignerHelper.reassignKaleoTask(
				kaleoTaskReassignments, executionContext);
		}

		NotificationHelper notificationHelper =
			_notificationHelperSnapshot.get();

		notificationHelper.sendKaleoNotifications(
			KaleoTimer.class.getName(), kaleoTimer.getKaleoTimerId(),
			ExecutionType.ON_TIMER, executionContext);

		if (!kaleoTimer.isRecurring()) {
			KaleoTimerInstanceTokenLocalService
				kaleoTimerInstanceTokenLocalService =
					_kaleoTimerInstanceTokenLocalServiceSnapshot.get();

			kaleoTimerInstanceTokenLocalService.completeKaleoTimerInstanceToken(
				kaleoTimerInstanceToken.getKaleoTimerInstanceTokenId(),
				executionContext.getServiceContext());
		}
	}

	private static final Snapshot<KaleoActionExecutor>
		_kaleoActionExecutorSnapshot = new Snapshot<>(
			TaskNodeExecutorUtil.class, KaleoActionExecutor.class);
	private static final Snapshot<KaleoTimerInstanceTokenLocalService>
		_kaleoTimerInstanceTokenLocalServiceSnapshot = new Snapshot<>(
			TaskNodeExecutorUtil.class,
			KaleoTimerInstanceTokenLocalService.class);
	private static final Snapshot<NotificationHelper>
		_notificationHelperSnapshot = new Snapshot<>(
			TaskNodeExecutorUtil.class, NotificationHelper.class);
	private static final Snapshot<TaskAssignerHelper>
		_taskAssignerHelperSnapshot = new Snapshot<>(
			TaskNodeExecutorUtil.class, TaskAssignerHelper.class);

}
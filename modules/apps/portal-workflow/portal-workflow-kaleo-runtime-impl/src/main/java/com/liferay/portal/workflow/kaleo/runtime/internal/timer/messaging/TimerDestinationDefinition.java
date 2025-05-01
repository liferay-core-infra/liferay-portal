/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.internal.timer.messaging;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationDefinition;
import com.liferay.portal.workflow.kaleo.runtime.constants.KaleoRuntimeDestinationNames;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.osgi.service.component.annotations.Component;

/**
 * @author Dante Wang
 */
@Component(
	property = "destination.name=" + KaleoRuntimeDestinationNames.WORKFLOW_TIMER,
	service = DestinationDefinition.class
)
public class TimerDestinationDefinition implements DestinationDefinition {

	@Override
	public String getDestinationName() {
		return KaleoRuntimeDestinationNames.WORKFLOW_TIMER;
	}

	@Override
	public String getDestinationType() {
		return DESTINATION_TYPE_PARALLEL;
	}

	@Override
	public int getMaximumQueueSize() {
		return _MAXIMUM_QUEUE_SIZE;
	}

	@Override
	public RejectedExecutionHandler getRejectedExecutionHandler() {
		return new ThreadPoolExecutor.CallerRunsPolicy() {

			@Override
			public void rejectedExecution(
				Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {

				if (_log.isWarnEnabled()) {
					_log.warn(
						"The current thread will handle the request because " +
							"the workflow timer task queue is at its maximum " +
								"capacity");
				}

				super.rejectedExecution(runnable, threadPoolExecutor);
			}

		};
	}

	private static final int _MAXIMUM_QUEUE_SIZE = 200;

	private static final Log _log = LogFactoryUtil.getLog(
		TimerDestinationDefinition.class);

}
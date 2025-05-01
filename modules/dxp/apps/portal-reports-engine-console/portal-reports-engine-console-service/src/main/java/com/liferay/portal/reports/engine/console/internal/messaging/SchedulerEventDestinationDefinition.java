/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.internal.messaging;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationDefinition;
import com.liferay.portal.reports.engine.console.configuration.ReportsPortletMessagingConfiguration;
import com.liferay.portal.reports.engine.console.internal.constants.ReportsEngineDestinationNames;

import java.util.Map;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Dante Wang
 */
@Component(
	configurationPid = "com.liferay.portal.reports.engine.console.configuration.ReportsPortletMessagingConfiguration",
	property = "destination.name=" + ReportsEngineDestinationNames.REPORTS_SCHEDULER_EVENT,
	service = DestinationDefinition.class
)
public class SchedulerEventDestinationDefinition
	implements DestinationDefinition {

	@Override
	public String getDestinationName() {
		return ReportsEngineDestinationNames.REPORTS_SCHEDULER_EVENT;
	}

	@Override
	public String getDestinationType() {
		return DESTINATION_TYPE_PARALLEL;
	}

	@Override
	public int getMaximumQueueSize() {
		return _reportsPortletMessagingConfiguration.reportMessageQueueSize();
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
							"the report console's task queue is at its " +
								"maximum capacity");
				}

				super.rejectedExecution(runnable, threadPoolExecutor);
			}

		};
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_reportsPortletMessagingConfiguration =
			ConfigurableUtil.createConfigurable(
				ReportsPortletMessagingConfiguration.class, properties);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SchedulerEventDestinationDefinition.class);

	private ReportsPortletMessagingConfiguration
		_reportsPortletMessagingConfiguration;

}
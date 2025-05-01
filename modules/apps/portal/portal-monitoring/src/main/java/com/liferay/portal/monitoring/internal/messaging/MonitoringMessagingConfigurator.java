/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.monitoring.internal.messaging;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationDefinition;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.monitoring.internal.configuration.MonitoringConfiguration;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(
	configurationPid = "com.liferay.portal.monitoring.internal.configuration.MonitoringConfiguration",
	enabled = false,
	property = "destination.name=" + DestinationNames.MONITORING,
	service = DestinationDefinition.class
)
public class MonitoringMessagingConfigurator implements DestinationDefinition {

	@Override
	public String getDestinationName() {
		return DestinationNames.MONITORING;
	}

	@Override
	public String getDestinationType() {
		return DESTINATION_TYPE_PARALLEL;
	}

	@Override
	public int getMaximumQueueSize() {
		return _monitoringConfiguration.monitoringMessageMaxQueueSize();
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
							"the monitoring destination's task queue is at " +
								"its maximum capacity");
				}

				super.rejectedExecution(runnable, threadPoolExecutor);
			}

		};
	}

	@Activate
	protected void activate(ComponentContext componentContext) {
		_monitoringConfiguration = ConfigurableUtil.createConfigurable(
			MonitoringConfiguration.class, componentContext.getProperties());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MonitoringMessagingConfigurator.class);

	private MonitoringConfiguration _monitoringConfiguration;

}
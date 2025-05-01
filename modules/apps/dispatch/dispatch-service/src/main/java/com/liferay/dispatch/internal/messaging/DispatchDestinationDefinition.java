/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.internal.messaging;

import com.liferay.dispatch.constants.DispatchConstants;
import com.liferay.dispatch.exception.DispatchTriggerSchedulerException;
import com.liferay.dispatch.executor.DispatchTaskClusterMode;
import com.liferay.dispatch.internal.helper.DispatchTriggerHelper;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.portal.kernel.cluster.BaseClusterMasterTokenTransitionListener;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutor;
import com.liferay.portal.kernel.cluster.ClusterMasterTokenTransitionListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationDefinition;
import com.liferay.portal.kernel.messaging.DestinationFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matija Petanjek
 */
@Component(
	property = "destination.name=" + DispatchConstants.EXECUTOR_DESTINATION_NAME,
	service = DestinationDefinition.class
)
public class DispatchDestinationDefinition implements DestinationDefinition {

	@Override
	public String getDestinationName() {
		return DispatchConstants.EXECUTOR_DESTINATION_NAME;
	}

	@Override
	public String getDestinationType() {
		return DestinationConfiguration.DESTINATION_TYPE_PARALLEL;
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
							"the graph walker's task queue is at its maximum " +
								"capacity");
				}

				super.rejectedExecution(runnable, threadPoolExecutor);
			}

		};
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		if (_clusterMasterExecutor.isEnabled()) {
			_dispatchClusterMasterTokenTransitionListener =
				new DispatchClusterMasterTokenTransitionListener();

			_clusterMasterExecutor.addClusterMasterTokenTransitionListener(
				_dispatchClusterMasterTokenTransitionListener);
		}

		_addScheduledJobs();
	}

	@Deactivate
	protected void deactivate() {
		_deleteScheduledJobs();

		if (_clusterMasterExecutor.isEnabled()) {
			_clusterMasterExecutor.removeClusterMasterTokenTransitionListener(
				_dispatchClusterMasterTokenTransitionListener);
		}
	}

	private void _addScheduledJobs() {
		for (DispatchTrigger dispatchTrigger :
				_dispatchTriggerLocalService.getDispatchTriggers(true)) {

			DispatchTaskClusterMode dispatchTaskClusterMode =
				DispatchTaskClusterMode.valueOf(
					dispatchTrigger.getDispatchTaskClusterMode());

			if (!_isSchedulable(dispatchTaskClusterMode)) {
				continue;
			}

			try {
				_dispatchTriggerHelper.addSchedulerJob(
					dispatchTrigger, dispatchTaskClusterMode.getStorageType(),
					dispatchTrigger.getTimeZoneId());
			}
			catch (DispatchTriggerSchedulerException
						dispatchTriggerSchedulerException) {

				_log.error(dispatchTriggerSchedulerException);
			}
		}
	}

	private void _deleteScheduledJobs() {
		for (DispatchTrigger dispatchTrigger :
				_dispatchTriggerLocalService.getDispatchTriggers(true)) {

			DispatchTaskClusterMode dispatchTaskClusterMode =
				DispatchTaskClusterMode.valueOf(
					dispatchTrigger.getDispatchTaskClusterMode());

			if (!_isSchedulable(dispatchTaskClusterMode)) {
				continue;
			}

			_dispatchTriggerHelper.deleteSchedulerJob(
				dispatchTrigger, dispatchTaskClusterMode.getStorageType());
		}
	}

	private boolean _isSchedulable(
		DispatchTaskClusterMode dispatchTaskClusterMode) {

		if ((dispatchTaskClusterMode == DispatchTaskClusterMode.ALL_NODES) ||
			(_clusterMasterExecutor.isMaster() &&
			 ((dispatchTaskClusterMode ==
				 DispatchTaskClusterMode.SINGLE_NODE_MEMORY_CLUSTERED) ||
			  (dispatchTaskClusterMode ==
				  DispatchTaskClusterMode.SINGLE_NODE_PERSISTED)))) {

			return true;
		}

		return false;
	}

	private static final int _MAXIMUM_QUEUE_SIZE = 100;

	private static final Log _log = LogFactoryUtil.getLog(
		DispatchDestinationDefinition.class);

	@Reference
	private ClusterMasterExecutor _clusterMasterExecutor;

	@Reference
	private DestinationFactory _destinationFactory;

	private ClusterMasterTokenTransitionListener
		_dispatchClusterMasterTokenTransitionListener;

	@Reference
	private DispatchTriggerHelper _dispatchTriggerHelper;

	@Reference
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	private class DispatchClusterMasterTokenTransitionListener
		extends BaseClusterMasterTokenTransitionListener {

		@Override
		protected void doMasterTokenAcquired() throws Exception {
			_addScheduledJobs();
		}

		@Override
		protected void doMasterTokenReleased() throws Exception {
			_addScheduledJobs();
		}

	}

}
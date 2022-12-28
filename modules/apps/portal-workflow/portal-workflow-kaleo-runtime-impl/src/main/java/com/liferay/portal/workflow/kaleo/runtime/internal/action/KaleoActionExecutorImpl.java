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

package com.liferay.portal.workflow.kaleo.runtime.internal.action;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.workflow.kaleo.definition.ExecutionType;
import com.liferay.portal.workflow.kaleo.model.KaleoAction;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.action.KaleoActionExecutor;
import com.liferay.portal.workflow.kaleo.runtime.action.executor.ActionExecutor;
import com.liferay.portal.workflow.kaleo.service.KaleoActionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoLogLocalService;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = KaleoActionExecutor.class)
public class KaleoActionExecutorImpl implements KaleoActionExecutor {

	@Override
	public void executeKaleoActions(
			String kaleoClassName, long kaleoClassPK,
			ExecutionType executionType, ExecutionContext executionContext)
		throws PortalException {

		ServiceContext serviceContext = executionContext.getServiceContext();

		List<KaleoAction> kaleoActions =
			_kaleoActionLocalService.getKaleoActions(
				serviceContext.getCompanyId(), kaleoClassName, kaleoClassPK,
				executionType.getValue());

		for (KaleoAction kaleoAction : kaleoActions) {
			long startTime = System.currentTimeMillis();

			String comment = _COMMENT_ACTION_SUCCESS;

			try {
				_executeKaleoAction(kaleoAction, executionContext);

				KaleoInstanceToken kaleoInstanceToken =
					executionContext.getKaleoInstanceToken();

				_kaleoInstanceLocalService.updateKaleoInstance(
					kaleoInstanceToken.getKaleoInstanceId(),
					executionContext.getWorkflowContext(), serviceContext);
			}
			catch (Exception exception) {
				_log.error(exception);

				comment = exception.getMessage();
			}
			finally {
				_kaleoLogLocalService.addActionExecutionKaleoLog(
					executionContext.getKaleoInstanceToken(), kaleoAction,
					startTime, System.currentTimeMillis(), comment,
					serviceContext);
			}
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ActionExecutor.class, "component.name");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private void _executeKaleoAction(
			KaleoAction kaleoAction, ExecutionContext executionContext)
		throws PortalException {

		ActionExecutor actionExecutor = _serviceTrackerMap.getService(
			StringUtil.trim(kaleoAction.getScript()));

		if ((actionExecutor == null) ||
			!actionExecutor.canEvaluate(kaleoAction.getScriptLanguage())) {

			throw new PortalException(
				"No action executor for " + kaleoAction.getScriptLanguage());
		}

		actionExecutor.execute(kaleoAction, executionContext);
	}

	private static final String _COMMENT_ACTION_SUCCESS =
		"Action completed successfully.";

	private static final Log _log = LogFactoryUtil.getLog(
		KaleoActionExecutorImpl.class);

	@Reference
	private KaleoActionLocalService _kaleoActionLocalService;

	@Reference
	private KaleoInstanceLocalService _kaleoInstanceLocalService;

	@Reference
	private KaleoLogLocalService _kaleoLogLocalService;

	private ServiceTrackerMap<String, ActionExecutor> _serviceTrackerMap;

}
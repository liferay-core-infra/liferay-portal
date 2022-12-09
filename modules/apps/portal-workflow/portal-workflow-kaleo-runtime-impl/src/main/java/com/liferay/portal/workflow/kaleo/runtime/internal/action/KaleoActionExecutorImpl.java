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

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.workflow.kaleo.definition.ExecutionType;
import com.liferay.portal.workflow.kaleo.definition.ScriptLanguage;
import com.liferay.portal.workflow.kaleo.definition.exception.KaleoDefinitionValidationException;
import com.liferay.portal.workflow.kaleo.model.KaleoAction;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.action.KaleoActionExecutor;
import com.liferay.portal.workflow.kaleo.runtime.action.executor.ActionExecutor;
import com.liferay.portal.workflow.kaleo.service.KaleoActionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoLogLocalService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

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
		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext, ActionExecutor.class,
			new ActionExecutorServiceTrackerCustomizer(bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private void _executeKaleoAction(
			KaleoAction kaleoAction, ExecutionContext executionContext)
		throws PortalException {

		String actionExecutorKey = _getActionExecutorKey(
			kaleoAction.getScriptLanguage(),
			StringUtil.trim(kaleoAction.getScript()));

		ActionExecutor actionExecutor = _actionExecutors.get(actionExecutorKey);

		if (actionExecutor == null) {
			throw new PortalException(
				"No action executor for " + actionExecutorKey);
		}

		actionExecutor.execute(kaleoAction, executionContext);
	}

	private String _getActionExecutorKey(
			String language, String actionExecutorClassName)
		throws KaleoDefinitionValidationException {

		ScriptLanguage scriptLanguage = ScriptLanguage.parse(language);

		if (scriptLanguage.equals(ScriptLanguage.JAVA)) {
			return language + StringPool.COLON + actionExecutorClassName;
		}

		return language;
	}

	private static final String _COMMENT_ACTION_SUCCESS =
		"Action completed successfully.";

	private static final Log _log = LogFactoryUtil.getLog(
		KaleoActionExecutorImpl.class);

	private final Map<String, ActionExecutor> _actionExecutors =
		new ConcurrentHashMap<>();

	@Reference
	private KaleoActionLocalService _kaleoActionLocalService;

	@Reference
	private KaleoInstanceLocalService _kaleoInstanceLocalService;

	@Reference
	private KaleoLogLocalService _kaleoLogLocalService;

	private ServiceTracker<ActionExecutor, ActionExecutor> _serviceTracker;

	private class ActionExecutorServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<ActionExecutor, ActionExecutor> {

		public ActionExecutorServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		@Override
		public ActionExecutor addingService(
			ServiceReference<ActionExecutor> serviceReference) {

			ActionExecutor actionExecutor = _bundleContext.getService(
				serviceReference);

			Object value = serviceReference.getProperty(
				"com.liferay.portal.workflow.kaleo.runtime.action.executor." +
					"language");

			String[] languages = GetterUtil.getStringValues(
				value, new String[] {String.valueOf(value)});

			try {
				for (String language : languages) {
					_actionExecutors.put(
						_getActionExecutorKey(
							language, ClassUtil.getClassName(actionExecutor)),
						actionExecutor);
				}
			}
			catch (KaleoDefinitionValidationException
						kaleoDefinitionValidationException) {

				throw new RuntimeException(kaleoDefinitionValidationException);
			}

			return actionExecutor;
		}

		@Override
		public void modifiedService(
			ServiceReference<ActionExecutor> serviceReference,
			ActionExecutor actionExecutor) {
		}

		@Override
		public void removedService(
			ServiceReference<ActionExecutor> serviceReference,
			ActionExecutor actionExecutor) {

			_bundleContext.ungetService(serviceReference);

			Object value = serviceReference.getProperty(
				"com.liferay.portal.workflow.kaleo.runtime.action.executor." +
					"language");

			String[] languages = GetterUtil.getStringValues(
				value, new String[] {String.valueOf(value)});

			try {
				for (String language : languages) {
					_actionExecutors.remove(
						_getActionExecutorKey(
							language, ClassUtil.getClassName(actionExecutor)));
				}
			}
			catch (KaleoDefinitionValidationException
						kaleoDefinitionValidationException) {

				throw new RuntimeException(kaleoDefinitionValidationException);
			}
		}

		private final BundleContext _bundleContext;

	}

}
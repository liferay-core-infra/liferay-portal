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

package com.liferay.portal.workflow.kaleo.runtime.internal.action.executor;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.workflow.kaleo.definition.ScriptLanguage;
import com.liferay.portal.workflow.kaleo.definition.exception.KaleoDefinitionValidationException;
import com.liferay.portal.workflow.kaleo.model.KaleoAction;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.action.ActionExecutorManager;
import com.liferay.portal.workflow.kaleo.runtime.action.executor.ActionExecutor;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Leonardo Barros
 */
@Component(service = ActionExecutorManager.class)
public class ActionExecutorManagerImpl implements ActionExecutorManager {

	@Override
	public void executeKaleoAction(
			KaleoAction kaleoAction, ExecutionContext executionContext)
		throws PortalException {

		String actionExecutorKey = _getActionExecutorKey(
			kaleoAction.getScriptLanguage(),
			StringUtil.trim(kaleoAction.getScript()));

		ActionExecutor actionExecutor = _serviceTrackerMap.getService(
			actionExecutorKey);

		if (actionExecutor == null) {
			throw new PortalException(
				"No action executor for " + actionExecutorKey);
		}

		actionExecutor.execute(kaleoAction, executionContext);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ActionExecutor.class, null,
			(serviceReference, emitter) -> {
				try {
					ActionExecutor actionExecutor = bundleContext.getService(
						serviceReference);

					Object value = serviceReference.getProperty(
						"com.liferay.portal.workflow.kaleo.runtime.action." +
							"executor.language");

					for (String language :
							GetterUtil.getStringValues(
								value, new String[] {String.valueOf(value)})) {

						emitter.emit(
							_getActionExecutorKey(
								language,
								ClassUtil.getClassName(actionExecutor)));
					}
				}
				catch (KaleoDefinitionValidationException
							kaleoDefinitionValidationException) {

					throw new RuntimeException(
						kaleoDefinitionValidationException);
				}
				finally {
					bundleContext.ungetService(serviceReference);
				}
			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
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

	private ServiceTrackerMap<String, ActionExecutor> _serviceTrackerMap;

}
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

package com.liferay.portal.workflow.kaleo.runtime.internal.assignment;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.workflow.kaleo.definition.ScriptLanguage;
import com.liferay.portal.workflow.kaleo.definition.exception.KaleoDefinitionValidationException;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignment;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.assignment.BaseKaleoTaskAssignmentSelector;
import com.liferay.portal.workflow.kaleo.runtime.assignment.KaleoTaskAssignmentSelector;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalService;

import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = "assignee.class.name=SCRIPT",
	service = KaleoTaskAssignmentSelector.class
)
public class MultiLanguageKaleoTaskAssignmentSelector
	extends BaseKaleoTaskAssignmentSelector {

	@Override
	public Collection<KaleoTaskAssignment> getKaleoTaskAssignments(
			KaleoTaskAssignment kaleoTaskAssignment,
			ExecutionContext executionContext)
		throws PortalException {

		KaleoTaskAssignmentSelector kaleoTaskAssignmentSelector =
			_serviceTrackerMap.getService(
				_getKaleoTaskAssignmentSelectKey(
					kaleoTaskAssignment.getAssigneeScriptLanguage(),
					StringUtil.trim(kaleoTaskAssignment.getAssigneeScript())));

		if (kaleoTaskAssignmentSelector == null) {
			throw new IllegalArgumentException(
				"No task assignment selector found for " +
					kaleoTaskAssignment.toString());
		}

		Collection<KaleoTaskAssignment> kaleoTaskAssignments =
			kaleoTaskAssignmentSelector.getKaleoTaskAssignments(
				kaleoTaskAssignment, executionContext);

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		_kaleoInstanceLocalService.updateKaleoInstance(
			kaleoInstanceToken.getKaleoInstanceId(),
			executionContext.getWorkflowContext(),
			executionContext.getServiceContext());

		return kaleoTaskAssignments;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, KaleoTaskAssignmentSelector.class,
			"(scripting.language=*)",
			(serviceReference, emitter) -> {
				Object propertyValue = serviceReference.getProperty(
					"scripting.language");

				KaleoTaskAssignmentSelector kaleoTaskAssignmentSelector =
					bundleContext.getService(serviceReference);

				try {
					for (String scriptingLanguage :
							GetterUtil.getStringValues(
								propertyValue,
								new String[] {String.valueOf(propertyValue)})) {

						emitter.emit(
							_getKaleoTaskAssignmentSelectKey(
								scriptingLanguage,
								ClassUtil.getClassName(
									kaleoTaskAssignmentSelector)));
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

	private String _getKaleoTaskAssignmentSelectKey(
			String language, String kaleoTaskAssignmentSelectorClassName)
		throws KaleoDefinitionValidationException {

		ScriptLanguage scriptLanguage = ScriptLanguage.parse(language);

		if (scriptLanguage.equals(ScriptLanguage.JAVA)) {
			return language + StringPool.COLON +
				kaleoTaskAssignmentSelectorClassName;
		}

		return language;
	}

	@Reference
	private KaleoInstanceLocalService _kaleoInstanceLocalService;

	private ServiceTrackerMap<String, KaleoTaskAssignmentSelector>
		_serviceTrackerMap;

}
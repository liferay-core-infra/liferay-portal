/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.scripting.internal.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.scripting.Scripting;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.util.ScriptingContextBuilder;
import com.liferay.portal.workflow.kaleo.runtime.util.WorkflowContextUtil;

import java.io.Serializable;

import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
public abstract class BaseKaleoScriptingEvaluator {

	public Map<String, Object> execute(
			ExecutionContext executionContext, Set<String> outputObjects,
			String scriptLanguage, String script)
		throws PortalException {

		Map<String, Object> inputObjects =
			scriptingContextBuilder.buildScriptingContext(executionContext);

		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		Map<String, Object> results = null;

		try {
			currentThread.setContextClassLoader(classLoader);

			results = scripting.eval(
				null, inputObjects, outputObjects, scriptLanguage, script);
		}
		finally {
			currentThread.setContextClassLoader(contextClassLoader);
		}

		Map<String, Serializable> resultsWorkflowContext =
			(Map<String, Serializable>)results.get(
				WorkflowContextUtil.WORKFLOW_CONTEXT_NAME);

		WorkflowContextUtil.mergeWorkflowContexts(
			executionContext, resultsWorkflowContext);

		return results;
	}

	@Reference
	protected Scripting scripting;

	@Reference
	protected ScriptingContextBuilder scriptingContextBuilder;

}
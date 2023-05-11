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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.workflow.kaleo.KaleoTaskAssignmentFactory;
import com.liferay.portal.workflow.kaleo.definition.exception.KaleoDefinitionValidationException;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignment;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentImpl;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.assignment.ScriptingKaleoTaskAssignmentSelector;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalService;

import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Jiaxu Wei
 */
public class MultiLanguageKaleoTaskAssignmentSelectorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@After
	public void tearDown() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testUseJavaScriptingKaleoTaskAssignmentSelector()
		throws PortalException {

		_getExecutionContext();
		_getKaleoInstanceLocalService();
		_getKaleoTaskAssignmentFactory();
		_getTestJavaScriptingKaleoTaskAssignmentSelector();
		_getMultiLanguageKaleoTaskAssignmentSelector();
		_getKaleoTaskAssignment();

		Collection<KaleoTaskAssignment> kaleoTaskAssignments =
			_multiLanguageKaleoTaskAssignmentSelector.getKaleoTaskAssignments(
				_kaleoTaskAssignment, _executionContext);

		Assert.assertTrue(kaleoTaskAssignments.size() == 1);

		Assert.assertTrue(
			_testJavaScriptingKaleoTaskAssignmentSelector.isExecuted());
	}

	private void _getExecutionContext() {
		_executionContext = Mockito.mock(ExecutionContext.class);

		Mockito.when(
			_executionContext.getKaleoInstanceToken()
		).thenReturn(
			Mockito.mock(KaleoInstanceToken.class)
		);
	}

	private void _getKaleoInstanceLocalService() {
		_kaleoInstanceLocalService = Mockito.mock(
			KaleoInstanceLocalService.class);

		Mockito.when(
			_kaleoInstanceLocalService.updateKaleoInstance(Mockito.any())
		).thenReturn(
			Mockito.mock(KaleoInstance.class)
		);
	}

	private void _getKaleoTaskAssignment() {
		_kaleoTaskAssignment = Mockito.mock(KaleoTaskAssignment.class);

		Mockito.when(
			_kaleoTaskAssignment.getAssigneeScriptLanguage()
		).thenReturn(
			"java"
		);

		Class<? extends ScriptingKaleoTaskAssignmentSelector> clazz =
			_testJavaScriptingKaleoTaskAssignmentSelector.getClass();

		Mockito.when(
			_kaleoTaskAssignment.getAssigneeScript()
		).thenReturn(
			clazz.getName()
		);

		Mockito.when(
			_kaleoTaskAssignment.getUserId()
		).thenReturn(
			0L
		);
	}

	private void _getKaleoTaskAssignmentFactory() {
		_kaleoTaskAssignmentFactory = Mockito.mock(
			KaleoTaskAssignmentFactory.class);

		Mockito.when(
			_kaleoTaskAssignmentFactory.createKaleoTaskAssignment()
		).thenReturn(
			new KaleoTaskAssignmentImpl()
		);
	}

	private void _getMultiLanguageKaleoTaskAssignmentSelector()
		throws KaleoDefinitionValidationException {

		_multiLanguageKaleoTaskAssignmentSelector =
			new MultiLanguageKaleoTaskAssignmentSelector();

		ReflectionTestUtil.setFieldValue(
			_multiLanguageKaleoTaskAssignmentSelector,
			"_kaleoInstanceLocalService", _kaleoInstanceLocalService);
		ReflectionTestUtil.setFieldValue(
			_multiLanguageKaleoTaskAssignmentSelector,
			"kaleoTaskAssignmentFactory", _kaleoTaskAssignmentFactory);

		_multiLanguageKaleoTaskAssignmentSelector.activate(_bundleContext);
	}

	private void _getTestJavaScriptingKaleoTaskAssignmentSelector() {
		_testJavaScriptingKaleoTaskAssignmentSelector =
			new TestJavaScriptingKaleoTaskAssignmentSelector();

		_serviceRegistration = _bundleContext.registerService(
			ScriptingKaleoTaskAssignmentSelector.class,
			_testJavaScriptingKaleoTaskAssignmentSelector,
			MapUtil.singletonDictionary("scripting.language", (Object)"java"));
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	private ExecutionContext _executionContext;
	private KaleoInstanceLocalService _kaleoInstanceLocalService;
	private KaleoTaskAssignment _kaleoTaskAssignment;
	private KaleoTaskAssignmentFactory _kaleoTaskAssignmentFactory;
	private MultiLanguageKaleoTaskAssignmentSelector
		_multiLanguageKaleoTaskAssignmentSelector;
	private ServiceRegistration<ScriptingKaleoTaskAssignmentSelector>
		_serviceRegistration;
	private TestJavaScriptingKaleoTaskAssignmentSelector
		_testJavaScriptingKaleoTaskAssignmentSelector;

}
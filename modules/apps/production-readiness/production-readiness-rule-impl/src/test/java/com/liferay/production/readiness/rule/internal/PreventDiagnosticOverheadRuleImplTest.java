/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.production.readiness.Result;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lily Chi
 */
public class PreventDiagnosticOverheadRuleImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCheckReturnsFailWhenUnlockDiagnosticVMOptionsIsPresent() {
		_assertCheckResult(
			Arrays.asList(
				"-Xmx2g", "-XX:+UnlockDiagnosticVMOptions", "-XX:+UseG1GC"),
			Result.Status.FAIL, _MESSAGE_KEY_FAIL,
			"-XX:+UnlockDiagnosticVMOptions");
	}

	@Test
	public void testCheckReturnsPassWhenInputArgumentsIsEmpty() {
		_assertCheckResult(
			Collections.emptyList(), Result.Status.PASS, _MESSAGE_KEY_PASS,
			null);
	}

	@Test
	public void testCheckReturnsPassWhenUnlockDiagnosticVMOptionsIsNotPresent() {
		_assertCheckResult(
			Arrays.asList("-Xmx2g", "-XX:+UseG1GC"), Result.Status.PASS,
			_MESSAGE_KEY_PASS, null);
	}

	private void _assertCheckResult(
		List<String> inputArguments, Result.Status expectedStatus,
		String expectedMessageKey, String expectedCurrentValue) {

		RuntimeMXBean runtimeMXBean = Mockito.mock(RuntimeMXBean.class);

		Mockito.when(
			runtimeMXBean.getInputArguments()
		).thenReturn(
			inputArguments
		);

		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			managementFactoryMockedStatic.when(
				ManagementFactory::getRuntimeMXBean
			).thenReturn(
				runtimeMXBean
			);

			Collection<Result> results =
				_preventDiagnosticOverheadRuleImpl.check(0L);

			Assert.assertEquals(results.toString(), 1, results.size());

			Result result = results.iterator(
			).next();

			Assert.assertEquals(expectedStatus, result.getStatus());
			Assert.assertEquals(Result.Severity.LOW, result.getSeverity());
			Assert.assertEquals(
				"jvm-and-infrastructure-validation", result.getCategory());
			Assert.assertEquals(expectedMessageKey, result.getMessageKey());
			Assert.assertEquals(expectedCurrentValue, result.getCurrentValue());
			Assert.assertNull(result.getRecommendedValue());
			Assert.assertEquals(0, result.getMessageParameters().length);
		}
	}

	private static final String _MESSAGE_KEY_FAIL =
		"production-readiness-rule-prevent-diagnostic-overhead-fail";

	private static final String _MESSAGE_KEY_PASS =
		"production-readiness-rule-prevent-diagnostic-overhead-pass";

	private final PreventDiagnosticOverheadRuleImpl
		_preventDiagnosticOverheadRuleImpl =
			new PreventDiagnosticOverheadRuleImpl();

}
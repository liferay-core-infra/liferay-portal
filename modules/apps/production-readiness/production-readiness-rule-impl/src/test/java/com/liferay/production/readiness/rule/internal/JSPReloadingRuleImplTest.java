/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.production.readiness.Result;

import java.util.Collection;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lily Chi
 */
public class JSPReloadingRuleImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCheckReturnsFailWhenDirectServletContextReloadIsTrue() {
		_assertCheckResult(
			"true", Result.Status.FAIL, Result.Severity.MEDIUM,
			_MESSAGE_KEY_FAIL, "direct.servlet.context.reload=true");
	}

	@Test
	public void testCheckReturnsPassWhenDirectServletContextReloadIsFalse() {
		_assertCheckResult(
			"false", Result.Status.PASS, Result.Severity.LOW, _MESSAGE_KEY_PASS,
			"direct.servlet.context.reload=false");
	}

	@Test
	public void testCheckReturnsPassWhenDirectServletContextReloadIsNotSet() {
		_assertCheckResult(
			null, Result.Status.PASS, Result.Severity.LOW, _MESSAGE_KEY_PASS,
			"direct.servlet.context.reload=false");
	}

	private void _assertCheckResult(
		String propertyValue, Result.Status expectedStatus,
		Result.Severity expectedSeverity, String expectedMessageKey,
		String expectedCurrentValue) {

		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.get("direct.servlet.context.reload")
			).thenReturn(
				propertyValue
			);

			Collection<Result> results = _jspReloadingRuleImpl.check(0L);

			Assert.assertEquals(results.toString(), 1, results.size());

			Result result = results.iterator(
			).next();

			Assert.assertEquals(expectedStatus, result.getStatus());
			Assert.assertEquals(expectedSeverity, result.getSeverity());
			Assert.assertEquals(
				"portal-properties-configuration", result.getCategory());
			Assert.assertEquals(expectedMessageKey, result.getMessageKey());
			Assert.assertEquals(expectedCurrentValue, result.getCurrentValue());
			Assert.assertEquals(
				"direct.servlet.context.reload=false",
				result.getRecommendedValue());
			Assert.assertEquals(0, result.getMessageParameters().length);
		}
	}

	private static final String _MESSAGE_KEY_FAIL =
		"production-readiness-rule-jsp-reloading-fail";

	private static final String _MESSAGE_KEY_PASS =
		"production-readiness-rule-jsp-reloading-pass";

	private final JSPReloadingRuleImpl _jspReloadingRuleImpl =
		new JSPReloadingRuleImpl();

}
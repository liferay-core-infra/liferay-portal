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
public class CounterIncrementRuleImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCheckReturnsFailWhenCounterIncrementIsBelowThreshold() {
		_assertCheckResult("100", Result.Status.FAIL, _MESSAGE_KEY_FAIL, "100");
	}

	@Test
	public void testCheckReturnsFailWhenCounterIncrementIsJustBelowThreshold() {
		_assertCheckResult(
			"1999", Result.Status.FAIL, _MESSAGE_KEY_FAIL, "1999");
	}

	@Test
	public void testCheckReturnsFailWhenCounterIncrementIsNotSet() {
		_assertCheckResult(null, Result.Status.FAIL, _MESSAGE_KEY_FAIL, "0");
	}

	@Test
	public void testCheckReturnsPassWhenCounterIncrementIsAboveThreshold() {
		_assertCheckResult(
			"5000", Result.Status.PASS, _MESSAGE_KEY_PASS, "5000");
	}

	@Test
	public void testCheckReturnsPassWhenCounterIncrementIsAtThreshold() {
		_assertCheckResult(
			"2000", Result.Status.PASS, _MESSAGE_KEY_PASS, "2000");
	}

	private void _assertCheckResult(
		String propertyValue, Result.Status expectedStatus,
		String expectedMessageKey, String expectedCurrentValue) {

		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.get("counter.increment")
			).thenReturn(
				propertyValue
			);

			Collection<Result> results = _counterIncrementRuleImpl.check(0L);

			Assert.assertEquals(results.toString(), 1, results.size());

			Result result = results.iterator(
			).next();

			Assert.assertEquals(expectedStatus, result.getStatus());
			Assert.assertEquals(Result.Severity.LOW, result.getSeverity());
			Assert.assertEquals(
				"portal-properties-configuration", result.getCategory());
			Assert.assertEquals(expectedMessageKey, result.getMessageKey());
			Assert.assertEquals(expectedCurrentValue, result.getCurrentValue());
			Assert.assertNull(result.getRecommendedValue());
			Assert.assertEquals(0, result.getMessageParameters().length);
		}
	}

	private static final String _MESSAGE_KEY_FAIL =
		"production-readiness-rule-counter-increment-fail";

	private static final String _MESSAGE_KEY_PASS =
		"production-readiness-rule-counter-increment-pass";

	private final CounterIncrementRuleImpl _counterIncrementRuleImpl =
		new CounterIncrementRuleImpl();

}
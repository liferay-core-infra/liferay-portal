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
public class PortalDeveloperPropertiesRuleImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCheckReturnsFailWhenDeveloperPropertiesAreIncluded() {
		_assertCheckResult(
			new String[] {"portal-developer.properties"}, Result.Status.FAIL,
			Result.Severity.MEDIUM, _MESSAGE_KEY_FAIL,
			"portal-developer.properties included");
	}

	@Test
	public void testCheckReturnsFailWhenDeveloperPropertiesAreIncludedAmongOthers() {
		_assertCheckResult(
			new String[] {
				"portal-ext.properties", "portal-developer.properties",
				"portal-test.properties"
			},
			Result.Status.FAIL, Result.Severity.MEDIUM, _MESSAGE_KEY_FAIL,
			"portal-developer.properties included");
	}

	@Test
	public void testCheckReturnsPassWhenDeveloperPropertiesAreNotIncluded() {
		_assertCheckResult(
			new String[] {"portal-ext.properties", "portal-test.properties"},
			Result.Status.PASS, Result.Severity.LOW, _MESSAGE_KEY_PASS,
			"portal-developer.properties is not included");
	}

	@Test
	public void testCheckReturnsPassWhenIncludeAndOverrideIsEmpty() {
		_assertCheckResult(
			new String[0], Result.Status.PASS, Result.Severity.LOW,
			_MESSAGE_KEY_PASS, "portal-developer.properties is not included");
	}

	private void _assertCheckResult(
		String[] includeAndOverrides, Result.Status expectedStatus,
		Result.Severity expectedSeverity, String expectedMessageKey,
		String expectedCurrentValue) {

		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.getArray("include-and-override")
			).thenReturn(
				includeAndOverrides
			);

			Collection<Result> results =
				_portalDeveloperPropertiesRuleImpl.check(0L);

			Assert.assertEquals(results.toString(), 1, results.size());

			Result result = results.iterator(
			).next();

			Assert.assertEquals(expectedStatus, result.getStatus());
			Assert.assertEquals(expectedSeverity, result.getSeverity());
			Assert.assertEquals(
				"portal-properties-configuration", result.getCategory());
			Assert.assertEquals(expectedMessageKey, result.getMessageKey());
			Assert.assertEquals(expectedCurrentValue, result.getCurrentValue());
			Assert.assertNull(result.getRecommendedValue());
			Assert.assertEquals(0, result.getMessageParameters().length);
		}
	}

	private static final String _MESSAGE_KEY_FAIL =
		"production-readiness-rule-portal-developer-properties-fail";

	private static final String _MESSAGE_KEY_PASS =
		"production-readiness-rule-portal-developer-properties-pass";

	private final PortalDeveloperPropertiesRuleImpl
		_portalDeveloperPropertiesRuleImpl =
			new PortalDeveloperPropertiesRuleImpl();

}
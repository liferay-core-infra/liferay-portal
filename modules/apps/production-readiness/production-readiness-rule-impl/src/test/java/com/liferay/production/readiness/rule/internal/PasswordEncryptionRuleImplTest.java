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
public class PasswordEncryptionRuleImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCheckReturnsFailWhenAlgorithmIsMD5() {
		_assertCheckResult(
			"MD5", Result.Status.FAIL, Result.Severity.HIGH, _MESSAGE_KEY_FAIL);
	}

	@Test
	public void testCheckReturnsFailWhenAlgorithmIsNotSet() {
		_assertCheckResult(
			null, Result.Status.FAIL, Result.Severity.HIGH, _MESSAGE_KEY_FAIL);
	}

	@Test
	public void testCheckReturnsFailWhenAlgorithmIsPBKDF2WithoutRounds() {
		_assertCheckResult(
			"PBKDF2WithHmacSHA1/160", Result.Status.FAIL, Result.Severity.HIGH,
			_MESSAGE_KEY_FAIL);
	}

	@Test
	public void testCheckReturnsFailWhenPBKDF2RoundsAreBelowThreshold() {
		_assertCheckResult(
			"PBKDF2WithHmacSHA1/160/1299999", Result.Status.FAIL,
			Result.Severity.HIGH, _MESSAGE_KEY_FAIL);
	}

	@Test
	public void testCheckReturnsPassWhenAlgorithmIsBCRYPT() {
		_assertCheckResult(
			"BCRYPT", Result.Status.PASS, Result.Severity.LOW,
			_MESSAGE_KEY_PASS);
	}

	@Test
	public void testCheckReturnsPassWhenAlgorithmIsBCRYPTWithCost() {
		_assertCheckResult(
			"BCRYPT/10", Result.Status.PASS, Result.Severity.LOW,
			_MESSAGE_KEY_PASS);
	}

	@Test
	public void testCheckReturnsPassWhenAlgorithmIsSCRYPT() {
		_assertCheckResult(
			"SCRYPT", Result.Status.PASS, Result.Severity.LOW,
			_MESSAGE_KEY_PASS);
	}

	@Test
	public void testCheckReturnsPassWhenPBKDF2RoundsAreAboveThreshold() {
		_assertCheckResult(
			"PBKDF2WithHmacSHA1/160/2000000", Result.Status.PASS,
			Result.Severity.LOW, _MESSAGE_KEY_PASS);
	}

	@Test
	public void testCheckReturnsPassWhenPBKDF2RoundsAreAtThreshold() {
		_assertCheckResult(
			"PBKDF2WithHmacSHA1/160/1300000", Result.Status.PASS,
			Result.Severity.LOW, _MESSAGE_KEY_PASS);
	}

	private void _assertCheckResult(
		String algorithm, Result.Status expectedStatus,
		Result.Severity expectedSeverity, String expectedMessageKey) {

		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.get("passwords.encryption.algorithm")
			).thenReturn(
				algorithm
			);

			Collection<Result> results = _passwordEncryptionRuleImpl.check(0L);

			Assert.assertEquals(results.toString(), 1, results.size());

			Result result = results.iterator(
			).next();

			Assert.assertEquals(expectedStatus, result.getStatus());
			Assert.assertEquals(expectedSeverity, result.getSeverity());
			Assert.assertEquals(
				"portal-properties-configuration", result.getCategory());
			Assert.assertEquals(expectedMessageKey, result.getMessageKey());
			Assert.assertEquals(algorithm, result.getCurrentValue());
			Assert.assertEquals(
				"PBKDF2WithHmacSHA1/160/1300000 (or stronger)",
				result.getRecommendedValue());
			Assert.assertEquals(0, result.getMessageParameters().length);
		}
	}

	private static final String _MESSAGE_KEY_FAIL =
		"production-readiness-rule-password-encryption-fail";

	private static final String _MESSAGE_KEY_PASS =
		"production-readiness-rule-password-encryption-pass";

	private final PasswordEncryptionRuleImpl _passwordEncryptionRuleImpl =
		new PasswordEncryptionRuleImpl();

}
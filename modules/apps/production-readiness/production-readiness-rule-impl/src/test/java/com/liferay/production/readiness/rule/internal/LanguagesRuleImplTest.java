/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.production.readiness.Result;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Lily Chi
 */
public class LanguagesRuleImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCheckReturnsBetaAndUnusedFailWhenBothPresent()
		throws Exception {

		try (AutoCloseable locales = _setLocales(
				new String[] {"en_US", "es_ES", "fr_FR"});
			AutoCloseable localesBeta = _setLocalesBeta(new String[] {"es_ES"});
			AutoCloseable localesEnabled = _setLocalesEnabled(
				new String[] {"en_US", "es_ES"})) {

			Collection<Result> results = _languagesRuleImpl.check(0L);

			Assert.assertEquals(results.toString(), 2, results.size());

			Iterator<Result> iterator = results.iterator();

			Result betaResult = iterator.next();

			_assertFailResult(
				betaResult, _MESSAGE_KEY_BETA_FAIL, "es_ES", null);

			Result unusedResult = iterator.next();

			_assertFailResult(
				unusedResult, _MESSAGE_KEY_UNUSED_FAIL, "fr_FR",
				"Remove unused locales from LOCALES (portal-ext.properties)");
		}
	}

	@Test
	public void testCheckReturnsBetaFailWhenEnabledLocaleIsBeta()
		throws Exception {

		try (AutoCloseable locales = _setLocales(
				new String[] {"en_US", "es_ES"});
			AutoCloseable localesBeta = _setLocalesBeta(new String[] {"es_ES"});
			AutoCloseable localesEnabled = _setLocalesEnabled(
				new String[] {"en_US", "es_ES"})) {

			Collection<Result> results = _languagesRuleImpl.check(0L);

			Assert.assertEquals(results.toString(), 1, results.size());

			Result result = results.iterator(
			).next();

			_assertFailResult(result, _MESSAGE_KEY_BETA_FAIL, "es_ES", null);
		}
	}

	@Test
	public void testCheckReturnsPassWhenNoBetaAndNoUnusedLocales()
		throws Exception {

		try (AutoCloseable locales = _setLocales(
				new String[] {"en_US", "es_ES"});
			AutoCloseable localesBeta = _setLocalesBeta(new String[0]);
			AutoCloseable localesEnabled = _setLocalesEnabled(
				new String[] {"en_US", "es_ES"})) {

			Collection<Result> results = _languagesRuleImpl.check(0L);

			Assert.assertEquals(results.toString(), 1, results.size());

			Result result = results.iterator(
			).next();

			Assert.assertEquals(Result.Status.PASS, result.getStatus());
			Assert.assertEquals(Result.Severity.LOW, result.getSeverity());
			Assert.assertEquals(
				"portal-properties-configuration", result.getCategory());
			Assert.assertEquals(_MESSAGE_KEY_PASS, result.getMessageKey());
			Assert.assertNull(result.getCurrentValue());
			Assert.assertNull(result.getRecommendedValue());
			Assert.assertEquals(0, result.getMessageParameters().length);
		}
	}

	@Test
	public void testCheckReturnsUnusedFailWhenAvailableLocaleIsNotEnabled()
		throws Exception {

		try (AutoCloseable locales = _setLocales(
				new String[] {"en_US", "es_ES", "fr_FR"});
			AutoCloseable localesBeta = _setLocalesBeta(new String[0]);
			AutoCloseable localesEnabled = _setLocalesEnabled(
				new String[] {"en_US"})) {

			Collection<Result> results = _languagesRuleImpl.check(0L);

			Assert.assertEquals(results.toString(), 1, results.size());

			Result result = results.iterator(
			).next();

			_assertFailResult(
				result, _MESSAGE_KEY_UNUSED_FAIL, "es_ES,fr_FR",
				"Remove unused locales from LOCALES (portal-ext.properties)");
		}
	}

	private void _assertFailResult(
		Result result, String expectedMessageKey, String expectedCurrentValue,
		String expectedRecommendedValue) {

		Assert.assertEquals(Result.Status.FAIL, result.getStatus());
		Assert.assertEquals(Result.Severity.LOW, result.getSeverity());
		Assert.assertEquals(
			"portal-properties-configuration", result.getCategory());
		Assert.assertEquals(expectedMessageKey, result.getMessageKey());
		Assert.assertEquals(expectedCurrentValue, result.getCurrentValue());
		Assert.assertEquals(
			expectedRecommendedValue, result.getRecommendedValue());
		Assert.assertEquals(1, result.getMessageParameters().length);
	}

	private AutoCloseable _setLocales(String[] locales) {
		return ReflectionTestUtil.setFieldValueWithAutoCloseable(
			PropsValues.class, "LOCALES", locales);
	}

	private AutoCloseable _setLocalesBeta(String[] locales) {
		return ReflectionTestUtil.setFieldValueWithAutoCloseable(
			PropsValues.class, "LOCALES_BETA", locales);
	}

	private AutoCloseable _setLocalesEnabled(String[] locales) {
		return ReflectionTestUtil.setFieldValueWithAutoCloseable(
			PropsValues.class, "LOCALES_ENABLED", locales);
	}

	private static final String _MESSAGE_KEY_BETA_FAIL =
		"production-readiness-rule-languages-beta-fail";

	private static final String _MESSAGE_KEY_PASS =
		"production-readiness-rule-languages-pass";

	private static final String _MESSAGE_KEY_UNUSED_FAIL =
		"production-readiness-rule-languages-unused-fail";

	private final LanguagesRuleImpl _languagesRuleImpl =
		new LanguagesRuleImpl();

}
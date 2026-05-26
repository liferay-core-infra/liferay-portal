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

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Lily Chi
 */
public class DLImagePreviewDPIRuleImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCheckReturnsFailWhenDPIIsAboveThreshold() throws Exception {
		_assertCheckResult(300, Result.Status.FAIL, _MESSAGE_KEY_FAIL);
	}

	@Test
	public void testCheckReturnsFailWhenDPIIsJustAboveThreshold()
		throws Exception {

		_assertCheckResult(76, Result.Status.FAIL, _MESSAGE_KEY_FAIL);
	}

	@Test
	public void testCheckReturnsPassWhenDPIIsAtThreshold() throws Exception {
		_assertCheckResult(75, Result.Status.PASS, _MESSAGE_KEY_PASS);
	}

	@Test
	public void testCheckReturnsPassWhenDPIIsBelowThreshold() throws Exception {
		_assertCheckResult(50, Result.Status.PASS, _MESSAGE_KEY_PASS);
	}

	private void _assertCheckResult(
			int dpi, Result.Status expectedStatus, String expectedMessageKey)
		throws Exception {

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "DL_FILE_ENTRY_PREVIEW_DOCUMENT_DPI",
					dpi)) {

			Collection<Result> results = _dlImagePreviewDPIRuleImpl.check(0L);

			Assert.assertEquals(results.toString(), 1, results.size());

			Result result = results.iterator(
			).next();

			Assert.assertEquals(expectedStatus, result.getStatus());
			Assert.assertEquals(Result.Severity.LOW, result.getSeverity());
			Assert.assertEquals(
				"portal-properties-configuration", result.getCategory());
			Assert.assertEquals(expectedMessageKey, result.getMessageKey());
			Assert.assertEquals(String.valueOf(dpi), result.getCurrentValue());
			Assert.assertNull(result.getRecommendedValue());
			Assert.assertEquals(0, result.getMessageParameters().length);
		}
	}

	private static final String _MESSAGE_KEY_FAIL =
		"production-readiness-rule-dl-image-preview-dpi-fail";

	private static final String _MESSAGE_KEY_PASS =
		"production-readiness-rule-dl-image-preview-dpi-pass";

	private final DLImagePreviewDPIRuleImpl _dlImagePreviewDPIRuleImpl =
		new DLImagePreviewDPIRuleImpl();

}
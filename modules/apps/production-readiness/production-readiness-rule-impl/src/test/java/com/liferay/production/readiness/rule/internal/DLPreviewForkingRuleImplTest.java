/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.production.readiness.Result;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Lily Chi
 */
public class DLPreviewForkingRuleImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCheckReturnsFailWhenForkProcessIsDisabled()
		throws Exception {

		_assertCheckResult(
			false, Result.Status.FAIL, _MESSAGE_KEY_FAIL,
			_PROPERTY_KEY + "=false");
	}

	@Test
	public void testCheckReturnsPassWhenForkProcessIsEnabled()
		throws Exception {

		_assertCheckResult(
			true, Result.Status.PASS, _MESSAGE_KEY_PASS,
			_PROPERTY_KEY + "=true");
	}

	private void _assertCheckResult(
			boolean forkProcessEnabled, Result.Status expectedStatus,
			String expectedMessageKey, String expectedCurrentValue)
		throws Exception {

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class,
					"DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED",
					forkProcessEnabled)) {

			Collection<Result> results = _dlPreviewForkingRuleImpl.check(0L);

			Assert.assertEquals(results.toString(), 1, results.size());

			Result result = results.iterator(
			).next();

			Assert.assertEquals(expectedStatus, result.getStatus());
			Assert.assertEquals(Result.Severity.LOW, result.getSeverity());
			Assert.assertEquals(
				"portal-properties-configuration", result.getCategory());
			Assert.assertEquals(expectedMessageKey, result.getMessageKey());
			Assert.assertEquals(expectedCurrentValue, result.getCurrentValue());
			Assert.assertEquals(
				_PROPERTY_KEY + "=true", result.getRecommendedValue());

			Object[] messageParameters = result.getMessageParameters();

			Assert.assertEquals(
				Arrays.toString(messageParameters), 1,
				messageParameters.length);
			Assert.assertEquals(
				Arrays.toString(messageParameters), _PROPERTY_KEY,
				messageParameters[0]);
		}
	}

	private static final String _MESSAGE_KEY_FAIL =
		"production-readiness-rule-dl-preview-forking-fail";

	private static final String _MESSAGE_KEY_PASS =
		"production-readiness-rule-dl-preview-forking-pass";

	private static final String _PROPERTY_KEY =
		"dl.file.entry.preview.fork.process.enabled";

	private final DLPreviewForkingRuleImpl _dlPreviewForkingRuleImpl =
		new DLPreviewForkingRuleImpl();

}
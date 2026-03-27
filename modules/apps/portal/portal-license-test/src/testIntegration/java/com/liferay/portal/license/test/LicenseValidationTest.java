/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import net.bytebuddy.agent.builder.ResettableClassFileTransformer;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kevin Lee
 */
@RunWith(Arquillian.class)
public class LicenseValidationTest extends BaseLicenseTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		Assume.assumeTrue(isReleaseBundle());
	}

	@Test
	public void testFreeTierLicenseValidateVersion() throws Exception {
		try (AutoCloseable autoCloseable = _disableValidate()) {
			_assertVersionIsValid("2026.Q1.0 LTS");
			_assertVersionIsValid("2026.Q2.0");
			_assertVersionIsValid("2026.Q3.0");
			_assertVersionIsValid("2026.Q4.0");

			_assertVersionIsInvalid("2026.Q1.0");
			_assertVersionIsInvalid("2026.Q1.1 LTS");
			_assertVersionIsInvalid("2026.Q2.1");
			_assertVersionIsInvalid("2026.Q3");
		}
	}

	private void _assertVersionIsInvalid(String version) throws Exception {
		try (AutoCloseable autoCloseable = _setVersion(version);
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_getLicenseManagerClassName(), LoggerTestUtil.ERROR)) {

			deployFreeTierPortalLicense();

			assertPortalLicenseInvalid();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertFalse(logEntries.isEmpty());

			LogEntry logEntry = logEntries.getFirst();

			Assert.assertEquals(
				"DXP Production license validation failed",
				logEntry.getMessage());

			Throwable throwable = logEntry.getThrowable();

			Assert.assertNotNull(throwable);

			Assert.assertEquals(
				"License is not suppported in " + version,
				throwable.getMessage());
		}
		finally {
			resetLicenseData();
		}
	}

	private void _assertVersionIsValid(String version) throws Exception {
		try (AutoCloseable autoCloseable = _setVersion(version);
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_getLicenseManagerClassName(), LoggerTestUtil.ERROR)) {

			deployFreeTierPortalLicense();

			assertPortalLicenseRegistered();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.isEmpty());
		}
		finally {
			resetLicenseData();
		}
	}

	private AutoCloseable _disableValidate() {
		ResettableClassFileTransformer resettableClassFileTransformer =
			disableValidate();

		return () -> resetClassFileTransformer(resettableClassFileTransformer);
	}

	private String _getLicenseManagerClassName() {
		return getProperty("license.manager.class.name");
	}

	private AutoCloseable _setVersion(String version) {
		ResettableClassFileTransformer resettableClassFileTransformer =
			setVersion(version);

		return () -> resetClassFileTransformer(resettableClassFileTransformer);
	}

}
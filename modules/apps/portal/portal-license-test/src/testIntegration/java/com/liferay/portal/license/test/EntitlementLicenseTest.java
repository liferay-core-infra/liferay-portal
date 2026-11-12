/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.log.LogEntry;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kevin Lee
 */
@RunWith(Arquillian.class)
public class EntitlementLicenseTest extends BaseLicenseTestCase {

	public static void assume() {
		Assume.assumeTrue(isReleaseBundle());
	}

	@BeforeClass
	public static void setUpClass() {
		_disableValidateSafeCloseable = disableValidateWithSafeCloseable();
		_setVersionSafeCloseable = setVersionWithSafeCloseable("2027.Q1.0 LTS");
	}

	@AfterClass
	public static void tearDownClass() {
		_setVersionSafeCloseable.close();
		_disableValidateSafeCloseable.close();
	}

	@Before
	public void setUp() throws Exception {
		_resetLicenseDataSafeCloseable = resetLicenseDataWithSafeCloseble();
	}

	@After
	public void tearDown() {
		_resetLicenseDataSafeCloseable.close();
	}

	@Test
	public void testPortalLicense() throws Exception {
		String installationId = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable = setInstallationIdWithSafeCloseable(
				installationId)) {

			deployEnterprisePortalLicense(
				RandomTestUtil.randomString(), "7", installationId, Time.HOUR);

			assertLicensePropertiesExisted(getPortalProductId());

			checkLicense(getPortalProductId());
		}
	}

	@Test
	public void testPortalLicenseForDifferentInstallation() throws Exception {
		try {
			deployEnterprisePortalLicense(
				RandomTestUtil.randomString(), "7",
				RandomTestUtil.randomString(), Time.HOUR);

			Assert.fail();
		}
		catch (LogEntriesException logEntriesException) {
			Assert.assertTrue(
				StringUtil.endsWith(
					_getThrowableMessage(logEntriesException),
					"belongs to a different installation"));
		}
	}

	@Test
	public void testPortalLicenseWithUnsupportedLicenseVersion()
		throws Exception {

		try {
			deployEnterprisePortalLicense(
				RandomTestUtil.randomString(), "6", null, Time.HOUR);

			Assert.fail();
		}
		catch (LogEntriesException logEntriesException) {
			Assert.assertEquals(
				"License version 6 is not supported",
				_getThrowableMessage(logEntriesException));
		}
	}

	private String _getThrowableMessage(
		LogEntriesException logEntriesException) {

		List<LogEntry> logEntries = logEntriesException.getLogEntries();

		Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

		LogEntry logEntry = logEntries.get(0);

		String message = logEntry.getMessage();

		Assert.assertTrue(
			message, message.endsWith(" license validation failed"));

		Throwable throwable = logEntry.getThrowable();

		return throwable.getMessage();
	}

	private static SafeCloseable _disableValidateSafeCloseable;
	private static SafeCloseable _setVersionSafeCloseable;

	private SafeCloseable _resetLicenseDataSafeCloseable;

}
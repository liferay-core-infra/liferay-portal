/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.log.LogEntry;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Kevin Lee
 */
public abstract class BaseEntitlementLicenseTestCase
	extends BaseLicenseTestCase {

	@BeforeClass
	public static void setUpClass() {
		_disableValidateSafeCloseable = disableValidateWithSafeCloseable();
		_entitlementReleaseSafeCloseable =
			setEntitlementReleaseWithSafeCloseable(true);
	}

	@AfterClass
	public static void tearDownClass() {
		_entitlementReleaseSafeCloseable.close();
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
	public void testAppLicenses() throws Exception {
		String installationId = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable = setInstallationIdWithSafeCloseable(
				installationId)) {

			for (App app : getApps()) {
				deployAppLicense(app, "7", installationId, Time.HOUR);

				assertLicensePropertiesExisted(getProductId(app));

				checkLicense(getProductId(app));
			}
		}
	}

	@Test
	public void testAppLicensesForDifferentInstallation() throws Exception {
		for (App app : getApps()) {
			try {
				deployAppLicense(
					app, "7", RandomTestUtil.randomString(), Time.HOUR);

				Assert.fail();
			}
			catch (LogEntriesException logEntriesException) {
				_assertDifferentInstallation(logEntriesException);
			}
		}
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
			_assertDifferentInstallation(logEntriesException);
		}
	}

	protected abstract App[] getApps();

	private void _assertDifferentInstallation(
		LogEntriesException logEntriesException) {

		List<LogEntry> logEntries = logEntriesException.getLogEntries();

		Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

		LogEntry logEntry = logEntries.get(0);

		String message = logEntry.getMessage();

		Assert.assertTrue(
			message, message.endsWith(" license validation failed"));

		Throwable throwable = logEntry.getThrowable();

		Assert.assertTrue(
			StringUtil.endsWith(
				throwable.getMessage(), "belongs to a different installation"));
	}

	private static SafeCloseable _disableValidateSafeCloseable;
	private static SafeCloseable _entitlementReleaseSafeCloseable;

	private SafeCloseable _resetLicenseDataSafeCloseable;

}
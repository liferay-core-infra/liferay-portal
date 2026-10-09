/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.log.LogEntry;

import java.io.File;

import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kevin Lee
 */
@RunWith(Arquillian.class)
public class AppLicenseTest extends BaseLicenseTestCase {

	@BeforeClass
	public static void setUpClass() {
		_disableKeyValidatorSafeCloseable = disableValidateWithSafeCloseable();
		_setVersionSafeCloseable = setVersionWithSafeCloseable("2026.Q1.0 LTS");
	}

	@AfterClass
	public static void tearDownClass() {
		_disableKeyValidatorSafeCloseable.close();
		_setVersionSafeCloseable.close();
	}

	@Test
	public void testAppLicenses() throws Exception {
		for (App app : App.values()) {
			if (isSupportedApp(app)) {
				_testAppLicenseSupported(app);
			}
			else {
				_testAppLicenseUnsupported(app);
			}
		}
	}

	private void _testAppLicenseSupported(App app) throws Exception {
		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			assertLicensePropertiesNotExisted(getProductId(app));

			Assert.assertFalse(LicenseManagerUtil.isAppEnabled(app));
			Assert.assertNull(LicenseManagerUtil.getAppExpirationDate(app));

			long startTime = System.currentTimeMillis();

			File binaryFile = deployAppLicense(app, startTime, Time.HOUR);

			assertLicensePropertiesExisted(getProductId(app));

			Assert.assertTrue(LicenseManagerUtil.isAppEnabled(app));
			Assert.assertEquals(
				getDateString(new Date(startTime + Time.HOUR)),
				getDateString(LicenseManagerUtil.getAppExpirationDate(app)));

			binaryFile.delete();

			checkLicense(getProductId(app));

			assertLicensePropertiesNotExisted(getProductId(app));

			Assert.assertFalse(LicenseManagerUtil.isAppEnabled(app));
			Assert.assertNull(LicenseManagerUtil.getAppExpirationDate(app));
		}
	}

	private void _testAppLicenseUnsupported(App app) throws Exception {
		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			assertLicensePropertiesNotExisted(getProductId(app));

			Assert.assertFalse(LicenseManagerUtil.isAppEnabled(app));
			Assert.assertNull(LicenseManagerUtil.getAppExpirationDate(app));

			long startTime = System.currentTimeMillis();

			try {
				deployAppLicense(app, startTime, Time.HOUR);

				Assert.fail();
			}
			catch (LogEntriesException logEntriesException) {
				Assert.assertFalse(LicenseManagerUtil.isAppEnabled(app));

				List<LogEntry> logEntries = logEntriesException.getLogEntries();

				LogEntry logEntry = logEntries.get(0);

				Assert.assertEquals(
					app + " license validation failed", logEntry.getMessage());
			}
		}
	}

	private static SafeCloseable _disableKeyValidatorSafeCloseable;
	private static SafeCloseable _setVersionSafeCloseable;

}
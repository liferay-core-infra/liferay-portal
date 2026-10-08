/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.log.LogEntry;

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
public class ProductLicenseTest extends BaseLicenseTestCase {

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
	public void testPortalLicense() throws Exception {
		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			assertLicensePropertiesNotExisted(getPortalProductId());

			assertPortalLicenseNotRegistered();

			if (ReleaseInfo.isCMSStandalone()) {
				deployCMSPortalLicense(Time.HOUR);
			}
			else {
				deployEnterprisePortalLicense(Time.HOUR);
			}

			assertLicensePropertiesExisted(getPortalProductId());

			assertPortalLicenseRegistered();
		}
	}

	@Test
	public void testPortalLicenseInvalid() throws Exception {
		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			assertLicensePropertiesNotExisted(getPortalProductId());

			assertPortalLicenseNotRegistered();

			try {
				if (ReleaseInfo.isCMSStandalone()) {
					deployEnterprisePortalLicense(Time.HOUR);
				}
				else {
					deployCMSPortalLicense(Time.HOUR);
				}

				Assert.fail();
			}
			catch (LogEntriesException logEntriesException) {
				List<LogEntry> logEntries = logEntriesException.getLogEntries();

				LogEntry logEntry = logEntries.get(0);

				if (ReleaseInfo.isCMSStandalone()) {
					Assert.assertEquals(
						"DXP Enterprise license validation failed",
						logEntry.getMessage());
				}
				else {
					Assert.assertEquals(
						"CMS Production license validation failed",
						logEntry.getMessage());
				}
			}
		}
	}

	private static SafeCloseable _disableKeyValidatorSafeCloseable;
	private static SafeCloseable _setVersionSafeCloseable;

}
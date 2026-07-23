/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.log.LogEntry;

import java.util.ArrayList;
import java.util.Arrays;
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
public class CombinedLicenseTest extends BaseLicenseTestCase {

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
	public void testAppLicensesWithExpiredPortalLicenseEnterprise()
		throws Exception {

		_testAppLicensesWithPortalLicense(false, -Time.WEEK);
	}

	@Test
	public void testAppLicensesWithExpiredPortalLicenseFreeTier()
		throws Exception {

		_testAppLicensesWithPortalLicense(true, -Time.WEEK);
	}

	@Test
	public void testAppLicensesWithPortalLicenseEnterprise() throws Exception {
		_testAppLicensesWithPortalLicense(false, Time.HOUR);
	}

	@Test
	public void testAppLicensesWithPortalLicenseFreeTier() throws Exception {
		_testAppLicensesWithPortalLicense(true, Time.HOUR);
	}

	@Test
	public void testDuplicateLicenses() throws Exception {
		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			assertLicensePropertiesNotExisted(getPortalProductId());

			assertPortalLicenseNotRegistered();

			deployLicenses(
				new String[][] {
					{FREE_TIER_LICENSE_TYPE, String.valueOf(Time.HOUR)},
					{FREE_TIER_LICENSE_TYPE, String.valueOf(Time.HOUR)}
				});

			assertLicensePropertiesExisted(getPortalProductId());

			assertPortalLicenseRegistered();
		}
	}

	@Test
	public void testNoLicenses() throws Exception {
		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			assertPortalLicenseNotRegistered();

			deployLicenses(new String[0][]);

			assertPortalLicenseNotRegistered();
		}
	}

	@Test
	public void testPortalLicensesEnterpriseAndFreeTier() throws Exception {
		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			assertPortalLicenseNotRegistered();

			deployLicenses(
				new String[][] {
					{ENTERPRISE_LICENSE_TYPE, String.valueOf(Time.HOUR)},
					{FREE_TIER_LICENSE_TYPE, String.valueOf(Time.HOUR)}
				});

			assertLicensePropertiesExisted(getPortalProductId());

			assertPortalLicenseRegistered();

			Assert.assertFalse(LicenseManagerUtil.isFreeTier());
		}
	}

	@Test
	public void testSingleLicense() throws Exception {
		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			assertLicensePropertiesNotExisted(getPortalProductId());

			assertPortalLicenseNotRegistered();

			deployLicenses(
				new String[][] {
					{FREE_TIER_LICENSE_TYPE, String.valueOf(Time.HOUR)}
				});

			assertLicensePropertiesExisted(getPortalProductId());

			assertPortalLicenseRegistered();
		}
	}

	private void _assertPortalAndAppLicensePropertiesExisted() {
		assertLicensePropertiesExisted(getPortalProductId());

		for (App app : App.values()) {
			assertLicensePropertiesExisted(getProductId(app));
		}
	}

	private void _testAppLicensesWithPortalLicense(
			boolean freeTier, long validityPeriod)
		throws Exception {

		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			assertLicensePropertiesNotExisted(getPortalProductId());

			for (App app : App.values()) {
				String[] appSymbolicNames = getAppSymbolicNames(app);

				Assert.assertFalse(
					Arrays.toString(appSymbolicNames),
					ArrayUtil.isEmpty(appSymbolicNames));

				assertLicensePropertiesNotExisted(getProductId(app));

				assertBundlesExisted(appSymbolicNames);
			}

			assertPortalLicenseNotRegistered();

			List<String[]> licenses = new ArrayList<>();

			String licenseType = null;

			if (freeTier) {
				licenseType = FREE_TIER_LICENSE_TYPE;
			}
			else {
				licenseType = ENTERPRISE_LICENSE_TYPE;
			}

			licenses.add(
				new String[] {licenseType, String.valueOf(validityPeriod)});

			for (App app : App.values()) {
				licenses.add(
					new String[] {app.name(), String.valueOf(validityPeriod)});
			}

			try {
				deployLicenses(licenses.toArray(new String[0][]));

				if (validityPeriod > 0) {
					assertPortalLicenseRegistered();

					_assertPortalAndAppLicensePropertiesExisted();

					for (App app : App.values()) {
						if (freeTier) {
							assertBundlesNotExisted(getAppSymbolicNames(app));
						}
						else {
							assertBundlesExisted(getAppSymbolicNames(app));
						}
					}
				}
				else {
					Assert.fail(
						"Expected expired portal license to fail validation");
				}
			}
			catch (LogEntriesException logEntriesException) {
				if (validityPeriod < 0) {
					List<LogEntry> logEntries =
						logEntriesException.getLogEntries();

					Assert.assertEquals(
						logEntries.toString(), 1, logEntries.size());

					LogEntry logEntry = logEntries.get(0);

					if (freeTier) {
						Assert.assertEquals(
							"DXP Production license is expired",
							logEntry.getMessage());
					}
					else {
						Assert.assertEquals(
							"DXP Enterprise license is expired",
							logEntry.getMessage());
					}

					assertPortalLicenseExpired();

					_assertPortalAndAppLicensePropertiesExisted();

					for (App app : App.values()) {
						assertBundlesExisted(getAppSymbolicNames(app));
					}
				}
			}
		}
	}

	private static SafeCloseable _disableKeyValidatorSafeCloseable;
	private static SafeCloseable _setVersionSafeCloseable;

}
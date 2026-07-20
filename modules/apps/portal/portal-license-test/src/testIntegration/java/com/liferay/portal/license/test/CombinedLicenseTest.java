/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.util.LicenseUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
	public void testAppLicensesWithPortalLicenseEnterprise() throws Exception {
		_testAppLicensesWithPortalLicense(false);
	}

	@Test
	public void testAppLicensesWithPortalLicenseFreeTier() throws Exception {
		_testAppLicensesWithPortalLicense(true);
	}

	@Test
	public void testDuplicateLicenses() throws Exception {
		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			assertLicensePropertiesNotExisted(getPortalProductId());

			assertPortalLicenseNotRegistered();

			File[] binaryFiles = _deployCombinedLicense(
				List.of(
					buildFreeTierPortalLicenseXML(Time.HOUR),
					buildFreeTierPortalLicenseXML(Time.HOUR)));

			assertLicensePropertiesExisted(getPortalProductId());

			assertPortalLicenseRegistered();

			Assert.assertEquals(
				Arrays.toString(binaryFiles), 1, binaryFiles.length);
		}
	}

	@Test
	public void testNoLicenses() throws Exception {
		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			assertPortalLicenseNotRegistered();

			File[] binaryFiles = _deployCombinedLicense(
				Collections.emptyList());

			assertPortalLicenseNotRegistered();

			Assert.assertNull(Arrays.toString(binaryFiles), binaryFiles);
		}
	}

	@Test
	public void testPortalLicensesEnterpriseAndFreeTier() throws Exception {
		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			assertPortalLicenseNotRegistered();

			File[] binaryFiles = _deployCombinedLicense(
				List.of(
					buildEnterprisePortalLicenseXML(Time.HOUR),
					buildFreeTierPortalLicenseXML(Time.HOUR)));

			assertLicensePropertiesExisted(getPortalProductId());

			assertPortalLicenseRegistered();

			Assert.assertEquals(
				Arrays.toString(binaryFiles), 2, binaryFiles.length);

			Assert.assertFalse(LicenseManagerUtil.isFreeTier());
		}
	}

	@Test
	public void testSingleLicense() throws Exception {
		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			assertLicensePropertiesNotExisted(getPortalProductId());

			assertPortalLicenseNotRegistered();

			File[] binaryFiles = _deployCombinedLicense(
				List.of(buildFreeTierPortalLicenseXML(Time.HOUR)));

			assertLicensePropertiesExisted(getPortalProductId());

			assertPortalLicenseRegistered();

			Assert.assertEquals(
				Arrays.toString(binaryFiles), 1, binaryFiles.length);
		}
	}

	private File[] _deployCombinedLicense(Collection<String> licenseXMLs)
		throws Exception {

		registerLicense(
			StringBundler.concat(
				"<licenses>", StringUtil.merge(licenseXMLs, StringPool.BLANK),
				"</licenses>"));

		return new File(
			LicenseUtil.LICENSE_REPOSITORY_DIR
		).listFiles(
			(dirFile, name) -> name.endsWith(".li")
		);
	}

	private void _testAppLicensesWithPortalLicense(boolean freeTier)
		throws Exception {

		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			assertLicensePropertiesNotExisted(getPortalProductId());

			for (App app : App.values()) {
				assertLicensePropertiesNotExisted(getProductId(app));
			}

			assertPortalLicenseNotRegistered();

			List<String> licenseXMLs = new ArrayList<>();

			if (freeTier) {
				licenseXMLs.add(buildFreeTierPortalLicenseXML(Time.HOUR));
			}
			else {
				licenseXMLs.add(buildEnterprisePortalLicenseXML(Time.HOUR));
			}

			long startTime = System.currentTimeMillis();

			for (App app : App.values()) {
				licenseXMLs.add(buildAppLicenseXML(app, startTime, Time.HOUR));
			}

			File[] binaryFiles = _deployCombinedLicense(licenseXMLs);

			assertPortalLicenseRegistered();

			assertLicensePropertiesExisted(getPortalProductId());

			for (App app : App.values()) {
				assertLicensePropertiesExisted(getProductId(app));
			}

			if (freeTier) {
				Assert.assertTrue(LicenseManagerUtil.isFreeTier());
			}
			else {
				Assert.assertFalse(LicenseManagerUtil.isFreeTier());
			}

			Assert.assertEquals(
				Arrays.toString(binaryFiles), App.values().length + 1,
				binaryFiles.length);
		}
	}

	private static SafeCloseable _disableKeyValidatorSafeCloseable;
	private static SafeCloseable _setVersionSafeCloseable;

}
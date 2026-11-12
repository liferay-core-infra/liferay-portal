/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.log.LogEntry;

import java.lang.reflect.Field;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class DXPFreeTierVersionLicenseTest extends BaseLicenseTestCase {

	public static void assume() {
		Assume.assumeTrue(isReleaseBundle() && !isCMS());
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		_disableKeyValidatorSafeCloseable = disableValidateWithSafeCloseable();

		_propertiesField = findField("properties.field");
	}

	@AfterClass
	public static void tearDownClass() {
		_disableKeyValidatorSafeCloseable.close();
	}

	@Test
	public void testIgnoredVersion() throws Exception {
		String ignoredVersion = _getIgnoredVersion();

		if (Validator.isNull(ignoredVersion)) {
			return;
		}

		Assert.assertEquals(ignoredVersion, getCurrentVersion());

		String[] parts = StringUtil.split(ignoredVersion, CharPool.PERIOD);

		if (GetterUtil.getInteger(parts[0]) < 2027) {
			_testVersion(true, 6, true, ignoredVersion);
		}
		else {
			_testVersion(true, 7, true, ignoredVersion);
		}
	}

	@Test
	public void testNonignoredNonpatchedVersions() throws Exception {
		_testNonignoredVersion(6, true, false);
		_testNonignoredVersion(6, false, false);
		_testNonignoredVersion(7, true, false);
		_testNonignoredVersion(7, false, false);
	}

	@Test
	public void testNonignoredPatchedVersions() throws Exception {
		_testNonignoredVersion(6, true, true);
		_testNonignoredVersion(6, false, true);
		_testNonignoredVersion(7, true, true);
		_testNonignoredVersion(7, false, true);
	}

	private String _generateRandomVersion(
		int licenseVersion, boolean ltsVersion, boolean patch) {

		int year;

		if (licenseVersion < 7) {
			year = RandomTestUtil.randomInt(1000, 2026);
		}
		else {
			year = RandomTestUtil.randomInt(2027, 9999);
		}

		int patchVersion = 0;

		if (patch) {
			patchVersion = RandomTestUtil.randomInt(1, 9999);
		}

		if (ltsVersion) {
			return StringBundler.concat(year, ".Q1.", patchVersion, " LTS");
		}

		return StringBundler.concat(
			year, ".Q", RandomTestUtil.randomInt(1, 4), ".", patchVersion);
	}

	private String _getIgnoredVersion() throws Exception {
		Properties properties = (Properties)_propertiesField.get(null);

		return properties.getProperty("license.free.tier.ignored.version");
	}

	private void _testNonignoredVersion(
			int licenseVersion, boolean ltsVersion, boolean patch)
		throws Exception {

		String ignoredVersion = _getIgnoredVersion();

		String version = null;

		while (true) {
			version = _generateRandomVersion(licenseVersion, ltsVersion, patch);

			if (!Objects.equals(ignoredVersion, version)) {
				break;
			}
		}

		_testVersion(false, licenseVersion, patch, version);
	}

	private void _testVersion(
			boolean ignored, int licenseVersion, boolean patch, String version)
		throws Exception {

		String installationId = null;

		if (licenseVersion >= 7) {
			installationId = RandomTestUtil.randomString();
		}

		assertLicensePropertiesNotExisted(getPortalProductId());

		assertPortalLicenseNotRegistered();

		try (SafeCloseable safeCloseable1 = setVersionWithSafeCloseable(
				version);
			SafeCloseable safeCloseable2 = resetLicenseDataWithSafeCloseble();
			SafeCloseable safeCloseable3 = setInstallationIdWithSafeCloseable(
				installationId)) {

			deployFreeTierPortalLicense(
				installationId, licenseVersion, Time.HOUR);

			if (!patch || ignored) {
				assertLicensePropertiesExisted(getPortalProductId());

				assertPortalLicenseRegistered();
			}
			else {
				Assert.fail(
					StringBundler.concat(
						"Unable to see error message \"License is not ",
						"suppported in ", version, "\""));
			}
		}
		catch (LogEntriesException logEntriesException) {
			List<LogEntry> logEntries = logEntriesException.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertEquals(
				throwable.getMessage(),
				"License is not suppported in " + version);
		}
	}

	private static SafeCloseable _disableKeyValidatorSafeCloseable;
	private static Field _propertiesField;

}
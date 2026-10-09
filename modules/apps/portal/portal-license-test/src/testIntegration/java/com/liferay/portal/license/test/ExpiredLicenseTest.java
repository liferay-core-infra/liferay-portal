/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.util.Time;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class ExpiredLicenseTest extends BaseLicenseTestCase {

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

	@Before
	public void setUp() throws Exception {
		_safeCloseable = resetLicenseDataWithSafeCloseble();
	}

	@After
	public void tearDown() {
		_safeCloseable.close();
	}

	@Test
	public void testAppLicenseExpired() throws Exception {
		for (App app : App.values()) {
			if (!isSupportedApp(app)) {
				continue;
			}

			assertLicensePropertiesNotExisted(getProductId(app));

			deployAppLicense(app, GRACE_PERIOD + _VALIDITY_PERIOD);

			assertLicensePropertiesExisted(getProductId(app));

			Assert.assertTrue(LicenseManagerUtil.isAppEnabled(app));

			Thread.sleep(_VALIDITY_PERIOD);

			assertLicensePropertiesExisted(getProductId(app));

			Assert.assertFalse(LicenseManagerUtil.isAppEnabled(app));
		}
	}

	@Test
	public void testCMSPortalLicenseExpired() throws Exception {
		Assume.assumeTrue(isCMSStandalone());

		_testPortalLicenseExpired(this::deployCMSPortalLicense);
	}

	@Test
	public void testEnterprisePortalLicenseExpired() throws Exception {
		Assume.assumeFalse(isCMSStandalone());

		_testPortalLicenseExpired(this::deployEnterprisePortalLicense);
	}

	@Test
	public void testFreeTierPortalLicenseExpired() throws Exception {
		Assume.assumeFalse(isCMSStandalone());

		_testPortalLicenseExpired(
			BaseLicenseTestCase::deployFreeTierPortalLicense);
	}

	private void _testPortalLicenseExpired(
			UnsafeConsumer<Long, Exception> deployLicenseUnsafeConsumer)
		throws Exception {

		assertLicensePropertiesNotExisted(getPortalProductId());

		assertPortalLicenseNotRegistered();

		deployLicenseUnsafeConsumer.accept(GRACE_PERIOD + _VALIDITY_PERIOD);

		assertLicensePropertiesExisted(getPortalProductId());

		assertPortalLicenseRegistered();

		Thread.sleep(_VALIDITY_PERIOD);

		assertLicensePropertiesExisted(getPortalProductId());

		assertPortalLicenseExpired();
	}

	private static final long _VALIDITY_PERIOD = 15 * Time.SECOND;

	private static SafeCloseable _disableKeyValidatorSafeCloseable;
	private static SafeCloseable _setVersionSafeCloseable;

	private SafeCloseable _safeCloseable;

}
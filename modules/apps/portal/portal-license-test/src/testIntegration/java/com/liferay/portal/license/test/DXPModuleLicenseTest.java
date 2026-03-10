/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.license.test.util.LicenseTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Kevin Lee
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class DXPModuleLicenseTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		Assume.assumeTrue(LicenseTestUtil.isReleaseBundle());
		Assume.assumeTrue(PropsValues.SETUP_WIZARD_ENABLED);
	}

	@After
	public void tearDown() throws Exception {
		LicenseTestUtil.removeAllLicenseBinaryFiles();
		LicenseTestUtil.resetLifecycleAction();
	}

	@Test
	public void testFreeLicense() throws Exception {
		Map<String, String> licenseProperties =
			LicenseTestUtil.getPortalLicenseProperties();

		Assert.assertTrue(
			licenseProperties.toString(), licenseProperties.isEmpty());

		Set<String> currentBundleNames =
			LicenseTestUtil.getCurrentBundleNames();

		Assert.assertTrue(
			currentBundleNames.contains(_ENTERPRISE_APP_SYMBOLIC_NAME));
		Assert.assertTrue(
			currentBundleNames.contains(_DXP_MODULE_SYMBOLIC_NAME));

		String response = LicenseTestUtil.hitHomePage("localhost", 8080);

		Assert.assertTrue(
			response.contains("This instance is not registered."));

		currentBundleNames = LicenseTestUtil.getCurrentBundleNames();

		Assert.assertTrue(
			currentBundleNames.contains(_ENTERPRISE_APP_SYMBOLIC_NAME));
		Assert.assertTrue(
			currentBundleNames.contains(_DXP_MODULE_SYMBOLIC_NAME));

		long now = System.currentTimeMillis();

		LicenseTestUtil.deployFreeTierLicenseContent(
			new Date(now), new Date(now + Time.HOUR));

		licenseProperties = LicenseTestUtil.getPortalLicenseProperties();

		Assert.assertFalse(
			licenseProperties.toString(), licenseProperties.isEmpty());

		response = LicenseTestUtil.hitHomePage("localhost", 8080);

		Assert.assertTrue(response.contains("setup_wizard"));

		currentBundleNames = LicenseTestUtil.getCurrentBundleNames();

		Assert.assertFalse(
			currentBundleNames.contains(_ENTERPRISE_APP_SYMBOLIC_NAME));
		Assert.assertFalse(
			currentBundleNames.contains(_DXP_MODULE_SYMBOLIC_NAME));

		LicenseTestUtil.removeFreeTierLicense();

		licenseProperties = LicenseTestUtil.getPortalLicenseProperties();

		Assert.assertTrue(
			licenseProperties.toString(), licenseProperties.isEmpty());
	}

	@Test
	public void testFreeLicenseManualDeploy() throws Exception {
		Map<String, String> licenseProperties =
			LicenseTestUtil.getPortalLicenseProperties();

		Assert.assertTrue(
			licenseProperties.toString(), licenseProperties.isEmpty());

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Bundle dxpBundle = null;
		Bundle enterpriseAppBundle = null;

		for (Bundle bundle : bundleContext.getBundles()) {
			if (Objects.equals(
					bundle.getSymbolicName(), _DXP_MODULE_SYMBOLIC_NAME)) {

				dxpBundle = bundle;
			}

			if (Objects.equals(
					bundle.getSymbolicName(), _ENTERPRISE_APP_SYMBOLIC_NAME)) {

				enterpriseAppBundle = bundle;
			}
		}

		Assert.assertNotNull(dxpBundle);
		Assert.assertNotNull(enterpriseAppBundle);

		Assert.assertEquals(Bundle.ACTIVE, dxpBundle.getState());
		Assert.assertEquals(Bundle.ACTIVE, enterpriseAppBundle.getState());

		String response = LicenseTestUtil.hitHomePage("localhost", 8080);

		Assert.assertTrue(
			response.contains("This instance is not registered."));

		Assert.assertEquals(Bundle.ACTIVE, dxpBundle.getState());
		Assert.assertEquals(Bundle.ACTIVE, enterpriseAppBundle.getState());

		long now = System.currentTimeMillis();

		LicenseTestUtil.deployFreeTierLicenseContent(
			new Date(now), new Date(now + Time.HOUR));

		licenseProperties = LicenseTestUtil.getPortalLicenseProperties();

		Assert.assertFalse(
			licenseProperties.toString(), licenseProperties.isEmpty());

		response = LicenseTestUtil.hitHomePage("localhost", 8080);

		Assert.assertTrue(response.contains("setup_wizard"));

		Assert.assertEquals(Bundle.UNINSTALLED, dxpBundle.getState());
		Assert.assertEquals(Bundle.UNINSTALLED, enterpriseAppBundle.getState());

		dxpBundle = bundleContext.installBundle(dxpBundle.getLocation());
		enterpriseAppBundle = bundleContext.installBundle(
			enterpriseAppBundle.getLocation());

		try {
			dxpBundle.start();
			enterpriseAppBundle.start();

			Assert.assertEquals(Bundle.ACTIVE, dxpBundle.getState());
			Assert.assertEquals(Bundle.ACTIVE, enterpriseAppBundle.getState());

			Thread.sleep(Time.MINUTE * 5);

			response = LicenseTestUtil.hitHomePage("localhost", 8080);

			Assert.assertTrue(response.contains("This instance is invalid."));
		}
		finally {
			dxpBundle.uninstall();
			enterpriseAppBundle.uninstall();
		}
	}

	private static final String _DXP_MODULE_SYMBOLIC_NAME =
		"com.liferay.saml.persistence.api";

	private static final String _ENTERPRISE_APP_SYMBOLIC_NAME =
		"com.liferay.portal.license.enterprise.app";

}
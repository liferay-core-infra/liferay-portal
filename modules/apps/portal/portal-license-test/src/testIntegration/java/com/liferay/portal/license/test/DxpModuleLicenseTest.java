/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.license.test.util.LicenseTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Map;
import java.util.Set;

import net.bytebuddy.agent.builder.ResettableClassFileTransformer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class DxpModuleLicenseTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		Assume.assumeTrue(LicenseTestUtil.isReleaseBundle());
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		_disableKeyValidatorResettableClassFileTransformer =
			LicenseTestUtil.disableKeyValidator();
		_setVersionResettableClassFileTransformer =
			LicenseTestUtil.setVersionDisplayName("2026.Q1.0");
	}

	@AfterClass
	public static void tearDownClass() {
		LicenseTestUtil.resetRransformer(
			_disableKeyValidatorResettableClassFileTransformer);
		LicenseTestUtil.resetRransformer(
			_setVersionResettableClassFileTransformer);
	}

	@After
	public void tearDown() throws Exception {
		LicenseTestUtil.removeAllLicenseBinaryFiles();
		LicenseTestUtil.resetLifecycleAction();
	}

	@Test
	public void test() throws Exception {
		Map<String, String> licenseProperties =
			LicenseTestUtil.getPortalLicenseProperties();

		Assert.assertTrue(
			licenseProperties.toString(), licenseProperties.isEmpty());

		Set<String> currentBundleNames =
			LicenseTestUtil.getCurrentBundleNames();

		Assert.assertTrue(
			currentBundleNames.contains(_ENTERPRISE_APP_SYMBOLIC_NAME));
		Assert.assertTrue(
			currentBundleNames.contains(_DXP_ONLY_MODULE_SYMBOLIC_NAME));

		String response = LicenseTestUtil.hitHomePage("localhost", 8080);

		Assert.assertTrue(
			response.contains("This instance is not registered."));

		currentBundleNames = LicenseTestUtil.getCurrentBundleNames();

		Assert.assertTrue(
			currentBundleNames.contains(_ENTERPRISE_APP_SYMBOLIC_NAME));
		Assert.assertTrue(
			currentBundleNames.contains(_DXP_ONLY_MODULE_SYMBOLIC_NAME));

		LicenseTestUtil.deployFreeTierLicenseContent(
			"Monday, February 17, 2026 02:00:00 AM GMT",
			"Monday, March 1, 2027 12:00:00 AM GMT");

		licenseProperties = LicenseTestUtil.getPortalLicenseProperties();

		Assert.assertFalse(
			licenseProperties.toString(), licenseProperties.isEmpty());

		response = LicenseTestUtil.hitHomePage("localhost", 8080);

		Assert.assertTrue(response.contains("setup_wizard"));

		currentBundleNames = LicenseTestUtil.getCurrentBundleNames();

		Assert.assertFalse(
			currentBundleNames.contains(_ENTERPRISE_APP_SYMBOLIC_NAME));
		Assert.assertFalse(
			currentBundleNames.contains(_DXP_ONLY_MODULE_SYMBOLIC_NAME));

		LicenseTestUtil.removeFreeTierLicense();

		licenseProperties = LicenseTestUtil.getPortalLicenseProperties();

		Assert.assertTrue(
			licenseProperties.toString(), licenseProperties.isEmpty());
	}

	private static final String _DXP_ONLY_MODULE_SYMBOLIC_NAME =
		"com.liferay.saml.api";

	private static final String _ENTERPRISE_APP_SYMBOLIC_NAME =
		"com.liferay.portal.license.enterprise.app";

	private static ResettableClassFileTransformer
		_disableKeyValidatorResettableClassFileTransformer;
	private static ResettableClassFileTransformer
		_setVersionResettableClassFileTransformer;

}
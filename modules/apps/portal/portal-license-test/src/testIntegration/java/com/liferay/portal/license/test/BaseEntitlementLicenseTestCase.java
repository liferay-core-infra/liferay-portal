/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.license.util.App;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

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

	protected abstract App[] getApps();

	private static SafeCloseable _disableValidateSafeCloseable;
	private static SafeCloseable _entitlementReleaseSafeCloseable;

	private SafeCloseable _resetLicenseDataSafeCloseable;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;

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
 * @author Kevin Lee
 */
@RunWith(Arquillian.class)
public class EnterpriseDatabaseTest extends BaseLicenseTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		Assume.assumeTrue(isReleaseBundle());
	}

	@BeforeClass
	public static void setUpClass() {
		_disableKeyValidatorResettableClassFileTransformer = disableValidate();
		_setVersionResettableClassFileTransformer = setVersion("2026.Q1.0 LTS");
	}

	@AfterClass
	public static void tearDownClass() {
		resetClassFileTransformer(
			_disableKeyValidatorResettableClassFileTransformer);
		resetClassFileTransformer(_setVersionResettableClassFileTransformer);
	}

	@After
	public void tearDown() throws Exception {
		resetLicenseData();
		resetLifecycleAction();
	}

	@Test
	public void testEnterpriseLicense() throws Exception {
		DB db = DBManagerUtil.getDB();

		for (DBType dbType :
				ArrayUtil.append(_ENTERPRISE_DB_TYPES, _FREE_TIER_DB_TYPES)) {

			try (AutoCloseable autoCloseable =
					ReflectionTestUtil.setFieldValueWithAutoCloseable(
						db, "_dbType", dbType)) {

				deployEnterprisePortalLicense();

				assertPortalLicenseRegistered();
			}
			finally {
				resetLicenseData();
			}
		}
	}

	@Test
	public void testEnterpriseLicenseSetupWizard() throws Exception {
		Assume.assumeTrue(PropsValues.SETUP_WIZARD_ENABLED);

		deployEnterprisePortalLicense();

		assertPortalLicenseRegistered();

		String response = hitHomePage("localhost", 8080);

		for (DBType dbType :
				ArrayUtil.append(_ENTERPRISE_DB_TYPES, _FREE_TIER_DB_TYPES)) {

			Assert.assertTrue(
				response.contains("value=\"" + dbType.getName() + "\""));
		}
	}

	@Test
	public void testFreeTierLicense() throws Exception {
		DB db = DBManagerUtil.getDB();

		for (DBType freeTierDBType : _FREE_TIER_DB_TYPES) {
			try (AutoCloseable autoCloseable =
					ReflectionTestUtil.setFieldValueWithAutoCloseable(
						db, "_dbType", freeTierDBType)) {

				deployFreeTierPortalLicense();

				assertPortalLicenseRegistered();
			}
			finally {
				resetLicenseData();
			}
		}

		for (DBType enterpriseDBType : _ENTERPRISE_DB_TYPES) {
			try (AutoCloseable autoCloseable =
					ReflectionTestUtil.setFieldValueWithAutoCloseable(
						db, "_dbType", enterpriseDBType)) {

				deployFreeTierPortalLicense();

				_assertDatabaseNotSupported(enterpriseDBType);
			}
			finally {
				resetLicenseData();
			}
		}

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					db, "_dbType", _FREE_TIER_DB_TYPES[0])) {

			deployFreeTierPortalLicense();

			assertPortalLicenseRegistered();
		}

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					db, "_dbType", _ENTERPRISE_DB_TYPES[0])) {

			_assertDatabaseNotSupported(_ENTERPRISE_DB_TYPES[0]);
		}
	}

	@Test
	public void testFreeTierLicenseSetupWizard() throws Exception {
		Assume.assumeTrue(PropsValues.SETUP_WIZARD_ENABLED);

		deployFreeTierPortalLicense();

		assertPortalLicenseRegistered();

		String response = hitHomePage("localhost", 8080);

		for (DBType freeTierDBType : _FREE_TIER_DB_TYPES) {
			Assert.assertTrue(
				response.contains(
					"value=\"" + freeTierDBType.getName() + "\""));
		}

		for (DBType enterpriseDBType : _ENTERPRISE_DB_TYPES) {
			Assert.assertFalse(
				response.contains(
					"value=\"" + enterpriseDBType.getName() + "\""));
		}

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					DBManagerUtil.getDB(), "_dbType",
					_ENTERPRISE_DB_TYPES[0])) {

			_assertDatabaseNotSupported(_ENTERPRISE_DB_TYPES[0]);
		}
	}

	@Test
	public void testInvalidLicenseSetupWizard() throws Exception {
		Assume.assumeTrue(PropsValues.SETUP_WIZARD_ENABLED);

		assertPortalLicenseNotRegistered();

		deployFreeTierPortalLicense();

		assertPortalLicenseRegistered();

		String response = hitHomePage("localhost", 8080);

		Assert.assertTrue(response.contains("setup_wizard"));

		resetLicenseData();

		assertPortalLicenseNotRegistered();

		deployFreeTierPortalLicense(
			Collections.emptyList(), StringPool.BLANK,
			System.currentTimeMillis());

		assertPortalLicenseInvalid();

		response = hitHomePage("localhost", 8080);

		Assert.assertFalse(response.contains("setup_wizard"));
	}

	private void _assertDatabaseNotSupported(DBType dbType) throws Exception {
		String response = hitHomePage("localhost", 8080);

		Assert.assertTrue(
			response.contains(
				"Database type " + dbType.getName() + " is not supported."));
	}

	private static final DBType[] _ENTERPRISE_DB_TYPES = {
		DBType.DB2, DBType.ORACLE, DBType.SQLSERVER
	};

	private static final DBType[] _FREE_TIER_DB_TYPES = {
		DBType.HYPERSONIC, DBType.MARIADB, DBType.MYSQL, DBType.POSTGRESQL
	};

	private static ResettableClassFileTransformer
		_disableKeyValidatorResettableClassFileTransformer;
	private static ResettableClassFileTransformer
		_setVersionResettableClassFileTransformer;

}
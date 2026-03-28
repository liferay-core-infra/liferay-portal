/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

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
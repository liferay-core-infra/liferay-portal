/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.model.Counter;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.v7_4_x.UpgradeVirtualHostDBPartition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
public class UpgradeVirtualHostDBPartitionTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgradeRetainsVirtualHost() throws Exception {
		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		String virtualHostname = company.getVirtualHostname();

		UpgradeProcess upgradeProcess = new UpgradeVirtualHostDBPartition();

		upgradeProcess.upgrade();

		Assert.assertNotNull(
			_virtualHostLocalService.fetchVirtualHost(virtualHostname));
	}

	@Test
	public void testUpgradeUpdatesCounter() throws Exception {
		Assume.assumeTrue(PropsValues.DATABASE_PARTITION_ENABLED);

		long companyId = TestPropsValues.getCompanyId();

		Assume.assumeTrue(
			companyId != PortalInstancePool.getDefaultCompanyId());

		long virtualHostId = _getCounterCurrentId(companyId) + 100000;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId);

			Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"insert into VirtualHost (mvccVersion, ctCollectionId, " +
					"virtualHostId, companyId, layoutSetId, hostname) values " +
						"(0, 0, ?, ?, 0, ?)")) {

			preparedStatement.setLong(1, virtualHostId);
			preparedStatement.setLong(2, companyId);
			preparedStatement.setString(
				3, StringUtil.toLowerCase(RandomTestUtil.randomString()));

			preparedStatement.executeUpdate();
		}

		try {
			UpgradeProcess upgradeProcess = new UpgradeVirtualHostDBPartition();

			upgradeProcess.upgrade();

			Assert.assertTrue(_getCounterCurrentId(companyId) >= virtualHostId);
		}
		finally {
			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId);

				Connection connection = DataAccess.getConnection();

				PreparedStatement preparedStatement =
					connection.prepareStatement(
						"delete from VirtualHost where virtualHostId = ?")) {

				preparedStatement.setLong(1, virtualHostId);

				preparedStatement.executeUpdate();
			}

			EntityCacheUtil.clearCache();
			FinderCacheUtil.clearCache();
		}
	}

	private long _getCounterCurrentId(long companyId) throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId);

			Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select currentId from Counter where name = ?")) {

			preparedStatement.setString(1, Counter.class.getName());

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				Assert.assertTrue(resultSet.next());

				return resultSet.getLong("currentId");
			}
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private VirtualHostLocalService _virtualHostLocalService;

}
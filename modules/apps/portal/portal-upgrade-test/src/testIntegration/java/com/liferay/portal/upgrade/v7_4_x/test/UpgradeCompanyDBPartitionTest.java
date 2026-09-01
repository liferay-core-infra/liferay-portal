/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.v7_4_x.UpgradeCompanyDBPartition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
public class UpgradeCompanyDBPartitionTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgradeRepairsCompanyTable() throws Exception {
		Assume.assumeTrue(PropsValues.DATABASE_PARTITION_ENABLED);

		long companyId = TestPropsValues.getCompanyId();

		Assert.assertNotEquals(
			PortalInstancePool.getDefaultCompanyId(), companyId);

		String partitionName = DBPartitionUtil.getPartitionName(companyId);

		try {
			_executeUpdate("drop table " + partitionName + ".Company");

			_runUpgrade();

			_assertOnlyCompanyRow(companyId);

			_executeUpdate(
				StringBundler.concat(
					"delete from ", partitionName,
					".Company where companyId = ", companyId));

			_runUpgrade();

			_assertOnlyCompanyRow(companyId);
		}
		finally {
			_restoreCompanyTable(companyId);
		}
	}

	@Test
	public void testUpgradeReplacesCompanyView() throws Exception {
		Assume.assumeTrue(PropsValues.DATABASE_PARTITION_ENABLED);

		long companyId = TestPropsValues.getCompanyId();

		Assert.assertNotEquals(
			PortalInstancePool.getDefaultCompanyId(), companyId);

		String partitionName = DBPartitionUtil.getPartitionName(companyId);

		try {
			_executeUpdate("drop table " + partitionName + ".Company");
			_executeUpdate(
				StringBundler.concat(
					"create or replace view ", partitionName,
					".Company as select * from ",
					DBPartitionUtil.getPartitionName(
						PortalInstancePool.getDefaultCompanyId()),
					".Company"));

			_runUpgrade();

			_assertOnlyCompanyRow(companyId);
		}
		finally {
			_restoreCompanyTable(companyId);
		}
	}

	@Test
	public void testUpgradeRetainsCompany() throws Exception {
		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Map<String, Object> modelAttributes = company.getModelAttributes();

		int companiesCount = _companyLocalService.getCompaniesCount();

		_runUpgrade();

		_multiVMPool.clear();

		company = _companyLocalService.getCompany(company.getCompanyId());

		Assert.assertEquals(modelAttributes, company.getModelAttributes());

		Assert.assertEquals(
			companiesCount, _companyLocalService.getCompaniesCount());
	}

	private void _assertOnlyCompanyRow(long companyId) throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId);

			Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select companyId from Company");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			List<Long> companyIds = new ArrayList<>();

			while (resultSet.next()) {
				companyIds.add(resultSet.getLong("companyId"));
			}

			Assert.assertEquals(
				Collections.singletonList(companyId), companyIds);
		}
	}

	private void _executeUpdate(String sql) throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId());

			Connection connection = DataAccess.getConnection();

			Statement statement = connection.createStatement()) {

			statement.executeUpdate(sql);
		}
	}

	private void _restoreCompanyTable(long companyId) throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId());

			Connection connection = DataAccess.getConnection()) {

			DBPartitionUtil.replaceByTable(
				connection, companyId, "Company", true,
				" where companyId = " + companyId);
		}

		EntityCacheUtil.clearCache();
		FinderCacheUtil.clearCache();
	}

	private void _runUpgrade() throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId())) {

			UpgradeProcess upgradeProcess = new UpgradeCompanyDBPartition();

			upgradeProcess.upgrade();
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

}
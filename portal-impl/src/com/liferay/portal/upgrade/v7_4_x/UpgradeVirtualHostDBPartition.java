/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.counter.kernel.model.Counter;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PropsValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author István András Dézsi
 */
public class UpgradeVirtualHostDBPartition extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		if (!CompanyThreadLocal.isDefaultCompany()) {
			return;
		}

		long defaultCompanyId = PortalInstancePool.getDefaultCompanyId();

		for (long companyId : PortalInstancePool.getCompanyIds()) {
			DBPartitionUtil.replaceByTable(
				connection, companyId, "VirtualHost", true,
				" where companyId = " + companyId);

			if (companyId != defaultCompanyId) {
				_updateCounter(companyId);
			}
		}

		runSQL(
			"delete from VirtualHost where companyId != " + defaultCompanyId);
	}

	@Override
	protected boolean isSkipUpgradeProcess() {
		return !PropsValues.DATABASE_PARTITION_ENABLED;
	}

	private void _updateCounter(long companyId) throws Exception {
		String partitionName = DBPartitionUtil.getPartitionName(companyId);

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select max(virtualHostId) as maxVirtualHostId from ",
					partitionName, ".VirtualHost"));

			ResultSet resultSet = preparedStatement1.executeQuery()) {

			if (!resultSet.next()) {
				return;
			}

			long maxVirtualHostId = resultSet.getLong("maxVirtualHostId");

			if (maxVirtualHostId == 0) {
				return;
			}

			try (PreparedStatement preparedStatement2 =
					connection.prepareStatement(
						StringBundler.concat(
							"update ", partitionName,
							".Counter set currentId = ? where name = ? and ",
							"currentId < ?"))) {

				preparedStatement2.setLong(1, maxVirtualHostId);
				preparedStatement2.setString(2, Counter.class.getName());
				preparedStatement2.setLong(3, maxVirtualHostId);

				preparedStatement2.executeUpdate();
			}
		}
	}

}
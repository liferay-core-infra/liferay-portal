/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PropsValues;

/**
 * @author István András Dézsi
 */
public class UpgradeCompanyDBPartition extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		if (!CompanyThreadLocal.isDefaultCompany()) {
			return;
		}

		for (long companyId : PortalInstancePool.getCompanyIds()) {
			DBPartitionUtil.replaceByTable(
				connection, companyId, "Company", true,
				" where companyId = " + companyId);
		}
	}

	@Override
	protected boolean isSkipUpgradeProcess() {
		return !PropsValues.DATABASE_PARTITION_ENABLED;
	}

}
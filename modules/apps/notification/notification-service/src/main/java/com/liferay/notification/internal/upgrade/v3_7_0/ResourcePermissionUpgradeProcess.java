/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.upgrade.v3_7_0;

import com.liferay.notification.constants.NotificationConstants;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.upgrade.BasePortletIdUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.upgrade.v7_0_0.UpgradeKernelPackage;

/**
 * @author Murilo Stodolni
 */
public class ResourcePermissionUpgradeProcess
	extends BasePortletIdUpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		UpgradeKernelPackage upgradeKernelPackage = new UpgradeKernelPackage() {

			@Override
			protected void doUpgrade() throws UpgradeException {
				try {
					upgradeTable(
						"ResourcePermission", "name", getClassNames(),
						WildcardMode.LEADING, true);
					upgradeTable(
						"ResourcePermission", "primKey", getResourceNames(),
						WildcardMode.LEADING, true);
				}
				catch (Exception exception) {
					throw new UpgradeException(exception);
				}
			}

		};

		upgradeKernelPackage.upgrade();

		updateResourceAction(_RESOURCE_NAMES[0], _RESOURCE_NAMES[1]);
	}

	private static final String[] _RESOURCE_NAMES = {
		"com.liferay.notification",
		NotificationConstants.RESOURCE_NAME_NOTIFICATION_TEMPLATE
	};

}
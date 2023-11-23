/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.upgrade.v7_0_0.UpgradeKernelPackage;

/**
 * @author Lily Chi
 */
public class UpgradeExpandoPackages extends UpgradeKernelPackage {

	@Override
	protected void doUpgrade() throws UpgradeException {
		try {
			upgradeTable(
				"ClassName_", "value", _CLASS_NAMES, WildcardMode.SURROUND,
				true);
			upgradeTable(
				"ResourceAction", "name", _CLASS_NAMES, WildcardMode.SURROUND,
				true);
			upgradeTable(
				"ResourcePermission", "name", _CLASS_NAMES,
				WildcardMode.SURROUND, true);
			upgradeTable(
				"ResourcePermission", "primKey", _CLASS_NAMES,
				WildcardMode.SURROUND, true);
		}
		catch (Exception exception) {
			throw new UpgradeException(exception);
		}
	}

	private static final String[][] _CLASS_NAMES = {
		{"com.liferay.expando.kernel.model.", "com.liferay.expando.model."}
	};

}
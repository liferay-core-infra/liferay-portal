/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.internal.upgrade.v1_0_0;

import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.upgrade.v7_0_0.UpgradeKernelPackage;

/**
 * @author Lily Chi
 */
public class ClassNamesUpgradeProcess extends UpgradeKernelPackage {

	@Override
	protected void doUpgrade() throws UpgradeException {
		super.doUpgrade();

		try {
			upgradeTable(
				"ResourcePermission", "primKey", _CLASS_NAMES,
				WildcardMode.SURROUND, true);
		}
		catch (Exception exception) {
			throw new UpgradeException(exception);
		}
	}

	@Override
	protected String[][] getClassNames() {
		return _CLASS_NAMES;
	}

	@Override
	protected String[][] getResourceNames() {
		return _RESOURCE_NAMES;
	}

	private static final String[][] _CLASS_NAMES = {
		{"com.liferay.expando.kernel.model.", "com.liferay.expando.model."}
	};

	private static final String[][] _RESOURCE_NAMES = new String[0][0];

}
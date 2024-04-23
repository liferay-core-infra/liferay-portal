/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

/**
 * @author Tito Kenzo
 */
public class UpgradePortalPreferenceValueKeySize extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		int sessionClicksMaxSizeTerms = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.SESSION_CLICKS_MAX_SIZE_TERMS));

		alterColumnType(
			"PortalPreferenceValue", "key_",
			"VARCHAR(" + sessionClicksMaxSizeTerms + ") null");
	}

}
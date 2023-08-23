/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringBundler;

/**
 * @author Lily Chi
 */
public class UpgradeClassName extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		runSQL(
			StringBundler.concat(
				_UPDATE_SQL_PREFIX, _NEW_PACKAGE_PREFIX, "ExpandoColumn",
				_SQL_CONDITION_PREFIX, _OLD_PACKAGE_PREFIX, "ExpandoColumn'"));
		runSQL(
			StringBundler.concat(
				_UPDATE_SQL_PREFIX, _NEW_PACKAGE_PREFIX, "ExpandoRow",
				_SQL_CONDITION_PREFIX, _OLD_PACKAGE_PREFIX, "ExpandoRow'"));

		runSQL(
			StringBundler.concat(
				_UPDATE_SQL_PREFIX, _NEW_PACKAGE_PREFIX, "ExpandoTable",
				_SQL_CONDITION_PREFIX, _OLD_PACKAGE_PREFIX, "ExpandoTable'"));

		runSQL(
			StringBundler.concat(
				_UPDATE_SQL_PREFIX, _NEW_PACKAGE_PREFIX, "ExpandoValue",
				_SQL_CONDITION_PREFIX, _OLD_PACKAGE_PREFIX, "ExpandoValue'"));

		runSQL(
			StringBundler.concat(
				_UPDATE_SQL_PREFIX, _NEW_PACKAGE_PREFIX,
				"adapter.StagedExpandoColumn", _SQL_CONDITION_PREFIX,
				_OLD_PACKAGE_PREFIX, "adapter.StagedExpandoColumn'"));

		runSQL(
			StringBundler.concat(
				_UPDATE_SQL_PREFIX, _NEW_PACKAGE_PREFIX,
				"adapter.StagedExpandoTable", _SQL_CONDITION_PREFIX,
				_OLD_PACKAGE_PREFIX, "adapter.StagedExpandoTable'"));
	}

	private static final String _NEW_PACKAGE_PREFIX =
		"com.liferay.expando.model.";

	private static final String _OLD_PACKAGE_PREFIX =
		"com.liferay.expando.kernel.model.";

	private static final String _SQL_CONDITION_PREFIX = "' where value='";

	private static final String _UPDATE_SQL_PREFIX =
		"update ClassName_ set value='";

}
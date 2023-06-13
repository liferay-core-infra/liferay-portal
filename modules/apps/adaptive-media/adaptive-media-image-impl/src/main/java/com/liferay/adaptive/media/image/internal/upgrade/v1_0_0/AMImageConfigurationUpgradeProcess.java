/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.adaptive.media.image.internal.upgrade.v1_0_0;

import com.liferay.adaptive.media.image.internal.configuration.AMImageConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.io.IOException;

import java.sql.SQLException;

import java.util.Dictionary;

import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Julius Lee
 */
public class AMImageConfigurationUpgradeProcess extends UpgradeProcess {

	public AMImageConfigurationUpgradeProcess(
		ConfigurationAdmin configurationAdmin) {

		_configurationAdmin = configurationAdmin;
	}

	@Override
	protected void doUpgrade()
		throws InvalidSyntaxException, IOException, SQLException {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			StringBundler.concat(
				"(", Constants.SERVICE_PID, "=",
				AMImageConfiguration.class.getName(), ")"));

		if (configurations == null) {
			return;
		}

		for (Configuration configuration : configurations) {
			Dictionary<String, Object> dictionary =
				configuration.getProperties();

			dictionary.remove("imageMaxSize");

			configuration.updateIfDifferent(dictionary);
		}

		File file = new File(
			PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR,
			AMImageConfiguration.class.getName() + ".config");

		if (file.exists() && (file.length() == 0)) {
			FileUtil.delete(file);

			DB db = DBManagerUtil.getDB();

			db.runSQL(
				"delete from Configuration_ where configurationId like '" +
					AMImageConfiguration.class.getName() + "%'");
		}
	}

	private final ConfigurationAdmin _configurationAdmin;

}
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

package com.liferay.journal.web.internal.upgrade.v1_2_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Julius Lee
 */
public class FFJournalAutoSaveDraftConfigurationRemovalUpgradeProcess
	extends UpgradeProcess {

	public FFJournalAutoSaveDraftConfigurationRemovalUpgradeProcess(
		ConfigurationAdmin configurationAdmin) {

		_configurationAdmin = configurationAdmin;
	}

	@Override
	protected void doUpgrade() throws Exception {
		String configurationId =
			"com.liferay.journal.web.internal.configuration." +
				"FFJournalAutoSaveDraftConfiguration";

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			StringBundler.concat(
				"(", Constants.SERVICE_PID, "=", configurationId, ")"));

		if (configurations == null) {
			return;
		}

		for (Configuration configuration : configurations) {
			configuration.delete();
		}
	}

	private final ConfigurationAdmin _configurationAdmin;

}
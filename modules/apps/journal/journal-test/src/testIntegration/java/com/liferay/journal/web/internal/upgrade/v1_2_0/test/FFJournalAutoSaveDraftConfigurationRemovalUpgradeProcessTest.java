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

package com.liferay.journal.web.internal.upgrade.v1_2_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Julius Lee
 */
@RunWith(Arquillian.class)
public class FFJournalAutoSaveDraftConfigurationRemovalUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgradeProcessWithoutTestConfiguration() throws Exception {
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			"(service.pid=" + _CLASS_NAME + "*)");

		if (configurations != null) {
			return;
		}

		_runUpgrade();
	}

	@Test
	public void testUpgradeProcessWithTestConfiguration() throws Exception {
		ConfigurationTestUtil.saveConfiguration(
			_CONFIGURATION_NAME + "~Test", new HashMapDictionary<>());

		_runUpgrade();

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			StringBundler.concat(
				"(", Constants.SERVICE_PID, "=", _CONFIGURATION_NAME,
				"~Test*)"));

		Assert.assertTrue(configurations == null);
	}

	private UpgradeProcess _getUpgradeProcess() {
		UpgradeProcess[] upgradeProcesses = new UpgradeProcess[1];

		_upgradeStepRegistrator.register(
			(fromSchemaVersionString, toSchemaVersionString, upgradeSteps) -> {
				for (UpgradeStep upgradeStep : upgradeSteps) {
					Class<? extends UpgradeStep> clazz = upgradeStep.getClass();

					if (Objects.equals(clazz.getName(), _CLASS_NAME)) {
						upgradeProcesses[0] = (UpgradeProcess)upgradeStep;

						break;
					}
				}
			});

		return upgradeProcesses[0];
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = _getUpgradeProcess();

		upgradeProcess.upgrade();
	}

	private static final String _CLASS_NAME =
		"com.liferay.journal.web.internal.upgrade.v1_2_0." +
			"FFJournalAutoSaveDraftConfigurationRemovalUpgradeProcess";

	private static final String _CONFIGURATION_NAME =
		"com.liferay.journal.web.internal.configuration." +
			"FFJournalAutoSaveDraftConfiguration";

	@Inject(
		filter = "(&(component.name=com.liferay.journal.web.internal.upgrade.registry.JournalWebUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

}
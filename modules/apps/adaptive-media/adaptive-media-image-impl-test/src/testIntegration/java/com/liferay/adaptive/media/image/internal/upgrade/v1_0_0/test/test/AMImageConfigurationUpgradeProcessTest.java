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

package com.liferay.adaptive.media.image.internal.upgrade.v1_0_0.test.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.util.Objects;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Julius Lee
 */
@RunWith(Arquillian.class)
public class AMImageConfigurationUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		_upgradeStepRegistrator.register(
			(fromSchemaVersionString, toSchemaVersionString, upgradeSteps) -> {
				for (UpgradeStep upgradeStep : upgradeSteps) {
					Class<?> clazz = upgradeStep.getClass();

					if (Objects.equals(
							clazz.getName(),
							"com.liferay.adaptive.media.image.internal." +
								"upgrade.v1_0_0." +
									"AMImageConfigurationUpgradeProcess")) {

						_upgradeProcess = (UpgradeProcess)upgradeStep;
					}
				}
			});
	}

	@Test
	public void testRunningUpgrade() throws Exception {

		// 		String configurationPid =

		//			"com.liferay.adaptive.media.image.internal.configuration." +
		//				"AMImageConfiguration";

		//
		//		Configuration configuration = _configurationAdmin.getConfiguration(configurationPid, StringPool.QUESTION);

		//
		//		configuration.update(HashMapDictionaryBuilder.<String, Object>put(
		//				"imageMaxSize", "L\"" + RandomTestUtil.randomLong() + "\""
		//			).put(
		//				RandomTestUtil.randomString(), RandomTestUtil.randomString()
		//			).build());

		_upgradeProcess.upgrade();

		//		configuration = _configurationAdmin.getConfiguration(
		//			configurationPid, StringPool.QUESTION);
		//
		//		Dictionary<String, Object> dictionary = configuration.getProperties();

		//
		//		Enumeration<String> enumeration = dictionary.keys();
		//
		//		while (enumeration.hasMoreElements()) {
		//			Assert.assertNotEquals("imageMaxSize", enumeration.nextElement());
		//		}
		//
		//		configuration.delete();
	}

	private static UpgradeProcess _upgradeProcess;

	@Inject(
		filter = "component.name=com.liferay.adaptive.media.image.internal.upgrade.registry.AMImageUpgradeStepRegistrator"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

}
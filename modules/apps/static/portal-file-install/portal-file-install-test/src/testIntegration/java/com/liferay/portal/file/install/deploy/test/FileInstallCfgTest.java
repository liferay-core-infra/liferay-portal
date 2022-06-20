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

package com.liferay.portal.file.install.deploy.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Dictionary;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedService;

/**
 * @author Matthew Tambara
 */
@RunWith(Arquillian.class)
public class FileInstallCfgTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(FileInstallCfgTest.class);

		_bundleContext = bundle.getBundleContext();

		_originalModuleFrameworkFileInstallCfgEnabled =
			ReflectionTestUtil.getAndSetFieldValue(
				PropsValues.class, "MODULE_FRAMEWORK_FILE_INSTALL_CFG_ENABLED",
				true);
	}

	@AfterClass
	public static void tearDownClass() {
		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "MODULE_FRAMEWORK_FILE_INSTALL_CFG_ENABLED",
			_originalModuleFrameworkFileInstallCfgEnabled);
	}

	@After
	public void tearDown() throws Exception {
		_deleteConfiguration();
	}

	@Test
	public void testConfiguration() throws Exception {
		String configurationPid = _CONFIGURATION_PID_PREFIX.concat(
			".testConfiguration");

		_configurationPath = Paths.get(
			PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR,
			configurationPid.concat(".cfg"));

		String testKey = "testKey";
		String testValue = "testValue";

		_configuration = _createConfiguration(
			configurationPid, testKey + StringPool.EQUAL + testValue);

		Dictionary<String, Object> dictionary = _configuration.getProperties();

		Assert.assertEquals("testValue", dictionary.get(testKey));
	}

	@Test
	public void testConfigurationDeprecatedFileExtension() throws Exception {
		Boolean originalModuleFrameworkFileInstallCfgEnabled =
			ReflectionTestUtil.getAndSetFieldValue(
				PropsValues.class, "MODULE_FRAMEWORK_FILE_INSTALL_CFG_ENABLED",
				false);

		String configurationPid = _CONFIGURATION_PID_PREFIX.concat(
			".testDummy");
		String configurationPidDeprecated = _CONFIGURATION_PID_PREFIX.concat(
			".testConfigurationDeprecatedFileExtension");

		String content = "testKey=\"testValue\"";
		String contentDeprecated = "testKeyDeprecated=testValueDeprecated";

		_configurationPath = Paths.get(
			PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR,
			configurationPid.concat(".config"));

		Path configPathDeprecated = Paths.get(
			PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR,
			configurationPidDeprecated.concat(".cfg"));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.file.install.internal.configuration." +
					"ConfigurationFileInstaller",
				LoggerTestUtil.WARN)) {

			Files.write(configPathDeprecated, contentDeprecated.getBytes());

			_configuration = _createConfiguration(configurationPid, content);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Unable to install .cfg file ", configPathDeprecated,
					", please use .config file instead."),
				logEntry.getMessage());

			Configuration configurationDeprecated =
				_configurationAdmin.getConfiguration(
					configurationPidDeprecated, StringPool.QUESTION);

			Assert.assertNull(configurationDeprecated.getProperties());
		}
		finally {
			Files.deleteIfExists(configPathDeprecated);

			ReflectionTestUtil.setFieldValue(
				PropsValues.class, "MODULE_FRAMEWORK_FILE_INSTALL_CFG_ENABLED",
				originalModuleFrameworkFileInstallCfgEnabled);
		}
	}

	private Configuration _createConfiguration(
			String configurationPid, String content)
		throws Exception {

		return _createConfiguration(
			configurationPid, content, Charset.defaultCharset());
	}

	private Configuration _createConfiguration(
			String configurationPid, String content, Charset charset)
		throws Exception {

		CountDownLatch countDownLatch = new CountDownLatch(2);

		ServiceRegistration<ManagedService> serviceRegistration =
			_bundleContext.registerService(
				ManagedService.class, props -> countDownLatch.countDown(),
				MapUtil.singletonDictionary(
					Constants.SERVICE_PID, configurationPid));

		try {
			Files.write(_configurationPath, content.getBytes(charset));

			countDownLatch.await();
		}
		finally {
			serviceRegistration.unregister();
		}

		return _configurationAdmin.getConfiguration(
			configurationPid, StringPool.QUESTION);
	}

	private void _deleteConfiguration() throws Exception {
		if (_configurationPath != null) {
			Files.deleteIfExists(_configurationPath);
		}

		if (_configuration != null) {
			ConfigurationTestUtil.deleteConfiguration(_configuration);
		}
	}

	private static final String _CONFIGURATION_PID_PREFIX =
		FileInstallCfgTest.class.getName() + "Configuration";

	private static BundleContext _bundleContext;

	@Inject
	private static ConfigurationAdmin _configurationAdmin;

	private static boolean _originalModuleFrameworkFileInstallCfgEnabled;

	private Configuration _configuration;
	private Path _configurationPath;

}
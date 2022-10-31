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
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;

import java.io.File;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Dictionary;

import org.apache.commons.io.FileUtils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;

/**
 * @author Kyle Miho
 */
@RunWith(Arquillian.class)
public class FileInstallSymLinkTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_backupOSGiConfigFolder();

		FileUtils.deleteDirectory(
			new File(PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR));
	}

	@AfterClass
	public static void tearDownClass() {
		_restoreOSGiConfigFolder();
	}

	@Before
	public void setUp() {
		_customConfigurationDirectory = new File(
			PropsValues.MODULE_FRAMEWORK_BASE_DIR.concat("/temp"));

		_customConfigurationDirectory.mkdir();
	}

	@After
	public void tearDown() throws Exception {
		_deleteConfiguration();

		FileUtils.deleteDirectory(
			new File(PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR));

		FileUtils.deleteDirectory(_customConfigurationDirectory);

		if (_symbolicLinkPath != null) {
			Files.deleteIfExists(_symbolicLinkPath);
		}
	}

	@Test
	public void testConfigurationWithDanglingSymbolicLink() throws Exception {
		_createOSGiConfigFolder(false);

		String fileName = RandomTestUtil.randomString();

		File configFile = new File(
			_customConfigurationDirectory, fileName.concat(".config"));

		String configurationPid = _CONFIGURATION_PID_PREFIX.concat(
			".testConfigurationWithDanglingSymbolicLink");

		_symbolicLinkPath = Paths.get(
			PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR,
			configurationPid.concat(".config"));

		Files.createSymbolicLink(_symbolicLinkPath, configFile.toPath());

		String configKey = "testKey";
		String configValue = "testValue";

		_configuration = ConfigurationTestUtil.updateConfiguration(
			configurationPid,
			() -> {
				String content = StringBundler.concat(
					configKey, StringPool.EQUAL, StringPool.QUOTE, configValue,
					StringPool.QUOTE);

				Files.write(configFile.toPath(), content.getBytes());
			});

		Dictionary<String, Object> dictionary = _configuration.getProperties();

		Assert.assertEquals(configValue, dictionary.get(configKey));
	}

	@Test
	public void testConfigurationWithSymbolicLink() throws Exception {
		_createOSGiConfigFolder(false);

		String fileName = RandomTestUtil.randomString();

		File configFile = new File(
			_customConfigurationDirectory, fileName.concat(".config"));

		String configKey = "testKey";
		String configValue = "testValue";

		String content = StringBundler.concat(
			configKey, StringPool.EQUAL, StringPool.QUOTE, configValue,
			StringPool.QUOTE);

		Path configPath = Files.write(configFile.toPath(), content.getBytes());

		String configurationPid = _CONFIGURATION_PID_PREFIX.concat(
			".testConfigurationWithSymbolicLink");

		_symbolicLinkPath = Paths.get(
			PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR,
			configurationPid.concat(".config"));

		_configuration = ConfigurationTestUtil.updateConfiguration(
			configurationPid,
			() -> Files.createSymbolicLink(_symbolicLinkPath, configPath));

		Dictionary<String, Object> dictionary = _configuration.getProperties();

		Assert.assertEquals(configValue, dictionary.get(configKey));
	}

	@Ignore
	@Test
	public void testConfigurationWithSymbolicLinkFolder() throws Exception {
		_createOSGiConfigFolder(true);

		String fileName = RandomTestUtil.randomString();

		File configFile = new File(
			_customConfigurationDirectory, fileName.concat(".config"));

		String configKey = "testKey";
		String configValue = "testValue";

		String content = StringBundler.concat(
			configKey, StringPool.EQUAL, StringPool.QUOTE, configValue,
			StringPool.QUOTE);

		String configurationPid = _CONFIGURATION_PID_PREFIX.concat(
			".testConfigurationWithSymbolicLinkFolder");

		_configuration = ConfigurationTestUtil.updateConfiguration(
			configurationPid,
			() -> Files.write(configFile.toPath(), content.getBytes()));

		Dictionary<String, Object> dictionary = _configuration.getProperties();

		Assert.assertEquals(configValue, dictionary.get(configKey));
	}

	@Test
	public void testUpdateConfigurationWithSymbolicLink() throws Exception {
		_createOSGiConfigFolder(false);

		String fileName = RandomTestUtil.randomString();

		File configFile = new File(
			_customConfigurationDirectory, fileName.concat(".config"));

		String originalKey = "originalKey";
		String originalValue = "originalValue";

		String originalContent = _getContent(originalKey, originalValue);

		Path configPath = Files.write(
			configFile.toPath(), originalContent.getBytes());

		String configurationPid = _CONFIGURATION_PID_PREFIX.concat(
			".testUpdateConfigurationWithSymbolicLink");

		_symbolicLinkPath = Paths.get(
			PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR,
			configurationPid.concat(".config"));

		ConfigurationTestUtil.updateConfiguration(
			configurationPid,
			() -> Files.createSymbolicLink(_symbolicLinkPath, configPath));

		String updatedKey = "testKey";
		String updatedValue = "testValue";

		String updatedContent = _getContent(updatedKey, updatedValue);

		_configuration = ConfigurationTestUtil.updateConfiguration(
			configurationPid,
			() -> FileUtils.writeStringToFile(
				configFile, updatedContent, Charset.defaultCharset()));

		Dictionary<String, Object> dictionary = _configuration.getProperties();

		Assert.assertEquals(updatedValue, dictionary.get(updatedKey));
		Assert.assertNull(dictionary.get(originalKey));
	}

	@Ignore
	@Test
	public void testUpdateConfigurationWithSymbolicLinkFolder()
		throws Exception {

		_createOSGiConfigFolder(true);

		String fileName = RandomTestUtil.randomString();

		File configFile = new File(
			_customConfigurationDirectory, fileName.concat(".config"));

		String content = _getContent(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		Files.write(configFile.toPath(), content.getBytes());

		String updatedKey = "testKey";
		String updatedValue = "testValue";

		String updatedContent = _getContent(updatedKey, updatedValue);

		String configurationPid = _CONFIGURATION_PID_PREFIX.concat(
			".testConfigurationWithSymbolicLinkFolder");

		_configuration = ConfigurationTestUtil.updateConfiguration(
			configurationPid,
			() -> Files.write(configFile.toPath(), updatedContent.getBytes()));

		Dictionary<String, Object> dictionary = _configuration.getProperties();

		Assert.assertEquals(updatedValue, dictionary.get(updatedKey));
	}

	private static void _backupOSGiConfigFolder() {
		_osgiConfigDirectory = new File(
			PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR);

		_osgiConfigDirectory.renameTo(new File(_TEMP_OSGI_CONFIG_NAME));

		_osgiConfigDirectory = new File(_TEMP_OSGI_CONFIG_NAME);
	}

	private static void _restoreOSGiConfigFolder() {
		_osgiConfigDirectory.renameTo(
			new File(PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR));
	}

	private void _createOSGiConfigFolder(boolean symlink) throws Exception {
		if (symlink) {
			_symbolicLinkPath = Paths.get(
				PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR);

			Files.createSymbolicLink(
				_symbolicLinkPath, _customConfigurationDirectory.toPath());
		}
		else {
			File file = new File(PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR);

			Files.createDirectory(file.toPath());
		}
	}

	private void _deleteConfiguration() throws Exception {
		if (_configuration != null) {
			ConfigurationTestUtil.deleteConfiguration(_configuration);
		}
	}

	private String _getContent(String key, String value) throws Exception {
		return StringBundler.concat(
			key, StringPool.EQUAL, StringPool.QUOTE, value, StringPool.QUOTE);
	}

	private static final String _CONFIGURATION_PID_PREFIX =
		FileInstallSymLinkTest.class.getName() + "Configuration";

	private static final String _TEMP_OSGI_CONFIG_NAME =
		PropsValues.MODULE_FRAMEWORK_BASE_DIR + "/tempOsgi";

	private static File _osgiConfigDirectory;

	private Configuration _configuration;
	private File _customConfigurationDirectory;
	private Path _symbolicLinkPath;

}
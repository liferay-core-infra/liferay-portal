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

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Dictionary;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
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

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(FileInstallConfigTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	@After
	public void tearDown() throws Exception {
		_deleteConfiguration();
	}

	@Test
	public void testConfigurationWithSymbolicLink() throws Exception {
		String configurationPid = _CONFIGURATION_PID_PREFIX.concat(
			".testConfigurationWithSymbolicLink");

		_symbolicLinkPath = Paths.get(
			PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR,
			configurationPid.concat(".config"));

		String symbolicLinkFileName = RandomTestUtil.randomString();

		String testKey = "testKey";
		String testValue = "testValue";

		String content = StringBundler.concat(
			testKey, StringPool.EQUAL, StringPool.QUOTE, testValue,
			StringPool.QUOTE);

		_configurationPath = Files.write(
			Paths.get(
				PropsValues.MODULE_FRAMEWORK_BASE_DIR,
				symbolicLinkFileName.concat(".config")),
			content.getBytes(Charset.defaultCharset()));

		_configuration = ConfigurationTestUtil.updateConfiguration(
			configurationPid,
			() -> Files.createSymbolicLink(
				_symbolicLinkPath, _configurationPath));

		Dictionary<String, Object> dictionary = _configuration.getProperties();

		Assert.assertEquals(testValue, dictionary.get(testKey));
	}

	private void _deleteConfiguration() throws Exception {
		if (_symbolicLinkPath != null) {
			Files.deleteIfExists(_symbolicLinkPath);
		}

		if (_configurationPath != null) {
			Files.deleteIfExists(_configurationPath);
		}

		if (_configuration != null) {
			ConfigurationTestUtil.deleteConfiguration(_configuration);
		}
	}

	private static final String _CONFIGURATION_PID_PREFIX =
		FileInstallSymLinkTest.class.getName() + "Configuration";

	private BundleContext _bundleContext;
	private Configuration _configuration;
	private Path _configurationPath;
	private Path _symbolicLinkPath;

}
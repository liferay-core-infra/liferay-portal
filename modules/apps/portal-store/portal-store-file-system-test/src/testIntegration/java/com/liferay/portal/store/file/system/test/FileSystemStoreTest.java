/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.store.file.system.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.store.test.util.BaseStoreTestCase;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class FileSystemStoreTest extends BaseStoreTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_rootDir =
			_props.get(PropsKeys.LIFERAY_HOME) + "/test/store/file_system";

		_configuration = _configurationAdmin.getConfiguration(
			"com.liferay.portal.store.file.system.configuration." +
				"FileSystemStoreConfiguration",
			StringPool.QUESTION);

		ConfigurationTestUtil.saveConfiguration(
			_configuration,
			HashMapDictionaryBuilder.<String, Object>put(
				"rootDir", _rootDir
			).build());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(_configuration);

		FileUtil.delete(_rootDir + "_symlink");

		FileUtil.deltree(_rootDir);
	}

	@Test
	public void testDeleteEmptyParentWithSymlinkRoot() throws Exception {
		String originalRootDir = _rootDir;

		_rootDir += "_symlink";

		Files.createSymbolicLink(
			Paths.get(_rootDir), Paths.get(originalRootDir));

		ConfigurationTestUtil.saveConfiguration(
			_configuration,
			HashMapDictionaryBuilder.<String, Object>put(
				"rootDir", _rootDir
			).build());

		String fileName = RandomTestUtil.randomString();

		_store.addFile(
			getCompanyId(), getRepositoryId(), fileName, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(getDATA_VERSION_1()));

		_store.deleteFile(
			getCompanyId(), getRepositoryId(), fileName, Store.VERSION_DEFAULT);

		File symlinkFile = new File(_rootDir);

		Assert.assertFalse(
			_store.hasFile(
				getCompanyId(), getRepositoryId(), fileName,
				Store.VERSION_DEFAULT));

		Assert.assertTrue(symlinkFile.exists());

		_rootDir = originalRootDir;

		ConfigurationTestUtil.saveConfiguration(
			_configuration,
			HashMapDictionaryBuilder.<String, Object>put(
				"rootDir", _rootDir
			).build());
	}

	@Override
	protected Store getStore() {
		return _store;
	}

	private static Configuration _configuration;

	@Inject
	private static ConfigurationAdmin _configurationAdmin;

	@Inject
	private static Props _props;

	private static String _rootDir;

	@Inject(
		filter = "store.type=com.liferay.portal.store.file.system.FileSystemStore",
		type = Store.class
	)
	private Store _store;

}
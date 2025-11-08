/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.store.s3.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.io.DummyOutputStream;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.store.test.util.BaseStoreTestCase;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.annotation.Generated;

import java.util.Dictionary;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Preston Crary
 * @author Manuel de la Peña
 */
@Generated("")
@RunWith(Arquillian.class)
public class IBMS3StoreTest extends BaseStoreTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		String s3StoreClassName = "com.liferay.portal.store.s3.IBMS3Store";
		String dlStoreImpl = PropsUtil.get(PropsKeys.DL_STORE_IMPL);

		Assume.assumeTrue(
			StringBundler.concat(
				"Property \"", PropsKeys.DL_STORE_IMPL, "\" is not set to \"",
				s3StoreClassName, "\""),
			dlStoreImpl.equals(s3StoreClassName));
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		_configuration = _configurationAdmin.getConfiguration(
			"com.liferay.portal.store.s3.configuration.S3StoreConfiguration",
			StringPool.QUESTION);

		_originalProperties = _configuration.getProperties();

		ConfigurationTestUtil.saveConfiguration(
			_configuration,
			HashMapDictionaryBuilder.putAll(
				_originalProperties
			).put(
				"httpClientMaxConnections", _HTTP_CLIENT_MAX_CONNECTIONS
			).build());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ConfigurationTestUtil.saveConfiguration(
			_configuration, _originalProperties);
	}

	@Test
	@TestInfo("LPS-127589")
	public void testS3ConnectionDoesNotLeakWhenServingFileAsStream()
		throws Exception {

		String fileName = RandomTestUtil.randomString();

		_store.addFile(
			companyId, repositoryId, fileName, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(DATA_VERSION_1));

		for (int i = 0; i <= _HTTP_CLIENT_MAX_CONNECTIONS; i++) {
			StreamUtil.transfer(
				_store.getFileAsStream(
					companyId, repositoryId, fileName, Store.VERSION_DEFAULT),
				new DummyOutputStream());
		}

		_store.addFile(
			companyId, repositoryId, fileName, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(DATA_VERSION_1));

		Assert.assertTrue(
			_store.hasFile(
				companyId, repositoryId, fileName, Store.VERSION_DEFAULT));
	}

	@Override
	protected Store getStore() {
		return _store;
	}

	private static final int _HTTP_CLIENT_MAX_CONNECTIONS = 10;

	private static Configuration _configuration;

	@Inject
	private static ConfigurationAdmin _configurationAdmin;

	private static Dictionary<String, Object> _originalProperties;

	@Inject(
		filter = "store.type=com.liferay.portal.store.s3.IBMS3Store",
		type = Store.class
	)
	private Store _store;

}
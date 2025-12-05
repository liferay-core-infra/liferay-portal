/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.store.s3;

import com.liferay.document.library.kernel.store.Store;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.store.s3.configuration.S3StoreConfiguration;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import io.netty.handler.codec.http.HttpRequest;

import java.net.InetSocketAddress;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Kevin Lee
 */
public class S3StoreUnitTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		new LiferayUnitTestRule();

	@Before
	public void setUp() {
		_s3StoreConfiguration = Mockito.mock(S3StoreConfiguration.class);

		Mockito.when(
			_s3StoreConfiguration.bucketName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_s3StoreConfiguration.connectionTimeout()
		).thenReturn(
			1000
		);

		Mockito.when(
			_s3StoreConfiguration.httpClientMaxConnections()
		).thenReturn(
			1
		);

		Mockito.when(
			_s3StoreConfiguration.httpClientMaxErrorRetry()
		).thenReturn(
			0
		);

		Mockito.when(
			_s3StoreConfiguration.maxPoolSize()
		).thenReturn(
			1
		);

		Mockito.when(
			_s3StoreConfiguration.s3Region()
		).thenReturn(
			"us-west-1"
		);

		Mockito.when(
			_s3StoreConfiguration.s3StorageClass()
		).thenReturn(
			"STANDARD"
		);

		Mockito.when(
			ConfigurableUtil.createConfigurable(
				Mockito.eq(S3StoreConfiguration.class), Mockito.anyMap())
		).thenReturn(
			_s3StoreConfiguration
		);
	}

	@After
	public void tearDown() {
		_configurableUtilMockedStatic.close();
	}

	private void _mockProxy(InetSocketAddress inetSocketAddress) {
		Mockito.when(
			_s3StoreConfiguration.proxyAuthType()
		).thenReturn(
			"none"
		);

		Mockito.when(
			_s3StoreConfiguration.proxyHost()
		).thenReturn(
			inetSocketAddress.getHostString()
		);

		Mockito.when(
			_s3StoreConfiguration.proxyPort()
		).thenReturn(
			inetSocketAddress.getPort()
		);
	}

	@Test
	public void testProxy() {
		InetSocketAddress inetSocketAddress = new InetSocketAddress(1234);

		_mockProxy(inetSocketAddress);

		AtomicBoolean proxyHit = new AtomicBoolean(false);

		HttpProxyServer httpProxyServer = DefaultHttpProxyServer.bootstrap(
		).withAddress(
			inetSocketAddress
		).withFiltersSource(
			new HttpFiltersSourceAdapter() {

				@Override
				public HttpFilters filterRequest(HttpRequest httpRequest) {
					proxyHit.set(true);

					return super.filterRequest(httpRequest);
				}

			}
		).start();

		try {
			S3Store s3Store = new S3Store();

			s3Store.activate(Collections.emptyMap());

			try {
				s3Store.getFileSize(
					RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
					RandomTestUtil.randomString(), Store.VERSION_DEFAULT);

				Assert.fail();
			}
			finally {
				s3Store.deactivate();
			}
		}
		catch (Exception exception) {
			Assert.assertTrue(proxyHit.get());
		}
		finally {
			httpProxyServer.stop();
		}
	}

	private final MockedStatic<ConfigurableUtil> _configurableUtilMockedStatic =
		Mockito.mockStatic(ConfigurableUtil.class);
	private S3StoreConfiguration _s3StoreConfiguration;

}
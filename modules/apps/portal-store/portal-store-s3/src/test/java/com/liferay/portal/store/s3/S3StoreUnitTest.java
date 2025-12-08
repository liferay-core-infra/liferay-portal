/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.store.s3;

import com.liferay.document.library.kernel.store.Store;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Validator;
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
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.ProxyAuthenticator;
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

	@Test
	public void testProxy() {
		_mockProxy(null, null);

		Assert.assertTrue(_testProxy(null, null));
	}

	@Test
	public void testProxyAuthentication() {
		String proxyPassword = RandomTestUtil.randomString();
		String proxyUserName = RandomTestUtil.randomString();

		_mockProxy(proxyUserName, proxyPassword);

		Assert.assertTrue(_testProxy(proxyUserName, proxyPassword));
	}

	@Test
	public void testProxyAuthenticationFailed() {
		String proxyPassword = RandomTestUtil.randomString();
		String proxyUserName = RandomTestUtil.randomString();

		_mockProxy(proxyUserName, proxyPassword);

		Assert.assertFalse(_testProxy(proxyUserName, proxyPassword + "1"));
	}

	private void _mockProxy(String proxyUserName, String proxyPassword) {
		if (Validator.isNotNull(proxyUserName)) {
			Mockito.when(
				_s3StoreConfiguration.proxyAuthType()
			).thenReturn(
				"username-password"
			);

			Mockito.when(
				_s3StoreConfiguration.proxyPassword()
			).thenReturn(
				proxyPassword
			);

			Mockito.when(
				_s3StoreConfiguration.proxyUsername()
			).thenReturn(
				proxyUserName
			);
		}
		else {
			Mockito.when(
				_s3StoreConfiguration.proxyAuthType()
			).thenReturn(
				"none"
			);
		}

		Mockito.when(
			_s3StoreConfiguration.proxyHost()
		).thenReturn(
			_INET_SOCKET_ADDRESS.getHostString()
		);

		Mockito.when(
			_s3StoreConfiguration.proxyPort()
		).thenReturn(
			_INET_SOCKET_ADDRESS.getPort()
		);
	}

	private boolean _testProxy(String proxyUserName, String proxyPassword) {
		AtomicBoolean proxyHit = new AtomicBoolean(false);

		HttpProxyServerBootstrap httpProxyServerBootstrap =
			DefaultHttpProxyServer.bootstrap(
			).withAddress(
				_INET_SOCKET_ADDRESS
			).withFiltersSource(
				new HttpFiltersSourceAdapter() {

					@Override
					public HttpFilters filterRequest(HttpRequest httpRequest) {
						proxyHit.set(true);

						return super.filterRequest(httpRequest);
					}

				}
			);

		if (Validator.isNotNull(proxyUserName)) {
			httpProxyServerBootstrap.withProxyAuthenticator(
				new ProxyAuthenticator() {

					@Override
					public boolean authenticate(
						String userName, String password) {

						if (userName.equals(proxyUserName) &&
							password.equals(proxyPassword)) {

							return true;
						}

						return false;
					}

					@Override
					public String getRealm() {
						return null;
					}

				});
		}

		HttpProxyServer httpProxyServer = httpProxyServerBootstrap.start();

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
			return proxyHit.get();
		}
		finally {
			httpProxyServer.stop();
		}

		return proxyHit.get();
	}

	private static final InetSocketAddress _INET_SOCKET_ADDRESS =
		new InetSocketAddress(1234);

	private final MockedStatic<ConfigurableUtil> _configurableUtilMockedStatic =
		Mockito.mockStatic(ConfigurableUtil.class);
	private S3StoreConfiguration _s3StoreConfiguration;

}
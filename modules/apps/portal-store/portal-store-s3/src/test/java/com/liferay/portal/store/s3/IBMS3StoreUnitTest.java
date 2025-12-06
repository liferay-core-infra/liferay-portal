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

import jakarta.annotation.Generated;

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
@Generated("")
public class IBMS3StoreUnitTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		new LiferayUnitTestRule();

	@Before
	public void setUp() {
		_ibmS3StoreConfiguration = Mockito.mock(S3StoreConfiguration.class);

		Mockito.when(
			_ibmS3StoreConfiguration.bucketName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_ibmS3StoreConfiguration.connectionTimeout()
		).thenReturn(
			1000
		);

		Mockito.when(
			_ibmS3StoreConfiguration.httpClientMaxConnections()
		).thenReturn(
			1
		);

		Mockito.when(
			_ibmS3StoreConfiguration.httpClientMaxErrorRetry()
		).thenReturn(
			0
		);

		Mockito.when(
			_ibmS3StoreConfiguration.maxPoolSize()
		).thenReturn(
			1
		);

		Mockito.when(
			_ibmS3StoreConfiguration.s3Region()
		).thenReturn(
			"us-west-1"
		);

		Mockito.when(
			_ibmS3StoreConfiguration.s3StorageClass()
		).thenReturn(
			"STANDARD"
		);

		Mockito.when(
			ConfigurableUtil.createConfigurable(
				Mockito.eq(S3StoreConfiguration.class), Mockito.anyMap())
		).thenReturn(
			_ibmS3StoreConfiguration
		);
	}

	@After
	public void tearDown() {
		_configurableUtilMockedStatic.close();
	}

	@Test
	public void testProxy() {
		InetSocketAddress inetSocketAddress = new InetSocketAddress(1234);

		_mockProxy(inetSocketAddress, null, null);

		Assert.assertTrue(_testProxy(inetSocketAddress, null, null));
	}

	@Test
	public void testProxyAuthentication() {
		InetSocketAddress inetSocketAddress = new InetSocketAddress(1234);

		String proxyPassword = RandomTestUtil.randomString();
		String proxyUserName = RandomTestUtil.randomString();

		_mockProxy(inetSocketAddress, proxyUserName, proxyPassword);

		Assert.assertTrue(
			_testProxy(inetSocketAddress, proxyUserName, proxyPassword));
	}

	@Test
	public void testProxyAuthenticationFailed() {
		InetSocketAddress inetSocketAddress = new InetSocketAddress(1234);

		String proxyPassword = RandomTestUtil.randomString();
		String proxyUserName = RandomTestUtil.randomString();

		_mockProxy(inetSocketAddress, proxyUserName, proxyPassword);

		Assert.assertFalse(
			_testProxy(inetSocketAddress, proxyUserName, proxyPassword + "1"));
	}

	private void _mockProxy(
		InetSocketAddress inetSocketAddress, String proxyUserName,
		String proxyPassword) {

		if (Validator.isNotNull(proxyUserName)) {
			Mockito.when(
				_ibmS3StoreConfiguration.proxyAuthType()
			).thenReturn(
				"username-password"
			);

			Mockito.when(
				_ibmS3StoreConfiguration.proxyPassword()
			).thenReturn(
				proxyPassword
			);

			Mockito.when(
				_ibmS3StoreConfiguration.proxyUsername()
			).thenReturn(
				proxyUserName
			);
		}
		else {
			Mockito.when(
				_ibmS3StoreConfiguration.proxyAuthType()
			).thenReturn(
				"none"
			);
		}

		Mockito.when(
			_ibmS3StoreConfiguration.proxyHost()
		).thenReturn(
			inetSocketAddress.getHostString()
		);

		Mockito.when(
			_ibmS3StoreConfiguration.proxyPort()
		).thenReturn(
			inetSocketAddress.getPort()
		);
	}

	private boolean _testProxy(
		InetSocketAddress inetSocketAddress, String proxyUserName,
		String proxyPassword) {

		AtomicBoolean proxyHit = new AtomicBoolean(false);

		HttpProxyServerBootstrap httpProxyServerBootstrap =
			DefaultHttpProxyServer.bootstrap(
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
			IBMS3Store ibmS3Store = new IBMS3Store();

			ibmS3Store.activate(Collections.emptyMap());

			try {
				ibmS3Store.getFileSize(
					RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
					RandomTestUtil.randomString(), Store.VERSION_DEFAULT);

				Assert.fail();
			}
			finally {
				ibmS3Store.deactivate();
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

	private final MockedStatic<ConfigurableUtil> _configurableUtilMockedStatic =
		Mockito.mockStatic(ConfigurableUtil.class);
	private S3StoreConfiguration _ibmS3StoreConfiguration;

}
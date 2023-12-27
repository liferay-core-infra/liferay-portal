/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.http.internal;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;

import java.lang.reflect.Field;

import java.util.concurrent.TimeUnit;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.DefaultManagedHttpClientConnection;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.execchain.ClientExecChain;
import org.apache.http.impl.execchain.MainClientExec;
import org.apache.http.impl.pool.BasicPoolEntry;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Miguel Pastor
 */
public class HttpImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(
			new PortalImpl() {

				@Override
				public String[] stripURLAnchor(String url, String separator) {
					return new String[] {url, StringPool.BLANK};
				}

			});
	}

	@Test
	public void testHttpKeepAlive() {
		int keepAliveTimeout = ReflectionTestUtil.getFieldValue(
			_httpImpl, "_KEEPALIVE_TIMEOUT");

		try {
			_setHttpImplKeepAliveTimeout(-1);
			_testHttpKeepAlive(true, Long.MAX_VALUE, -1);
			_testHttpKeepAlive(true, Long.MAX_VALUE, 0);
			_testHttpKeepAlive(true, 300000, 300);

			_setHttpImplKeepAliveTimeout(0);
			_testHttpKeepAlive(true, Long.MAX_VALUE, -1);
			_testHttpKeepAlive(true, Long.MAX_VALUE, 0);
			_testHttpKeepAlive(true, 300000, 300);

			_setHttpImplKeepAliveTimeout(600);
			_testHttpKeepAlive(true, 600000, -1);
			_testHttpKeepAlive(true, 600000, 0);
			_testHttpKeepAlive(true, 300000, 300);
		}
		finally {
			_setHttpImplKeepAliveTimeout(keepAliveTimeout);
		}
	}

	@Test
	public void testHttpKeepAliveWithRequestClose() {
		HttpRequest httpRequest = new BasicHttpRequest("GET", "/");

		httpRequest.setHeader(
			HttpHeaders.CONNECTION, HttpHeaders.CONNECTION_CLOSE_VALUE);

		_httpContext.setAttribute(HttpCoreContext.HTTP_REQUEST, httpRequest);

		_httpResponse.setHeaders(
			new Header[] {
				new BasicHeader(HttpHeaders.CONNECTION, HttpHeaders.KEEP_ALIVE),
				new BasicHeader(HttpHeaders.CONTENT_LENGTH, "10")
			});

		_testHttpKeepAlive(false, -1);
	}

	@Test
	public void testHttpKeepAliveWithResponseClose() {
		HttpRequest httpRequest = new BasicHttpRequest("GET", "/");

		httpRequest.setHeader(HttpHeaders.CONNECTION, HttpHeaders.KEEP_ALIVE);

		_httpContext.setAttribute(HttpCoreContext.HTTP_REQUEST, httpRequest);

		_httpResponse.setHeaders(
			new Header[] {
				new BasicHeader(
					HttpHeaders.CONNECTION, HttpHeaders.CONNECTION_CLOSE_VALUE),
				new BasicHeader(HttpHeaders.CONTENT_LENGTH, "10")
			});

		_testHttpKeepAlive(false, -1);
	}

	@Test
	public void testIsNonProxyHost() throws Exception {
		String domain = "foo.com";
		String ipAddress = "192.168.0.250";
		String ipAddressWithStarWildcard = "182.*.0.250";

		Field field = ReflectionTestUtil.getField(
			HttpImpl.class, "_NON_PROXY_HOSTS");

		Object value = field.get(null);

		try {
			field.set(
				null,
				new String[] {domain, ipAddress, ipAddressWithStarWildcard});

			Assert.assertTrue(_httpImpl.isNonProxyHost(domain));
			Assert.assertTrue(_httpImpl.isNonProxyHost(ipAddress));
			Assert.assertTrue(_httpImpl.isNonProxyHost("182.123.0.250"));
			Assert.assertFalse(_httpImpl.isNonProxyHost("182.100.1.250"));
			Assert.assertFalse(_httpImpl.isNonProxyHost("google.com"));
		}
		finally {
			field.set(null, value);
		}
	}

	@Test
	public void testTCPKeepAlive() {
		boolean tcpKeepAliveEnabled = ReflectionTestUtil.getFieldValue(
			_httpImpl, "_TCP_KEEPALIVE_ENABLED");

		try {
			_setHttpImplTCPKeepAliveEnabled(false);
			_testTCPKeepAlive(false);

			_setHttpImplTCPKeepAliveEnabled(true);
			_testTCPKeepAlive(true);
		}
		finally {
			_setHttpImplTCPKeepAliveEnabled(tcpKeepAliveEnabled);
		}
	}

	private Tuple _getHttpImplConnectionStrategies() {
		CloseableHttpClient closeableHttpClient = ReflectionTestUtil.invoke(
			_httpImpl, "getCloseableHttpClient",
			new Class<?>[] {HttpHost.class}, new Object[] {null});

		ClientExecChain clientExecChain = ReflectionTestUtil.getFieldValue(
			closeableHttpClient, "execChain");

		while (true) {
			clientExecChain = ReflectionTestUtil.getFieldValue(
				clientExecChain, "requestExecutor");

			if (clientExecChain instanceof MainClientExec) {
				ConnectionKeepAliveStrategy connectionKeepAliveStrategy =
					ReflectionTestUtil.getFieldValue(
						clientExecChain, "keepAliveStrategy");
				ConnectionReuseStrategy connectionReuseStrategy =
					ReflectionTestUtil.getFieldValue(
						clientExecChain, "reuseStrategy");

				return new Tuple(
					connectionKeepAliveStrategy, connectionReuseStrategy);
			}
		}
	}

	private void _resetHttpImpl() {
		DCLSingleton<CloseableHttpClient> closeableHttpClientDCLSingleton =
			ReflectionTestUtil.getFieldValue(
				_httpImpl, "_closeableHttpClientDCLSingleton");

		ReflectionTestUtil.setFieldValue(
			closeableHttpClientDCLSingleton, "_singleton", null);

		DCLSingleton<PoolingHttpClientConnectionManager>
			poolingHttpClientConnectionManagerDCLSingleton =
				ReflectionTestUtil.getFieldValue(
					_httpImpl,
					"_poolingHttpClientConnectionManagerDCLSingleton");

		ReflectionTestUtil.setFieldValue(
			poolingHttpClientConnectionManagerDCLSingleton, "_singleton", null);
	}

	private void _setHttpImplKeepAliveTimeout(int keepAliveTimeout) {
		ReflectionTestUtil.setFieldValue(
			_httpImpl, "_KEEPALIVE_TIMEOUT", keepAliveTimeout);
	}

	private void _setHttpImplTCPKeepAliveEnabled(boolean tcpKeepAliveEnabled) {
		ReflectionTestUtil.setFieldValue(
			_httpImpl, "_TCP_KEEPALIVE_ENABLED", tcpKeepAliveEnabled);
	}

	private void _testHttpKeepAlive(
		boolean expectedKeepAlive,
		long expectedKeepAliveTimeoutInMilliseconds) {

		_httpImpl.activate();

		try {
			Tuple connectionStrategiesTuple =
				_getHttpImplConnectionStrategies();

			ConnectionReuseStrategy connectionReuseStrategy =
				(ConnectionReuseStrategy)connectionStrategiesTuple.getObject(1);

			Assert.assertEquals(
				expectedKeepAlive,
				connectionReuseStrategy.keepAlive(_httpResponse, _httpContext));

			long keepAliveTimeout = -1;

			if (expectedKeepAlive) {
				ConnectionKeepAliveStrategy connectionKeepAliveStrategy =
					(ConnectionKeepAliveStrategy)
						connectionStrategiesTuple.getObject(0);

				long keepAliveDuration =
					connectionKeepAliveStrategy.getKeepAliveDuration(
						_httpResponse, new BasicHttpContext(null));

				BasicPoolEntry basicPoolEntry = new BasicPoolEntry(
					"id", new HttpHost("localhost", 8080),
					new DefaultManagedHttpClientConnection("id", 8 * 1024));

				basicPoolEntry.updateExpiry(
					keepAliveDuration, TimeUnit.MILLISECONDS);

				keepAliveTimeout = basicPoolEntry.getExpiry();

				if (keepAliveTimeout != Long.MAX_VALUE) {
					keepAliveTimeout -= basicPoolEntry.getUpdated();
				}
			}

			Assert.assertEquals(
				expectedKeepAliveTimeoutInMilliseconds, keepAliveTimeout);
		}
		finally {
			_resetHttpImpl();
		}
	}

	private void _testHttpKeepAlive(
		boolean expectedKeepAlive, long expectedKeepAliveTimeoutInMilliseconds,
		long keepAliveTimeoutHeaderValue) {

		_httpResponse.setHeaders(
			new Header[] {
				new BasicHeader(HttpHeaders.CONNECTION, HttpHeaders.KEEP_ALIVE),
				new BasicHeader(HttpHeaders.CONTENT_LENGTH, "10")
			});

		if (keepAliveTimeoutHeaderValue > -1) {
			_httpResponse.setHeader(
				HttpHeaders.KEEP_ALIVE,
				"timeout=" + keepAliveTimeoutHeaderValue);
		}

		_testHttpKeepAlive(
			expectedKeepAlive, expectedKeepAliveTimeoutInMilliseconds);
	}

	private void _testTCPKeepAlive(boolean expectedEnabledTCPKeepAlive) {
		try {
			CloseableHttpClient closeableHttpClient = ReflectionTestUtil.invoke(
				_httpImpl, "getCloseableHttpClient",
				new Class<?>[] {HttpHost.class}, new Object[] {null});

			PoolingHttpClientConnectionManager
				poolingHttpClientConnectionManager =
					ReflectionTestUtil.getFieldValue(
						closeableHttpClient, "connManager");

			SocketConfig defaultSocketConfig =
				poolingHttpClientConnectionManager.getDefaultSocketConfig();

			if (expectedEnabledTCPKeepAlive) {
				Assert.assertTrue(defaultSocketConfig.isSoKeepAlive());
			}
			else {
				Assert.assertNull(defaultSocketConfig);
			}
		}
		finally {
			_resetHttpImpl();
		}
	}

	private final HttpContext _httpContext = new BasicHttpContext(null);
	private final HttpImpl _httpImpl = new HttpImpl();
	private final HttpResponse _httpResponse = new BasicHttpResponse(
		new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));

}
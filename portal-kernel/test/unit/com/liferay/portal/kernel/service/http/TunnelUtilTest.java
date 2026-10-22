/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.http;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.FIPSModeTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Caio Farias
 */
public class TunnelUtilTest {

	@Before
	public void setUp() {
		PropsUtil.set(
			PropsKeys.DNS_SECURITY_ADDRESS_TIMEOUT_SECONDS, String.valueOf(2));
		PropsUtil.set(PropsKeys.DNS_SECURITY_THREAD_LIMIT, String.valueOf(10));
		PropsUtil.set(
			PropsKeys.DNS_SECURITY_THREAD_QUEUE_LIMIT, String.valueOf(5));
	}

	@Test
	public void testGetConnection() throws Exception {
		String hostName = RandomTestUtil.randomString();
		int hostPort = RandomTestUtil.randomInt(1, 65535);

		HttpPrincipal httpPrincipal = new HttpPrincipal(
			StringBundler.concat("https://", hostName, ":", hostPort));

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					TunnelUtil.class, "_VERIFY_SSL_HOSTNAME", false);
			SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			FIPSModeTestUtil.assertSecurityException(
				"SSL hostname verification must be enabled in FIPS mode",
				() -> ReflectionTestUtil.invoke(
					TunnelUtil.class, "_getConnection",
					new Class<?>[] {HttpPrincipal.class}, httpPrincipal));
		}
	}

	@Test
	public void testGetConnectionWithHostNotAllowed() throws Exception {
		HttpPrincipal httpPrincipal = new HttpPrincipal(
			"http://127.0.0.1:18778");

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class,
					"TUNNELING_SERVLET_OUTBOUND_HOSTS_ALLOWED",
					new String[] {"live.example.com"})) {

			SecurityException securityException = Assert.assertThrows(
				SecurityException.class,
				() -> ReflectionTestUtil.invoke(
					TunnelUtil.class, "_getConnection",
					new Class<?>[] {HttpPrincipal.class}, httpPrincipal));

			Assert.assertEquals(
				"Tunnel connections to host 127.0.0.1 are not allowed by the " +
					"portal property " +
						"\"tunneling.servlet.outbound.hosts.allowed\"",
				securityException.getMessage());
		}
	}

	@Test
	public void testIsHostAllowed() throws Exception {
		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class,
					"TUNNELING_SERVLET_OUTBOUND_HOSTS_ALLOWED",
					new String[0])) {

			Assert.assertTrue(TunnelUtil.isHostAllowed("127.0.0.1"));
			Assert.assertTrue(
				TunnelUtil.isHostAllowed(RandomTestUtil.randomString()));
		}

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class,
					"TUNNELING_SERVLET_OUTBOUND_HOSTS_ALLOWED",
					new String[] {
						"10.0.0.0/8", "192.168.1.5", "fd00::/8",
						"live.example.com"
					})) {

			Assert.assertTrue(TunnelUtil.isHostAllowed("10.1.2.3"));
			Assert.assertTrue(TunnelUtil.isHostAllowed("192.168.1.5"));
			Assert.assertTrue(TunnelUtil.isHostAllowed("LIVE.example.com"));
			Assert.assertTrue(TunnelUtil.isHostAllowed("[fd00::1]"));
			Assert.assertTrue(TunnelUtil.isHostAllowed("live.example.com"));

			Assert.assertFalse(TunnelUtil.isHostAllowed(null));
			Assert.assertFalse(TunnelUtil.isHostAllowed(""));
			Assert.assertFalse(TunnelUtil.isHostAllowed("127.0.0.1"));
			Assert.assertFalse(TunnelUtil.isHostAllowed("192.168.1.6"));
			Assert.assertFalse(TunnelUtil.isHostAllowed("[::1]"));
			Assert.assertFalse(TunnelUtil.isHostAllowed("evil.example.com"));
			Assert.assertFalse(
				TunnelUtil.isHostAllowed("live.example.com.evil.com"));
		}
	}

}
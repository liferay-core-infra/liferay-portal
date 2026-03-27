/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.net.InetAddress;

import java.util.List;
import java.util.Set;

import net.bytebuddy.agent.builder.ResettableClassFileTransformer;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kevin Lee
 */
@RunWith(Arquillian.class)
public class LicenseValidationTest extends BaseLicenseTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		Assume.assumeTrue(isReleaseBundle());
	}

	@Test
	public void testFreeTierLicenseValidateDomain() throws Exception {
		InetAddress inetAddress = InetAddress.getLocalHost();

		try (AutoCloseable autoCloseable = _disableValidate()) {
			_assertDomainIsValid("localhost");
			_assertDomainIsValid("LOCALHOST");
			_assertDomainIsValid(inetAddress.getCanonicalHostName());
			_assertDomainIsValid(inetAddress.getHostName());

			_assertDomainIsInvalid(RandomTestUtil.randomString());
		}
	}

	@Test
	public void testFreeTierLicenseValidateVersion() throws Exception {
		try (AutoCloseable autoCloseable = _disableValidate()) {
			_assertVersionIsValid("2026.Q1.0 LTS");
			_assertVersionIsValid("2026.Q2.0");
			_assertVersionIsValid("2026.Q3.0");
			_assertVersionIsValid("2026.Q4.0");

			_assertVersionIsInvalid("2026.Q1.0");
			_assertVersionIsInvalid("2026.Q1.1 LTS");
			_assertVersionIsInvalid("2026.Q2.1");
			_assertVersionIsInvalid("2026.Q3");
		}
	}

	@Test
	public void testLicenseValidateKey() throws Exception {
		List<String> domains = ListUtil.fromArray("localhost");

		long startTimeMillis = System.currentTimeMillis();

		try (AutoCloseable autoCloseable1 = _setVersion("2026.Q1.0 LTS")) {
			String key = _getLicenseKey(domains, startTimeMillis);

			deployFreeTierPortalLicense(domains, key, startTimeMillis);

			assertPortalLicenseRegistered();

			resetLicenseData();

			try (AutoCloseable autoCloseable2 = _setBannedKeys(
					SetUtil.fromArray(key));
				LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					_getLicenseManagerClassName(), LoggerTestUtil.ERROR)) {

				deployFreeTierPortalLicense(domains, key, startTimeMillis);

				assertPortalLicenseNotRegistered();

				List<String> messages = logCapture.getMessages();

				Assert.assertFalse(messages.isEmpty());

				String message = messages.getFirst();

				Assert.assertTrue(
					message.contains(
						"Corrupt license file. License was not registered"));
			}
		}
	}

	private void _assertDomainIsInvalid(String domain) throws Exception {
		try (AutoCloseable autoCloseable = _setVersion("2026.Q1.0 LTS");
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_getLicenseManagerClassName(), LoggerTestUtil.ERROR)) {

			deployFreeTierPortalLicense(
				ListUtil.fromArray(domain), StringPool.BLANK,
				System.currentTimeMillis());

			assertPortalLicenseInvalid();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertFalse(logEntries.isEmpty());

			LogEntry logEntry = logEntries.getFirst();

			Assert.assertEquals(
				"DXP Production license validation failed",
				logEntry.getMessage());

			Throwable throwable = logEntry.getThrowable();

			Assert.assertNotNull(throwable);

			Assert.assertEquals(
				"Current domain is not allowed, allowed domains are: " + domain,
				throwable.getMessage());
		}
		finally {
			resetLicenseData();
		}
	}

	private void _assertDomainIsValid(String domain) throws Exception {
		try (AutoCloseable autoCloseable = _setVersion("2026.Q1.0 LTS");
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_getLicenseManagerClassName(), LoggerTestUtil.ERROR)) {

			deployFreeTierPortalLicense(
				ListUtil.fromArray(domain), StringPool.BLANK,
				System.currentTimeMillis());

			assertPortalLicenseRegistered();

			Assert.assertTrue(ListUtil.isEmpty(logCapture.getLogEntries()));
		}
		finally {
			resetLicenseData();
		}
	}

	private void _assertVersionIsInvalid(String version) throws Exception {
		try (AutoCloseable autoCloseable = _setVersion(version);
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_getLicenseManagerClassName(), LoggerTestUtil.ERROR)) {

			deployFreeTierPortalLicense();

			assertPortalLicenseInvalid();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertFalse(logEntries.isEmpty());

			LogEntry logEntry = logEntries.getFirst();

			Assert.assertEquals(
				"DXP Production license validation failed",
				logEntry.getMessage());

			Throwable throwable = logEntry.getThrowable();

			Assert.assertNotNull(throwable);

			Assert.assertEquals(
				"License is not suppported in " + version,
				throwable.getMessage());
		}
		finally {
			resetLicenseData();
		}
	}

	private void _assertVersionIsValid(String version) throws Exception {
		try (AutoCloseable autoCloseable = _setVersion(version);
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_getLicenseManagerClassName(), LoggerTestUtil.ERROR)) {

			deployFreeTierPortalLicense();

			assertPortalLicenseRegistered();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.isEmpty());
		}
		finally {
			resetLicenseData();
		}
	}

	private AutoCloseable _disableValidate() {
		ResettableClassFileTransformer resettableClassFileTransformer =
			disableValidate();

		return () -> resetClassFileTransformer(resettableClassFileTransformer);
	}

	private String _getLicenseKey(List<String> domains, long startTimeMillis)
		throws Exception {

		try (AutoCloseable autoCloseable = _disableValidate()) {
			deployFreeTierPortalLicense(
				domains, StringPool.BLANK, startTimeMillis);

			return encryptLicenseProperties(
				LicenseManagerUtil.getLicenseProperties(getPortalProductId()));
		}
		finally {
			resetLicenseData();
		}
	}

	private String _getLicenseManagerClassName() {
		return getProperty("license.manager.class.name");
	}

	private AutoCloseable _setBannedKeys(Set<String> keys) throws Exception {
		String bannedKeysFieldString = getProperty("banned.keys.field");

		String bannedFieldClassName = bannedKeysFieldString.substring(
			0, bannedKeysFieldString.lastIndexOf(CharPool.PERIOD));
		String bannedFieldFieldName = bannedKeysFieldString.substring(
			bannedKeysFieldString.lastIndexOf(CharPool.PERIOD) + 1);

		ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

		return ReflectionTestUtil.setFieldValueWithAutoCloseable(
			classLoader.loadClass(bannedFieldClassName), bannedFieldFieldName,
			keys);
	}

	private AutoCloseable _setVersion(String version) {
		ResettableClassFileTransformer resettableClassFileTransformer =
			setVersion(version);

		return () -> resetClassFileTransformer(resettableClassFileTransformer);
	}

}
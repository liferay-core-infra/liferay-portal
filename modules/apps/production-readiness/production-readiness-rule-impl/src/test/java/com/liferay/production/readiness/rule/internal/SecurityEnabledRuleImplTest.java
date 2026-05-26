/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.production.readiness.Result;

import java.io.File;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lily Chi
 */
public class SecurityEnabledRuleImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCheckReturnsFailWhenAuthenticationIsDisabled()
		throws Exception {

		Result result = _check("authenticationEnabled=B\"false\"", null, null);

		_assertFailResult(
			result, _MESSAGE_KEY_AUTHENTICATION_DISABLED_FAIL,
			"authenticationEnabled=false", "authenticationEnabled=true",
			_ELASTICSEARCH_CONFIGURATION_FILE_NAME);
	}

	@Test
	public void testCheckReturnsFailWhenConnectionAuthenticationIsDisabled()
		throws Exception {

		Result result = _check(
			"remoteClusterConnectionId=\"remote\"",
			"authenticationEnabled=B\"false\"", "remote");

		_assertFailResult(
			result, _MESSAGE_KEY_AUTHENTICATION_DISABLED_FAIL,
			"authenticationEnabled=false", "authenticationEnabled=true",
			_getConnectionConfigurationFileName("remote"));
	}

	@Test
	public void testCheckReturnsFailWhenConnectionConfigurationFileIsMissing()
		throws Exception {

		Result result = _check(
			"remoteClusterConnectionId=\"remote\"", null, "remote");

		_assertFailResult(
			result, _MESSAGE_KEY_FILE_MISSING_FAIL, null, null,
			_getConnectionConfigurationFileName("remote"));
	}

	@Test
	public void testCheckReturnsFailWhenConnectionSSLIsDisabled()
		throws Exception {

		Result result = _check(
			"remoteClusterConnectionId=\"remote\"", "httpSSLEnabled=B\"false\"",
			"remote");

		_assertFailResult(
			result, _MESSAGE_KEY_SSL_DISABLED_FAIL, "httpSSLEnabled=false",
			"httpSSLEnabled=true",
			_getConnectionConfigurationFileName("remote"));
	}

	@Test
	public void testCheckReturnsFailWhenElasticsearchConfigurationFileIsMissing()
		throws Exception {

		Result result = _check(null, null, null);

		_assertFailResult(
			result, _MESSAGE_KEY_FILE_MISSING_FAIL, null, null,
			_ELASTICSEARCH_CONFIGURATION_FILE_NAME);
	}

	@Test
	public void testCheckReturnsFailWhenSSLIsDisabled() throws Exception {
		Result result = _check("httpSSLEnabled=B\"false\"", null, null);

		_assertFailResult(
			result, _MESSAGE_KEY_SSL_DISABLED_FAIL, "httpSSLEnabled=false",
			"httpSSLEnabled=true", _ELASTICSEARCH_CONFIGURATION_FILE_NAME);
	}

	@Test
	public void testCheckReturnsPassWhenSecurityIsEnabled() throws Exception {
		Result result = _check(
			"remoteClusterConnectionId=\"remote\"",
			"authenticationEnabled=B\"true\"", "remote");

		Assert.assertEquals(Result.Status.PASS, result.getStatus());
		Assert.assertEquals(Result.Severity.HIGH, result.getSeverity());
		Assert.assertEquals(
			"search-engine-connectivity-validation", result.getCategory());
		Assert.assertEquals(_MESSAGE_KEY_PASS, result.getMessageKey());
		Assert.assertNull(result.getCurrentValue());
		Assert.assertNull(result.getRecommendedValue());
		Assert.assertEquals(0, result.getMessageParameters().length);
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _assertFailResult(
		Result result, String expectedMessageKey, String expectedCurrentValue,
		String expectedRecommendedValue, String expectedFileName) {

		Assert.assertEquals(Result.Status.FAIL, result.getStatus());
		Assert.assertEquals(Result.Severity.HIGH, result.getSeverity());
		Assert.assertEquals(
			"search-engine-connectivity-validation", result.getCategory());
		Assert.assertEquals(expectedMessageKey, result.getMessageKey());
		Assert.assertEquals(expectedCurrentValue, result.getCurrentValue());
		Assert.assertEquals(
			expectedRecommendedValue, result.getRecommendedValue());

		Object[] messageParameters = result.getMessageParameters();

		Assert.assertEquals(
			Arrays.toString(messageParameters), 1, messageParameters.length);
		Assert.assertEquals(expectedFileName, messageParameters[0]);
	}

	private Result _check(
			String elasticsearchConfigurationContent,
			String connectionConfigurationContent, String connectionId)
		throws Exception {

		if (elasticsearchConfigurationContent != null) {
			_createFile(_ELASTICSEARCH_CONFIGURATION_FILE_NAME);
		}

		if (connectionConfigurationContent != null) {
			_createFile(_getConnectionConfigurationFileName(connectionId));
		}

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LIFERAY_HOME",
					temporaryFolder.getRoot(
					).getAbsolutePath());
			MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(
				FileUtil.class)) {

			fileUtilMockedStatic.when(
				() -> FileUtil.read(Mockito.any(File.class))
			).thenAnswer(
				invocation -> {
					File file = invocation.getArgument(0);

					String fileName = file.getName();

					if (fileName.contains(
							"ElasticsearchConnectionConfiguration")) {

						return connectionConfigurationContent;
					}

					return elasticsearchConfigurationContent;
				}
			);

			Collection<Result> results = _securityEnabledRuleImpl.check(0L);

			Assert.assertEquals(results.toString(), 1, results.size());

			return results.iterator(
			).next();
		}
	}

	private void _createFile(String fileName) throws Exception {
		File file = new File(
			temporaryFolder.getRoot(), "osgi/configs/" + fileName);

		File parentFile = file.getParentFile();

		parentFile.mkdirs();

		file.createNewFile();
	}

	private String _getConnectionConfigurationFileName(String connectionId) {
		return StringBundler.concat(
			"com.liferay.portal.search.elasticsearch8.configuration.",
			"ElasticsearchConnectionConfiguration-", connectionId, ".config");
	}

	private static final String _ELASTICSEARCH_CONFIGURATION_FILE_NAME =
		"com.liferay.portal.search.elasticsearch8.configuration." +
			"ElasticsearchConfiguration.config";

	private static final String _MESSAGE_KEY_AUTHENTICATION_DISABLED_FAIL =
		"production-readiness-rule-security-enabled-authentication-disabled-" +
			"fail";

	private static final String _MESSAGE_KEY_FILE_MISSING_FAIL =
		"production-readiness-rule-security-enabled-file-missing-fail";

	private static final String _MESSAGE_KEY_PASS =
		"production-readiness-rule-security-enabled-pass";

	private static final String _MESSAGE_KEY_SSL_DISABLED_FAIL =
		"production-readiness-rule-security-enabled-ssl-disabled-fail";

	private final SecurityEnabledRuleImpl _securityEnabledRuleImpl =
		new SecurityEnabledRuleImpl();

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.io.File;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lily Chi
 */
@Component(service = ProductionReadinessRule.class)
public class SecurityEnabledRuleImpl implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		File elasticsearchConfigurationFile = _getFile(
			"com.liferay.portal.search.elasticsearch8.configuration." +
				"ElasticsearchConfiguration.config");

		if (!elasticsearchConfigurationFile.exists()) {
			return _failFileMissing(elasticsearchConfigurationFile.getName());
		}

		String elasticsearchConfigurationContent = _read(
			elasticsearchConfigurationFile);

		Result elasticsearchConfigurationResult = _checkSecurity(
			elasticsearchConfigurationContent,
			elasticsearchConfigurationFile.getName());

		if (elasticsearchConfigurationResult != null) {
			return Collections.singletonList(elasticsearchConfigurationResult);
		}

		String connectionId = _extractConnectionId(
			elasticsearchConfigurationContent);

		File connectionIdFile = _getFile(
			StringBundler.concat(
				"com.liferay.portal.search.elasticsearch8.configuration.",
				"ElasticsearchConnectionConfiguration-", connectionId,
				".config"));

		if (!connectionIdFile.exists()) {
			return _failFileMissing(connectionIdFile.getName());
		}

		Result connectionIdResult = _checkSecurity(
			_read(connectionIdFile), connectionIdFile.getName());

		if (connectionIdResult != null) {
			return Collections.singletonList(connectionIdResult);
		}

		return Collections.singletonList(
			new Result(
				Result.Status.PASS, Result.Severity.HIGH, getCategory(), null,
				null, "production-readiness-rule-security-enabled-pass",
				new Object[0], null, getKey()));
	}

	@Override
	public String getCategory() {
		return "search-engine-connectivity-validation";
	}

	@Override
	public String getKey() {
		return "security-enabled";
	}

	private Result _checkSecurity(String content, String fileName) {
		if (content.contains("authenticationEnabled=B\"false\"")) {
			return new Result(
				Result.Status.FAIL, Result.Severity.HIGH, getCategory(),
				"authenticationEnabled=false", "authenticationEnabled=true",
				"production-readiness-rule-security-enabled-authentication-" +
					"disabled-fail",
				new Object[] {fileName}, null, getKey());
		}

		if (content.contains("httpSSLEnabled=B\"false\"")) {
			return new Result(
				Result.Status.FAIL, Result.Severity.HIGH, getCategory(),
				"httpSSLEnabled=false", "httpSSLEnabled=true",
				"production-readiness-rule-security-enabled-ssl-disabled-fail",
				new Object[] {fileName}, null, getKey());
		}

		return null;
	}

	private String _extractConnectionId(String content) {
		Matcher matcher = _connectionIdPattern.matcher(content);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return "__REMOTE__";
	}

	private Collection<Result> _failFileMissing(String fileName) {
		return Collections.singletonList(
			new Result(
				Result.Status.FAIL, Result.Severity.HIGH, getCategory(), null,
				null,
				"production-readiness-rule-security-enabled-file-missing-fail",
				new Object[] {fileName}, null, getKey()));
	}

	private File _getFile(String fileName) {
		return new File(PropsValues.LIFERAY_HOME, "osgi/configs/" + fileName);
	}

	private String _read(File file) {
		try {
			return FileUtil.read(file);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return "";
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SecurityEnabledRuleImpl.class);

	private static final Pattern _connectionIdPattern = Pattern.compile(
		"remoteClusterConnectionId=\"(.*)\"");

}
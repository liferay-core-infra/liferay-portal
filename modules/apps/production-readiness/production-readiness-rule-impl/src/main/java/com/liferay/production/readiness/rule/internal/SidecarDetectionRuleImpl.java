/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.io.File;

import java.util.Collection;
import java.util.Collections;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lily Chi
 */
@Component(service = ProductionReadinessRule.class)
public class SidecarDetectionRuleImpl implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		File file = new File(
			PropsValues.LIFERAY_HOME,
			"osgi/configs/com.liferay.portal.search.elasticsearch8." +
				"configuration.ElasticsearchConfiguration.config");

		if (!file.exists() || !_isProductionModeEnabled(file)) {
			return Collections.singletonList(
				new Result(
					Result.Status.FAIL, Result.Severity.HIGH, getCategory(),
					null, null,
					"production-readiness-rule-sidecar-detection-fail",
					new Object[0], null, getKey()));
		}

		return Collections.singletonList(
			new Result(
				Result.Status.PASS, Result.Severity.HIGH, getCategory(), null,
				null, "production-readiness-rule-sidecar-detection-pass",
				new Object[0], null, getKey()));
	}

	@Override
	public String getCategory() {
		return "search-engine-connectivity-validation";
	}

	@Override
	public String getKey() {
		return "sidecar-detection";
	}

	private boolean _isProductionModeEnabled(File file) {
		try {
			String content = FileUtil.read(file);

			return content.contains("productionModeEnabled=B\"true\"");
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SidecarDetectionRuleImpl.class);

}
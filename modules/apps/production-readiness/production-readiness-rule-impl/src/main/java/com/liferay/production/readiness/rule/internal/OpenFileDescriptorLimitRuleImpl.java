/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.util.Collection;
import java.util.Collections;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lily Chi
 */
@Component(service = ProductionReadinessRule.class)
public class OpenFileDescriptorLimitRuleImpl
	implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		return Collections.singletonList(
			new Result(
				getCategory(), null,
				"https://www.elastic.co/docs/deploy-manage/deploy/self-" +
					"managed/file-descriptors",
				getKey(),
				"production-readiness-rule-open-file-descriptor-limit-message",
				new Object[0], "ulimit -n >= 65536", Result.Severity.HIGH,
				Result.Status.FAIL));
	}

	@Override
	public String getCategory() {
		return "search-engine-settings-validation";
	}

	@Override
	public String getKey() {
		return "open-file-descriptor-limit";
	}

}
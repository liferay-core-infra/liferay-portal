/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.util.Collection;
import java.util.Collections;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lily Chi
 */
@Component(service = ProductionReadinessRule.class)
public class CounterIncrementRuleImpl implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		int counterIncrement = GetterUtil.getInteger(
			PropsUtil.get("counter.increment"));

		if (counterIncrement < 2000) {
			return Collections.singletonList(
				new Result(
					getCategory(), String.valueOf(counterIncrement), null,
					getKey(),
					"production-readiness-rule-counter-increment-fail",
					new Object[0], null, Result.Severity.LOW,
					Result.Status.FAIL));
		}

		return Collections.singletonList(
			new Result(
				getCategory(), String.valueOf(counterIncrement), null, getKey(),
				"production-readiness-rule-counter-increment-pass",
				new Object[0], null, Result.Severity.LOW, Result.Status.PASS));
	}

	@Override
	public String getCategory() {
		return "portal-properties-configuration";
	}

	@Override
	public String getKey() {
		return "counter-increment";
	}

}
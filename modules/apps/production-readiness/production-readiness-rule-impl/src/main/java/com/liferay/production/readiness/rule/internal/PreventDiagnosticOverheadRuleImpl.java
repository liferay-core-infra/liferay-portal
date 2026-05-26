/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lily Chi
 */
@Component(service = ProductionReadinessRule.class)
public class PreventDiagnosticOverheadRuleImpl
	implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

		List<String> inputArguments = runtimeMXBean.getInputArguments();

		boolean unlocked = false;

		for (String arg : inputArguments) {
			if (arg.equals("-XX:+UnlockDiagnosticVMOptions")) {
				unlocked = true;

				break;
			}
		}

		if (unlocked) {
			return Collections.singletonList(
				new Result(
					Result.Status.FAIL, Result.Severity.LOW, getCategory(),
					"-XX:+UnlockDiagnosticVMOptions", null,
					"production-readiness-rule-prevent-diagnostic-overhead-" +
						"fail",
					new Object[0], null));
		}

		return Collections.singletonList(
			new Result(
				Result.Status.PASS, Result.Severity.LOW, getCategory(), null,
				null,
				"production-readiness-rule-prevent-diagnostic-overhead-pass",
				new Object[0], null));
	}

	@Override
	public String getCategory() {
		return "jvm-and-infrastructure-validation";
	}

	@Override
	public String getKey() {
		return "prevent-diagnostic-overhead";
	}

}
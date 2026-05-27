/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import java.util.Collection;
import java.util.Collections;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lily Chi
 */
@Component(service = ProductionReadinessRule.class)
public class HeapSizeUpperLimitRuleImpl implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

		MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();

		long xmxBytes = heapUsage.getMax();

		double maxMemoryGB = xmxBytes / (1024.0 * 1024.0 * 1024.0);

		if (maxMemoryGB <= 32.0) {
			return Collections.singletonList(
				new Result(
					Result.Status.PASS, Result.Severity.LOW, getCategory(),
					maxMemoryGB + "GB", null,
					"production-readiness-rule-heap-size-upper-limit-pass",
					new Object[0], null, getKey()));
		}

		return Collections.singletonList(
			new Result(
				Result.Status.FAIL, Result.Severity.LOW, getCategory(),
				maxMemoryGB + "GB", null,
				"production-readiness-rule-heap-size-upper-limit-fail",
				new Object[0], null, getKey()));
	}

	@Override
	public String getCategory() {
		return "jvm-and-infrastructure-validation";
	}

	@Override
	public String getKey() {
		return "heap-size-upper-limit";
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lily Chi
 */
@Component(service = ProductionReadinessRule.class)
public class GarbageCollectorTypeRuleImpl implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		List<GarbageCollectorMXBean> garbageCollectorMXBeans =
			ManagementFactory.getGarbageCollectorMXBeans();

		List<String> gcNames = new ArrayList<>();

		boolean pass = false;

		for (GarbageCollectorMXBean garbageCollectorMXBean :
				garbageCollectorMXBeans) {

			String name = garbageCollectorMXBean.getName();

			gcNames.add(name);

			if (name.contains("G1") || name.contains("Shenandoah") ||
				name.contains("ZGC")) {

				pass = true;
			}
		}

		String currentGCs = String.join(", ", gcNames);

		if (pass) {
			return Collections.singletonList(
				new Result(
					Result.Status.PASS, Result.Severity.LOW, getCategory(),
					currentGCs, "G1, Shenandoah, or ZGC",
					"production-readiness-rule-garbage-collector-type-pass",
					new Object[0], null));
		}

		return Collections.singletonList(
			new Result(
				Result.Status.FAIL, Result.Severity.LOW, getCategory(),
				currentGCs, "G1, Shenandoah, or ZGC",
				"production-readiness-rule-garbage-collector-type-fail",
				new Object[0], null));
	}

	@Override
	public String getCategory() {
		return "jvm-and-infrastructure-validation";
	}

	@Override
	public String getKey() {
		return "garbage-collector-type";
	}

}
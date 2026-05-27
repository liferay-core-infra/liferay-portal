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
public class JSPReloadingRuleImpl implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		boolean directServletContextReload = GetterUtil.getBoolean(
			PropsUtil.get("direct.servlet.context.reload"));

		if (directServletContextReload) {
			return Collections.singletonList(
				new Result(
					Result.Status.FAIL, Result.Severity.MEDIUM, getCategory(),
					"direct.servlet.context.reload=" +
						directServletContextReload,
					"direct.servlet.context.reload=false",
					"production-readiness-rule-jsp-reloading-fail",
					new Object[0], null, getKey()));
		}

		return Collections.singletonList(
			new Result(
				Result.Status.PASS, Result.Severity.LOW, getCategory(),
				"direct.servlet.context.reload=" + directServletContextReload,
				"direct.servlet.context.reload=false",
				"production-readiness-rule-jsp-reloading-pass", new Object[0],
				null, getKey()));
	}

	@Override
	public String getCategory() {
		return "portal-properties-configuration";
	}

	@Override
	public String getKey() {
		return "jsp-reloading";
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lily Chi
 */
@Component(service = ProductionReadinessRule.class)
public class LanguagesRuleImpl implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		List<String> availableLocales = List.of(PropsValues.LOCALES);
		List<String> betaLocales = List.of(PropsValues.LOCALES_BETA);
		List<String> enabledLocales = List.of(PropsValues.LOCALES_ENABLED);

		List<String> enabledBetaLocales = new ArrayList<>();

		for (String locale : enabledLocales) {
			if (betaLocales.contains(locale)) {
				enabledBetaLocales.add(locale);
			}
		}

		List<String> unusedLocales = new ArrayList<>();

		for (String locale : availableLocales) {
			if (!enabledLocales.contains(locale)) {
				unusedLocales.add(locale);
			}
		}

		if (enabledBetaLocales.isEmpty() && unusedLocales.isEmpty()) {
			return Collections.singletonList(
				new Result(
					Result.Status.PASS, Result.Severity.LOW, getCategory(),
					null, null, "production-readiness-rule-languages-pass",
					new Object[0], null, getKey()));
		}

		List<Result> results = new ArrayList<>(2);

		if (!enabledBetaLocales.isEmpty()) {
			results.add(
				new Result(
					Result.Status.FAIL, Result.Severity.LOW, getCategory(),
					StringUtil.merge(enabledBetaLocales), null,
					"production-readiness-rule-languages-beta-fail",
					new Object[] {
						"You are using Beta locale in production:" +
							StringUtil.merge(enabledBetaLocales)
					},
					null, getKey()));
		}

		if (!unusedLocales.isEmpty()) {
			results.add(
				new Result(
					Result.Status.FAIL, Result.Severity.LOW, getCategory(),
					StringUtil.merge(unusedLocales),
					"Remove unused locales from LOCALES " +
						"(portal-ext.properties)",
					"production-readiness-rule-languages-unused-fail",
					new Object[] {
						"Unused languages add overhead to the XML’s stored " +
							"in the database" + StringUtil.merge(unusedLocales)
					},
					null, getKey()));
		}

		return results;
	}

	@Override
	public String getCategory() {
		return "portal-properties-configuration";
	}

	@Override
	public String getKey() {
		return "languages";
	}

}
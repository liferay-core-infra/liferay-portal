/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.portlet.action;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;
import com.liferay.production.readiness.ignore.model.IgnoredRule;
import com.liferay.production.readiness.ignore.service.IgnoredRuleLocalService;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.PrintWriter;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lily Chi
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PortletKeys.SERVER_ADMIN,
		"mvc.command.name=/server_admin/get_production_readiness_results"
	},
	service = MVCResourceCommand.class
)
public class GetProductionReadinessResultsMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, ProductionReadinessRule.class);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long companyId = themeDisplay.getCompanyId();
		Locale locale = themeDisplay.getLocale();

		Map<String, IgnoredRule> ignoredRulesByKey = _getIgnoredRulesByKey(
			companyId);

		JSONArray resultsJSONArray = _jsonFactory.createJSONArray();

		int passed = 0;
		int failed = 0;
		int ignored = 0;

		for (ProductionReadinessRule productionReadinessRule :
				_serviceTrackerList) {

			Collection<Result> results = productionReadinessRule.check(
				companyId);

			if (results == null) {
				continue;
			}

			for (Result result : results) {
				IgnoredRule ignoredRule = ignoredRulesByKey.get(
					productionReadinessRule.getKey());

				boolean ignoredResult = false;

				if (ignoredRule != null) {
					ignoredResult = true;
				}

				if (ignoredResult) {
					ignored++;
				}
				else if (result.getStatus() == Result.Status.PASS) {
					passed++;
				}
				else {
					failed++;
				}

				resultsJSONArray.put(
					_toJSONObject(result, ignoredRule, locale));
			}
		}

		JSONObject summaryJSONObject = _jsonFactory.createJSONObject(
		).put(
			"failed", failed
		).put(
			"ignored", ignored
		).put(
			"passed", passed
		);

		JSONObject responseJSONObject = _jsonFactory.createJSONObject(
		).put(
			"results", resultsJSONArray
		).put(
			"summary", summaryJSONObject
		);

		resourceResponse.setContentType(ContentTypes.APPLICATION_JSON);

		PrintWriter printWriter = resourceResponse.getWriter();

		printWriter.write(responseJSONObject.toString());
	}

	private Map<String, IgnoredRule> _getIgnoredRulesByKey(long companyId) {
		Map<String, IgnoredRule> ignoredRulesByKey = new HashMap<>();

		List<IgnoredRule> ignoredRules =
			_ignoredRuleLocalService.getIgnoredRules(companyId);

		for (IgnoredRule ignoredRule : ignoredRules) {
			ignoredRulesByKey.put(ignoredRule.getRuleKey(), ignoredRule);
		}

		return ignoredRulesByKey;
	}

	private JSONObject _toJSONObject(
		Result result, IgnoredRule ignoredRule, Locale locale) {

		String message = LanguageUtil.format(
			locale, result.getMessageKey(), result.getMessageParameters(),
			false);

		JSONObject resultJSONObject = _jsonFactory.createJSONObject(
		).put(
			"category", result.getCategory()
		).put(
			"currentValue", result.getCurrentValue()
		).put(
			"docsLink", result.getDocsLink()
		).put(
			"ignored", ignoredRule != null
		).put(
			"message", message
		).put(
			"recommendedValue", result.getRecommendedValue()
		).put(
			"ruleKey", result.getKey()
		).put(
			"severity", String.valueOf(result.getSeverity())
		).put(
			"status", String.valueOf(result.getStatus())
		);

		if (ignoredRule != null) {
			resultJSONObject.put(
				"ignoredAt", ignoredRule.getCreateDate()
			).put(
				"ignoredBy", ignoredRule.getUserName()
			).put(
				"ignoreReason", ignoredRule.getReason()
			);
		}

		return resultJSONObject;
	}

	@Reference
	private IgnoredRuleLocalService _ignoredRuleLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	private ServiceTrackerList<ProductionReadinessRule> _serviceTrackerList;

}
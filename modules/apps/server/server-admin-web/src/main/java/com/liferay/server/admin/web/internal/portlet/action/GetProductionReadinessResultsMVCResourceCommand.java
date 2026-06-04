/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.portlet.action;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.server.admin.web.internal.production.readiness.ProductionReadinessResult;
import com.liferay.server.admin.web.internal.production.readiness.ProductionReadinessRuleUtil;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.File;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
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

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_checkOmniadmin(themeDisplay);

		Locale locale = themeDisplay.getLocale();

		JSONArray resultsJSONArray = _jsonFactory.createJSONArray();

		int passed = 0;
		int failed = 0;

		List<String> ignoreRules = _getIgnoreRules();

		int ignored = ignoreRules.size();

		for (ProductionReadinessResult productionReadinessResult :
				ProductionReadinessRuleUtil.check()) {

			if (productionReadinessResult == null) {
				continue;
			}

			if (!ignoreRules.contains(productionReadinessResult.getKey())) {
				if (productionReadinessResult.getStatus() ==
						ProductionReadinessResult.Status.PASS) {

					passed++;
				}
				else {
					failed++;
				}
			}

			resultsJSONArray.put(
				_toJSONObject(ignoreRules, locale, productionReadinessResult));
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

	private void _checkOmniadmin(ThemeDisplay themeDisplay) throws Exception {
		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (!permissionChecker.isOmniadmin()) {
			throw new PrincipalException.MustBeOmniadmin(
				themeDisplay.getUserId());
		}
	}

	private List<String> _getIgnoreRules() throws Exception {
		File configsDir = new File(PropsValues.LIFERAY_HOME, "osgi/configs");

		File configFile = new File(configsDir, _PID + ".config");

		if (!FileUtil.exists(configFile)) {
			return new ArrayList<>();
		}

		String configFileContent = FileUtil.read(configFile);

		String configValue = configFileContent.split(StringPool.EQUAL)[1];

		configValue = configValue.substring(1, configValue.length() - 1);

		String[] ignoreRules = configValue.split(StringPool.COMMA);

		return new ArrayList<>(Arrays.asList(ignoreRules));
	}

	private JSONObject _toJSONObject(
		List<String> ignoreRules, Locale locale,
		ProductionReadinessResult productionReadinessResult) {

		String message = LanguageUtil.format(
			locale, productionReadinessResult.getMessageKey(),
			productionReadinessResult.getMessageParameters(), false);

		return _jsonFactory.createJSONObject(
		).put(
			"category", productionReadinessResult.getCategory()
		).put(
			"categoryLabel",
			LanguageUtil.get(
				locale,
				"production-readiness-category-" +
					productionReadinessResult.getCategory())
		).put(
			"currentValue", productionReadinessResult.getCurrentValue()
		).put(
			"docsLink", _CHECKLIST_DOCS_LINK
		).put(
			"ignored", ignoreRules.contains(productionReadinessResult.getKey())
		).put(
			"message", message
		).put(
			"name",
			LanguageUtil.get(
				locale,
				"production-readiness-rule-" +
					productionReadinessResult.getKey())
		).put(
			"recommendedValue", productionReadinessResult.getRecommendedValue()
		).put(
			"ruleKey", productionReadinessResult.getKey()
		).put(
			"severity", String.valueOf(productionReadinessResult.getSeverity())
		).put(
			"status", String.valueOf(productionReadinessResult.getStatus())
		);
	}

	private static final String _CHECKLIST_DOCS_LINK =
		"https://www.liferay.com/documents/10182/3292406/Liferay+DXP+7." +
			"4+Deployment+Checklist.pdf/f3464a36-c0f0-6708-37dd-efe7b8270403?" +
				"t=1643744619710";

	private static final String _PID =
		"com.liferay.server.admin.web.internal.configuration." +
			"ProductionReadinessConfiguration";

	@Reference
	private JSONFactory _jsonFactory;

}
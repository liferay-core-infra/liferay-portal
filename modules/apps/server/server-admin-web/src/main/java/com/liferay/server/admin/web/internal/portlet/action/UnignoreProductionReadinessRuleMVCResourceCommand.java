/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.portlet.action;

import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.server.admin.web.internal.production.readiness.IgnoredRuleStore;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.PrintWriter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lily Chi
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PortletKeys.SERVER_ADMIN,
		"mvc.command.name=/server_admin/unignore_production_readiness_rule"
	},
	service = MVCResourceCommand.class
)
public class UnignoreProductionReadinessRuleMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_checkAdmin(themeDisplay);

		String ruleKey = ParamUtil.getString(resourceRequest, "ruleKey");

		_ignoredRuleStore.deleteIgnoredRule(ruleKey);

		JSONObject responseJSONObject = _jsonFactory.createJSONObject(
		).put(
			"success", true
		);

		resourceResponse.setContentType(ContentTypes.APPLICATION_JSON);

		PrintWriter printWriter = resourceResponse.getWriter();

		printWriter.write(responseJSONObject.toString());
	}

	private void _checkAdmin(ThemeDisplay themeDisplay) throws Exception {
		if (!_roleLocalService.hasUserRole(
				themeDisplay.getUserId(), themeDisplay.getCompanyId(),
				RoleConstants.ADMINISTRATOR, true)) {

			throw new PrincipalException.MustBeCompanyAdmin(
				themeDisplay.getUserId());
		}
	}

	@Reference
	private IgnoredRuleStore _ignoredRuleStore;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private RoleLocalService _roleLocalService;

}
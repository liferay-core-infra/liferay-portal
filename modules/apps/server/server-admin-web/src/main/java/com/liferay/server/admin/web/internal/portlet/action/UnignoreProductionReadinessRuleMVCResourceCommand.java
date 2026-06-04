/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.portlet.action;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

		File configsDir = new File(PropsValues.LIFERAY_HOME, "osgi/configs");

		File configFile = new File(configsDir, _PID + ".config");

		String configFileContent = FileUtil.read(configFile);

		String configValue = configFileContent.split(StringPool.EQUAL)[1];

		configValue = configValue.substring(1, configValue.length() - 1);

		String[] ignoreRules = configValue.split(StringPool.COMMA);

		if (ignoreRules.length == 1) {
			FileUtil.delete(configFile);
		}
		else {
			List<String> ignoreRuleList = new ArrayList<>(
				Arrays.asList(ignoreRules));

			ignoreRuleList.remove(
				ParamUtil.getString(resourceRequest, "ruleKey"));

			StringBundler sb = new StringBundler();

			sb.append("ignoreRules=\"");

			for (String ignoreRule : ignoreRuleList) {
				sb.append(ignoreRule);
				sb.append(StringPool.COMMA);
			}

			sb.setIndex(sb.index() - 1);

			sb.append(StringPool.QUOTE);

			FileUtil.write(configFile, sb.toString());
		}
	}

	private void _checkAdmin(ThemeDisplay themeDisplay) throws Exception {
		if (!_roleLocalService.hasUserRole(
				themeDisplay.getUserId(), themeDisplay.getCompanyId(),
				RoleConstants.ADMINISTRATOR, true)) {

			throw new PrincipalException.MustBeCompanyAdmin(
				themeDisplay.getUserId());
		}
	}

	private static final String _PID =
		"com.liferay.server.admin.web.internal.configuration." +
			"ProductionReadinessConfiguration";

	@Reference
	private RoleLocalService _roleLocalService;

}
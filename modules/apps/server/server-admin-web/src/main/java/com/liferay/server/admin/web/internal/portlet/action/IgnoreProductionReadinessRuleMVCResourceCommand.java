/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.portlet.action;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.File;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lily Chi
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PortletKeys.SERVER_ADMIN,
		"mvc.command.name=/server_admin/ignore_production_readiness_rule"
	},
	service = MVCResourceCommand.class
)
public class IgnoreProductionReadinessRuleMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_checkOmniadmin(themeDisplay);

		String ruleKey = ParamUtil.getString(resourceRequest, "ruleKey");

		File configsDir = new File(PropsValues.LIFERAY_HOME, "osgi/configs");

		if (!configsDir.exists()) {
			configsDir.mkdirs();
		}

		File configFile = new File(configsDir, _PID + ".config");

		if (FileUtil.exists(configFile)) {
			String content = FileUtil.read(configFile);

			if (!content.contains(ruleKey)) {
				String updatedContent;
				int closingQuoteIndex = content.lastIndexOf(StringPool.QUOTE);
				int openingQuoteIndex = content.indexOf(StringPool.QUOTE);

				String currentRules = content.substring(
					openingQuoteIndex + 1, closingQuoteIndex);

				if (currentRules.trim(
					).isEmpty()) {

					updatedContent =
						content.substring(0, closingQuoteIndex) + ruleKey +
							StringPool.QUOTE;
				}
				else {
					updatedContent = StringBundler.concat(
						content.substring(0, closingQuoteIndex),
						StringPool.COMMA, ruleKey, StringPool.QUOTE);
				}

				FileUtil.write(configFile, updatedContent);
			}
		}
		else {
			FileUtil.write(configFile, "ignoreRules=\"");
			FileUtil.write(configFile, ruleKey + StringPool.QUOTE, false, true);
		}
	}

	private void _checkOmniadmin(ThemeDisplay themeDisplay) throws Exception {
		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (!permissionChecker.isOmniadmin()) {
			throw new PrincipalException.MustBeOmniadmin(
				themeDisplay.getUserId());
		}
	}

	private static final String _PID =
		"com.liferay.server.admin.web.internal.configuration." +
			"ProductionReadinessConfiguration";

}
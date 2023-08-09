/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.display.context;

import com.liferay.commerce.product.content.web.internal.configuration.CPContentPortletInstanceConfiguration;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Alessio Antonio Rendina
 */
public class CPContentConfigurationDisplayContext {

	public CPContentConfigurationDisplayContext(
			HttpServletRequest httpServletRequest,
			ConfigurationProvider configurationProvider)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		_cpContentPortletInstanceConfiguration =
			configurationProvider.getPortletInstanceConfiguration(
				CPContentPortletInstanceConfiguration.class,
				themeDisplay.getLayout(), portletDisplay.getPortletId());
	}

	public String getDisplayStyle() {
		return _cpContentPortletInstanceConfiguration.displayStyle();
	}

	public long getDisplayStyleGroupId() {
		return _cpContentPortletInstanceConfiguration.displayStyleGroupId();
	}

	public String getSelectionStyle() {
		return _cpContentPortletInstanceConfiguration.selectionStyle();
	}

	public boolean isSelectionStyleADT() {
		String selectionStyle = getSelectionStyle();

		if (selectionStyle.equals("adt")) {
			return true;
		}

		return false;
	}

	public boolean isSelectionStyleCustomRenderer() {
		String selectionStyle = getSelectionStyle();

		if (selectionStyle.equals("custom")) {
			return true;
		}

		return false;
	}

	private final CPContentPortletInstanceConfiguration
		_cpContentPortletInstanceConfiguration;

}
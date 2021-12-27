/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Hai Yu
 */
public class SetupEmbeddedPortletPortletPreferencesUtil {

	public static void createEmbeddedPortletPortletPreferences(
			String portletId, Portlet portlet, String defaultPreferences,
			HttpServletRequest httpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		PortletPreferencesFactoryUtil.getLayoutPortletSetup(
			layout.getCompanyId(), layout.getGroupId(),
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, PortletKeys.PREFS_PLID_SHARED,
			portletId, defaultPreferences);

		long count =
			PortletPreferencesLocalServiceUtil.getPortletPreferencesCount(
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, themeDisplay.getPlid(),
				portletId);

		if (count < 1) {
			PortletPreferencesFactoryUtil.getLayoutPortletSetup(
				layout, portletId, defaultPreferences);
			PortletPreferencesFactoryUtil.getPortletSetup(
				httpServletRequest, portletId, defaultPreferences);

			PortletLayoutListener portletLayoutListener =
				portlet.getPortletLayoutListenerInstance();

			if (portletLayoutListener != null) {
				portletLayoutListener.onAddToLayout(
					portletId, themeDisplay.getPlid());
			}
		}
	}

}
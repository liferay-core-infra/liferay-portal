/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.google.drive.web.internal.util;

import com.liferay.document.library.opener.google.drive.web.internal.DLOpenerGoogleDriveManager;
import com.liferay.document.library.opener.google.drive.web.internal.oauth.OAuth2StateUtil;
import com.liferay.document.library.opener.oauth.OAuth2State;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PwdGenerator;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Alejandro Tardín
 */
public class GoogleDrivePortletRequestAuthorizationUtil {

	public static void performAuthorizationFlow(
			DLOpenerGoogleDriveManager dlOpenerGoogleDriveManager,
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws IOException, PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String state = PwdGenerator.getPassword(5);

		HttpServletRequest originalHttpServletRequest =
			PortalUtil.getOriginalServletRequest(
				PortalUtil.getHttpServletRequest(portletRequest));

		OAuth2StateUtil.save(
			originalHttpServletRequest,
			new OAuth2State(
				themeDisplay.getUserId(), _getSuccessURL(portletRequest),
				_getFailureURL(portletRequest), state));

		HttpServletResponse httpServletResponse =
			PortalUtil.getHttpServletResponse(portletResponse);

		String authorizationURL =
			dlOpenerGoogleDriveManager.getAuthorizationURL(
				themeDisplay.getCompanyId(), state,
				OAuth2StateUtil.getRedirectURI(
					PortalUtil.getPortalURL(portletRequest)));

		if (!dlOpenerGoogleDriveManager.hasValidCredential(
				themeDisplay.getCompanyId(), themeDisplay.getUserId())) {

			authorizationURL = HttpComponentsUtil.setParameter(
				authorizationURL, "prompt", "select_account");
		}

		httpServletResponse.sendRedirect(authorizationURL);
	}

	private static String _getFailureURL(PortletRequest portletRequest)
		throws PortalException {

		LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
			portletRequest, PortalUtil.getPortletId(portletRequest),
			PortalUtil.getControlPanelPlid(portletRequest),
			PortletRequest.RENDER_PHASE);

		return liferayPortletURL.toString();
	}

	private static String _getSuccessURL(PortletRequest portletRequest) {
		return PortalUtil.getCurrentURL(
			PortalUtil.getHttpServletRequest(portletRequest));
	}

}
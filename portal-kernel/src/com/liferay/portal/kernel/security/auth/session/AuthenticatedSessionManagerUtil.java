/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.auth.session;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Tomas Polesovsky
 */
public class AuthenticatedSessionManagerUtil {

	public static AuthenticatedSessionManager getAuthenticatedSessionManager() {
		return _authenticatedSessionManagerSnapshot.get();
	}

	public static long getAuthenticatedUserId(
			HttpServletRequest httpServletRequest, String login,
			String password, String authType)
		throws PortalException {

		AuthenticatedSessionManager authenticatedSessionManager =
			_authenticatedSessionManagerSnapshot.get();

		return authenticatedSessionManager.getAuthenticatedUserId(
			httpServletRequest, login, password, authType);
	}

	public static void login(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String login,
			String password, boolean rememberMe, String authType)
		throws Exception {

		AuthenticatedSessionManager authenticatedSessionManager =
			_authenticatedSessionManagerSnapshot.get();

		authenticatedSessionManager.login(
			httpServletRequest, httpServletResponse, login, password,
			rememberMe, authType);
	}

	public static void logout(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		AuthenticatedSessionManager authenticatedSessionManager =
			_authenticatedSessionManagerSnapshot.get();

		authenticatedSessionManager.logout(
			httpServletRequest, httpServletResponse);
	}

	public static HttpSession renewSession(
			HttpServletRequest httpServletRequest, HttpSession httpSession)
		throws Exception {

		AuthenticatedSessionManager authenticatedSessionManager =
			_authenticatedSessionManagerSnapshot.get();

		return authenticatedSessionManager.renewSession(
			httpServletRequest, httpSession);
	}

	public static void signOutSimultaneousLogins(long userId) throws Exception {
		AuthenticatedSessionManager authenticatedSessionManager =
			_authenticatedSessionManagerSnapshot.get();

		authenticatedSessionManager.signOutSimultaneousLogins(userId);
	}

	private AuthenticatedSessionManagerUtil() {
	}

	private static final Snapshot<AuthenticatedSessionManager>
		_authenticatedSessionManagerSnapshot = new Snapshot<>(
			AuthenticatedSessionManagerUtil.class,
			AuthenticatedSessionManager.class);

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.auth.tunnel;

import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.auth.AuthException;

import java.net.HttpURLConnection;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Tomas Polesovsky
 */
public class TunnelAuthenticationManagerUtil {

	public static long getUserId(HttpServletRequest httpServletRequest)
		throws AuthException {

		TunnelAuthenticationManager tunnelAuthenticationManager =
			_tunnelAuthenticationManagerSnapshot.get();

		return tunnelAuthenticationManager.getUserId(httpServletRequest);
	}

	public static void setCredentials(
			String login, HttpURLConnection httpURLConnection)
		throws Exception {

		TunnelAuthenticationManager tunnelAuthenticationManager =
			_tunnelAuthenticationManagerSnapshot.get();

		tunnelAuthenticationManager.setCredentials(login, httpURLConnection);
	}

	private TunnelAuthenticationManagerUtil() {
	}

	private static final Snapshot<TunnelAuthenticationManager>
		_tunnelAuthenticationManagerSnapshot = new Snapshot<>(
			TunnelAuthenticationManagerUtil.class,
			TunnelAuthenticationManager.class);

}
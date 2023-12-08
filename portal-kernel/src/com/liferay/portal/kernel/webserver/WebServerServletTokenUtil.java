/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.webserver;

import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * @author Brian Wing Shun Chan
 * @since  6.1, replaced com.liferay.portal.kernel.servlet.ImageServletTokenUtil
 */
public class WebServerServletTokenUtil {

	public static String getToken(long imageId) {
		return _getWebServerServletToken().getToken(imageId);
	}

	public static WebServerServletToken getWebServerServletToken() {
		return _getWebServerServletToken();
	}

	public static void resetToken(long imageId) {
		_getWebServerServletToken().resetToken(imageId);
	}

	private static WebServerServletToken _getWebServerServletToken() {
		return _webServerServletTokenSnapshot.get();
	}

	private static final Snapshot<WebServerServletToken>
		_webServerServletTokenSnapshot = new Snapshot<>(
			WebServerServletTokenUtil.class, WebServerServletToken.class);

}
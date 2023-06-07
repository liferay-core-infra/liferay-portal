/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.loader.modules.extender.internal.servlet.util;

import com.liferay.petra.string.StringPool;

import java.util.UUID;

/**
 * @author Joao Victor Alves
 */
public class JSLoaderModulesUtil {

	public static String getExpectedPathInfo() {
		return _expectedPathInfo;
	}

	public static String getURL() {
		return _url;
	}

	public static void updateJSLoaderProps() {
		String hash = String.valueOf(UUID.randomUUID());

		_expectedPathInfo = StringPool.SLASH + hash;
		_url = "/js_resolve_modules/" + hash;
	}

	private static volatile String _expectedPathInfo;
	private static volatile String _url;

}
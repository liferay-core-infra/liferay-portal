/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ryan Park
 * @author Brian Wing Shun Chan
 */
public class CustomJspRegistryUtil {

	public static String getCustomJspFileName(
		String servletContextName, String fileName) {

		int pos = fileName.lastIndexOf(CharPool.PERIOD);

		if (pos == -1) {
			return StringBundler.concat(
				fileName, StringPool.PERIOD, servletContextName);
		}

		return StringBundler.concat(
			fileName.substring(0, pos), CharPool.PERIOD, servletContextName,
			fileName.substring(pos));
	}

	public static String getDisplayName(String servletContextName) {
		return _servletContextNames.get(servletContextName);
	}

	public static Set<String> getServletContextNames() {
		return _servletContextNames.keySet();
	}

	public static void registerServletContextName(
		String servletContextName, String displayName) {

		_servletContextNames.put(servletContextName, displayName);
	}

	public static void unregisterServletContextName(String servletContextName) {
		_servletContextNames.remove(servletContextName);
	}

	private static final Map<String, String> _servletContextNames =
		new ConcurrentHashMap<>();

}
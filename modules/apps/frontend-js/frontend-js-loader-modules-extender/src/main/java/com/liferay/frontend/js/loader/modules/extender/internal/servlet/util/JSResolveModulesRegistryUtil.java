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

package com.liferay.frontend.js.loader.modules.extender.internal.servlet.util;

/**
 * @author Joao Victor Alves
 */
public class JSResolveModulesRegistryUtil {

	public static String getExpectedPathInfo() {
		return _expectedPathInfo;
	}

	public static String getUrl() {
		return _url;
	}

	public static void setExpectedPathInfo(String expectedPathInfo) {
		_expectedPathInfo = expectedPathInfo;
	}

	public static void setUrl(String url) {
		_url = url;
	}

	private static volatile String _expectedPathInfo;
	private static volatile String _url;

}
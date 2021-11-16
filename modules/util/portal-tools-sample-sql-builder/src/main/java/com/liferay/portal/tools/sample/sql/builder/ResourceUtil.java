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

package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lily Chi
 */
public class ResourceUtil {

	public static InputStream getFragmentComponentInputStream(
			String fragmentName, String suffix, Class<?> clazz)
		throws Exception {

		ClassLoader classLoader = clazz.getClassLoader();

		URL url = classLoader.getResource(
			StringBundler.concat(
				"com/liferay/fragment/collection/contributor/basic/component",
				"/dependencies/", fragmentName, "/index.", suffix));

		return url.openStream();
	}

	public static InputStream getResourceInputStream(
		String resourceName, Class<?> clazz) {

		ClassLoader classLoader = clazz.getClassLoader();

		return classLoader.getResourceAsStream(
			_DEPENDENCIES_DIR + resourceName);
	}

	public static String readFile(InputStream inputStream) throws Exception {
		List<String> lines = new ArrayList<>();

		StringUtil.readLines(inputStream, lines);

		return StringUtil.merge(lines, StringPool.SPACE);
	}

	public static String readFile(String resourceName, Class<?> clazz)
		throws Exception {

		return readFile(getResourceInputStream(resourceName, clazz));
	}

	private static final String _DEPENDENCIES_DIR =
		"com/liferay/portal/tools/sample/sql/builder/dependencies/data/";

}
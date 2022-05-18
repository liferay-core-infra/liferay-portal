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

package com.liferay.portal.tools.db.upgrade.client;

import java.io.File;
import java.io.ObjectInputStream;

import java.lang.reflect.Method;

import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Dante Wang
 */
public class DBUpgraderLauncher {

	public static void main(String[] args) throws Exception {
		ObjectInputStream objectInputStream = new ObjectInputStream(System.in);

		String classPath = (String)objectInputStream.readObject();

		Map<String, String> systemProperties =
			(Map<String, String>)objectInputStream.readObject();

		ClassLoader classLoader = new URLClassLoader(
			_getClassPathURLs(classPath));

		Class<?> dbUpgraderClass = classLoader.loadClass(
			"com.liferay.portal.tools.DBUpgrader");

		Method mainMethod = dbUpgraderClass.getMethod("main", String[].class);

		Class<?> systemPropertiesClass = classLoader.loadClass(
			"com.liferay.portal.kernel.util.SystemProperties");

		Method setMethod = systemPropertiesClass.getMethod(
			"set", String.class, String.class);

		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		currentThread.setContextClassLoader(classLoader);

		try {
			for (Map.Entry<String, String> entry :
					systemProperties.entrySet()) {

				setMethod.invoke(null, entry.getKey(), entry.getValue());
			}

			mainMethod.invoke(null, new Object[] {args});
		}
		finally {
			currentThread.setContextClassLoader(contextClassLoader);
		}
	}

	private static URL[] _getClassPathURLs(String classPath) throws Exception {
		String[] paths = classPath.split(File.pathSeparator);

		Set<URL> urls = new LinkedHashSet<>();

		for (String path : paths) {
			File file = new File(path);

			URI uri = file.toURI();

			urls.add(uri.toURL());
		}

		return urls.toArray(new URL[0]);
	}

}
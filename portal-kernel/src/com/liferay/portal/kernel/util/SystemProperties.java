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

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 * @author Mirco Tamburini
 * @author Brett Randall
 * @author Shuyang Zhou
 */
public class SystemProperties {

	public static final String SYSTEM_PROPERTIES_QUIET =
		"system.properties.quiet";

	public static final String SYSTEM_PROPERTIES_SET = "system.properties.set";

	public static final String SYSTEM_PROPERTIES_SET_OVERRIDE =
		"system.properties.set.override";

	public static final String TMP_DIR = "java.io.tmpdir";

	public static void clear(String key) {
		System.clearProperty(key);

		_properties.remove(key);
	}

	public static String get(String key) {
		return _parseProperty(_get(key));
	}

	public static String get(String key, String defaultValue) {
		String value = _parseProperty(_get(key));

		if (value == null) {
			return defaultValue;
		}

		return value;
	}

	public static String[] getArray(String key) {
		return _arrayValues.computeIfAbsent(key, k -> StringUtil.split(get(k)));
	}

	public static Map<String, String> getProperties(
		String prefix, boolean removePrefix) {

		Map<String, String> properties = new HashMap<>();

		for (Map.Entry<String, String> entry : _properties.entrySet()) {
			String key = entry.getKey();

			if (key.startsWith(prefix)) {
				if (removePrefix) {
					key = key.substring(prefix.length());
				}

				properties.put(key, _parseProperty(entry.getValue()));
			}
		}

		return properties;
	}

	public static Set<String> getPropertyNames() {
		return Collections.unmodifiableSet(_properties.keySet());
	}

	public static void load(ClassLoader classLoader) {
		ExtendedProperties properties = new ExtendedProperties();

		List<URL> urls = null;

		if (!GetterUtil.getBoolean(
				System.getProperty(SYSTEM_PROPERTIES_QUIET))) {

			urls = new ArrayList<>();
		}

		// system.properties

		try {
			Enumeration<URL> enumeration = classLoader.getResources(
				"system.properties");

			while (enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();

				try (InputStream inputStream = url.openStream()) {
					properties.load(inputStream);
				}

				if (urls != null) {
					urls.add(url);
				}
			}
		}
		catch (IOException ioException) {
			throw new ExceptionInInitializerError(ioException);
		}

		// system-ext.properties

		try {
			Enumeration<URL> enumeration = classLoader.getResources(
				"system-ext.properties");

			while (enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();

				try (InputStream inputStream = url.openStream()) {
					properties.load(inputStream);
				}

				if (urls != null) {
					urls.add(url);
				}
			}
		}
		catch (IOException ioException) {
			throw new ExceptionInInitializerError(ioException);
		}

		// Set environment properties

		SystemEnv.setProperties(properties);

		// Default liferay home directory

		properties.put(
			SystemPropsKeys.DEFAULT_LIFERAY_HOME, _getDefaultLiferayHome());

		// Set system properties

		if (GetterUtil.getBoolean(
				System.getProperty(SYSTEM_PROPERTIES_SET), true)) {

			boolean systemPropertiesSetOverride = GetterUtil.getBoolean(
				System.getProperty(SYSTEM_PROPERTIES_SET_OVERRIDE), true);

			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				String key = String.valueOf(entry.getKey());

				if (systemPropertiesSetOverride ||
					Validator.isNull(System.getProperty(key))) {

					System.setProperty(key, String.valueOf(entry.getValue()));
				}
			}

			if (!systemPropertiesSetOverride) {
				Properties systemProperties = System.getProperties();

				for (Map.Entry<Object, Object> entry :
						systemProperties.entrySet()) {

					String key = String.valueOf(entry.getKey());

					if (Validator.isNotNull(properties.get(key))) {
						properties.put(key, entry.getValue());
					}
				}
			}
		}

		// Use a fast concurrent hash map implementation instead of the slower
		// java.util.Properties

		PropertiesUtil.fromProperties(properties, _properties);

		if (urls != null) {
			for (URL url : urls) {
				System.out.println("Loading " + url);
			}
		}
	}

	public static void set(String key, String value) {
		System.setProperty(key, value);

		_properties.put(key, value);
	}

	private static String _get(String key) {
		String value = _properties.get(key);

		if (value == null) {
			value = System.getProperty(key);
		}

		return value;
	}

	private static String _getDefaultLiferayHome() {
		String defaultLiferayHome = null;

		if (ServerDetector.isJBoss()) {
			defaultLiferayHome = get("jboss.home.dir") + "/..";
		}
		else if (ServerDetector.isWebLogic()) {
			defaultLiferayHome = get("env.DOMAIN_HOME") + "/..";
		}
		else if (ServerDetector.isTomcat()) {
			defaultLiferayHome = get("catalina.base") + "/..";
		}
		else {
			defaultLiferayHome = get("user.dir") + "/liferay";
		}

		defaultLiferayHome = StringUtil.replace(
			defaultLiferayHome, CharPool.BACK_SLASH, CharPool.SLASH);

		defaultLiferayHome = StringUtil.replace(
			defaultLiferayHome, StringPool.DOUBLE_SLASH, StringPool.SLASH);

		if (defaultLiferayHome.endsWith("/..")) {
			int pos = defaultLiferayHome.lastIndexOf(
				CharPool.SLASH, defaultLiferayHome.length() - 4);

			if (pos != -1) {
				defaultLiferayHome = defaultLiferayHome.substring(0, pos);
			}
		}

		return defaultLiferayHome;
	}

	private static String _parseProperty(String value) {
		return _replacePlaceholders(value);
	}

	private static String _replacePlaceholders(String value) {
		if (value == null) {
			return value;
		}

		int startIndex = value.indexOf(StringPool.DOLLAR_AND_OPEN_CURLY_BRACE);

		if (startIndex != -1) {
			int endIndex = value.indexOf(
				StringPool.CLOSE_CURLY_BRACE, startIndex);

			if (endIndex != -1) {
				String placeholderKey = value.substring(
					startIndex +
						StringPool.DOLLAR_AND_OPEN_CURLY_BRACE.length(),
					endIndex);

				String placeholderValue = _get(placeholderKey);

				if (placeholderValue == null) {
					placeholderValue = StringPool.BLANK;
				}
				else {
					placeholderValue = _replacePlaceholders(placeholderValue);
				}

				String newValue = StringUtil.replace(
					value,
					StringPool.DOLLAR_AND_OPEN_CURLY_BRACE + placeholderKey +
						StringPool.CLOSE_CURLY_BRACE,
					placeholderValue, startIndex);

				value = _replacePlaceholders(newValue);
			}
		}

		return value;
	}

	private static final Map<String, String[]> _arrayValues =
		new ConcurrentHashMap<>();
	private static final Map<String, String> _properties =
		new ConcurrentHashMap<>();

	static {
		Thread currentThread = Thread.currentThread();

		ClassLoader classLoader = currentThread.getContextClassLoader();

		load(classLoader);
	}

	private static class ExtendedProperties extends Properties {

		@Override
		public void load(InputStream inputStream) throws IOException {
			try (UnsyncBufferedReader unsyncBufferedReader =
					new UnsyncBufferedReader(
						new InputStreamReader(inputStream))) {

				String line = null;

				StringBundler multiLineSB = new StringBundler();

				while ((line = unsyncBufferedReader.readLine()) != null) {
					line = line.trim();

					// Comment line, Empty line or "\"

					if (line.startsWith(StringPool.POUND) || line.isEmpty() ||
						line.equals(StringPool.BACK_SLASH)) {

						continue;
					}

					// Line ending with "\" indicates multi-line property

					if (line.endsWith(StringPool.BACK_SLASH)) {
						line = line.substring(0, line.length() - 1);

						multiLineSB.append(line);

						continue;
					}

					// Not ending with "\" -- end of multi-line property

					if (multiLineSB.index() != 0) {
						multiLineSB.append(line);

						_setProperty(multiLineSB.toString());

						multiLineSB.setIndex(0);

						continue;
					}

					// Single line property

					_setProperty(line);
				}
			}
		}

		private void _setProperty(String line) {
			int index = line.indexOf(CharPool.EQUAL);

			if (index < 0) {
				return;
			}

			setProperty(line.substring(0, index), line.substring(index + 1));
		}

	}

}
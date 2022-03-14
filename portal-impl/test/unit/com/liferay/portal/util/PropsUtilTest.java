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

package com.liferay.portal.util;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.net.URL;

import java.nio.file.Path;

import java.util.List;
import java.util.logging.Level;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Julius Lee
 */
public class PropsUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetLibDir() {
		ClassLoader classLoader = PropsUtilTest.class.getClassLoader();

		URL url = classLoader.getResource("java/lang/String.class");

		Path path = ReflectionTestUtil.invoke(
			PropsUtil.class, "_getPathFromURL", new Class<?>[] {URL.class},
			url);

		String expectedPath = path.toString();

		int pos = expectedPath.lastIndexOf("!/");

		expectedPath = expectedPath.substring(0, pos);

		pos = expectedPath.lastIndexOf("/");

		expectedPath = expectedPath.substring(0, pos + 1);

		String actualPath = PropsUtil.getLibDir(
			classLoader, "java.lang.String.class");

		Assert.assertEquals(expectedPath, actualPath);

		actualPath = PropsUtil.getLibDir(classLoader, "java.lang.String.class");

		Assert.assertEquals(expectedPath, actualPath);

		// Test log output

		try (LogCapture logCapture = LoggerTestUtil.configureJDKLogger(
				PropsUtil.class.getName(), Level.FINE)) {

			PropsUtil.getLibDir(classLoader, "java.lang.String");

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 2, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Class name java.lang.String", logEntry.getMessage());

			logEntry = logEntries.get(1);

			Assert.assertEquals(
				"Path " + path.toString(), logEntry.getMessage());
		}
	}

}
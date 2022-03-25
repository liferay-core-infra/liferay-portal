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

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.logging.Level;

import org.junit.Assert;
import org.junit.BeforeClass;
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

	@BeforeClass
	public static void setUpClass() {
		URL.setURLStreamHandlerFactory(
			protocol -> {
				if (protocol.equals("vfs") || protocol.equals("zip") ||
					protocol.equals("bundleresource") ||
					protocol.equals("wsjar")) {

					return new URLStreamHandler() {

						protected URLConnection openConnection(URL url) {
							return new URLConnection(url) {

								public void connect() {
									throw new UnsupportedOperationException(
										"protocol not supported");
								}

							};
						}

					};
				}

				return null;
			});
	}

	@Test
	public void testGetLibDir() {
		ClassLoader classLoader = PropsUtilTest.class.getClassLoader();

		URL url = classLoader.getResource("java/lang/String.class");

		String expectedPath = url.getPath();

		if (expectedPath.startsWith("file:")) {
			expectedPath = expectedPath.substring(5);
		}

		int pos = expectedPath.lastIndexOf("!/");

		expectedPath = expectedPath.substring(0, pos);

		pos = expectedPath.lastIndexOf("/");

		expectedPath = expectedPath.substring(0, pos + 1);

		String actualPath = ReflectionTestUtil.invoke(
			PropsUtil.class, "_getLibDir",
			new Class<?>[] {ClassLoader.class, String.class}, classLoader,
			"java/lang/String.class");

		Assert.assertEquals(expectedPath, actualPath);

		actualPath = ReflectionTestUtil.invoke(
			PropsUtil.class, "_getLibDir",
			new Class<?>[] {ClassLoader.class, String.class}, classLoader,
			"java/lang/String");

		Assert.assertEquals(expectedPath, actualPath);
	}

	@Test
	public void testGetPathURIFromURL() throws Exception {

		// Tomcat

		_testGetPathFromURL("jar:file:", "jar:file:/");
		_testGetPathFromURL(
			new URL(
				"file:/opt/liferay/tomcat/classes/javax/servlet/Servlet.class"),
			"/opt/liferay/tomcat/classes/javax/servlet/Servlet.class");
		_testGetPathFromURL(
			new URL(
				"file:/C:/Liferay/tomcat/classes/javax/servlet/Servlet.class"),
			"/C:/Liferay/tomcat/classes/javax/servlet/Servlet.class");

		// Weblogic

		_testGetPathFromURL("zip:", "zip:");

		// Websphere

		_testGetPathFromURL("wsjar:file:", "wsjar:file:/");
		_testGetPathFromURL(
			new URL(
				"bundleresource://266.fwk-486185329/javax/servlet/Servlet." +
					"class"),
			"/javax/servlet/Servlet.class");

		// Wildfly

		_testGetPathFromURL("vfs:", "vfs:/");

		// logging

		try (LogCapture logCapture = LoggerTestUtil.configureJDKLogger(
				PropsUtil.class.getName(), Level.FINE)) {

			ReflectionTestUtil.invoke(
				PropsUtil.class, "_getPathFromURL", new Class<?>[] {URL.class},
				new URL(
					"jar:file:/opt/liferay/tomcat/lib/servlet-api.jar" +
						"!/javax/servlet/Servlet.class"));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"URI file:/opt/liferay/tomcat/lib/servlet-api.jar!/javax" +
					"/servlet/Servlet.class",
				logEntry.getMessage());
		}
	}

	@Test
	public void testGetPathURIFromURLWithIllegalCharacter() {
		try {
			ReflectionTestUtil.invoke(
				PropsUtil.class, "_getPathFromURL", new Class<?>[] {URL.class},
				new URL(
					"jar:file:/[opt/liferay/tomcat/lib/servlet-api.jar" +
						"!/javax/servlet/Servlet.class"));

			Assert.fail(
				"SystemException caused by URISyntaxException should be " +
					"thrown because of the illegal character '['");
		}
		catch (Exception exception) {
			Assert.assertSame(SystemException.class, exception.getClass());

			Throwable throwable = exception.getCause();

			Assert.assertSame(URISyntaxException.class, throwable.getClass());
		}
	}

	@Test
	public void testGetPathURIFromURLWithUnknownProtocol() {
		try {
			ReflectionTestUtil.invoke(
				PropsUtil.class, "_getPathFromURL", new Class<?>[] {URL.class},
				new URL(
					"jar", null, -1,
					"unknown:/opt/liferay/tomcat/lib/servlet-api.jar!/javax" +
						"/servlet/Servlet.class",
					null));

			Assert.fail(
				"SystemException caused by MalformedURLException should be " +
					"thrown because of the unknown protocol");
		}
		catch (Exception exception) {
			Assert.assertSame(SystemException.class, exception.getClass());

			Throwable throwable = exception.getCause();

			Assert.assertSame(
				MalformedURLException.class, throwable.getClass());
		}
	}

	private void _testGetPathFromURL(
			String linuxProtocol, String windowsProtocol)
		throws Exception {

		_testGetPathFromURL(
			new URL(
				linuxProtocol + "/opt/liferay/tomcat/lib/servlet-api.jar" +
					"!/javax/servlet/Servlet.class"),
			"/opt/liferay/tomcat/lib/servlet-api.jar" +
				"!/javax/servlet/Servlet.class");
		_testGetPathFromURL(
			new URL(
				linuxProtocol + "/opt/with%20space/tomcat/lib/servlet-api.jar" +
					"!/javax/servlet/Servlet.class"),
			"/opt/with space/tomcat/lib/servlet-api.jar" +
				"!/javax/servlet/Servlet.class");
		_testGetPathFromURL(
			new URL(
				windowsProtocol + "C:/Liferay/tomcat/lib/servlet-api.jar" +
					"!/javax/servlet/Servlet.class"),
			"/C:/Liferay/tomcat/lib/servlet-api.jar" +
				"!/javax/servlet/Servlet.class");
		_testGetPathFromURL(
			new URL(
				windowsProtocol + "C:/With%20Space/tomcat/lib/servlet-api.jar" +
					"!/javax/servlet/Servlet.class"),
			"/C:/With Space/tomcat/lib/servlet-api.jar" +
				"!/javax/servlet/Servlet.class");
	}

	private void _testGetPathFromURL(URL url, String expectedPath) {
		Path path = ReflectionTestUtil.invoke(
			PropsUtil.class, "_getPathFromURL", new Class<?>[] {URL.class},
			url);

		Assert.assertEquals(Paths.get(expectedPath), path);
	}

}
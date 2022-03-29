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
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import junit.framework.TestResult;

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
		ClassLoader classLoader = PropsUtil.class.getClassLoader();

		URL url = classLoader.getResource("junit/framework/TestResult.class");

		String expectedPath = url.getPath();

		if (expectedPath.startsWith("file:")) {
			expectedPath = expectedPath.substring(5);
		}

		int pos = expectedPath.lastIndexOf("!/");

		expectedPath = expectedPath.substring(0, pos);

		pos = expectedPath.lastIndexOf("/");

		expectedPath = expectedPath.substring(0, pos + 1);

		String actualPath = ReflectionTestUtil.invoke(
			PropsUtil.class, "_getLibDir", new Class<?>[] {Class.class},
			TestResult.class);

		Assert.assertEquals(expectedPath, actualPath);
	}

}
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

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.net.URL;

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

		String expectedPath = url.getPath();

		if (expectedPath.startsWith("file:")) {
			expectedPath = expectedPath.substring(5);
		}

		int pos = expectedPath.lastIndexOf("!/");

		expectedPath = expectedPath.substring(0, pos);

		pos = expectedPath.lastIndexOf("/");

		expectedPath = expectedPath.substring(0, pos + 1);

		String actualPath = PropsUtil.getLibDir(
			classLoader, "java/lang/String.class");

		Assert.assertEquals(expectedPath, actualPath);

		actualPath = PropsUtil.getLibDir(classLoader, "java/lang/String");

		Assert.assertEquals(expectedPath, actualPath);
	}

}
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

package com.liferay.petra.http.invoker;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Jiefeng Wu
 */
public class HttpInvokerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testFilterFileName() {
		String fileNameWithLineBreak = "\nhttp://www.liferay.com\n";

		Assert.assertEquals(
			"http://www.liferay.com",
			ReflectionTestUtil.invoke(
				HttpInvoker.newHttpInvoker(), "_filter",
				new Class<?>[] {String.class}, fileNameWithLineBreak));

		String fileNameWithDoubleQuote = "\"http://www.liferay.com\"";

		Assert.assertEquals(
			"http://www.liferay.com",
			ReflectionTestUtil.invoke(
				HttpInvoker.newHttpInvoker(), "_filter",
				new Class<?>[] {String.class}, fileNameWithDoubleQuote));

		String fileNameWithCarriageReturn = "\rhttp://www.liferay.com\r";

		Assert.assertEquals(
			"http://www.liferay.com",
			ReflectionTestUtil.invoke(
				HttpInvoker.newHttpInvoker(), "_filter",
				new Class<?>[] {String.class}, fileNameWithCarriageReturn));

		String fileNameWithMixed = "\r\"\nhttp://www.liferay.com\n\"\n";

		Assert.assertEquals(
			"http://www.liferay.com",
			ReflectionTestUtil.invoke(
				HttpInvoker.newHttpInvoker(), "_filter",
				new Class<?>[] {String.class}, fileNameWithMixed));

		Assert.assertNotEquals("http://www.liferay.com", fileNameWithMixed);
	}

}
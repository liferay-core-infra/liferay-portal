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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Tina Tian
 */
public class SystemPropertiesTest {

	@Test
	public void testGetSetAndClear() {
		Assert.assertNull(SystemProperties.get(_TEST_KEY));

		SystemProperties.set(_TEST_KEY, _TEST_VALUE);

		Assert.assertEquals(_TEST_VALUE, SystemProperties.get(_TEST_KEY));

		SystemProperties.clear(_TEST_KEY);

		Assert.assertNull(SystemProperties.get(_TEST_KEY));
	}

	@Test
	public void testGetSetAndClearWithReference() {
		Assert.assertNull(SystemProperties.get(_TEST_KEY));

		SystemProperties.set(
			"test.reference.key", "value1.${test.reference.key.1}");
		SystemProperties.set(
			"test.reference.key.1", "${test.reference.key.2}.value2");
		SystemProperties.set(
			"test.reference.key.2", "${test.reference.key.3}.${}");
		SystemProperties.set("test.reference.key.3", _TEST_VALUE);

		SystemProperties.set(_TEST_KEY, "${test.reference.key}");

		Assert.assertEquals(
			"value1." + _TEST_VALUE + ".${}.value2",
			SystemProperties.get(_TEST_KEY));

		SystemProperties.clear("test.reference.key.3");

		Assert.assertEquals(
			"value1.${test.reference.key.3}.${}.value2",
			SystemProperties.get(_TEST_KEY));

		SystemProperties.clear("test.reference.key.2");

		Assert.assertEquals(
			"value1.${test.reference.key.2}.value2",
			SystemProperties.get(_TEST_KEY));

		SystemProperties.clear("test.reference.key.1");

		Assert.assertEquals(
			"value1.${test.reference.key.1}", SystemProperties.get(_TEST_KEY));

		SystemProperties.clear("test.reference.key");

		Assert.assertEquals(
			"${test.reference.key}", SystemProperties.get(_TEST_KEY));

		SystemProperties.clear(_TEST_KEY);

		Assert.assertNull(SystemProperties.get(_TEST_KEY));
	}

	private static final String _TEST_KEY = "test.key";

	private static final String _TEST_VALUE = "test.value";

}
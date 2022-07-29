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

import com.liferay.portal.kernel.test.ReflectionTestUtil;

import java.io.IOException;

import java.net.URL;

import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jiaxu Wei
 */
public class SystemPropertiesTest {

	@After
	public void tearDown() {
		Map<String, String> properties = SystemProperties.getProperties(
			_PREFIX, false);

		for (String propertyKey : properties.keySet()) {
			SystemProperties.clear(propertyKey);
		}
	}

	@Test
	public void testGetArray() {
		Assert.assertTrue(
			ArrayUtil.isEmpty(SystemProperties.getArray(_TEST_KEY)));

		SystemProperties.set(_TEST_KEY, "test.array.value,test.array.value");

		Assert.assertArrayEquals(
			new String[] {"test.array.value", "test.array.value"},
			SystemProperties.getArray(_TEST_KEY));
	}

	@Test
	public void testGetProperties() {
		Properties properties1 = SystemProperties.getProperties();

		Assert.assertNull(properties1.getProperty(_TEST_KEY));

		SystemProperties.set(_TEST_KEY, _TEST_VALUE);

		Properties properties2 = SystemProperties.getProperties();

		Assert.assertEquals(properties1.size() + 1, properties2.size());

		Assert.assertNull(properties1.getProperty(_TEST_KEY));
		Assert.assertEquals(_TEST_VALUE, properties2.get(_TEST_KEY));
	}

	@Test
	public void testGetPropertiesWithPrefix() {
		Map<String, String> propertiesWithPrefix =
			SystemProperties.getProperties(_PREFIX, false);

		Assert.assertTrue(propertiesWithPrefix.isEmpty());

		SystemProperties.set(_TEST_KEY, _TEST_VALUE);

		Map<String, String> propertiesWithoutPrefix =
			SystemProperties.getProperties(_PREFIX, true);

		Assert.assertEquals(
			new HashMapBuilder<String, String>().put(
				_KEY, _TEST_VALUE
			).build(),
			propertiesWithoutPrefix);

		propertiesWithPrefix = SystemProperties.getProperties(_PREFIX, false);

		Assert.assertEquals(
			new HashMapBuilder<String, String>().put(
				_TEST_KEY, _TEST_VALUE
			).build(),
			propertiesWithPrefix);
	}

	@Test
	public void testGetSetAndClear() {
		Assert.assertNull(SystemProperties.get(_TEST_KEY));

		// Property set via SystemProperties is also set to System.props

		SystemProperties.set(_TEST_KEY, _TEST_VALUE);

		Assert.assertEquals(_TEST_VALUE, SystemProperties.get(_TEST_KEY));
		Assert.assertEquals(_TEST_VALUE, System.getProperty(_TEST_KEY));

		// Property cleared via SystemProperties is also removed from
		// System.props

		SystemProperties.clear(_TEST_KEY);

		Assert.assertNull(SystemProperties.get(_TEST_KEY));
		Assert.assertNull(System.getProperty(_TEST_KEY));

		// Property in System.props is also accessible via SystemProperties

		System.setProperty(_TEST_KEY, _TEST_VALUE);

		Assert.assertEquals(_TEST_VALUE, SystemProperties.get(_TEST_KEY));
		Assert.assertEquals(_TEST_VALUE, System.getProperty(_TEST_KEY));

		System.clearProperty(_TEST_KEY);
	}

	@Test
	public void testGetWithDefaultValue() {
		Assert.assertNull(SystemProperties.get(_TEST_KEY));

		Assert.assertEquals(
			"defaultValue", SystemProperties.get(_TEST_KEY, "defaultValue"));

		SystemProperties.set(_TEST_KEY, _TEST_VALUE);

		Assert.assertEquals(
			_TEST_VALUE, SystemProperties.get(_TEST_KEY, "defaultValue"));
	}

	@Test
	public void testLoad() throws IOException {
		Properties properties = new Properties();

		ReflectionTestUtil.invoke(
			SystemProperties.class, "_load",
			new Class<?>[] {URL.class, Properties.class},
			SystemProperties.class.getResource(
				"dependencies/multiline-comment.properties"),
			properties);

		Assert.assertEquals(_TEST_VALUE, properties.get(_TEST_KEY));
	}

	@Test
	public void testReference() {

		// Properties that refer to other Properties can also be parsed
		// by SystemProperties

		SystemProperties.set("test.SystemProperties.test.key", "test.value");
		Assert.assertEquals(
			"test.value",
			SystemProperties.get("test.SystemProperties.test.key"));

		// Simple reference

		Assert.assertNull(
			SystemProperties.get("test.SystemProperties.test.reference.key"));

		SystemProperties.set(
			"test.SystemProperties.test.reference.key",
			"${test.SystemProperties.test.key}");

		Assert.assertEquals(
			"test.value",
			SystemProperties.get("test.SystemProperties.test.reference.key"));

		// Blank reference

		Assert.assertNull(
			SystemProperties.get(
				"test.SystemProperties.test.blank.reference.key"));

		SystemProperties.set(
			"test.SystemProperties.test.blank.reference.key", "${}");

		Assert.assertEquals(
			"${}",
			SystemProperties.get(
				"test.SystemProperties.test.blank.reference.key"));

		// Value contains a single symbol "}"

		Assert.assertNull(
			SystemProperties.get(
				"test.SystemProperties.test.right.part.reference.key"));

		SystemProperties.set(
			"test.SystemProperties.test.right.part.reference.key",
			"test.SystemProperties.test.key}${test.SystemProperties.test.key}");

		Assert.assertEquals(
			"test.SystemProperties.test.key}test.value",
			SystemProperties.get(
				"test.SystemProperties.test.right.part.reference.key"));

		// Value contains a single symbol "${"

		Assert.assertNull(
			SystemProperties.get(
				"test.SystemProperties.test.left.part.reference.key"));

		SystemProperties.set(
			"test.SystemProperties.test.left.part.reference.key",
			"test.SystemProperties.test.key${test.SystemProperties.test." +
				"key}${");

		Assert.assertEquals(
			"test.SystemProperties.test.keytest.value${",
			SystemProperties.get(
				"test.SystemProperties.test.left.part.reference.key"));

		// Multiple reference

		Assert.assertNull(
			SystemProperties.get(
				"test.SystemProperties.test.double.reference.key"));

		SystemProperties.set(
			"test.SystemProperties.test.double.reference.key",
			"${test.SystemProperties.test.key}${test.SystemProperties.test." +
				"key}");

		Assert.assertEquals(
			"test.valuetest.value",
			SystemProperties.get(
				"test.SystemProperties.test.double.reference.key"));

		// Nested references

		Assert.assertNull(
			SystemProperties.get(
				"test.SystemProperties.test.nested.reference.key"));

		SystemProperties.set(
			"test.SystemProperties.test.nested.reference.key",
			"${test.SystemProperties.test.key${test.SystemProperties.test." +
				"key}}");

		Assert.assertEquals(
			"${test.SystemProperties.test.key${test.SystemProperties.test." +
				"key}}",
			SystemProperties.get(
				"test.SystemProperties.test.nested.reference.key"));

		// The referenced property does not exist

		SystemProperties.clear("test.SystemProperties.test.key");

		Assert.assertEquals(
			"${test.SystemProperties.test.key}",
			SystemProperties.get("test.SystemProperties.test.reference.key"));
	}

	private static final String _KEY = "test.key";

	private static final String _PREFIX = "test.SystemProperties.";

	private static final String _TEST_KEY = _PREFIX + _KEY;

	private static final String _TEST_VALUE = "test.value";

}
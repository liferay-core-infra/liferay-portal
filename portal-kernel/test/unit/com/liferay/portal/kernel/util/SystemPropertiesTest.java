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

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

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

		Assert.assertTrue(propertiesWithoutPrefix.size() == 1);
		Assert.assertEquals(
			_TEST_VALUE, propertiesWithoutPrefix.get("test.key"));

		propertiesWithPrefix = SystemProperties.getProperties(_PREFIX, false);

		Assert.assertTrue(propertiesWithPrefix.size() == 1);
		Assert.assertEquals(_TEST_VALUE, propertiesWithPrefix.get(_TEST_KEY));
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

		// Property references other properties via SystemProperties is
		// also parsed

		SystemProperties.set(_TEST_KEY, _TEST_VALUE);

		Assert.assertEquals(_TEST_VALUE, SystemProperties.get(_TEST_KEY));

		// Simple reference

		String testReferenceKey = _PREFIX + "test.reference.key";
		String testReferenceValue =
			StringPool.DOLLAR_AND_OPEN_CURLY_BRACE + _TEST_KEY +
				StringPool.CLOSE_CURLY_BRACE;

		Assert.assertNull(SystemProperties.get(testReferenceKey));

		SystemProperties.set(testReferenceKey, testReferenceValue);

		Assert.assertEquals(
			_TEST_VALUE, SystemProperties.get(testReferenceKey));

		// The referenced property does not exist

		String testNoReferenceKey = _PREFIX + "test.no.reference.key";
		String testNoReferenceValue = "${test.no.key}";

		Assert.assertNull(SystemProperties.get(testNoReferenceKey));

		SystemProperties.set(testNoReferenceKey, testNoReferenceValue);

		Assert.assertEquals(
			testNoReferenceValue, SystemProperties.get(testNoReferenceKey));

		// The referenced property is to the left of the value

		String rightValue = "rightValue";
		String testLeftReferenceKey = _PREFIX + "test.left.reference.key";
		String testLeftReferenceValue = StringBundler.concat(
			StringPool.DOLLAR_AND_OPEN_CURLY_BRACE, testReferenceKey,
			StringPool.CLOSE_CURLY_BRACE, rightValue);

		Assert.assertNull(SystemProperties.get(testLeftReferenceKey));

		SystemProperties.set(testLeftReferenceKey, testLeftReferenceValue);

		Assert.assertEquals(
			_TEST_VALUE + rightValue,
			SystemProperties.get(testLeftReferenceKey));

		// Blank reference

		String testBlankReferenceKey = _PREFIX + "test.blank.reference.key";
		String testBlankReferenceValue =
			StringPool.DOLLAR_AND_OPEN_CURLY_BRACE +
				StringPool.CLOSE_CURLY_BRACE;

		Assert.assertNull(SystemProperties.get(testBlankReferenceKey));

		SystemProperties.set(testBlankReferenceKey, testBlankReferenceValue);

		Assert.assertEquals(
			testBlankReferenceValue,
			SystemProperties.get(testBlankReferenceKey));

		// Value contains a single symbol "}"

		String testRightPartReferenceKey =
			_PREFIX + "test.right.part.reference.key";
		String testRightPartReferenceValue = StringBundler.concat(
			_TEST_KEY, StringPool.CLOSE_CURLY_BRACE,
			StringPool.DOLLAR_AND_OPEN_CURLY_BRACE, _TEST_KEY,
			StringPool.CLOSE_CURLY_BRACE);

		Assert.assertNull(SystemProperties.get(testRightPartReferenceKey));

		SystemProperties.set(
			testRightPartReferenceKey, testRightPartReferenceValue);

		Assert.assertEquals(
			_TEST_KEY + StringPool.CLOSE_CURLY_BRACE + _TEST_VALUE,
			SystemProperties.get(testRightPartReferenceKey));

		// Value contains a single symbol "${"

		String testLeftPartReferenceKey =
			_PREFIX + "test.left.part.reference.key";
		String testLeftPartReferenceValue = StringBundler.concat(
			StringPool.DOLLAR_AND_OPEN_CURLY_BRACE, _TEST_KEY,
			StringPool.DOLLAR_AND_OPEN_CURLY_BRACE, _TEST_KEY,
			StringPool.CLOSE_CURLY_BRACE);

		Assert.assertNull(SystemProperties.get(testLeftPartReferenceKey));

		SystemProperties.set(
			testLeftPartReferenceKey, testLeftPartReferenceValue);

		Assert.assertEquals(
			testLeftPartReferenceValue,
			SystemProperties.get(testLeftPartReferenceKey));

		// Multiple reference

		String testDoubleReferenceKey = _PREFIX + "test.double.reference.key";
		String testDoubleReferenceValue = StringBundler.concat(
			StringPool.DOLLAR_AND_OPEN_CURLY_BRACE, _TEST_KEY,
			StringPool.CLOSE_CURLY_BRACE,
			StringPool.DOLLAR_AND_OPEN_CURLY_BRACE, _TEST_KEY,
			StringPool.CLOSE_CURLY_BRACE);

		Assert.assertNull(SystemProperties.get(testDoubleReferenceKey));

		SystemProperties.set(testDoubleReferenceKey, testDoubleReferenceValue);

		Assert.assertEquals(
			_TEST_VALUE + _TEST_VALUE,
			SystemProperties.get(testDoubleReferenceKey));

		// Nested references

		String testNestedReferenceKey = "test.nested.reference.key";
		String testNestedReferenceValue = StringBundler.concat(
			StringPool.DOLLAR_AND_OPEN_CURLY_BRACE, _TEST_KEY,
			StringPool.DOLLAR_AND_OPEN_CURLY_BRACE, _TEST_KEY,
			StringPool.DOUBLE_CLOSE_CURLY_BRACE);

		Assert.assertNull(SystemProperties.get(testNestedReferenceKey));

		SystemProperties.set(testNestedReferenceKey, testNestedReferenceValue);

		Assert.assertEquals(
			testNestedReferenceValue,
			SystemProperties.get(testNestedReferenceKey));
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

	private static final String _PREFIX =
		SystemPropertiesTest.class.getName() + StringPool.PERIOD;

	private static final String _TEST_KEY = _PREFIX + "test.key";

	private static final String _TEST_VALUE = "test.value";

}
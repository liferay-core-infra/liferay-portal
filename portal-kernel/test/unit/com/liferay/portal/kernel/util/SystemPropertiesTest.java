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
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;

import java.io.File;
import java.io.FileWriter;

import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Tina Tian
 */
public class SystemPropertiesTest {

	@Test
	public void testGet() {
		Assert.assertNull(SystemProperties.get(_TEST_KEY));

		SystemProperties.set(_TEST_KEY, _TEST_VALUE);

		Assert.assertEquals(_TEST_VALUE, SystemProperties.get(_TEST_KEY));

		String testReferenceKey = "test.reference.key";
		String testReferenceValue =
			StringPool.DOLLAR_AND_OPEN_CURLY_BRACE + _TEST_KEY +
				StringPool.CLOSE_CURLY_BRACE;

		Assert.assertNull(SystemProperties.get(testReferenceKey));

		SystemProperties.set(testReferenceKey, testReferenceValue);

		Assert.assertEquals(
			_TEST_VALUE, SystemProperties.get(testReferenceKey));

		String testNoReferenceKey = "test.no.reference.key";
		String testNoReferenceValue = "${test.no.key}";

		Assert.assertNull(SystemProperties.get(testNoReferenceKey));

		SystemProperties.set(testNoReferenceKey, testNoReferenceValue);

		Assert.assertEquals(
			testNoReferenceValue, SystemProperties.get(testNoReferenceKey));

		String rightValue = "rightValue";
		String testLeftReferenceKey = "test.left.reference.key";
		String testLeftReferenceValue = StringBundler.concat(
			StringPool.DOLLAR_AND_OPEN_CURLY_BRACE, testReferenceKey,
			StringPool.CLOSE_CURLY_BRACE, rightValue);

		Assert.assertNull(SystemProperties.get(testLeftReferenceKey));

		SystemProperties.set(testLeftReferenceKey, testLeftReferenceValue);

		Assert.assertEquals(
			_TEST_VALUE + rightValue,
			SystemProperties.get(testLeftReferenceKey));

		String testBlankReferenceKey = "test.blank.reference.key";
		String testBlankReferenceValue =
			StringPool.DOLLAR_AND_OPEN_CURLY_BRACE +
				StringPool.CLOSE_CURLY_BRACE;

		Assert.assertNull(SystemProperties.get(testBlankReferenceKey));

		SystemProperties.set(testBlankReferenceKey, testBlankReferenceValue);

		Assert.assertEquals(
			testBlankReferenceValue,
			SystemProperties.get(testBlankReferenceKey));

		String testRightPartReferenceKey = "test.right.part.reference.key";
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

		String testLeftPartReferenceKey = "test.left.part.reference.key";
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

		String testDoubleReferenceKey = "test.double.reference.key";
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

		SystemProperties.clear(_TEST_KEY);

		Assert.assertEquals(
			testReferenceValue, SystemProperties.get(testReferenceKey));

		Assert.assertNull(SystemProperties.get(_TEST_KEY));

		Assert.assertEquals(
			"defaultValue", SystemProperties.get(_TEST_KEY, "defaultValue"));
	}

	@Test
	public void testGetArray() {
		Assert.assertTrue(
			ArrayUtil.isEmpty(SystemProperties.getArray("test.array.key")));

		SystemProperties.set(
			"test.array.key", "test.array.value,test.array.value");

		Assert.assertArrayEquals(
			new String[] {"test.array.value", "test.array.value"},
			SystemProperties.getArray("test.array.key"));
	}

	@Test
	public void testGetProperties() {
		String prefix = "properties.test.";

		Map<String, String> propertiesWithPrefix =
			SystemProperties.getProperties(prefix, false);

		Assert.assertTrue(propertiesWithPrefix.isEmpty());

		SystemProperties.set("properties.test.key", "properties.test.value");

		Map<String, String> propertiesWithoutPrefix =
			SystemProperties.getProperties(prefix, true);

		for (Map.Entry<String, String> property :
				propertiesWithoutPrefix.entrySet()) {

			Assert.assertEquals("key", property.getKey());

			Assert.assertEquals("properties.test.value", property.getValue());
		}

		propertiesWithPrefix = SystemProperties.getProperties(prefix, false);

		for (Map.Entry<String, String> property :
				propertiesWithPrefix.entrySet()) {

			Assert.assertEquals("properties.test.key", property.getKey());

			Assert.assertEquals("properties.test.value", property.getValue());
		}
	}

	@Test
	public void testGetPropertyNames() {
		Set<String> propertyNames = SystemProperties.getPropertyNames();

		Assert.assertFalse(propertyNames.contains("property.names.test.key"));

		SystemProperties.set(
			"property.names.test.key", "property.names.test.value");

		propertyNames = SystemProperties.getPropertyNames();

		Assert.assertTrue(propertyNames.contains("property.names.test.key"));
	}

	@Test
	public void testLoad() throws Exception {
		String userDir = StringUtil.replace(
			System.getProperty("user.dir"), CharPool.BACK_SLASH,
			CharPool.FORWARD_SLASH);

		File systemPropertiesFile = new File(
			userDir + "/portal-kernel/test-classes/unit/system.properties");

		try {
			systemPropertiesFile.createNewFile();

			try (FileWriter fileWriter = new FileWriter(systemPropertiesFile)) {
				fileWriter.write(
					StringBundler.concat(
						"#test case", StringPool.NEW_LINE, "test.case.key=\\",
						StringPool.NEW_LINE, "\\", StringPool.NEW_LINE, "#",
						StringPool.NEW_LINE, "test.case.value",
						StringPool.NEW_LINE));

				fileWriter.flush();
			}

			SystemProperties.load(SystemPropertiesTest.class.getClassLoader());

			Assert.assertEquals(
				"test.case.value", SystemProperties.get("test.case.key"));
		}
		finally {
			systemPropertiesFile.delete();
		}
	}

	@Test
	public void testSetAndClear() {
		Assert.assertNull(SystemProperties.get(_TEST_KEY));

		SystemProperties.set(_TEST_KEY, _TEST_VALUE);

		Assert.assertEquals(_TEST_VALUE, SystemProperties.get(_TEST_KEY));

		SystemProperties.clear(_TEST_KEY);

		Assert.assertNull(SystemProperties.get(_TEST_KEY));
	}

	private static final String _TEST_KEY = "test.key";

	private static final String _TEST_VALUE = "test.value";

}
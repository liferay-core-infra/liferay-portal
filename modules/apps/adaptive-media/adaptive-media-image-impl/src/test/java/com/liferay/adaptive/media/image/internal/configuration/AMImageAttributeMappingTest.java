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

package com.liferay.adaptive.media.image.internal.configuration;

import com.liferay.adaptive.media.image.processor.AMImageAttribute;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Adolfo Pérez
 */
public class AMImageAttributeMappingTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCreateFromEmptyMap() {
		AMImageAttributeMapping amImageAttributeMapping =
			AMImageAttributeMapping.fromProperties(Collections.emptyMap());

		Integer height = amImageAttributeMapping.getValue(
			AMImageAttribute.AM_IMAGE_ATTRIBUTE_HEIGHT);

		Assert.assertNull(height);

		Integer width = amImageAttributeMapping.getValue(
			AMImageAttribute.AM_IMAGE_ATTRIBUTE_WIDTH);

		Assert.assertNull(width);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFailWhenCreatingFromNullMap() {
		AMImageAttributeMapping.fromProperties(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFailWhenGettingValueOfNullAttribute() {
		AMImageAttributeMapping amImageAttributeMapping =
			AMImageAttributeMapping.fromProperties(
				MapUtil.fromArray(
					AMImageAttribute.AM_IMAGE_ATTRIBUTE_HEIGHT.getName(), "100",
					AMImageAttribute.AM_IMAGE_ATTRIBUTE_WIDTH.getName(),
					"200"));

		amImageAttributeMapping.getValue(null);
	}

	@Test
	public void testIgnoreUnknownAttributes() {
		AMImageAttributeMapping amImageAttributeMapping =
			AMImageAttributeMapping.fromProperties(
				MapUtil.fromArray("foo", RandomTestUtil.randomString()));

		Integer height = amImageAttributeMapping.getValue(
			AMImageAttribute.AM_IMAGE_ATTRIBUTE_HEIGHT);

		Assert.assertNull(height);

		Integer width = amImageAttributeMapping.getValue(
			AMImageAttribute.AM_IMAGE_ATTRIBUTE_WIDTH);

		Assert.assertNull(width);
	}

	@Test
	public void testValidAttributes() {
		AMImageAttributeMapping amImageAttributeMapping =
			AMImageAttributeMapping.fromProperties(
				MapUtil.fromArray(
					AMImageAttribute.AM_IMAGE_ATTRIBUTE_HEIGHT.getName(), "100",
					AMImageAttribute.AM_IMAGE_ATTRIBUTE_WIDTH.getName(),
					"200"));

		Integer height = amImageAttributeMapping.getValue(
			AMImageAttribute.AM_IMAGE_ATTRIBUTE_HEIGHT);

		Assert.assertEquals(100, height.intValue());

		Integer width = amImageAttributeMapping.getValue(
			AMImageAttribute.AM_IMAGE_ATTRIBUTE_WIDTH);

		Assert.assertEquals(200, width.intValue());
	}

	@Test
	public void testValidSingleAttribute() {
		AMImageAttributeMapping amImageAttributeMapping =
			AMImageAttributeMapping.fromProperties(
				MapUtil.fromArray(
					AMImageAttribute.AM_IMAGE_ATTRIBUTE_HEIGHT.getName(),
					"100"));

		Integer height = amImageAttributeMapping.getValue(
			AMImageAttribute.AM_IMAGE_ATTRIBUTE_HEIGHT);

		Assert.assertEquals(100, height.intValue());

		Integer width = amImageAttributeMapping.getValue(
			AMImageAttribute.AM_IMAGE_ATTRIBUTE_WIDTH);

		Assert.assertNull(width);
	}

}
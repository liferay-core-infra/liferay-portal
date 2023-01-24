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

package com.liferay.portal.vulcan.internal.jaxrs;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory.ServiceWrapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Carlos Correa
 */
public class JaxRsResourceRegistryImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetPropertyValue() {
		String className = RandomTestUtil.randomString();
		String propertyKey = RandomTestUtil.randomString();
		String propertyValue = RandomTestUtil.randomString();

		_mockServiceTrackerMap(className, propertyKey, propertyValue);

		Assert.assertEquals(
			propertyValue,
			_jaxRsResourceRegistryImpl.getPropertyValue(
				className, propertyKey));
	}

	@Test
	public void testGetPropertyValueUnknownClassName() {
		String propertyKey = RandomTestUtil.randomString();
		String propertyValue = RandomTestUtil.randomString();

		_mockServiceTrackerMap(
			RandomTestUtil.randomString(), propertyKey, propertyValue);

		Assert.assertNull(
			_jaxRsResourceRegistryImpl.getPropertyValue(
				RandomTestUtil.randomString(), propertyKey));
	}

	@Test
	public void testGetPropertyValueUnknownPropertyKey() {
		String className = RandomTestUtil.randomString();
		String propertyKey = RandomTestUtil.randomString();

		_mockServiceTrackerMap(
			className, propertyKey, RandomTestUtil.randomString());

		Assert.assertNull(
			_jaxRsResourceRegistryImpl.getPropertyValue(
				className, RandomTestUtil.randomString()));
	}

	private void _mockServiceTrackerMap(
		String className, String propertyKey, String propertyValue) {

		ServiceTrackerMap<String, ServiceWrapper<Object>> serviceTrackerMap =
			Mockito.mock(ServiceTrackerMap.class);

		Mockito.when(
			serviceTrackerMap.getService(className)
		).thenReturn(
			new ServiceWrapper<Object>() {

				@Override
				public Map<String, Object> getProperties() {
					return Collections.singletonMap(propertyKey, propertyValue);
				}

				@Override
				public Object getService() {
					return null;
				}

			}
		);

		ReflectionTestUtil.setFieldValue(
			_jaxRsResourceRegistryImpl, "_serviceTrackerMap",
			serviceTrackerMap);
	}

	private final JaxRsResourceRegistryImpl _jaxRsResourceRegistryImpl =
		new JaxRsResourceRegistryImpl();

}
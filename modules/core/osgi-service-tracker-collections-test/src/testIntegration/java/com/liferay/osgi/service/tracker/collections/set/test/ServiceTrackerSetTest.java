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

package com.liferay.osgi.service.tracker.collections.set.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osgi.service.tracker.collections.map.test.TrackedOne;
import com.liferay.osgi.service.tracker.collections.set.ServiceTrackerSet;
import com.liferay.osgi.service.tracker.collections.set.ServiceTrackerSetFactory;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Jiaxu Wei
 */
@RunWith(Arquillian.class)
public class ServiceTrackerSetTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(ServiceTrackerSetTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	@Test
	public void testGetServiceWithServiceTrackerCustomizer() {
		try (ServiceTrackerSet<TrackedOne> serviceTrackerSet =
				ServiceTrackerSetFactory.open(
					_bundleContext, TrackedOne.class,
					new ServiceTrackerCustomizer<TrackedOne, TrackedOne>() {

						@Override
						public TrackedOne addingService(
							ServiceReference<TrackedOne> serviceReference) {

							return new CustomizedService();
						}

						@Override
						public void modifiedService(
							ServiceReference<TrackedOne> serviceReference,
							TrackedOne service) {
						}

						@Override
						public void removedService(
							ServiceReference<TrackedOne> serviceReference,
							TrackedOne service) {
						}

					})) {

			ServiceRegistration<TrackedOne> serviceRegistration =
				_registerService(TrackedOne.class, new TrackedOne());

			for (TrackedOne service : serviceTrackerSet) {
				Assert.assertTrue(service instanceof CustomizedService);
			}

			serviceTrackerSet.close();

			serviceRegistration.unregister();
		}
	}

	@Test
	public void testServiceInsertionAndRemoval() {
		try (ServiceTrackerSet<TrackedOne> serviceTrackerSet =
				ServiceTrackerSetFactory.open(
					_bundleContext, TrackedOne.class)) {

			Assert.assertEquals(
				serviceTrackerSet.toString(), 0, serviceTrackerSet.size());

			ServiceRegistration<TrackedOne> serviceRegistration =
				_registerService(TrackedOne.class, new TrackedOne());

			Assert.assertEquals(
				serviceTrackerSet.toString(), 1, serviceTrackerSet.size());

			serviceRegistration.unregister();

			Assert.assertEquals(
				serviceTrackerSet.toString(), 0, serviceTrackerSet.size());

			serviceTrackerSet.close();
		}
	}

	@Test
	public void testServiceModification() {
		try (ServiceTrackerSet<TrackedOne> serviceTrackerSet =
				ServiceTrackerSetFactory.open(
					_bundleContext, TrackedOne.class)) {

			TrackedOne trackedOne = new TrackedOne();

			ServiceRegistration<TrackedOne> serviceRegistration =
				_registerService(TrackedOne.class, trackedOne);

			for (TrackedOne service : serviceTrackerSet) {
				Assert.assertSame(trackedOne, service);
			}

			Dictionary<String, Object> properties = new Hashtable<>();

			properties.put("service.tracker.set", "test.value");

			serviceRegistration.setProperties(properties);

			for (TrackedOne service : serviceTrackerSet) {
				Assert.assertSame(trackedOne, service);
			}

			serviceTrackerSet.close();

			serviceRegistration.unregister();
		}
	}

	public static class CustomizedService extends TrackedOne {
	}

	private <T> ServiceRegistration<T> _registerService(
		Class<T> clazz, T service) {

		return _bundleContext.registerService(
			clazz, service, new Hashtable<>());
	}

	private BundleContext _bundleContext;

}
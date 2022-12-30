/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.saml.opensaml.integration.internal.service.tracker.collections;

import com.liferay.osgi.service.tracker.collections.ServiceReferenceServiceTuple;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Stian Sigvartsen
 */
@Component(service = OrderedServiceTrackerMapFactory.class)
public class OrderedServiceTrackerMapFactoryImpl
	implements OrderedServiceTrackerMapFactory {

	@Override
	public <T> OrderedServiceTrackerMap<T> create(
		BundleContext bundleContext, Class<T> clazz, String propertyKey) {

		return new OrderedServiceTrackerMapImpl<>(
			bundleContext, clazz, "(default=true)", propertyKey);
	}

	private static class OrderedServiceTrackerMapImpl<T>
		implements OrderedServiceTrackerMap<T> {

		@Override
		public void close() {
			_serviceTrackerMap.close();
		}

		@Override
		public String getDefaultServiceKey() {
			ServiceReference<T> serviceReference =
				_defaultServiceTracker.getServiceReference();

			if (serviceReference == null) {
				return StringPool.BLANK;
			}

			return GetterUtil.getString(
				serviceReference.getProperty(_propertyKey));
		}

		@Override
		public List<Map.Entry<String, T>> getOrderedServices() {
			Set<String> prefixes = _serviceTrackerMap.keySet();

			List<Map.Entry<String, T>> orderedServices = new ArrayList<>();

			List<ServiceReferenceServiceTuple<T, T>>
				serviceReferenceServiceTuples = new ArrayList<>();

			for (String prefix : prefixes) {
				ServiceReferenceServiceTuple<T, T>
					curServiceReferenceServiceTuple =
						_serviceTrackerMap.getService(prefix);

				if (curServiceReferenceServiceTuple != null) {
					serviceReferenceServiceTuples.add(
						curServiceReferenceServiceTuple);
				}
			}

			Collections.sort(
				serviceReferenceServiceTuples,
				Comparator.comparing(
					serviceReferenceServiceTuple -> {
						ServiceReference<?> serviceReference =
							serviceReferenceServiceTuple.getServiceReference();

						return GetterUtil.getInteger(
							serviceReference.getProperty("display.index"));
					}));

			for (ServiceReferenceServiceTuple<T, T>
					serviceReferenceServiceTuple :
						serviceReferenceServiceTuples) {

				ServiceReference<T> serviceReference =
					serviceReferenceServiceTuple.getServiceReference();

				orderedServices.add(
					new AbstractMap.SimpleEntry<String, T>(
						(String)serviceReference.getProperty(_propertyKey),
						serviceReferenceServiceTuple.getService()));
			}

			return orderedServices;
		}

		@Override
		public List<String> getOrderedServicesKeys() {
			Set<String> prefixes = _serviceTrackerMap.keySet();

			List<String> orderedServiceKeys = new ArrayList<>();

			List<ServiceReference<T>> serviceReferences = new ArrayList<>();

			for (String prefix : prefixes) {
				ServiceReferenceServiceTuple<T, T>
					curServiceReferenceServiceTuple =
						_serviceTrackerMap.getService(prefix);

				serviceReferences.add(
					curServiceReferenceServiceTuple.getServiceReference());
			}

			Collections.sort(
				serviceReferences,
				Comparator.comparing(
					serviceReference -> GetterUtil.getInteger(
						serviceReference.getProperty("display.index"))));

			for (ServiceReference<T> serviceReference : serviceReferences) {
				orderedServiceKeys.add(
					GetterUtil.getString(
						serviceReference.getProperty(_propertyKey)));
			}

			return orderedServiceKeys;
		}

		@Override
		public T getService(String key) {
			ServiceReferenceServiceTuple<T, T> service =
				_serviceTrackerMap.getService(key);

			if (service != null) {
				return service.getService();
			}

			return _defaultServiceTracker.getService();
		}

		@Override
		public Set<String> getServicesKeys() {
			return _serviceTrackerMap.keySet();
		}

		private OrderedServiceTrackerMapImpl(
			BundleContext bundleContext, Class<T> clazz,
			String defaultServiceFilter, String propertyKey) {

			_propertyKey = propertyKey;

			_defaultServiceTracker = ServiceTrackerFactory.open(
				bundleContext,
				StringBundler.concat(
					"(&(objectClass=", clazz.getName(), ")",
					defaultServiceFilter, ")"));
			_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, clazz, propertyKey,
				ServiceTrackerCustomizerFactory.serviceReferenceServiceTuple(
					bundleContext));
		}

		private final ServiceTracker<T, T> _defaultServiceTracker;
		private final String _propertyKey;
		private final ServiceTrackerMap
			<String, ServiceReferenceServiceTuple<T, T>> _serviceTrackerMap;

	}

}
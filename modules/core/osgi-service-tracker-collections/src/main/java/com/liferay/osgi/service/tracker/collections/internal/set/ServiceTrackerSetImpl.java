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

package com.liferay.osgi.service.tracker.collections.internal.set;

import com.liferay.osgi.service.tracker.collections.ServiceReferenceServiceTuple;
import com.liferay.osgi.service.tracker.collections.internal.ServiceTrackerUtil;
import com.liferay.osgi.service.tracker.collections.set.ServiceTrackerSet;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Jiaxu Wei
 */
public class ServiceTrackerSetImpl<S, T> implements ServiceTrackerSet<T> {

	public ServiceTrackerSetImpl(
		BundleContext bundleContext, Class<S> clazz, String filterString,
		ServiceTrackerCustomizer<S, T> serviceTrackerCustomizer) {

		_bundleContext = bundleContext;
		_serviceTrackerCustomizer = serviceTrackerCustomizer;

		_serviceTracker = ServiceTrackerUtil.createServiceTracker(
			_bundleContext, clazz, filterString,
			new ServiceTrackerSetImpl.
				ServiceReferenceServiceTrackerCustomizer());

		_serviceTracker.open();
	}

	@Override
	public void close() {
		_serviceTracker.close();
	}

	@Override
	public void forEach(Consumer<? super T> consumer) {
		_services.forEach(
			serviceReferenceServiceTuple -> consumer.accept(
				serviceReferenceServiceTuple.getService()));
	}

	@Override
	public Iterator<T> iterator() {
		return new ServiceTrackerSetImpl.ServiceTrackerSetIterator<>(
			_services.iterator());
	}

	@Override
	public int size() {
		return _services.size();
	}

	private final BundleContext _bundleContext;
	private final Set<ServiceReferenceServiceTuple<S, T>> _services =
		new CopyOnWriteArraySet<>();
	private final ServiceTracker<S, T> _serviceTracker;
	private final ServiceTrackerCustomizer<S, T> _serviceTrackerCustomizer;

	private static class ServiceTrackerSetIterator<S, T>
		implements Iterator<T> {

		@Override
		public boolean hasNext() {
			return _iterator.hasNext();
		}

		@Override
		public T next() {
			ServiceReferenceServiceTuple<S, T> serviceReferenceServiceTuple =
				_iterator.next();

			return serviceReferenceServiceTuple.getService();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		private ServiceTrackerSetIterator(
			Iterator<ServiceReferenceServiceTuple<S, T>> iterator) {

			_iterator = iterator;
		}

		private final Iterator<ServiceReferenceServiceTuple<S, T>> _iterator;

	}

	private class ServiceReferenceServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<S, T> {

		@Override
		public T addingService(ServiceReference<S> serviceReference) {
			T service = _serviceTrackerCustomizer.addingService(
				serviceReference);

			if (service == null) {
				return null;
			}

			_update(serviceReference, service, false);

			return service;
		}

		@Override
		public void modifiedService(
			ServiceReference<S> serviceReference, T service) {

			_serviceTrackerCustomizer.modifiedService(
				serviceReference, service);
		}

		@Override
		public void removedService(
			ServiceReference<S> serviceReference, T service) {

			_serviceTrackerCustomizer.removedService(serviceReference, service);

			_update(serviceReference, service, true);

			_bundleContext.ungetService(serviceReference);
		}

		private void _update(
			ServiceReference<S> serviceReference, T service, boolean remove) {

			ServiceReferenceServiceTuple<S, T> serviceReferenceServiceTuple =
				new ServiceReferenceServiceTuple<>(serviceReference, service);

			synchronized (_services) {
				if (remove) {
					_services.remove(serviceReferenceServiceTuple);
				}
				else {
					_services.add(serviceReferenceServiceTuple);
				}
			}
		}

	}

}
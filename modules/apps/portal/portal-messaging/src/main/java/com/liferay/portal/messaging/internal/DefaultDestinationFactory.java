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

package com.liferay.portal.messaging.internal;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Michael C. Han
 */
@Component(immediate = true, service = DestinationFactory.class)
public class DefaultDestinationFactory implements DestinationFactory {

	@Override
	public Destination createDestination(
		DestinationConfiguration destinationConfiguration) {

		String type = destinationConfiguration.getDestinationType();

		DestinationPrototype destinationPrototype = _destinationPrototypes.get(
			type);

		if (destinationPrototype == null) {
			throw new IllegalArgumentException(
				"No destination prototype configured for " + type);
		}

		return destinationPrototype.createDestination(destinationConfiguration);
	}

	@Override
	public Collection<String> getDestinationTypes() {
		return Collections.unmodifiableCollection(
			_destinationPrototypes.keySet());
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_destinationPrototypes.put(
			DestinationConfiguration.DESTINATION_TYPE_PARALLEL,
			new ParallelDestinationPrototype(
				_portalExecutorManager, _permissionCheckerFactory,
				_userLocalService));
		_destinationPrototypes.put(
			DestinationConfiguration.DESTINATION_TYPE_SERIAL,
			new SerialDestinationPrototype(
				_portalExecutorManager, _permissionCheckerFactory,
				_userLocalService));
		_destinationPrototypes.put(
			DestinationConfiguration.DESTINATION_TYPE_SYNCHRONOUS,
			new SynchronousDestinationPrototype());

		String key = "destination.type";

		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext, DestinationPrototype.class,
			new ServiceTrackerCustomizer
				<DestinationPrototype, DestinationPrototype>() {

				@Override
				public DestinationPrototype addingService(
					ServiceReference<DestinationPrototype> serviceReference) {

					DestinationPrototype destinationPrototype =
						bundleContext.getService(serviceReference);

					_destinationPrototypes.put(
						MapUtil.getString(
							Collections.singletonMap(
								key, serviceReference.getProperty(key)),
							key),
						destinationPrototype);

					return destinationPrototype;
				}

				@Override
				public void modifiedService(
					ServiceReference<DestinationPrototype> serviceReference,
					DestinationPrototype service) {
				}

				@Override
				public void removedService(
					ServiceReference<DestinationPrototype> serviceReference,
					DestinationPrototype service) {

					_destinationPrototypes.remove(
						MapUtil.getString(
							Collections.singletonMap(
								key, serviceReference.getProperty(key)),
							key),
						service);

					bundleContext.ungetService(serviceReference);
				}

			});
	}

	@Deactivate
	protected void deactivate() {
		_destinationPrototypes.clear();

		_serviceTracker.close();
	}

	private final ConcurrentMap<String, DestinationPrototype>
		_destinationPrototypes = new ConcurrentHashMap<>();

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private PortalExecutorManager _portalExecutorManager;

	private ServiceTracker<DestinationPrototype, DestinationPrototype>
		_serviceTracker;

	@Reference
	private UserLocalService _userLocalService;

}
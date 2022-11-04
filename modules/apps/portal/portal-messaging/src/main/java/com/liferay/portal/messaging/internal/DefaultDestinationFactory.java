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

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(immediate = true, service = DestinationFactory.class)
public class DefaultDestinationFactory implements DestinationFactory {

	@Override
	public Destination createDestination(
		DestinationConfiguration destinationConfiguration) {

		String type = destinationConfiguration.getDestinationType();

		DestinationPrototype destinationPrototype =
			_serviceTrackerMap.getService(type);

		if (destinationPrototype == null) {
			throw new IllegalArgumentException(
				"No destination prototype configured for " + type);
		}

		return destinationPrototype.createDestination(destinationConfiguration);
	}

	@Override
	public Collection<String> getDestinationTypes() {
		return Collections.unmodifiableCollection(_serviceTrackerMap.keySet());
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceRegistrations.add(
			bundleContext.registerService(
				DestinationPrototype.class,
				new ParallelDestinationPrototype(
					_portalExecutorManager, _permissionCheckerFactory,
					_userLocalService),
				MapUtil.singletonDictionary(
					"destination.type",
					DestinationConfiguration.DESTINATION_TYPE_PARALLEL)));
		_serviceRegistrations.add(
			bundleContext.registerService(
				DestinationPrototype.class,
				new SerialDestinationPrototype(
					_portalExecutorManager, _permissionCheckerFactory,
					_userLocalService),
				MapUtil.singletonDictionary(
					"destination.type",
					DestinationConfiguration.DESTINATION_TYPE_SERIAL)));
		_serviceRegistrations.add(
			bundleContext.registerService(
				DestinationPrototype.class,
				new SynchronousDestinationPrototype(),
				MapUtil.singletonDictionary(
					"destination.type",
					DestinationConfiguration.DESTINATION_TYPE_SYNCHRONOUS)));

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, DestinationPrototype.class, null,
			(serviceReference, emitter) -> emitter.emit(
				GetterUtil.getString(
					serviceReference.getProperty("destination.type"))));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();

		_serviceRegistrations.forEach(ServiceRegistration::unregister);
	}

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private PortalExecutorManager _portalExecutorManager;

	private final List<ServiceRegistration<DestinationPrototype>>
		_serviceRegistrations = new ArrayList<>();
	private ServiceTrackerMap<String, DestinationPrototype> _serviceTrackerMap;

	@Reference
	private UserLocalService _userLocalService;

}
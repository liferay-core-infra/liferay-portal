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

import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.messaging.internal.configuration.DestinationWorkerConfiguration;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Joao Victor Alves
 */
public class DestinationRegisterUtil {

	public static void clearDestinations() {
		_destinations.clear();
	}

	public static Destination getDestination(String destinationName) {
		return _destinations.get(destinationName);
	}

	public static Collection<Destination> getDestinationsValues() {
		return _destinations.values();
	}

	public static DestinationWorkerConfiguration
		getDestinationWorkerConfiguration(String destinationName) {

		return _destinationWorkerConfigurations.get(destinationName);
	}

	public static void putDestination(
		String destinationName, Destination destination) {

		_destinations.put(destinationName, destination);
	}

	public static void putDestinationWorkerConfiguration(
		String destinationName,
		DestinationWorkerConfiguration destinationWorkerConfiguration) {

		_destinationWorkerConfigurations.put(
			destinationName, destinationWorkerConfiguration);
	}

	public static Destination removeDestination(String destinationName) {
		return _destinations.remove(destinationName);
	}

	public static void removeDestinationWorkerConfiguration(
		String destinationName) {

		_destinationWorkerConfigurations.remove(destinationName);
	}

	public static void updateDestination(
		Destination destination,
		DestinationWorkerConfiguration destinationWorkerConfiguration) {

		if ((destination == null) || (destinationWorkerConfiguration == null)) {
			return;
		}

		if (destination instanceof BaseAsyncDestination) {
			BaseAsyncDestination baseAsyncDestination =
				(BaseAsyncDestination)destination;

			baseAsyncDestination.setMaximumQueueSize(
				destinationWorkerConfiguration.maxQueueSize());
			baseAsyncDestination.setWorkersSize(
				destinationWorkerConfiguration.workerCoreSize(),
				destinationWorkerConfiguration.workerMaxSize());
		}
	}

	private static final Map<String, Destination> _destinations =
		new ConcurrentHashMap<>();
	private static final Map<String, DestinationWorkerConfiguration>
		_destinationWorkerConfigurations = new ConcurrentHashMap<>();

}
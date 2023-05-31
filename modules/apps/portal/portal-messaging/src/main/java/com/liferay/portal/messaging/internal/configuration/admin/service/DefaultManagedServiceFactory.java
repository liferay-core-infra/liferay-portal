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

package com.liferay.portal.messaging.internal.configuration.admin.service;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.messaging.internal.DestinationRegisterUtil;
import com.liferay.portal.messaging.internal.configuration.DestinationWorkerConfiguration;

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;

/**
 * @author Joao Victor Alves
 */
@Component(
	property = Constants.SERVICE_PID + "=com.liferay.portal.messaging.internal.configuration.DestinationWorkerConfiguration",
	service = ManagedServiceFactory.class
)
public class DefaultManagedServiceFactory implements ManagedServiceFactory {

	@Override
	public void deleted(String factoryPid) {
		String destinationName = _factoryPidsToDestinationName.remove(
			factoryPid);

		DestinationRegisterUtil.removeDestinationWorkerConfiguration(
			destinationName);
	}

	@Override
	public String getName() {
		return "Default Message Bus";
	}

	@Override
	public void updated(String factoryPid, Dictionary<String, ?> dictionary)
		throws ConfigurationException {

		DestinationWorkerConfiguration destinationWorkerConfiguration =
			ConfigurableUtil.createConfigurable(
				DestinationWorkerConfiguration.class, dictionary);

		_factoryPidsToDestinationName.put(
			factoryPid, destinationWorkerConfiguration.destinationName());

		DestinationRegisterUtil.putDestinationWorkerConfiguration(
			destinationWorkerConfiguration.destinationName(),
			destinationWorkerConfiguration);

		DestinationRegisterUtil.updateDestination(
			DestinationRegisterUtil.getDestination(
				destinationWorkerConfiguration.destinationName()),
			destinationWorkerConfiguration);
	}

	private final Map<String, String> _factoryPidsToDestinationName =
		new ConcurrentHashMap<>();

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.messaging.destination.creator;

import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationDefinition;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Marcos Martins
 */
public class DestinationCreator {

	public void createDestination(
		BundleContext bundleContext, DestinationFactory destinationFactory,
		String destinationName) {

		DestinationDefinition destinationDefinition =
			DestinationConfiguration.createSerialDestinationConfiguration(
				destinationName);

		_serviceRegistration = bundleContext.registerService(
			DestinationDefinition.class, destinationDefinition,
			HashMapDictionaryBuilder.<String, Object>put(
				"destination.name", destinationName
			).build());
	}

	public void removeDestination() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	private ServiceRegistration<DestinationDefinition> _serviceRegistration;

}
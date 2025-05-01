/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.messaging.config;

import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationDefinition;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.module.util.ServiceLatch;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Michael C. Han
 *
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class DefaultMessagingConfigurator implements MessagingConfigurator {

	public void afterPropertiesSet() {
		ServiceLatch serviceLatch = SystemBundleUtil.newServiceLatch();

		serviceLatch.waitFor(DestinationFactory.class);
		serviceLatch.waitFor(MessageBus.class);
		serviceLatch.openOn(this::initialize);
	}

	@Override
	public void destroy() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();

		_destinationDefinitions.clear();
		_messageListeners.clear();
	}

	@Override
	public void setDestinationConfigurations(
		Set<DestinationConfiguration> destinationConfigurations) {

		_destinationDefinitions.addAll(destinationConfigurations);
	}

	@Override
	public void setDestinations(List<Destination> destinations) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMessageListeners(
		Map<String, List<MessageListener>> messageListeners) {

		_messageListeners.putAll(messageListeners);
	}

	protected void initialize() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		for (DestinationDefinition destinationDefinition :
				_destinationDefinitions) {

			_serviceRegistrations.add(
				bundleContext.registerService(
					DestinationDefinition.class, destinationDefinition,
					MapUtil.singletonDictionary(
						"destination.name",
						destinationDefinition.getDestinationName())));
		}

		for (Map.Entry<String, List<MessageListener>> messageListeners :
				_messageListeners.entrySet()) {

			String destinationName = messageListeners.getKey();

			for (MessageListener messageListener :
					messageListeners.getValue()) {

				_serviceRegistrations.add(
					bundleContext.registerService(
						MessageListener.class, messageListener,
						MapUtil.singletonDictionary(
							"destination.name", destinationName)));
			}
		}
	}

	private final Set<DestinationDefinition> _destinationDefinitions =
		new HashSet<>();
	private final Map<String, List<MessageListener>> _messageListeners =
		new HashMap<>();
	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

}
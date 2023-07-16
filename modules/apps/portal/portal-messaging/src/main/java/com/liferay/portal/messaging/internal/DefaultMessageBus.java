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

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationEventListener;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.MessageBusEventListener;
import com.liferay.portal.kernel.messaging.MessageBusInterceptor;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.messaging.internal.configuration.DestinationWorkerConfiguration;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Michael C. Han
 * @author Brian Wing Shun Chan
 */
@Component(service = MessageBus.class)
public class DefaultMessageBus implements MessageBus {

	@Override
	public Destination getDestination(String destinationName) {
		return _destinations.get(destinationName);
	}

	@Override
	public void sendMessage(String destinationName, Message message) {
		MessageBusThreadLocalUtil.populateMessageFromThreadLocals(message);

		for (MessageBusInterceptor messageBusInterceptor :
				_messageBusInterceptorServiceTrackerList) {

			if (messageBusInterceptor.intercept(
					this, destinationName, message)) {

				return;
			}
		}

		Destination destination = _destinations.get(destinationName);

		if (destination == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Destination " + destinationName + " is not configured");
			}

			return;
		}

		message.setDestinationName(destinationName);

		if (message.get("companyId") == null) {
			Long[] companyIds = (Long[])message.get("companyIds");

			if (companyIds != null) {
				long orignalCompanyId = CompanyThreadLocal.getCompanyId();

				try {
					for (Long id : companyIds) {
						CompanyThreadLocal.setCompanyId(id);

						message.put("companyId", id);

						destination.send(message.clone());
					}
				}
				finally {
					CompanyThreadLocal.setCompanyId(orignalCompanyId);
				}

				return;
			}
		}

		destination.send(message);
	}

	@Override
	public void shutdown() {
		shutdown(false);
	}

	@Override
	public synchronized void shutdown(boolean force) {
		for (Destination destination : _destinations.values()) {
			destination.close(force);
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_destinationServiceTracker = ServiceTrackerFactory.open(
			bundleContext,
			"(&(objectClass=" + Destination.class.getName() +
				")(destination.name=*))",
			new DestinationServiceTrackerCustomizer());

		_destinationEventListenerServiceTracker = ServiceTrackerFactory.open(
			bundleContext,
			"(&(objectClass=" + DestinationEventListener.class.getName() +
				")(destination.name=*))",
			new DestinationEventListenerServiceTrackerCustomizer());

		_messageListenerServiceTracker = new ServiceTracker<>(
			bundleContext, MessageListener.class,
			new ServiceTrackerCustomizer
				<MessageListener, ObjectValuePair<String, MessageListener>>() {

				@Override
				public ObjectValuePair<String, MessageListener> addingService(
					ServiceReference<MessageListener> serviceReference) {

					String destinationName =
						(String)serviceReference.getProperty(
							"destination.name");

					if (destinationName == null) {
						return null;
					}

					MessageListener messageListener = bundleContext.getService(
						serviceReference);

					_registerMessageListener(destinationName, messageListener);

					return new ObjectValuePair<>(
						destinationName, messageListener);
				}

				@Override
				public void modifiedService(
					ServiceReference<MessageListener> serviceReference,
					ObjectValuePair<String, MessageListener> objectValuePair) {

					removedService(serviceReference, objectValuePair);

					ObjectValuePair<String, MessageListener>
						newObjectValuePair = addingService(serviceReference);

					objectValuePair.setKey(newObjectValuePair.getKey());
				}

				@Override
				public void removedService(
					ServiceReference<MessageListener> serviceReference,
					ObjectValuePair<String, MessageListener> objectValuePair) {

					_unregisterMessageListener(
						objectValuePair.getKey(), objectValuePair.getValue());

					bundleContext.ungetService(serviceReference);
				}

			});

		_messageListenerServiceTracker.open();

		_serviceRegistration = bundleContext.registerService(
			ManagedServiceFactory.class,
			new DefaultMessageBusManagedServiceFactory(),
			HashMapDictionaryBuilder.put(
				Constants.SERVICE_PID,
				"com.liferay.portal.messaging.internal.configuration." +
					"DestinationWorkerConfiguration"
			).build());

		_messageBusEventListenerServiceTrackerList =
			ServiceTrackerListFactory.open(
				bundleContext, MessageBusEventListener.class);

		_messageBusInterceptorServiceTrackerList =
			ServiceTrackerListFactory.open(
				bundleContext, MessageBusInterceptor.class);
	}

	@Deactivate
	protected void deactivate() {
		_destinationServiceTracker.close();

		_destinationEventListenerServiceTracker.close();

		_messageBusInterceptorServiceTrackerList.close();

		_serviceRegistration.unregister();

		_messageListenerServiceTracker.close();

		_messageBusEventListenerServiceTrackerList.close();

		shutdown(true);

		for (Destination destination : _destinations.values()) {
			destination.destroy();
		}

		_destinations.clear();
	}

	private void _addDestination(Destination destination) {
		Destination oldDestination = _destinations.get(destination.getName());

		if (oldDestination != null) {
			oldDestination.copyDestinationEventListeners(destination);
			oldDestination.copyMessageListeners(destination);
		}
		else {
			List<MessageListener> messageListeners =
				_queuedMessageListeners.remove(destination.getName());

			if (ListUtil.isNotEmpty(messageListeners)) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Registering ", messageListeners.size(),
							" queued message listeners for destination ",
							destination.getName()));
				}

				for (MessageListener messageListener : messageListeners) {
					destination.register(messageListener);
				}
			}
		}

		destination.open();

		_destinations.put(destination.getName(), destination);

		if (oldDestination != null) {
			oldDestination.destroy();

			for (MessageBusEventListener messageBusEventListener :
					_messageBusEventListenerServiceTrackerList) {

				messageBusEventListener.destinationRemoved(oldDestination);
			}
		}

		for (MessageBusEventListener messageBusEventListener :
				_messageBusEventListenerServiceTrackerList) {

			messageBusEventListener.destinationAdded(destination);
		}
	}

	private synchronized boolean _registerMessageListener(
		String destinationName, MessageListener messageListener) {

		Destination destination = _destinations.get(destinationName);

		if (destination != null) {
			return destination.register(messageListener);
		}

		List<MessageListener> queuedMessageListeners =
			_queuedMessageListeners.get(destinationName);

		if (queuedMessageListeners == null) {
			queuedMessageListeners = new ArrayList<>();

			_queuedMessageListeners.put(
				destinationName, queuedMessageListeners);
		}

		queuedMessageListeners.add(messageListener);

		if (_log.isWarnEnabled()) {
			_log.warn(
				"Queuing message listener until destination " +
					destinationName + " is added");
		}

		return false;
	}

	private Destination _removeDestination(String destinationName) {
		Destination destination = _destinations.remove(destinationName);

		if (destination == null) {
			return null;
		}

		destination.destroy();

		for (MessageBusEventListener messageBusEventListener :
				_messageBusEventListenerServiceTrackerList) {

			messageBusEventListener.destinationRemoved(destination);
		}

		return destination;
	}

	private synchronized boolean _unregisterMessageListener(
		String destinationName, MessageListener messageListener) {

		Destination destination = _destinations.get(destinationName);

		if (destination != null) {
			return destination.unregister(messageListener);
		}

		List<MessageListener> queuedMessageListeners =
			_queuedMessageListeners.get(destinationName);

		if (ListUtil.isEmpty(queuedMessageListeners)) {
			return false;
		}

		return queuedMessageListeners.remove(messageListener);
	}

	private void _updateDestination(
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

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultMessageBus.class);

	private BundleContext _bundleContext;
	private ServiceTracker<DestinationEventListener, DestinationEventListener>
		_destinationEventListenerServiceTracker;
	private final Map<String, Destination> _destinations =
		new ConcurrentHashMap<>();
	private ServiceTracker<Destination, Destination> _destinationServiceTracker;
	private final Map<String, DestinationWorkerConfiguration>
		_destinationWorkerConfigurations = new ConcurrentHashMap<>();
	private final Map<String, String> _factoryPidsToDestinationNames =
		new ConcurrentHashMap<>();
	private ServiceTrackerList<MessageBusEventListener>
		_messageBusEventListenerServiceTrackerList;
	private ServiceTrackerList<MessageBusInterceptor>
		_messageBusInterceptorServiceTrackerList;
	private ServiceTracker
		<MessageListener, ObjectValuePair<String, MessageListener>>
			_messageListenerServiceTracker;
	private final Map<String, List<MessageListener>> _queuedMessageListeners =
		new HashMap<>();
	private ServiceRegistration<ManagedServiceFactory> _serviceRegistration;

	private class DefaultMessageBusManagedServiceFactory
		implements ManagedServiceFactory {

		@Override
		public void deleted(String factoryPid) {
			String destinationName = _factoryPidsToDestinationNames.remove(
				factoryPid);

			_destinationWorkerConfigurations.remove(destinationName);
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

			_factoryPidsToDestinationNames.put(
				factoryPid, destinationWorkerConfiguration.destinationName());

			_destinationWorkerConfigurations.put(
				destinationWorkerConfiguration.destinationName(),
				destinationWorkerConfiguration);

			Destination destination = _destinations.get(
				destinationWorkerConfiguration.destinationName());

			_updateDestination(destination, destinationWorkerConfiguration);
		}

	}

	private class DestinationEventListenerServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<DestinationEventListener, DestinationEventListener> {

		@Override
		public DestinationEventListener addingService(
			ServiceReference<DestinationEventListener> serviceReference) {

			DestinationEventListener destinationEventListener =
				_bundleContext.getService(serviceReference);

			String destinationName = GetterUtil.getString(
				serviceReference.getProperty("destination.name"));

			Destination destination = _destinations.get(destinationName);

			if (destination != null) {
				destination.addDestinationEventListener(
					destinationEventListener);

				return destinationEventListener;
			}

			if (_log.isInfoEnabled()) {
				_log.info(
					"Unable to unregister destination event listener for " +
						destinationName);
			}

			return destinationEventListener;
		}

		@Override
		public void modifiedService(
			ServiceReference<DestinationEventListener> serviceReference,
			DestinationEventListener destinationEventListener) {
		}

		@Override
		public void removedService(
			ServiceReference<DestinationEventListener> serviceReference,
			DestinationEventListener destinationEventListener) {

			_bundleContext.ungetService(serviceReference);

			String destinationName = GetterUtil.getString(
				serviceReference.getProperty("destination.name"));

			Destination destination = _destinations.get(destinationName);

			if (destination != null) {
				destination.removeDestinationEventListener(
					destinationEventListener);

				return;
			}

			if (_log.isInfoEnabled()) {
				_log.info(
					"Unable to unregister destination event listener for " +
						destinationName);
			}
		}

	}

	private class DestinationServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<Destination, Destination> {

		@Override
		public Destination addingService(
			ServiceReference<Destination> serviceReference) {

			Destination destination = _bundleContext.getService(
				serviceReference);

			_addDestination(destination);

			DestinationWorkerConfiguration destinationWorkerConfiguration =
				_destinationWorkerConfigurations.get(
					GetterUtil.getString(
						serviceReference.getProperty("destination.name")));

			_updateDestination(destination, destinationWorkerConfiguration);

			return destination;
		}

		@Override
		public void modifiedService(
			ServiceReference<Destination> serviceReference,
			Destination destination) {
		}

		@Override
		public void removedService(
			ServiceReference<Destination> serviceReference,
			Destination destination) {

			_bundleContext.ungetService(serviceReference);

			_removeDestination(destination.getName());
		}

	}

}
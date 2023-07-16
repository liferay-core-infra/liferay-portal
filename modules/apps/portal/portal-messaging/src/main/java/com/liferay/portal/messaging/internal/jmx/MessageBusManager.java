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

package com.liferay.portal.messaging.internal.jmx;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import java.util.Dictionary;
import java.util.Set;

import javax.management.DynamicMBean;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Michael C. Han
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"jmx.objectname=com.liferay.portal.messaging:classification=message_bus,name=MessageBusManager",
		"jmx.objectname.cache.key=MessageBusManager"
	},
	service = DynamicMBean.class
)
public class MessageBusManager
	extends StandardMBean implements MessageBusManagerMBean {

	public MessageBusManager() throws NotCompliantMBeanException {
		super(MessageBusManagerMBean.class);
	}

	@Override
	public int getDestinationCount() {
		Set<String> destinationNames = _serviceTrackerMap.keySet();

		return destinationNames.size();
	}

	@Override
	public int getMessageListenerCount(String destinationName) {
		Destination destination = _messageBus.getDestination(destinationName);

		if (destination == null) {
			return 0;
		}

		return destination.getMessageListenerCount();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, Destination.class, "(destination.name=*)",
			ServiceReferenceMapperFactory.create(
				bundleContext,
				(destination, emitter) -> emitter.emit(destination.getName())),
			new ServiceTrackerCustomizer
				<Destination, ServiceRegistration<DynamicMBean>>() {

				@Override
				public ServiceRegistration<DynamicMBean> addingService(
					ServiceReference<Destination> serviceReference) {

					ServiceRegistration<DynamicMBean> serviceRegistration =
						null;

					Destination destination = bundleContext.getService(
						serviceReference);

					try {
						DestinationStatisticsManager
							destinationStatisticsManager =
								new DestinationStatisticsManager(destination);

						Dictionary<String, Object> mBeanProperties =
							HashMapDictionaryBuilder.<String, Object>put(
								"jmx.objectname",
								destinationStatisticsManager.getObjectName()
							).put(
								"jmx.objectname.cache.key",
								destinationStatisticsManager.
									getObjectNameCacheKey()
							).build();

						serviceRegistration = bundleContext.registerService(
							DynamicMBean.class, destinationStatisticsManager,
							mBeanProperties);
					}
					catch (NotCompliantMBeanException
								notCompliantMBeanException) {

						if (_log.isInfoEnabled()) {
							_log.info(
								"Unable to register destination mbean",
								notCompliantMBeanException);
						}
					}

					return serviceRegistration;
				}

				@Override
				public void modifiedService(
					ServiceReference<Destination> serviceReference,
					ServiceRegistration<DynamicMBean> serviceRegistration) {
				}

				@Override
				public void removedService(
					ServiceReference<Destination> serviceReference,
					ServiceRegistration<DynamicMBean> serviceRegistration) {

					bundleContext.ungetService(serviceReference);

					serviceRegistration.unregister();
				}

			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MessageBusManager.class);

	@Reference
	private MessageBus _messageBus;

	private ServiceTrackerMap<String, ServiceRegistration<DynamicMBean>>
		_serviceTrackerMap;

}
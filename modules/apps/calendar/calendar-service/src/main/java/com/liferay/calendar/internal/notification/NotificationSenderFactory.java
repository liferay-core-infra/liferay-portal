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

package com.liferay.calendar.internal.notification;

import com.liferay.calendar.notification.NotificationSender;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Eduardo Lundgren
 */
@Component(service = NotificationSenderFactory.class)
public class NotificationSenderFactory {

	public NotificationSender getNotificationSender(String notificationType)
		throws PortalException {

		NotificationSender notificationSender = _notificationSenders.get(
			notificationType);

		if (notificationSender == null) {
			throw new PortalException(
				"Invalid notification type " + notificationType);
		}

		return notificationSender;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = new ServiceTracker<>(
			bundleContext, NotificationSender.class,
			new ServiceTrackerCustomizer
				<NotificationSender, NotificationSender>() {

				@Override
				public NotificationSender addingService(
					ServiceReference<NotificationSender> serviceReference) {

					NotificationSender notificationSender =
						bundleContext.getService(serviceReference);

					String notificationType =
						(String)serviceReference.getProperty(
							"notification.type");

					if (notificationType == null) {
						throw new IllegalArgumentException(
							"The property \"notification.type\" is null");
					}

					NotificationSender previousNotificationSender =
						_notificationSenders.put(
							notificationType, notificationSender);

					if (_log.isWarnEnabled() &&
						(previousNotificationSender != null)) {

						Class<?> clazz = previousNotificationSender.getClass();

						_log.warn(
							"Overriding notification sender " +
								clazz.getName());
					}

					return notificationSender;
				}

				@Override
				public void modifiedService(
					ServiceReference<NotificationSender> serviceReference,
					NotificationSender service) {
				}

				@Override
				public void removedService(
					ServiceReference<NotificationSender> serviceReference,
					NotificationSender service) {

					String notificationType =
						(String)serviceReference.getProperty(
							"notification.type");

					if (notificationType == null) {
						throw new IllegalArgumentException(
							"The property \"notification.type\" is null");
					}

					_notificationSenders.remove(notificationType);

					bundleContext.ungetService(serviceReference);
				}

			});

		_serviceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		NotificationSenderFactory.class);

	private final Map<String, NotificationSender> _notificationSenders =
		new ConcurrentHashMap<>();
	private ServiceTracker<NotificationSender, NotificationSender>
		_serviceTracker;

}
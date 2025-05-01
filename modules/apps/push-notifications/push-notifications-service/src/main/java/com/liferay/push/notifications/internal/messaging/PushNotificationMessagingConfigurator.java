/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.push.notifications.internal.messaging;

import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationDefinition;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.push.notifications.constants.PushNotificationsDestinationNames;
import com.liferay.push.notifications.service.PushNotificationsDeviceLocalService;

import java.util.ArrayList;
import java.util.Dictionary;
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
@Component(service = PushNotificationMessagingConfigurator.class)
public class PushNotificationMessagingConfigurator {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_registerMessaging(
			bundleContext, PushNotificationsDestinationNames.PUSH_NOTIFICATION,
			new PushNotificationsMessageListener(
				_pushNotificationsDeviceLocalService));

		_registerMessaging(
			bundleContext,
			PushNotificationsDestinationNames.PUSH_NOTIFICATION_RESPONSE,
			new PushNotificationsResponseMessageListener(_jsonFactory));
	}

	@Deactivate
	protected void deactivate() {
		if (!_serviceRegistrations.isEmpty()) {
			for (ServiceRegistration<?> serviceRegistration :
					_serviceRegistrations) {

				serviceRegistration.unregister();
			}

			_serviceRegistrations.clear();
		}
	}

	private void _registerMessaging(
		BundleContext bundleContext, String destinationName,
		MessageListener messageListener) {

		DestinationDefinition destinationDefinition =
			new DestinationConfiguration(
				DestinationConfiguration.DESTINATION_TYPE_SERIAL,
				destinationName);

		Dictionary<String, Object> dictionary = MapUtil.singletonDictionary(
			"destination.name", destinationName);

		_serviceRegistrations.add(
			bundleContext.registerService(
				DestinationDefinition.class, destinationDefinition,
				dictionary));

		_serviceRegistrations.add(
			bundleContext.registerService(
				MessageListener.class, messageListener, dictionary));
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private PushNotificationsDeviceLocalService
		_pushNotificationsDeviceLocalService;

	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

}
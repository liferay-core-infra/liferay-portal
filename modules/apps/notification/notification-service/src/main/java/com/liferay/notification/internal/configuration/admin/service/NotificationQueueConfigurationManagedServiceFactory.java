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

package com.liferay.notification.internal.configuration.admin.service;

import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tina Tian
 */
@Component(
	property = Constants.SERVICE_PID + "=com.liferay.notification.internal.configuration.NotificationQueueConfiguration.scoped",
	service = ManagedServiceFactory.class
)
public class NotificationQueueConfigurationManagedServiceFactory
	implements ManagedServiceFactory {

	@Override
	public void deleted(String pid) {
		ComponentInstance<?> componentInstance = _componentInstances.remove(
			pid);

		componentInstance.dispose();
	}

	@Override
	public String getName() {
		return NotificationQueueConfigurationManagedServiceFactory.class.
			getName();
	}

	@Override
	public void updated(String pid, Dictionary<String, ?> dictionary) {
		_componentInstances.compute(
			pid,
			(key, value) -> {
				if (value != null) {
					value.dispose();
				}

				return _componentFactory.newInstance(
					HashMapDictionaryBuilder.<String, Object>putAll(
						dictionary
					).remove(
						Constants.SERVICE_PID
					).build());
			});
	}

	@Reference(
		target = "(component.factory=com.liferay.notification.internal.messaging.CheckNotificationQueueEntryMessageListener)"
	)
	private ComponentFactory _componentFactory;

	private final Map<String, ComponentInstance<?>> _componentInstances =
		new ConcurrentHashMap<>();

}
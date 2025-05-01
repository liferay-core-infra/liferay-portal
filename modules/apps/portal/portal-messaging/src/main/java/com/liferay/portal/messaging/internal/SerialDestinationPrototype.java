/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.messaging.internal;

import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationDefinition;
import com.liferay.portal.kernel.messaging.MessageListenerRegistry;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.UserLocalService;

/**
 * @author Michael C. Han
 */
public class SerialDestinationPrototype implements DestinationPrototype {

	public SerialDestinationPrototype(
		MessageListenerRegistry messageListenerRegistry,
		PortalExecutorManager portalExecutorManager,
		PermissionCheckerFactory permissionCheckerFactory,
		UserLocalService userLocalService) {

		_messageListenerRegistry = messageListenerRegistry;
		_portalExecutorManager = portalExecutorManager;
		_permissionCheckerFactory = permissionCheckerFactory;
		_userLocalService = userLocalService;
	}

	@Override
	public Destination createDestination(
		DestinationDefinition destinationDefinition) {

		SerialDestination serialDestination = new SerialDestination();

		serialDestination.setDestinationType(
			destinationDefinition.getDestinationType());
		serialDestination.setName(destinationDefinition.getDestinationName());
		serialDestination.setMaximumQueueSize(
			destinationDefinition.getMaximumQueueSize());
		serialDestination.setMessageListenerRegistry(_messageListenerRegistry);
		serialDestination.setPermissionCheckerFactory(
			_permissionCheckerFactory);
		serialDestination.setPortalExecutorManager(_portalExecutorManager);
		serialDestination.setRejectedExecutionHandler(
			destinationDefinition.getRejectedExecutionHandler());
		serialDestination.setUserLocalService(_userLocalService);
		serialDestination.setWorkersSize(_WORKERS_CORE_SIZE, _WORKERS_MAX_SIZE);

		return serialDestination;
	}

	private static final int _WORKERS_CORE_SIZE = 1;

	private static final int _WORKERS_MAX_SIZE = 1;

	private final MessageListenerRegistry _messageListenerRegistry;
	private final PermissionCheckerFactory _permissionCheckerFactory;
	private final PortalExecutorManager _portalExecutorManager;
	private final UserLocalService _userLocalService;

}
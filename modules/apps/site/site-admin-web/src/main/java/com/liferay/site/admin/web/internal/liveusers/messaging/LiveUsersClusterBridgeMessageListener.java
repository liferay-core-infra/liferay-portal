/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.liveusers.messaging;

import com.liferay.portal.kernel.cluster.Priority;
import com.liferay.portal.kernel.cluster.messaging.ClusterBridgeMessageListener;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.MessageListener;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Janis Zhang
 */
@Component(
	property = "destination.name=" + DestinationNames.LIVE_USERS,
	service = MessageListener.class
)
public class LiveUsersClusterBridgeMessageListener
	extends ClusterBridgeMessageListener {

	@Activate
	protected void activate() {
		setPriority(Priority.LEVEL5);
	}

}
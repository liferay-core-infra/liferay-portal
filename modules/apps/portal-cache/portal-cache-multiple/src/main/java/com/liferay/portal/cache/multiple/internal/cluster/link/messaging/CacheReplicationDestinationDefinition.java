/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.multiple.internal.cluster.link.messaging;

import com.liferay.portal.cache.multiple.internal.constants.PortalCacheDestinationNames;
import com.liferay.portal.kernel.messaging.DestinationDefinition;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tina Tian
 */
@Component(
	enabled = false,
	property = "destination.name=" + PortalCacheDestinationNames.CACHE_REPLICATION,
	service = DestinationDefinition.class
)
public class CacheReplicationDestinationDefinition
	implements DestinationDefinition {

	@Override
	public String getDestinationName() {
		return PortalCacheDestinationNames.CACHE_REPLICATION;
	}

	@Override
	public String getDestinationType() {
		return DESTINATION_TYPE_PARALLEL;
	}

}
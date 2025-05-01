/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.messaging;

import com.liferay.change.tracking.constants.CTDestinationNames;
import com.liferay.portal.kernel.messaging.DestinationDefinition;

import org.osgi.service.component.annotations.Component;

/**
 * @author Preston Crary
 */
@Component(
	property = "destination.name=" + CTDestinationNames.CT_COLLECTION_SCHEDULED_PUBLISH,
	service = DestinationDefinition.class
)
public class CTCollectionScheduledPublishDestinationDefinition
	implements DestinationDefinition {

	@Override
	public String getDestinationName() {
		return CTDestinationNames.CT_COLLECTION_SCHEDULED_PUBLISH;
	}

	@Override
	public String getDestinationType() {
		return DESTINATION_TYPE_SYNCHRONOUS;
	}

}
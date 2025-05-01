/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.messaging;

import com.liferay.portal.kernel.messaging.DestinationDefinition;
import com.liferay.segments.internal.constants.SegmentsDestinationNames;

import org.osgi.service.component.annotations.Component;

/**
 * @author Preston Crary
 */
@Component(
	property = "destination.name=" + SegmentsDestinationNames.SEGMENTS_ENTRY_REINDEX,
	service = DestinationDefinition.class
)
public class SegmentsEntryReindexDestinationDefinition
	implements DestinationDefinition {

	@Override
	public String getDestinationName() {
		return SegmentsDestinationNames.SEGMENTS_ENTRY_REINDEX;
	}

	@Override
	public String getDestinationType() {
		return DESTINATION_TYPE_SERIAL;
	}

}
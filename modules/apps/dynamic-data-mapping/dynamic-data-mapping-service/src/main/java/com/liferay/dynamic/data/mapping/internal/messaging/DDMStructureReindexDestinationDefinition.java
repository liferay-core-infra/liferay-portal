/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.messaging;

import com.liferay.dynamic.data.mapping.internal.constants.DDMDestinationNames;
import com.liferay.portal.kernel.messaging.DestinationDefinition;

import org.osgi.service.component.annotations.Component;

/**
 * @author Preston Crary
 */
@Component(
	property = "destination.name=" + DDMDestinationNames.DDM_STRUCTURE_REINDEX,
	service = DestinationDefinition.class
)
public class DDMStructureReindexDestinationDefinition
	implements DestinationDefinition {

	@Override
	public String getDestinationName() {
		return DDMDestinationNames.DDM_STRUCTURE_REINDEX;
	}

	@Override
	public String getDestinationType() {
		return DESTINATION_TYPE_SERIAL;
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.resources.importer.internal.messaging;

import com.liferay.exportimport.resources.importer.internal.constants.ResourcesImporterDestinationNames;
import com.liferay.portal.kernel.messaging.DestinationDefinition;

import org.osgi.service.component.annotations.Component;

/**
 * @author Dante Wang
 */
@Component(
	property = "destination.name=" + ResourcesImporterDestinationNames.RESOURCES_IMPORTER,
	service = DestinationDefinition.class
)
public class ResourcesImporterDestinationDefinition
	implements DestinationDefinition {

	@Override
	public String getDestinationName() {
		return ResourcesImporterDestinationNames.RESOURCES_IMPORTER;
	}

	@Override
	public String getDestinationType() {
		return DESTINATION_TYPE_SERIAL;
	}

}
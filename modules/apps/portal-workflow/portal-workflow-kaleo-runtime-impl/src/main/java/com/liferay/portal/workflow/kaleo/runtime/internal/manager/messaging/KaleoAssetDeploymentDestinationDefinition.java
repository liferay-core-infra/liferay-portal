/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.internal.manager.messaging;

import com.liferay.portal.kernel.messaging.DestinationDefinition;
import com.liferay.portal.workflow.kaleo.runtime.constants.KaleoRuntimeDestinationNames;

import org.osgi.service.component.annotations.Component;

/**
 * @author Dante Wang
 */
@Component(
	property = "destination.name=" + KaleoRuntimeDestinationNames.WORKFLOW_DEFINITION_LINK,
	service = DestinationDefinition.class
)
public class KaleoAssetDeploymentDestinationDefinition
	implements DestinationDefinition {

	@Override
	public String getDestinationName() {
		return KaleoRuntimeDestinationNames.WORKFLOW_DEFINITION_LINK;
	}

	@Override
	public String getDestinationType() {
		return DESTINATION_TYPE_SYNCHRONOUS;
	}

}
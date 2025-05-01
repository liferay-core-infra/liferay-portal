/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.antivirus.async.store.internal.messaging;

import com.liferay.antivirus.async.store.constants.AntivirusAsyncDestinationNames;
import com.liferay.portal.kernel.messaging.DestinationDefinition;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tina Tian
 */
@Component(
	property = "destination.name=" + AntivirusAsyncDestinationNames.ANTIVIRUS_BATCH,
	service = DestinationDefinition.class
)
public class AntivirusAsyncBatchDestinationDefinition
	implements DestinationDefinition {

	@Override
	public String getDestinationName() {
		return AntivirusAsyncDestinationNames.ANTIVIRUS_BATCH;
	}

	@Override
	public String getDestinationType() {
		return DESTINATION_TYPE_SERIAL;
	}

	@Override
	public int getMaximumQueueSize() {
		return 1;
	}

}
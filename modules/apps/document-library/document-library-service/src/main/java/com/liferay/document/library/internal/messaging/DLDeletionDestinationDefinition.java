/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.messaging;

import com.liferay.document.library.internal.configuration.StoreAreaConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.messaging.DestinationDefinition;
import com.liferay.portal.kernel.messaging.DestinationNames;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 */
@Component(
	configurationPid = "com.liferay.document.library.internal.configuration.StoreAreaConfiguration",
	property = "destination.name=" + DestinationNames.DOCUMENT_LIBRARY_DELETION,
	service = DestinationDefinition.class
)
public class DLDeletionDestinationDefinition implements DestinationDefinition {

	@Override
	public String getDestinationName() {
		return DestinationNames.DOCUMENT_LIBRARY_DELETION;
	}

	@Override
	public String getDestinationType() {
		return DESTINATION_TYPE_SERIAL;
	}

	@Override
	public int getMaximumQueueSize() {
		int maxDeletionQueueSize =
			_storeAreaConfiguration.maxDeletionQueueSize();

		if (maxDeletionQueueSize <= 0) {
			return Integer.MAX_VALUE;
		}

		return maxDeletionQueueSize;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_storeAreaConfiguration = ConfigurableUtil.createConfigurable(
			StoreAreaConfiguration.class, properties);
	}

	private StoreAreaConfiguration _storeAreaConfiguration;

}
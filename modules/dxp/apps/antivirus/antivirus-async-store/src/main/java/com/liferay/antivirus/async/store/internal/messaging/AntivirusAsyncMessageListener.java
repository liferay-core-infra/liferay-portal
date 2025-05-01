/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.antivirus.async.store.internal.messaging;

import com.liferay.antivirus.async.store.AntivirusScannerHelper;
import com.liferay.antivirus.async.store.constants.AntivirusAsyncDestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 */
@Component(
	configurationPid = "com.liferay.antivirus.async.store.configuration.AntivirusAsyncConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = "destination.name=" + AntivirusAsyncDestinationNames.ANTIVIRUS,
	service = MessageListener.class
)
public class AntivirusAsyncMessageListener implements MessageListener {

	@Override
	public void receive(Message message) {
		_antivirusScannerHelper.processMessage(message);
	}

	@Reference
	private AntivirusScannerHelper _antivirusScannerHelper;

}
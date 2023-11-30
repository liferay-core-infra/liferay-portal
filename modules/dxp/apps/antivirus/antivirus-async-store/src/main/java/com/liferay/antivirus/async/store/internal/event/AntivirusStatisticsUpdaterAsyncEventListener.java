/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.antivirus.async.store.internal.event;

import com.liferay.antivirus.async.store.constants.AntivirusAsyncDestinationNames;
import com.liferay.antivirus.async.store.event.AntivirusAsyncEvent;
import com.liferay.antivirus.async.store.event.AntivirusAsyncEventListener;
import com.liferay.antivirus.async.store.jmx.AntivirusStatisticsHolder;
import com.liferay.portal.kernel.messaging.Message;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

/**
 * @author Roberto Cassio Silva do Nascimento Junior
 */
@Component(
	configurationPid = "com.liferay.antivirus.async.store.configuration.AntivirusAsyncConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = AntivirusAsyncEventListener.class
)
public class AntivirusStatisticsUpdaterAsyncEventListener
	implements AntivirusAsyncEventListener {

	@Override
	public void receive(Message message) {
		AntivirusAsyncEvent antivirusAsyncEvent =
			(AntivirusAsyncEvent)message.get("antivirusAsyncEvent");

		_antivirusStatisticsHolder.onAntivirusEvent(antivirusAsyncEvent);
	}

	private final AntivirusStatisticsHolder _antivirusStatisticsHolder =
		AntivirusStatisticsHolder.getInstance();

}
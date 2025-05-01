/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.antivirus.async.store.internal.messaging;

import com.liferay.antivirus.async.store.configuration.AntivirusAsyncConfiguration;
import com.liferay.antivirus.async.store.constants.AntivirusAsyncDestinationNames;
import com.liferay.antivirus.async.store.retry.AntivirusAsyncRetryScheduler;
import com.liferay.antivirus.async.store.util.AntivirusAsyncUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationDefinition;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageRunnable;

import java.util.Map;
import java.util.concurrent.RejectedExecutionHandler;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dante Wang
 */
@Component(
	configurationPid = "com.liferay.antivirus.async.store.configuration.AntivirusAsyncConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = "destination.name=" + AntivirusAsyncDestinationNames.ANTIVIRUS,
	service = DestinationDefinition.class
)
public class AntiVirusAsyncDestinationDefinition
	implements DestinationDefinition {

	@Override
	public String getDestinationName() {
		return AntivirusAsyncDestinationNames.ANTIVIRUS;
	}

	@Override
	public String getDestinationType() {
		return DESTINATION_TYPE_SERIAL;
	}

	@Override
	public int getMaximumQueueSize() {
		int maximumQueueSize = _antivirusAsyncConfiguration.maximumQueueSize();

		if (maximumQueueSize == 0) {
			return Integer.MAX_VALUE;
		}

		return maximumQueueSize;
	}

	@Override
	public RejectedExecutionHandler getRejectedExecutionHandler() {
		return (runnable, threadPoolExecutor) -> {
			MessageRunnable messageRunnable = (MessageRunnable)runnable;

			Message message = messageRunnable.getMessage();

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Schedule ",
						AntivirusAsyncUtil.getFileIdentifier(message),
						" into persistent storage because the async antivirus ",
						"queue is overflowing: ", message.getValues()));
			}

			_antivirusAsyncRetryScheduler.schedule(message);
		};
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_antivirusAsyncConfiguration = ConfigurableUtil.createConfigurable(
			AntivirusAsyncConfiguration.class, properties);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AntiVirusAsyncDestinationDefinition.class);

	private AntivirusAsyncConfiguration _antivirusAsyncConfiguration;

	@Reference
	private AntivirusAsyncRetryScheduler _antivirusAsyncRetryScheduler;

}
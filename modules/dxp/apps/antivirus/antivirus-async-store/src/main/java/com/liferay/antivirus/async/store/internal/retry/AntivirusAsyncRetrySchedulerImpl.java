/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.antivirus.async.store.internal.retry;

import com.liferay.antivirus.async.store.configuration.AntivirusAsyncConfiguration;
import com.liferay.antivirus.async.store.retry.AntivirusAsyncRetryScheduler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.TransientValue;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

/**
 * @author Raymond Augé
 */
@Component(
	configurationPid = "com.liferay.antivirus.async.store.configuration.AntivirusAsyncConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = AntivirusAsyncRetryScheduler.class
)
public class AntivirusAsyncRetrySchedulerImpl
	implements AntivirusAsyncRetryScheduler {

	@Override
	public int getPendingMessageCount() {
		return _delayedMessages.size();
	}

	public Message getScheduledMessage() {
		DelayedMessage delayedMessage = _delayedMessages.poll();

		if (delayedMessage != null) {
			return delayedMessage.getMessage();
		}

		return null;
	}

	@Override
	public void schedule(Message message) {
		Map<String, Object> map = message.getValues();

		Set<Map.Entry<String, Object>> entrySet = map.entrySet();

		Iterator<Map.Entry<String, Object>> iterator = entrySet.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = iterator.next();

			if (entry.getValue() instanceof TransientValue) {
				iterator.remove();
			}
		}

		_delayedMessages.add(new DelayedMessage(message));
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_antivirusAsyncConfiguration = ConfigurableUtil.createConfigurable(
			AntivirusAsyncConfiguration.class, properties);
	}

	private AntivirusAsyncConfiguration _antivirusAsyncConfiguration;
	private final DelayQueue<DelayedMessage> _delayedMessages =
		new DelayQueue<>();

	private class DelayedMessage implements Delayed {

		public DelayedMessage(Message message) {
			_message = message;

			_usableTime =
				(_antivirusAsyncConfiguration.retryInterval() * Time.MINUTE) +
					System.currentTimeMillis();
		}

		@Override
		public int compareTo(Delayed delayed) {
			return (int)(getDelay(TimeUnit.MILLISECONDS) -
				delayed.getDelay(TimeUnit.MILLISECONDS));
		}

		@Override
		public long getDelay(TimeUnit timeUnit) {
			return timeUnit.convert(
				_usableTime - System.currentTimeMillis(),
				TimeUnit.MILLISECONDS);
		}

		public Message getMessage() {
			return _message;
		}

		private final Message _message;
		private final long _usableTime;

	}

}
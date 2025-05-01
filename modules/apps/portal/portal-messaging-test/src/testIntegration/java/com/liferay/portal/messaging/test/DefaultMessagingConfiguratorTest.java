/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.messaging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationDefinition;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerRegistry;
import com.liferay.portal.kernel.messaging.config.DefaultMessagingConfigurator;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Michael C. Han
 */
@RunWith(Arquillian.class)
public class DefaultMessagingConfiguratorTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() {
		_defaultMessagingConfigurator.destroy();
	}

	@Test
	public void testPortalClassLoaderDestinationConfiguration()
		throws InterruptedException {

		CountDownLatch countDownLatch = new CountDownLatch(1);

		_defaultMessagingConfigurator = new DefaultMessagingConfigurator() {

			protected void initialize() {
				super.initialize();

				countDownLatch.countDown();
			}

		};

		Set<DestinationConfiguration> destinationConfigurations =
			new HashSet<>();

		destinationConfigurations.add(
			DestinationConfiguration.createSynchronousDestinationConfiguration(
				"liferay/portaltest1"));
		destinationConfigurations.add(
			DestinationConfiguration.createParallelDestinationConfiguration(
				"liferay/portaltest2"));

		_defaultMessagingConfigurator.setDestinationConfigurations(
			destinationConfigurations);

		List<MessageListener> messageListenersList1 = new ArrayList<>();

		Map<String, List<MessageListener>> messageListeners =
			HashMapBuilder.<String, List<MessageListener>>put(
				"liferay/portaltest1", messageListenersList1
			).build();

		messageListenersList1.add(
			new TestMessageListener("liferay/portaltest1"));

		List<MessageListener> messageListenersList2 = new ArrayList<>();

		messageListeners.put("liferay/portaltest2", messageListenersList2);

		messageListenersList2.add(
			new TestMessageListener("liferay/portaltest2"));

		_defaultMessagingConfigurator.setMessageListeners(messageListeners);

		_defaultMessagingConfigurator.afterPropertiesSet();

		countDownLatch.await();

		for (DestinationDefinition destinationDefinition :
				destinationConfigurations) {

			Destination destination = _messageBus.getDestination(
				destinationDefinition.getDestinationName());

			Assert.assertNotNull(destination);

			String destinationName = destination.getName();

			Assert.assertTrue(
				destinationName, destinationName.contains("portaltest"));

			List<MessageListener> destinationMessageListeners =
				_messageListenerRegistry.getMessageListeners(destinationName);

			if (destinationName.equals("liferay/portaltest1")) {
				Assert.assertEquals(
					destinationMessageListeners.toString(), 1,
					destinationMessageListeners.size());
			}

			if (!destinationMessageListeners.isEmpty()) {
				Message message = new Message();

				message.setDestinationName(destinationName);

				destination.send(message);
			}
		}
	}

	private DefaultMessagingConfigurator _defaultMessagingConfigurator;

	@Inject
	private MessageBus _messageBus;

	@Inject
	private MessageListenerRegistry _messageListenerRegistry;

	private static class TestMessageListener implements MessageListener {

		public TestMessageListener(String destinationName) {
			_destinationName = destinationName;
		}

		@Override
		public void receive(Message message) {
			Assert.assertEquals(_destinationName, message.getDestinationName());
		}

		private final String _destinationName;

	}

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.messaging.internal;

import com.liferay.portal.kernel.messaging.DestinationEventListener;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Dante Wang
 */
public class MessageListenerRegistryImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_messageListenerRegistryImpl.activate(_bundleContext);
	}

	@After
	public void tearDown() {
		_messageListenerRegistryImpl.deactivate();
	}

	@Test
	public void testRegisterUnregisterMessageListener() {
		List<MessageListener> eventNotifiedMessageListeners = new ArrayList<>();

		ServiceRegistration<DestinationEventListener> serviceRegistration1 =
			_bundleContext.registerService(
				DestinationEventListener.class,
				new DestinationEventListener() {

					@Override
					public void messageListenerRegistered(
						String destinationName,
						MessageListener messageListener) {

						eventNotifiedMessageListeners.add(messageListener);
					}

					@Override
					public void messageListenerUnregistered(
						String destinationName,
						MessageListener messageListener) {

						eventNotifiedMessageListeners.remove(messageListener);
					}

				},
				HashMapDictionaryBuilder.put(
					"destination.name", "test"
				).build());

		ServiceRegistration<MessageListener> serviceRegistration2 =
			_bundleContext.registerService(
				MessageListener.class,
				message -> {
				},
				HashMapDictionaryBuilder.put(
					"destination.name", "test"
				).build());
		ServiceRegistration<MessageListener> serviceRegistration3 =
			_bundleContext.registerService(
				MessageListener.class,
				message -> {
				},
				HashMapDictionaryBuilder.put(
					"destination.name", "test"
				).build());

		try {
			List<MessageListener> messageListeners =
				_messageListenerRegistryImpl.getMessageListeners("test");

			Assert.assertEquals(
				messageListeners.toString(), 2, messageListeners.size());

			Assert.assertEquals(
				eventNotifiedMessageListeners.toString(), 2,
				eventNotifiedMessageListeners.size());

			serviceRegistration3.unregister();

			messageListeners = _messageListenerRegistryImpl.getMessageListeners(
				"test");

			Assert.assertEquals(
				messageListeners.toString(), 1, messageListeners.size());

			Assert.assertEquals(
				eventNotifiedMessageListeners.toString(), 1,
				eventNotifiedMessageListeners.size());
		}
		finally {
			serviceRegistration2.unregister();
			serviceRegistration1.unregister();
		}
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	private final MessageListenerRegistryImpl _messageListenerRegistryImpl =
		new MessageListenerRegistryImpl();

}
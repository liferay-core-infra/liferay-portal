/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cluster.multiple.internal;

import com.liferay.portal.kernel.cluster.Address;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import java.net.InetAddress;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Jiefeng Wu
 */
public class BaseClusterChannelTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testSendMulticastMessageWhenDoSendMessageFails()
		throws Exception {

		ExecutorService executorService = Executors.newSingleThreadExecutor();

		try {
			CountDownLatch countDownLatch = new CountDownLatch(2);

			List<Thread> threads = new CopyOnWriteArrayList<>();

			AtomicInteger atomicInteger = new AtomicInteger();

			BaseClusterChannel baseClusterChannel = new TestBaseClusterChannel(
				executorService) {

				@Override
				protected void doSendMessage(
					Serializable message, Address address) {

					threads.add(Thread.currentThread());

					countDownLatch.countDown();

					if (atomicInteger.getAndIncrement() == 0) {
						throw new RuntimeException(
							"Simulated channel teardown failure");
					}
				}

			};

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					BaseClusterChannel.class.getName(), LoggerTestUtil.WARN)) {

				baseClusterChannel.sendMulticastMessage("message1");
				baseClusterChannel.sendMulticastMessage("message2");

				Assert.assertTrue(countDownLatch.await(1, TimeUnit.MINUTES));

				Assert.assertEquals(threads.toString(), 2, threads.size());

				Assert.assertSame(threads.get(0), threads.get(1));
			}
		}
		finally {
			executorService.shutdownNow();
		}
	}

	private abstract static class TestBaseClusterChannel
		extends BaseClusterChannel {

		@Override
		public void close() {
		}

		@Override
		public InetAddress getBindInetAddress() {
			return null;
		}

		@Override
		public String getClusterName() {
			return null;
		}

		@Override
		public ClusterReceiver getClusterReceiver() {
			return null;
		}

		@Override
		public Address getLocalAddress() {
			return null;
		}

		private TestBaseClusterChannel(ExecutorService executorService) {
			super(executorService);
		}

	}

}
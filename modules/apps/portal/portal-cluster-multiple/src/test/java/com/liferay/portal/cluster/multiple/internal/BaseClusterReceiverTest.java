/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cluster.multiple.internal;

import com.liferay.portal.kernel.cluster.Address;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Jiefeng Wu
 */
public class BaseClusterReceiverTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAddressesUpdatedWhenDoAddressesUpdatedFails()
		throws Exception {

		_assertTaskFailureDoesNotKillWorkerThread(
			baseClusterReceiver -> {
				baseClusterReceiver.addressesUpdated(Collections.emptyList());
				baseClusterReceiver.addressesUpdated(Collections.emptyList());
				baseClusterReceiver.addressesUpdated(Collections.emptyList());
			});
	}

	@Test
	public void testCoordinatorAddressUpdatedWhenDoCoordinatorAddressUpdatedFails()
		throws Exception {

		_assertTaskFailureDoesNotKillWorkerThread(
			baseClusterReceiver -> {
				baseClusterReceiver.coordinatorAddressUpdated(
					new TestAddress(1));
				baseClusterReceiver.coordinatorAddressUpdated(
					new TestAddress(2));
				baseClusterReceiver.coordinatorAddressUpdated(
					new TestAddress(3));
			});
	}

	@Test
	public void testReceiveWhenDoReceiveFails() throws Exception {
		_assertTaskFailureDoesNotKillWorkerThread(
			baseClusterReceiver -> {
				baseClusterReceiver.receive("message1", null);
				baseClusterReceiver.receive("message2", null);
			});
	}

	private void _assertTaskFailureDoesNotKillWorkerThread(
			Consumer<BaseClusterReceiver> baseClusterReceiverConsumer)
		throws Exception {

		ExecutorService executorService = Executors.newSingleThreadExecutor();

		try {
			List<Thread> threads = new CopyOnWriteArrayList<>();

			AtomicInteger atomicInteger = new AtomicInteger();

			CountDownLatch countDownLatch = new CountDownLatch(2);

			BaseClusterReceiver baseClusterReceiver =
				new TestBaseClusterReceiver(
					executorService, threads, atomicInteger, countDownLatch);

			baseClusterReceiver.openLatch();

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					BaseClusterReceiver.class.getName(), LoggerTestUtil.WARN)) {

				baseClusterReceiverConsumer.accept(baseClusterReceiver);

				Assert.assertTrue(countDownLatch.await(1, TimeUnit.MINUTES));

				Assert.assertEquals(threads.toString(), 2, threads.size());

				Assert.assertSame(threads.get(0), threads.get(1));
			}
		}
		finally {
			executorService.shutdownNow();
		}
	}

	private static class TestBaseClusterReceiver extends BaseClusterReceiver {

		@Override
		protected void doAddressesUpdated(
			List<Address> oldAddresses, List<Address> newAddresses) {

			_runTask();
		}

		@Override
		protected void doCoordinatorAddressUpdated(
			Address oldCoordinatorAddress, Address newCoordinatorAddress) {

			_runTask();
		}

		@Override
		protected void doReceive(Object messagePayload, Address srcAddress) {
			_runTask();
		}

		private TestBaseClusterReceiver(
			ExecutorService executorService, List<Thread> threads,
			AtomicInteger atomicInteger, CountDownLatch countDownLatch) {

			super(executorService);

			_threads = threads;
			_atomicInteger = atomicInteger;
			_countDownLatch = countDownLatch;
		}

		private void _runTask() {
			_threads.add(Thread.currentThread());

			_countDownLatch.countDown();

			if (_atomicInteger.getAndIncrement() == 0) {
				throw new RuntimeException("Simulated receiver task failure");
			}
		}

		private final AtomicInteger _atomicInteger;
		private final CountDownLatch _countDownLatch;
		private final List<Thread> _threads;

	}

}
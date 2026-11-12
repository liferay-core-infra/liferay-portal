/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Luis Ortiz
 */
public class BatchProcessorTest {

	@Test
	public void testAdd() throws Exception {
		CountDownLatch countDownLatch = new CountDownLatch(1);
		AtomicReference<Thread> threadAtomicReference = new AtomicReference<>();

		BatchProcessor<String> batchProcessor = new BatchProcessor<>(
			0, 1,
			items -> {
				threadAtomicReference.set(Thread.currentThread());

				countDownLatch.countDown();
			},
			BatchProcessorTest.class.getName());

		try {
			batchProcessor.add(RandomTestUtil.randomString());

			Assert.assertTrue(countDownLatch.await(_TIMEOUT, TimeUnit.SECONDS));
			Assert.assertNotSame(
				Thread.currentThread(), threadAtomicReference.get());
		}
		finally {
			batchProcessor.close();
		}
	}

	@Test
	public void testAddWithFailingConsumer() throws Exception {
		AtomicInteger atomicInteger = new AtomicInteger();
		CountDownLatch failedCountDownLatch = new CountDownLatch(1);
		CountDownLatch flushedCountDownLatch = new CountDownLatch(1);
		RuntimeException runtimeException = new RuntimeException();

		BatchProcessor<String> batchProcessor = new BatchProcessor<>(
			0, 1,
			items -> {
				if (atomicInteger.getAndIncrement() == 0) {
					failedCountDownLatch.countDown();

					throw runtimeException;
				}

				flushedCountDownLatch.countDown();
			},
			BatchProcessorTest.class.getName());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BatchProcessor.class.getName(), LoggerTestUtil.ERROR)) {

			batchProcessor.add(RandomTestUtil.randomString());

			Assert.assertTrue(
				failedCountDownLatch.await(_TIMEOUT, TimeUnit.SECONDS));

			batchProcessor.add(RandomTestUtil.randomString());

			Assert.assertTrue(
				flushedCountDownLatch.await(_TIMEOUT, TimeUnit.SECONDS));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertSame(runtimeException, logEntry.getThrowable());
		}
		finally {
			batchProcessor.close();
		}
	}

	@Test
	public void testAddWithFailingConsumerAndLargeBatchSize() throws Exception {
		AtomicInteger atomicInteger = new AtomicInteger();
		CountDownLatch failedCountDownLatch = new CountDownLatch(1);
		CountDownLatch flushedCountDownLatch = new CountDownLatch(1);

		BatchProcessor<String> batchProcessor = new BatchProcessor<>(
			10, 100,
			items -> {
				if (atomicInteger.getAndIncrement() == 0) {
					failedCountDownLatch.countDown();

					throw new RuntimeException();
				}

				flushedCountDownLatch.countDown();
			},
			BatchProcessorTest.class.getName());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BatchProcessor.class.getName(), LoggerTestUtil.OFF)) {

			batchProcessor.add(RandomTestUtil.randomString());

			Assert.assertTrue(
				failedCountDownLatch.await(_TIMEOUT, TimeUnit.SECONDS));

			batchProcessor.add(RandomTestUtil.randomString());

			Assert.assertTrue(
				flushedCountDownLatch.await(_TIMEOUT, TimeUnit.SECONDS));
		}
		finally {
			batchProcessor.close();
		}
	}

	private static final long _TIMEOUT = 10;

}
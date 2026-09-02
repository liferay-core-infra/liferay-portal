/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cluster.multiple.internal;

import com.liferay.portal.kernel.cluster.Address;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Debora Buriti
 */
public class BaseClusterReceiverTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testReceiveWhenDoReceiveThrowsError() throws Exception {
		_assertReceiveFailureContained(new Error());
	}

	@Test
	public void testReceiveWhenDoReceiveThrowsRuntimeException()
		throws Exception {

		_assertReceiveFailureContained(new RuntimeException());
	}

	private void _assertReceiveFailureContained(Throwable throwable)
		throws Exception {

		ExecutorService executorService = Executors.newSingleThreadExecutor();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BaseClusterReceiver.class.getName(), LoggerTestUtil.WARN)) {

			BaseClusterReceiver baseClusterReceiver =
				new TestBaseClusterReceiver(executorService, throwable);

			baseClusterReceiver.openLatch();

			baseClusterReceiver.receive("message", null);

			executorService.shutdown();

			Assert.assertTrue(
				executorService.awaitTermination(1, TimeUnit.MINUTES));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(LoggerTestUtil.WARN, logEntry.getPriority());
			Assert.assertSame(throwable, logEntry.getThrowable());
		}
		finally {
			executorService.shutdownNow();
		}
	}

	private static class TestBaseClusterReceiver extends BaseClusterReceiver {

		@Override
		protected void doReceive(Object messagePayload, Address srcAddress) {
			if (_throwable instanceof RuntimeException) {
				throw (RuntimeException)_throwable;
			}

			throw (Error)_throwable;
		}

		private TestBaseClusterReceiver(
			ExecutorService executorService, Throwable throwable) {

			super(executorService);

			_throwable = throwable;
		}

		private final Throwable _throwable;

	}

}
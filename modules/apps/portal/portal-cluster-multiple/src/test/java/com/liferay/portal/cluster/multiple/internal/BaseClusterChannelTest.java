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

import java.io.Serializable;

import java.net.InetAddress;

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
public class BaseClusterChannelTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testSendMulticastMessageWhenDoSendMessageThrowsError()
		throws Exception {

		_assertSendFailureContained(new Error());
	}

	@Test
	public void testSendMulticastMessageWhenDoSendMessageThrowsRuntimeException()
		throws Exception {

		_assertSendFailureContained(new RuntimeException());
	}

	private void _assertSendFailureContained(Throwable throwable)
		throws Exception {

		ExecutorService executorService = Executors.newSingleThreadExecutor();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BaseClusterChannel.class.getName(), LoggerTestUtil.WARN)) {

			BaseClusterChannel baseClusterChannel = new TestBaseClusterChannel(
				executorService, throwable);

			baseClusterChannel.sendMulticastMessage("message");

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

	private static class TestBaseClusterChannel extends BaseClusterChannel {

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

		@Override
		protected void doSendMessage(Serializable message, Address address) {
			if (_throwable instanceof RuntimeException) {
				throw (RuntimeException)_throwable;
			}

			throw (Error)_throwable;
		}

		private TestBaseClusterChannel(
			ExecutorService executorService, Throwable throwable) {

			super(executorService);

			_throwable = throwable;
		}

		private final Throwable _throwable;

	}

}
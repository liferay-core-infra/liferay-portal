/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cluster.multiple.internal.jgroups;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.cluster.multiple.configuration.ClusterExecutorConfiguration;
import com.liferay.portal.cluster.multiple.internal.ClusterChannel;
import com.liferay.portal.cluster.multiple.internal.ClusterChannelFactory;
import com.liferay.portal.cluster.multiple.internal.ClusterReceiver;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cluster.Address;
import com.liferay.portal.kernel.test.performance.PerformanceTimer;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Kevin Lee
 */
public class JGroupsClusterChannelPerformanceTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		Class<?> clazz = JGroupsClusterChannelPerformanceTest.class;

		Properties properties = PropertiesUtil.load(
			clazz.getResourceAsStream(
				"dependencies/jgroups-cluster-channel-performance.properties"),
			"UTF-8");

		_executorService = Executors.newFixedThreadPool(
			GetterUtil.getInteger(
				properties.getProperty(
					"jgroups.cluster.channel.threads.count")));
		_iterations = GetterUtil.getInteger(
			properties.getProperty("jgroups.cluster.channel.iterations"));
		_messagesCount = GetterUtil.getInteger(
			properties.getProperty("jgroups.cluster.channel.messages.count"));
		_messagesLength = GetterUtil.getInteger(
			properties.getProperty("jgroups.cluster.channel.messages.length"));

		String logFile = properties.getProperty(
			"jgroups.cluster.channel.log.file");

		if (Validator.isNotNull(logFile)) {
			_logFilePath = Paths.get(logFile);
		}

		ClusterChannelFactory clusterChannelFactory =
			new JGroupsClusterChannelFactory(
				ConfigurableUtil.createConfigurable(
					ClusterExecutorConfiguration.class,
					Collections.emptyMap()));

		for (int i = 0;
			 i < GetterUtil.getInteger(
				 properties.getProperty(
					 "jgroups.cluster.channel.receivers.count"));
			 i++) {

			_receiverClusterChannels.add(
				clusterChannelFactory.createClusterChannel(
					_executorService, "sender",
					PropsUtil.get(
						PropsKeys.CLUSTER_LINK_CHANNEL_PROPERTIES_CONTROL),
					"test", new TestClusterReceiver()));
		}

		_senderClusterChannel = clusterChannelFactory.createClusterChannel(
			_executorService, "sender",
			PropsUtil.get(PropsKeys.CLUSTER_LINK_CHANNEL_PROPERTIES_CONTROL),
			"test", new TestClusterReceiver());
	}

	@AfterClass
	public static void tearDownClass() {
		for (ClusterChannel receiverClusterChannel : _receiverClusterChannels) {
			receiverClusterChannel.close();
		}

		_senderClusterChannel.close();

		_executorService.shutdown();
	}

	@Test
	public void testMulticast() throws Exception {
		_test(true);
	}

	@Test
	public void testUnicast() throws Exception {
		_test(false);
	}

	private void _test(boolean multicast) throws Exception {
		String message = RandomTestUtil.randomString(_messagesLength);

		for (int iteration = 1; iteration <= _iterations; iteration++) {
			for (ClusterChannel receiverClusterChannel :
					_receiverClusterChannels) {

				TestClusterReceiver testClusterReceiver =
					(TestClusterReceiver)
						receiverClusterChannel.getClusterReceiver();

				testClusterReceiver.reset(message);
			}

			try (PerformanceTimer performanceTimer = new PerformanceTimer(
					_logFilePath, Long.MAX_VALUE,
					StringBundler.concat(
						" Iteration ", iteration, " (", _messagesCount,
						" messages x ", _messagesLength, " length x ",
						_receiverClusterChannels.size(), " receivers)"))) {

				for (int i = 0; i < _messagesCount; i++) {
					if (multicast) {
						_senderClusterChannel.sendMulticastMessage(message);
					}
					else {
						for (ClusterChannel receiverClusterChannel :
								_receiverClusterChannels) {

							_senderClusterChannel.sendUnicastMessage(
								message,
								receiverClusterChannel.getLocalAddress());
						}
					}
				}

				for (ClusterChannel receiverClusterChannel :
						_receiverClusterChannels) {

					TestClusterReceiver testClusterReceiver =
						(TestClusterReceiver)
							receiverClusterChannel.getClusterReceiver();

					testClusterReceiver.await();
				}
			}
		}
	}

	private static ExecutorService _executorService;
	private static int _iterations;
	private static Path _logFilePath;
	private static int _messagesCount;
	private static int _messagesLength;
	private static final List<ClusterChannel> _receiverClusterChannels =
		new ArrayList<>();
	private static ClusterChannel _senderClusterChannel;

	private static class TestClusterReceiver implements ClusterReceiver {

		@Override
		public void addressesUpdated(List<Address> addresses) {
		}

		public void await() throws Exception {
			_countDownLatch.await();
		}

		@Override
		public void coordinatorAddressUpdated(Address coordinatorAddress) {
		}

		@Override
		public List<Address> getAddresses() {
			return List.of();
		}

		@Override
		public Address getCoordinatorAddress() {
			return null;
		}

		@Override
		public void openLatch() {
		}

		@Override
		public void receive(Object message, Address srcAddress) {
			if ((_countDownLatch != null) &&
				Objects.equals(message, _expectedMessage)) {

				_countDownLatch.countDown();
			}
		}

		public void reset(Object expectedPayload) {
			_countDownLatch = new CountDownLatch(_messagesCount);
			_expectedMessage = expectedPayload;
		}

		private CountDownLatch _countDownLatch;
		private Object _expectedMessage;

	}

}
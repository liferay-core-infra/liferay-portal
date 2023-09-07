/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.scheduler.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.concurrent.DefaultNoticeableFuture;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.FrameworkWiring;

/**
 * @author Mariano Álvaro Sáiz
 */
@RunWith(Arquillian.class)
public class SchedulerEngineHelperTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(
			SchedulerEngineHelperTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	@Test
	public void testJobIsScheduledIfCronExpressionIsNotBlank()
		throws Exception {

		try (AutoCloseable autoCloseable = _registerCronJobConfiguration(
				"0 0 7 1/2 * ? *")) {

			Thread.sleep(3000);

			Assert.assertNotNull(
				_schedulerEngineHelper.getScheduledJob(
					TestSchedulerCronJobConfiguration.class.getName(),
					TestSchedulerCronJobConfiguration.class.getName(),
					StorageType.MEMORY_CLUSTERED));
		}
	}

	@Test
	public void testJobIsScheduledIfIntervalGreaterThanZero() throws Exception {
		try (AutoCloseable autoCloseable = _registerIntervalJobConfiguration(
				16, TimeUnit.MINUTE)) {

			Thread.sleep(3000);

			Assert.assertNotNull(
				_schedulerEngineHelper.getScheduledJob(
					TestSchedulerIntervalJobConfiguration.class.getName(),
					TestSchedulerIntervalJobConfiguration.class.getName(),
					StorageType.MEMORY_CLUSTERED));
		}
	}

	@Test
	public void testSchedulerGetsActivatedAfterInvalidInterval()
		throws Exception {

		_assertExpectedLogs(
			() -> {
				try (AutoCloseable autoCloseable1 =
						_registerIntervalJobConfiguration(
							-1, TimeUnit.MINUTE)) {

					_refreshPortalSchedulerBundle();

					try (AutoCloseable autoCloseable2 =
							_registerIntervalJobConfiguration(
								16, TimeUnit.MINUTE)) {

						Thread.sleep(3000);

						Assert.assertNotNull(
							_schedulerEngineHelper.getScheduledJob(
								TestSchedulerIntervalJobConfiguration.class.
									getName(),
								TestSchedulerIntervalJobConfiguration.class.
									getName(),
								StorageType.MEMORY_CLUSTERED));
					}
				}
			},
			StringBundler.concat(
				"Unable to process job. If you want to disable it please ",
				"blacklist ",
				TestSchedulerIntervalJobConfiguration.class.getName()),
			"Interval is either equal or less than 0");
	}

	@Test
	public void testSchedulerGetsActivatedAfterNullCronExpression()
		throws Exception {

		_assertExpectedLogs(
			() -> {
				try (AutoCloseable autoCloseable1 =
						_registerCronJobConfiguration(null)) {

					_refreshPortalSchedulerBundle();

					try (AutoCloseable autoCloseable2 =
							_registerCronJobConfiguration("0 0 7 1/2 * ? *")) {

						Thread.sleep(3000);

						Assert.assertNotNull(
							_schedulerEngineHelper.getScheduledJob(
								TestSchedulerCronJobConfiguration.class.
									getName(),
								TestSchedulerCronJobConfiguration.class.
									getName(),
								StorageType.MEMORY_CLUSTERED));
					}
				}
			},
			StringBundler.concat(
				"Unable to process job. If you want to disable it please ",
				"blacklist ",
				TestSchedulerCronJobConfiguration.class.getName()),
			"Cron expression is null or empty");
	}

	private void _assertExpectedLogs(
			UnsafeRunnable<Exception> runnable, String... expectedMessages)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_LOG_CLASS, LoggerTestUtil.ERROR)) {

			runnable.run();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			for (LogEntry logEntry : logEntries) {
				Assert.assertEquals(
					LoggerTestUtil.ERROR, logEntry.getPriority());

				Assert.assertTrue(
					ArrayUtil.contains(
						expectedMessages, logEntry.getMessage()));

				Throwable throwable = logEntry.getThrowable();

				Assert.assertTrue(
					ArrayUtil.contains(
						expectedMessages, throwable.getMessage()));
			}
		}
	}

	private void _refreshPortalSchedulerBundle() throws Exception {
		for (Bundle bundle : _bundleContext.getBundles()) {
			String symbolicName = bundle.getSymbolicName();

			if (symbolicName.equals("com.liferay.portal.scheduler")) {
				Bundle frameworkBundle = _bundleContext.getBundle(0);

				FrameworkWiring frameworkWiring = frameworkBundle.adapt(
					FrameworkWiring.class);

				DefaultNoticeableFuture<FrameworkEvent>
					defaultNoticeableFuture = new DefaultNoticeableFuture<>();

				frameworkWiring.refreshBundles(
					Collections.<Bundle>singletonList(bundle),
					new FrameworkListener() {

						@Override
						public void frameworkEvent(
							FrameworkEvent frameworkEvent) {

							defaultNoticeableFuture.set(frameworkEvent);
						}

					});

				defaultNoticeableFuture.get();

				return;
			}
		}

		throw new IllegalStateException(
			"Liferay Portal Scheduler is not deployed");
	}

	private AutoCloseable _registerCronJobConfiguration(String cronExpression) {
		return _registerJobConfiguration(
			new TestSchedulerCronJobConfiguration(cronExpression));
	}

	private AutoCloseable _registerIntervalJobConfiguration(
		int interval, TimeUnit timeUnit) {

		return _registerJobConfiguration(
			new TestSchedulerIntervalJobConfiguration(interval, timeUnit));
	}

	private AutoCloseable _registerJobConfiguration(
		SchedulerJobConfiguration schedulerJobConfiguration) {

		Bundle bundle = FrameworkUtil.getBundle(
			SchedulerEngineHelperTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(
				SchedulerJobConfiguration.class, schedulerJobConfiguration,
				null);

		return serviceRegistration::unregister;
	}

	private static final String _LOG_CLASS =
		"com.liferay.portal.scheduler.internal.SchedulerEngineHelperImpl";

	private static BundleContext _bundleContext;

	@Inject
	private SchedulerEngineHelper _schedulerEngineHelper;

	private static class TestSchedulerCronJobConfiguration
		implements SchedulerJobConfiguration {

		public TestSchedulerCronJobConfiguration(String cronExpression) {
			_cronExpression = cronExpression;
		}

		@Override
		public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
			return () -> {
			};
		}

		@Override
		public TriggerConfiguration getTriggerConfiguration() {
			return TriggerConfiguration.createTriggerConfiguration(
				_cronExpression);
		}

		private final String _cronExpression;

	}

	private static class TestSchedulerIntervalJobConfiguration
		implements SchedulerJobConfiguration {

		public TestSchedulerIntervalJobConfiguration(
			int interval, TimeUnit timeUnit) {

			_interval = interval;
			_timeUnit = timeUnit;
		}

		@Override
		public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
			return () -> {
			};
		}

		@Override
		public TriggerConfiguration getTriggerConfiguration() {
			return TriggerConfiguration.createTriggerConfiguration(
				_interval, _timeUnit);
		}

		private final int _interval;
		private final TimeUnit _timeUnit;

	}

}
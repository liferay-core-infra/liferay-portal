/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.view.count.service.impl.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.increment.BufferedIncrementThreadLocal;
import com.liferay.portal.kernel.messaging.proxy.ProxyModeThreadLocal;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.view.count.model.ViewCountEntry;
import com.liferay.view.count.service.ViewCountEntryLocalService;
import com.liferay.view.count.service.persistence.ViewCountEntryFinder;
import com.liferay.view.count.service.persistence.ViewCountEntryPK;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.FutureTask;

import org.hibernate.engine.jdbc.batch.internal.BatchingBatch;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class ViewCountEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		_className = _classNameLocalService.getClassName(
			ViewCountEntryLocalServiceTest.class.getName());

		_db = DBManagerUtil.getDB();
	}

	@Test
	public void testCreationWithHoldLock() throws Throwable {
		Assume.assumeTrue(_db.getDBType() == DBType.SQLSERVER);

		ViewCountEntryPK viewCountEntryPK = new ViewCountEntryPK(
			TestPropsValues.getCompanyId(), _className.getClassNameId(),
			_CLASS_PK);

		SessionFactory sessionFactory = ReflectionTestUtil.getFieldValue(
			_viewCountEntryFinder, "_sessionFactory");

		CyclicBarrier cyclicBarrier = new CyclicBarrier(2);

		ReflectionTestUtil.setFieldValue(
			_viewCountEntryFinder, "_sessionFactory",
			_createSessionFactoryProxy(sessionFactory, cyclicBarrier));

		try (LogCapture logCapture1 = LoggerTestUtil.configureLog4JLogger(
				SqlExceptionHelper.class.getName(), LoggerTestUtil.OFF);
			LogCapture logCapture2 = LoggerTestUtil.configureLog4JLogger(
				BatchingBatch.class.getName(), LoggerTestUtil.OFF)) {

			FutureTask<Void> futureTask = new FutureTask<>(
				() -> {
					try (SafeCloseable safeCloseable1 =
							BufferedIncrementThreadLocal.setWithSafeCloseable(
								true);
						SafeCloseable safeCloseable2 =
							ProxyModeThreadLocal.setWithSafeCloseable(true)) {

						_viewCountEntryLocalService.incrementViewCount(
							TestPropsValues.getCompanyId(),
							_className.getClassNameId(), _CLASS_PK,
							_VIEW_COUNT);
					}

					return null;
				});

			Thread thread = new Thread(futureTask, _THREAD_NAME);

			thread.start();

			_viewCountEntryLocalService.incrementViewCount(
				TestPropsValues.getCompanyId(), _className.getClassNameId(),
				_CLASS_PK, _VIEW_COUNT);

			futureTask.get();
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_viewCountEntryFinder, "_sessionFactory", sessionFactory);
		}

		Assert.assertTrue(_viewCountEntries.size() == 2);
		Assert.assertNull(_viewCountEntries.get(0));
		Assert.assertNotNull(_viewCountEntries.get(1));

		_viewCountEntry = _viewCountEntryLocalService.getViewCountEntry(
			viewCountEntryPK);

		Assert.assertEquals(_VIEW_COUNT * 2, _viewCountEntry.getViewCount());
	}

	@Test
	public void testLazyCreationWithRaceCondition() throws Throwable {
		Assume.assumeFalse(
			"HSQL does not allow concurrent Session assess, skip test.",
			_db.getDBType() == DBType.HYPERSONIC);

		Assume.assumeFalse(
			"Due to HHH-10654 changed, the test is not suitable for " +
				"SQLSERVER database, skip test.",
			_db.getDBType() == DBType.SQLSERVER);

		ViewCountEntryPK viewCountEntryPK = new ViewCountEntryPK(
			TestPropsValues.getCompanyId(), _className.getClassNameId(),
			_CLASS_PK);

		SessionFactory sessionFactory = ReflectionTestUtil.getFieldValue(
			_viewCountEntryFinder, "_sessionFactory");

		CyclicBarrier cyclicBarrier = new CyclicBarrier(2);

		ReflectionTestUtil.setFieldValue(
			_viewCountEntryFinder, "_sessionFactory",
			_createSessionFactoryProxy(sessionFactory, cyclicBarrier));

		try (LogCapture logCapture1 = LoggerTestUtil.configureLog4JLogger(
				SqlExceptionHelper.class.getName(), LoggerTestUtil.OFF);
			LogCapture logCapture2 = LoggerTestUtil.configureLog4JLogger(
				BatchingBatch.class.getName(), LoggerTestUtil.OFF)) {

			FutureTask<Void> futureTask = new FutureTask<>(
				() -> {
					try (SafeCloseable safeCloseable1 =
							BufferedIncrementThreadLocal.setWithSafeCloseable(
								true);
						SafeCloseable safeCloseable2 =
							ProxyModeThreadLocal.setWithSafeCloseable(true)) {

						_viewCountEntryLocalService.incrementViewCount(
							TestPropsValues.getCompanyId(),
							_className.getClassNameId(), _CLASS_PK,
							_VIEW_COUNT);
					}

					return null;
				});

			Thread thread = new Thread(futureTask, _THREAD_NAME);

			thread.start();

			_viewCountEntryLocalService.incrementViewCount(
				TestPropsValues.getCompanyId(), _className.getClassNameId(),
				_CLASS_PK, _VIEW_COUNT);

			futureTask.get();
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_viewCountEntryFinder, "_sessionFactory", sessionFactory);
		}

		Assert.assertTrue(_viewCountEntries.size() > 2);
		Assert.assertNull(_viewCountEntries.get(0));
		Assert.assertNull(_viewCountEntries.get(1));

		_viewCountEntry = _viewCountEntryLocalService.getViewCountEntry(
			viewCountEntryPK);

		Assert.assertEquals(_VIEW_COUNT * 2, _viewCountEntry.getViewCount());
	}

	private Object _createSessionFactoryProxy(
		SessionFactory sessionFactory, CyclicBarrier cyclicBarrier) {

		return ProxyUtil.newProxyInstance(
			SessionFactory.class.getClassLoader(),
			new Class<?>[] {SessionFactory.class},
			(proxy, method, args) -> {
				if (Objects.equals("openSession", method.getName())) {
					return _createSessionProxy(
						sessionFactory.openSession(), cyclicBarrier);
				}

				return method.invoke(sessionFactory, args);
			});
	}

	private Object _createSessionProxy(
		Session session, CyclicBarrier cyclicBarrier) {

		return ProxyUtil.newProxyInstance(
			Session.class.getClassLoader(), new Class<?>[] {Session.class},
			(proxy, method, args) -> {
				if (Objects.equals("flush", method.getName()) &&
					(_db.getDBType() != DBType.SQLSERVER)) {

					cyclicBarrier.await();
				}

				if (Objects.equals("get", method.getName())) {
					if (_db.getDBType() == DBType.SQLSERVER) {
						cyclicBarrier.await();
					}

					ViewCountEntry viewCountEntry =
						(ViewCountEntry)method.invoke(session, args);

					_viewCountEntries.add(viewCountEntry);

					return viewCountEntry;
				}

				try {
					return method.invoke(session, args);
				}
				catch (InvocationTargetException invocationTargetException) {
					throw invocationTargetException.getCause();
				}
			});
	}

	private static final long _CLASS_PK = 0;

	private static final String _THREAD_NAME = "Inner View Count Incrementer";

	private static final int _VIEW_COUNT = 100;

	@DeleteAfterTestRun
	private static ClassName _className;

	@Inject
	private static ClassNameLocalService _classNameLocalService;

	@Inject
	private static ViewCountEntryFinder _viewCountEntryFinder;

	@Inject
	private static ViewCountEntryLocalService _viewCountEntryLocalService;

	private DB _db;
	private final List<ViewCountEntry> _viewCountEntries = new ArrayList<>();

	@DeleteAfterTestRun
	private ViewCountEntry _viewCountEntry;

}
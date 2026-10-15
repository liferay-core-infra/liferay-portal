/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.background.task.service.BackgroundTaskService;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.HashMap;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Debora Buriti
 */
@RunWith(Arquillian.class)
public class BackgroundTaskServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_group.setType(GroupConstants.TYPE_SITE_PRIVATE);

		_group = _groupLocalService.updateGroup(_group);

		_user = UserTestUtil.addUser();
	}

	@Test
	public void testGetBackgroundTasksCount() throws Exception {
		String taskExecutorClassName = _addBackgroundTask();

		Assert.assertEquals(
			1,
			_backgroundTaskService.getBackgroundTasksCount(
				_group.getGroupId(), taskExecutorClassName, true));

		UserTestUtil.setUser(_user);

		Assert.assertThrows(
			PrincipalException.class,
			() -> _backgroundTaskService.getBackgroundTasksCount(
				_group.getGroupId(), taskExecutorClassName, true));
	}

	private String _addBackgroundTask() throws Exception {
		BackgroundTaskExecutor backgroundTaskExecutor =
			(BackgroundTaskExecutor)ProxyUtil.newProxyInstance(
				BackgroundTaskExecutor.class.getClassLoader(),
				new Class<?>[] {BackgroundTaskExecutor.class},
				(proxy, method, arguments) -> {
					String methodName = method.getName();

					if (Objects.equals(methodName, "clone")) {
						return proxy;
					}
					else if (Objects.equals(methodName, "execute")) {
						return new BackgroundTaskResult(
							BackgroundTaskConstants.STATUS_SUCCESSFUL);
					}
					else if (Objects.equals(methodName, "getIsolationLevel")) {
						return BackgroundTaskConstants.
							ISOLATION_LEVEL_NOT_ISOLATED;
					}
					else if (Objects.equals(methodName, "isSerial")) {
						return false;
					}

					return null;
				});

		Class<?> backgroundTaskExecutorClass =
			backgroundTaskExecutor.getClass();

		Bundle bundle = FrameworkUtil.getBundle(
			BackgroundTaskServiceTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<BackgroundTaskExecutor> serviceRegistration =
			bundleContext.registerService(
				BackgroundTaskExecutor.class, backgroundTaskExecutor,
				HashMapDictionaryBuilder.<String, Object>put(
					"background.task.executor.class.name",
					backgroundTaskExecutorClass.getName()
				).build());

		try {
			_backgroundTaskManager.addBackgroundTask(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				backgroundTaskExecutorClass.getName(), new HashMap<>(),
				new ServiceContext());
		}
		finally {
			serviceRegistration.unregister();
		}

		return backgroundTaskExecutorClass.getName();
	}

	@Inject
	private BackgroundTaskManager _backgroundTaskManager;

	@Inject
	private BackgroundTaskService _backgroundTaskService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private User _user;

}
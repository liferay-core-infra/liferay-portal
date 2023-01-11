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

package com.liferay.portal.upgrade.internal.executor;

import com.liferay.portal.dao.db.MySQLDB;
import com.liferay.portal.kernel.log.LogContext;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsTestUtil;
import com.liferay.portal.kernel.upgrade.BaseAdminPortletsUpgradeProcess;
import com.liferay.portal.kernel.upgrade.BasePortletIdUpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.tools.DBUpgrader;
import com.liferay.portal.upgrade.internal.registry.UpgradeStepRegistratorTracker;
import com.liferay.portal.upgrade.internal.release.ReleaseManagerImpl;
import com.liferay.portal.verify.VerifyProperties;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Tina Tian
 */
public class UpgradeExecutorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testUpgradeLogContextWhenDisabled() {
		UpgradeExecutor upgradeExecutor = new UpgradeExecutor();

		ReflectionTestUtil.setFieldValue(
			upgradeExecutor, "_props",
			PropsTestUtil.setProps(
				PropsKeys.UPGRADE_LOG_CONTEXT_ENABLED, "false"));

		upgradeExecutor.activate(SystemBundleUtil.getBundleContext());

		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				upgradeExecutor, "_serviceRegistration"));
	}

	@Test
	public void testUpgradeLogContextWhenEnabled() {
		UpgradeExecutor upgradeExecutor = new UpgradeExecutor();

		ReflectionTestUtil.setFieldValue(
			upgradeExecutor, "_props",
			PropsTestUtil.setProps(
				PropsKeys.UPGRADE_LOG_CONTEXT_ENABLED, "true"));

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		upgradeExecutor.activate(bundleContext);

		ServiceRegistration<LogContext> serviceRegistration =
			ReflectionTestUtil.getFieldValue(
				upgradeExecutor, "_serviceRegistration");

		Assert.assertNotNull(serviceRegistration);

		LogContext logContext = bundleContext.getService(
			serviceRegistration.getReference());

		Map<String, String> context = ReflectionTestUtil.getFieldValue(
			logContext, "_context");

		Assert.assertFalse(context.isEmpty());

		Assert.assertSame(
			context, logContext.getContext(DBUpgrader.class.getName()));
		Assert.assertSame(
			context, logContext.getContext(LoggingTimer.class.getName()));
		Assert.assertSame(
			context, logContext.getContext(ReleaseManagerImpl.class.getName()));
		Assert.assertSame(
			context,
			logContext.getContext(
				UpgradeStepRegistratorTracker.class.getName()));
		Assert.assertSame(
			context, logContext.getContext(VerifyProperties.class.getName()));
		Assert.assertSame(
			context, logContext.getContext(MySQLDB.class.getName()));
		Assert.assertSame(
			context,
			logContext.getContext(
				BaseAdminPortletsUpgradeProcess.class.getName()));
		Assert.assertSame(
			context,
			logContext.getContext(BasePortletIdUpgradeProcess.class.getName()));

		Assert.assertSame(
			Collections.emptyMap(),
			logContext.getContext(UpgradeExecutorTest.class.getName()));
	}

}
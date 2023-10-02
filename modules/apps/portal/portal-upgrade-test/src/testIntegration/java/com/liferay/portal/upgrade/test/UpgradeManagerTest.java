/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.ReleaseManager;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsUtil;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.util.promise.Promise;

/**
 * @author Alberto Chaparro
 */
@RunWith(Arquillian.class)
public class UpgradeManagerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		_originalUpgradeDatabaseAutoRun = PropsUtil.get(
			"upgrade.database.auto.run");
	}

	@AfterClass
	public static void tearDownClass() {
		PropsUtil.set(
			"upgrade.database.auto.run", _originalUpgradeDatabaseAutoRun);
	}

	@After
	public void tearDown() throws Exception {
		Promise<?> promise = _serviceComponentRuntime.disableComponent(
			_serviceComponentRuntime.getComponentDescriptionDTO(
				_getBundle(),
				"com.liferay.portal.upgrade.internal.jmx.UpgradeManager"));

		promise.getValue();

		_upgradeManager = null;
	}

	@Test
	public void testUpgradeManager() throws Exception {
		_restartComponentEnabler(true);

		Class<?> clazz = _getBundle().loadClass(
			"com.liferay.portal.upgrade.internal.recorder.UpgradeRecorderUtil");

		String originalResult = ReflectionTestUtil.getFieldValue(
			clazz, "_result");
		String originalType = ReflectionTestUtil.getFieldValue(clazz, "_type");

		try {
			Assert.assertEquals(
				originalResult, _upgradeManagerInvoke("getResult"));
			Assert.assertEquals(originalType, _upgradeManagerInvoke("getType"));

			ReflectionTestUtil.setFieldValue(clazz, "_result", "testResult");
			ReflectionTestUtil.setFieldValue(clazz, "_type", "testType");

			Assert.assertEquals(
				"testResult", _upgradeManagerInvoke("getResult"));
			Assert.assertEquals("testType", _upgradeManagerInvoke("getType"));
		}
		finally {
			ReflectionTestUtil.setFieldValue(clazz, "_result", originalResult);
			ReflectionTestUtil.setFieldValue(clazz, "_type", originalType);
		}
	}

	@Test
	public void testUpgradeManagerMBeanDisabled() throws Exception {
		_restartComponentEnabler(false);

		Assert.assertFalse(_isUpgradeManagerMBeanRegistered());
	}

	@Test
	public void testUpgradeManagerMBeanEnabled() throws Exception {
		_restartComponentEnabler(true);

		Assert.assertTrue(_isUpgradeManagerMBeanRegistered());
	}

	private Bundle _getBundle() {
		return FrameworkUtil.getBundle(_releaseManager.getClass());
	}

	private boolean _isUpgradeManagerMBeanRegistered() throws Exception {
		ObjectName objectName = new ObjectName(
			"com.liferay.portal.upgrade:classification=upgrade," +
				"name=UpgradeManager");

		for (int i = 0; i < 10; i++) {
			Thread.sleep(100);

			MBeanServer mBeanServer =
				ManagementFactory.getPlatformMBeanServer();

			if (mBeanServer.isRegistered(objectName)) {
				return true;
			}
		}

		return false;
	}

	private void _restartComponentEnabler(boolean upgradeDatabaseAutoRun)
		throws Exception {

		Promise<?> promise = _serviceComponentRuntime.disableComponent(
			_serviceComponentRuntime.getComponentDescriptionDTO(
				_getBundle(),
				"com.liferay.portal.upgrade.internal.component.enabler." +
					"ComponentEnabler"));

		promise.getValue();

		PropsUtil.set(
			"upgrade.database.auto.run",
			String.valueOf(upgradeDatabaseAutoRun));

		promise = _serviceComponentRuntime.enableComponent(
			_serviceComponentRuntime.getComponentDescriptionDTO(
				FrameworkUtil.getBundle(_releaseManager.getClass()),
				"com.liferay.portal.upgrade.internal.component.enabler." +
					"ComponentEnabler"));

		promise.getValue();
	}

	private String _upgradeManagerInvoke(String methodName) throws Exception {
		if (_upgradeManager == null) {
			BundleContext bundleContext = _getBundle().getBundleContext();

			ServiceReference<?>[] serviceReferences =
				bundleContext.getServiceReferences(
					"javax.management.DynamicMBean",
					"(component.name=com.liferay.portal.upgrade.internal.jmx." +
						"UpgradeManager)");

			_upgradeManager = bundleContext.getService(serviceReferences[0]);
		}

		return ReflectionTestUtil.invoke(
			_upgradeManager, methodName, new Class<?>[0], null);
	}

	private static String _originalUpgradeDatabaseAutoRun;
	private static Object _upgradeManager;

	@Inject
	private ReleaseManager _releaseManager;

	@Inject
	private ServiceComponentRuntime _serviceComponentRuntime;

}
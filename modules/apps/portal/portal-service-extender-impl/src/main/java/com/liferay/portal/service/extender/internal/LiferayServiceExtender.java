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

package com.liferay.portal.service.extender.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceComponentLocalService;
import com.liferay.portal.module.util.BundleUtil;
import com.liferay.portal.service.extender.internal.configuration.PortletConfigurationExtension;
import com.liferay.portal.service.extender.internal.configuration.ServiceConfigurationExtension;
import com.liferay.portal.service.extender.internal.upgrade.InitialUpgradeExtension;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.concurrent.FutureTask;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * @author Preston Crary
 */
@Component(service = {})
public class LiferayServiceExtender
	implements BundleTrackerCustomizer<List<LiferayServiceExtension>> {

	@Override
	public List<LiferayServiceExtension> addingBundle(
		Bundle bundle, BundleEvent bundleEvent) {

		if (!BundleUtil.isLiferayServiceBundle(bundle)) {
			return null;
		}

		List<LiferayServiceExtension> liferayServiceExtensions =
			new ArrayList<>();

		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		ClassLoader classLoader = bundleWiring.getClassLoader();

		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		try {
			if (headers.get("Liferay-Spring-Context") == null) {
				LiferayServiceExtension defaultServiceExtension =
					new DefaultServiceExtension(bundle, classLoader);

				liferayServiceExtensions.add(defaultServiceExtension);

				defaultServiceExtension.start();
			}

			InitialUpgradeExtension initialUpgradeExtension =
				new InitialUpgradeExtension(bundle, classLoader);

			liferayServiceExtensions.add(initialUpgradeExtension);

			initialUpgradeExtension.start();

			Configuration portletConfiguration =
				ConfigurationFactoryUtil.getConfiguration(
					classLoader, "portlet");

			if (portletConfiguration != null) {
				PortletConfigurationExtension portletConfigurationExtension =
					new PortletConfigurationExtension(
						bundle, classLoader, portletConfiguration);

				liferayServiceExtensions.add(portletConfigurationExtension);

				portletConfigurationExtension.start();
			}

			Configuration serviceConfiguration =
				ConfigurationFactoryUtil.getConfiguration(
					classLoader, "service");

			if (serviceConfiguration != null) {
				ServiceConfigurationExtension serviceConfigurationExtension =
					new ServiceConfigurationExtension(
						bundle, classLoader, serviceConfiguration,
						_serviceComponentLocalService);

				liferayServiceExtensions.add(serviceConfigurationExtension);

				serviceConfigurationExtension.start();
			}

			return liferayServiceExtensions;
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return null;
	}

	@Override
	public void modifiedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		List<LiferayServiceExtension> liferayServiceExtensions) {
	}

	@Override
	public void removedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		List<LiferayServiceExtension> liferayServiceExtensions) {

		for (LiferayServiceExtension liferayPortalServiceExtension :
				liferayServiceExtensions) {

			liferayPortalServiceExtension.destroy();
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleTracker = new BundleTracker<>(
			bundleContext, Bundle.ACTIVE | Bundle.STARTING, this);

		FutureTask<Void> futureTask = new FutureTask<>(
			() -> {
				_bundleTracker.open();

				return null;
			});

		Thread bundleTrackerOpenerThread = new Thread(
			futureTask,
			LiferayServiceExtender.class.getName() + "-BundleTrackerOpener");

		bundleTrackerOpenerThread.setDaemon(true);

		bundleTrackerOpenerThread.start();

		DependencyManagerSyncUtil.registerSyncFuture(futureTask);
	}

	@Deactivate
	protected void deactivate() {
		_bundleTracker.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayServiceExtender.class);

	private BundleTracker<?> _bundleTracker;

	@Reference
	private ServiceComponentLocalService _serviceComponentLocalService;

}
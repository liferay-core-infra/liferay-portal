/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.startup.service.component.actor.sync.internal;

import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tina Tian
 */
@Component(service = {})
public class ServiceComponentActorSync {

	@Activate
	protected void activate(ComponentContext componentContext) {
		BundleContext bundleContext = componentContext.getBundleContext();

		DependencyManagerSyncUtil.registerSyncCallable(
			() -> {
				String componentName =
					ServiceComponentActorGateKeeper.class.getName();

				CountDownLatch countDownLatch = new CountDownLatch(1);

				ServiceListener serviceListener = serviceEvent -> {
					if (serviceEvent.getType() == ServiceEvent.REGISTERED) {
						ServiceReference<?> serviceReference =
							serviceEvent.getServiceReference();

						if (Objects.equals(
								componentName,
								serviceReference.getProperty(
									"component.name"))) {

							countDownLatch.countDown();
						}
					}
				};

				try {
					bundleContext.addServiceListener(serviceListener);

					componentContext.enableComponent(componentName);

					if (!countDownLatch.await(
							GetterUtil.getInteger(
								_props.get(
									PropsKeys.DEPENDENCY_MANAGER_SYNC_TIMEOUT),
								60),
							TimeUnit.SECONDS)) {

						_log.error("Unable to sync SCR component actor thread");
					}
				}
				finally {
					componentContext.disableComponent(componentName);

					componentContext.disableComponent(
						ServiceComponentActorSync.class.getName());

					bundleContext.removeServiceListener(serviceListener);
				}

				return null;
			});
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ServiceComponentActorSync.class);

	@Reference
	private Props _props;

}
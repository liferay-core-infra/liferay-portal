/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.registration.internal;

import com.liferay.bean.portlet.registration.BeanPortletRegistrar;
import com.liferay.bean.portlet.registration.BeanPortletRegistrarBag;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Jiaxu Wei
 */
@Component(service = {})
public class BeanPortletRegistrarBagRegistry {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = new ServiceTracker<>(
			bundleContext, BeanPortletRegistrarBag.class,
			new ServiceTrackerCustomizer
				<BeanPortletRegistrarBag, BeanPortletRegistrarBag>() {

				@Override
				public BeanPortletRegistrarBag addingService(
					ServiceReference<BeanPortletRegistrarBag>
						serviceReference) {

					BeanPortletRegistrarBag beanPortletRegistrarBag =
						bundleContext.getService(serviceReference);

					_serviceRegistrations.put(
						beanPortletRegistrarBag,
						_beanPortletRegistrar.register(
							beanPortletRegistrarBag.
								getBeanFilterMethodFactory(),
							beanPortletRegistrarBag.
								getBeanFilterMethodInvoker(),
							beanPortletRegistrarBag.
								getBeanPortletMethodFactory(),
							beanPortletRegistrarBag.
								getBeanPortletMethodInvoker(),
							beanPortletRegistrarBag.getDiscoveredClasses(),
							beanPortletRegistrarBag.getServletContext()));

					return beanPortletRegistrarBag;
				}

				@Override
				public void modifiedService(
					ServiceReference<BeanPortletRegistrarBag> serviceReference,
					BeanPortletRegistrarBag beanPortletRegistrarBag) {
				}

				@Override
				public void removedService(
					ServiceReference<BeanPortletRegistrarBag> serviceReference,
					BeanPortletRegistrarBag beanPortletRegistrarBag) {

					bundleContext.ungetService(serviceReference);

					_beanPortletRegistrar.unregister(
						_serviceRegistrations.remove(beanPortletRegistrarBag),
						beanPortletRegistrarBag.getServletContext());
				}

			});

		_serviceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();

		for (List<ServiceRegistration<?>> serviceRegistrations :
				_serviceRegistrations.values()) {

			for (ServiceRegistration<?> serviceRegistration :
					serviceRegistrations) {

				try {
					serviceRegistration.unregister();
				}
				catch (IllegalStateException illegalStateException) {
					_log.error(illegalStateException);
				}
			}
		}

		_serviceRegistrations.clear();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BeanPortletRegistrarBagRegistry.class);

	@Reference
	private BeanPortletRegistrar _beanPortletRegistrar;

	private final Map<BeanPortletRegistrarBag, List<ServiceRegistration<?>>>
		_serviceRegistrations = new ConcurrentHashMap<>();
	private ServiceTracker<BeanPortletRegistrarBag, BeanPortletRegistrarBag>
		_serviceTracker;

}
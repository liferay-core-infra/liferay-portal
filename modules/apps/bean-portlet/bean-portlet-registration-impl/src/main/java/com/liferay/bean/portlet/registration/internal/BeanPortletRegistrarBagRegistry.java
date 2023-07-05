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

package com.liferay.bean.portlet.registration.internal;

import com.liferay.bean.portlet.registration.BeanPortletRegistrar;
import com.liferay.bean.portlet.registration.BeanPortletRegistrarBag;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
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

					beanPortletRegistrarBag.addServiceRegistrations(
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
						beanPortletRegistrarBag.getServiceRegistrations(),
						beanPortletRegistrarBag.getServletContext());
				}

			});

		_serviceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	@Reference
	private BeanPortletRegistrar _beanPortletRegistrar;

	private ServiceTracker<BeanPortletRegistrarBag, BeanPortletRegistrarBag>
		_serviceTracker;

}
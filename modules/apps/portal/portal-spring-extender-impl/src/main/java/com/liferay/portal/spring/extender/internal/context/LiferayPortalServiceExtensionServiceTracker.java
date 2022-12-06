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

package com.liferay.portal.spring.extender.internal.context;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.spring.configurator.ConfigurableApplicationContextConfigurator;
import com.liferay.portal.spring.extender.internal.LiferayPortalServiceExtension;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Hai Yu
 */
@Component(service = {})
public class LiferayPortalServiceExtensionServiceTracker
	implements ServiceTrackerCustomizer
		<LiferayPortalServiceExtension, LiferayPortalServiceExtension> {

	@Override
	public LiferayPortalServiceExtension addingService(
		ServiceReference<LiferayPortalServiceExtension> serviceReference) {

		try {
			ModuleApplicationContextExtension
				moduleApplicationContextExtension =
					new ModuleApplicationContextExtension(
						serviceReference.getBundle(), _bundleContext,
						_configurableApplicationContextConfigurator);

			moduleApplicationContextExtension.start();

			return moduleApplicationContextExtension;
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return null;
	}

	@Override
	public void modifiedService(
		ServiceReference<LiferayPortalServiceExtension> serviceReference,
		LiferayPortalServiceExtension liferayPortalServiceExtension) {
	}

	@Override
	public void removedService(
		ServiceReference<LiferayPortalServiceExtension> serviceReference,
		LiferayPortalServiceExtension liferayPortalServiceExtension) {

		liferayPortalServiceExtension.destroy();

		_bundleContext.ungetService(serviceReference);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_serviceTracker = new ServiceTracker<>(
			_bundleContext, LiferayPortalServiceExtension.class, this);

		_serviceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayPortalServiceExtensionServiceTracker.class);

	private BundleContext _bundleContext;

	@Reference
	private ConfigurableApplicationContextConfigurator
		_configurableApplicationContextConfigurator;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	private ServiceTracker
		<LiferayPortalServiceExtension, LiferayPortalServiceExtension>
			_serviceTracker;

}
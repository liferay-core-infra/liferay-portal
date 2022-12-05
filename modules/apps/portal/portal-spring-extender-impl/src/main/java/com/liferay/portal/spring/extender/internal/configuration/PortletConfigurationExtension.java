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

package com.liferay.portal.spring.extender.internal.configuration;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.spring.extender.internal.LiferayPortalServiceExtension;
import com.liferay.portal.util.PropsValues;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Tina Tian
 */
public class PortletConfigurationExtension
	implements LiferayPortalServiceExtension {

	public PortletConfigurationExtension(
		Bundle bundle, ClassLoader classLoader,
		Configuration portletConfiguration) {

		_bundle = bundle;
		_classLoader = classLoader;
		_portletConfiguration = portletConfiguration;
	}

	@Override
	public void destroy() {
		if (_configurationServiceRegistration != null) {
			_configurationServiceRegistration.unregister();

			_configurationServiceRegistration = null;
		}
	}

	@Override
	public void start() {
		try {
			ResourceActionsUtil.populateModelResources(
				_classLoader,
				StringUtil.split(
					_portletConfiguration.get(
						PropsKeys.RESOURCE_ACTIONS_CONFIGS)));

			if (!PropsValues.RESOURCE_ACTIONS_STRICT_MODE_ENABLED) {
				ResourceActionsUtil.populatePortletResources(
					_classLoader,
					StringUtil.split(
						_portletConfiguration.get(
							PropsKeys.RESOURCE_ACTIONS_CONFIGS)));
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to read resource actions config in " +
					PropsKeys.RESOURCE_ACTIONS_CONFIGS,
				exception);
		}

		BundleContext bundleContext = _bundle.getBundleContext();

		_configurationServiceRegistration = bundleContext.registerService(
			Configuration.class, _portletConfiguration,
			HashMapDictionaryBuilder.<String, Object>put(
				"name", "portlet"
			).put(
				"origin.bundle.symbolic.name", _bundle.getSymbolicName()
			).build());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletConfigurationExtension.class);

	private final Bundle _bundle;
	private final ClassLoader _classLoader;
	private ServiceRegistration<Configuration>
		_configurationServiceRegistration;
	private final Configuration _portletConfiguration;

}
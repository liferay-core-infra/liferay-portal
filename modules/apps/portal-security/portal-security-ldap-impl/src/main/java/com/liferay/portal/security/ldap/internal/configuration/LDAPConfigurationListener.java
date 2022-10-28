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

package com.liferay.portal.security.ldap.internal.configuration;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.ldap.configuration.ConfigurationProvider;

import java.io.IOException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Michael C. Han
 */
@Component(immediate = true, service = ConfigurationListener.class)
public class LDAPConfigurationListener implements ConfigurationListener {

	@Override
	public void configurationEvent(ConfigurationEvent configurationEvent) {
		String factoryPid = configurationEvent.getFactoryPid();

		if (Validator.isNull(factoryPid)) {
			return;
		}

		if (factoryPid.endsWith(".scoped")) {
			factoryPid = StringUtil.replaceLast(
				factoryPid, ".scoped", StringPool.BLANK);
		}

		if (!_serviceTrackerMap.containsKey(factoryPid)) {
			return;
		}

		ConfigurationProvider<?> configurationProvider =
			_serviceTrackerMap.getService(factoryPid);

		try {
			if (configurationEvent.getType() == ConfigurationEvent.CM_DELETED) {
				configurationProvider.unregisterConfiguration(
					configurationEvent.getPid());
			}
			else {
				configurationProvider.registerConfiguration(
					_configurationAdmin.getConfiguration(
						configurationEvent.getPid(), StringPool.QUESTION));
			}
		}
		catch (IOException ioException) {
			throw new SystemException(
				"Unable to load configuration " + configurationEvent.getPid(),
				ioException);
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext,
			(Class<ConfigurationProvider<?>>)
				(Class<?>)ConfigurationProvider.class,
			null,
			(serviceReference, emitter) -> {
				String factoryPid = serviceReference.getProperty("factoryPid");

				if (Validator.isNull(factoryPid)) {
					throw new IllegalArgumentException(
						"No factory PID specified for configuration provider " +
							configurationProvider);
				}

				emitter.emit(factoryPid);
			},
			new LDAPConfigurationListenerServiceTrackerCustomizer(
				bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	@Reference(unbind = "-")
	protected void setConfigurationAdmin(
		ConfigurationAdmin configurationAdmin) {

		_configurationAdmin = configurationAdmin;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LDAPConfigurationListener.class);

	private ConfigurationAdmin _configurationAdmin;
	private volatile ServiceTrackerMap<String, ConfigurationProvider<?>>
		_serviceTrackerMap;

	private class LDAPConfigurationListenerServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<ConfigurationProvider<?>, ConfigurationProvider<?>> {

		public LDAPConfigurationListenerServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		@Override
		public ConfigurationProvider<?> addingService(
			ServiceReference<ConfigurationProvider<?>> serviceReference) {

			ConfigurationProvider<?> configurationProvider =
				_bundleContext.getService(serviceReference);

			String factoryPid = (String)serviceReference.getProperty(
				"factoryPid");

			try {
				Configuration[] configurations =
					_configurationAdmin.listConfigurations(
						"(service.factoryPid=" + factoryPid + "*)");

				if (configurations != null) {
					for (Configuration configuration : configurations) {
						configurationProvider.registerConfiguration(
							configuration);
					}
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to register configurations", exception);
				}
			}

			return configurationProvider;
		}

		@Override
		public void modifiedService(
			ServiceReference<ConfigurationProvider<?>> serviceReference,
			ConfigurationProvider<?> service) {
		}

		@Override
		public void removedService(
			ServiceReference<ConfigurationProvider<?>> serviceReference,
			ConfigurationProvider<?> service) {

			_bundleContext.ungetService(serviceReference);
		}

		private final BundleContext _bundleContext;

	}

}
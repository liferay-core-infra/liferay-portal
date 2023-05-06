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

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapListener;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.ldap.configuration.ConfigurationProvider;

import java.io.IOException;

import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = ConfigurationListener.class)
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

		ConfigurationProvider<?> configurationProvider =
			_serviceTrackerMap.getService(factoryPid);

		if (configurationProvider == null) {
			return;
		}

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
			"factoryPid",
			new ServiceTrackerMapListener
				<String, ConfigurationProvider<?>, ConfigurationProvider<?>>() {

				@Override
				public void keyEmitted(
					ServiceTrackerMap<String, ConfigurationProvider<?>>
						serviceTrackerMap,
					String key,
					ConfigurationProvider<?> serviceConfigurationProvider,
					ConfigurationProvider<?> contentConfigurationProvider) {

					if (Validator.isNull(key)) {
						throw new IllegalArgumentException(
							"No factory PID specified for configuration " +
								"provider " + serviceConfigurationProvider);
					}

					try {
						Configuration[] configurations =
							_configurationAdmin.listConfigurations(
								"(service.factoryPid=" + key + "*)");

						if (configurations != null) {
							for (Configuration configuration : configurations) {
								serviceConfigurationProvider.
									registerConfiguration(configuration);
							}
						}
					}
					catch (Exception exception) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								"Unable to register configurations", exception);
						}
					}
				}

				@Override
				public void keyRemoved(
					ServiceTrackerMap<String, ConfigurationProvider<?>>
						serviceTrackerMap,
					String key,
					ConfigurationProvider<?> serviceConfigurationProvider,
					ConfigurationProvider<?> contentConfigurationProvider) {
				}

			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LDAPConfigurationListener.class);

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	private ServiceTrackerMap<String, ConfigurationProvider<?>>
		_serviceTrackerMap;

}
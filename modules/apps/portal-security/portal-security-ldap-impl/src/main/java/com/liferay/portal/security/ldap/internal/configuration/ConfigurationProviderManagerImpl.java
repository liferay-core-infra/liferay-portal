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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.security.ldap.configuration.BaseConfigurationProvider;
import com.liferay.portal.security.ldap.configuration.ConfigurationProvider;
import com.liferay.portal.security.ldap.configuration.ConfigurationProviderManager;
import com.liferay.portal.security.ldap.internal.authenticator.configuration.LDAPAuthConfigurationProviderImpl;
import com.liferay.portal.security.ldap.internal.exportimport.configuration.LDAPExportConfigurationProviderImpl;
import com.liferay.portal.security.ldap.internal.exportimport.configuration.LDAPImportConfigurationProviderImpl;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dante Wang
 */
@Component(service = ConfigurationProviderManager.class)
public class ConfigurationProviderManagerImpl
	implements ConfigurationProviderManager {

	@Override
	public ConfigurationProvider<?> getConfigurationProvider(
		String factoryPid) {

		return _configurationProviders.get(factoryPid);
	}

	@Activate
	protected void activate() {
		_register(new LDAPServerConfigurationProviderImpl());
		_register(new SystemLDAPConfigurationProviderImpl());
		_register(new LDAPExportConfigurationProviderImpl());
		_register(new LDAPImportConfigurationProviderImpl());
		_register(new LDAPAuthConfigurationProviderImpl());
	}

	private void _register(BaseConfigurationProvider<?> configurationProvider) {
		configurationProvider.setConfigurationAdmin(_configurationAdmin);

		Class<?> metaTypeClass = configurationProvider.getMetatype();

		_configurationProviders.put(
			metaTypeClass.getName(), configurationProvider);

		try {
			Configuration[] configurations =
				_configurationAdmin.listConfigurations(
					"(service.factoryPid=" + metaTypeClass.getName() + "*)");

			if (configurations != null) {
				for (Configuration configuration : configurations) {
					configurationProvider.registerConfiguration(configuration);
				}
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to register configurations", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationProviderManagerImpl.class);

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	private final Map<String, ConfigurationProvider<?>>
		_configurationProviders = new HashMap<>();

}
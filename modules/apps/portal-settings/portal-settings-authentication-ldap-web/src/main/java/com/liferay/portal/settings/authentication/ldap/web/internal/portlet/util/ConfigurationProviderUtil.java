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

package com.liferay.portal.settings.authentication.ldap.web.internal.portlet.util;

import com.liferay.osgi.util.service.Snapshot;
import com.liferay.portal.security.ldap.authenticator.configuration.LDAPAuthConfiguration;
import com.liferay.portal.security.ldap.configuration.ConfigurationProvider;
import com.liferay.portal.security.ldap.configuration.ConfigurationProviderManager;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;
import com.liferay.portal.security.ldap.exportimport.configuration.LDAPExportConfiguration;
import com.liferay.portal.security.ldap.exportimport.configuration.LDAPImportConfiguration;

/**
 * @author Michael C. Han
 */
public class ConfigurationProviderUtil {

	public static ConfigurationProvider<LDAPAuthConfiguration>
		getLDAPAuthConfigurationProvider() {

		ConfigurationProviderManager configurationProviderManager =
			_configurationProviderManagerSnapshot.get();

		return configurationProviderManager.getConfigurationProvider(
			LDAPAuthConfiguration.class);
	}

	public static ConfigurationProvider<LDAPExportConfiguration>
		getLDAPExportConfigurationProvider() {

		ConfigurationProviderManager configurationProviderManager =
			_configurationProviderManagerSnapshot.get();

		return configurationProviderManager.getConfigurationProvider(
			LDAPExportConfiguration.class);
	}

	public static ConfigurationProvider<LDAPImportConfiguration>
		getLDAPImportConfigurationProvider() {

		ConfigurationProviderManager configurationProviderManager =
			_configurationProviderManagerSnapshot.get();

		return configurationProviderManager.getConfigurationProvider(
			LDAPImportConfiguration.class);
	}

	public static ConfigurationProvider<LDAPServerConfiguration>
		getLDAPServerConfigurationProvider() {

		ConfigurationProviderManager configurationProviderManager =
			_configurationProviderManagerSnapshot.get();

		return configurationProviderManager.getConfigurationProvider(
			LDAPServerConfiguration.class);
	}

	private static final Snapshot<ConfigurationProviderManager>
		_configurationProviderManagerSnapshot = new Snapshot<>(
			ConfigurationProviderUtil.class,
			ConfigurationProviderManager.class);

}
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

import com.liferay.portal.security.ldap.authenticator.configuration.LDAPAuthConfiguration;
import com.liferay.portal.security.ldap.configuration.ConfigurationProvider;
import com.liferay.portal.security.ldap.configuration.ConfigurationProviderManager;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;
import com.liferay.portal.security.ldap.exportimport.configuration.LDAPExportConfiguration;
import com.liferay.portal.security.ldap.exportimport.configuration.LDAPImportConfiguration;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = {})
public class ConfigurationProviderUtil {

	public static ConfigurationProvider<LDAPAuthConfiguration>
		getLDAPAuthConfigurationProvider() {

		return _ldapAuthConfigurationProvider;
	}

	public static ConfigurationProvider<LDAPExportConfiguration>
		getLDAPExportConfigurationProvider() {

		return _ldapExportConfigurationProvider;
	}

	public static ConfigurationProvider<LDAPImportConfiguration>
		getLDAPImportConfigurationProvider() {

		return _ldapImportConfigurationProvider;
	}

	public static ConfigurationProvider<LDAPServerConfiguration>
		getLDAPServerConfigurationProvider() {

		return _ldapServerConfigurationProvider;
	}

	@Activate
	protected void activate() {
		_ldapAuthConfigurationProvider =
			_configurationProviderManager.getConfigurationProvider(
				LDAPAuthConfiguration.class);
		_ldapExportConfigurationProvider =
			_configurationProviderManager.getConfigurationProvider(
				LDAPExportConfiguration.class);
		_ldapImportConfigurationProvider =
			_configurationProviderManager.getConfigurationProvider(
				LDAPImportConfiguration.class);
		_ldapServerConfigurationProvider =
			_configurationProviderManager.getConfigurationProvider(
				LDAPServerConfiguration.class);
	}

	private static ConfigurationProvider<LDAPAuthConfiguration>
		_ldapAuthConfigurationProvider;
	private static ConfigurationProvider<LDAPExportConfiguration>
		_ldapExportConfigurationProvider;
	private static ConfigurationProvider<LDAPImportConfiguration>
		_ldapImportConfigurationProvider;
	private static ConfigurationProvider<LDAPServerConfiguration>
		_ldapServerConfigurationProvider;

	@Reference
	private ConfigurationProviderManager _configurationProviderManager;

}
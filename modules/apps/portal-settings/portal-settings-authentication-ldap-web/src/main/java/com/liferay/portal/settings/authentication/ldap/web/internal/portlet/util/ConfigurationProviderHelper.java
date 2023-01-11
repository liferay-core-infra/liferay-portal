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
import com.liferay.portal.security.ldap.configuration.ConfigurationProviderManager;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;
import com.liferay.portal.security.ldap.exportimport.configuration.LDAPExportConfiguration;
import com.liferay.portal.security.ldap.exportimport.configuration.LDAPImportConfiguration;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Janis Zhang
 */
@Component(service = {})
public class ConfigurationProviderHelper {

	@Activate
	protected void activate() {
		ConfigurationProviderUtil.setLDAPAuthConfigurationProvider(
			_configurationProviderManager.getConfigurationProvider(
				LDAPAuthConfiguration.class));
		ConfigurationProviderUtil.setLDAPExportConfigurationProvider(
			_configurationProviderManager.getConfigurationProvider(
				LDAPExportConfiguration.class));
		ConfigurationProviderUtil.setLDAPImportConfigurationProvider(
			_configurationProviderManager.getConfigurationProvider(
				LDAPImportConfiguration.class));
		ConfigurationProviderUtil.setLDAPServerConfigurationProvider(
			_configurationProviderManager.getConfigurationProvider(
				LDAPServerConfiguration.class));
	}

	@Reference
	private ConfigurationProviderManager _configurationProviderManager;

}
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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.ldap.configuration.ConfigurationProvider;
import com.liferay.portal.security.ldap.configuration.ConfigurationProviderManager;

import java.io.IOException;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.component.annotations.Component;
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
			_configurationProviderManager.getConfigurationProvider(factoryPid);

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

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ConfigurationProviderManager _configurationProviderManager;

}
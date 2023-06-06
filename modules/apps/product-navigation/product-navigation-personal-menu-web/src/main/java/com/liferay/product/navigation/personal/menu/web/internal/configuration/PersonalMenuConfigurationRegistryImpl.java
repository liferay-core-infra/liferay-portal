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

package com.liferay.product.navigation.personal.menu.web.internal.configuration;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.product.navigation.personal.menu.configuration.PersonalMenuConfiguration;
import com.liferay.product.navigation.personal.menu.configuration.PersonalMenuConfigurationRegistry;
import com.liferay.product.navigation.personal.menu.web.internal.configuration.admin.util.PersonalMenuConfigurationRegistryUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Samuel Trong Tran
 */
@Component(
	configurationPid = "com.liferay.product.navigation.personal.menu.configuration.PersonalMenuConfiguration",
	service = PersonalMenuConfigurationRegistry.class
)
public class PersonalMenuConfigurationRegistryImpl
	implements PersonalMenuConfigurationRegistry {

	@Override
	public PersonalMenuConfiguration getCompanyPersonalMenuConfiguration(
		long companyId) {

		PersonalMenuConfiguration personalMenuConfiguration =
			PersonalMenuConfigurationRegistryUtil.
				getCompanyConfigurationBeansPersonalMenuConfiguration(
					companyId);

		if (personalMenuConfiguration != null) {
			return personalMenuConfiguration;
		}

		return _systemPersonalMenuConfiguration;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_systemPersonalMenuConfiguration = ConfigurableUtil.createConfigurable(
			PersonalMenuConfiguration.class, properties);
	}

	private volatile PersonalMenuConfiguration _systemPersonalMenuConfiguration;

}
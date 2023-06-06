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

package com.liferay.product.navigation.personal.menu.web.internal.configuration.admin.service;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.product.navigation.personal.menu.configuration.PersonalMenuConfiguration;
import com.liferay.product.navigation.personal.menu.web.internal.configuration.admin.util.PersonalMenuConfigurationRegistryUtil;

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;

/**
 * @author Joao Victor Alves
 */
@Component(
	property = Constants.SERVICE_PID + "=com.liferay.product.navigation.personal.menu.configuration.PersonalMenuConfiguration.scoped",
	service = ManagedServiceFactory.class
)
public class PersonalMenuConfigurationManagedServiceFactory
	implements ManagedServiceFactory {

	@Override
	public void deleted(String pid) {
		_unmapPid(pid);
	}

	@Override
	public String getName() {
		return "com.liferay.product.navigation.personal.menu.configuration." +
			"PersonalMenuConfiguration.scoped";
	}

	@Override
	public void updated(String pid, Dictionary dictionary)
		throws ConfigurationException {

		_unmapPid(pid);

		long companyId = GetterUtil.getLong(
			dictionary.get("companyId"), CompanyConstants.SYSTEM);

		if (companyId != CompanyConstants.SYSTEM) {
			PersonalMenuConfigurationRegistryUtil.
				updateCompanyConfigurationBeans(
					companyId,
					ConfigurableUtil.createConfigurable(
						PersonalMenuConfiguration.class, dictionary));
			_companyIds.put(pid, companyId);
		}
	}

	private void _unmapPid(String pid) {
		Long companyId = _companyIds.remove(pid);

		if (companyId != null) {
			PersonalMenuConfigurationRegistryUtil.
				removeCompanyConfigurationBeans(companyId);
		}
	}

	private final Map<String, Long> _companyIds = new ConcurrentHashMap<>();

}
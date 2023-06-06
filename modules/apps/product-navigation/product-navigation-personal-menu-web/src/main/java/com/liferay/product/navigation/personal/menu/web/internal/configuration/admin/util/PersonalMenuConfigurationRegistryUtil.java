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

package com.liferay.product.navigation.personal.menu.web.internal.configuration.admin.util;

import com.liferay.product.navigation.personal.menu.configuration.PersonalMenuConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Joao Victor Alves
 */
public class PersonalMenuConfigurationRegistryUtil {

	public static PersonalMenuConfiguration
		getCompanyConfigurationBeansPersonalMenuConfiguration(long companyId) {

		return _companyConfigurationBeans.get(companyId);
	}

	public static void removeCompanyConfigurationBeans(long companyId) {
		_companyConfigurationBeans.remove(companyId);
	}

	public static void updateCompanyConfigurationBeans(
		long companyId, PersonalMenuConfiguration personalMenuConfiguration) {

		_companyConfigurationBeans.put(companyId, personalMenuConfiguration);
	}

	private static final Map<Long, PersonalMenuConfiguration>
		_companyConfigurationBeans = new ConcurrentHashMap<>();

}
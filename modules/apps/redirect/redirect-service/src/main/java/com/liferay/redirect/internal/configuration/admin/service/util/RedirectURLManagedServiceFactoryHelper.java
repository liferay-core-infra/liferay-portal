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

package com.liferay.redirect.internal.configuration.admin.service.util;

import com.liferay.redirect.internal.configuration.RedirectURLConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * @author Joao Victor Alves
 */
@Component(service = RedirectURLManagedServiceFactoryHelper.class)
public class RedirectURLManagedServiceFactoryHelper {

	public RedirectURLConfiguration getCompanyRedirectURLConfiguration(
		long companyId) {

		if (_companyConfigurationBeans.containsKey(companyId)) {
			return _companyConfigurationBeans.get(companyId);
		}

		return _systemRedirectURLConfiguration;
	}

	public void putCompanyConfigurationBeans(
		long companyId, RedirectURLConfiguration redirectURLConfiguration) {

		_companyConfigurationBeans.put(companyId, redirectURLConfiguration);
	}

	public void removeCompanyConfigurationBeans(long companyId) {
		_companyConfigurationBeans.remove(companyId);
	}

	public void setSystemRedirectURLConfiguration(
		RedirectURLConfiguration redirectURLConfiguration) {

		_systemRedirectURLConfiguration = redirectURLConfiguration;
	}

	private final Map<Long, RedirectURLConfiguration>
		_companyConfigurationBeans = new ConcurrentHashMap<>();
	private volatile RedirectURLConfiguration _systemRedirectURLConfiguration;

}
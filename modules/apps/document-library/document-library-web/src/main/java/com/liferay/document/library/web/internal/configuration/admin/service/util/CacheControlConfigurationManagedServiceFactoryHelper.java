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

package com.liferay.document.library.web.internal.configuration.admin.service.util;

import com.liferay.document.library.web.internal.configuration.CacheControlConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * @author Joao Victor Alves
 */
@Component(service = CacheControlConfigurationManagedServiceFactoryHelper.class)
public class CacheControlConfigurationManagedServiceFactoryHelper {

	public CacheControlConfiguration getCompanyCacheControlConfiguration(
		long companyId) {

		if (_companyConfigurationBeans.containsKey(companyId)) {
			return _companyConfigurationBeans.get(companyId);
		}

		return _systemCacheControlConfiguration;
	}

	public void putCompanyConfigurationBeans(
		long companyId, CacheControlConfiguration cacheControlConfiguration) {

		_companyConfigurationBeans.put(companyId, cacheControlConfiguration);
	}

	public void removeCompanyConfigurationBeans(long companyId) {
		_companyConfigurationBeans.remove(companyId);
	}

	public void setSystemCacheControlConfiguration(
		CacheControlConfiguration systemCacheControlConfiguration) {

		_systemCacheControlConfiguration = systemCacheControlConfiguration;
	}

	private final Map<Long, CacheControlConfiguration>
		_companyConfigurationBeans = new ConcurrentHashMap<>();
	private volatile CacheControlConfiguration _systemCacheControlConfiguration;

}
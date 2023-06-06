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

package com.liferay.analytics.settings.internal.configuration.util;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.osgi.util.service.Snapshot;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Dictionary;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Joao Victor Alves
 */
public class AnalyticsConfigurationRegistryUtil {

	public static Map<Long, AnalyticsConfiguration>
		getAnalyticsConfiguration() {

		return _analyticsConfigurations;
	}

	public static Long getCompanyId(String id) {
		return _companyIds.get(id);
	}

	public static Set<Map.Entry<String, Long>> getEntrySetCompanyIds() {
		return _companyIds.entrySet();
	}

	public static AnalyticsConfiguration getOrDefaultAnalyticsConfiguration(
		Long companyId, AnalyticsConfiguration analyticsConfiguration) {

		return _analyticsConfigurations.getOrDefault(
			companyId, analyticsConfiguration);
	}

	public static long getOrDefaultCompanyId(String pid, long value) {
		return _companyIds.getOrDefault(pid, value);
	}

	public static boolean hasConfiguration() {
		Configuration[] configurations = null;

		try {
			ConfigurationAdmin configurationAdmin =
				_configurationAdminSnapshot.get();

			configurations = configurationAdmin.listConfigurations(
				"(service.pid=" + AnalyticsConfiguration.class.getName() +
					"*)");
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to list analytics configurations", exception);
			}
		}

		if (configurations == null) {
			return false;
		}

		for (Configuration configuration : configurations) {
			Dictionary<String, Object> properties =
				configuration.getProperties();

			if (Validator.isNotNull(properties.get("token"))) {
				return true;
			}
		}

		return false;
	}

	public static void removeAnalyticsConfiguration(Long companyId) {
		_analyticsConfigurations.remove(companyId);
	}

	public static Long removeCompanyId(String pid) {
		return _companyIds.remove(pid);
	}

	public static void updateAnalyticsConfiguration(
		AnalyticsConfiguration analyticsConfiguration, Long companyId) {

		_analyticsConfigurations.put(companyId, analyticsConfiguration);
	}

	public static void updateEntrySetCompanyIds(String pid, long companyId) {
		_companyIds.put(pid, companyId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsConfigurationRegistryUtil.class);

	private static final Map<Long, AnalyticsConfiguration>
		_analyticsConfigurations = new ConcurrentHashMap<>();
	private static final Map<String, Long> _companyIds =
		new ConcurrentHashMap<>();
	private static final Snapshot<ConfigurationAdmin>
		_configurationAdminSnapshot = new Snapshot<>(
			AnalyticsConfigurationRegistryUtil.class, ConfigurationAdmin.class);

}
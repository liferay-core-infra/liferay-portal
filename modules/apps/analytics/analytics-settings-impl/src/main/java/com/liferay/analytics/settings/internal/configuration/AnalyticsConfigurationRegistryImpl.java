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

package com.liferay.analytics.settings.internal.configuration;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.configuration.AnalyticsConfigurationRegistry;
import com.liferay.analytics.settings.internal.configuration.util.AnalyticsConfigurationRegistryUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.service.ClassNameLocalService;

import java.util.Dictionary;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rachael Koestartyo
 */
@Component(
	configurationPid = "com.liferay.analytics.settings.configuration.AnalyticsConfiguration",
	service = AnalyticsConfigurationRegistry.class
)
public class AnalyticsConfigurationRegistryImpl
	implements AnalyticsConfigurationRegistry {

	@Override
	public AnalyticsConfiguration getAnalyticsConfiguration(long companyId) {
		return AnalyticsConfigurationRegistryUtil.
			getOrDefaultAnalyticsConfiguration(
				companyId, _systemAnalyticsConfiguration);
	}

	@Override
	public AnalyticsConfiguration getAnalyticsConfiguration(String pid) {
		Long companyId = AnalyticsConfigurationRegistryUtil.getCompanyId(pid);

		if (companyId == null) {
			return _systemAnalyticsConfiguration;
		}

		return getAnalyticsConfiguration(companyId);
	}

	@Override
	public Dictionary<String, Object> getAnalyticsConfigurationProperties(
		long companyId) {

		if (!isActive()) {
			return null;
		}

		for (Map.Entry<String, Long> entry :
				AnalyticsConfigurationRegistryUtil.getEntrySetCompanyIds()) {

			if (Objects.equals(entry.getValue(), companyId)) {
				try {
					Configuration configuration =
						_configurationAdmin.getConfiguration(
							entry.getKey(), StringPool.QUESTION);

					return configuration.getProperties();
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to get configuration for company " +
								companyId,
							exception);
					}

					break;
				}
			}
		}

		return null;
	}

	@Override
	public Map<Long, AnalyticsConfiguration> getAnalyticsConfigurations() {
		return AnalyticsConfigurationRegistryUtil.getAnalyticsConfiguration();
	}

	@Override
	public long getCompanyId(String pid) {
		return AnalyticsConfigurationRegistryUtil.getOrDefaultCompanyId(
			pid, CompanyConstants.SYSTEM);
	}

	@Override
	public boolean isActive() {
		if (!_active && AnalyticsConfigurationRegistryUtil.hasConfiguration()) {
			_active = true;
		}
		else if (_active &&
				 !AnalyticsConfigurationRegistryUtil.hasConfiguration()) {

			_active = false;
		}

		return _active;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_systemAnalyticsConfiguration = ConfigurableUtil.createConfigurable(
			AnalyticsConfiguration.class, properties);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsConfigurationRegistryImpl.class);

	private boolean _active;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	private volatile AnalyticsConfiguration _systemAnalyticsConfiguration;

}
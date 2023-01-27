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

package com.liferay.notification.internal.instance.lifecycle;

import com.liferay.notification.internal.configuration.NotificationQueueConfiguration;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Murilo Stodolni
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class NotificationQueuePortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		if (_log.isDebugEnabled()) {
			_log.debug("Registered portal instance " + company);
		}

		NotificationQueueConfiguration notificationQueueConfiguration =
			_configurationProvider.getCompanyConfiguration(
				NotificationQueueConfiguration.class, company.getCompanyId());

		_configurationProvider.saveCompanyConfiguration(
			NotificationQueueConfiguration.class, company.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"checkInterval", notificationQueueConfiguration.checkInterval()
			).put(
				"deleteInterval",
				notificationQueueConfiguration.deleteInterval()
			).build());
	}

	@Override
	public void portalInstanceUnregistered(Company company) throws Exception {
		_configurationProvider.deleteCompanyConfiguration(
			NotificationQueueConfiguration.class, company.getCompanyId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		NotificationQueuePortalInstanceLifecycleListener.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

}
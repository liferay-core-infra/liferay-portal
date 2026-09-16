/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.partition.internal.instance.lifecycle;

import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.util.PropsValues;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author István András Dézsi
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class VirtualHostPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		if (!PropsValues.DATABASE_PARTITION_ENABLED) {
			return;
		}

		for (VirtualHost virtualHost :
				_virtualHostLocalService.getVirtualHosts(
					company.getCompanyId())) {

			_virtualHostLocalService.registerVirtualHost(
				virtualHost.getHostname(), virtualHost.getCompanyId());
		}
	}

	@Override
	public void portalInstanceUnregistered(Company company) throws Exception {
		if (!PropsValues.DATABASE_PARTITION_ENABLED) {
			return;
		}

		_virtualHostLocalService.unregisterVirtualHosts(company.getCompanyId());
	}

	@Reference
	private VirtualHostLocalService _virtualHostLocalService;

}
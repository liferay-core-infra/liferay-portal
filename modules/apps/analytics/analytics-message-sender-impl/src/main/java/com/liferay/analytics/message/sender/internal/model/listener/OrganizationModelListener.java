/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.sender.internal.model.listener;

import com.liferay.analytics.message.sender.model.listener.BaseAnalyticsModelListener;
import com.liferay.analytics.message.sender.model.listener.EntityModelListener;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.service.OrganizationLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rachael Koestartyo
 */
@Component(service = ModelListener.class)
public class OrganizationModelListener
	extends BaseAnalyticsModelListener<Organization> {

	@Override
	public void onAfterRemove(Organization organization)
		throws ModelListenerException {

		if (!analyticsConfigurationRegistry.isActive() ||
			isExcluded(organization)) {

			return;
		}

		updateConfigurationProperties(
			organization.getCompanyId(), "syncedOrganizationIds",
			String.valueOf(organization.getOrganizationId()), null);
	}

	@Override
	protected ActionableDynamicQuery getActionableDynamicQuery() {
		return _organizationLocalService.getActionableDynamicQuery();
	}

	@Override
	protected EntityModelListener<Organization> getEntityModelListener() {
		return _organizationEntityModelListener;
	}

	@Override
	protected Organization getModel(long id) throws Exception {
		return _organizationLocalService.getOrganization(id);
	}

	@Override
	protected String getPrimaryKeyName() {
		return "organizationId";
	}

	@Reference(target = "(entity.model.listener.type=organization)")
	private EntityModelListener<Organization> _organizationEntityModelListener;

	@Reference
	private OrganizationLocalService _organizationLocalService;

}
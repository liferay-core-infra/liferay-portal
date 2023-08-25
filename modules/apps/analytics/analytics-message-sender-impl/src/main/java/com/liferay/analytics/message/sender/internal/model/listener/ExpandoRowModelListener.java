/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.sender.internal.model.listener;

import com.liferay.analytics.message.sender.model.listener.EntityModel;
import com.liferay.expando.kernel.model.ExpandoRow;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rachael Koestartyo
 */
@Component(service = ModelListener.class)
public class ExpandoRowModelListener
	extends BaseAnalyticsModelListener<ExpandoRow> {

	@Override
	protected EntityModel<ExpandoRow> getEntityModelListener() {
		return _expandoRowEntityModel;
	}

	@Override
	protected ExpandoRow getModel(long id) throws Exception {
		return _expandoRowLocalService.getExpandoRow(id);
	}

	@Override
	protected boolean isExcluded(ExpandoRow expandoRow) {
		if (analyticsModelHelper.isCustomField(
				Organization.class.getName(), expandoRow.getTableId())) {

			return false;
		}

		if (analyticsModelHelper.isCustomField(
				User.class.getName(), expandoRow.getTableId())) {

			User user = userLocalService.fetchUser(expandoRow.getClassPK());

			if (!analyticsModelHelper.isUserActive(user)) {
				return true;
			}

			return analyticsModelHelper.isUserExcluded(user);
		}

		return true;
	}

	@Reference(target = "(entity.model.type=expandorow)")
	private EntityModel<ExpandoRow> _expandoRowEntityModel;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

}
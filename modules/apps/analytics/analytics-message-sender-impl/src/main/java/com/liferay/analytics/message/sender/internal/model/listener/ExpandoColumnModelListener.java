/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.sender.internal.model.listener;

import com.liferay.analytics.message.sender.internal.helper.AnalyticsModelHelper;
import com.liferay.analytics.message.sender.model.listener.EntityModel;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rachael Koestartyo
 */
@Component(service = ModelListener.class)
public class ExpandoColumnModelListener
	extends BaseAnalyticsModelListener<ExpandoColumn> {

	@Override
	public void onBeforeUpdate(
			ExpandoColumn originalExpandoColumn, ExpandoColumn expandoColumn)
		throws ModelListenerException {

		if (!analyticsConfigurationRegistry.isActive()) {
			return;
		}

		ExpandoColumn oldExpandoColumn =
			_expandoColumnLocalService.fetchExpandoColumn(
				expandoColumn.getColumnId());

		if (Objects.equals(
				oldExpandoColumn.getName(), expandoColumn.getName()) &&
			Objects.equals(
				oldExpandoColumn.getType(), expandoColumn.getType())) {

			return;
		}

		_expandoColumnEntityModel.addAnalyticsMessage(
			"update",
			_expandoColumnEntityModel.getAttributeNames(
				expandoColumn.getCompanyId()),
			expandoColumn);
	}

	@Override
	protected EntityModel<ExpandoColumn> getEntityModelListener() {
		return _expandoColumnEntityModel;
	}

	@Override
	protected ExpandoColumn getModel(long id) throws Exception {
		return _expandoColumnLocalService.getColumn(id);
	}

	@Override
	protected boolean isExcluded(ExpandoColumn expandoColumn) {
		if (_analyticsModelHelper.isCustomField(
				Organization.class.getName(), expandoColumn.getTableId())) {

			return false;
		}

		if (_analyticsModelHelper.isCustomField(
				User.class.getName(), expandoColumn.getTableId())) {

			AnalyticsConfiguration analyticsConfiguration =
				analyticsConfigurationRegistry.getAnalyticsConfiguration(
					expandoColumn.getCompanyId());

			if (ArrayUtil.isEmpty(
					analyticsConfiguration.syncedUserFieldNames())) {

				return true;
			}

			for (String syncedUserFieldName :
					analyticsConfiguration.syncedUserFieldNames()) {

				if (Objects.equals(
						expandoColumn.getName(), syncedUserFieldName)) {

					return false;
				}
			}

			return true;
		}

		return true;
	}

	@Reference
	private AnalyticsModelHelper _analyticsModelHelper;

	@Reference(target = "(entity.model.type=expandocolumn)")
	private EntityModel<ExpandoColumn> _expandoColumnEntityModel;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

}
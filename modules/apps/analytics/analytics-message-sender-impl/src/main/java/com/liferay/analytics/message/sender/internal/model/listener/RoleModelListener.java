/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.sender.internal.model.listener;

import com.liferay.analytics.message.sender.model.listener.EntityModel;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shinn Lok
 */
@Component(service = ModelListener.class)
public class RoleModelListener extends BaseAnalyticsModelListener<Role> {

	@Override
	protected ActionableDynamicQuery getActionableDynamicQuery() {
		ActionableDynamicQuery actionableDynamicQuery =
			_roleLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property nameProperty = PropertyFactoryUtil.forName("name");

				dynamicQuery.add(
					nameProperty.ne(RoleConstants.ANALYTICS_ADMINISTRATOR));

				Property typeProperty = PropertyFactoryUtil.forName("type");

				dynamicQuery.add(typeProperty.eq(RoleConstants.TYPE_REGULAR));
			});

		return actionableDynamicQuery;
	}

	@Override
	protected EntityModel<Role> getEntityModelListener() {
		return _roleEntityModel;
	}

	@Override
	protected Role getModel(long id) throws Exception {
		return _roleLocalService.getRole(id);
	}

	@Override
	protected String getPrimaryKeyName() {
		return "roleId";
	}

	@Override
	protected boolean isExcluded(Role role) {
		if (role.getType() == RoleConstants.TYPE_REGULAR) {
			return false;
		}

		return true;
	}

	@Reference(target = "(entity.model.type=role)")
	private EntityModel<Role> _roleEntityModel;

	@Reference
	private RoleLocalService _roleLocalService;

}
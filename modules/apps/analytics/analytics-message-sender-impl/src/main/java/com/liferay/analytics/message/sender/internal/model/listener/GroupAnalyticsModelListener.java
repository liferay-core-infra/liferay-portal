/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.sender.internal.model.listener;

import com.liferay.analytics.message.sender.model.listener.BaseAnalyticsModelListener;
import com.liferay.analytics.message.sender.model.listener.EntityModelListener;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.GroupLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rachael Koestartyo
 */
@Component(service = ModelListener.class)
public class GroupAnalyticsModelListener
	extends BaseAnalyticsModelListener<Group> {

	@Override
	public void onAfterRemove(Group group) throws ModelListenerException {
		if (!analyticsConfigurationRegistry.isActive() || isExcluded(group)) {
			return;
		}

		updateConfigurationProperties(
			group.getCompanyId(), "syncedGroupIds",
			String.valueOf(group.getGroupId()), "liferayAnalyticsGroupIds");
	}

	@Override
	protected ActionableDynamicQuery getActionableDynamicQuery() {
		ActionableDynamicQuery actionableDynamicQuery =
			_groupLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property property = PropertyFactoryUtil.forName("site");

				dynamicQuery.add(property.eq(true));
			});

		return actionableDynamicQuery;
	}

	@Override
	protected EntityModelListener<Group> getEntityModelListener() {
		return _groupEntityModelListener;
	}

	@Override
	protected Group getModel(long id) throws Exception {
		return _groupLocalService.getGroup(id);
	}

	@Override
	protected String getPrimaryKeyName() {
		return "groupId";
	}

	@Override
	protected boolean isExcluded(Group group) {
		if (!group.isSite()) {
			return true;
		}

		return false;
	}

	@Reference(target = "(entity.model.listener.type=group)")
	private EntityModelListener<Group> _groupEntityModelListener;

	@Reference
	private GroupLocalService _groupLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.sender.internal.model.listener;

import com.liferay.analytics.message.sender.model.listener.BaseAnalyticsModelListener;
import com.liferay.analytics.message.sender.model.listener.EntityModelListener;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.service.TeamLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shinn Lok
 */
@Component(service = ModelListener.class)
public class TeamAnalyticsModelListener
	extends BaseAnalyticsModelListener<Team> {

	@Override
	protected ActionableDynamicQuery getActionableDynamicQuery() {
		return _teamLocalService.getActionableDynamicQuery();
	}

	@Override
	protected EntityModelListener<Team> getEntityModelListener() {
		return _teamEntityModelListener;
	}

	@Override
	protected Team getModel(long id) throws Exception {
		return _teamLocalService.getTeam(id);
	}

	@Override
	protected String getPrimaryKeyName() {
		return "teamId";
	}

	@Reference(target = "(entity.model.listener.type=team)")
	private EntityModelListener<Team> _teamEntityModelListener;

	@Reference
	private TeamLocalService _teamLocalService;

}
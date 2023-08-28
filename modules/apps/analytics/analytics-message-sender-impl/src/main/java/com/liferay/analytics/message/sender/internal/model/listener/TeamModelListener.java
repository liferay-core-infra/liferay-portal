/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.sender.internal.model.listener;

import com.liferay.analytics.message.sender.model.listener.EntityModel;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.service.TeamLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shinn Lok
 */
@Component(service = ModelListener.class)
public class TeamModelListener extends BaseModelListener<Team> {

	@Override
	protected EntityModel<Team> getEntityModelListener() {
		return _teamEntityModel;
	}

	@Override
	protected Team getModel(long id) throws Exception {
		return _teamLocalService.getTeam(id);
	}

	@Reference(target = "(entity.model.type=team)")
	private EntityModel<Team> _teamEntityModel;

	@Reference
	private TeamLocalService _teamLocalService;

}
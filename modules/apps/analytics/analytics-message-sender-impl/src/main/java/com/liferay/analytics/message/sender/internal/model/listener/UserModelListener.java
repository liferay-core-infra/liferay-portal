/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.sender.internal.model.listener;

import com.liferay.analytics.message.sender.internal.helper.AnalyticsModelHelper;
import com.liferay.analytics.message.sender.model.listener.EntityModel;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.User;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rachael Koestartyo
 */
@Component(service = ModelListener.class)
public class UserModelListener extends BaseAnalyticsModelListener<User> {

	@Override
	protected EntityModel<User> getEntityModelListener() {
		return _userEntityModel;
	}

	@Override
	protected User getModel(long id) throws Exception {
		return userLocalService.getUser(id);
	}

	@Override
	protected boolean isExcluded(User user) {
		return _analyticsModelHelper.isUserExcluded(user);
	}

	@Reference
	private AnalyticsModelHelper _analyticsModelHelper;

	@Reference(target = "(entity.model.type=user)")
	private EntityModel<User> _userEntityModel;

}
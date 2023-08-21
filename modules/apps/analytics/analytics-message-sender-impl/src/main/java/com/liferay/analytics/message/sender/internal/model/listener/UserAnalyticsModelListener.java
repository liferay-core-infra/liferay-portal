/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.sender.internal.model.listener;

import com.liferay.analytics.message.sender.model.listener.BaseAnalyticsModelListener;
import com.liferay.analytics.message.sender.model.listener.EntityModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.User;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rachael Koestartyo
 */
@Component(service = ModelListener.class)
public class UserAnalyticsModelListener
	extends BaseAnalyticsModelListener<User> {

	@Override
	protected EntityModelListener<User> getEntityModelListener() {
		return _userEntityModelListener;
	}

	@Override
	protected User getModel(long id) throws Exception {
		return userLocalService.getUser(id);
	}

	@Override
	protected String getPrimaryKeyName() {
		return "userId";
	}

	@Override
	protected boolean isExcluded(User user) {
		return isUserExcluded(user);
	}

	@Reference(target = "(entity.model.listener.type=user)")
	private EntityModelListener<User> _userEntityModelListener;

}
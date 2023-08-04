/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.sender.internal.entity.model;

import com.liferay.analytics.message.sender.model.listener.EntityModel;
import com.liferay.portal.kernel.model.User;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Joao Victor Alves
 */
@Component(property = "entity.model.type=user", service = EntityModel.class)
public class UserEntityModel extends BaseEntityModel<User> {

	@Override
	public List<String> getAttributeNames(long companyId) {
		return getUserAttributeNames(companyId);
	}

	@Override
	protected User getModel(long id) throws Exception {
		return userLocalService.getUser(id);
	}

	@Override
	protected String getPrimaryKeyName() {
		return "userId";
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.sender.internal.entity.model;

import com.liferay.analytics.message.sender.model.listener.EntityModel;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.RoleLocalService;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Joao Victor Alves
 */
@Component(property = "entity.model.type=role", service = EntityModel.class)
public class RoleEntityModel extends BaseEntityModel<Role> {

	@Override
	public List<String> getAttributeNames(long companyId) {
		return _attributeNames;
	}

	@Override
	public long[] getMembershipIds(User user) {
		return user.getRoleIds();
	}

	@Override
	public String getModelClassName() {
		return Role.class.getName();
	}

	@Override
	protected Role getModel(long id) throws Exception {
		return _roleLocalService.getRole(id);
	}

	@Override
	protected String getPrimaryKeyName() {
		return "roleId";
	}

	private static final List<String> _attributeNames =
		Collections.singletonList("name");

	@Reference
	private RoleLocalService _roleLocalService;

}
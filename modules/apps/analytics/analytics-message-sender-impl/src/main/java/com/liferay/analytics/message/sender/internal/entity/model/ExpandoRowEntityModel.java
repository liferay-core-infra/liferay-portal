/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.sender.internal.entity.model;

import com.liferay.analytics.message.sender.model.listener.EntityModel;
import com.liferay.expando.kernel.model.ExpandoRow;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Joao Victor Alves
 */
@Component(
	property = "entity.model.type=expandorow", service = EntityModel.class
)
public class ExpandoRowEntityModel extends BaseEntityModel<ExpandoRow> {

	@Override
	public List<String> getAttributeNames(long companyId) {
		return Collections.singletonList("modifiedDate");
	}

	@Override
	protected ExpandoRow getModel(long id) throws Exception {
		return _expandoRowLocalService.getExpandoRow(id);
	}

	@Override
	protected String getPrimaryKeyName() {
		return "classPK";
	}

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

}
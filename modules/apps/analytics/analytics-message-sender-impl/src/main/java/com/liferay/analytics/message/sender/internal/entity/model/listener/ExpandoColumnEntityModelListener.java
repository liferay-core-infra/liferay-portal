/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.sender.internal.entity.model.listener;

import com.liferay.analytics.message.sender.model.listener.BaseEntityModelListener;
import com.liferay.analytics.message.sender.model.listener.EntityModelListener;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Joao Victor Alves
 */
@Component(
	property = "entity.model.listener.type=expandocolumn",
	service = EntityModelListener.class
)
public class ExpandoColumnEntityModelListener
	extends BaseEntityModelListener<ExpandoColumn> {

	@Override
	public List<String> getAttributeNames(long companyId) {
		return _attributeNames;
	}

	@Override
	protected ExpandoColumn getModel(long id) throws Exception {
		return _expandoColumnLocalService.getColumn(id);
	}

	@Override
	protected String getPrimaryKeyName() {
		return "name";
	}

	private static final List<String> _attributeNames = Arrays.asList(
		"className", "companyId", "dataType", "displayType", "name",
		"typeLabel");

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

}
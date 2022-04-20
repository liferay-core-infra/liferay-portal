/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.object.web.internal.object.definitions.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.web.internal.configuration.activator.FFOneToOneRelationshipConfigurationActivator;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.portlet.url.builder.PortletURLBuilder;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Marco Leo
 */
public class ObjectDefinitionsRelationshipsDisplayContext
	extends BaseObjectDefinitionsDisplayContext {

	public ObjectDefinitionsRelationshipsDisplayContext(
		FFOneToOneRelationshipConfigurationActivator
			ffOneToOneRelationshipConfigurationActivator,
		HttpServletRequest httpServletRequest,
		ModelResourcePermission<ObjectDefinition>
			objectDefinitionModelResourcePermission) {

		super(httpServletRequest, objectDefinitionModelResourcePermission);

		_ffOneToOneRelationshipConfigurationActivator =
			ffOneToOneRelationshipConfigurationActivator;
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws Exception {

		return Arrays.asList(
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					getPortletURL()
				).setMVCRenderCommandName(
					"/object_definitions/edit_object_relationship"
				).setParameter(
					"objectRelationshipId", "{id}"
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString(),
				"view", "view",
				LanguageUtil.get(objectRequestHelper.getRequest(), "view"),
				"get", null, "sidePanel"),
			new FDSActionDropdownItem(
				"/o/object-admin/v1.0/object-relationships/{id}", "trash",
				"delete",
				LanguageUtil.get(objectRequestHelper.getRequest(), "delete"),
				"delete", "delete", "async"));
	}

	public boolean isFFOneToOneRelationshipConfigurationEnabled() {
		return _ffOneToOneRelationshipConfigurationActivator.enabled();
	}

	@Override
	protected String getAPIURI() {
		return "/object-relationships";
	}

	@Override
	protected UnsafeConsumer<DropdownItem, Exception>
		getCreationMenuDropdownItemUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref("addObjectRelationship");
			dropdownItem.setLabel(
				LanguageUtil.get(
					objectRequestHelper.getRequest(),
					"add-object-relationship"));
			dropdownItem.setTarget("event");
		};
	}

	private final FFOneToOneRelationshipConfigurationActivator
		_ffOneToOneRelationshipConfigurationActivator;

}
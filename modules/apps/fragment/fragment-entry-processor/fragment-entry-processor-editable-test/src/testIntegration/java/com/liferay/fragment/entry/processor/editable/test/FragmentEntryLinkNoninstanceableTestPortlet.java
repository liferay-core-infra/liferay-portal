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

package com.liferay.fragment.entry.processor.editable.test;

import com.liferay.fragment.entry.processor.editable.test.constants.FragmentEntryLinkPortletKeys;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import java.util.Map;

import javax.portlet.Portlet;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"com.liferay.fragment.entry.processor.portlet.alias=fragment-entry-link-noninstanceable",
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.instanceable=false",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.scopeable=true", "javax.portlet.display-name=Test",
		"javax.portlet.expiration-cache=0",
		"javax.portlet.name=" + FragmentEntryLinkPortletKeys.FRAGMENT_ENTRY_LINK_NONINSTANCEABLE_TEST_PORTLET,
		"javax.portlet.version=3.0"
	},
	service = Portlet.class
)
public class FragmentEntryLinkNoninstanceableTestPortlet extends MVCPortlet {

	@Activate
	protected void activate(Map<String, Object> properties) {
		_portletRegistry.registerAlias(properties);
	}

	@Deactivate
	protected void deactivate(Map<String, Object> properties) {
		_portletRegistry.unregisterAlias(properties);
	}

	@Reference
	private PortletRegistry _portletRegistry;

}
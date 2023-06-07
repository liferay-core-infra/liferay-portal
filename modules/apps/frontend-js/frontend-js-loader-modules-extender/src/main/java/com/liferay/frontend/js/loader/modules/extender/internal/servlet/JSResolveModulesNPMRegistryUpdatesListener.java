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

package com.liferay.frontend.js.loader.modules.extender.internal.servlet;

import com.liferay.frontend.js.loader.modules.extender.internal.servlet.util.JSResolveModulesRegistryUtil;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMRegistryUpdatesListener;
import com.liferay.petra.string.StringPool;

import java.util.UUID;

import org.osgi.service.component.annotations.Component;

/**
 * @author Joao Victor Alves
 */
@Component(
	configurationPid = "com.liferay.frontend.js.loader.modules.extender.internal.configuration.Details",
	service = NPMRegistryUpdatesListener.class
)
public class JSResolveModulesNPMRegistryUpdatesListener
	implements NPMRegistryUpdatesListener {

	@Override
	public void onAfterUpdate() {
		String hash = String.valueOf(UUID.randomUUID());

		JSResolveModulesRegistryUtil.setExpectedPathInfo(
			StringPool.SLASH + hash);
		JSResolveModulesRegistryUtil.setUrl("/js_resolve_modules/" + hash);
	}

}
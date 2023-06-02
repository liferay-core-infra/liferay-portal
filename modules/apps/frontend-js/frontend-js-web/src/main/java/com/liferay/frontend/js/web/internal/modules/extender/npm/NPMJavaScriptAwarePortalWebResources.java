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

package com.liferay.frontend.js.web.internal.modules.extender.npm;

import com.liferay.frontend.js.loader.modules.extender.npm.JavaScriptAwarePortalWebResources;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Peter Fellwock
 */
@Component(service = JavaScriptAwarePortalWebResources.class)
public class NPMJavaScriptAwarePortalWebResources
	extends NPMPortalWebResources implements JavaScriptAwarePortalWebResources {

	@Override
	public void updateLastModifed(long lastModified) {
		LastModifiedUtil.accumulateAndGetLastModified(lastModified);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		Bundle bundle = bundleContext.getBundle();

		LastModifiedUtil.setLastModified(bundle.getLastModified());
	}

}
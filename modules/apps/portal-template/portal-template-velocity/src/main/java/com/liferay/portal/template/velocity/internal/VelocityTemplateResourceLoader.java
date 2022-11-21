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

package com.liferay.portal.template.velocity.internal;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateResourceLoader;
import com.liferay.portal.template.BaseTemplateResourceLoader;
import com.liferay.portal.template.TemplateResourceParser;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Igor Spasic
 * @author Peter Fellwock
 */
@Component(
	service = {
		TemplateResourceLoader.class, VelocityTemplateResourceLoader.class
	}
)
public class VelocityTemplateResourceLoader extends BaseTemplateResourceLoader {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, TemplateResourceParser.class,
			"(lang.type=" + TemplateConstants.LANG_TYPE_VM + ")");

		init(
			TemplateConstants.LANG_TYPE_VM, _serviceTrackerList,
			_velocityTemplateResourceCache);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
		destroy();
	}

	private ServiceTrackerList<TemplateResourceParser> _serviceTrackerList;

	@Reference
	private VelocityTemplateResourceCache _velocityTemplateResourceCache;

}
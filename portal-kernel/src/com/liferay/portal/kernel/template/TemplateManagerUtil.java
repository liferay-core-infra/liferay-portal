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

package com.liferay.portal.kernel.template;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;

import java.util.Set;

import org.osgi.framework.BundleContext;

/**
 * @author Tina Tian
 * @author Raymond Augé
 */
public class TemplateManagerUtil {

	public static Template getTemplate(
			String templateManagerName, TemplateResource templateResource,
			boolean restricted)
		throws TemplateException {

		TemplateManager templateManager = _getTemplateManagerChecked(
			templateManagerName);

		return templateManager.getTemplate(templateResource, restricted);
	}

	public static TemplateManager getTemplateManager(
		String templateManagerName) {

		return _templateManagers.getService(templateManagerName);
	}

	public static Set<String> getTemplateManagerNames() {
		return _templateManagers.keySet();
	}

	public static boolean hasTemplateManager(String templateManagerName) {
		return _templateManagers.containsKey(templateManagerName);
	}

	private static TemplateManager _getTemplateManagerChecked(
			String templateManagerName)
		throws TemplateException {

		TemplateManager templateManager = _templateManagers.getService(
			templateManagerName);

		if (templateManager == null) {
			throw new TemplateException(
				"Unsupported template manager " + templateManagerName);
		}

		return templateManager;
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	private static final ServiceTrackerMap<String, TemplateManager>
		_templateManagers = ServiceTrackerMapFactory.openSingleValueMap(
			_bundleContext, TemplateManager.class, null,
			(serviceReference, emitter) -> {
				TemplateManager templateManager = _bundleContext.getService(
					serviceReference);

				emitter.emit(templateManager.getName());

				_bundleContext.ungetService(serviceReference);
			});

}
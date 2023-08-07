/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.controller;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import org.osgi.framework.BundleContext;

/**
 * @author Daniel Kocsis
 */
public class ExportImportControllerRegistryUtil {

	public static ExportController getExportController(String className) {
		return (ExportController)_exportControllers.getService(className);
	}

	public static ImportController getImportController(String className) {
		return (ImportController)_importControllers.getService(className);
	}

	private ExportImportControllerRegistryUtil() {
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	private static final ServiceTrackerMap<String, ExportController>
		_exportControllers = ServiceTrackerMapFactory.openSingleValueMap(
			_bundleContext, null, "(export.controller=true)",
			(serviceReference, emitter) -> {
				for (String modelClassName :
						StringUtil.asList(
							serviceReference.getProperty("model.class.name"))) {

					emitter.emit(modelClassName);
				}

				_bundleContext.ungetService(serviceReference);
			});

	private static final ServiceTrackerMap<String, ImportController>
		_importControllers = ServiceTrackerMapFactory.openSingleValueMap(
			_bundleContext, null, "(import.controller=true)",
			(serviceReference, emitter) -> {
				for (String modelClassName :
						StringUtil.asList(
							serviceReference.getProperty("model.class.name"))) {

					emitter.emit(modelClassName);
				}

				_bundleContext.ungetService(serviceReference);
			});

}
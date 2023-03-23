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

package com.liferay.exportimport.content.processor;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Gergely Mathe
 * @author Máté Thurzó
 */
public class ExportImportContentProcessorRegistryUtil {

	public static ExportImportContentProcessor<String>
		getExportImportContentProcessor(String className) {

		return _serviceTrackerMap.getService(className);
	}

	public static List<ExportImportContentProcessor<String>>
		getExportImportContentProcessors() {

		return ListUtil.fromCollection(_serviceTrackerMap.values());
	}

	private static final ServiceTrackerMap
		<String, ExportImportContentProcessor<String>> _serviceTrackerMap;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			ExportImportContentProcessorRegistryUtil.class);

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundle.getBundleContext(),
			(Class<ExportImportContentProcessor<String>>)
				(Class<?>)ExportImportContentProcessor.class,
			"model.class.name");
	}

}
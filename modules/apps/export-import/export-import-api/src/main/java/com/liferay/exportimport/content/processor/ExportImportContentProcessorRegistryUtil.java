/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.content.processor;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.osgi.util.StringPlus;
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

		ServiceTrackerMap<String, ExportImportContentProcessor<String>>
			serviceTrackerMap =
				_exportImportContentProcessorRegistryUtil._serviceTrackerMap;

		return serviceTrackerMap.getService(className);
	}

	public static ExportImportContentProcessor<String>
		getExportImportContentProcessorByContentProcessorType(
			String contentProcessorType) {

		ServiceTrackerMap<String, ExportImportContentProcessor<String>>
			serviceTrackerMap =
				_exportImportContentProcessorRegistryUtil._serviceTrackerMap;

		return serviceTrackerMap.getService(contentProcessorType);
	}

	public static List<ExportImportContentProcessor<String>>
		getExportImportContentProcessors() {

		ServiceTrackerMap<String, ExportImportContentProcessor<String>>
			serviceTrackerMap =
				_exportImportContentProcessorRegistryUtil._serviceTrackerMap;

		return ListUtil.fromCollection(serviceTrackerMap.values());
	}

	private ExportImportContentProcessorRegistryUtil() {
		Bundle bundle = FrameworkUtil.getBundle(
			ExportImportContentProcessorRegistryUtil.class);

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundle.getBundleContext(),
			(Class<ExportImportContentProcessor<String>>)
				(Class<?>)ExportImportContentProcessor.class,
			null, _serviceReferenceMapper);
	}

	private static final ExportImportContentProcessorRegistryUtil
		_exportImportContentProcessorRegistryUtil =
			new ExportImportContentProcessorRegistryUtil();

	private final ServiceReferenceMapper
		<String, ExportImportContentProcessor<String>> _serviceReferenceMapper =
			(serviceReference, emitter) -> {
				List<String> modelClassNames = StringPlus.asList(
					serviceReference.getProperty("model.class.name"));

				for (String modelClassName : modelClassNames) {
					emitter.emit(modelClassName);
				}

				List<String> contentProcessorTypes = StringPlus.asList(
					serviceReference.getProperty("content.processor.type"));

				for (String contentProcessorType : contentProcessorTypes) {
					emitter.emit(contentProcessorType);
				}
			};

	private final ServiceTrackerMap
		<String, ExportImportContentProcessor<String>> _serviceTrackerMap;

}
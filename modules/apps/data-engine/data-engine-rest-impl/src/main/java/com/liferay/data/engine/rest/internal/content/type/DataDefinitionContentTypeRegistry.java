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

package com.liferay.data.engine.rest.internal.content.type;

import com.liferay.data.engine.content.type.DataDefinitionContentType;
import com.liferay.data.engine.rest.resource.exception.DataDefinitionValidationException;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Leonardo Barros
 */
@Component(service = DataDefinitionContentTypeRegistry.class)
public class DataDefinitionContentTypeRegistry {

	public Long getClassNameId(String contentType) {
		DataDefinitionContentType dataDefinitionContentType =
			_serviceTrackerMap.getService(contentType);

		if (dataDefinitionContentType == null) {
			throw new DataDefinitionValidationException.MustSetValidContentType(
				contentType);
		}

		return dataDefinitionContentType.getClassNameId();
	}

	public DataDefinitionContentType getDataDefinitionContentType(
		long classNameId) {

		return _dataDefinitionContentTypesByClassNameId.get(classNameId);
	}

	public DataDefinitionContentType getDataDefinitionContentType(
		String contentType) {

		return Optional.ofNullable(
			_serviceTrackerMap.getService(contentType)
		).orElseThrow(
			() -> new DataDefinitionValidationException.MustSetValidContentType(
				contentType)
		);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, DataDefinitionContentType.class, "(content.type=*)",
			(serviceReference, emitter) -> emitter.emit(
				GetterUtil.getString(
					serviceReference.getProperty("content.type"))),
			new ServiceTrackerCustomizer
				<DataDefinitionContentType, DataDefinitionContentType>() {

				@Override
				public DataDefinitionContentType addingService(
					ServiceReference<DataDefinitionContentType>
						serviceReference) {

					DataDefinitionContentType dataDefinitionContentType =
						bundleContext.getService(serviceReference);

					_dataDefinitionContentTypesByClassNameId.put(
						dataDefinitionContentType.getClassNameId(),
						dataDefinitionContentType);

					return dataDefinitionContentType;
				}

				@Override
				public void modifiedService(
					ServiceReference<DataDefinitionContentType>
						serviceReference,
					DataDefinitionContentType dataDefinitionContentType) {
				}

				@Override
				public void removedService(
					ServiceReference<DataDefinitionContentType>
						serviceReference,
					DataDefinitionContentType dataDefinitionContentType) {

					_dataDefinitionContentTypesByClassNameId.remove(
						dataDefinitionContentType.getClassNameId());

					bundleContext.ungetService(serviceReference);
				}

			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private final Map<Long, DataDefinitionContentType>
		_dataDefinitionContentTypesByClassNameId = new ConcurrentHashMap<>();
	private ServiceTrackerMap<String, DataDefinitionContentType>
		_serviceTrackerMap;

}
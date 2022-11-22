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
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Leonardo Barros
 */
@Component(service = DataDefinitionContentTypeRegistry.class)
public class DataDefinitionContentTypeRegistry {

	public Long getClassNameId(String contentType) {
		DataDefinitionContentType dataDefinitionContentType =
			_dataDefinitionContentTypesByContentType.get(contentType);

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
			_dataDefinitionContentTypesByContentType.get(contentType)
		).orElseThrow(
			() -> new DataDefinitionValidationException.MustSetValidContentType(
				contentType)
		);
	}

	@Activate
	protected void activate(BundleContext bundleContext)
		throws InvalidSyntaxException {

		String filterString =
			"(&(content.type=*)(objectClass=" +
				DataDefinitionContentType.class.getName() + "))";

		_serviceTracker = new ServiceTracker<>(
			bundleContext, bundleContext.createFilter(filterString),
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

					_dataDefinitionContentTypesByContentType.put(
						GetterUtil.getString("content.type"),
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

					_dataDefinitionContentTypesByContentType.remove(
						GetterUtil.getString("content.type"));

					bundleContext.ungetService(serviceReference);
				}

			});

		_serviceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_dataDefinitionContentTypesByContentType.clear();

		_serviceTracker.close();
	}

	private final Map<Long, DataDefinitionContentType>
		_dataDefinitionContentTypesByClassNameId = new TreeMap<>();
	private final Map<String, DataDefinitionContentType>
		_dataDefinitionContentTypesByContentType = new TreeMap<>();
	private ServiceTracker<DataDefinitionContentType, DataDefinitionContentType>
		_serviceTracker;

}
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
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Leonardo Barros
 */
@Component(service = DataDefinitionContentTypeTracker.class)
public class DataDefinitionContentTypeTracker {

	public Long getClassNameId(String contentType) {
		return Optional.ofNullable(
			_classNameIds.get(contentType)
		).orElseThrow(
			() -> new DataDefinitionValidationException.MustSetValidContentType(
				contentType)
		);
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
	protected void activate(BundleContext bundleContext) {
		_serviceTracker =
			new ServiceTracker
				<DataDefinitionContentType, DataDefinitionContentType>(
					bundleContext, DataDefinitionContentType.class,
					new ServiceTrackerCustomizer
						<DataDefinitionContentType,
						 DataDefinitionContentType>() {

						@Override
						public DataDefinitionContentType addingService(
							ServiceReference<DataDefinitionContentType>
								serviceReference) {

							DataDefinitionContentType
								dataDefinitionContentType =
									bundleContext.getService(serviceReference);

							if (!ArrayUtil.contains(
									serviceReference.getPropertyKeys(),
									"content.type")) {

								return dataDefinitionContentType;
							}

							String contentType = MapUtil.getString(
								Collections.singletonMap(
									_key, serviceReference.getProperty(_key)),
								_key);

							_classNameIds.put(
								contentType,
								dataDefinitionContentType.getClassNameId());

							_dataDefinitionContentTypesByClassNameId.put(
								dataDefinitionContentType.getClassNameId(),
								dataDefinitionContentType);

							_dataDefinitionContentTypesByContentType.put(
								contentType, dataDefinitionContentType);

							return dataDefinitionContentType;
						}

						@Override
						public void modifiedService(
							ServiceReference<DataDefinitionContentType>
								serviceReference,
							DataDefinitionContentType service) {
						}

						@Override
						public void removedService(
							ServiceReference<DataDefinitionContentType>
								serviceReference,
							DataDefinitionContentType service) {

							String contentType = MapUtil.getString(
								Collections.singletonMap(
									_key, serviceReference.getProperty(_key)),
								_key);

							_dataDefinitionContentTypesByClassNameId.remove(
								_classNameIds.get(contentType));

							_classNameIds.remove(contentType);
							_dataDefinitionContentTypesByContentType.remove(
								contentType);

							bundleContext.ungetService(serviceReference);
						}

						private final String _key = "content.type";

					});

		_serviceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_dataDefinitionContentTypesByContentType.clear();
		_serviceTracker.close();
	}

	private final Map<String, Long> _classNameIds = new TreeMap<>();
	private final Map<Long, DataDefinitionContentType>
		_dataDefinitionContentTypesByClassNameId = new TreeMap<>();
	private final Map<String, DataDefinitionContentType>
		_dataDefinitionContentTypesByContentType = new TreeMap<>();
	private ServiceTracker<DataDefinitionContentType, DataDefinitionContentType>
		_serviceTracker;

}
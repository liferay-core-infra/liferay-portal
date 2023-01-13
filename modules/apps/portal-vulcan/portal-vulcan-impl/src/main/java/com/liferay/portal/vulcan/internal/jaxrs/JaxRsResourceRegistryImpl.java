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

package com.liferay.portal.vulcan.internal.jaxrs;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory.ServiceWrapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.vulcan.jaxrs.JaxRsResourceRegistry;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

/**
 * @author Carlos Correa
 */
@Component(service = JaxRsResourceRegistry.class)
public class JaxRsResourceRegistryImpl implements JaxRsResourceRegistry {

	@Override
	public Object getPropertyValue(String className, String propertyName) {
		ServiceWrapper<Object> serviceWrapper = _serviceTrackerMap.getService(
			className);

		if (serviceWrapper == null) {
			return null;
		}

		Map<String, Object> properties = serviceWrapper.getProperties();

		return properties.get(propertyName);
	}

	@Activate
	protected void activate(BundleContext bundleContext)
		throws InvalidSyntaxException {

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, null,
			"(" + JaxrsWhiteboardConstants.JAX_RS_RESOURCE + "=true)",
			(serviceReference, emitter) -> {
				Object object = bundleContext.getService(serviceReference);

				Class<?> clazz = object.getClass();

				emitter.emit(clazz.getName());

				bundleContext.ungetService(serviceReference);
			},
			ServiceTrackerCustomizerFactory.<Object>serviceWrapper(
				bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private ServiceTrackerMap<String, ServiceWrapper<Object>>
		_serviceTrackerMap;

}
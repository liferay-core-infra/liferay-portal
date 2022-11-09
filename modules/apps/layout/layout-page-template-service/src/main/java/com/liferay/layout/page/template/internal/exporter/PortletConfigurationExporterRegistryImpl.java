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

package com.liferay.layout.page.template.internal.exporter;

import com.liferay.layout.page.template.exporter.PortletConfigurationExporter;
import com.liferay.layout.page.template.exporter.PortletConfigurationExporterRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Jürgen Kappler
 */
@Component(service = PortletConfigurationExporterRegistry.class)
public class PortletConfigurationExporterRegistryImpl
	implements PortletConfigurationExporterRegistry {

	@Override
	public PortletConfigurationExporter getPortletConfigurationExporter(
		String portletName) {

		return _serviceTrackerMap.getService(portletName);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, PortletConfigurationExporter.class, null,
			ServiceReferenceMapperFactory.create(
				bundleContext,
				(portletConfigurationExporter, emitter) -> emitter.emit(
					portletConfigurationExporter.getPortletName())));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private ServiceTrackerMap<String, PortletConfigurationExporter>
		_serviceTrackerMap;

}
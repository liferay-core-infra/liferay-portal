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

package com.liferay.oauth2.provider.jsonws.internal.scope.spi.scope.finder;

import com.liferay.oauth2.provider.jsonws.internal.service.access.policy.scope.SAPEntryScopeDescriptorFinderRegistrator;
import com.liferay.oauth2.provider.scope.spi.scope.finder.ScopeFinder;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Jiaxu Wei
 */
@Component(service = ScopeFinderRegistry.class)
public class ScopeFinderRegistry {

	public List<String> getJaxRsApplicationNames() {
		return _serviceTrackerList.toList();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, ScopeFinder.class,
			"(&(osgi.jaxrs.name=*)(sap.scope.finder=true))",
			new ServiceTrackerCustomizer<ScopeFinder, String>() {

				@Override
				public String addingService(
					ServiceReference<ScopeFinder> serviceReference) {

					String jaxRsApplicationName = GetterUtil.getString(
						serviceReference.getProperty("osgi.jaxrs.name"));

					_sapEntryScopeDescriptorFinderRegistrator.
						addJaxRsApplicationNames(jaxRsApplicationName);

					return jaxRsApplicationName;
				}

				@Override
				public void modifiedService(
					ServiceReference<ScopeFinder> serviceReference,
					String jaxRsApplicationName) {
				}

				@Override
				public void removedService(
					ServiceReference<ScopeFinder> serviceReference,
					String jaxRsApplicationName) {

					_sapEntryScopeDescriptorFinderRegistrator.
						removeJaxRsApplicationNames(jaxRsApplicationName);
				}

			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	@Reference
	private SAPEntryScopeDescriptorFinderRegistrator
		_sapEntryScopeDescriptorFinderRegistrator;

	private ServiceTrackerList<String> _serviceTrackerList;

}
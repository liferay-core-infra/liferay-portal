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

package com.liferay.wiki.web.internal.importer;

import com.liferay.wiki.importer.WikiImporter;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Iván Zaera
 */
@Component(immediate = true, service = WikiImporterRegistry.class)
public class WikiImporterRegistry {

	public Collection<String> getImporters() {
		return _serviceReferences.keySet();
	}

	public String getProperty(String importer, String key) {
		ServiceReference<WikiImporter> serviceReference =
			_serviceReferences.get(importer);

		return (String)serviceReference.getProperty(key);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = new ServiceTracker<>(
			bundleContext, WikiImporter.class,
			new ServiceTrackerCustomizer<WikiImporter, WikiImporter>() {

				@Override
				public WikiImporter addingService(
					ServiceReference<WikiImporter> serviceReference) {

					String format = (String)serviceReference.getProperty(
						"importer");

					_serviceReferences.put(format, serviceReference);

					return bundleContext.getService(serviceReference);
				}

				@Override
				public void modifiedService(
					ServiceReference<WikiImporter> serviceReference,
					WikiImporter wikiImporter) {

					removedService(serviceReference, wikiImporter);

					addingService(serviceReference);
				}

				@Override
				public void removedService(
					ServiceReference<WikiImporter> serviceReference,
					WikiImporter wikiImporter) {

					String importer = (String)serviceReference.getProperty(
						"importer");

					_serviceReferences.remove(importer);

					bundleContext.ungetService(serviceReference);
				}

			});

		_serviceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_serviceReferences.clear();
		_serviceTracker.close();
	}

	private final ConcurrentMap<String, ServiceReference<WikiImporter>>
		_serviceReferences = new ConcurrentSkipListMap<>();
	private ServiceTracker<WikiImporter, WikiImporter> _serviceTracker;

}
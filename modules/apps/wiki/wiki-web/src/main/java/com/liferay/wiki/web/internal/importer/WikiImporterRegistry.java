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

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.wiki.importer.WikiImporter;

import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Iván Zaera
 */
@Component(immediate = true, service = WikiImporterRegistry.class)
public class WikiImporterRegistry {

	public Collection<String> getImporters() {
		return _serviceTrackerMap.keySet();
	}

	public WikiImporter getWikiImporter(String importer) {
		return _serviceTrackerMap.getService(importer);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, WikiImporter.class, null,
			ServiceReferenceMapperFactory.create(
				bundleContext,
				(wikiImporter, emitter) -> emitter.emit(
					wikiImporter.getImporterName())));
	}

	private ServiceTrackerMap<String, WikiImporter> _serviceTrackerMap;

}
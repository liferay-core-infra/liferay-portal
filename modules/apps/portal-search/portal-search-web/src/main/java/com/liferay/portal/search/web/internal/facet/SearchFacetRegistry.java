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

package com.liferay.portal.search.web.internal.facet;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.search.web.facet.SearchFacet;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Eudaldo Alonso
 */
@Component(immediate = true, service = SearchFacetRegistry.class)
public class SearchFacetRegistry {

	public List<SearchFacet> getSearchFacets() {
		return _searchFacets;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext, SearchFacet.class,
			new ServiceTrackerCustomizer<SearchFacet, SearchFacet>() {

				@Override
				public SearchFacet addingService(
					ServiceReference<SearchFacet> serviceReference) {

					SearchFacet searchFacet = bundleContext.getService(
						serviceReference);

					_searchFacets.add(searchFacet);

					return searchFacet;
				}

				@Override
				public void modifiedService(
					ServiceReference<SearchFacet> serviceReference,
					SearchFacet service) {
				}

				@Override
				public void removedService(
					ServiceReference<SearchFacet> serviceReference,
					SearchFacet service) {

					_searchFacets.remove(service);

					bundleContext.ungetService(serviceReference);
				}

			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private final List<SearchFacet> _searchFacets =
		new CopyOnWriteArrayList<>();
	private ServiceTracker<SearchFacet, SearchFacet> _serviceTracker;

}
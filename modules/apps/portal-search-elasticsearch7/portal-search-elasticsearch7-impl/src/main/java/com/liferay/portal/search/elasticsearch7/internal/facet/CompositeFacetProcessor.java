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

package com.liferay.portal.search.elasticsearch7.internal.facet;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.search.facet.Facet;

import java.util.Optional;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

/**
 * @author Michael C. Han
 */
@Component(
	immediate = true,
	service = {CompositeFacetProcessor.class, FacetProcessor.class}
)
public class CompositeFacetProcessor
	implements FacetProcessor<SearchRequestBuilder> {

	@Override
	public Optional<AggregationBuilder> processFacet(Facet facet) {
		Class<?> clazz = facet.getClass();

		FacetProcessor<SearchRequestBuilder> facetProcessor =
			_serviceTrackerMap.getService(clazz.getName());

		if (facetProcessor == null) {
			facetProcessor = defaultFacetProcessor;
		}

		return facetProcessor.processFacet(facet);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext,
			(Class<FacetProcessor<SearchRequestBuilder>>)
				(Class<?>)FacetProcessor.class,
			"(&(class.name=*)(!(class.name=DEFAULT)))",
			(facetProcessor, emitter) -> emitter.emit(
				(String)facetProcessor.getProperty("class.name")));
	}

	@Reference(
		cardinality = ReferenceCardinality.MANDATORY,
		target = "(class.name=DEFAULT)"
	)
	protected FacetProcessor<SearchRequestBuilder> defaultFacetProcessor;

	private ServiceTrackerMap<String, FacetProcessor<SearchRequestBuilder>>
		_serviceTrackerMap;

}
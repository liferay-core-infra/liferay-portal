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

import com.liferay.portal.kernel.search.facet.Facet;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Michael C. Han
 */
@Component(service = {CompositeFacetProcessor.class, FacetProcessor.class})
public class CompositeFacetProcessor
	implements FacetProcessor<SearchRequestBuilder> {

	@Override
	public String getFacetClassName() {
		return null;
	}

	@Override
	public Optional<AggregationBuilder> processFacet(Facet facet) {
		Class<?> clazz = facet.getClass();

		FacetProcessor<SearchRequestBuilder> facetProcessor =
			_facetProcessors.get(clazz.getName());

		if (facetProcessor == null) {
			facetProcessor = defaultFacetProcessor;
		}

		return facetProcessor.processFacet(facet);
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected void setFacetProcessor(
		FacetProcessor<SearchRequestBuilder> facetProcessor) {

		String facetClassName = facetProcessor.getFacetClassName();

		if (facetClassName != null) {
			_facetProcessors.put(facetClassName, facetProcessor);
		}
	}

	protected void unsetFacetProcessor(
		FacetProcessor<SearchRequestBuilder> facetProcessor) {

		String facetClassName = facetProcessor.getFacetClassName();

		if (facetClassName != null) {
			_facetProcessors.remove(facetClassName, facetProcessor);
		}
	}

	@Reference(service = DefaultFacetProcessor.class)
	protected FacetProcessor<SearchRequestBuilder> defaultFacetProcessor;

	private final Map<String, FacetProcessor<SearchRequestBuilder>>
		_facetProcessors = new HashMap<>();

}
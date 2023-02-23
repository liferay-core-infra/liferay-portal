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

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.IncludeExclude;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

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
	public AggregationBuilder processFacet(Facet facet) {
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
			null,
			ServiceReferenceMapperFactory.create(
				bundleContext,
				(facetProcessor, emitter) -> {
					String facetClassName = facetProcessor.getFacetClassName();

					if (facetClassName != null) {
						emitter.emit(facetClassName);
					}
				}));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	protected FacetProcessor<SearchRequestBuilder> defaultFacetProcessor =
		new FacetProcessor<SearchRequestBuilder>() {

			@Override
			public String getFacetClassName() {
				return null;
			}

			@Override
			public AggregationBuilder processFacet(Facet facet) {
				TermsAggregationBuilder termsAggregationBuilder =
					AggregationBuilders.terms(
						FacetUtil.getAggregationName(facet));

				termsAggregationBuilder.field(facet.getFieldName());

				FacetConfiguration facetConfiguration =
					facet.getFacetConfiguration();

				JSONObject dataJSONObject = facetConfiguration.getData();

				String include = dataJSONObject.getString("include", null);

				if (include != null) {
					termsAggregationBuilder.includeExclude(
						new IncludeExclude(include, null));
				}

				int minDocCount = dataJSONObject.getInt("frequencyThreshold");

				if (minDocCount > 0) {
					termsAggregationBuilder.minDocCount(minDocCount);
				}

				termsAggregationBuilder.order(BucketOrder.count(false));

				int size = dataJSONObject.getInt("maxTerms");

				if (size > 0) {
					termsAggregationBuilder.size(size);
				}

				return termsAggregationBuilder;
			}

		};

	private ServiceTrackerMap<String, FacetProcessor<SearchRequestBuilder>>
		_serviceTrackerMap;

}
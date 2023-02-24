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

package com.liferay.portal.search.solr8.internal.facet;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = {CompositeFacetProcessor.class, FacetProcessor.class})
public class CompositeFacetProcessor implements FacetProcessor<SolrQuery> {

	@Override
	public String getFacetClassName() {
		return null;
	}

	@Override
	public Map<String, JSONObject> processFacet(Facet facet) {
		Class<?> clazz = facet.getClass();

		FacetProcessor<SolrQuery> facetProcessor =
			_serviceTrackerMap.getService(clazz.getName());

		if (facetProcessor == null) {
			facetProcessor = _defaultFacetProcessor;
		}

		return facetProcessor.processFacet(facet);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext,
			(Class<FacetProcessor<SolrQuery>>)(Class<?>)FacetProcessor.class,
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

	private final FacetProcessor<SolrQuery> _defaultFacetProcessor =
		new FacetProcessor<SolrQuery>() {

			@Override
			public String getFacetClassName() {
				return null;
			}

			@Override
			public Map<String, JSONObject> processFacet(Facet facet) {
				return LinkedHashMapBuilder.<String, JSONObject>put(
					FacetUtil.getAggregationName(facet),
					_getFacetParametersJSONObject(facet)
				).build();
			}

			private JSONObject _getFacetParametersJSONObject(Facet facet) {
				JSONObject jsonObject = _jsonFactory.createJSONObject();

				jsonObject.put(
					"field", facet.getFieldName()
				).put(
					"type", "terms"
				);

				FacetConfiguration facetConfiguration =
					facet.getFacetConfiguration();

				JSONObject dataJSONObject = facetConfiguration.getData();

				int minCount = dataJSONObject.getInt("frequencyThreshold");

				if (minCount > 0) {
					jsonObject.put("mincount", minCount);
				}

				int limit = dataJSONObject.getInt("maxTerms");

				if (limit > 0) {
					jsonObject.put("limit", limit);
				}

				String sortParam = "count";
				String sortValue = "desc";

				String order = facetConfiguration.getOrder();

				if (order.equals("OrderValueAsc")) {
					sortParam = "index";
					sortValue = "asc";
				}

				JSONObject sortJSONObject = _jsonFactory.createJSONObject();

				sortJSONObject.put(sortParam, sortValue);

				jsonObject.put("sort", sortJSONObject);

				return jsonObject;
			}

		};

	@Reference
	private JSONFactory _jsonFactory;

	private ServiceTrackerMap<String, FacetProcessor<SolrQuery>>
		_serviceTrackerMap;

}
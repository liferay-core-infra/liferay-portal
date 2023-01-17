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

package com.liferay.portal.search.internal.hits;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.hits.HitsProcessor;
import com.liferay.portal.kernel.search.hits.HitsProcessorRegistry;
import com.liferay.portal.kernel.util.Validator;

import java.util.Comparator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Michael C. Han
 */
@Component(service = HitsProcessorRegistry.class)
public class HitsProcessorRegistryImpl implements HitsProcessorRegistry {

	@Override
	public boolean process(SearchContext searchContext, Hits hits)
		throws SearchException {

		if ((_serviceTrackerList.size() == 0) ||
			Validator.isNull(searchContext.getKeywords())) {

			return false;
		}

		QueryConfig queryConfig = searchContext.getQueryConfig();

		if (!queryConfig.isHitsProcessingEnabled()) {
			return false;
		}

		for (HitsProcessor hitsProcessor : _serviceTrackerList) {
			if (!hitsProcessor.process(searchContext, hits)) {
				break;
			}
		}

		return true;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, HitsProcessor.class,
			new HitsProcessorServiceReferenceComparator(bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	private ServiceTrackerList<HitsProcessor> _serviceTrackerList;

	private class HitsProcessorServiceReferenceComparator
		implements Comparator<ServiceReference<HitsProcessor>> {

		public HitsProcessorServiceReferenceComparator(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		@Override
		public int compare(
			ServiceReference<HitsProcessor> serviceReference1,
			ServiceReference<HitsProcessor> serviceReference2) {

			if (serviceReference1 == null) {
				if (serviceReference2 == null) {
					return 0;
				}

				return -1;
			}
			else if (serviceReference2 == null) {
				return 1;
			}

			try {
				HitsProcessor hitsProcessor1 = _bundleContext.getService(
					serviceReference1);

				HitsProcessor hitsProcessor2 = _bundleContext.getService(
					serviceReference2);

				Integer sortOrder1 = hitsProcessor1.getSortOrder();

				Integer sortOrder2 = hitsProcessor2.getSortOrder();

				if (sortOrder1 == null) {
					if (sortOrder2 == null) {
						return 0;
					}

					return -1;
				}
				else if (sortOrder2 == null) {
					return 1;
				}

				return sortOrder1.compareTo(sortOrder2);
			}
			finally {
				_bundleContext.ungetService(serviceReference1);

				_bundleContext.ungetService(serviceReference2);
			}
		}

		private final BundleContext _bundleContext;

	}

}
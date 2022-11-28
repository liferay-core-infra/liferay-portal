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

import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.hits.HitsProcessor;
import com.liferay.portal.kernel.search.hits.HitsProcessorRegistry;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Michael C. Han
 */
@Component(immediate = true, service = HitsProcessorRegistry.class)
public class HitsProcessorRegistryImpl implements HitsProcessorRegistry {

	@Override
	public boolean process(SearchContext searchContext, Hits hits)
		throws SearchException {

		if (_hitsProcessors.isEmpty() ||
			Validator.isNull(searchContext.getKeywords())) {

			return false;
		}

		QueryConfig queryConfig = searchContext.getQueryConfig();

		if (!queryConfig.isHitsProcessingEnabled()) {
			return false;
		}

		for (HitsProcessor hitsProcessor : _hitsProcessors) {
			if (!hitsProcessor.process(searchContext, hits)) {
				break;
			}
		}

		return true;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = new ServiceTracker<HitsProcessor, HitsProcessor>(
			bundleContext, HitsProcessor.class,
			new ServiceTrackerCustomizer<HitsProcessor, HitsProcessor>() {

				@Override
				public HitsProcessor addingService(
					ServiceReference<HitsProcessor> serviceReference) {

					HitsProcessor hitsProcessor = bundleContext.getService(
						serviceReference);

					String sortOrderString =
						(String)serviceReference.getProperty("sort.order");

					Integer sortOrder = null;

					if (Validator.isNotNull(sortOrderString)) {
						sortOrder = GetterUtil.getInteger(sortOrderString);
					}

					SortableHitsProcessor sortableHitsProcessor =
						new SortableHitsProcessor(hitsProcessor, sortOrder);

					_hitsProcessors.add(sortableHitsProcessor);

					return hitsProcessor;
				}

				@Override
				public void modifiedService(
					ServiceReference<HitsProcessor> serviceReference,
					HitsProcessor hitsProcessor) {
				}

				@Override
				public void removedService(
					ServiceReference<HitsProcessor> serviceReference,
					HitsProcessor hitsProcessor) {

					String sortOrderString =
						(String)serviceReference.getProperty("sort.order");

					Integer sortOrder = null;

					if (Validator.isNotNull(sortOrderString)) {
						sortOrder = GetterUtil.getInteger(sortOrderString);
					}

					SortableHitsProcessor sortableHitsProcessor =
						new SortableHitsProcessor(hitsProcessor, sortOrder);

					_hitsProcessors.remove(sortableHitsProcessor);

					bundleContext.ungetService(serviceReference);
				}

			});

		_serviceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private final Set<SortableHitsProcessor> _hitsProcessors =
		new ConcurrentSkipListSet<>();
	private ServiceTracker<HitsProcessor, HitsProcessor> _serviceTracker;

	private static class SortableHitsProcessor
		implements Comparable<SortableHitsProcessor>, HitsProcessor {

		public SortableHitsProcessor(
			HitsProcessor hitsProcessor, Integer sortOrder) {

			_hitsProcessor = hitsProcessor;
			_sortOrder = sortOrder;
		}

		@Override
		public int compareTo(SortableHitsProcessor sortableHitsProcessor) {
			if ((sortableHitsProcessor._sortOrder == null) &&
				(_sortOrder != null)) {

				return 1;
			}
			else if ((sortableHitsProcessor._sortOrder != null) &&
					 (_sortOrder == null)) {

				return -1;
			}
			else if ((sortableHitsProcessor._sortOrder == null) &&
					 (_sortOrder == null)) {

				return 0;
			}

			return _sortOrder.compareTo(sortableHitsProcessor._sortOrder);
		}

		@Override
		public boolean equals(Object object) {
			SortableHitsProcessor sortableHitsProcessor =
				(SortableHitsProcessor)object;

			return sortableHitsProcessor._hitsProcessor.equals(_hitsProcessor);
		}

		@Override
		public int hashCode() {
			return _hitsProcessor.hashCode();
		}

		@Override
		public boolean process(SearchContext searchContext, Hits hits)
			throws SearchException {

			return _hitsProcessor.process(searchContext, hits);
		}

		private final HitsProcessor _hitsProcessor;
		private Integer _sortOrder;

	}

}
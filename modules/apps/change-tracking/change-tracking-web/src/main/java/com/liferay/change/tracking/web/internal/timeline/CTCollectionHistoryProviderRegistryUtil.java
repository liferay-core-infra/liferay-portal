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

package com.liferay.change.tracking.web.internal.timeline;

import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.spi.history.CTCollectionHistoryProvider;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.osgi.util.service.Snapshot;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ClassNameLocalService;

import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Noor Najjar
 */
public class CTCollectionHistoryProviderRegistry {

	public static CTCollectionHistoryProvider getCTCollectionHistoryProvider(
		long classNameId) {

		CTCollectionHistoryProvider<?> ctCollectionHistoryProvider =
			(CTCollectionHistoryProvider<?>)
				_ctCollectionHistoryProviderServiceTrackerMap.getService(
					classNameId);

		if (ctCollectionHistoryProvider == null) {
			return _defaultCTCollectionHistoryProvider;
		}

		return ctCollectionHistoryProvider;
	}

	public static List<CTCollection> getCTCollections(
			long classNameId, long classPK)
		throws PortalException {

		CTCollectionHistoryProvider<?> ctCollectionHistoryProvider =
			getCTCollectionHistoryProvider(classNameId);

		return ctCollectionHistoryProvider.getCTCollections(
			classNameId, classPK);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {

	}

	private static ServiceTrackerMap<Long, CTCollectionHistoryProvider<?>>
		_ctCollectionHistoryProviderServiceTrackerMap;
	private static CTCollectionHistoryProvider<?>
		_defaultCTCollectionHistoryProvider;

	static {

		Bundle bundle = FrameworkUtil.getBundle(
			CTCollectionHistoryProviderRegistry.class);

		BundleContext bundleContext = bundle.getBundleContext();


		_ctCollectionHistoryProviderServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext,
				(Class<CTCollectionHistoryProvider<?>>)
					(Class<?>)CTCollectionHistoryProvider.class,
				null,
				(serviceReference, emitter) -> {
					CTCollectionHistoryProvider<?> ctCollectionHistoryProvider =
						bundleContext.getService(serviceReference);

					try {
						emitter.emit(
							_classNameLocalService.getClassNameId(
								ctCollectionHistoryProvider.getModelClass()));
					}
					finally {
						bundleContext.ungetService(serviceReference);
					}
				});

		_defaultCTCollectionHistoryProvider =
			new DefaultCTCollectionHistoryProvider<>();
	}

}
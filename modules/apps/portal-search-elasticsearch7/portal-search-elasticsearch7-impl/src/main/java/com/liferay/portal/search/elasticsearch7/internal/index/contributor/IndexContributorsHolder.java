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

package com.liferay.portal.search.elasticsearch7.internal.index.contributor;

import com.liferay.osgi.service.tracker.collections.EagerServiceTrackerCustomizer;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.search.spi.model.index.contributor.IndexContributor;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Adam Brandizzi
 */
@Component(service = IndexContributorsHolder.class)
public class IndexContributorsHolder {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_indexContributorServiceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, IndexContributor.class, null,
			new EagerServiceTrackerCustomizer
				<IndexContributor, IndexContributor>() {

				@Override
				public IndexContributor addingService(
					ServiceReference<IndexContributor> serviceReference) {

					IndexContributor indexContributor =
						bundleContext.getService(serviceReference);

					if (_indexContributorReceiverServiceTrackerList == null) {
						return indexContributor;
					}

					for (IndexContributorReceiver indexContributorReceiver :
							_indexContributorReceiverServiceTrackerList) {

						indexContributorReceiver.addIndexContributor(
							indexContributor);
					}

					return indexContributor;
				}

				@Override
				public void modifiedService(
					ServiceReference<IndexContributor> serviceReference,
					IndexContributor indexContributor) {
				}

				@Override
				public void removedService(
					ServiceReference<IndexContributor> serviceReference,
					IndexContributor indexContributor) {

					bundleContext.ungetService(serviceReference);

					if (_indexContributorReceiverServiceTrackerList == null) {
						return;
					}

					for (IndexContributorReceiver indexContributorReceiver :
							_indexContributorReceiverServiceTrackerList) {

						indexContributorReceiver.removeIndexContributor(
							indexContributor);
					}
				}

			});

		_indexContributorReceiverServiceTrackerList =
			ServiceTrackerListFactory.open(
				bundleContext, IndexContributorReceiver.class, null,
				new EagerServiceTrackerCustomizer
					<IndexContributorReceiver, IndexContributorReceiver>() {

					@Override
					public IndexContributorReceiver addingService(
						ServiceReference<IndexContributorReceiver>
							serviceReference) {

						IndexContributorReceiver indexContributorReceiver =
							bundleContext.getService(serviceReference);

						for (IndexContributor indexContributor :
								_indexContributorServiceTrackerList) {

							indexContributorReceiver.addIndexContributor(
								indexContributor);
						}

						return indexContributorReceiver;
					}

					@Override
					public void modifiedService(
						ServiceReference<IndexContributorReceiver>
							serviceReference,
						IndexContributorReceiver indexContributorReceiver) {
					}

					@Override
					public void removedService(
						ServiceReference<IndexContributorReceiver>
							serviceReference,
						IndexContributorReceiver indexContributorReceiver) {

						bundleContext.ungetService(serviceReference);
					}

				});
	}

	@Deactivate
	protected void deactivate() {
		_indexContributorReceiverServiceTrackerList.close();

		_indexContributorServiceTrackerList.close();
	}

	private ServiceTrackerList<IndexContributorReceiver>
		_indexContributorReceiverServiceTrackerList;
	private ServiceTrackerList<IndexContributor>
		_indexContributorServiceTrackerList;

}
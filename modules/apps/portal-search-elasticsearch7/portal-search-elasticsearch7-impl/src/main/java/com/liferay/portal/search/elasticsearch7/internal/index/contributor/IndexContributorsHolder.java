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

import com.liferay.portal.search.spi.model.index.contributor.IndexContributor;

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
 * @author Adam Brandizzi
 */
@Component(service = IndexContributorsHolder.class)
public class IndexContributorsHolder {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_indexContributorReceiverServiceTracker =
			new ServiceTracker
				<IndexContributorReceiver, IndexContributorReceiver>(
					bundleContext, IndexContributorReceiver.class,
					new ServiceTrackerCustomizer
						<IndexContributorReceiver, IndexContributorReceiver>() {

						@Override
						public IndexContributorReceiver addingService(
							ServiceReference<IndexContributorReceiver>
								serviceReference) {

							IndexContributorReceiver indexContributorReceiver =
								bundleContext.getService(serviceReference);

							_indexContributorReceivers.add(
								indexContributorReceiver);

							for (IndexContributor indexContributor :
									_indexContributors) {

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

							_indexContributorReceivers.remove(
								indexContributorReceiver);

							bundleContext.ungetService(serviceReference);
						}

					});

		_indexContributorReceiverServiceTracker.open();

		_indexContributorServiceTracker =
			new ServiceTracker<IndexContributor, IndexContributor>(
				bundleContext, IndexContributor.class,
				new ServiceTrackerCustomizer
					<IndexContributor, IndexContributor>() {

					@Override
					public IndexContributor addingService(
						ServiceReference<IndexContributor> serviceReference) {

						IndexContributor indexContributor =
							bundleContext.getService(serviceReference);

						_indexContributors.add(indexContributor);

						for (IndexContributorReceiver indexContributorReceiver :
								_indexContributorReceivers) {

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

						_indexContributors.remove(indexContributor);

						for (IndexContributorReceiver indexContributorReceiver :
								_indexContributorReceivers) {

							indexContributorReceiver.removeIndexContributor(
								indexContributor);
						}

						bundleContext.ungetService(serviceReference);
					}

				});

		_indexContributorServiceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_indexContributorReceiverServiceTracker.close();

		_indexContributorServiceTracker.close();
	}

	private final List<IndexContributorReceiver> _indexContributorReceivers =
		new CopyOnWriteArrayList<>();
	private ServiceTracker<IndexContributorReceiver, IndexContributorReceiver>
		_indexContributorReceiverServiceTracker;
	private final List<IndexContributor> _indexContributors =
		new CopyOnWriteArrayList<>();
	private ServiceTracker<IndexContributor, IndexContributor>
		_indexContributorServiceTracker;

}
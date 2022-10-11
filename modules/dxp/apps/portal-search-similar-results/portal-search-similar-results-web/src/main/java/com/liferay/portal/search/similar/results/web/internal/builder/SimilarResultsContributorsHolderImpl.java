/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.search.similar.results.web.internal.builder;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.search.similar.results.web.spi.contributor.SimilarResultsContributor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author André de Oliveira
 */
@Component(immediate = true, service = SimilarResultsContributorsHolder.class)
public class SimilarResultsContributorsHolderImpl
	implements SimilarResultsContributorsHolder {

	@Override
	public Stream<SimilarResultsContributor> stream() {
		List<SimilarResultsContributor> similarResultsContributors =
			new ArrayList<>(_serviceTrackerList.size());

		for (SimilarResultsContributor similarResultsContributor :
				_serviceTrackerList) {

			similarResultsContributors.add(similarResultsContributor);
		}

		return similarResultsContributors.stream();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, SimilarResultsContributor.class);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	private ServiceTrackerList<SimilarResultsContributor> _serviceTrackerList;

}
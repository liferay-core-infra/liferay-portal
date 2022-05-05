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

package com.liferay.health.check.internal.service;

import com.liferay.health.check.configuration.HealthCheckSearchConfiguration;
import com.liferay.health.check.model.HealthCheckResponse;
import com.liferay.health.check.service.HealthCheckService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.cluster.ClusterHealthStatus;
import com.liferay.portal.search.engine.adapter.cluster.HealthClusterRequest;
import com.liferay.portal.search.engine.adapter.cluster.HealthClusterResponse;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Louis-Guillaume Durand
 */
@Component(
	configurationPid = HealthCheckSearchConfiguration.PID, immediate = true,
	service = HealthCheckService.class
)
public class SearchHealthCheckService implements HealthCheckService {

	@Override
	public HealthCheckResponse isLive() {
		return _checkSearchConnection();
	}

	@Override
	public HealthCheckResponse isReady() {
		return _checkSearchConnection();
	}

	@Activate
	@Modified
	protected void activate(Map<String, String> properties) {
		_healthCheckSearchConfiguration = ConfigurableUtil.createConfigurable(
			HealthCheckSearchConfiguration.class, properties);
	}

	private HealthCheckResponse _checkSearchConnection() {
		long timeout = _healthCheckSearchConfiguration.timeout();
		String statusUp = _healthCheckSearchConfiguration.statusUp();

		HealthClusterRequest healthClusterRequest = new HealthClusterRequest();

		healthClusterRequest.setTimeout(timeout);

		healthClusterRequest.setWaitForClusterHealthStatus(
			ClusterHealthStatus.valueOf(statusUp));

		HealthClusterResponse healthClusterResponse =
			_searchEngineAdapter.execute(healthClusterRequest);

		if ((healthClusterResponse.getClusterHealthStatus() ==
				ClusterHealthStatus.RED) ||
			((healthClusterResponse.getClusterHealthStatus() ==
				ClusterHealthStatus.YELLOW) &&
			 (ClusterHealthStatus.valueOf(statusUp) ==
				 ClusterHealthStatus.GREEN))) {

			String healthStatusMessage =
				healthClusterResponse.getHealthStatusMessage();

			_log.error("Failed with response: " + healthStatusMessage);

			return HealthCheckResponse.builder(
			).name(
				SearchHealthCheckService.class.getName()
			).down(
			).issues(
				Collections.singletonList(healthStatusMessage)
			).build();
		}

		return HealthCheckResponse.builder(
		).name(
			SearchHealthCheckService.class.getName()
		).up(
		).build();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SearchHealthCheckService.class);

	private volatile HealthCheckSearchConfiguration
		_healthCheckSearchConfiguration;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

}
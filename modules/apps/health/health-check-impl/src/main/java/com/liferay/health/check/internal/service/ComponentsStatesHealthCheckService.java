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

import com.liferay.health.check.configuration.HealthCheckLivenessConfiguration;
import com.liferay.health.check.configuration.HealthCheckReadinessConfiguration;
import com.liferay.health.check.model.HealthCheckResponse;
import com.liferay.health.check.service.HealthCheckService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.dm.ComponentDeclaration;
import org.apache.felix.dm.diagnostics.DependencyGraph;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Louis-Guillaume Durand
 */
@Component(
	configurationPid = {
		HealthCheckLivenessConfiguration.PID,
		HealthCheckReadinessConfiguration.PID
	},
	immediate = true, service = HealthCheckService.class
)
public class ComponentsStatesHealthCheckService implements HealthCheckService {

	@Override
	public HealthCheckResponse isLive() {
		if (_healthCheckLivenessConfiguration.ignoreOSGiStatesForLiveness()) {
			return HealthCheckResponse.builder(
			).name(
				ComponentsStatesHealthCheckService.class.getName()
			).up(
			).build();
		}

		return _verifyComponents();
	}

	@Override
	public HealthCheckResponse isReady() {
		if (_healthCheckReadinessConfiguration.ignoreOSGiStatesForReadiness()) {
			return HealthCheckResponse.builder(
			).name(
				ComponentsStatesHealthCheckService.class.getName()
			).up(
			).build();
		}

		return _verifyComponents();
	}

	@Activate
	@Modified
	protected void activate(Map<String, String> properties) {
		_healthCheckLivenessConfiguration = ConfigurableUtil.createConfigurable(
			HealthCheckLivenessConfiguration.class, properties);
		_healthCheckReadinessConfiguration =
			ConfigurableUtil.createConfigurable(
				HealthCheckReadinessConfiguration.class, properties);
	}

	/**
	 * Verify if there is any unregistered component.
	 *
	 * @return HTTP response corresponding to the result
	 * @see HealthCheckResponse
	 */
	private HealthCheckResponse _verifyComponents() {
		DependencyGraph graph = DependencyGraph.getGraph(
			DependencyGraph.ComponentState.UNREGISTERED,
			DependencyGraph.DependencyState.REQUIRED_UNAVAILABLE);

		List<ComponentDeclaration> unregisteredComponents =
			graph.getAllComponents();

		if (!unregisteredComponents.isEmpty()) {
			List<String> issues = new ArrayList<>();

			for (ComponentDeclaration componentDeclaration :
					unregisteredComponents) {

				BundleContext bundleContext =
					componentDeclaration.getBundleContext();

				if (bundleContext != null) {
					Bundle bundle = bundleContext.getBundle();

					if (bundle != null) {
						issues.add(
							StringBundler.concat(
								"Unregistered component ",
								componentDeclaration.getName(), " in bundle: ",
								bundle.getSymbolicName()));
					}
				}
			}

			return HealthCheckResponse.builder(
			).name(
				ComponentsStatesHealthCheckService.class.getName()
			).down(
			).issues(
				issues
			).build();
		}

		return HealthCheckResponse.builder(
		).name(
			ComponentsStatesHealthCheckService.class.getName()
		).up(
		).build();
	}

	private volatile HealthCheckLivenessConfiguration
		_healthCheckLivenessConfiguration;
	private volatile HealthCheckReadinessConfiguration
		_healthCheckReadinessConfiguration;

}
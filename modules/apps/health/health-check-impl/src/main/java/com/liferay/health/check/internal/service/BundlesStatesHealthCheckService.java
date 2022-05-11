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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.StringBundler;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.dm.diagnostics.CircularDependency;
import org.apache.felix.dm.diagnostics.DependencyGraph;
import org.apache.felix.dm.diagnostics.MissingDependency;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
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
public class BundlesStatesHealthCheckService implements HealthCheckService {

	@Override
	public HealthCheckResponse isLive() {
		if (_healthCheckLivenessConfiguration.ignoreOSGiStatesForLiveness()) {
			return HealthCheckResponse.builder(
			).name(
				BundlesStatesHealthCheckService.class.getName()
			).up(
			).build();
		}

		Map<String, String> bundlesData = _getBundlesData();

		if (bundlesData.isEmpty()) {
			return HealthCheckResponse.builder(
			).name(
				BundlesStatesHealthCheckService.class.getName()
			).up(
			).build();
		}

		return HealthCheckResponse.builder(
		).name(
			BundlesStatesHealthCheckService.class.getName()
		).down(
		).withData(
			bundlesData
		).build();
	}

	@Override
	public HealthCheckResponse isReady() {
		if (_healthCheckReadinessConfiguration.ignoreOSGiStatesForReadiness()) {
			return HealthCheckResponse.builder(
			).name(
				BundlesStatesHealthCheckService.class.getName()
			).up(
			).build();
		}

		Map<String, String> bundlesData = _getBundlesData();

		if (bundlesData.isEmpty()) {
			return HealthCheckResponse.builder(
			).name(
				BundlesStatesHealthCheckService.class.getName()
			).up(
			).build();
		}

		return HealthCheckResponse.builder(
		).name(
			BundlesStatesHealthCheckService.class.getName()
		).down(
		).withData(
			bundlesData
		).build();
	}

	@Activate
	@Modified
	protected void activate(
		BundleContext bundleContext, Map<String, String> properties) {

		_bundleContext = bundleContext;
		_healthCheckLivenessConfiguration = ConfigurableUtil.createConfigurable(
			HealthCheckLivenessConfiguration.class, properties);
		_healthCheckReadinessConfiguration =
			ConfigurableUtil.createConfigurable(
				HealthCheckReadinessConfiguration.class, properties);
	}

	// SPDX-SnippetBegin
	// SPDX-License-Identifier: Apache-2.0 License
	// SPDX-SnippetCopyrightText: © 2022 The Apache Software Foundation,
	// <https://felix.apache.org/>
	// SPDX-SnippetComment: This snippet includes the logic behind the command
	// "dm:wtf" from Apache Felix.
	// SPDX-ExternalRef: PACKAGE-MANAGER purl
	// pkg:github/apache/felix-dev@dccce6feb31e75b636bab7507dbfa832fbb723b4

	private Map<String, String> _getBundlesData() {
		Map<String, String> data = new HashMap<>();

		DependencyGraph graph = DependencyGraph.getGraph(
			DependencyGraph.ComponentState.UNREGISTERED,
			DependencyGraph.DependencyState.REQUIRED_UNAVAILABLE);

		List<String> resolvedBundles = _listResolvedBundles(
			_bundleContext.getBundles());
		List<String> installedBundles = _listInstalledBundles(
			_bundleContext.getBundles());

		resolvedBundles.forEach(bundle -> data.put(bundle, "RESOLVED"));
		installedBundles.forEach(bundle -> data.put(bundle, "INSTALLED"));

		List<CircularDependency> circularDependencies =
			graph.getCircularDependencies();

		if (!circularDependencies.isEmpty()) {
			List<String> circularDependenciesComponents =
				_getCircularDependenciesComponents(circularDependencies);

			circularDependenciesComponents.forEach(
				component -> data.put(component, "Circular dependency"));
		}

		List<MissingDependency> missingConfigDependencies =
			graph.getMissingDependencies("configuration");

		if (!missingConfigDependencies.isEmpty()) {
			data.putAll(_getMissingDependenciesData(missingConfigDependencies));
		}

		List<MissingDependency> missingServiceDependencies =
			graph.getMissingDependencies("service");

		if (!missingServiceDependencies.isEmpty()) {
			data.putAll(
				_getMissingDependenciesData(missingServiceDependencies));
		}

		List<MissingDependency> missingResourceDependencies =
			graph.getMissingDependencies("resource");

		if (!missingResourceDependencies.isEmpty()) {
			data.putAll(
				_getMissingDependenciesData(missingResourceDependencies));
		}

		List<MissingDependency> missingBundleDependencies =
			graph.getMissingDependencies("bundle");

		if (!missingBundleDependencies.isEmpty()) {
			data.putAll(_getMissingDependenciesData(missingBundleDependencies));
		}

		List<MissingDependency> missingCustomDependencies =
			graph.getMissingCustomDependencies();

		if (!missingCustomDependencies.isEmpty()) {
			data.putAll(
				_getMissingCustomDependenciesData(missingCustomDependencies));
		}

		return data;
	}

	private List<String> _getCircularDependenciesComponents(
		List<CircularDependency> circularDependencies) {

		List<String> components = new ArrayList<>();

		circularDependencies.forEach(
			circularDependency -> circularDependency.getComponents(
			).forEach(
				componentDeclaration -> components.add(
					componentDeclaration.getName())
			));

		return components;
	}

	private Map<String, String> _getMissingCustomDependenciesData(
		List<MissingDependency> missingDependencies) {

		Map<String, String> data = new HashMap<>();

		missingDependencies.forEach(
			missingDependency -> data.put(
				missingDependency.getBundleName(),
				StringBundler.concat(
					"Missing custom dependency ", missingDependency.getName(),
					" (", missingDependency.getType(), ")")));

		return data;
	}

	private Map<String, String> _getMissingDependenciesData(
		List<MissingDependency> missingDependencies) {

		Map<String, String> data = new HashMap<>();

		missingDependencies.forEach(
			missingDependency -> data.put(
				missingDependency.getBundleName(),
				"Missing dependency " + missingDependency.getName()));

		return data;
	}

	private boolean _isNotFragment(Bundle b) {
		Dictionary<String, String> headers = b.getHeaders(StringPool.BLANK);

		if (headers.get(Constants.FRAGMENT_HOST) == null) {
			return true;
		}

		return false;
	}

	private List<String> _listInstalledBundles(Bundle[] bundles) {
		List<String> installedBundles = new ArrayList<>();

		boolean areInstalled = false;

		for (Bundle b : bundles) {
			if (b.getState() == Bundle.INSTALLED) {
				areInstalled = true;

				break;
			}
		}

		if (areInstalled) {
			for (Bundle bundle : bundles) {
				if (bundle.getState() == Bundle.INSTALLED) {
					installedBundles.add(bundle.getSymbolicName());
				}
			}
		}

		return installedBundles;
	}

	private List<String> _listResolvedBundles(Bundle[] bundles) {
		List<String> resolveBundleNames = new ArrayList<>();

		boolean areResolved = false;

		for (Bundle bundle : bundles) {
			if ((bundle.getState() == Bundle.RESOLVED) &&
				_isNotFragment(bundle)) {

				areResolved = true;

				break;
			}
		}

		if (areResolved) {
			for (Bundle bundle : bundles) {
				if ((bundle.getState() == Bundle.RESOLVED) &&
					_isNotFragment(bundle)) {

					resolveBundleNames.add(bundle.getSymbolicName());
				}
			}
		}

		return resolveBundleNames;
	}

	// SPDX-SnippetEnd

	private volatile BundleContext _bundleContext;
	private volatile HealthCheckLivenessConfiguration
		_healthCheckLivenessConfiguration;
	private volatile HealthCheckReadinessConfiguration
		_healthCheckReadinessConfiguration;

}
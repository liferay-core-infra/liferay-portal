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
import java.util.List;
import java.util.Map;

import org.apache.felix.dm.diagnostics.CircularDependency;
import org.apache.felix.dm.diagnostics.DependencyGraph;
import org.apache.felix.dm.diagnostics.MissingDependency;

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

		List<String> bundlesIssues = _getBundlesIssues();

		if (bundlesIssues.isEmpty()) {
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
		).issues(
			bundlesIssues
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

		List<String> bundlesIssues = _getBundlesIssues();

		if (bundlesIssues.isEmpty()) {
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
		).issues(
			bundlesIssues
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
	// Original source code: https://github.com/apache/felix-dev

	private List<String> _getBundlesIssues() {
		List<String> issues = new ArrayList<>();

		DependencyGraph graph = DependencyGraph.getGraph(
			DependencyGraph.ComponentState.UNREGISTERED,
			DependencyGraph.DependencyState.REQUIRED_UNAVAILABLE);

		issues.addAll(_listResolvedBundles(_bundleContext.getBundles()));
		issues.addAll(_listInstalledBundles(_bundleContext.getBundles()));

		List<CircularDependency> circularDependencies =
			graph.getCircularDependencies();

		if (!circularDependencies.isEmpty()) {
			issues.addAll(_getCircularDependenciesIssues(circularDependencies));
		}

		List<MissingDependency> missingConfigDependencies =
			graph.getMissingDependencies("configuration");

		if (!missingConfigDependencies.isEmpty()) {
			issues.addAll(
				_getMissingDependenciesIssues(missingConfigDependencies));
		}

		List<MissingDependency> missingServiceDependencies =
			graph.getMissingDependencies("service");

		if (!missingServiceDependencies.isEmpty()) {
			issues.addAll(
				_getMissingDependenciesIssues(missingServiceDependencies));
		}

		List<MissingDependency> missingResourceDependencies =
			graph.getMissingDependencies("resource");

		if (!missingResourceDependencies.isEmpty()) {
			issues.addAll(
				_getMissingDependenciesIssues(missingResourceDependencies));
		}

		List<MissingDependency> missingBundleDependencies =
			graph.getMissingDependencies("bundle");

		if (!missingBundleDependencies.isEmpty()) {
			issues.addAll(
				_getMissingDependenciesIssues(missingBundleDependencies));
		}

		List<MissingDependency> missingCustomDependencies =
			graph.getMissingCustomDependencies();

		if (!missingCustomDependencies.isEmpty()) {
			issues.addAll(
				_getMissingCustomDependenciesIssues(missingCustomDependencies));
		}

		return issues;
	}

	private List<String> _getCircularDependenciesIssues(
		List<CircularDependency> circularDependencies) {

		List<String> issues = new ArrayList<>();

		circularDependencies.forEach(
			circularDependency -> circularDependency.getComponents(
			).forEach(
				componentDeclaration -> issues.add(
					componentDeclaration.getName())
			));

		return issues;
	}

	private List<String> _getMissingCustomDependenciesIssues(
		List<MissingDependency> missingDependencies) {

		List<String> issues = new ArrayList<>();

		missingDependencies.forEach(
			missingDependency -> issues.add(
				StringBundler.concat(
					"Missing custom dependency ", missingDependency.getName(),
					"(", missingDependency.getType(), ") for bundle ",
					missingDependency.getBundleName())));

		return issues;
	}

	private List<String> _getMissingDependenciesIssues(
		List<MissingDependency> missingDependencies) {

		List<String> issues = new ArrayList<>();

		missingDependencies.forEach(
			missingDependency -> issues.add(
				StringBundler.concat(
					"Missing dependency ", missingDependency.getName(),
					" for bundle ", missingDependency.getBundleName())));

		return issues;
	}

	private boolean _isNotFragment(Bundle b) {
		Dictionary<String, String> headers = b.getHeaders(StringPool.BLANK);

		if (headers.get("Fragment-Host") == null) {
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
					installedBundles.add(
						StringBundler.concat(
							"[", String.valueOf(bundle.getBundleId()), "] ",
							bundle.getSymbolicName(), " is INSTALLED"));
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

					resolveBundleNames.add(
						StringBundler.concat(
							" * [", String.valueOf(bundle.getBundleId()), "] ",
							bundle.getSymbolicName(), " is RESOLVED"));
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
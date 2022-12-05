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

package com.liferay.portal.spring.extender.internal.configuration;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.spring.extender.internal.LiferayPortalServiceExtension;

import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyManager;
import org.apache.felix.dm.ServiceDependency;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;

/**
 * @author Preston Crary
 */
public class ServiceConfigurationExtension
	implements LiferayPortalServiceExtension {

	public ServiceConfigurationExtension(
		Bundle bundle, String requireSchemaVersion,
		ServiceConfigurationInitializer serviceConfigurationInitializer) {

		_dependencyManager = new DependencyManager(bundle.getBundleContext());

		_component = _dependencyManager.createComponent();

		_component.setImplementation(serviceConfigurationInitializer);

		if (requireSchemaVersion == null) {
			return;
		}

		String versionRangeFilter = null;

		// See LPS-76926

		try {
			Version version = new Version(requireSchemaVersion);

			versionRangeFilter = _getVersionRangerFilter(version);
		}
		catch (IllegalArgumentException illegalArgumentException1) {
			try {
				VersionRange versionRange = new VersionRange(
					requireSchemaVersion);

				versionRangeFilter = versionRange.toFilterString(
					"release.schema.version");
			}
			catch (IllegalArgumentException illegalArgumentException2) {
				illegalArgumentException1.addSuppressed(
					illegalArgumentException2);

				if (_log.isWarnEnabled()) {
					_log.warn(
						"Invalid \"Liferay-Require-SchemaVersion\" header " +
							"for bundle: " + bundle.getBundleId(),
						illegalArgumentException1);
				}
			}
		}

		if (versionRangeFilter == null) {
			return;
		}

		ServiceDependency serviceDependency =
			_dependencyManager.createServiceDependency();

		serviceDependency.setRequired(true);

		serviceDependency.setService(
			Release.class,
			StringBundler.concat(
				"(&(release.bundle.symbolic.name=", bundle.getSymbolicName(),
				")", versionRangeFilter,
				"(|(!(release.state=*))(release.state=0)))"));

		_component.add(serviceDependency);
	}

	@Override
	public void destroy() {
		_dependencyManager.remove(_component);
	}

	@Override
	public void start() {
		_dependencyManager.add(_component);
	}

	private String _getVersionRangerFilter(Version version) {
		return StringBundler.concat(
			"(&(release.schema.version>=", version.getMajor(), ".",
			version.getMinor(), ".0)(!(release.schema.version>=",
			version.getMajor() + 1, ".0.0)))");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ServiceConfigurationExtension.class);

	private final Component _component;
	private final DependencyManager _dependencyManager;

}
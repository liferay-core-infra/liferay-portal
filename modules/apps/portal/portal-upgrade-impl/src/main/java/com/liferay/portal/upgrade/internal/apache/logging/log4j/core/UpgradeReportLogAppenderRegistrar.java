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

package com.liferay.portal.upgrade.internal.apache.logging.log4j.core;

import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.upgrade.internal.release.osgi.commands.ReleaseManagerOSGiCommands;

import org.apache.felix.cm.PersistenceManager;
import org.apache.felix.dm.DependencyManager;
import org.apache.felix.dm.ServiceDependency;
import org.apache.logging.log4j.core.Appender;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Joao Victor
 */
@Component(service = {})
public class UpgradeReportLogAppenderRegistrar {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_dependencyManager = new DependencyManager(bundleContext);

		org.apache.felix.dm.Component component =
			_dependencyManager.createComponent();

		component.setImplementation(new UpgradeReportLogAppender());

		component.setInterface(
			Appender.class,
			MapUtil.singletonDictionary(
				"appender.name", "UpgradeReportLogAppender"));

		ServiceDependency serviceDependency =
			_dependencyManager.createServiceDependency();

		serviceDependency.setCallbacks("setReleaseManagerOSGiCommands", null);
		serviceDependency.setRequired(false);
		serviceDependency.setService(ReleaseManagerOSGiCommands.class);

		component.add(serviceDependency);

		serviceDependency = _dependencyManager.createServiceDependency();

		serviceDependency.setCallbacks("setPersistenceManager", null);
		serviceDependency.setRequired(true);
		serviceDependency.setService(PersistenceManager.class);

		component.add(serviceDependency);

		_dependencyManager.add(component);
	}

	@Deactivate
	protected void deactivate() {
		_dependencyManager.clear();
	}

	private DependencyManager _dependencyManager;

}
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

package com.liferay.portal.configuration.settings.internal;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeInformation;
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * @author Rafael Praxedes
 */
@Component(service = {})
public class ConfigurationBeanClassBundleTracker {

	public class ConfigurationBeanClassBundleTrackerCustomizer
		implements BundleTrackerCustomizer<List<SafeCloseable>> {

		@Override
		public List<SafeCloseable> addingBundle(
			Bundle bundle, BundleEvent bundleEvent) {

			String bundleSymbolicName = bundle.getSymbolicName();

			if (bundleSymbolicName.endsWith(".test")) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Skipping bundle (do not check test modules): " +
							bundleSymbolicName);
				}

				return null;
			}

			ExtendedMetaTypeInformation metaTypeInformation =
				_extendedMetaTypeService.getMetaTypeInformation(bundle);

			if (metaTypeInformation == null) {
				return null;
			}

			List<SafeCloseable> autoCloseables = new ArrayList<>();

			for (String pid :
					ArrayUtil.append(
						metaTypeInformation.getPids(),
						metaTypeInformation.getFactoryPids())) {

				Class<?> configurationBeanClass;

				try {
					configurationBeanClass = bundle.loadClass(pid);
				}
				catch (ClassNotFoundException classNotFoundException) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Class not found: " +
								classNotFoundException.getMessage());
					}

					continue;
				}

				SafeCloseable safeCloseable =
					_configurationBeanClassSettingsRegistry.
						registerConfigurationBeanClass(configurationBeanClass);

				if (safeCloseable != null) {
					autoCloseables.add(safeCloseable);
				}
			}

			if (ListUtil.isEmpty(autoCloseables)) {
				return null;
			}

			return autoCloseables;
		}

		@Override
		public void modifiedBundle(
			Bundle bundle, BundleEvent bundleEvent,
			List<SafeCloseable> autoCloseables) {
		}

		@Override
		public void removedBundle(
			Bundle bundle, BundleEvent bundleEvent,
			List<SafeCloseable> safeCloseables) {

			if (ListUtil.isEmpty(safeCloseables)) {
				return;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Un-registering configuration classes for bundle: " +
						bundle.getSymbolicName());
			}

			for (SafeCloseable safeCloseable : safeCloseables) {
				safeCloseable.close();
			}
		}

	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleTracker = new BundleTracker<>(
			bundleContext, Bundle.ACTIVE,
			new ConfigurationBeanClassBundleTrackerCustomizer());

		_bundleTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_bundleTracker.close();

		_bundleTracker = null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationBeanClassBundleTracker.class);

	private BundleTracker<List<SafeCloseable>> _bundleTracker;

	@Reference
	private ConfigurationBeanClassSettingsRegistry
		_configurationBeanClassSettingsRegistry;

	@Reference
	private ExtendedMetaTypeService _extendedMetaTypeService;

}
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

package com.liferay.portal.upgrade.internal.release;

import com.liferay.osgi.service.tracker.collections.EagerServiceTrackerCustomizer;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceComparator;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.upgrade.internal.graph.ReleaseGraphManager;
import com.liferay.portal.upgrade.internal.registry.UpgradeInfo;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Tina Tian
 */
@Component(service = {})
public class UpgradeStepRegistry {

	public Set<String> getBundleSymbolicNames() {
		return _serviceTrackerMap.keySet();
	}

	public String getSchemaVersionString(String bundleSymbolicName) {
		Release release = _releaseLocalService.fetchRelease(bundleSymbolicName);

		if ((release != null) &&
			Validator.isNotNull(release.getSchemaVersion())) {

			return release.getSchemaVersion();
		}

		return "0.0.0";
	}

	public Set<String> getUpgradableBundleSymbolicNames() {
		Set<String> upgradableBundleSymbolicNames = new HashSet<>();

		for (String bundleSymbolicName : _serviceTrackerMap.keySet()) {
			if (_isUpgradable(bundleSymbolicName)) {
				upgradableBundleSymbolicNames.add(bundleSymbolicName);
			}
		}

		return upgradableBundleSymbolicNames;
	}

	public List<UpgradeInfo> getUpgradeInfos(String bundleSymbolicName) {
		return _serviceTrackerMap.getService(bundleSymbolicName);
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_initialUpgradeStepServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, UpgradeStep.class,
				"(upgrade.initial.database.creation=true)",
				new PropertyServiceReferenceMapper<>(
					"upgrade.bundle.symbolic.name"),
				new InitialUpgradeStepServiceTrackerCustomizer(bundleContext));

		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, UpgradeStep.class, null,
			new PropertyServiceReferenceMapper<>(
				"upgrade.bundle.symbolic.name"),
			new UpgradeServiceTrackerCustomizer(bundleContext),
			Collections.reverseOrder(
				new PropertyServiceReferenceComparator<>(
					"upgrade.from.schema.version")));

		_serviceRegistration = bundleContext.registerService(
			UpgradeStepRegistry.class, this,
			new HashMapDictionary<>(properties));
	}

	@Deactivate
	protected void deactivate() {
		_initialUpgradeStepServiceTrackerMap.close();

		_serviceTrackerMap.close();

		_serviceRegistration.unregister();
	}

	private boolean _isUpgradable(String bundleSymbolicName) {
		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			getUpgradeInfos(bundleSymbolicName));

		List<List<UpgradeInfo>> upgradeInfosList =
			releaseGraphManager.getUpgradeInfosList(
				getSchemaVersionString(bundleSymbolicName));

		if (upgradeInfosList.size() == 1) {
			return true;
		}

		return false;
	}

	private ServiceTrackerMap<String, Release>
		_initialUpgradeStepServiceTrackerMap;

	@Reference
	private ReleaseLocalService _releaseLocalService;

	@Reference
	private ReleasePublisher _releasePublisher;

	private ServiceRegistration<UpgradeStepRegistry> _serviceRegistration;
	private ServiceTrackerMap<String, List<UpgradeInfo>> _serviceTrackerMap;

	private static class UpgradeServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<UpgradeStep, UpgradeInfo> {

		@Override
		public UpgradeInfo addingService(
			ServiceReference<UpgradeStep> serviceReference) {

			String fromSchemaVersionString =
				(String)serviceReference.getProperty(
					"upgrade.from.schema.version");
			String toSchemaVersionString = (String)serviceReference.getProperty(
				"upgrade.to.schema.version");
			int buildNumber = GetterUtil.getInteger(
				serviceReference.getProperty("build.number"));

			return new UpgradeInfo(
				fromSchemaVersionString, toSchemaVersionString, buildNumber,
				_bundleContext.getService(serviceReference));
		}

		@Override
		public void modifiedService(
			ServiceReference<UpgradeStep> serviceReference,
			UpgradeInfo upgradeInfo) {
		}

		@Override
		public void removedService(
			ServiceReference<UpgradeStep> serviceReference,
			UpgradeInfo upgradeInfo) {

			_bundleContext.ungetService(serviceReference);
		}

		private UpgradeServiceTrackerCustomizer(BundleContext bundleContext) {
			_bundleContext = bundleContext;
		}

		private final BundleContext _bundleContext;

	}

	private class InitialUpgradeStepServiceTrackerCustomizer
		implements EagerServiceTrackerCustomizer<UpgradeStep, Release> {

		@Override
		public Release addingService(
			ServiceReference<UpgradeStep> serviceReference) {

			String bundleSymbolicName = (String)serviceReference.getProperty(
				"upgrade.bundle.symbolic.name");

			Release release = _releaseLocalService.fetchRelease(
				bundleSymbolicName);

			if (release == null) {
				UpgradeStep initialUpgradeStep = _bundleContext.getService(
					serviceReference);

				try {
					initialUpgradeStep.upgrade();

					release = _releaseLocalService.updateRelease(
						bundleSymbolicName,
						(String)serviceReference.getProperty(
							"upgrade.to.schema.version"),
						"0.0.0");

					release.setVerified(true);

					release = _releaseLocalService.updateRelease(release);

					_releasePublisher.publish(release, true);
				}
				catch (Exception exception) {
					ReflectionUtil.throwException(exception);
				}
			}

			return release;
		}

		@Override
		public void modifiedService(
			ServiceReference<UpgradeStep> serviceReference, Release release) {
		}

		@Override
		public void removedService(
			ServiceReference<UpgradeStep> serviceReference, Release release) {
		}

		private InitialUpgradeStepServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		private final BundleContext _bundleContext;

	}

}
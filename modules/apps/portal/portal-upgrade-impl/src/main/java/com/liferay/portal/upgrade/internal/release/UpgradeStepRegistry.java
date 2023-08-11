/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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
import com.liferay.portal.upgrade.internal.registry.UpgradeInfo;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Tina Tian
 */
@Component(service = UpgradeStepRegistry.class)
public class UpgradeStepRegistry {

	public Set<String> getBundleSymbolicNames() {
		return _serviceTrackerMap.keySet();
	}

	public List<UpgradeInfo> getUpgradeInfos(String bundleSymbolicName) {
		return _serviceTrackerMap.getService(bundleSymbolicName);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
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
	}

	@Deactivate
	protected void deactivate() {
		_initialUpgradeStepServiceTrackerMap.close();

		_serviceTrackerMap.close();
	}

	private ServiceTrackerMap<String, Release>
		_initialUpgradeStepServiceTrackerMap;

	@Reference
	private ReleaseLocalService _releaseLocalService;

	@Reference
	private ReleasePublisher _releasePublisher;

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
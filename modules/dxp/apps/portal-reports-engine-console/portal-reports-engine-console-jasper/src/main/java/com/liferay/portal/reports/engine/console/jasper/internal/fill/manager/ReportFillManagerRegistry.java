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

package com.liferay.portal.reports.engine.console.jasper.internal.fill.manager;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.reports.engine.ReportDataSourceType;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Gavin Wan
 * @author Brian Wing Shun Chan
 * @author Brian Greenwald
 */
@Component(immediate = true, service = ReportFillManagerRegistry.class)
public class ReportFillManagerRegistry {

	public ReportFillManager getReportFillManager(
		ReportDataSourceType reportDataSourceType) {

		ReportFillManager reportFillManager = _serviceTrackerMap.getService(
			reportDataSourceType);

		if (reportFillManager == null) {
			throw new IllegalArgumentException(
				"No report fill manager found for " + reportDataSourceType);
		}

		return reportFillManager;
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ReportFillManager.class, null,
			ServiceReferenceMapperFactory.create(
				bundleContext,
				(reportFillManager, emitter) -> {
					String reportDataSourceTypeString = GetterUtil.getString(
						properties.get("reportDataSourceType"));

					emitter.emit(
						ReportDataSourceType.parse(reportDataSourceTypeString));
				}),
			new ReportFillManagerServiceTrackerCustomizer(
				bundleContext, properties));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ReportFillManagerRegistry.class);

	private ServiceTrackerMap<ReportDataSourceType, ReportFillManager>
		_serviceTrackerMap;

	private class ReportFillManagerServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<ReportFillManager, ReportFillManager> {

		public ReportFillManagerServiceTrackerCustomizer(
			BundleContext bundleContext, Map<String, Object> properties) {

			_bundleContext = bundleContext;
			_properties = properties;
		}

		@Override
		public ReportFillManager addingService(
			ServiceReference<ReportFillManager> serviceReference) {

			return _bundleContext.getService(serviceReference);
		}

		@Override
		public void modifiedService(
			ServiceReference<ReportFillManager> serviceReference,
			ReportFillManager reportFillManager) {
		}

		@Override
		public void removedService(
			ServiceReference<ReportFillManager> serviceReference,
			ReportFillManager reportFillManager) {

			String reportDataSourceTypeString = GetterUtil.getString(
				_properties.get("reportDataSourceType"));

			ReportDataSourceType reportDataSourceType =
				ReportDataSourceType.parse(reportDataSourceTypeString);

			if (reportDataSourceType == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"No report data source type specified for " +
							reportFillManager);
				}

				return;
			}

			_bundleContext.ungetService(serviceReference);
		}

		private final BundleContext _bundleContext;
		private final Map<String, Object> _properties;

	}

}
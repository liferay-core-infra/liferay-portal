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

package com.liferay.portal.reports.engine.console.jasper.internal.exporter;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.reports.engine.ReportFormat;
import com.liferay.portal.reports.engine.ReportFormatExporter;
import com.liferay.portal.reports.engine.ReportFormatExporterRegistry;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Brian Greenwald
 */
@Component(immediate = true, service = ReportFormatExporterRegistry.class)
public class JasperReportFormatExporterRegistry
	extends ReportFormatExporterRegistry {

	@Override
	public ReportFormatExporter getReportFormatExporter(
		ReportFormat reportFormat) {

		ReportFormatExporter reportFormatExporter =
			_serviceTrackerMap.getService(reportFormat);

		if (reportFormatExporter == null) {
			throw new IllegalArgumentException(
				"No report format exporter found for " + reportFormat);
		}

		return reportFormatExporter;
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ReportFormatExporter.class, null,
			ServiceReferenceMapperFactory.create(
				bundleContext,
				(reportFormatExporter, emitter) -> {
					String reportFormatString = GetterUtil.getString(
						properties.get("reportFormat"));

					emitter.emit(ReportFormat.parse(reportFormatString));
				}),
			new ReportFormatExporterServiceTrackerCustomizer(
				bundleContext, properties));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JasperReportFormatExporterRegistry.class);

	private ServiceTrackerMap<ReportFormat, ReportFormatExporter>
		_serviceTrackerMap;

	private class ReportFormatExporterServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<ReportFormatExporter, ReportFormatExporter> {

		public ReportFormatExporterServiceTrackerCustomizer(
			BundleContext bundleContext, Map<String, Object> properties) {

			_bundleContext = bundleContext;
			_properties = properties;
		}

		@Override
		public ReportFormatExporter addingService(
			ServiceReference<ReportFormatExporter> serviceReference) {

			return _bundleContext.getService(serviceReference);
		}

		@Override
		public void modifiedService(
			ServiceReference<ReportFormatExporter> serviceReference,
			ReportFormatExporter reportFormatExporter) {
		}

		@Override
		public void removedService(
			ServiceReference<ReportFormatExporter> serviceReference,
			ReportFormatExporter reportFormatExporter) {

			String reportFormatString = GetterUtil.getString(
				_properties.get("reportFormat"));

			ReportFormat reportFormat = ReportFormat.parse(reportFormatString);

			if (reportFormat == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"No report format specified for " +
							reportFormatExporter);
				}

				return;
			}

			_bundleContext.ungetService(serviceReference);
		}

		private final BundleContext _bundleContext;
		private final Map<String, Object> _properties;

	}

}
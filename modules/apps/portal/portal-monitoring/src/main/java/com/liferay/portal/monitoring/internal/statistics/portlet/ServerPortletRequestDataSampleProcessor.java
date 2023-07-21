/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.monitoring.internal.statistics.portlet;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.monitoring.DataSampleProcessor;
import com.liferay.portal.kernel.monitoring.MonitoringException;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 * @author Brian Wing Shun Chan
 */
@Component(
	enabled = false, property = "namespace=com.liferay.monitoring.Portlet",
	service = {DataSampleProcessor.class, ServerPortletRequestDataSampleProcessor.class}
)
public class ServerPortletRequestDataSampleProcessor
	implements DataSampleProcessor<PortletRequestDataSample> {

	public Set<Long> getCompanyIds() {
		return _companyStatisticsByCompanyId.keySet();
	}

	public PortletRequestDataSampleProcessor getCompanyStatistics(long companyId)
		throws MonitoringException {

		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor = _companyStatisticsByCompanyId.get(
			companyId);

		if (portletRequestDataSampleProcessor == null) {
			throw new MonitoringException(
				"No statistics found for company ID " + companyId);
		}

		return portletRequestDataSampleProcessor;
	}

	public PortletRequestDataSampleProcessor getCompanyStatistics(String webId)
		throws MonitoringException {

		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor = _companyStatisticsByWebId.get(
			webId);

		if (portletRequestDataSampleProcessor == null) {
			throw new MonitoringException(
				"No statistics found for web ID " + webId);
		}

		return portletRequestDataSampleProcessor;
	}

	public Set<PortletRequestDataSampleProcessor> getCompanyStatisticsSet() {
		return new HashSet<>(_companyStatisticsByWebId.values());
	}

	public Set<String> getPortletIds() {
		Set<String> portletIds = new HashSet<>();

		for (PortletRequestDataSampleProcessor containerStatistics :
				_companyStatisticsByWebId.values()) {

			portletIds.addAll(containerStatistics.getPortletIds());
		}

		return portletIds;
	}

	public Set<String> getWebIds() {
		return _companyStatisticsByWebId.keySet();
	}

	@Override
	public void processDataSample(
			PortletRequestDataSample portletRequestDataSample)
		throws MonitoringException {

		long companyId = portletRequestDataSample.getCompanyId();

		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor = _companyStatisticsByCompanyId.get(
			companyId);

		if (portletRequestDataSampleProcessor == null) {
			try {
				Company company = _companyLocalService.getCompany(companyId);

				portletRequestDataSampleProcessor = register(company.getWebId());
			}
			catch (Exception exception) {
				throw new IllegalStateException(
					"Unable to get company with company ID " + companyId,
					exception);
			}
		}

		portletRequestDataSampleProcessor.processDataSample(portletRequestDataSample);
	}

	public synchronized PortletRequestDataSampleProcessor register(String webId) {
		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor = new PortletRequestDataSampleProcessor(
			_companyLocalService, webId);

		_companyStatisticsByCompanyId.put(
			portletRequestDataSampleProcessor.getCompanyId(),
			portletRequestDataSampleProcessor);
		_companyStatisticsByWebId.put(webId, portletRequestDataSampleProcessor);

		return portletRequestDataSampleProcessor;
	}

	public void reset() {
		_companyLocalService.forEachCompanyId(
			companyId -> reset(companyId),
			ArrayUtil.toLongArray(_companyStatisticsByCompanyId.keySet()));
	}

	public void reset(long companyId) {
		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor = _companyStatisticsByCompanyId.get(
			companyId);

		if (portletRequestDataSampleProcessor == null) {
			return;
		}

		portletRequestDataSampleProcessor.reset();
	}

	public void reset(String webId) {
		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor = _companyStatisticsByWebId.get(
			webId);

		if (portletRequestDataSampleProcessor == null) {
			return;
		}

		portletRequestDataSampleProcessor.reset();
	}

	public synchronized void unregister(String webId) {
		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor = _companyStatisticsByWebId.remove(
			webId);

		if (portletRequestDataSampleProcessor != null) {
			_companyStatisticsByCompanyId.remove(
				portletRequestDataSampleProcessor.getCompanyId());
		}
	}

	@Activate
	protected void activate() {
		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor = new PortletRequestDataSampleProcessor();

		_companyStatisticsByCompanyId.put(
			portletRequestDataSampleProcessor.getCompanyId(),
			portletRequestDataSampleProcessor);
		_companyStatisticsByWebId.put(
			portletRequestDataSampleProcessor.getWebId(),
			portletRequestDataSampleProcessor);
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	private final Map<Long, PortletRequestDataSampleProcessor> _companyStatisticsByCompanyId =
		new TreeMap<>();
	private final Map<String, PortletRequestDataSampleProcessor> _companyStatisticsByWebId =
		new TreeMap<>();

}
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.monitoring.internal.statistics.portal;

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
	enabled = false, property = "namespace=com.liferay.monitoring.Portal",
	service = {DataSampleProcessor.class, ServerPortalRequestDataSampleProcessor.class}
)
public class ServerPortalRequestDataSampleProcessor
	implements DataSampleProcessor<PortalRequestDataSample> {

	public Set<Long> getCompanyIds() {
		return _companyStatisticsByCompanyId.keySet();
	}

	public PortalRequestDataSampleProcessor getCompanyStatistics(long companyId)
		throws MonitoringException {

		PortalRequestDataSampleProcessor portalRequestDataSampleProcessor = _companyStatisticsByCompanyId.get(
			companyId);

		if (portalRequestDataSampleProcessor == null) {
			throw new MonitoringException(
				"No statistics found for company ID " + companyId);
		}

		return portalRequestDataSampleProcessor;
	}

	public PortalRequestDataSampleProcessor getCompanyStatistics(String webId)
		throws MonitoringException {

		PortalRequestDataSampleProcessor portalRequestDataSampleProcessor = _companyStatisticsByWebId.get(
			webId);

		if (portalRequestDataSampleProcessor == null) {
			throw new MonitoringException(
				"No statistics found for web ID " + webId);
		}

		return portalRequestDataSampleProcessor;
	}

	public Set<PortalRequestDataSampleProcessor> getCompanyStatisticsSet() {
		return new HashSet<>(_companyStatisticsByWebId.values());
	}

	public Set<String> getWebIds() {
		return _companyStatisticsByWebId.keySet();
	}

	@Override
	public void processDataSample(
		PortalRequestDataSample portalRequestDataSample) {

		long companyId = portalRequestDataSample.getCompanyId();

		PortalRequestDataSampleProcessor portalRequestDataSampleProcessor = _companyStatisticsByCompanyId.get(
			companyId);

		if (portalRequestDataSampleProcessor == null) {
			try {
				Company company = _companyLocalService.getCompany(companyId);

				portalRequestDataSampleProcessor = register(company.getWebId());
			}
			catch (Exception exception) {
				throw new IllegalStateException(
					"Unable to get company with company ID " + companyId,
					exception);
			}
		}

		portalRequestDataSampleProcessor.processDataSample(portalRequestDataSample);
	}

	public synchronized PortalRequestDataSampleProcessor register(String webId) {
		PortalRequestDataSampleProcessor
			portalRequestDataSampleProcessor = new PortalRequestDataSampleProcessor(
			_companyLocalService, webId);

		_companyStatisticsByCompanyId.put(
			portalRequestDataSampleProcessor.getCompanyId(),
			portalRequestDataSampleProcessor);
		_companyStatisticsByWebId.put(webId, portalRequestDataSampleProcessor);

		return portalRequestDataSampleProcessor;
	}

	public void reset() {
		_companyLocalService.forEachCompanyId(
			companyId -> reset(companyId),
			ArrayUtil.toLongArray(_companyStatisticsByCompanyId.keySet()));
	}

	public void reset(long companyId) {
		PortalRequestDataSampleProcessor portalRequestDataSampleProcessor = _companyStatisticsByCompanyId.get(
			companyId);

		if (portalRequestDataSampleProcessor == null) {
			return;
		}

		portalRequestDataSampleProcessor.reset();
	}

	public void reset(String webId) {
		PortalRequestDataSampleProcessor portalRequestDataSampleProcessor = _companyStatisticsByWebId.get(
			webId);

		if (portalRequestDataSampleProcessor == null) {
			return;
		}

		portalRequestDataSampleProcessor.reset();
	}

	public synchronized void unregister(String webId) {
		PortalRequestDataSampleProcessor portalRequestDataSampleProcessor = _companyStatisticsByWebId.remove(
			webId);

		if (portalRequestDataSampleProcessor != null) {
			_companyStatisticsByCompanyId.remove(
				portalRequestDataSampleProcessor.getCompanyId());
		}
	}

	@Activate
	protected void activate() {
		PortalRequestDataSampleProcessor
			portalRequestDataSampleProcessor = new PortalRequestDataSampleProcessor();

		_companyStatisticsByCompanyId.put(
			portalRequestDataSampleProcessor.getCompanyId(),
			portalRequestDataSampleProcessor);
		_companyStatisticsByWebId.put(
			portalRequestDataSampleProcessor.getWebId(),
			portalRequestDataSampleProcessor);
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	private final Map<Long, PortalRequestDataSampleProcessor> _companyStatisticsByCompanyId =
		new TreeMap<>();
	private final Map<String, PortalRequestDataSampleProcessor> _companyStatisticsByWebId =
		new TreeMap<>();

}
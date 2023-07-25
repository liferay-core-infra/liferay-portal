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
	service = {
		DataSampleProcessor.class, ServerPortalRequestDataSampleProcessor.class
	}
)
public class ServerPortalRequestDataSampleProcessor
	implements DataSampleProcessor<PortalRequestDataSample> {

	public Set<Long> getPortalCompanyIds() {
		return _portalRequestDataSampleProcessorByCompanyId.keySet();
	}

	public PortalRequestDataSampleProcessor getPortalRequestDataSampleProcessor(
			long companyId)
		throws MonitoringException {

		PortalRequestDataSampleProcessor portalRequestDataSampleProcessor =
			_portalRequestDataSampleProcessorByCompanyId.get(companyId);

		if (portalRequestDataSampleProcessor == null) {
			throw new MonitoringException(
				"No statistics found for company ID " + companyId);
		}

		return portalRequestDataSampleProcessor;
	}

	public PortalRequestDataSampleProcessor getPortalRequestDataSampleProcessor(
			String webId)
		throws MonitoringException {

		PortalRequestDataSampleProcessor portalRequestDataSampleProcessor =
			_portalRequestDataSampleProcessorByWebId.get(webId);

		if (portalRequestDataSampleProcessor == null) {
			throw new MonitoringException(
				"No statistics found for web ID " + webId);
		}

		return portalRequestDataSampleProcessor;
	}

	public Set<PortalRequestDataSampleProcessor>
		getPortalRequestDataSampleProcessorSet() {

		return new HashSet<>(_portalRequestDataSampleProcessorByWebId.values());
	}

	public Set<String> getPortalWebIds() {
		return _portalRequestDataSampleProcessorByWebId.keySet();
	}

	@Override
	public void processDataSample(
		PortalRequestDataSample portalRequestDataSample) {

		long companyId = portalRequestDataSample.getCompanyId();

		PortalRequestDataSampleProcessor portalRequestDataSampleProcessor =
			_portalRequestDataSampleProcessorByCompanyId.get(companyId);

		if (portalRequestDataSampleProcessor == null) {
			try {
				Company company = _companyLocalService.getCompany(companyId);

				portalRequestDataSampleProcessor =
					registerPortalRequestDataSampleProcessor(
						company.getWebId());
			}
			catch (Exception exception) {
				throw new IllegalStateException(
					"Unable to get company with company ID " + companyId,
					exception);
			}
		}

		portalRequestDataSampleProcessor.processDataSample(
			portalRequestDataSample);
	}

	public synchronized PortalRequestDataSampleProcessor
		registerPortalRequestDataSampleProcessor(String webId) {

		PortalRequestDataSampleProcessor portalRequestDataSampleProcessor =
			new PortalRequestDataSampleProcessor(_companyLocalService, webId);

		_portalRequestDataSampleProcessorByCompanyId.put(
			portalRequestDataSampleProcessor.getCompanyId(),
			portalRequestDataSampleProcessor);
		_portalRequestDataSampleProcessorByWebId.put(
			webId, portalRequestDataSampleProcessor);

		return portalRequestDataSampleProcessor;
	}

	public void resetPortalRequestDataSampleProcessor() {
		_companyLocalService.forEachCompanyId(
			companyId -> resetPortalRequestDataSampleProcessor(companyId),
			ArrayUtil.toLongArray(
				_portalRequestDataSampleProcessorByCompanyId.keySet()));
	}

	public void resetPortalRequestDataSampleProcessor(long companyId) {
		PortalRequestDataSampleProcessor portalRequestDataSampleProcessor =
			_portalRequestDataSampleProcessorByCompanyId.get(companyId);

		if (portalRequestDataSampleProcessor == null) {
			return;
		}

		portalRequestDataSampleProcessor.reset();
	}

	public void resetPortalRequestDataSampleProcessor(String webId) {
		PortalRequestDataSampleProcessor portalRequestDataSampleProcessor =
			_portalRequestDataSampleProcessorByWebId.get(webId);

		if (portalRequestDataSampleProcessor == null) {
			return;
		}

		portalRequestDataSampleProcessor.reset();
	}

	public synchronized void unregisterPortalRequestDataSampleProcessor(
		String webId) {

		PortalRequestDataSampleProcessor portalRequestDataSampleProcessor =
			_portalRequestDataSampleProcessorByWebId.remove(webId);

		if (portalRequestDataSampleProcessor != null) {
			_portalRequestDataSampleProcessorByCompanyId.remove(
				portalRequestDataSampleProcessor.getCompanyId());
		}
	}

	@Activate
	protected void activate() {
		PortalRequestDataSampleProcessor portalRequestDataSampleProcessor =
			new PortalRequestDataSampleProcessor();

		_portalRequestDataSampleProcessorByCompanyId.put(
			portalRequestDataSampleProcessor.getCompanyId(),
			portalRequestDataSampleProcessor);
		_portalRequestDataSampleProcessorByWebId.put(
			portalRequestDataSampleProcessor.getWebId(),
			portalRequestDataSampleProcessor);
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	private final Map<Long, PortalRequestDataSampleProcessor>
		_portalRequestDataSampleProcessorByCompanyId = new TreeMap<>();
	private final Map<String, PortalRequestDataSampleProcessor>
		_portalRequestDataSampleProcessorByWebId = new TreeMap<>();

}
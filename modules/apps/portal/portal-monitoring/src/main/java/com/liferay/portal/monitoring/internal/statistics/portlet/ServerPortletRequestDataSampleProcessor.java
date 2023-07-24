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
	service = {
		DataSampleProcessor.class, ServerPortletRequestDataSampleProcessor.class
	}
)
public class ServerPortletRequestDataSampleProcessor
	implements DataSampleProcessor<PortletRequestDataSample> {

	public Set<Long> getPortletCompanyIds() {
		return _portletRequestDataSampleProcessorByCompanyId.keySet();
	}

	public Set<String> getPortletIds() {
		Set<String> portletIds = new HashSet<>();

		for (PortletRequestDataSampleProcessor containerStatistics :
				_portletRequestDataSampleProcessorByWebId.values()) {

			portletIds.addAll(containerStatistics.getPortletIds());
		}

		return portletIds;
	}

	public Set<PortletRequestDataSampleProcessor>
		getPortletRequestDataSampleProcessor() {

		return new HashSet<>(
			_portletRequestDataSampleProcessorByWebId.values());
	}

	public PortletRequestDataSampleProcessor
			getPortletRequestDataSampleProcessor(long companyId)
		throws MonitoringException {

		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor =
			_portletRequestDataSampleProcessorByCompanyId.get(companyId);

		if (portletRequestDataSampleProcessor == null) {
			throw new MonitoringException(
				"No statistics found for company ID " + companyId);
		}

		return portletRequestDataSampleProcessor;
	}

	public PortletRequestDataSampleProcessor
			getPortletRequestDataSampleProcessor(String webId)
		throws MonitoringException {

		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor =
			_portletRequestDataSampleProcessorByWebId.get(webId);

		if (portletRequestDataSampleProcessor == null) {
			throw new MonitoringException(
				"No statistics found for web ID " + webId);
		}

		return portletRequestDataSampleProcessor;
	}

	public Set<String> getPortletWebIds() {
		return _portletRequestDataSampleProcessorByWebId.keySet();
	}

	@Override
	public void processDataSample(
			PortletRequestDataSample portletRequestDataSample)
		throws MonitoringException {

		long companyId = portletRequestDataSample.getCompanyId();

		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor =
			_portletRequestDataSampleProcessorByCompanyId.get(companyId);

		if (portletRequestDataSampleProcessor == null) {
			try {
				Company company = _companyLocalService.getCompany(companyId);

				portletRequestDataSampleProcessor =
					registerPortletRequestDataSampleProcessor(
						company.getWebId());
			}
			catch (Exception exception) {
				throw new IllegalStateException(
					"Unable to get company with company ID " + companyId,
					exception);
			}
		}

		portletRequestDataSampleProcessor.processDataSample(
			portletRequestDataSample);
	}

	public synchronized PortletRequestDataSampleProcessor
		registerPortletRequestDataSampleProcessor(String webId) {

		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor =
			new PortletRequestDataSampleProcessor(_companyLocalService, webId);

		_portletRequestDataSampleProcessorByCompanyId.put(
			portletRequestDataSampleProcessor.getCompanyId(),
			portletRequestDataSampleProcessor);
		_portletRequestDataSampleProcessorByWebId.put(
			webId, portletRequestDataSampleProcessor);

		return portletRequestDataSampleProcessor;
	}

	public void resetPortletRequestDataSampleProcessor() {
		_companyLocalService.forEachCompanyId(
			companyId -> resetPortletRequestDataSampleProcessor(companyId),
			ArrayUtil.toLongArray(
				_portletRequestDataSampleProcessorByCompanyId.keySet()));
	}

	public void resetPortletRequestDataSampleProcessor(long companyId) {
		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor =
			_portletRequestDataSampleProcessorByCompanyId.get(companyId);

		if (portletRequestDataSampleProcessor == null) {
			return;
		}

		portletRequestDataSampleProcessor.reset();
	}

	public void resetPortletRequestDataSampleProcessor(String webId) {
		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor =
			_portletRequestDataSampleProcessorByWebId.get(webId);

		if (portletRequestDataSampleProcessor == null) {
			return;
		}

		portletRequestDataSampleProcessor.reset();
	}

	public synchronized void unregisterPortletRequestDataSampleProcessor(
		String webId) {

		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor =
			_portletRequestDataSampleProcessorByWebId.remove(webId);

		if (portletRequestDataSampleProcessor != null) {
			_portletRequestDataSampleProcessorByCompanyId.remove(
				portletRequestDataSampleProcessor.getCompanyId());
		}
	}

	@Activate
	protected void activate() {
		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor =
			new PortletRequestDataSampleProcessor();

		_portletRequestDataSampleProcessorByCompanyId.put(
			portletRequestDataSampleProcessor.getCompanyId(),
			portletRequestDataSampleProcessor);
		_portletRequestDataSampleProcessorByWebId.put(
			portletRequestDataSampleProcessor.getWebId(),
			portletRequestDataSampleProcessor);
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	private final Map<Long, PortletRequestDataSampleProcessor>
		_portletRequestDataSampleProcessorByCompanyId = new TreeMap<>();
	private final Map<String, PortletRequestDataSampleProcessor>
		_portletRequestDataSampleProcessorByWebId = new TreeMap<>();

}
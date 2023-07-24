/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.monitoring.internal.statistics.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.monitoring.MonitoringException;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.monitoring.internal.statistics.portal.PortalRequestDataSampleProcessor;
import com.liferay.portal.monitoring.internal.statistics.portlet.PortletRequestDataSampleProcessor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Renan Vasconcelos
 */
@Component(service = RequestDataSampleProcessorHelper.class)
public class RequestDataSampleProcessorHelper {

	public static Set<PortalRequestDataSampleProcessor>
		getPortalRequestDataSampleProcessorSet() {

		return new HashSet<>(_portalRequestDataSampleProcessorByWebId.values());
	}

	public Company getCompanyByCompanyId(long companyId)
		throws PortalException {

		return _companyLocalService.getCompany(companyId);
	}

	public Set<Long> getPorletCompanyIds() {
		return _portletRequestDataSampleProcessorByCompanyId.keySet();
	}

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

	public PortalRequestDataSampleProcessor
		getPortalRequestDataSampleProcessorByCompanyId(long companyId) {

		return _portalRequestDataSampleProcessorByCompanyId.get(companyId);
	}

	public Set<String> getPortalWebIds() {
		return _portalRequestDataSampleProcessorByWebId.keySet();
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

	public PortletRequestDataSampleProcessor
		getPortletRequestDataSampleProcessorByCompanyId(long companyId) {

		return _portletRequestDataSampleProcessorByCompanyId.get(companyId);
	}

	public Set<String> getPortletWebIds() {
		return _portletRequestDataSampleProcessorByWebId.keySet();
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

	public synchronized void unregisterPortalRequestDataSampleProcessor(
		String webId) {

		PortalRequestDataSampleProcessor portalRequestDataSampleProcessor =
			_portalRequestDataSampleProcessorByWebId.remove(webId);

		if (portalRequestDataSampleProcessor != null) {
			_portalRequestDataSampleProcessorByCompanyId.remove(
				portalRequestDataSampleProcessor.getCompanyId());
		}
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
		PortalRequestDataSampleProcessor portalRequestDataSampleProcessor =
			new PortalRequestDataSampleProcessor();

		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor =
			new PortletRequestDataSampleProcessor();

		_portalRequestDataSampleProcessorByCompanyId.put(
			portalRequestDataSampleProcessor.getCompanyId(),
			portalRequestDataSampleProcessor);
		_portalRequestDataSampleProcessorByWebId.put(
			portalRequestDataSampleProcessor.getWebId(),
			portalRequestDataSampleProcessor);

		_portletRequestDataSampleProcessorByCompanyId.put(
			portletRequestDataSampleProcessor.getCompanyId(),
			portletRequestDataSampleProcessor);
		_portletRequestDataSampleProcessorByWebId.put(
			portletRequestDataSampleProcessor.getWebId(),
			portletRequestDataSampleProcessor);
	}

	private static final Map<Long, PortalRequestDataSampleProcessor>
		_portalRequestDataSampleProcessorByCompanyId = new TreeMap<>();
	private static final Map<String, PortalRequestDataSampleProcessor>
		_portalRequestDataSampleProcessorByWebId = new TreeMap<>();

	@Reference
	private CompanyLocalService _companyLocalService;

	private final Map<Long, PortletRequestDataSampleProcessor>
		_portletRequestDataSampleProcessorByCompanyId = new TreeMap<>();
	private final Map<String, PortletRequestDataSampleProcessor>
		_portletRequestDataSampleProcessorByWebId = new TreeMap<>();

}
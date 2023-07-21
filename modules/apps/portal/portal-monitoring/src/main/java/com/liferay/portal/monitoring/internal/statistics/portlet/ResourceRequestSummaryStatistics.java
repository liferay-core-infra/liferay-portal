/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.monitoring.internal.statistics.portlet;

import com.liferay.portal.kernel.monitoring.MonitoringException;
import com.liferay.portal.monitoring.internal.statistics.RequestStatistics;

import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 * @author Brian Wing Shun Chan
 */
@Component(enabled = false, service = ResourceRequestSummaryStatistics.class)
public class ResourceRequestSummaryStatistics
	implements PortletSummaryStatistics {

	@Override
	public long getAverageTime() {
		long averageTime = 0;

		long count = 0;

		for (PortletRequestDataSampleProcessor portletRequestDataSampleProcessor :
				_serverPortletRequestDataSampleProcessor.getCompanyStatisticsSet()) {

			for (RequestStatistics requestStatistics :
					portletRequestDataSampleProcessor.getResourceRequestStatisticsSet()) {

				averageTime += requestStatistics.getAverageTime();

				count++;
			}
		}

		if (count > 0) {
			return averageTime / count;
		}

		return 0;
	}

	@Override
	public long getAverageTimeByCompany(long companyId)
		throws MonitoringException {

		return getAverageTimeByCompany(
			_serverPortletRequestDataSampleProcessor.getCompanyStatistics(companyId));
	}

	@Override
	public long getAverageTimeByCompany(String webId)
		throws MonitoringException {

		return getAverageTimeByCompany(
			_serverPortletRequestDataSampleProcessor.getCompanyStatistics(webId));
	}

	@Override
	public long getAverageTimeByPortlet(String portletId)
		throws MonitoringException {

		long averageTime = 0;

		Set<PortletRequestDataSampleProcessor>
			portletRequestDataSampleProcessorSet =
			_serverPortletRequestDataSampleProcessor.getCompanyStatisticsSet();

		for (PortletRequestDataSampleProcessor portletRequestDataSampleProcessor : portletRequestDataSampleProcessorSet) {
			RequestStatistics requestStatistics =
				portletRequestDataSampleProcessor.getResourceRequestStatistics(portletId);

			averageTime += requestStatistics.getAverageTime();
		}

		if (!portletRequestDataSampleProcessorSet.isEmpty()) {
			return averageTime / portletRequestDataSampleProcessorSet.size();
		}

		return averageTime;
	}

	@Override
	public long getAverageTimeByPortlet(String portletId, long companyId)
		throws MonitoringException {

		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor =
			_serverPortletRequestDataSampleProcessor.getCompanyStatistics(companyId);

		RequestStatistics requestStatistics =
			portletRequestDataSampleProcessor.getResourceRequestStatistics(portletId);

		return requestStatistics.getAverageTime();
	}

	@Override
	public long getAverageTimeByPortlet(String portletId, String webId)
		throws MonitoringException {

		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor =
			_serverPortletRequestDataSampleProcessor.getCompanyStatistics(webId);

		RequestStatistics requestStatistics =
			portletRequestDataSampleProcessor.getResourceRequestStatistics(portletId);

		return requestStatistics.getAverageTime();
	}

	@Override
	public long getErrorCount() {
		long errorCount = 0;

		for (PortletRequestDataSampleProcessor portletRequestDataSampleProcessor :
				_serverPortletRequestDataSampleProcessor.getCompanyStatisticsSet()) {

			errorCount += getErrorCountByCompany(
				portletRequestDataSampleProcessor);
		}

		return errorCount;
	}

	@Override
	public long getErrorCountByCompany(long companyId)
		throws MonitoringException {

		return getErrorCountByCompany(
			_serverPortletRequestDataSampleProcessor.getCompanyStatistics(companyId));
	}

	@Override
	public long getErrorCountByCompany(String webId)
		throws MonitoringException {

		return getErrorCountByCompany(
			_serverPortletRequestDataSampleProcessor.getCompanyStatistics(webId));
	}

	@Override
	public long getErrorCountByPortlet(String portletId)
		throws MonitoringException {

		long errorCount = 0;

		for (PortletRequestDataSampleProcessor portletRequestDataSampleProcessor :
				_serverPortletRequestDataSampleProcessor.getCompanyStatisticsSet()) {

			errorCount += getErrorCountByPortlet(portletId,
				portletRequestDataSampleProcessor);
		}

		return errorCount;
	}

	@Override
	public long getErrorCountByPortlet(String portletId, long companyId)
		throws MonitoringException {

		return getErrorCountByPortlet(
			portletId, _serverPortletRequestDataSampleProcessor.getCompanyStatistics(companyId));
	}

	@Override
	public long getErrorCountByPortlet(String portletId, String webId)
		throws MonitoringException {

		return getErrorCountByPortlet(
			portletId, _serverPortletRequestDataSampleProcessor.getCompanyStatistics(webId));
	}

	@Override
	public long getMaxTime() {
		long maxTime = 0;

		for (PortletRequestDataSampleProcessor portletRequestDataSampleProcessor :
				_serverPortletRequestDataSampleProcessor.getCompanyStatisticsSet()) {

			for (RequestStatistics requestStatistics :
					portletRequestDataSampleProcessor.getResourceRequestStatisticsSet()) {

				if (requestStatistics.getMaxTime() > maxTime) {
					maxTime = requestStatistics.getMaxTime();
				}
			}
		}

		return maxTime;
	}

	@Override
	public long getMaxTimeByCompany(long companyId) throws MonitoringException {
		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor =
			_serverPortletRequestDataSampleProcessor.getCompanyStatistics(companyId);

		return portletRequestDataSampleProcessor.getMaxTime();
	}

	@Override
	public long getMaxTimeByCompany(String webId) throws MonitoringException {
		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor =
			_serverPortletRequestDataSampleProcessor.getCompanyStatistics(webId);

		return portletRequestDataSampleProcessor.getMaxTime();
	}

	@Override
	public long getMaxTimeByPortlet(String portletId)
		throws MonitoringException {

		long maxTime = 0;

		for (PortletRequestDataSampleProcessor portletRequestDataSampleProcessor :
				_serverPortletRequestDataSampleProcessor.getCompanyStatisticsSet()) {

			long curMaxTime = getMaxTimeByPortlet(portletId,
				portletRequestDataSampleProcessor);

			if (curMaxTime > maxTime) {
				maxTime = curMaxTime;
			}
		}

		return maxTime;
	}

	@Override
	public long getMaxTimeByPortlet(String portletId, long companyId)
		throws MonitoringException {

		return getMaxTimeByPortlet(
			portletId, _serverPortletRequestDataSampleProcessor.getCompanyStatistics(companyId));
	}

	@Override
	public long getMaxTimeByPortlet(String portletId, String webId)
		throws MonitoringException {

		return getMaxTimeByPortlet(
			portletId, _serverPortletRequestDataSampleProcessor.getCompanyStatistics(webId));
	}

	@Override
	public long getMinTime() {
		long minTime = 0;

		for (PortletRequestDataSampleProcessor portletRequestDataSampleProcessor :
				_serverPortletRequestDataSampleProcessor.getCompanyStatisticsSet()) {

			for (RequestStatistics requestStatistics :
					portletRequestDataSampleProcessor.getResourceRequestStatisticsSet()) {

				if (requestStatistics.getMinTime() < minTime) {
					minTime = requestStatistics.getMinTime();
				}
			}
		}

		return minTime;
	}

	@Override
	public long getMinTimeByCompany(long companyId) throws MonitoringException {
		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor =
			_serverPortletRequestDataSampleProcessor.getCompanyStatistics(companyId);

		return portletRequestDataSampleProcessor.getMinTime();
	}

	@Override
	public long getMinTimeByCompany(String webId) throws MonitoringException {
		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor =
			_serverPortletRequestDataSampleProcessor.getCompanyStatistics(webId);

		return portletRequestDataSampleProcessor.getMinTime();
	}

	@Override
	public long getMinTimeByPortlet(String portletId)
		throws MonitoringException {

		long minTime = 0;

		for (PortletRequestDataSampleProcessor portletRequestDataSampleProcessor :
				_serverPortletRequestDataSampleProcessor.getCompanyStatisticsSet()) {

			long curMinTime = getMinTimeByPortlet(portletId,
				portletRequestDataSampleProcessor);

			if (curMinTime < minTime) {
				minTime = curMinTime;
			}
		}

		return minTime;
	}

	@Override
	public long getMinTimeByPortlet(String portletId, long companyId)
		throws MonitoringException {

		return getMinTimeByPortlet(
			portletId, _serverPortletRequestDataSampleProcessor.getCompanyStatistics(companyId));
	}

	@Override
	public long getMinTimeByPortlet(String portletId, String webId)
		throws MonitoringException {

		return getMinTimeByPortlet(
			portletId, _serverPortletRequestDataSampleProcessor.getCompanyStatistics(webId));
	}

	@Override
	public long getRequestCount() {
		long requestCount = 0;

		for (PortletRequestDataSampleProcessor portletRequestDataSampleProcessor :
				_serverPortletRequestDataSampleProcessor.getCompanyStatisticsSet()) {

			requestCount += getRequestCountByCompany(
				portletRequestDataSampleProcessor);
		}

		return requestCount;
	}

	@Override
	public long getRequestCountByCompany(long companyId)
		throws MonitoringException {

		return getRequestCountByCompany(
			_serverPortletRequestDataSampleProcessor.getCompanyStatistics(companyId));
	}

	@Override
	public long getRequestCountByCompany(String webId)
		throws MonitoringException {

		return getRequestCountByCompany(
			_serverPortletRequestDataSampleProcessor.getCompanyStatistics(webId));
	}

	@Override
	public long getRequestCountByPortlet(String portletId)
		throws MonitoringException {

		long requestCount = 0;

		for (PortletRequestDataSampleProcessor portletRequestDataSampleProcessor :
				_serverPortletRequestDataSampleProcessor.getCompanyStatisticsSet()) {

			requestCount += getRequestCountByPortlet(
				portletId, portletRequestDataSampleProcessor);
		}

		return requestCount;
	}

	@Override
	public long getRequestCountByPortlet(String portletId, long companyId)
		throws MonitoringException {

		return getRequestCountByPortlet(
			portletId, _serverPortletRequestDataSampleProcessor.getCompanyStatistics(companyId));
	}

	@Override
	public long getRequestCountByPortlet(String portletId, String webId)
		throws MonitoringException {

		return getRequestCountByPortlet(
			portletId, _serverPortletRequestDataSampleProcessor.getCompanyStatistics(webId));
	}

	@Override
	public long getSuccessCount() {
		long successCount = 0;

		for (PortletRequestDataSampleProcessor portletRequestDataSampleProcessor :
				_serverPortletRequestDataSampleProcessor.getCompanyStatisticsSet()) {

			successCount += getSuccessCountByCompany(
				portletRequestDataSampleProcessor);
		}

		return successCount;
	}

	@Override
	public long getSuccessCountByCompany(long companyId)
		throws MonitoringException {

		return getSuccessCountByCompany(
			_serverPortletRequestDataSampleProcessor.getCompanyStatistics(companyId));
	}

	@Override
	public long getSuccessCountByCompany(String webId)
		throws MonitoringException {

		return getSuccessCountByCompany(
			_serverPortletRequestDataSampleProcessor.getCompanyStatistics(webId));
	}

	@Override
	public long getSuccessCountByPortlet(String portletId)
		throws MonitoringException {

		long successCount = 0;

		for (PortletRequestDataSampleProcessor portletRequestDataSampleProcessor :
				_serverPortletRequestDataSampleProcessor.getCompanyStatisticsSet()) {

			successCount += getSuccessCountByPortlet(
				portletId, portletRequestDataSampleProcessor);
		}

		return successCount;
	}

	@Override
	public long getSuccessCountByPortlet(String portletId, long companyId)
		throws MonitoringException {

		return getSuccessCountByPortlet(
			portletId, _serverPortletRequestDataSampleProcessor.getCompanyStatistics(companyId));
	}

	@Override
	public long getSuccessCountByPortlet(String portletId, String webId)
		throws MonitoringException {

		return getSuccessCountByPortlet(
			portletId, _serverPortletRequestDataSampleProcessor.getCompanyStatistics(webId));
	}

	@Override
	public long getTimeoutCount() {
		long timeoutCount = 0;

		for (PortletRequestDataSampleProcessor portletRequestDataSampleProcessor :
				_serverPortletRequestDataSampleProcessor.getCompanyStatisticsSet()) {

			timeoutCount += getTimeoutCountByCompany(
				portletRequestDataSampleProcessor);
		}

		return timeoutCount;
	}

	@Override
	public long getTimeoutCountByCompany(long companyId)
		throws MonitoringException {

		return getTimeoutCountByCompany(
			_serverPortletRequestDataSampleProcessor.getCompanyStatistics(companyId));
	}

	@Override
	public long getTimeoutCountByCompany(String webId)
		throws MonitoringException {

		return getTimeoutCountByCompany(
			_serverPortletRequestDataSampleProcessor.getCompanyStatistics(webId));
	}

	@Override
	public long getTimeoutCountByPortlet(String portletId)
		throws MonitoringException {

		long timeoutCount = 0;

		for (PortletRequestDataSampleProcessor portletRequestDataSampleProcessor :
				_serverPortletRequestDataSampleProcessor.getCompanyStatisticsSet()) {

			timeoutCount += getTimeoutCountByPortlet(
				portletId, portletRequestDataSampleProcessor);
		}

		return timeoutCount;
	}

	@Override
	public long getTimeoutCountByPortlet(String portletId, long companyId)
		throws MonitoringException {

		return getTimeoutCountByPortlet(
			portletId, _serverPortletRequestDataSampleProcessor.getCompanyStatistics(companyId));
	}

	@Override
	public long getTimeoutCountByPortlet(String portletId, String webId)
		throws MonitoringException {

		return getTimeoutCountByPortlet(
			portletId, _serverPortletRequestDataSampleProcessor.getCompanyStatistics(webId));
	}

	protected long getAverageTimeByCompany(
		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor) {

		long averageTime = 0;

		Set<RequestStatistics> requestStatisticsSet =
			portletRequestDataSampleProcessor.getResourceRequestStatisticsSet();

		for (RequestStatistics requestStatistics : requestStatisticsSet) {
			averageTime += requestStatistics.getAverageTime();
		}

		if (!requestStatisticsSet.isEmpty()) {
			return averageTime / requestStatisticsSet.size();
		}

		return averageTime;
	}

	protected long getErrorCountByCompany(
		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor) {
		long errorCount = 0;

		for (RequestStatistics requestStatistics :
				portletRequestDataSampleProcessor.getResourceRequestStatisticsSet()) {

			errorCount += requestStatistics.getErrorCount();
		}

		return errorCount;
	}

	protected long getErrorCountByPortlet(
			String portletId, PortletRequestDataSampleProcessor portletRequestDataSampleProcessor)
		throws MonitoringException {

		RequestStatistics requestStatistics =
			portletRequestDataSampleProcessor.getResourceRequestStatistics(portletId);

		return requestStatistics.getErrorCount();
	}

	protected long getMaxTimeByPortlet(
			String portletId, PortletRequestDataSampleProcessor portletRequestDataSampleProcessor)
		throws MonitoringException {

		long maxTime = 0;

		RequestStatistics requestStatistics =
			portletRequestDataSampleProcessor.getResourceRequestStatistics(portletId);

		if (requestStatistics.getMaxTime() > maxTime) {
			maxTime = requestStatistics.getMaxTime();
		}

		return maxTime;
	}

	protected long getMinTimeByPortlet(
			String portletId, PortletRequestDataSampleProcessor portletRequestDataSampleProcessor)
		throws MonitoringException {

		long minTime = 0;

		RequestStatistics requestStatistics =
			portletRequestDataSampleProcessor.getResourceRequestStatistics(portletId);

		if (requestStatistics.getMinTime() < minTime) {
			minTime = requestStatistics.getMinTime();
		}

		return minTime;
	}

	protected long getRequestCountByCompany(
		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor) {

		long requestCount = 0;

		for (RequestStatistics requestStatistics :
				portletRequestDataSampleProcessor.getResourceRequestStatisticsSet()) {

			requestCount += requestStatistics.getRequestCount();
		}

		return requestCount;
	}

	protected long getRequestCountByPortlet(
			String portletId, PortletRequestDataSampleProcessor portletRequestDataSampleProcessor)
		throws MonitoringException {

		RequestStatistics requestStatistics =
			portletRequestDataSampleProcessor.getResourceRequestStatistics(portletId);

		return requestStatistics.getRequestCount();
	}

	protected long getSuccessCountByCompany(
		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor) {

		long successCount = 0;

		for (RequestStatistics requestStatistics :
				portletRequestDataSampleProcessor.getResourceRequestStatisticsSet()) {

			successCount += requestStatistics.getSuccessCount();
		}

		return successCount;
	}

	protected long getSuccessCountByPortlet(
			String portletId, PortletRequestDataSampleProcessor portletRequestDataSampleProcessor)
		throws MonitoringException {

		RequestStatistics requestStatistics =
			portletRequestDataSampleProcessor.getResourceRequestStatistics(portletId);

		return requestStatistics.getSuccessCount();
	}

	protected long getTimeoutCountByCompany(
		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor) {

		long timeoutCount = 0;

		for (RequestStatistics requestStatistics :
				portletRequestDataSampleProcessor.getResourceRequestStatisticsSet()) {

			timeoutCount += requestStatistics.getTimeoutCount();
		}

		return timeoutCount;
	}

	protected long getTimeoutCountByPortlet(
			String portletId, PortletRequestDataSampleProcessor portletRequestDataSampleProcessor)
		throws MonitoringException {

		RequestStatistics requestStatistics =
			portletRequestDataSampleProcessor.getResourceRequestStatistics(portletId);

		return requestStatistics.getTimeoutCount();
	}

	@Reference
	private ServerPortletRequestDataSampleProcessor
		_serverPortletRequestDataSampleProcessor;

}
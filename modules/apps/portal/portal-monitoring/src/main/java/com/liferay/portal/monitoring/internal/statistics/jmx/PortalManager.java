/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.monitoring.internal.statistics.jmx;

import com.liferay.portal.kernel.monitoring.MonitoringException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.monitoring.internal.statistics.portal.PortalRequestDataSampleProcessor;
import com.liferay.portal.monitoring.internal.statistics.portal.ServerSummaryStatistics;
import com.liferay.portal.monitoring.internal.statistics.util.RequestDataSampleProcessorHelper;

import java.util.Set;

import javax.management.DynamicMBean;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 * @author Brian Wing Shun Chan
 */
@Component(
	enabled = false,
	property = {
		"jmx.objectname=com.liferay.portal.monitoring:classification=portal_statistic,name=PortalManager",
		"jmx.objectname.cache.key=PortalManager"
	},
	service = DynamicMBean.class
)
public class PortalManager extends StandardMBean implements PortalManagerMBean {

	public PortalManager() throws NotCompliantMBeanException {
		super(PortalManagerMBean.class);
	}

	@Override
	public long getAverageTime() {
		return _serverSummaryStatistics.getAverageTime();
	}

	@Override
	public long getAverageTimeByCompany(long companyId)
		throws MonitoringException {

		return _serverSummaryStatistics.getAverageTimeByCompany(companyId);
	}

	@Override
	public long getAverageTimeByCompany(String webId)
		throws MonitoringException {

		return _serverSummaryStatistics.getAverageTimeByCompany(webId);
	}

	@Override
	public long[] getCompanyIds() {
		Set<Long> companyIds =
			_requestDataSampleProcessorHelper.getPortalCompanyIds();

		return ArrayUtil.toArray(companyIds.toArray(new Long[0]));
	}

	@Override
	public long getErrorCount() {
		return _serverSummaryStatistics.getErrorCount();
	}

	@Override
	public long getErrorCountByCompany(long companyId)
		throws MonitoringException {

		return _serverSummaryStatistics.getErrorCountByCompany(companyId);
	}

	@Override
	public long getErrorCountByCompany(String webId)
		throws MonitoringException {

		return _serverSummaryStatistics.getErrorCountByCompany(webId);
	}

	@Override
	public long getMaxTime() {
		return _serverSummaryStatistics.getMaxTime();
	}

	@Override
	public long getMaxTimeByCompany(long companyId) throws MonitoringException {
		return _serverSummaryStatistics.getMaxTimeByCompany(companyId);
	}

	@Override
	public long getMaxTimeByCompany(String webId) throws MonitoringException {
		return _serverSummaryStatistics.getMaxTimeByCompany(webId);
	}

	@Override
	public long getMinTime() {
		return _serverSummaryStatistics.getMinTime();
	}

	@Override
	public long getMinTimeByCompany(long companyId) throws MonitoringException {
		return _serverSummaryStatistics.getMinTimeByCompany(companyId);
	}

	@Override
	public long getMinTimeByCompany(String webId) throws MonitoringException {
		return _serverSummaryStatistics.getMinTimeByCompany(webId);
	}

	@Override
	public long getRequestCount() {
		return _serverSummaryStatistics.getRequestCount();
	}

	@Override
	public long getRequestCountByCompany(long companyId)
		throws MonitoringException {

		return _serverSummaryStatistics.getRequestCountByCompany(companyId);
	}

	@Override
	public long getRequestCountByCompany(String webId)
		throws MonitoringException {

		return _serverSummaryStatistics.getRequestCountByCompany(webId);
	}

	public long getStartTime(long companyId) throws MonitoringException {
		PortalRequestDataSampleProcessor portalRequestDataSampleProcessor =
			_requestDataSampleProcessorHelper.
				getPortalRequestDataSampleProcessor(companyId);

		return portalRequestDataSampleProcessor.getStartTime();
	}

	public long getStartTime(String webId) throws MonitoringException {
		PortalRequestDataSampleProcessor portalRequestDataSampleProcessor =
			_requestDataSampleProcessorHelper.
				getPortalRequestDataSampleProcessor(webId);

		return portalRequestDataSampleProcessor.getStartTime();
	}

	@Override
	public long getSuccessCount() {
		return _serverSummaryStatistics.getSuccessCount();
	}

	@Override
	public long getSuccessCountByCompany(long companyId)
		throws MonitoringException {

		return _serverSummaryStatistics.getSuccessCountByCompany(companyId);
	}

	@Override
	public long getSuccessCountByCompany(String webId)
		throws MonitoringException {

		return _serverSummaryStatistics.getSuccessCountByCompany(webId);
	}

	@Override
	public long getTimeoutCount() {
		return _serverSummaryStatistics.getTimeoutCount();
	}

	@Override
	public long getTimeoutCountByCompany(long companyId)
		throws MonitoringException {

		return _serverSummaryStatistics.getTimeoutCountByCompany(companyId);
	}

	@Override
	public long getTimeoutCountByCompany(String webId)
		throws MonitoringException {

		return _serverSummaryStatistics.getTimeoutCountByCompany(webId);
	}

	@Override
	public long getUptime(long companyId) throws MonitoringException {
		PortalRequestDataSampleProcessor portalRequestDataSampleProcessor =
			_requestDataSampleProcessorHelper.
				getPortalRequestDataSampleProcessor(companyId);

		return portalRequestDataSampleProcessor.getUptime();
	}

	@Override
	public long getUptime(String webId) throws MonitoringException {
		PortalRequestDataSampleProcessor portalRequestDataSampleProcessor =
			_requestDataSampleProcessorHelper.
				getPortalRequestDataSampleProcessor(webId);

		return portalRequestDataSampleProcessor.getUptime();
	}

	@Override
	public String[] getWebIds() {
		Set<String> webIds =
			_requestDataSampleProcessorHelper.getPortalWebIds();

		return webIds.toArray(new String[0]);
	}

	@Override
	public void reset() {
		_requestDataSampleProcessorHelper.
			resetPortalRequestDataSampleProcessor();
	}

	@Override
	public void reset(long companyId) {
		_requestDataSampleProcessorHelper.resetPortalRequestDataSampleProcessor(
			companyId);
	}

	@Override
	public void reset(String webId) {
		_requestDataSampleProcessorHelper.resetPortalRequestDataSampleProcessor(
			webId);
	}

	@Reference
	private RequestDataSampleProcessorHelper _requestDataSampleProcessorHelper;

	@Reference
	private ServerSummaryStatistics _serverSummaryStatistics;

}
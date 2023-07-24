/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.monitoring.internal.statistics.portlet;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.monitoring.DataSampleProcessor;
import com.liferay.portal.kernel.monitoring.MonitoringException;
import com.liferay.portal.monitoring.internal.statistics.util.RequestDataSampleProcessorHelper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 * @author Brian Wing Shun Chan
 */
@Component(
	enabled = false, property = "namespace=com.liferay.monitoring.Portlet",
	service = DataSampleProcessor.class
)
public class ServerPortletRequestDataSampleProcessor
	implements DataSampleProcessor<PortletRequestDataSample> {

	@Override
	public void processDataSample(
			PortletRequestDataSample portletRequestDataSample)
		throws MonitoringException {

		long companyId = portletRequestDataSample.getCompanyId();

		PortletRequestDataSampleProcessor portletRequestDataSampleProcessor =
			_requestDataSampleProcessorHelper.
				getPortletRequestDataSampleProcessorByCompanyId(companyId);

		if (portletRequestDataSampleProcessor == null) {
			try {
				Company company =
					_requestDataSampleProcessorHelper.getCompanyByCompanyId(
						companyId);

				portletRequestDataSampleProcessor =
					_requestDataSampleProcessorHelper.
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

	@Reference
	private RequestDataSampleProcessorHelper _requestDataSampleProcessorHelper;

}
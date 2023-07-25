/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.monitoring.internal.statistics.service;

import com.liferay.portal.kernel.monitoring.DataSampleProcessor;
import com.liferay.portal.kernel.monitoring.MethodSignature;
import com.liferay.portal.monitoring.internal.statistics.util.RequestDataSampleProcessorHelper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	enabled = false, property = "namespace=com.liferay.monitoring.Service",
	service = ServerServiceRequestDataSampleProcessor.class
)
public class ServerServiceRequestDataSampleProcessor
	implements DataSampleProcessor<ServiceRequestDataSample> {

	@Override
	public void processDataSample(
		ServiceRequestDataSample serviceRequestDataSample) {

		MethodSignature methodSignature =
			serviceRequestDataSample.getMethodSignature();

		String className = methodSignature.getClassName();

		ServiceRequestDataSampleProcessor serviceRequestDataSampleProcessor =
			_requestDataSampleProcessorHelper.
				getServiceRequestDataSampleProcessorByClassName(className);

		if (serviceRequestDataSampleProcessor == null) {
			serviceRequestDataSampleProcessor =
				new ServiceRequestDataSampleProcessor(className);

			_requestDataSampleProcessorHelper.setServiceStatistics(
				className, serviceRequestDataSampleProcessor);
		}

		serviceRequestDataSampleProcessor.processDataSample(
			serviceRequestDataSample);
	}

	@Reference
	private RequestDataSampleProcessorHelper _requestDataSampleProcessorHelper;

}
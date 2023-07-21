/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.monitoring.internal.statistics.service;

import com.liferay.portal.kernel.monitoring.DataSampleProcessor;
import com.liferay.portal.kernel.monitoring.MethodSignature;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(
	enabled = false, property = "namespace=com.liferay.monitoring.Service",
	service = {DataSampleProcessor.class, ServerServiceRequestDataSampleProcessor.class}
)
public class ServerServiceRequestDataSampleProcessor
	implements DataSampleProcessor<ServiceRequestDataSample> {

	public long getAverageTime(
		String className, String methodName, String[] parameterTypes) {

		ServiceRequestDataSampleProcessor serviceRequestDataSampleProcessor = _serviceStatistics.get(className);

		if (serviceRequestDataSampleProcessor != null) {
			return serviceRequestDataSampleProcessor.getAverageTime(methodName, parameterTypes);
		}

		return -1;
	}

	public long getErrorCount(
		String className, String methodName, String[] parameterTypes) {

		ServiceRequestDataSampleProcessor serviceRequestDataSampleProcessor = _serviceStatistics.get(className);

		if (serviceRequestDataSampleProcessor != null) {
			return serviceRequestDataSampleProcessor.getErrorCount(methodName, parameterTypes);
		}

		return -1;
	}

	public long getMaxTime(
		String className, String methodName, String[] parameterTypes) {

		ServiceRequestDataSampleProcessor serviceRequestDataSampleProcessor = _serviceStatistics.get(className);

		if (serviceRequestDataSampleProcessor != null) {
			return serviceRequestDataSampleProcessor.getMaxTime(methodName, parameterTypes);
		}

		return -1;
	}

	public long getMinTime(
		String className, String methodName, String[] parameterTypes) {

		ServiceRequestDataSampleProcessor serviceRequestDataSampleProcessor = _serviceStatistics.get(className);

		if (serviceRequestDataSampleProcessor != null) {
			return serviceRequestDataSampleProcessor.getMinTime(methodName, parameterTypes);
		}

		return -1;
	}

	public long getRequestCount(
		String className, String methodName, String[] parameterTypes) {

		ServiceRequestDataSampleProcessor serviceRequestDataSampleProcessor = _serviceStatistics.get(className);

		if (serviceRequestDataSampleProcessor != null) {
			return serviceRequestDataSampleProcessor.getRequestCount(
				methodName, parameterTypes);
		}

		return -1;
	}

	@Override
	public void processDataSample(
		ServiceRequestDataSample serviceRequestDataSample) {

		MethodSignature methodSignature =
			serviceRequestDataSample.getMethodSignature();

		String className = methodSignature.getClassName();

		ServiceRequestDataSampleProcessor serviceRequestDataSampleProcessor = _serviceStatistics.get(className);

		if (serviceRequestDataSampleProcessor == null) {
			serviceRequestDataSampleProcessor = new ServiceRequestDataSampleProcessor(className);

			_serviceStatistics.put(className, serviceRequestDataSampleProcessor);
		}

		serviceRequestDataSampleProcessor.processDataSample(serviceRequestDataSample);
	}

	private final Map<String, ServiceRequestDataSampleProcessor> _serviceStatistics =
		new ConcurrentHashMap<>();

}
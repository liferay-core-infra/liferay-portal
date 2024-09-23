/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.partition.internal.operation;

import com.liferay.portal.kernel.concurrent.SystemExecutorServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.util.PropsValues;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Mariano Álvaro Sáiz
 */
public abstract class BaseVirtualInstanceOperation {

	public abstract String getOperationCompletedMessage(long companyId);

	public void onVirtualInstance(
		Callable<Company> callable, Map<String, Object> properties) {

		try {
			ExecutorService executorService =
				SystemExecutorServiceUtil.getExecutorService();

			List<Future<Company>> futures = executorService.invokeAll(
				Collections.singleton(callable));

			Future<Company> future = futures.get(0);

			Company company = future.get();

			if (company != null) {
				_deleteConfiguration(
					"com.liferay.portal.instances.internal.configuration." +
						"PortalInstancesConfiguration~" + company.getWebId());

				if (_log.isInfoEnabled()) {
					_log.info(
						getOperationCompletedMessage(company.getCompanyId()));
				}
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to perform operation on virtual instance", exception);
		}
		finally {
			_deleteConfiguration((String)properties.get("service.pid"));
		}
	}

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	protected ModuleServiceLifecycle moduleServiceLifecycle;

	private void _deleteConfiguration(String pid) {
		try {
			Files.deleteIfExists(
				Paths.get(
					PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR,
					pid.concat(".config")));
		}
		catch (IOException ioException) {
			_log.error(ioException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseVirtualInstanceOperation.class);

}
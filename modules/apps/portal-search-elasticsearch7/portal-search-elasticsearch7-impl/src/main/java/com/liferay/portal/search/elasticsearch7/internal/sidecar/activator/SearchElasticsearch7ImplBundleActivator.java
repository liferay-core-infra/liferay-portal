/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar.activator;

import com.liferay.petra.process.ProcessChannel;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.portal.kernel.concurrent.SystemExecutorServiceUtil;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.PersistedProcessUtil;

import java.io.File;
import java.io.Serializable;

import java.nio.file.Files;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Tina Tian
 */
public class SearchElasticsearch7ImplBundleActivator
	implements BundleActivator {

	public static Future<ProcessChannel<Serializable>>
		getProcessChannelFuture() {

		return _processChannelFuture;
	}

	public static byte[] getProcessFileContent() {
		return _processFileContent;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		File sidecarProcessFile = bundleContext.getDataFile("sidecar.process");

		if (!sidecarProcessFile.exists()) {
			return;
		}

		_processFileContent = Files.readAllBytes(sidecarProcessFile.toPath());

		ServiceReference<ProcessExecutor> serviceReference =
			bundleContext.getServiceReference(ProcessExecutor.class);

		ExecutorService executorService =
			SystemExecutorServiceUtil.getExecutorService();

		_processChannelFuture = executorService.submit(
			() -> PersistedProcessUtil.start(
				bundleContext.getService(serviceReference),
				_processFileContent));
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
	}

	private static volatile Future<ProcessChannel<Serializable>>
		_processChannelFuture;
	private static volatile byte[] _processFileContent;

}
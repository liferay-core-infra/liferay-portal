/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.concurrent.FutureListener;
import com.liferay.petra.concurrent.NoticeableFuture;
import com.liferay.petra.process.ProcessChannel;
import com.liferay.petra.process.ProcessException;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.search.elasticsearch7.internal.util.ResourceUtil;

import java.io.IOException;
import java.io.Serializable;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Tina Tian
 */
public class Sidecar {

	public static final String DEFAULT_MODULES_FOLDER_NAME = "modules";

	public static final String SIDECAR_MODULES_FOLDER_NAME =
		"liferay-sidecar-modules";

	public Sidecar(
		ProcessExecutor processExecutor,
		FutureListener<Serializable> restartFutureListener,
		SidecarRuntimeConfiguration sidecarRuntimeConfiguration) {

		_processExecutor = processExecutor;
		_restartFutureListener = restartFutureListener;
		_sidecarRuntimeConfiguration = sidecarRuntimeConfiguration;

		_sidecarHomePath = sidecarRuntimeConfiguration.getHomePath();
	}

	public String getNetworkHostAddress() {
		return _address;
	}

	public boolean isStopped() {
		return _stopped;
	}

	public void start() {
		if (isStopped()) {
			throw new IllegalStateException();
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Sidecar Elasticsearch starting");
		}

		ProcessChannel<Serializable> processChannel =
			_executeSidecarMainProcess();

		NoticeableFuture<Serializable> noticeableFuture =
			processChannel.getProcessNoticeableFuture();

		if (_restartFutureListener != null) {
			noticeableFuture.addFutureListener(_restartFutureListener);
		}

		String address = _startElasticsearch(processChannel);

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Sidecar Elasticsearch ",
					_sidecarRuntimeConfiguration.getVersion(), StringPool.SPACE,
					_sidecarRuntimeConfiguration.getNodeName(), " started at ",
					address));
		}

		_address = address;
		_processChannel = processChannel;
	}

	public void stop() {
		if (_log.isDebugEnabled()) {
			_log.debug("Stopping sidecar Elasticsearch");
		}

		if (_processChannel != null) {
			NoticeableFuture<Serializable> noticeableFuture =
				_processChannel.getProcessNoticeableFuture();

			if (_restartFutureListener != null) {
				noticeableFuture.removeFutureListener(_restartFutureListener);
			}

			_processChannel.write(new StopSidecarProcessCallable());

			try {
				noticeableFuture.get(
					_sidecarRuntimeConfiguration.getShutdownTimeout(),
					TimeUnit.MILLISECONDS);
			}
			catch (Exception exception) {
				if (!noticeableFuture.isDone()) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Forcibly shutdown sidecar Elasticsearch ",
								"process because it did not shut down in ",
								_sidecarRuntimeConfiguration.
									getShutdownTimeout(),
								" ms"),
							exception);
					}

					noticeableFuture.cancel(true);
				}
			}

			_processChannel = null;
		}

		PathUtil.deleteDir(_sidecarRuntimeConfiguration.getTempDirPath());

		_stopped = true;
	}

	private ProcessChannel<Serializable> _executeSidecarMainProcess() {
		if (!Files.isDirectory(_sidecarHomePath)) {
			throw new IllegalArgumentException(
				"Sidecar Elasticsearch home does not exist: " +
					_sidecarHomePath);
		}

		Path sidecarTempDirPath = _sidecarRuntimeConfiguration.getTempDirPath();

		Path configFolder = sidecarTempDirPath.resolve("config");

		try {
			Files.createDirectories(configFolder);

			Files.write(
				configFolder.resolve("log4j2.properties"),
				List.of(
					ResourceUtil.getResourceAsString(
						Sidecar.class, "/log4j2.properties")));
		}
		catch (IOException ioException) {
			_log.error(
				"Unable to copy log4j2.properties to " + configFolder,
				ioException);
		}

		try {
			return _processExecutor.execute(
				_sidecarRuntimeConfiguration.getProcessConfig(),
				new SidecarMainProcessCallable(
					_sidecarRuntimeConfiguration.getHeartbeatInterval()));
		}
		catch (ProcessException processException) {
			throw new RuntimeException(
				"Unable to start sidecar Elasticsearch process",
				processException);
		}
	}

	private String _startElasticsearch(
		ProcessChannel<Serializable> processChannel) {

		NoticeableFuture<String> noticeableFuture = processChannel.write(
			new StartSidecarProcessCallable(
				_sidecarRuntimeConfiguration.getSidecarServerArgs()));

		try {
			return _waitForPublishedAddress(noticeableFuture);
		}
		catch (IOException ioException) {
			if (Objects.equals(ioException.getMessage(), "Stream closed")) {
				throw new RuntimeException(
					StringBundler.concat(
						"Sidecar JVM did not launch successfully. ",
						SidecarMainProcessCallable.class.getSimpleName(),
						" may have crashed, or its classpath may be missing ",
						"required libraries"),
					ioException);
			}

			processChannel.write(new StopSidecarProcessCallable());

			throw new RuntimeException(ioException);
		}
		catch (Exception exception) {
			processChannel.write(new StopSidecarProcessCallable());

			if (exception instanceof RuntimeException) {
				throw (RuntimeException)exception;
			}

			throw new RuntimeException(exception);
		}
	}

	private String _waitForPublishedAddress(
			NoticeableFuture<String> noticeableFuture)
		throws Exception {

		try {
			return noticeableFuture.get();
		}
		catch (ExecutionException executionException) {
			throw new Exception(executionException.getCause());
		}
		catch (InterruptedException interruptedException) {
			throw new RuntimeException(interruptedException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(Sidecar.class);

	private String _address;
	private ProcessChannel<Serializable> _processChannel;
	private final ProcessExecutor _processExecutor;
	private final FutureListener<Serializable> _restartFutureListener;
	private final Path _sidecarHomePath;
	private final SidecarRuntimeConfiguration _sidecarRuntimeConfiguration;
	private volatile boolean _stopped;

}
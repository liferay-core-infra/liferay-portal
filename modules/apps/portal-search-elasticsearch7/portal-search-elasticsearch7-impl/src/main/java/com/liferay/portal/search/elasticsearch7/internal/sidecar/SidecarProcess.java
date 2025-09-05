/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.concurrent.NoticeableFuture;
import com.liferay.petra.io.Deserializer;
import com.liferay.petra.io.Serializer;
import com.liferay.petra.process.ProcessChannel;
import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.petra.process.ProcessLog;
import com.liferay.petra.process.local.LocalProcessExecutor;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import java.net.URL;

import java.nio.ByteBuffer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Tina Tian
 */
public class SidecarProcess {

	public static final String TEMP_DIR =
		SystemProperties.get(SystemProperties.TMP_DIR) + "/sidecar";

	public static void init(SidecarProcessBag sidecarProcessBag) {
		SidecarProcess sidecarProcess =
			_sidecarProcessDCLSingleton.getSingleton(SidecarProcess::new);

		sidecarProcess._init(sidecarProcessBag);
	}

	public static void start() {
		SidecarProcess sidecarProcess =
			_sidecarProcessDCLSingleton.getSingleton(SidecarProcess::new);

		sidecarProcess._start();
	}

	public static void stop() {
		_sidecarProcessDCLSingleton.destroy(SidecarProcess::_stop);
	}

	private void _consumeProcessLog(ProcessLog processLog) {
		if (ProcessLog.Level.DEBUG == processLog.getLevel()) {
			if (_log.isDebugEnabled()) {
				_log.debug(processLog.getMessage(), processLog.getThrowable());
			}
		}
		else if (ProcessLog.Level.INFO == processLog.getLevel()) {
			if (_log.isInfoEnabled()) {
				_log.info(processLog.getMessage(), processLog.getThrowable());
			}
		}
		else if (ProcessLog.Level.WARN == processLog.getLevel()) {
			if (_log.isWarnEnabled()) {
				_log.warn(processLog.getMessage(), processLog.getThrowable());
			}
		}
		else {
			_log.error(processLog.getMessage(), processLog.getThrowable());
		}
	}

	private String _createClasspath(
		Path dirPath, DirectoryStream.Filter<Path> filter) {

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
				dirPath, filter)) {

			StringBundler sb = new StringBundler();

			directoryStream.forEach(
				path -> {
					sb.append(path);
					sb.append(File.pathSeparator);
				});

			if (sb.index() > 0) {
				sb.setIndex(sb.index() - 1);
			}

			return sb.toString();
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to iterate " + dirPath, ioException);
		}
	}

	private boolean _fileNameContains(Path path, String s) {
		String name = String.valueOf(path.getFileName());

		return name.contains(s);
	}

	private String _getBootstrapClassPath() {
		return _createClasspath(
			Paths.get(PropsValues.LIFERAY_SHIELDED_CONTAINER_LIB_PORTAL_DIR),
			path -> _fileNameContains(path, "petra"));
	}

	private URL _getBundleURL(Class<?> clazz) {
		ProtectionDomain protectionDomain = clazz.getProtectionDomain();

		CodeSource codeSource = protectionDomain.getCodeSource();

		return codeSource.getLocation();
	}

	private synchronized void _init(SidecarProcessBag sidecarProcessBag) {
		if (Objects.equals(sidecarProcessBag, _sidecarProcessBag)) {
			return;
		}

		_stop();

		FileUtil.deltree(TEMP_DIR);

		Serializer serializer = new Serializer();

		serializer.writeObject(sidecarProcessBag);

		ByteBuffer byteBuffer = serializer.toByteBuffer();

		try {
			FileUtil.write(
				new File(TEMP_DIR, SidecarProcess.class.getName()),
				byteBuffer.array());
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to initialize sidecar process information",
				ioException);
		}
	}

	private synchronized void _start() {
		if (_processChannel != null) {
			return;
		}

		File sidecarProcessFile = new File(
			TEMP_DIR, SidecarProcess.class.getName());

		if (!sidecarProcessFile.exists()) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Skip starting Sidecar process as " + sidecarProcessFile +
						" does not exist");
			}

			return;
		}

		try {
			Deserializer deserializer = new Deserializer(
				ByteBuffer.wrap(FileUtil.getBytes(sidecarProcessFile)));

			SidecarProcessBag sidecarProcessBag = deserializer.readObject();

			List<String> log4j2Properties =
				sidecarProcessBag.getLog4j2Properties();

			StringBundler sb = new StringBundler(2 * log4j2Properties.size());

			for (String log4j2Property : log4j2Properties) {
				sb.append(log4j2Property);
				sb.append(System.lineSeparator());
			}

			FileUtil.write(
				sidecarProcessBag.getLog4j2PropertiesFile(), sb.toString());

			ProcessConfig.Builder builder = new ProcessConfig.Builder();

			URL bundleURL = _getBundleURL(SidecarProcess.class);

			String bootstrapClassPath = _getBootstrapClassPath();

			ProcessConfig processConfig = builder.setArguments(
				sidecarProcessBag.getJvmArguments()
			).setBootstrapClassPath(
				bootstrapClassPath
			).setEnvironment(
				sidecarProcessBag.getSystemEnvironments()
			).setJavaExecutable(
				System.getProperty("java.home") + "/bin/java"
			).setProcessLogConsumer(
				this::_consumeProcessLog
			).setReactClassLoader(
				SidecarProcess.class.getClassLoader()
			).setRuntimeClassPath(
				StringBundler.concat(
					bundleURL.getPath(), File.pathSeparator, bootstrapClassPath)
			).build();

			_processChannel = _processExecutor.execute(
				processConfig,
				new SidecarMainProcessCallable(
					sidecarProcessBag.getHeartbeatInterval()));

			NoticeableFuture<String> noticeableFuture = _processChannel.write(
				new StartSidecarProcessCallable(
					sidecarProcessBag.getSidecarServerBytes()));

			noticeableFuture.get();

			_sidecarProcessBag = sidecarProcessBag;
		}
		catch (Exception exception) {
			_stop();

			FileUtil.deltree(TEMP_DIR);

			if (exception instanceof RuntimeException) {
				throw (RuntimeException)exception;
			}

			throw new RuntimeException(
				"Unable to start sidecar process", exception);
		}
	}

	private synchronized void _stop() {
		if (_processChannel != null) {
			NoticeableFuture<Serializable> noticeableFuture =
				_processChannel.getProcessNoticeableFuture();

			_processChannel.write(new StopSidecarProcessCallable());

			try {
				noticeableFuture.get(
					_sidecarProcessBag.getShutdownTimeout(),
					TimeUnit.MILLISECONDS);
			}
			catch (Exception exception) {
				if (!noticeableFuture.isDone()) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Forcibly shutdown sidecar Elasticsearch ",
								"process because it did not shut down in ",
								_sidecarProcessBag.getShutdownTimeout(), " ms"),
							exception);
					}

					noticeableFuture.cancel(true);
				}
			}

			_processChannel = null;
			_sidecarProcessBag = null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(SidecarProcess.class);

	private static final ProcessExecutor _processExecutor =
		new LocalProcessExecutor();
	private static final DCLSingleton<SidecarProcess>
		_sidecarProcessDCLSingleton = new DCLSingleton<>();

	private ProcessChannel<Serializable> _processChannel;
	private SidecarProcessBag _sidecarProcessBag;

}
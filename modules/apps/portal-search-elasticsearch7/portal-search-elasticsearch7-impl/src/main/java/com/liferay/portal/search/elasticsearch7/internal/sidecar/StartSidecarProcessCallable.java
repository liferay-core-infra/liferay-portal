/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.io.OutputStreamWriter;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.process.ProcessCallable;
import com.liferay.petra.process.ProcessException;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.lang.reflect.Method;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.security.MessageDigest;

import java.util.Arrays;

import org.elasticsearch.common.hash.MessageDigests;
import org.elasticsearch.common.io.stream.OutputStreamStreamOutput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.logging.LogConfigurator;
import org.elasticsearch.common.settings.KeyStoreWrapper;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.xcontent.XContentType;

/**
 * @author Tina Tian
 */
public class StartSidecarProcessCallable
	implements ProcessCallable<Serializable> {

	@Override
	public Serializable call() throws ProcessException {
		System.setProperty("es.distribution.type", "tar");
		System.setProperty("es.networkaddress.cache.negative.ttl", "10");
		System.setProperty("es.networkaddress.cache.ttl", "60");
		System.setProperty("file.encoding", "UTF-8");
		System.setProperty("io.netty.noKeySetOptimization", "true");
		System.setProperty("io.netty.noUnsafe", "true");
		System.setProperty("io.netty.recycler.maxCapacityPerThread", "0");
		System.setProperty("java.awt.headless", "true");
		System.setProperty("jna.nosys", "true");
		System.setProperty("log4j.shutdownHookEnabled", "false");
		System.setProperty("log4j2.disable.jmx", "true");
		System.setProperty("log4j2.formatMsgNoLookups", "true");
		System.setProperty(
			"org.apache.lucene.vectorization.upperJavaFeatureVersion", "21");
		System.setProperty("jdk.module.main", "org.elasticsearch.server");

		Path bundleDataPath = Path.of(
			System.getProperty("sidecar.bundle.data.path"));

		Path configFolder = bundleDataPath.resolve("config");

		Path log4jPropertiesPath = configFolder.resolve("log4j2.properties");

		try {
			byte[] log4jProperties = _getLog4jProperties();

			File log4jPropertiesFile = log4jPropertiesPath.toFile();

			if (log4jPropertiesFile.exists() &&
				!Arrays.equals(
					log4jProperties, Files.readAllBytes(log4jPropertiesPath))) {

				log4jPropertiesFile.delete();
			}

			if (!log4jPropertiesFile.exists()) {
				Files.createDirectories(configFolder);

				Files.write(log4jPropertiesPath, log4jProperties);
			}
		}
		catch (IOException ioException) {
			throw new ProcessException(
				"Unable to create log4j2.properties", ioException);
		}

		ElasticsearchServerUtil.start(_getSidecarServerArgs(configFolder));

		return null;
	}

	private byte[] _getLog4jProperties() throws IOException {
		ClassLoader classLoader =
			StartSidecarProcessCallable.class.getClassLoader();

		try (InputStream inputStream = classLoader.getResourceAsStream(
				"log4j2.properties");
			UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream();
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
				unsyncByteArrayOutputStream)) {

			outputStreamWriter.write(
				StringBundler.concat(
					"logger.bootstrapchecks.name=org.elasticsearch.bootstrap.",
					"BootstrapChecks\n", "logger.bootstrapchecks.level=error\n",
					"logger.deprecation.name=org.elasticsearch.deprecation\n",
					"logger.deprecation.level=error\n"));

			outputStreamWriter.write(StringUtil.read(inputStream));

			outputStreamWriter.flush();

			return unsyncByteArrayOutputStream.toByteArray();
		}
	}

	private byte[] _getSidecarServerArgs(Path configFolder) {
		try (UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream();
			StreamOutput streamOutput = new OutputStreamStreamOutput(
				unsyncByteArrayOutputStream)) {

			streamOutput.writeBoolean(false);
			streamOutput.writeBoolean(false);
			streamOutput.writeOptionalString(null);
			streamOutput.writeString(KeyStoreWrapper.class.getName());

			try (KeyStoreWrapper keyStoreWrapper = KeyStoreWrapper.create()) {
				streamOutput.writeInt(keyStoreWrapper.getFormatVersion());
				streamOutput.writeBoolean(keyStoreWrapper.hasPassword());
				streamOutput.writeBoolean(false);
				streamOutput.writeVInt(1);
				streamOutput.writeString(KeyStoreWrapper.SEED_SETTING.getKey());

				ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(
					ElasticsearchServerUtil.class.getSimpleName());

				byte[] bytes = byteBuffer.array();

				MessageDigest messageDigest = MessageDigests.sha256();

				streamOutput.writeByteArray(bytes);
				streamOutput.writeByteArray(messageDigest.digest(bytes));
				streamOutput.writeBoolean(false);
			}

			Method method = ReflectionUtil.getDeclaredMethod(
				LogConfigurator.class, "configureESLogging");

			method.invoke(null);

			Settings.Builder builder = Settings.builder();

			builder.loadFromSource(
				System.getProperty("sidecar.settings"), XContentType.YAML);

			Settings settings = builder.build();

			method = ReflectionUtil.getDeclaredMethod(
				Settings.class, "writeTo", new Class<?>[] {StreamOutput.class});

			method.invoke(settings, streamOutput);

			streamOutput.writeString(
				String.valueOf(configFolder.toAbsolutePath()));
			streamOutput.writeString(settings.get("path.logs"));

			streamOutput.flush();

			return unsyncByteArrayOutputStream.toByteArray();
		}
		catch (Exception exception) {
			throw new IllegalStateException(
				"Unable to prepare sidecar server arguments", exception);
		}
	}

	private static final long serialVersionUID = 1L;

}
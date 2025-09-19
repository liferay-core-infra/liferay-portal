/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.process.ProcessCallable;
import com.liferay.petra.process.ProcessException;

import java.io.Serializable;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;

import org.elasticsearch.common.hash.MessageDigests;
import org.elasticsearch.common.io.stream.OutputStreamStreamOutput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.settings.KeyStoreWrapper;

/**
 * @author Tina Tian
 */
public class StartSidecarProcessCallable
	implements ProcessCallable<Serializable> {

	public StartSidecarProcessCallable(byte[] settings) {
		_settings = settings;
	}

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

		ElasticsearchServerUtil.start(_getSidecarServerArgs());

		return null;
	}

	private byte[] _getSidecarServerArgs() {
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

			streamOutput.writeBytes(_settings);

			streamOutput.writeString(System.getProperty("es.path.conf"));
			streamOutput.writeString(System.getProperty("es.path.log"));

			streamOutput.flush();

			return unsyncByteArrayOutputStream.toByteArray();
		}
		catch (Exception exception) {
			throw new IllegalStateException(
				"Unable to prepare sidecar server arguments", exception);
		}
	}

	private static final long serialVersionUID = 1L;

	private final byte[] _settings;

}
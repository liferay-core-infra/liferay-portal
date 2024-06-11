/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.support.tomcat.session;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.apache.catalina.ha.session.DeltaRequest;

/**
 * @author Shuyang Zhou
 */
public class LiferayDeltaRequest extends DeltaRequest {

	public static void init(
		Function<InputStream, ObjectInput> objectInputFunction,
		Function<OutputStream, ObjectOutput> objectOutputFunction) {

		_objectInputFunctionFuture.complete(objectInputFunction);
		_objectOutputFunctionFuture.complete(objectOutputFunction);
	}

	public LiferayDeltaRequest() {
	}

	public LiferayDeltaRequest(String sessionId, boolean recordAllActions) {
		super(sessionId, recordAllActions);
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		try {
			int size = objectInput.readInt();

			byte[] data = new byte[size];

			objectInput.readFully(data);

			Function<InputStream, ObjectInput> objectInputFunction =
				_objectInputFunctionFuture.get();

			try (ObjectInput liferayObjectInput = objectInputFunction.apply(
					new ByteArrayInputStream(data))) {

				super.readExternal(liferayObjectInput);
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		try {
			ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream();

			Function<OutputStream, ObjectOutput> objectOutputFunction =
				_objectOutputFunctionFuture.get();

			try (ObjectOutput liferayObjectOutput = objectOutputFunction.apply(
					byteArrayOutputStream)) {

				super.writeExternal(liferayObjectOutput);
			}

			byte[] data = byteArrayOutputStream.toByteArray();

			objectOutput.writeInt(data.length);
			objectOutput.write(data);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private static final CompletableFuture<Function<InputStream, ObjectInput>>
		_objectInputFunctionFuture = new CompletableFuture<>();
	private static final CompletableFuture<Function<OutputStream, ObjectOutput>>
		_objectOutputFunctionFuture = new CompletableFuture<>();

}
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.zip;

import com.liferay.petra.memory.DeleteFileFinalizeAction;
import com.liferay.petra.memory.FinalizeManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.kernel.zip.ZipWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import java.net.URI;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

import java.util.Collections;

/**
 * @author Raymond Augé
 */
public class ZipWriterImpl extends BaseZipWriter implements ZipWriter {

	public ZipWriterImpl() {
		this(
			new File(
				StringBundler.concat(
					SystemProperties.get(SystemProperties.TMP_DIR),
					StringPool.SLASH, PortalUUIDUtil.generate(), ".zip")));

		FinalizeManager.register(
			_file, new DeleteFileFinalizeAction(_file.getAbsolutePath()),
			FinalizeManager.PHANTOM_REFERENCE_FACTORY);
	}

	public ZipWriterImpl(File file) {
		_file = file.getAbsoluteFile();

		URI fileURI = _file.toURI();

		_uri = URI.create("jar:file:" + fileURI.getPath());

		try (FileSystem fileSystem = FileSystems.newFileSystem(
				_uri, Collections.singletonMap("create", "true"))) {
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}
	}

	@Override
	public void addEntry(String name, byte[] bytes) throws IOException {
		if (bytes == null) {
			return;
		}

		try (FileSystem fileSystem = FileSystems.newFileSystem(
				_uri, Collections.emptyMap())) {

			addEntry(fileSystem.getPath(name), bytes);
		}
	}

	@Override
	public void addEntry(String name, InputStream inputStream)
		throws IOException {

		if (inputStream == null) {
			return;
		}

		try (FileSystem fileSystem = FileSystems.newFileSystem(
				_uri, Collections.emptyMap())) {

			addEntry(fileSystem.getPath(name), inputStream);
		}
	}

	@Override
	public File getFile() {
		return _file;
	}

	private final File _file;
	private final URI _uri;

}
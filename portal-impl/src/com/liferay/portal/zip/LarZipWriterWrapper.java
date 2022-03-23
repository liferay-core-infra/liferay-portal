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

import com.liferay.petra.io.StreamUtil;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterWrapper;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Minhchau Dang
 */
public class LarZipWriterWrapper extends ZipWriterWrapper {

	public LarZipWriterWrapper(ZipWriter zipWriter) {
		super(zipWriter);
	}

	@Override
	public void addEntry(String name, byte[] bytes) throws IOException {
		if (_exportEntries == null) {
			_exportEntries = new LinkedList<>();
		}

		_exportEntries.add(new AbstractMap.SimpleImmutableEntry<>(name, bytes));

		_exportEntriesBytes += bytes.length;

		if (_exportEntriesBytes >=
				PropsValues.ZIP_FILE_WRITER_EXPORT_BUFFER_SIZE) {

			_writeExportEntries();
		}
	}

	@Override
	public void addEntry(String name, InputStream inputStream)
		throws IOException {

		addEntry(name, StreamUtil.toByteArray(inputStream));
	}

	@Override
	public File getFile() {
		if (_exportEntries != null) {
			_writeExportEntries();
		}

		return super.getFile();
	}

	private void _writeExportEntries() {
		try {
			Iterator<Map.Entry<String, byte[]>> iterator =
				_exportEntries.iterator();

			while (iterator.hasNext()) {
				Map.Entry<String, byte[]> entry = iterator.next();

				iterator.remove();

				LarZipWriterWrapper.super.addEntry(
					entry.getKey(), entry.getValue());
			}

			_exportEntries = null;
			_exportEntriesBytes = 0;
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}
	}

	private List<Map.Entry<String, byte[]>> _exportEntries;
	private long _exportEntriesBytes;

}
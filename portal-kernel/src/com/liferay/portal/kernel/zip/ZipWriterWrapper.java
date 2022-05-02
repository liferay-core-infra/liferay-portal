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

package com.liferay.portal.kernel.zip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Minhchau Dang
 */
public class ZipWriterWrapper implements ZipWriter {

	public ZipWriterWrapper(ZipWriter zipWriter) {
		_zipWriter = zipWriter;
	}

	@Override
	public void addEntry(String name, byte[] bytes) throws IOException {
		_zipWriter.addEntry(name, bytes);
	}

	@Override
	public void addEntry(String name, InputStream inputStream)
		throws IOException {

		_zipWriter.addEntry(name, inputStream);
	}

	@Override
	public void addEntry(String name, String s) throws IOException {
		_zipWriter.addEntry(name, s);
	}

	@Override
	public void addEntry(String name, StringBuilder sb) throws IOException {
		_zipWriter.addEntry(name, sb);
	}

	@Override
	public byte[] finish() throws IOException {
		return _zipWriter.finish();
	}

	@Override
	public File getFile() {
		return _zipWriter.getFile();
	}

	@Override
	public String getPath() {
		return _zipWriter.getPath();
	}

	@Override
	public void umount() {
		_zipWriter.umount();
	}

	private final ZipWriter _zipWriter;

}
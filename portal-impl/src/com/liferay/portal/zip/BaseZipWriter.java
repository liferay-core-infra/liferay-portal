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

import com.liferay.portal.kernel.zip.ZipWriter;

import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

/**
 * @author Minhchau Dang
 */
public abstract class BaseZipWriter implements ZipWriter {

	protected void addEntry(Path path, byte[] bytes) throws IOException {
		if (bytes == null) {
			return;
		}

		Path parentPath = path.getParent();

		if (parentPath != null) {
			Files.createDirectories(parentPath);
		}

		Files.write(
			path, bytes, StandardOpenOption.CREATE,
			StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
	}

	protected void addEntry(Path path, InputStream inputStream)
		throws IOException {

		if (inputStream == null) {
			return;
		}

		Path parentPath = path.getParent();

		if (parentPath != null) {
			Files.createDirectories(parentPath);
		}

		Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
	}

}
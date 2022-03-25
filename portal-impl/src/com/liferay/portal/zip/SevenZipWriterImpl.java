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
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.kernel.zip.ZipWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;

/**
 * @author Hong Vo
 */
public class SevenZipWriterImpl extends BaseZipWriter implements ZipWriter {

	public SevenZipWriterImpl() {
		_directory = new File(
			StringBundler.concat(
				SystemProperties.get(SystemProperties.TMP_DIR),
				StringPool.SLASH, PortalUUIDUtil.generate()));

		FinalizeManager.register(
			_directory, reference -> FileUtil.deltree(_directory),
			FinalizeManager.PHANTOM_REFERENCE_FACTORY);

		_directoryPath = _directory.toPath();

		_file = new File(_directory.getAbsolutePath() + ".7z");

		FinalizeManager.register(
			_file, new DeleteFileFinalizeAction(_file.getAbsolutePath()),
			FinalizeManager.PHANTOM_REFERENCE_FACTORY);
	}

	@Override
	public void addEntry(String name, byte[] bytes) throws IOException {
		if (bytes == null) {
			return;
		}

		while (name.startsWith(StringPool.SLASH)) {
			name = name.substring(1);
		}

		addEntry(_directoryPath.resolve(name), bytes);
	}

	@Override
	public void addEntry(String name, InputStream inputStream)
		throws IOException {

		if (inputStream == null) {
			return;
		}

		while (name.startsWith(StringPool.SLASH)) {
			name = name.substring(1);
		}

		addEntry(_directoryPath.resolve(name), inputStream);
	}

	@Override
	public File getFile() {
		_generateSevenZipOutput();

		return _file;
	}

	private void _generateSevenZipOutput() {
		try (SevenZOutputFile sevenZOutputFile = new SevenZOutputFile(_file)) {
			Files.walkFileTree(
				_directoryPath,
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult visitFile(
							Path path, BasicFileAttributes basicFileAttributes)
						throws IOException {

						Path entryPath = _directoryPath.relativize(path);

						SevenZArchiveEntry sevenZArchiveEntry =
							sevenZOutputFile.createArchiveEntry(
								path, entryPath.toString());

						sevenZOutputFile.putArchiveEntry(sevenZArchiveEntry);

						sevenZOutputFile.write(path);

						sevenZOutputFile.closeArchiveEntry();

						return FileVisitResult.CONTINUE;
					}

				});
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}
	}

	private final File _directory;
	private final Path _directoryPath;
	private final File _file;

}
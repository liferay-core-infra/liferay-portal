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

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.petra.io.StreamUtil;
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

import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;

/**
 * @author Hong Vo
 */
public class SevenZipWriterImpl implements ZipWriter {

	public SevenZipWriterImpl() {
		this(
			new File(
				StringBundler.concat(
					SystemProperties.get(SystemProperties.TMP_DIR),
					StringPool.SLASH, PortalUUIDUtil.generate())));

		FinalizeManager.register(
			_directory,
			new DeleteFileFinalizeAction(_directory.getAbsolutePath()),
			FinalizeManager.PHANTOM_REFERENCE_FACTORY);
	}

	public SevenZipWriterImpl(File directory) {
		_directory = directory.getAbsoluteFile();

		_path = _directory.toPath();

		_file = null;
	}

	@Override
	public void addEntry(String name, byte[] bytes) throws IOException {
		if (bytes == null) {
			return;
		}

		FileSystem fileSystem = _path.getFileSystem();

		Path filePath = fileSystem.getPath(
			StringBundler.concat(_path.toString(), StringPool.SLASH, name));

		Path parentPath = filePath.getParent();

		if (parentPath != null) {
			Files.createDirectories(parentPath);
		}

		Files.write(
			filePath, bytes, StandardOpenOption.CREATE,
			StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
	}

	@Override
	public void addEntry(String name, InputStream inputStream)
		throws IOException {

		if (inputStream == null) {
			return;
		}

		if (ExportImportThreadLocal.isExportInProcess()) {
			addEntry(name, StreamUtil.toByteArray(inputStream));

			return;
		}

		FileSystem fileSystem = _path.getFileSystem();

		Path filePath = fileSystem.getPath(
			StringBundler.concat(_path.toString(), StringPool.SLASH, name));

		Path parentPath = filePath.getParent();

		if (parentPath != null) {
			Files.createDirectories(parentPath);
		}

		Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
	}

	@Override
	public void addEntry(String name, String s) throws IOException {
		if (s == null) {
			return;
		}

		addEntry(name, s.getBytes(StringPool.UTF8));
	}

	@Override
	public void addEntry(String name, StringBuilder sb) throws IOException {
		if (sb == null) {
			return;
		}

		addEntry(name, sb.toString());
	}

	@Override
	public byte[] finish() throws IOException {
		return FileUtil.getBytes(getFile());
	}

	@Override
	public File getFile() {
		if (_file != null) {
			return _file;
		}

		_file = _generateSevenZipOutput();

		return _file;
	}

	@Override
	public String getPath() {
		return _file.getPath();
	}

	@Override
	public void umount() {
	}

	private void _appendSevenZip(
			File entryFile, Path entryPath, SevenZOutputFile sevenZOutputFile)
		throws IOException {

		SevenZArchiveEntry sevenZArchiveEntry =
			sevenZOutputFile.createArchiveEntry(
				entryFile, entryPath.toString());

		sevenZOutputFile.putArchiveEntry(sevenZArchiveEntry);

		sevenZOutputFile.write(Files.readAllBytes(entryFile.toPath()));

		sevenZOutputFile.closeArchiveEntry();
	}

	private File _generateSevenZipOutput() {
		try {
			_file = new File(_directory.getAbsolutePath() + ".7z");

			FinalizeManager.register(
				_file, new DeleteFileFinalizeAction(_file.getAbsolutePath()),
				FinalizeManager.PHANTOM_REFERENCE_FACTORY);

			_sevenZip(_path, _file);

			FileUtil.deltree(_directory);

			return _file;
		}
		catch (Exception exception) {
			return _file;
		}
	}

	private void _sevenZip(Path sourcePath, File sevenZipFile)
		throws Exception {

		final Path parentPath = sourcePath;

		try (SevenZOutputFile sevenZOutputFile = new SevenZOutputFile(
				sevenZipFile)) {

			Files.walkFileTree(
				sourcePath,
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult visitFile(
							Path path, BasicFileAttributes basicFileAttributes)
						throws IOException {

						Path entryPath = parentPath.relativize(path);

						_appendSevenZip(
							path.toFile(), entryPath, sevenZOutputFile);

						return FileVisitResult.CONTINUE;
					}

				});
		}
	}

	private File _directory;
	private File _file;
	private Path _path;

}
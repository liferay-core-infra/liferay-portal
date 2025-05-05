/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mail.kernel.model;

import com.liferay.portal.kernel.util.FileUtil;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Barrie Selack
 * @author Brian Wing Shun Chan
 */
public class FileAttachment implements Closeable {

	public FileAttachment() {
	}

	public FileAttachment(File file, String fileName) {
		_file = file;
		_fileName = fileName;
	}

	public FileAttachment(File file, String fileName, boolean tempFile) {
		_file = file;
		_fileName = fileName;
		_tempFile = tempFile;
	}

	public FileAttachment(InputStream inputStream, String fileName)
		throws IOException {

		_fileName = fileName;

		_file = FileUtil.createTempFile(inputStream);
		_tempFile = true;
	}

	@Override
	public void close() throws IOException {
		if (_tempFile && _file.exists()) {
			_file.delete();
		}
	}

	public File getFile() {
		return _file;
	}

	public String getFileName() {
		return _fileName;
	}

	public boolean isTempFile() {
		return _tempFile;
	}

	private File _file;
	private String _fileName;
	private boolean _tempFile;

}
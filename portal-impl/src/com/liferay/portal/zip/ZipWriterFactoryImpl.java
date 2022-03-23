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
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;

import java.io.File;

/**
 * @author Raymond Augé
 */
public class ZipWriterFactoryImpl implements ZipWriterFactory {

	@Override
	public ZipWriter getZipWriter() {
		if (ExportImportThreadLocal.isExportInProcess()) {
			return new LarZipWriterImpl();
		}

		return new ZipWriterImpl();
	}

	@Override
	public ZipWriter getZipWriter(File file) {
		if (ExportImportThreadLocal.isExportInProcess()) {
			return new LarZipWriterImpl(file);
		}

		return new ZipWriterImpl(file);
	}

}
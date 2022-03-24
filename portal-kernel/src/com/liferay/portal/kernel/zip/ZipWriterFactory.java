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

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Raymond Augé
 */
@ProviderType
public interface ZipWriterFactory {

	public static final String FORMAT_7ZIP = "7z";

	public static final String FORMAT_ZIP = "zip";

	public ZipWriter getZipWriter();

	public ZipWriter getZipWriter(File file);

	public ZipWriter getZipWriter(String format);

}
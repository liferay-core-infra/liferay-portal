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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
@ProviderType
public interface ZipWriter {

	public void addEntry(String name, byte[] bytes) throws IOException;

	public void addEntry(String name, InputStream inputStream)
		throws IOException;

	public default void addEntry(String name, String s) throws IOException {
		if (s == null) {
			return;
		}

		addEntry(name, s.getBytes(StringPool.UTF8));
	}

	public default void addEntry(String name, StringBuilder sb)
		throws IOException {

		if (sb == null) {
			return;
		}

		addEntry(name, sb.toString());
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #getFile()}
	 */
	@Deprecated
	public default byte[] finish() throws IOException {
		return FileUtil.getBytes(getFile());
	}

	public File getFile();

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #getFile()}
	 */
	@Deprecated
	public default String getPath() {
		File file = getFile();

		return file.getPath();
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #getFile()}
	 */
	@Deprecated
	public default void umount() {
	}

}
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

package com.liferay.asset.list.internal.dynamic.data.mapping.util;

import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.osgi.util.service.Snapshot;

import java.util.Locale;

/**
 * @author Pavel Savinov
 */
public class DDMIndexerUtil {

	public static String encodeName(
		long ddmStructureId, String fieldReference, Locale locale) {

		DDMIndexer ddmIndexer = _ddmIndexerSnapshot.get();

		return ddmIndexer.encodeName(ddmStructureId, fieldReference, locale);
	}

	private static final Snapshot<DDMIndexer> _ddmIndexerSnapshot =
		new Snapshot<>(DDMIndexerUtil.class, DDMIndexer.class);

}
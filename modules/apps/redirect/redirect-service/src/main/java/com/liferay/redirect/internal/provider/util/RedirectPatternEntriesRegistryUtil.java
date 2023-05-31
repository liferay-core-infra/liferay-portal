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

package com.liferay.redirect.internal.provider.util;

import com.liferay.redirect.model.RedirectPatternEntry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Joao Victor Alves
 */
public class RedirectPatternEntriesRegistryUtil {

	public static List<RedirectPatternEntry> getOrDefaultRedirectPatternEntry(
		long groupId, List<RedirectPatternEntry> redirectPatternEntries) {

		return _redirectPatternEntries.getOrDefault(
			groupId, redirectPatternEntries);
	}

	public static List<RedirectPatternEntry> getRedirectPatternEntry(
		long groupId) {

		return _redirectPatternEntries.get(groupId);
	}

	public static void putRedirectPatternEntry(
		long groupId, List<RedirectPatternEntry> redirectPatternEntries) {

		_redirectPatternEntries.put(groupId, redirectPatternEntries);
	}

	public static void removeRedirectPatternEntry(long groupId) {
		_redirectPatternEntries.remove(groupId);
	}

	public static void setRedirectPatternEntry(
		Map<Long, List<RedirectPatternEntry>> redirectPatternEntries) {

		_redirectPatternEntries = redirectPatternEntries;
	}

	private static Map<Long, List<RedirectPatternEntry>>
		_redirectPatternEntries = new ConcurrentHashMap<>();

}
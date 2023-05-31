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

package com.liferay.portal.monitoring.internal.messaging.util;

import com.liferay.portal.kernel.monitoring.Level;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Joao Victor Alves
 */
public class LevelsRegistryUtil {

	public static Set<String> getKeySet() {
		return _levels.keySet();
	}

	public static Level getLevel(String namespace) {
		return _levels.get(namespace);
	}

	public static void setLevels(String namespace, Level level) {
		_levels.put(namespace, level);
	}

	private static final Map<String, Level> _levels = new ConcurrentHashMap<>();

}
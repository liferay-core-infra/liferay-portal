/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Dante Wang
 */
public class SessionMaps {

	public SessionMaps(Supplier<Map<String, Object>> mapSupplier) {
		_mapSupplier = mapSupplier;
	}

	public void add(
		HttpSession httpSession, String mapKey, String key, Object value) {

		_updateMap(httpSession, mapKey, true, map -> map.put(key, value));
	}

	public void clear(HttpSession httpSession, String mapKey) {
		_updateMap(httpSession, mapKey, false, Map::clear);
	}

	public boolean contains(
		HttpSession httpSession, String mapKey, String key) {

		return _readMap(
			httpSession, mapKey, false, map -> map.containsKey(key));
	}

	public Object get(HttpSession httpSession, String mapKey, String key) {
		return _readMap(httpSession, mapKey, null, map -> map.get(key));
	}

	public boolean isEmpty(HttpSession httpSession, String mapKey) {
		return _readMap(httpSession, mapKey, true, Map::isEmpty);
	}

	public Iterator<String> iterator(HttpSession httpSession, String mapKey) {
		Set<String> keySet = keySet(httpSession, mapKey);

		return keySet.iterator();
	}

	public Set<String> keySet(HttpSession httpSession, String mapKey) {
		return _readMap(
			httpSession, mapKey, Collections.emptySet(),
			map -> Collections.unmodifiableSet(new HashSet<>(map.keySet())));
	}

	public void remove(HttpSession httpSession, String mapKey, String key) {
		_updateMap(httpSession, mapKey, false, map -> map.remove(key));
	}

	public int size(HttpSession httpSession, String mapKey) {
		return _readMap(httpSession, mapKey, 0, Map::size);
	}

	private Map<String, Object> _getMap(
		HttpSession httpSession, String mapKey) {

		if (httpSession == null) {
			return null;
		}

		try {
			return (Map<String, Object>)httpSession.getAttribute(mapKey);
		}
		catch (IllegalStateException illegalStateException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalStateException);
			}

			// Session is already invalidated, just return a null map

			return null;
		}
	}

	private <T> T _readMap(
		HttpSession httpSession, String mapKey, T defaultValue,
		Function<Map<String, Object>, T> function) {

		Map<String, Object> map = _getMap(httpSession, mapKey);

		if (map == null) {
			return defaultValue;
		}

		T value;

		synchronized (map) {
			value = function.apply(map);
		}

		return value;
	}

	private void _updateMap(
		HttpSession httpSession, String mapKey, boolean createIfAbsent,
		Consumer<Map<String, Object>> consumer) {

		if (httpSession == null) {
			return;
		}

		Map<String, Object> map = _getMap(httpSession, mapKey);

		if (map == null) {
			if (!createIfAbsent) {
				return;
			}

			synchronized (httpSession) {
				map = _getMap(httpSession, mapKey);

				if (map == null) {
					map = _mapSupplier.get();

					httpSession.setAttribute(mapKey, map);
				}
			}
		}

		synchronized (map) {
			consumer.accept(map);

			httpSession.setAttribute(mapKey, map);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(SessionMaps.class);

	private final Supplier<Map<String, Object>> _mapSupplier;

}
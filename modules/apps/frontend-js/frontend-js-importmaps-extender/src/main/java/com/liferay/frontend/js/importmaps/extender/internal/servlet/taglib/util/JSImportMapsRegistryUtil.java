/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.importmaps.extender.internal.servlet.taglib.util;

import com.liferay.frontend.js.importmaps.extender.internal.servlet.taglib.JSImportMapsRegistration;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Joao Victor Alves
 */
public class JSImportMapsRegistryUtil {

	public static String getImportMaps(JSONFactory jsonFactory) {
		return _importMaps.getSingleton(() -> _rebuildImportMaps(jsonFactory));
	}

	public static boolean isImportMapsJSONObjectsEmpty() {
		if (_globalImportMapJSONObjects.isEmpty() &&
			_scopedImportMapJSONObjects.isEmpty()) {

			return true;
		}

		return false;
	}

	public static JSImportMapsRegistration register(
		JSONObject jsonObject, String scope) {

		if (scope == null) {
			return _createJSImportMapsRegistration(
				_globalImportMapJSONObjects, jsonObject,
				_nextGlobalId.getAndIncrement());
		}

		return _createJSImportMapsRegistration(
			_scopedImportMapJSONObjects, jsonObject, scope);
	}

	private static <T> JSImportMapsRegistration _createJSImportMapsRegistration(
		Map<T, JSONObject> importMapJSONObjects, JSONObject jsonObject, T key) {

		importMapJSONObjects.put(key, jsonObject);

		_importMaps.destroy(null);

		return () -> {
			_importMaps.destroy(null);

			importMapJSONObjects.remove(key);
		};
	}

	private static String _rebuildImportMaps(JSONFactory jsonFactory) {
		JSONObject jsonObject = jsonFactory.createJSONObject();

		jsonObject.put(
			"imports",
			() -> {
				JSONObject importsJSONObject = jsonFactory.createJSONObject();

				for (JSONObject globalImportMapJSONObject :
						_globalImportMapJSONObjects.values()) {

					for (String key : globalImportMapJSONObject.keySet()) {
						importsJSONObject.put(
							key, globalImportMapJSONObject.getString(key));
					}
				}

				return importsJSONObject;
			}
		).put(
			"scopes",
			() -> {
				JSONObject scopesJSONObject = jsonFactory.createJSONObject();

				for (Map.Entry<String, JSONObject> entry :
						_scopedImportMapJSONObjects.entrySet()) {

					scopesJSONObject.put(entry.getKey(), entry.getValue());
				}

				return scopesJSONObject;
			}
		);

		return jsonFactory.looseSerializeDeep(jsonObject);
	}

	private static final ConcurrentMap<Long, JSONObject>
		_globalImportMapJSONObjects = new ConcurrentHashMap<>();
	private static final DCLSingleton<String> _importMaps =
		new DCLSingleton<>();
	private static final AtomicLong _nextGlobalId = new AtomicLong();
	private static final ConcurrentMap<String, JSONObject>
		_scopedImportMapJSONObjects = new ConcurrentHashMap<>();

}
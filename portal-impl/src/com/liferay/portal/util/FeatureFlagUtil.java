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

package com.liferay.portal.util;

import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Map;
import java.util.Properties;

/**
 * @author Dante Wang
 */
public class FeatureFlagUtil {

	public static String getJSON() {
		return _featureFlagJSON;
	}

	public static void set(Properties properties) {
		PropsUtil.addProperties(properties);

		_updateFeatureFlagJSON();
	}

	public static void set(String featureFlag, boolean value) {
		PropsUtil.set(featureFlag, String.valueOf(value));

		_updateFeatureFlagJSON();
	}

	private static void _updateFeatureFlagJSON() {
		Properties properties = PropsUtil.getProperties("feature.flag.", true);

		JSONObject jsonObject = new JSONObjectImpl();

		for (Map.Entry<Object, Object> property : properties.entrySet()) {

			// Keep boolean value for Javascript

			jsonObject.put(
				GetterUtil.getString(property.getKey()),
				GetterUtil.getBoolean(property.getValue()));
		}

		_featureFlagJSON = jsonObject.toString();
	}

	private static volatile String _featureFlagJSON;

	static {
		_updateFeatureFlagJSON();
	}

}
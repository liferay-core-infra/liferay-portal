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

package com.liferay.learn;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.util.PropsValues;

/**
 * @author Brian Wing Shun Chan
 */
public class LearnMessageUtil {

	public static JSONObject getJSONObject(String resource) {
		String key =
			LearnMessageUtil.class.getName() + StringPool.POUND + resource;

		JSONObject jsonObject = _portalCache.get(key);

		if (jsonObject != null) {
			return jsonObject;
		}

		jsonObject = _createJSONObject(resource);

		_portalCache.put(
			key, jsonObject,
			(int)(PropsValues.LEARN_RESOURCES_REFRESH_TIME / Time.SECOND));

		return jsonObject;
	}

	public static LearnMessage getLearnMessage(
		String key, String languageId, String resource) {

		JSONObject jsonObject = getJSONObject(resource);

		return new LearnMessage(jsonObject, key, languageId);
	}

	public static JSONObject getReactDataJSONObject(String resource) {
		JSONObject learnMessageJSONObject = getJSONObject(resource);

		return JSONUtil.put(resource, learnMessageJSONObject);
	}

	public static JSONObject getReactDataJSONObject(String[] resources) {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		for (String resource : resources) {
			JSONObject learnMessageJSONObject = getJSONObject(resource);

			jsonObject.put(resource, learnMessageJSONObject);
		}

		return jsonObject;
	}

	private static JSONObject _createJSONObject(String resource) {
		try {
			if (!PropsValues.LEARN_RESOURCES_ENABLED) {
				return JSONFactoryUtil.createJSONObject();
			}

			StringBundler sb = new StringBundler(5);

			sb.append(Http.HTTPS_WITH_SLASH);

			if (!PropsValues.LEARN_RESOURCES_CDN_ENABLED) {
				sb.append("s3.amazonaws.com/");
			}

			sb.append("learn-resources.liferay.com/");
			sb.append(resource);
			sb.append(".json");

			String url = sb.toString();

			if (_log.isDebugEnabled()) {
				_log.debug("Reading " + url);
			}

			return JSONFactoryUtil.createJSONObject(HttpUtil.URLtoString(url));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return JSONFactoryUtil.createJSONObject();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LearnMessageUtil.class);

	private static final PortalCache<String, JSONObject> _portalCache =
		PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.MULTI_VM, LearnMessageUtil.class.getName());

}
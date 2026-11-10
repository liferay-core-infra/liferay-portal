/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.encryptor.EncryptorException;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.security.Key;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eric Yan
 */
public class PortletURLEncryptionUtil {

	public static Map<String, String[]> decryptParameterMap(
		HttpServletRequest httpServletRequest, Key key) {

		Map<String, String[]> parameterMap = new HashMap<>();

		Map<String, String[]> httpServletRequestParameterMap =
			httpServletRequest.getParameterMap();

		Map<String, String[]> queryParameterMap =
			HttpComponentsUtil.getParameterMap(
				HttpComponentsUtil.getQueryString(httpServletRequest));

		for (Map.Entry<String, String[]> entry :
				httpServletRequestParameterMap.entrySet()) {

			String name = entry.getKey();

			String[] values = entry.getValue();

			for (int i = 0; i < values.length; i++) {
				if (Validator.isNull(values[i]) ||
					!ArrayUtil.contains(
						queryParameterMap.get(name), values[i])) {

					continue;
				}

				try {
					values[i] = EncryptorUtil.decrypt(key, values[i]);
				}
				catch (EncryptorException encryptorException) {
					if (_log.isDebugEnabled()) {
						_log.debug(encryptorException);
					}

					values[i] = StringPool.BLANK;
				}
			}

			parameterMap.put(name, values);
		}

		return parameterMap;
	}

	public static String encryptParameterValue(Key key, String value) {
		try {
			return EncryptorUtil.encrypt(key, value);
		}
		catch (EncryptorException encryptorException) {
			if (_log.isDebugEnabled()) {
				_log.debug(encryptorException);
			}

			return value;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletURLEncryptionUtil.class);

}
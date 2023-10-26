/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.contacts.demo.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import java.nio.charset.StandardCharsets;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Joao Victor Alves
 */
public class HeadersUtil {

	public static Map<String, String> getHeaders() {
		return _headers;
	}

	private static String _encodeAuthorizationFields(
		String userName, String password) {

		String authorizationString = StringBundler.concat(
			userName, StringPool.COLON, password);

		return new String(
			Base64.encodeBase64(
				authorizationString.getBytes(StandardCharsets.UTF_8)),
			StandardCharsets.UTF_8);
	}

	private static final Map<String, String> _headers =
		Collections.singletonMap(
			"Authorization",
			"Basic " + _encodeAuthorizationFields("test@liferay.com", "test"));

}
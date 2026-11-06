/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portlet.internal.PortletURLImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.security.Key;

import java.util.Collections;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class EncryptedServletRequest extends HttpServletRequestWrapper {

	public EncryptedServletRequest(
		HttpServletRequest httpServletRequest, Key key) {

		super(httpServletRequest);

		_params = PortletURLImpl.decryptParameterMap(httpServletRequest, key);
	}

	@Override
	public String getParameter(String name) {
		String[] values = _params.get(name);

		if (ArrayUtil.isNotEmpty(values)) {
			return values[0];
		}

		return null;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return Collections.unmodifiableMap(_params);
	}

	@Override
	public String[] getParameterValues(String name) {
		return _params.get(name);
	}

	private final Map<String, String[]> _params;

}
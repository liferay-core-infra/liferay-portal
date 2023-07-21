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

package com.liferay.oauth2.provider.scope.internal.util;

import com.liferay.petra.string.StringPool;

/**
 * @author Renan Vasconcelos
 */
public class OAuth2ProviderScopeThreadLocalUtil {

	public static void clear() {
		_accessTokenThreadLocal.remove();
		_applicationNameThreadLocal.remove();
		_bundleSymbolicNameThreadLocal.remove();
		_companyIdThreadLocal.remove();
	}

	public static String getAccessToken() {
		return _accessTokenThreadLocal.get();
	}

	public static String getApplicationName() {
		return _applicationNameThreadLocal.get();
	}

	public static String getBundleSymbolicName() {
		return _bundleSymbolicNameThreadLocal.get();
	}

	public static Long getCompanyId() {
		return _companyIdThreadLocal.get();
	}

	public static void setAccessToken(String accessToken) {
		_accessTokenThreadLocal.set(accessToken);
	}

	public static void setApplicationName(String applicationName) {
		_applicationNameThreadLocal.set(applicationName);
	}

	public static void setBundleSymbolicName(String bundleSymbolicName) {
		_bundleSymbolicNameThreadLocal.set(bundleSymbolicName);
	}

	public static void setCompanyId(long companyId) {
		_companyIdThreadLocal.set(companyId);
	}

	private static final ThreadLocal<String> _accessTokenThreadLocal =
		ThreadLocal.withInitial(() -> StringPool.BLANK);
	private static final ThreadLocal<String> _applicationNameThreadLocal =
		ThreadLocal.withInitial(() -> StringPool.BLANK);
	private static final ThreadLocal<String> _bundleSymbolicNameThreadLocal =
		ThreadLocal.withInitial(() -> StringPool.BLANK);
	private static final ThreadLocal<Long> _companyIdThreadLocal =
		ThreadLocal.withInitial(() -> 0L);

}
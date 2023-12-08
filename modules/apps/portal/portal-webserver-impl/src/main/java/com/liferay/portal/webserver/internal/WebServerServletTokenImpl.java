/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.webserver.internal;

import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.webserver.WebServerServletToken;
import com.liferay.portal.servlet.filters.cache.CacheUtil;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 * @since  6.1, replaced com.liferay.portal.servlet.ImageServletTokenImpl
 */
@Component(service = WebServerServletToken.class)
public class WebServerServletTokenImpl implements WebServerServletToken {

	@Override
	public String getToken(long imageId) {
		Long key = imageId;

		String token = _portalCache.get(imageId);

		if (token == null) {
			token = _createToken();

			_portalCache.put(key, token);
		}

		return token;
	}

	@Override
	public void resetToken(long imageId) {
		_portalCache.remove(imageId);

		// Layout cache

		CacheUtil.clearCache();
	}

	@Activate
	protected void activate() {
		_portalCache = PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.MULTI_VM, _CACHE_NAME);
	}

	private String _createToken() {
		return String.valueOf(System.currentTimeMillis());
	}

	private static final String _CACHE_NAME =
		WebServerServletToken.class.getName();

	private PortalCache<Long, String> _portalCache;

}
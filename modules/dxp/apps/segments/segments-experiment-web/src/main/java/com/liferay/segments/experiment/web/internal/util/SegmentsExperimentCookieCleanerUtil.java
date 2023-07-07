/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.segments.experiment.web.internal.util;

import com.liferay.osgi.util.service.Snapshot;
import com.liferay.portal.kernel.cookies.CookiesManager;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Regisson Aguiar
 */
public class SegmentsExperimentCookieCleanerUtil {

	public static void cleanCookieLogoutAction(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		Cookie[] cookies = httpServletRequest.getCookies();

		if (ArrayUtil.isEmpty(cookies)) {
			return;
		}

		for (Cookie cookie : cookies) {
			if (StringUtil.startsWith(
					cookie.getName(), _AB_TEST_VARIANT_ID_COOKIE_PREFIX)) {

				CookiesManager cookiesManager = _cookieManagerSnapshot.get();

				cookiesManager.deleteCookies(
					cookiesManager.getDomain(httpServletRequest),
					httpServletRequest, httpServletResponse, cookie.getName());
			}
		}
	}

	private static final String _AB_TEST_VARIANT_ID_COOKIE_PREFIX =
		"ab_test_variant_id_";

	private static final Snapshot<CookiesManager> _cookieManagerSnapshot =
		new Snapshot<>(
			SegmentsExperimentCookieCleanerUtil.class, CookiesManager.class);

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.experiment.web.internal.events;

import com.liferay.portal.kernel.cookies.CookiesManager;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.segments.experiment.web.internal.constants.ProductNavigationControlMenuEntryConstants;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yurena Cabrera
 */
@Component(property = "key=logout.events.pre", service = LifecycleAction.class)
public class LogoutPreAction extends Action {

	@Override
	public void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		_segmentsExperimentProductNavigationControlMenuEntry.setPanelState(
			httpServletRequest,
			ProductNavigationControlMenuEntryConstants.SESSION_CLICKS_KEY,
			"closed");

		Cookie[] cookies = httpServletRequest.getCookies();

		if (ArrayUtil.isEmpty(cookies)) {
			return;
		}

		for (Cookie cookie : cookies) {
			if (StringUtil.startsWith(
					cookie.getName(), _AB_TEST_VARIANT_ID_COOKIE_PREFIX)) {

				_cookiesManager.deleteCookies(
					_cookiesManager.getDomain(httpServletRequest),
					httpServletRequest, httpServletResponse, cookie.getName());
			}
		}
	}

	private static final String _AB_TEST_VARIANT_ID_COOKIE_PREFIX =
		"ab_test_variant_id_";

	@Reference
	private CookiesManager _cookiesManager;

	@Reference(
		target = "(component.name=com.liferay.segments.experiment.web.internal.product.navigation.control.menu.SegmentsExperimentProductNavigationControlMenuEntry)"
	)
	private ProductNavigationControlMenuEntry
		_segmentsExperimentProductNavigationControlMenuEntry;

}
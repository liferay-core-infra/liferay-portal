/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.taglib.internal.servlet.filter;

import com.liferay.captcha.util.CaptchaUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.BaseFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jiefeng Wu
 */
@Component(
	property = {
		"after-filter=Virtual Host Filter", "dispatcher=REQUEST",
		"servlet-context-name=",
		"servlet-filter-name=Captcha Image Portal Filter",
		"url-pattern=/c/portal/captcha/get_image"
	},
	service = Filter.class
)
public class CaptchaImagePortalFilter extends BaseFilter {

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		CaptchaUtil.serveImage(httpServletRequest, httpServletResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CaptchaImagePortalFilter.class);

}
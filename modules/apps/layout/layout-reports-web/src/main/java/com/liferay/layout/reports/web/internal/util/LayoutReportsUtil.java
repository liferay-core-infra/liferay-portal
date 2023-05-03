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

package com.liferay.layout.reports.web.internal.util;

import com.liferay.portal.kernel.util.SessionClicks;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Renan Vasconcelos
 */
public class LayoutReportsUtil {

	public static boolean isPanelStateOpen(
		HttpServletRequest httpServletRequest) {

		String layoutReportsPanelState = SessionClicks.get(
			httpServletRequest, _SESSION_CLICKS_KEY, "closed");

		if (Objects.equals(layoutReportsPanelState, "open")) {
			return true;
		}

		return false;
	}

	public static void setPanelState(
		HttpServletRequest httpServletRequest, String panelState) {

		SessionClicks.put(httpServletRequest, _SESSION_CLICKS_KEY, panelState);
	}

	private static final String _SESSION_CLICKS_KEY =
		"com.liferay.layout.reports.web_layoutReportsPanelState";

}
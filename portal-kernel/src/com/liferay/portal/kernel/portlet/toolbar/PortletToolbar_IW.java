/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.toolbar;

import com.liferay.portal.kernel.servlet.taglib.ui.Menu;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import java.util.List;

/**
 * @author Jiaxu Wei
 */
public class PortletToolbar_IW {

	public static PortletToolbar_IW getInstance() {
		return _instance;
	}

	public List<Menu> getPortletTitleMenus(
		String portletId, PortletRequest portletRequest,
		PortletResponse portletResponse) {

		return PortletToolbar.getPortletTitleMenus(
			portletId, portletRequest, portletResponse);
	}

	private PortletToolbar_IW() {
	}

	public static PortletToolbar_IW _instance = new PortletToolbar_IW();
}

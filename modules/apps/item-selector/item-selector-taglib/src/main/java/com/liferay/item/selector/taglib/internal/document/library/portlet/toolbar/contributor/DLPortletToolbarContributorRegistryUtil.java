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

package com.liferay.item.selector.taglib.internal.document.library.portlet.toolbar.contributor;

import com.liferay.document.library.portlet.toolbar.contributor.DLPortletToolbarContributor;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.servlet.taglib.ui.Menu;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Alejandro Tardín
 */
public class DLPortletToolbarContributorRegistryUtil {

	public static DLPortletToolbarContributor getDLPortletToolbarContributor() {
		return _dlPortletToolbarContributor;
	}

	private static final DLPortletToolbarContributor
		_dlPortletToolbarContributor;
	private static final ServiceTrackerList<DLPortletToolbarContributor>
		_serviceTrackerList;

	private static class AggregateDLPortletToolbarContributor
		implements DLPortletToolbarContributor {

		@Override
		public List<Menu> getPortletTitleMenus(
			PortletRequest portletRequest, PortletResponse portletResponse) {

			List<Menu> menus = new ArrayList<>();

			_serviceTrackerList.forEach(
				dlPortletToolbarContributor -> menus.addAll(
					dlPortletToolbarContributor.getPortletTitleMenus(
						portletRequest, portletResponse)));

			return menus;
		}

	}

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			DLPortletToolbarContributorRegistryUtil.class);
		_dlPortletToolbarContributor =
			new AggregateDLPortletToolbarContributor();
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundle.getBundleContext(), DLPortletToolbarContributor.class);
	}

}
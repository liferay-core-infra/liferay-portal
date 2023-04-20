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

package com.liferay.site.navigation.taglib.internal.servlet;

import com.liferay.osgi.util.service.Snapshot;
import com.liferay.portal.kernel.theme.NavItem;
import com.liferay.portal.kernel.util.Portal;

/**
 * @author Shuyang Zhou
 */
public class NavItemClassNameIdUtil {

	public static long getNavItemClassNameId() {
		return _NAV_ITEM_CLASS_NAME_ID;
	}

	private static final long _NAV_ITEM_CLASS_NAME_ID =
		NavItemClassNameIdUtil._portal.getClassNameId(NavItem.class);

	private static final Portal _portal =
		NavItemClassNameIdUtil._portalSnapshot.get();
	private static final Snapshot<Portal> _portalSnapshot = new Snapshot<>(
		NavItemClassNameIdUtil.class, Portal.class);

}
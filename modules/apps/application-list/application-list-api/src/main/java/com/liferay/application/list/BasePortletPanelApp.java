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

package com.liferay.application.list;

import com.liferay.portal.kernel.model.Portlet;

/**
 * @author Jiaxu Wei
 */
public abstract class BasePortletPanelApp extends BasePanelApp {

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	/**
	 * Sets the portlet associated with the application.
	 *
	 * @param portlet the portlet associated with the application
	 */
	public void setPortlet(Portlet portlet) {
		_portlet = portlet;
	}

	private Portlet _portlet;

}
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

package com.liferay.depot.web.internal.application.list;

import com.liferay.application.list.PanelAppRegistry;
import com.liferay.depot.application.controller.DepotApplicationController;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = DepotPanelAppController.class)
public class DepotPanelAppController {

	public boolean isShow(String portletId) {
		if (_panelAppRegistry.isAlwaysShow(portletId)) {
			return true;
		}

		return _depotApplicationController.isEnabled(portletId);
	}

	@Reference
	private DepotApplicationController _depotApplicationController;

	@Reference
	private PanelAppRegistry _panelAppRegistry;

}
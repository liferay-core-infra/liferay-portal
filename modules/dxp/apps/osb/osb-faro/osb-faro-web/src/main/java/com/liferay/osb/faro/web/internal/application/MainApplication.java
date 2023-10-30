/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.application;

import com.liferay.osb.faro.web.internal.constants.FaroConstants;

import java.util.Collections;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.osgi.service.component.annotations.Component;

/**
 * @author Matthew Kong
 */
@Component(
	property = {
		"jaxrs.application=true",
		"osgi.http.whiteboard.filter.dispatcher=FORWARD",
		"osgi.http.whiteboard.filter.dispatcher=REQUEST",
		"osgi.jaxrs.application.base=/" + FaroConstants.APPLICATION_MAIN,
		"osgi.jaxrs.name=Liferay.Osb.Faro.Web"
	},
	service = Application.class
)
public class MainApplication extends BaseApplication {

	@Override
	public Set<Object> getControllers() {
		return Collections.emptySet();
	}

}
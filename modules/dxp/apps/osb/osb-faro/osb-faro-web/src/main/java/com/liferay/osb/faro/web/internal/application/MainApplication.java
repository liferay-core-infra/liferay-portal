/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.application;

import com.liferay.osb.faro.web.internal.constants.FaroConstants;
import com.liferay.osb.faro.web.internal.controller.FaroController;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@ApplicationPath("/" + FaroConstants.APPLICATION_MAIN)
@Component(
	property = {
		"jaxrs.application=true",
		"osgi.http.whiteboard.filter.dispatcher=FORWARD",
		"osgi.http.whiteboard.filter.dispatcher=REQUEST"
	},
	service = Application.class
)
public class MainApplication extends BaseApplication {

	@Override
	public Set<Object> getControllers() {
		Set<Object> controllers = new HashSet<>();

		controllers.add(_blockedKeywordsFaroController);
		controllers.add(_channelFaroController);
		controllers.add(_definitionsFaroController);
		controllers.add(_issueFaroController);
		controllers.add(_mainFaroController);
		controllers.add(_notificationFaroController);
		controllers.add(_oAuth2FaroController);
		controllers.add(_preferencesFaroController);
		controllers.add(_projectFaroController);
		controllers.add(_reportFaroController);
		controllers.add(_userFaroController);

		return controllers;
	}

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.BlockedKeywordsFaroController)"
	)
	private FaroController _blockedKeywordsFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.ChannelFaroController)"
	)
	private FaroController _channelFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.DefinitionsFaroController)"
	)
	private FaroController _definitionsFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.IssueFaroController)"
	)
	private FaroController _issueFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.MainFaroController)"
	)
	private FaroController _mainFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.NotificationFaroController)"
	)
	private FaroController _notificationFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.OAuth2FaroController)"
	)
	private FaroController _oAuth2FaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.PreferencesFaroController)"
	)
	private FaroController _preferencesFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.ProjectFaroController)"
	)
	private FaroController _projectFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.ReportFaroController)"
	)
	private FaroController _reportFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.UserFaroController)"
	)
	private FaroController _userFaroController;

}
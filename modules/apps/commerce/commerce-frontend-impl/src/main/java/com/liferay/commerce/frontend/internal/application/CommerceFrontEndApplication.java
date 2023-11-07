/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.internal.application;

import com.liferay.commerce.frontend.internal.application.context.provider.ThemeDisplayContextProvider;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

/**
 * @author Marco Leo
 */
@Component(
	property = {
		JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE + "=/commerce-ui",
		JaxrsWhiteboardConstants.JAX_RS_NAME + "=CommerceUi.Application",
		"auth.verifier.auth.verifier.PortalSessionAuthVerifier.urls.includes=/*",
		"auth.verifier.guest.allowed=true", "liferay.oauth2=false"
	},
	service = Application.class
)
public class CommerceFrontEndApplication extends Application {

	@Override
	public Set<Object> getSingletons() {
		Set<Object> singletons = new HashSet<>();

		singletons.add(new ThemeDisplayContextProvider());

		return singletons;
	}

}
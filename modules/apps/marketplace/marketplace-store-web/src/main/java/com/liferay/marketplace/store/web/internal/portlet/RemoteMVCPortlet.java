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

package com.liferay.marketplace.store.web.internal.portlet;

import com.liferay.marketplace.store.web.internal.oauth.util.OAuthManager;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

/**
 * @author Ryan Park
 * @author Joan Kim
 * @author Douglas Wong
 * @author Haote Chou
 */
public class RemoteMVCPortlet extends MVCPortlet {

	protected Response getResponse(User user, OAuthRequest oAuthRequest)
		throws Exception {

		Token token = oAuthManager.getAccessToken(user);

		if (token != null) {
			OAuthService oAuthService = oAuthManager.getOAuthService();

			oAuthService.signRequest(token, oAuthRequest);
		}

		oAuthRequest.setFollowRedirects(false);

		return oAuthRequest.send();
	}

	protected void setOAuthManager(OAuthManager oAuthManager) {
		this.oAuthManager = oAuthManager;
	}

	protected OAuthManager oAuthManager;

}
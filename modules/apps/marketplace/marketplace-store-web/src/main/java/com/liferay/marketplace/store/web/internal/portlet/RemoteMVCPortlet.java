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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import javax.servlet.http.HttpServletRequest;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * @author Ryan Park
 * @author Joan Kim
 * @author Douglas Wong
 * @author Haote Chou
 */
public class RemoteMVCPortlet extends MVCPortlet {

	protected void addOAuthParameter(
		OAuthRequest oAuthRequest, String key, String value) {

		if (oAuthRequest.getVerb() == Verb.GET) {
			oAuthRequest.addQuerystringParameter(key, value);
		}
		else if (oAuthRequest.getVerb() == Verb.POST) {
			oAuthRequest.addBodyParameter(key, value);
		}
	}

	protected String getClientPortletId() {
		return StringPool.BLANK;
	}

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

	protected String getServerNamespace() {
		return PortalUtil.getPortletNamespace(getServerPortletId());
	}

	protected String getServerPortletId() {
		return StringPool.BLANK;
	}

	protected void setBaseRequestParameters(
		PortletRequest portletRequest, PortletResponse portletResponse,
		OAuthRequest oAuthRequest) {

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(portletRequest);

		String clientAuthToken = AuthTokenUtil.getToken(httpServletRequest);

		addOAuthParameter(oAuthRequest, "clientAuthToken", clientAuthToken);

		addOAuthParameter(
			oAuthRequest, "clientPortletId", getClientPortletId());
		addOAuthParameter(
			oAuthRequest, "clientURL",
			PortalUtil.getCurrentCompleteURL(httpServletRequest));
		addOAuthParameter(oAuthRequest, "p_p_id", getServerPortletId());
	}

	protected void setOAuthManager(OAuthManager oAuthManager) {
		this.oAuthManager = oAuthManager;
	}

	protected OAuthManager oAuthManager;

}
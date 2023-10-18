/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.portlet.tab;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
public abstract class BaseWorkflowPortletTab
	extends BaseJSPDynamicInclude implements WorkflowPortletTab {

	@Override
	public PortletURL getSearchURL(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		return PortletURLBuilder.createRenderURL(
			renderResponse
		).setMVCPath(
			"/view.jsp"
		).setParameter(
			"groupId",
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)renderRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return themeDisplay.getScopeGroupId();
			}
		).setParameter(
			"tab", getName()
		).buildPortletURL();
	}

	@Override
	public void prepareDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {
	}

	@Override
	public void prepareProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {
	}

	@Override
	public void prepareRender(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {
	}

	@Override
	protected Log getLog() {
		Class<? extends BaseWorkflowPortletTab> clazz = getClass();

		if (!_logs.containsKey(clazz)) {
			_logs.put(clazz, LogFactoryUtil.getLog(clazz));
		}

		return _logs.get(clazz);
	}

	protected String getMVCPathAttributeName(String namespace) {
		return StringBundler.concat(
			namespace, StringPool.PERIOD,
			MVCRenderConstants.MVC_PATH_REQUEST_ATTRIBUTE_NAME);
	}

	protected String getPath(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		String mvcPath = portletRequest.getParameter("mvcPath");

		if (mvcPath == null) {
			mvcPath = (String)portletRequest.getAttribute(
				getMVCPathAttributeName(portletResponse.getNamespace()));
		}

		// Check deprecated parameter

		if (mvcPath == null) {
			mvcPath = portletRequest.getParameter("jspPage");
		}

		return mvcPath;
	}

	protected void hideDefaultErrorMessage(PortletRequest portletRequest) {
		SessionMessages.add(
			portletRequest,
			portal.getPortletId(portletRequest) +
				SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
	}

	protected void hideDefaultSuccessMessage(PortletRequest portletRequest) {
		SessionMessages.add(
			portletRequest,
			portal.getPortletId(portletRequest) +
				SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE);
	}

	protected boolean isSessionErrorException(Throwable throwable) {
		if (_log.isDebugEnabled()) {
			_log.debug(throwable, throwable);
		}

		if (throwable instanceof PortalException) {
			return true;
		}

		return false;
	}

	@Reference
	protected Portal portal;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseWorkflowPortletTab.class);

	private static final Map<Class<? extends BaseWorkflowPortletTab>, Log>
		_logs = new HashMap<>();

}
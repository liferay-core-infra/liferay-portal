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

package com.liferay.layout.content.page.editor.web.internal.util;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.list.asset.entry.provider.AssetListAssetEntryProvider;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.util.AssetHelper;
import com.liferay.asset.util.AssetPublisherAddItemHolder;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.osgi.util.service.Snapshot;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletPreferencesIds;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayRenderRequest;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletInstanceFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.RenderRequestFactory;
import com.liferay.portlet.RenderResponseFactory;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuPortletKeys;
import com.liferay.segments.SegmentsEntryRetriever;
import com.liferay.segments.context.RequestContextMapper;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.WindowState;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Jürgen Kappler
 */
public class AssetHelperUtil {

	public static List<AssetPublisherAddItemHolder>
			getAssetPublisherAddItemHolders(
				AssetListEntry assetListEntry,
				HttpServletRequest httpServletRequest,
				HttpServletResponse httpServletResponse)
		throws Exception {

		SegmentsEntryRetriever segmentsEntryRetriever =
			_segmentsEntryRetrieverSnapshot.get();

		AssetListAssetEntryProvider assetListAssetEntryProvider =
			_assetListAssetEntryProviderSnapshot.get();

		Portal portal = _portalSnapshot.get();

		RequestContextMapper requestContextMapper =
			_requestContextMapperSnapshot.get();

		AssetEntryQuery assetEntryQuery =
			assetListAssetEntryProvider.getAssetEntryQuery(
				assetListEntry,
				segmentsEntryRetriever.getSegmentsEntryIds(
					portal.getScopeGroupId(httpServletRequest),
					portal.getUserId(httpServletRequest),
					requestContextMapper.map(httpServletRequest)),
				StringPool.BLANK);

		long[] allTagIds = assetEntryQuery.getAllTagIds();

		String[] allTagNames = new String[allTagIds.length];

		int index = 0;

		for (long tagId : allTagIds) {
			AssetTagLocalService assetTagLocalService =
				_assetTagLocalServiceSnapshot.get();

			AssetTag assetTag = assetTagLocalService.getAssetTag(tagId);

			allTagNames[index++] = assetTag.getName();
		}

		LiferayPortletRequest liferayPortletRequest = _createRenderRequest(
			httpServletRequest, httpServletResponse);

		PortletResponse portletResponse =
			(PortletResponse)liferayPortletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		LiferayPortletResponse liferayPortletResponse =
			portal.getLiferayPortletResponse(portletResponse);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		AssetHelper assetHelper = _assetHelperSnapshot.get();

		return assetHelper.getAssetPublisherAddItemHolders(
			liferayPortletRequest, liferayPortletResponse,
			assetListEntry.getGroupId(), assetEntryQuery.getClassNameIds(),
			assetEntryQuery.getClassTypeIds(),
			assetEntryQuery.getAllCategoryIds(), allTagNames,
			_getRedirect(
				assetListEntry.getAssetListEntryId(), httpServletRequest,
				themeDisplay));
	}

	private static LiferayRenderRequest _createRenderRequest(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		PortletLocalService portletLocalService =
			_portletLocalServiceSnapshot.get();

		Portlet portlet = portletLocalService.getPortletById(
			ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET);

		ServletContext servletContext =
			(ServletContext)httpServletRequest.getAttribute(WebKeys.CTX);

		PortletPreferencesIds portletPreferencesIds =
			PortletPreferencesFactoryUtil.getPortletPreferencesIds(
				httpServletRequest, portlet.getPortletId());

		PortletConfig portletConfig = PortletConfigFactoryUtil.create(
			portlet, servletContext);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletPreferencesLocalService portletPreferencesLocalService =
			_portletPreferencesLocalServiceSnapshot.get();

		LiferayRenderRequest liferayRenderRequest = RenderRequestFactory.create(
			httpServletRequest, portlet,
			PortletInstanceFactoryUtil.create(portlet, servletContext),
			portletConfig.getPortletContext(), WindowState.NORMAL,
			PortletMode.VIEW,
			portletPreferencesLocalService.getStrictPreferences(
				portletPreferencesIds),
			themeDisplay.getPlid());

		liferayRenderRequest.setPortletRequestDispatcherRequest(
			httpServletRequest);

		liferayRenderRequest.defineObjects(
			portletConfig,
			RenderResponseFactory.create(
				httpServletResponse, liferayRenderRequest));

		return liferayRenderRequest;
	}

	private static String _getRedirect(
			long assetListEntryId, HttpServletRequest httpServletRequest,
			ThemeDisplay themeDisplay)
		throws Exception {

		Portal portal = _portalSnapshot.get();

		String currentURL = HttpComponentsUtil.addParameter(
			portal.getLayoutRelativeURL(themeDisplay.getLayout(), themeDisplay),
			"p_l_mode", Constants.EDIT);

		return HttpComponentsUtil.addParameter(
			PortletURLBuilder.create(
				PortalUtil.getControlPanelPortletURL(
					httpServletRequest,
					ProductNavigationControlMenuPortletKeys.
						PRODUCT_NAVIGATION_CONTROL_MENU,
					PortletRequest.ACTION_PHASE)
			).setActionName(
				"/control_menu/add_collection_item"
			).setRedirect(
				currentURL
			).setParameter(
				"assetListEntryId", assetListEntryId
			).buildString(),
			"portletResource",
			ProductNavigationControlMenuPortletKeys.
				PRODUCT_NAVIGATION_CONTROL_MENU);
	}

	private static final Snapshot<AssetHelper> _assetHelperSnapshot =
		new Snapshot<>(AssetHelperUtil.class, AssetHelper.class);
	private static final Snapshot<AssetListAssetEntryProvider>
		_assetListAssetEntryProviderSnapshot = new Snapshot<>(
			AssetHelperUtil.class, AssetListAssetEntryProvider.class);
	private static final Snapshot<AssetTagLocalService>
		_assetTagLocalServiceSnapshot = new Snapshot<>(
			AssetHelperUtil.class, AssetTagLocalService.class);
	private static final Snapshot<Portal> _portalSnapshot = new Snapshot<>(
		AssetHelperUtil.class, Portal.class);
	private static final Snapshot<PortletLocalService>
		_portletLocalServiceSnapshot = new Snapshot<>(
			AssetHelperUtil.class, PortletLocalService.class);
	private static final Snapshot<PortletPreferencesLocalService>
		_portletPreferencesLocalServiceSnapshot = new Snapshot<>(
			AssetHelperUtil.class, PortletPreferencesLocalService.class);
	private static final Snapshot<RequestContextMapper>
		_requestContextMapperSnapshot = new Snapshot<>(
			AssetHelperUtil.class, RequestContextMapper.class);
	private static final Snapshot<SegmentsEntryRetriever>
		_segmentsEntryRetrieverSnapshot = new Snapshot<>(
			AssetHelperUtil.class, SegmentsEntryRetriever.class);

}
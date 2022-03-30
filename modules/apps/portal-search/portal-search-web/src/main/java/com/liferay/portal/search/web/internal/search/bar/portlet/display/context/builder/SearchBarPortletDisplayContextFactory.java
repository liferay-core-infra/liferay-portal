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

package com.liferay.portal.search.web.internal.search.bar.portlet.display.context.builder;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.web.internal.display.context.SearchScope;
import com.liferay.portal.search.web.internal.display.context.SearchScopePreference;
import com.liferay.portal.search.web.internal.portlet.preferences.PortletPreferencesLookup;
import com.liferay.portal.search.web.internal.search.bar.portlet.SearchBarPortletDestinationUtil;
import com.liferay.portal.search.web.internal.search.bar.portlet.SearchBarPortletPreferences;
import com.liferay.portal.search.web.internal.search.bar.portlet.SearchBarPortletPreferencesImpl;
import com.liferay.portal.search.web.internal.search.bar.portlet.configuration.SearchBarPortletInstanceConfiguration;
import com.liferay.portal.search.web.internal.search.bar.portlet.display.context.SearchBarPortletDisplayContext;
import com.liferay.portal.search.web.internal.search.bar.portlet.helper.SearchBarPrecedenceHelper;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;
import com.liferay.portal.search.web.search.request.SearchSettings;

import java.util.Optional;

import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author André de Oliveira
 */
public class SearchBarPortletDisplayContextFactory {

	public SearchBarPortletDisplayContextFactory(
		Http http, LayoutLocalService layoutLocalService, Portal portal,
		PortletPreferencesLookup portletPreferencesLookup,
		PortletSharedSearchRequest portletSharedSearchRequest,
		RenderRequest renderRequest,
		SearchBarPrecedenceHelper searchBarPrecedenceHelper) {

		_http = http;
		_layoutLocalService = layoutLocalService;
		_portal = portal;
		_portletPreferencesLookup = portletPreferencesLookup;
		_portletSharedSearchRequest = portletSharedSearchRequest;
		_renderRequest = renderRequest;
		_searchBarPrecedenceHelper = searchBarPrecedenceHelper;

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public SearchBarPortletDisplayContext create() {
		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			new SearchBarPortletDisplayContext();

		SearchBarPortletPreferences searchBarPortletPreferences =
			new SearchBarPortletPreferencesImpl(
				Optional.ofNullable(_renderRequest.getPreferences()));

		String destination = searchBarPortletPreferences.getDestinationString();

		if (!Validator.isBlank(destination) &&
			(_getDestinationURL(destination) == null)) {

			searchBarPortletDisplayContext.setDestinationUnreachable(true);
			searchBarPortletDisplayContext.setRenderNothing(true);

			return searchBarPortletDisplayContext;
		}

		if (Validator.isBlank(destination)) {
			searchBarPortletDisplayContext.setSearchURL(_getURLCurrentPath());
		}
		else {
			searchBarPortletDisplayContext.setSearchURL(
				_getDestinationURL(destination));
		}

		if (searchBarPortletPreferences.isInvisible()) {
			searchBarPortletDisplayContext.setRenderNothing(true);
		}

		_searchScopePreference =
			searchBarPortletPreferences.getSearchScopePreference();

		if (_searchScopePreference ==
				SearchScopePreference.LET_THE_USER_CHOOSE) {

			searchBarPortletDisplayContext.setLetTheUserChooseTheSearchScope(
				true);
		}

		PortletSharedSearchResponse portletSharedSearchResponse =
			_portletSharedSearchRequest.search(_renderRequest);

		searchBarPortletDisplayContext.setEmptySearchEnabled(
			_isEmptySearchEnabled(portletSharedSearchResponse));

		searchBarPortletDisplayContext.setKeywordsParameterName(
			_getKeywordsParameterName(
				searchBarPortletPreferences,
				portletSharedSearchResponse.getSearchSettings()));

		_scopeParameterName = _getScopeParameterName(
			searchBarPortletPreferences,
			portletSharedSearchResponse.getSearchSettings());

		searchBarPortletDisplayContext.setScopeParameterName(
			_scopeParameterName);

		SearchResponse searchResponse = _getSearchResponse(
			portletSharedSearchResponse, searchBarPortletPreferences);

		SearchRequest searchRequest = searchResponse.getRequest();

		Optional.ofNullable(
			searchRequest.getQueryString()
		).ifPresent(
			keywords -> _keywords = keywords
		);

		searchBarPortletDisplayContext.setKeywords(
			_getNoneNullValue(_keywords));

		searchBarPortletDisplayContext.setPaginationStartParameterName(
			_getNoneNullValue(searchRequest.getPaginationStartParameterName()));

		Optional<String> scopeParameterValueOptional =
			portletSharedSearchResponse.getParameter(
				_scopeParameterName, _renderRequest);

		scopeParameterValueOptional.ifPresent(
			scopeParameterValue -> _scopeParameterValue = scopeParameterValue);

		searchBarPortletDisplayContext.setScopeParameterValue(
			_getNoneNullValue(_scopeParameterValue));

		HttpServletRequest httpServletRequest = getHttpServletRequest(
			_renderRequest);

		searchBarPortletDisplayContext.setInputPlaceholder(
			LanguageUtil.get(httpServletRequest, "search-..."));

		SearchBarPortletInstanceConfiguration
			searchBarPortletInstanceConfiguration =
				getSearchBarPortletInstanceConfiguration(
					_themeDisplay.getPortletDisplay());

		searchBarPortletDisplayContext.setDisplayStyleGroupId(
			getDisplayStyleGroupId(
				searchBarPortletInstanceConfiguration, _themeDisplay));

		searchBarPortletDisplayContext.setSearchBarPortletInstanceConfiguration(
			searchBarPortletInstanceConfiguration);

		searchBarPortletDisplayContext.setAvailableEverythingSearchScope(
			isAvailableEverythingSearchScope());
		searchBarPortletDisplayContext.setCurrentSiteSearchScopeParameterString(
			SearchScope.THIS_SITE.getParameterString());
		searchBarPortletDisplayContext.setEverythingSearchScopeParameterString(
			SearchScope.EVERYTHING.getParameterString());

		_setSelectedSearchScope(searchBarPortletDisplayContext);

		return searchBarPortletDisplayContext;
	}

	protected Layout fetchLayoutByFriendlyURL(
		long groupId, String friendlyURL) {

		Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
			groupId, false, friendlyURL);

		if (layout != null) {
			return layout;
		}

		return _layoutLocalService.fetchLayoutByFriendlyURL(
			groupId, true, friendlyURL);
	}

	protected long getDisplayStyleGroupId(
		SearchBarPortletInstanceConfiguration
			searchBarPortletInstanceConfiguration,
		ThemeDisplay themeDisplay) {

		long displayStyleGroupId =
			searchBarPortletInstanceConfiguration.displayStyleGroupId();

		if (displayStyleGroupId <= 0) {
			displayStyleGroupId = themeDisplay.getScopeGroupId();
		}

		return displayStyleGroupId;
	}

	protected HttpServletRequest getHttpServletRequest(
		RenderRequest renderRequest) {

		LiferayPortletRequest liferayPortletRequest =
			_portal.getLiferayPortletRequest(renderRequest);

		return liferayPortletRequest.getHttpServletRequest();
	}

	protected String getLayoutFriendlyURL(Layout layout) {
		try {
			return _portal.getLayoutFriendlyURL(layout, _themeDisplay);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get friendly URL for layout " +
						layout.getLinkedToLayout(),
					portalException);
			}

			return null;
		}
	}

	protected SearchBarPortletInstanceConfiguration
		getSearchBarPortletInstanceConfiguration(
			PortletDisplay portletDisplay) {

		try {
			return portletDisplay.getPortletInstanceConfiguration(
				SearchBarPortletInstanceConfiguration.class);
		}
		catch (ConfigurationException configurationException) {
			throw new RuntimeException(configurationException);
		}
	}

	protected SearchScope getSearchScope() {
		if (_scopeParameterValue != null) {
			return SearchScope.getSearchScope(_scopeParameterValue);
		}

		SearchScope searchScope = _searchScopePreference.getSearchScope();

		if (searchScope != null) {
			return searchScope;
		}

		return SearchScope.THIS_SITE;
	}

	protected boolean isAvailableEverythingSearchScope() {
		return true;
	}

	private String _getDestinationURL(String friendlyURL) {
		Layout layout = fetchLayoutByFriendlyURL(
			_themeDisplay.getScopeGroupId(), _slashify(friendlyURL));

		if (layout == null) {
			return null;
		}

		return getLayoutFriendlyURL(layout);
	}

	private String _getKeywordsParameterName(
		SearchBarPortletPreferences searchBarPortletPreferences,
		SearchSettings searchSettings) {

		Optional<Portlet> headerSearchBarOptional =
			_searchBarPrecedenceHelper.findHeaderSearchBarPortletOptional(
				_themeDisplay);

		if (headerSearchBarOptional.isPresent()) {
			Optional<PortletPreferences> headerPortletPreferencesOptional =
				_portletPreferencesLookup.fetchPreferences(
					headerSearchBarOptional.get(), _themeDisplay);

			if (headerPortletPreferencesOptional.isPresent() &&
				SearchBarPortletDestinationUtil.isSameDestination(
					headerPortletPreferencesOptional.get(), _themeDisplay)) {

				Optional<String> optional =
					searchSettings.getKeywordsParameterName();

				return optional.orElse(
					searchBarPortletPreferences.getKeywordsParameterName());
			}
		}

		return searchBarPortletPreferences.getKeywordsParameterName();
	}

	private String _getNoneNullValue(String originValue) {
		if (originValue != null) {
			return originValue;
		}

		return StringPool.BLANK;
	}

	private String _getScopeParameterName(
		SearchBarPortletPreferences searchBarPortletPreferences,
		SearchSettings searchSettings) {

		Optional<Portlet> headerSearchBarOptional =
			_searchBarPrecedenceHelper.findHeaderSearchBarPortletOptional(
				_themeDisplay);

		if (headerSearchBarOptional.isPresent()) {
			Optional<PortletPreferences> headerPortletPreferencesOptional =
				_portletPreferencesLookup.fetchPreferences(
					headerSearchBarOptional.get(), _themeDisplay);

			if (headerPortletPreferencesOptional.isPresent() &&
				SearchBarPortletDestinationUtil.isSameDestination(
					headerPortletPreferencesOptional.get(), _themeDisplay)) {

				Optional<String> optional =
					searchSettings.getScopeParameterName();

				return optional.orElse(
					searchBarPortletPreferences.getScopeParameterName());
			}
		}

		return searchBarPortletPreferences.getScopeParameterName();
	}

	private SearchResponse _getSearchResponse(
		PortletSharedSearchResponse portletSharedSearchResponse,
		SearchBarPortletPreferences searchBarPortletPreferences) {

		return portletSharedSearchResponse.getFederatedSearchResponse(
			searchBarPortletPreferences.getFederatedSearchKeyOptional());
	}

	private String _getURLCurrentPath() {
		return _http.getPath(_themeDisplay.getURLCurrent());
	}

	private boolean _isEmptySearchEnabled(
		PortletSharedSearchResponse portletSharedSearchResponse) {

		SearchResponse searchResponse =
			portletSharedSearchResponse.getSearchResponse();

		SearchRequest searchRequest = searchResponse.getRequest();

		return searchRequest.isEmptySearchEnabled();
	}

	private void _setSelectedSearchScope(
		SearchBarPortletDisplayContext searchBarPortletDisplayContext) {

		SearchScope searchScope = getSearchScope();

		if (searchScope == SearchScope.EVERYTHING) {
			searchBarPortletDisplayContext.setSelectedEverythingSearchScope(
				true);
		}

		if (searchScope == SearchScope.THIS_SITE) {
			searchBarPortletDisplayContext.setSelectedCurrentSiteSearchScope(
				true);
		}
	}

	private String _slashify(String s) {
		if (s.charAt(0) == CharPool.SLASH) {
			return s;
		}

		return StringPool.SLASH.concat(s);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SearchBarPortletDisplayContextFactory.class);

	private final Http _http;
	private String _keywords;
	private final LayoutLocalService _layoutLocalService;
	private final Portal _portal;
	private final PortletPreferencesLookup _portletPreferencesLookup;
	private final PortletSharedSearchRequest _portletSharedSearchRequest;
	private final RenderRequest _renderRequest;
	private String _scopeParameterName;
	private String _scopeParameterValue;
	private final SearchBarPrecedenceHelper _searchBarPrecedenceHelper;
	private SearchScopePreference _searchScopePreference;
	private final ThemeDisplay _themeDisplay;

}
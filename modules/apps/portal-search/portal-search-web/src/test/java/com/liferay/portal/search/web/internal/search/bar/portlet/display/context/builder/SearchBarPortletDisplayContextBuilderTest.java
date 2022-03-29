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

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.web.internal.display.context.SearchScope;
import com.liferay.portal.search.web.internal.display.context.SearchScopePreference;
import com.liferay.portal.search.web.internal.portlet.preferences.PortletPreferencesLookup;
import com.liferay.portal.search.web.internal.search.bar.portlet.SearchBarPortletPreferences;
import com.liferay.portal.search.web.internal.search.bar.portlet.configuration.SearchBarPortletInstanceConfiguration;
import com.liferay.portal.search.web.internal.search.bar.portlet.display.context.SearchBarPortletDisplayContext;
import com.liferay.portal.search.web.internal.search.bar.portlet.helper.SearchBarPrecedenceHelper;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portlet.PortletPreferencesImpl;

import java.lang.reflect.Field;

import java.util.Optional;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.powermock.api.mockito.PowerMockito;

/**
 * @author Adam Brandizzi
 */
public class SearchBarPortletDisplayContextBuilderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		_setUpHttp();
		_setUpLanguageUtil();
		_setUpPortal();
		_setUpThemeDisplay();
	}

	@Test
	public void testDestinationBlank() throws ReadOnlyException {
		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		portletPreferences.setValue(
			SearchBarPortletPreferences.PREFERENCE_KEY_DESTINATION,
			StringPool.BLANK);

		SearchBarPortletDisplayContextBuilder
			searchBarPortletDisplayContextBuilder =
				_createSearchBarPortletDisplayContextBuilder(
					portletPreferences);

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayContextBuilder.build();

		Assert.assertFalse(
			searchBarPortletDisplayContext.isDestinationUnreachable());
	}

	@Test
	public void testDestinationNull() throws ReadOnlyException {
		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		portletPreferences.setValue(
			SearchBarPortletPreferences.PREFERENCE_KEY_DESTINATION, null);

		SearchBarPortletDisplayContextBuilder
			searchBarPortletDisplayContextBuilder =
				_createSearchBarPortletDisplayContextBuilder(
					portletPreferences);

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayContextBuilder.build();

		Assert.assertFalse(
			searchBarPortletDisplayContext.isDestinationUnreachable());
	}

	@Test
	public void testDestinationUnreachable() throws ReadOnlyException {
		String destination = RandomTestUtil.randomString();

		_whenLayoutLocalServiceFetchLayoutByFriendlyURL(destination, null);

		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		portletPreferences.setValue(
			SearchBarPortletPreferences.PREFERENCE_KEY_DESTINATION,
			destination);

		SearchBarPortletDisplayContextBuilder
			searchBarPortletDisplayContextBuilder =
				_createSearchBarPortletDisplayContextBuilder(
					portletPreferences);

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayContextBuilder.build();

		Assert.assertTrue(
			searchBarPortletDisplayContext.isDestinationUnreachable());
	}

	@Test
	public void testDestinationWithLeadingSlash() throws Exception {
		String destination = RandomTestUtil.randomString();

		Layout layout = Mockito.mock(Layout.class);

		_whenLayoutLocalServiceFetchLayoutByFriendlyURL(destination, layout);

		String layoutFriendlyURL = RandomTestUtil.randomString();

		_whenPortalGetLayoutFriendlyURL(layout, layoutFriendlyURL);

		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		portletPreferences.setValue(
			SearchBarPortletPreferences.PREFERENCE_KEY_DESTINATION,
			StringPool.SLASH.concat(destination));

		SearchBarPortletDisplayContextBuilder
			searchBarPortletDisplayContextBuilder =
				_createSearchBarPortletDisplayContextBuilder(
					portletPreferences);

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayContextBuilder.build();

		Assert.assertEquals(
			layoutFriendlyURL, searchBarPortletDisplayContext.getSearchURL());

		Assert.assertFalse(
			searchBarPortletDisplayContext.isDestinationUnreachable());
	}

	@Test
	public void testDestinationWithoutLeadingSlash() throws Exception {
		String destination = RandomTestUtil.randomString();

		Layout layout = Mockito.mock(Layout.class);

		_whenLayoutLocalServiceFetchLayoutByFriendlyURL(destination, layout);

		String layoutFriendlyURL = RandomTestUtil.randomString();

		_whenPortalGetLayoutFriendlyURL(layout, layoutFriendlyURL);

		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		portletPreferences.setValue(
			SearchBarPortletPreferences.PREFERENCE_KEY_DESTINATION,
			destination);

		SearchBarPortletDisplayContextBuilder
			searchBarPortletDisplayContextBuilder =
				_createSearchBarPortletDisplayContextBuilder(
					portletPreferences);

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayContextBuilder.build();

		Assert.assertEquals(
			layoutFriendlyURL, searchBarPortletDisplayContext.getSearchURL());

		Assert.assertFalse(
			searchBarPortletDisplayContext.isDestinationUnreachable());
	}

	@Test
	public void testSamePageNoDestination() {
		Mockito.doReturn(
			"http://example.com/web/guest/home?param=arg"
		).when(
			_themeDisplay
		).getURLCurrent();

		SearchBarPortletDisplayContextBuilder
			searchBarPortletDisplayContextBuilder =
				_createSearchBarPortletDisplayContextBuilder(null);

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayContextBuilder.build();

		Assert.assertFalse(
			searchBarPortletDisplayContext.isDestinationUnreachable());

		Assert.assertEquals(
			"/web/guest/home", searchBarPortletDisplayContext.getSearchURL());
	}

	@Test
	public void testSearchScope() {
		SearchBarPortletDisplayContextBuilder
			searchBarPortletDisplayContextBuilder =
				_createSearchBarPortletDisplayContextBuilder(null);

		try {
			Field field = ReflectionUtil.getDeclaredField(
				SearchBarPortletDisplayContextBuilder.class,
				"_scopeParameterValue");

			field.set(
				searchBarPortletDisplayContextBuilder,
				Optional.of(SearchScope.EVERYTHING.getParameterString()));
		}
		catch (Exception exception) {
		}

		Assert.assertEquals(
			SearchScope.EVERYTHING,
			searchBarPortletDisplayContextBuilder.getSearchScope());
	}

	protected HttpServletRequest getHttpServletRequest() {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			(ThemeDisplay)httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);

		return httpServletRequest;
	}

	protected String getPath(String url) {
		if (Validator.isNull(url)) {
			return url;
		}

		if (url.startsWith(Http.HTTP)) {
			int pos = url.indexOf(
				CharPool.SLASH, Http.HTTPS_WITH_SLASH.length());

			url = url.substring(pos);
		}

		int pos = url.indexOf(CharPool.QUESTION);

		if (pos == -1) {
			return url;
		}

		return url.substring(0, pos);
	}

	private LiferayPortletRequest _createLiferayPortletRequest() {
		LiferayPortletRequest liferayPortletRequest = Mockito.mock(
			LiferayPortletRequest.class);

		Mockito.doReturn(
			getHttpServletRequest()
		).when(
			liferayPortletRequest
		).getHttpServletRequest();

		return liferayPortletRequest;
	}

	private SearchBarPortletDisplayContextBuilder
		_createSearchBarPortletDisplayContextBuilder(
			PortletPreferences portletPreferences) {

		RenderRequest renderRequest = Mockito.mock(RenderRequest.class);

		Mockito.when(
			renderRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);

		if (portletPreferences != null) {
			Mockito.when(
				renderRequest.getPreferences()
			).thenReturn(
				portletPreferences
			);
		}

		PortletSharedSearchRequest portletSharedSearchRequest = Mockito.mock(
			PortletSharedSearchRequest.class);

		PortletSharedSearchResponse portletSharedSearchResponse = Mockito.mock(
			PortletSharedSearchResponse.class);

		Mockito.when(
			portletSharedSearchRequest.search(renderRequest)
		).thenReturn(
			portletSharedSearchResponse
		);

		SearchResponse searchResponse1 = Mockito.mock(SearchResponse.class);

		Mockito.when(
			portletSharedSearchResponse.getSearchResponse()
		).thenReturn(
			searchResponse1
		);

		SearchRequest searchRequest1 = Mockito.mock(SearchRequest.class);

		Mockito.when(
			searchResponse1.getRequest()
		).thenReturn(
			searchRequest1
		);

		Mockito.when(
			searchRequest1.isEmptySearchEnabled()
		).thenReturn(
			false
		);

		SearchBarPrecedenceHelper searchBarPrecedenceHelper = Mockito.mock(
			SearchBarPrecedenceHelper.class);

		Mockito.when(
			searchBarPrecedenceHelper.findHeaderSearchBarPortletOptional(
				_themeDisplay)
		).thenReturn(
			Optional.empty()
		);

		SearchResponse searchResponse2 = Mockito.mock(SearchResponse.class);

		Mockito.when(
			portletSharedSearchResponse.getFederatedSearchResponse(
				Mockito.anyObject())
		).thenReturn(
			searchResponse2
		);

		SearchRequest searchRequest2 = Mockito.mock(SearchRequest.class);

		Mockito.when(
			searchResponse2.getRequest()
		).thenReturn(
			searchRequest2
		);

		Mockito.when(
			portletSharedSearchResponse.getParameter(
				Mockito.anyObject(), Mockito.anyObject())
		).thenReturn(
			Optional.empty()
		);

		SearchBarPortletDisplayContextBuilder
			searchBarPortletDisplayContextBuilder =
				new SearchBarPortletDisplayContextBuilder(
					_http, _layoutLocalService, _portal,
					Mockito.mock(PortletPreferencesLookup.class),
					portletSharedSearchRequest, renderRequest,
					searchBarPrecedenceHelper);

		try {
			Field field = ReflectionUtil.getDeclaredField(
				SearchBarPortletDisplayContextBuilder.class,
				"_searchScopePreference");

			field.set(
				searchBarPortletDisplayContextBuilder,
				SearchScopePreference.getSearchScopePreference("everything"));
		}
		catch (Exception exception) {
		}

		return searchBarPortletDisplayContextBuilder;
	}

	private void _setUpHttp() {
		Mockito.doAnswer(
			invocation -> getPath(invocation.getArgumentAt(0, String.class))
		).when(
			_http
		).getPath(
			Mockito.anyString()
		);
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(PowerMockito.mock(Language.class));
	}

	private void _setUpPortal() {
		Mockito.doReturn(
			_createLiferayPortletRequest()
		).when(
			_portal
		).getLiferayPortletRequest(
			Mockito.anyObject()
		);
	}

	private void _setUpThemeDisplay() {
		Mockito.when(
			_themeDisplay.getScopeGroup()
		).thenReturn(
			_group
		);

		try {
			Mockito.when(
				_portletDisplay.getPortletInstanceConfiguration(Mockito.any())
			).thenReturn(
				Mockito.mock(SearchBarPortletInstanceConfiguration.class)
			);
		}
		catch (Exception exception) {
		}

		Mockito.when(
			_themeDisplay.getPortletDisplay()
		).thenReturn(
			_portletDisplay
		);
	}

	private void _whenLayoutLocalServiceFetchLayoutByFriendlyURL(
		String friendlyURL, Layout layout) {

		if (!StringUtil.startsWith(friendlyURL, CharPool.SLASH)) {
			friendlyURL = StringPool.SLASH.concat(friendlyURL);
		}

		Mockito.doReturn(
			layout
		).when(
			_layoutLocalService
		).fetchLayoutByFriendlyURL(
			Mockito.anyLong(), Mockito.anyBoolean(), Mockito.eq(friendlyURL)
		);
	}

	private void _whenPortalGetLayoutFriendlyURL(
			Layout layout, String layoutFriendlyURL)
		throws Exception {

		Mockito.doReturn(
			layoutFriendlyURL
		).when(
			_portal
		).getLayoutFriendlyURL(
			Mockito.eq(layout), Mockito.any()
		);
	}

	@Mock
	private Group _group;

	@Mock
	private Http _http;

	@Mock
	private LayoutLocalService _layoutLocalService;

	@Mock
	private Portal _portal;

	@Mock
	private PortletDisplay _portletDisplay;

	@Mock
	private ThemeDisplay _themeDisplay;

}
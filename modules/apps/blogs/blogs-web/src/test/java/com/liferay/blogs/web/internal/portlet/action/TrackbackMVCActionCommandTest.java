/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.portlet.action;

import com.liferay.blogs.exception.NoSuchEntryException;
import com.liferay.blogs.linkback.LinkbackConsumer;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryService;
import com.liferay.blogs.service.BlogsEntryServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContextFunction;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author André de Oliveira
 */
public class TrackbackMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		ReflectionTestUtil.setFieldValue(
			BlogsEntryServiceUtil.class, "_service", _blogsEntryService);

		_setUpActionRequest();
		_setUpBlogsEntry();
		_setUpPortalUtil();
		_setUpPortletPreferencesFactoryUtil();
		_setUpPropsUtil();
		_setUpThemeDisplay();
		_setUpUserLocalService();
	}

	@Test
	public void testDisabledComments() throws Exception {
		_whenGetEntryThenReturn(_blogsEntry);

		Mockito.when(
			_portletPreferences.getValue("enableComments", null)
		).thenReturn(
			"false"
		);

		addTrackback();

		_assertError("Comments are disabled");
	}

	@Test
	public void testMismatchedIPAddress() throws Exception {
		_whenGetEntryThenReturn(_blogsEntry);

		_initURL("123");

		addTrackback();

		_assertError("Remote IP does not match the trackback URL's IP");
	}

	@Test
	public void testMissingURL() throws Exception {
		_whenGetEntryThenReturn(_blogsEntry);

		addTrackback();

		_assertError("Trackback requires a valid permanent URL");
	}

	@Test(expected = NoSuchEntryException.class)
	public void testNoSuchEntryException() throws Exception {
		_whenGetEntryThenThrow(new NoSuchEntryException());

		_initValidURL();

		addTrackback();
	}

	@Test
	public void testPrincipalException() throws Exception {
		_whenGetEntryThenThrow(new PrincipalException());

		_initValidURL();

		addTrackback();

		_assertError(
			"Blog entry must have guest view permissions to enable trackbacks");
	}

	@Test
	public void testSuccess() throws Exception {
		_whenGetEntryThenReturn(_blogsEntry);

		_originalMockHttpServletRequest.setParameter(
			"blog_name", "__blogName__");
		_originalMockHttpServletRequest.setParameter("excerpt", "__excerpt__");
		_originalMockHttpServletRequest.setParameter("title", "__title__");

		_initValidURL();

		addTrackback();

		_assertSuccess();

		long commentId = Mockito.verify(
			_commentManager
		).addComment(
			Mockito.isNull(), Mockito.eq(1L), Mockito.eq(1L),
			Mockito.eq(BlogsEntry.class.getName()), Mockito.eq(1L),
			Mockito.eq("__blogName__"), Mockito.eq("__title__"),
			Mockito.anyString(), Mockito.any(ServiceContextFunction.class)
		);

		Mockito.verify(
			_linkbackConsumer
		).addNewTrackback(
			Mockito.eq(commentId),
			Mockito.eq(_mockHttpServletRequest.getRemoteAddr()),
			Mockito.endsWith("/-/blogs/__urlTitle__")
		);
	}

	@Test
	public void testTrackbacksNotEnabled() throws Exception {
		_whenGetEntryThenReturn(_blogsEntry);

		Mockito.when(
			_blogsEntry.isAllowTrackbacks()
		).thenReturn(
			false
		);

		_initValidURL();

		addTrackback();

		_assertError("Trackbacks are not enabled");
	}

	protected void addTrackback() throws Exception {
		TrackbackMVCActionCommand trackbackMVCActionCommand =
			new TrackbackMVCActionCommand();

		ReflectionTestUtil.setFieldValue(
			trackbackMVCActionCommand, "_commentManager", _commentManager);
		ReflectionTestUtil.setFieldValue(
			trackbackMVCActionCommand, "_linkbackConsumer", _linkbackConsumer);
		ReflectionTestUtil.setFieldValue(
			trackbackMVCActionCommand, "_portal", PortalUtil.getPortal());
		ReflectionTestUtil.setFieldValue(
			trackbackMVCActionCommand, "_userLocalService", _userLocalService);

		trackbackMVCActionCommand.addTrackback(_actionRequest, _actionResponse);
	}

	private void _assertError(String msg) throws Exception {
		_assertResponseContent(
			StringBundler.concat(
				"<?xml version=\"1.0\" encoding=\"utf-8\"?><response><error>1",
				"</error><message>", msg, "</message></response>"));
	}

	private void _assertResponseContent(String expected) throws Exception {
		Assert.assertEquals(
			expected, _mockHttpServletResponse.getContentAsString());
	}

	private void _assertSuccess() throws Exception {
		_assertResponseContent(
			"<?xml version=\"1.0\" encoding=\"utf-8\"?><response><error>0" +
				"</error></response>");
	}

	private void _initURL(String remoteIP) {
		_originalMockHttpServletRequest.addParameter("url", remoteIP);
	}

	private void _initValidURL() {
		_initURL(_mockHttpServletRequest.getRemoteAddr());
	}

	private void _setUpActionRequest() {
		Mockito.when(
			_actionRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);

		Mockito.when(
			_actionRequest.getPreferences()
		).thenReturn(
			_portletPreferences
		);

		Mockito.when(
			_actionRequest.getAttribute(WebKeys.BLOGS_ENTRY)
		).thenReturn(
			_blogsEntry
		);
	}

	private void _setUpBlogsEntry() {
		Mockito.when(
			_blogsEntry.getEntryId()
		).thenReturn(
			1L
		);

		Mockito.when(
			_blogsEntry.getGroupId()
		).thenReturn(
			1L
		);

		Mockito.when(
			_blogsEntry.getUrlTitle()
		).thenReturn(
			"__urlTitle__"
		);

		Mockito.when(
			_blogsEntry.isAllowTrackbacks()
		).thenReturn(
			true
		);
	}

	private void _setUpPortalUtil() throws Exception {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getOriginalServletRequest(Mockito.any())
		).thenReturn(
			_originalMockHttpServletRequest
		);

		Mockito.when(
			portal.getHttpServletRequest(Mockito.any())
		).thenReturn(
			_mockHttpServletRequest
		);

		Mockito.when(
			portal.getHttpServletResponse(Mockito.any())
		).thenReturn(
			_mockHttpServletResponse
		);

		portalUtil.setPortal(portal);
	}

	private void _setUpPortletPreferencesFactoryUtil() throws Exception {
		PortletPreferencesFactoryUtil portletPreferencesFactoryUtil =
			new PortletPreferencesFactoryUtil();

		PortletPreferencesFactory portletPreferencesFactory = Mockito.mock(
			PortletPreferencesFactory.class);

		Mockito.when(
			portletPreferencesFactory.getExistingPortletSetup(
				Mockito.any(PortletRequest.class))
		).thenReturn(
			_portletPreferences
		);

		portletPreferencesFactoryUtil.setPortletPreferencesFactory(
			portletPreferencesFactory);
	}

	private void _setUpPropsUtil() {
		PropsTestUtil.setProps(Collections.emptyMap());
	}

	private void _setUpThemeDisplay() {
		Mockito.when(
			_themeDisplay.getCompanyId()
		).thenReturn(
			1L
		);
	}

	private void _setUpUserLocalService() throws Exception {
		Mockito.when(
			_userLocalService.getGuestUserId(Mockito.eq(1L))
		).thenReturn(
			1L
		);
	}

	private void _whenGetEntryThenReturn(BlogsEntry blogsEntry)
		throws Exception {

		Mockito.when(
			_actionRequest.getParameter(Mockito.anyString())
		).thenReturn(
			String.valueOf(10L)
		);

		Mockito.when(
			_blogsEntryService.getEntry(Mockito.anyLong())
		).thenReturn(
			blogsEntry
		);
	}

	private void _whenGetEntryThenThrow(Throwable throwable) throws Exception {
		Mockito.when(
			_actionRequest.getParameter(Mockito.anyString())
		).thenReturn(
			String.valueOf(10L)
		);

		Mockito.when(
			_blogsEntryService.getEntry(Mockito.anyLong())
		).thenThrow(
			throwable
		);
	}

	private final ActionRequest _actionRequest = Mockito.mock(
		ActionRequest.class);
	private final ActionResponse _actionResponse = Mockito.mock(
		ActionResponse.class);
	private final BlogsEntry _blogsEntry = Mockito.mock(BlogsEntry.class);
	private final BlogsEntryService _blogsEntryService = Mockito.mock(
		BlogsEntryService.class);
	private final CommentManager _commentManager = Mockito.mock(
		CommentManager.class);
	private final LinkbackConsumer _linkbackConsumer = Mockito.mock(
		LinkbackConsumer.class);
	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();
	private final MockHttpServletResponse _mockHttpServletResponse =
		new MockHttpServletResponse();
	private final MockHttpServletRequest _originalMockHttpServletRequest =
		new MockHttpServletRequest();
	private final PortletPreferences _portletPreferences = Mockito.mock(
		PortletPreferences.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);
	private final UserLocalService _userLocalService = Mockito.mock(
		UserLocalService.class);

}
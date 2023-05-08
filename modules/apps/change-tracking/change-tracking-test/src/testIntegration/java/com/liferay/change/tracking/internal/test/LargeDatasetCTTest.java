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

package com.liferay.change.tracking.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.fragment.contributor.FragmentCollectionContributor;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class LargeDatasetCTTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_ctCollection = _ctCollectionLocalService.addCTCollection(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			LargeDatasetCTTest.class.getName(), null);

		_group = GroupTestUtil.addGroup();

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_httpServletRequest = _getHttpServletRequest(TestPropsValues.getUser());

		_themeDisplay = _getThemeDisplay(
			_httpServletRequest, TestPropsValues.getUser());

		_dlFolder = DLTestUtil.addDLFolder(_group.getGroupId());

		try (SafeCloseable safeCloseable1 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			if (_SITE_INITIALIZER) {
				SiteInitializer siteInitializer =
					_siteInitializerRegistry.getSiteInitializer(
						"com.liferay.site.initializer.masterclass");

				siteInitializer.initialize(_group.getGroupId());
			}

			for (int i = 0; i < _PAGE_CONTENT_TEST_SIZE; i++) {
				_layoutContent = LayoutTestUtil.addTypeContentLayout(_group);

				_addFragmentEntryLink(_layoutContent.getPlid());
			}

			for (int i = 0; i < _PAGE_WIDGET_TEST_SIZE; i++) {
				_layoutWidget = LayoutTestUtil.addTypePortletLayout(_group);
			}

			for (int i = 0; i < _WEB_CONTENT_TEST_SIZE; i++) {
				_journalArticle = JournalTestUtil.addArticle(
					_group.getGroupId(), RandomTestUtil.randomString(),
					RandomTestUtil.randomString());
			}

			for (int i = 0; i < _DOCUMENT_TEST_SIZE; i++) {
				_dlFileEntry = DLTestUtil.addDLFileEntry(
					_dlFolder.getFolderId());
			}
		}
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testDiscardEntry() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			_ctCollectionLocalService.discardCTEntry(
				_ctCollection.getCtCollectionId(),
				_portal.getClassNameId(Layout.class.getName()),
				_layoutContent.getPrimaryKey(), false);
		}
	}

	private void _addFragmentEntryLink(long plid) throws Exception {
		Layout layout = _layoutLocalService.fetchLayout(plid);

		Layout draftLayout = layout.fetchDraftLayout();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		FragmentCollectionContributor fragmentCollectionContributor =
			_fragmentCollectionContributorRegistry.
				getFragmentCollectionContributor("BASIC_COMPONENT");

		List<FragmentEntry> fragmentEntries =
			fragmentCollectionContributor.getFragmentEntries(
				_themeDisplay.getLocale());

		long defaultSegmentsExperienceId =
			SegmentsExperienceLocalServiceUtil.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		for (FragmentEntry fragmentEntry : fragmentEntries) {
			FragmentEntryLinkLocalServiceUtil.addFragmentEntryLink(
				TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
				0, fragmentEntry.getFragmentEntryId(),
				defaultSegmentsExperienceId, draftLayout.getPlid(),
				fragmentEntry.getCss(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
				StringPool.BLANK, StringPool.BLANK, 1, StringPool.BLANK,
				fragmentEntry.getType(), serviceContext);
		}
	}

	private HttpServletRequest _getHttpServletRequest(User user)
		throws Exception {

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			WebKeys.CURRENT_URL, "http://localhost:8080/");

		UserTestUtil.setUser(user);

		httpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(httpServletRequest, user));

		return httpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay(
			HttpServletRequest httpServletRequest, User user)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		Company company = _companyLocalService.getCompany(
			_group.getCompanyId());

		themeDisplay.setCompany(company);

		themeDisplay.setLanguageId(_group.getDefaultLanguageId());

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			_group.getGroupId(), false);

		themeDisplay.setLayoutSet(layoutSet);

		themeDisplay.setLocale(
			LocaleUtil.fromLanguageId(_group.getDefaultLanguageId()));
		themeDisplay.setLookAndFeel(layoutSet.getTheme(), null);
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setPortalDomain(company.getVirtualHostname());
		themeDisplay.setPortalURL(company.getPortalURL(_group.getGroupId()));
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setServerPort(8080);
		themeDisplay.setSignedIn(true);
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(user);

		return themeDisplay;
	}

	private static final int _DOCUMENT_TEST_SIZE = 1;

	private static final int _PAGE_CONTENT_TEST_SIZE = 1;

	private static final int _PAGE_WIDGET_TEST_SIZE = 1;

	private static final boolean _SITE_INITIALIZER = false;

	private static final int _WEB_CONTENT_TEST_SIZE = 1;

	@Inject
	private static CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private static LayoutLocalService _layoutLocalService;

	@Inject
	private static LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	@DeleteAfterTestRun
	private DLFileEntry _dlFileEntry;

	@DeleteAfterTestRun
	private DLFolder _dlFolder;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@DeleteAfterTestRun
	private Group _group;

	private HttpServletRequest _httpServletRequest;

	@DeleteAfterTestRun
	private JournalArticle _journalArticle;

	@DeleteAfterTestRun
	private Layout _layoutContent;

	@DeleteAfterTestRun
	private Layout _layoutWidget;

	@Inject
	private Portal _portal;

	@Inject
	private SiteInitializerRegistry _siteInitializerRegistry;

	private ThemeDisplay _themeDisplay;

}
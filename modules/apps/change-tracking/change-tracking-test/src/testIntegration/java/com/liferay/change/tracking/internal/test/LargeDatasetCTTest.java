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
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
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
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
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
import java.util.Locale;

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
@DataGuard(scope = DataGuard.Scope.NONE)
@RunWith(Arquillian.class)
public class LargeDatasetCTTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	//@Test
	public void generateData() throws Exception {
		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			LargeDatasetCTTest.class.getName(), null);

		Group group = GroupTestUtil.addGroup();

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		Locale locale = LocaleUtil.fromLanguageId(group.getDefaultLanguageId());

		DLFolder dlFolder = DLTestUtil.addDLFolder(group.getGroupId());

		Layout layoutContent = null;

		try (SafeCloseable safeCloseable1 =
				 CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					 ctCollection.getCtCollectionId())) {

			if (_SITE_INITIALIZER) {
				SiteInitializer siteInitializer =
					_siteInitializerRegistry.getSiteInitializer(
						"com.liferay.site.initializer.masterclass");

				siteInitializer.initialize(group.getGroupId());
			}

			for (int i = 0; i < _PAGE_CONTENT_TEST_SIZE; i++) {
				layoutContent = LayoutTestUtil.addTypeContentLayout(group);

				_addFragmentEntryLink(
					group.getGroupId(), layoutContent.getPlid(), locale);
			}

			for (int i = 0; i < _PAGE_WIDGET_TEST_SIZE; i++) {
				LayoutTestUtil.addTypePortletLayout(group);
			}

			for (int i = 0; i < _WEB_CONTENT_TEST_SIZE; i++) {
				JournalTestUtil.addArticle(
					group.getGroupId(), RandomTestUtil.randomString(),
					RandomTestUtil.randomString());
			}

			for (int i = 0; i < _DOCUMENT_TEST_SIZE; i++) {
				DLTestUtil.addDLFileEntry(dlFolder.getFolderId());
			}
		}
		java.io.File file = new java.io.File("/home/me/generated.csv");

		if (file.exists()) {
			file.delete();
		}

		FileUtil.write(file, ctCollection.getCtCollectionId() + "," + layoutContent.getPrimaryKey());
	}

	@Test
	public void testDiscardEntry() throws Exception {
		java.io.File file = new java.io.File("/home/me/generated.csv");
		
		List<String> values = StringUtil.split(FileUtil.read(file));

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			_ctCollectionLocalService.discardCTEntry(
				GetterUtil.getLong(values.get(0)),
				_portal.getClassNameId(Layout.class.getName()),
				GetterUtil.getLong(values.get(1)), false);
		}
	}

	private void _addFragmentEntryLink(long groupId, long plid, Locale locale) throws Exception {
		Layout layout = _layoutLocalService.fetchLayout(plid);

		Layout draftLayout = layout.fetchDraftLayout();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		FragmentCollectionContributor fragmentCollectionContributor =
			_fragmentCollectionContributorRegistry.
				getFragmentCollectionContributor("BASIC_COMPONENT");

		List<FragmentEntry> fragmentEntries =
			fragmentCollectionContributor.getFragmentEntries(
				locale);

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

	private static final int _DOCUMENT_TEST_SIZE = 1000;

	private static final int _PAGE_CONTENT_TEST_SIZE = 800;

	private static final int _PAGE_WIDGET_TEST_SIZE = 200;

	private static final boolean _SITE_INITIALIZER = false;

	private static final int _WEB_CONTENT_TEST_SIZE = 1000;

	@Inject
	private static CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private static LayoutLocalService _layoutLocalService;

	@Inject
	private static LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Inject
	private Portal _portal;

	@Inject
	private SiteInitializerRegistry _siteInitializerRegistry;

}
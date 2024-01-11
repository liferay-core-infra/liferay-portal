/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.linkback.LinkbackConsumer;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.IdentityServiceContextFunction;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class TrackbackMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testAddTrackback() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		BlogsEntry blogsEntry = _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), StringUtil.randomString(),
			StringUtil.randomString(), new Date(), serviceContext);

		IdentityServiceContextFunction serviceContextFunction =
			new IdentityServiceContextFunction(serviceContext);

		_commentManager.addComment(
			TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			BlogsEntry.class.getName(), blogsEntry.getEntryId(),
			StringUtil.randomString(), serviceContextFunction);

		int initialCommentsCount = _commentManager.getCommentsCount(
			BlogsEntry.class.getName(), blogsEntry.getEntryId());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"entryId", String.valueOf(blogsEntry.getEntryId()));

		mockLiferayPortletActionRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST,
			new MockHttpServletRequest() {
				{
					addParameter("blog_name", StringUtil.randomString());
					addParameter("excerpt", StringUtil.randomString());
					addParameter("title", StringUtil.randomString());
					addParameter("url", "127.0.0.1");
				}
			});
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			new ThemeDisplay() {
				{
					setCompany(
						_companyLocalService.getCompany(
							TestPropsValues.getCompanyId()));
					setUser(TestPropsValues.getUser());
				}
			});

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertEquals(
			initialCommentsCount + 1,
			_commentManager.getCommentsCount(
				BlogsEntry.class.getName(), blogsEntry.getEntryId()));

		_linkbackConsumer.verifyNewTrackbacks();
	}

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private CommentManager _commentManager;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private LinkbackConsumer _linkbackConsumer;

	@Inject(filter = "mvc.command.name=/blogs/trackback")
	private MVCActionCommand _mvcActionCommand;

}
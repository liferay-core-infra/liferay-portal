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

package com.liferay.portal.osgi.web.portlet.container.embedded.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.layoutconfiguration.util.velocity.TemplateProcessor;
import com.liferay.portal.osgi.web.portlet.container.test.BasePortletContainerTestCase;
import com.liferay.portal.osgi.web.portlet.container.test.util.PortletContainerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Hai Yu
 */
@RunWith(Arquillian.class)
public class EmbeddedPortletWhenEmbeddingPortletUsingTemplateProcessorTest
	extends BasePortletContainerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testShouldCreatePortletEmbeddedPortletPreferences()
		throws Exception {

		setUpPortlet(
			testPortlet, new HashMapDictionary<>(), TEST_PORTLET_ID, false);

		HttpServletRequest httpServletRequest =
			PortletContainerTestUtil.getHttpServletRequest(group, layout);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)layout.getLayoutType());

		Assert.assertFalse(
			"layout.isPortletEmbedded() should return false",
			layout.isPortletEmbedded(TEST_PORTLET_ID, layout.getGroupId()));

		TemplateProcessor templateProcessor = new TemplateProcessor(
			httpServletRequest, new MockHttpServletResponse(), TEST_PORTLET_ID);

		templateProcessor.processPortlet(TEST_PORTLET_ID);

		Assert.assertTrue(
			"layout.isPortletEmbedded() should return true",
			layout.isPortletEmbedded(TEST_PORTLET_ID, layout.getGroupId()));
	}

}
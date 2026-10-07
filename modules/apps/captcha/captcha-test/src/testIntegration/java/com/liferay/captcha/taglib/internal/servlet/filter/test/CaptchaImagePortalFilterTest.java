/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.taglib.internal.servlet.filter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.servlet.PortalSessionContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.HttpSession;

import java.net.HttpURLConnection;

import java.util.TreeMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jiefeng Wu
 */
@RunWith(Arquillian.class)
public class CaptchaImagePortalFilterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		RoleTestUtil.removeResourcePermission(
			RoleConstants.GUEST, Layout.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(layout.getPlid()), ActionKeys.VIEW);

		_hostname =
			StringUtil.toLowerCase(RandomTestUtil.randomString()) + ".test";

		_layoutSetLocalService.updateVirtualHosts(
			_group.getGroupId(), false,
			TreeMapBuilder.put(
				_hostname, StringPool.BLANK
			).build());
	}

	@After
	public void tearDown() throws Exception {
		_layoutSetLocalService.updateVirtualHosts(
			_group.getGroupId(), false, new TreeMap<>());
	}

	@Test
	@TestInfo("LPD-107079")
	public void testProcessFilterWhenPromptIsDisabled() throws Exception {
		_assertCaptchaImage();
	}

	@Test
	@TestInfo("LPD-107079")
	public void testProcessFilterWhenPromptIsEnabled() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.login.web.internal.configuration." +
						"AuthLoginConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"promptEnabled", true
					).build())) {

			_assertCaptchaImage();
		}
	}

	private void _assertCaptchaImage() throws Exception {
		String captchaId = RandomTestUtil.randomString();

		Http.Options options = new Http.Options();

		options.addHeader("Host", _hostname);
		options.setFollowRedirects(false);
		options.setLocation(
			StringBundler.concat(
				"http://localhost:", _portal.getPortalServerPort(false),
				"/c/portal/captcha/get_image?captchaId=", captchaId));

		_http.URLtoByteArray(options);

		Http.Response response = options.getResponse();

		Assert.assertEquals(
			HttpURLConnection.HTTP_OK, response.getResponseCode());

		String contentType = response.getContentType();

		Assert.assertTrue(
			contentType, contentType.startsWith(ContentTypes.IMAGE_PNG));

		boolean captchaTextStored = false;

		for (HttpSession httpSession : PortalSessionContext.values()) {
			if (Validator.isNotNull(
					httpSession.getAttribute(
						captchaId + WebKeys.CAPTCHA_TEXT))) {

				captchaTextStored = true;

				break;
			}
		}

		Assert.assertTrue(captchaTextStored);
	}

	@DeleteAfterTestRun
	private Group _group;

	private String _hostname;

	@Inject
	private Http _http;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private Portal _portal;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.encryptor.Encryptor;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.security.Key;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Eric Yan
 */
public class EncryptedServletRequestTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		Snapshot<Encryptor> snapshot = Mockito.mock(Snapshot.class);

		Encryptor encryptor = Mockito.mock(Encryptor.class);

		Mockito.when(
			encryptor.decrypt(_key, _PORTLET_ID_ENCRYPTED)
		).thenReturn(
			_PORTLET_ID
		);

		Mockito.when(
			snapshot.get()
		).thenReturn(
			encryptor
		);

		_autoCloseable = ReflectionTestUtil.setFieldValueWithAutoCloseable(
			EncryptorUtil.class, "_encryptorSnapshot", snapshot);
	}

	@After
	public void tearDown() throws Exception {
		_autoCloseable.close();
	}

	@Test
	public void testGetParameter() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("p_p_id", _PORTLET_ID_ENCRYPTED);
		mockHttpServletRequest.setQueryString(
			"p_p_id=" + _PORTLET_ID_ENCRYPTED);

		HttpServletRequest httpServletRequest = new EncryptedServletRequest(
			mockHttpServletRequest, _key);

		Assert.assertEquals(
			_PORTLET_ID, httpServletRequest.getParameter("p_p_id"));

		Assert.assertEquals(
			_PORTLET_ID_ENCRYPTED,
			mockHttpServletRequest.getParameter("p_p_id"));
	}

	@Test
	public void testGetParameterFromRequestBody() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("p_p_id", _PORTLET_ID_ENCRYPTED);

		HttpServletRequest httpServletRequest = new EncryptedServletRequest(
			mockHttpServletRequest, _key);

		Assert.assertEquals(
			_PORTLET_ID_ENCRYPTED, httpServletRequest.getParameter("p_p_id"));
	}

	@Test
	public void testGetParameterWhenForwarded() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_SERVLET_FORWARD_QUERY_STRING,
			"p_p_id=" + _PORTLET_ID_ENCRYPTED);
		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_SERVLET_FORWARD_REQUEST_URI,
			RandomTestUtil.randomString());
		mockHttpServletRequest.setParameter("p_p_id", _PORTLET_ID_ENCRYPTED);

		String plid = String.valueOf(RandomTestUtil.randomLong());

		mockHttpServletRequest.setParameter("p_l_id", plid);
		mockHttpServletRequest.setQueryString("p_l_id=" + plid);

		HttpServletRequest httpServletRequest = new EncryptedServletRequest(
			mockHttpServletRequest, _key);

		Assert.assertEquals(plid, httpServletRequest.getParameter("p_l_id"));
		Assert.assertEquals(
			_PORTLET_ID, httpServletRequest.getParameter("p_p_id"));
	}

	@Test
	public void testGetParameterWhenForwardedWithRepeatedParameter() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_SERVLET_FORWARD_QUERY_STRING,
			"p_p_id=" + _PORTLET_ID_ENCRYPTED);
		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_SERVLET_FORWARD_REQUEST_URI,
			RandomTestUtil.randomString());
		mockHttpServletRequest.setParameter("p_p_id", _PORTLET_ID_ENCRYPTED);

		String plid = String.valueOf(RandomTestUtil.randomLong());

		mockHttpServletRequest.setParameter("p_l_id", plid);
		mockHttpServletRequest.setQueryString(
			StringBundler.concat(
				"p_l_id=", plid, "&p_p_id=", _PORTLET_ID_ENCRYPTED));

		HttpServletRequest httpServletRequest = new EncryptedServletRequest(
			mockHttpServletRequest, _key);

		Assert.assertEquals(plid, httpServletRequest.getParameter("p_l_id"));
		Assert.assertEquals(
			_PORTLET_ID, httpServletRequest.getParameter("p_p_id"));
	}

	@Test
	public void testGetParameterWithEncryptParameter() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(WebKeys.ENCRYPT, "1");
		mockHttpServletRequest.setQueryString(WebKeys.ENCRYPT + "=1");

		HttpServletRequest httpServletRequest = new EncryptedServletRequest(
			mockHttpServletRequest, _key);

		Assert.assertEquals(
			"1", httpServletRequest.getParameter(WebKeys.ENCRYPT));
	}

	@Test
	public void testGetParameterWithPortalAuthenticationToken() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		String authenticationToken = RandomTestUtil.randomString();

		mockHttpServletRequest.setParameter("p_auth", authenticationToken);
		mockHttpServletRequest.setQueryString("p_auth=" + authenticationToken);

		HttpServletRequest httpServletRequest = new EncryptedServletRequest(
			mockHttpServletRequest, _key);

		Assert.assertEquals(
			authenticationToken, httpServletRequest.getParameter("p_auth"));
	}

	private static final String _PORTLET_ID = RandomTestUtil.randomString();

	private static final String _PORTLET_ID_ENCRYPTED =
		RandomTestUtil.randomString();

	private AutoCloseable _autoCloseable;
	private final Key _key = Mockito.mock(Key.class);

}
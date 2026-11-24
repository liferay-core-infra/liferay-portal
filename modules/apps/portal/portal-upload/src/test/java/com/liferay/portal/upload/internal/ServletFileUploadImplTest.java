/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upload.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upload.UploadException;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProviderUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.HashMap;

import org.apache.commons.fileupload.FileCountLimitExceededException;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jiefeng Wu
 */
public class ServletFileUploadImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testParseRequest() throws Exception {
		ServletFileUploadImpl servletFileUploadImpl =
			new ServletFileUploadImpl();

		servletFileUploadImpl.activate(new HashMap<>());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		String boundary = RandomTestUtil.randomString();

		String part = StringBundler.concat(
			StringPool.DOUBLE_DASH, boundary,
			"\r\nContent-Disposition: form-data; name=\"",
			RandomTestUtil.randomString(), "\"\r\n\r\n\r\n");

		String content = StringBundler.concat(
			part.repeat(1001), StringPool.DOUBLE_DASH, boundary,
			StringPool.DOUBLE_DASH);

		mockHttpServletRequest.setContent(content.getBytes());

		mockHttpServletRequest.setContentType(
			StringBundler.concat(
				ContentTypes.MULTIPART_FORM_DATA, "; boundary=", boundary));

		try (MockedStatic<UploadServletRequestConfigurationProviderUtil>
				mockedStatic = Mockito.mockStatic(
					UploadServletRequestConfigurationProviderUtil.class)) {

			mockedStatic.when(
				UploadServletRequestConfigurationProviderUtil::
					getMaxSizeWithPadding
			).thenReturn(
				-1L
			);

			servletFileUploadImpl.parseRequest(
				mockHttpServletRequest, RandomTestUtil.randomString(), 1024);

			Assert.fail();
		}
		catch (UploadException uploadException) {
			FileCountLimitExceededException fileCountLimitExceededException =
				(FileCountLimitExceededException)uploadException.getCause();

			Assert.assertEquals(
				1000, fileCountLimitExceededException.getLimit());
		}
	}

}
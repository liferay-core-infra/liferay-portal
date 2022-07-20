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

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StreamUtil;

import java.io.ByteArrayInputStream;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Hai Yu
 */
public class ServletResponseUtilContentLengthTest {

	@BeforeClass
	public static void setUpClass() {
		PropsUtil.setProps(Mockito.mock(Props.class));
	}

	@Test
	public void testContentLengthLowerThanBufferSizeOfStreamUtil()
		throws Exception {

		byte[] bytes = new byte[StreamUtil.BUFFER_SIZE];

		Arrays.fill(bytes, (byte)48);

		int contentLength = StreamUtil.BUFFER_SIZE - 1;

		ServletResponseUtil.write(
			_mockHttpServletResponse, new ByteArrayInputStream(bytes),
			(long)contentLength);

		String content = new String(bytes);

		Assert.assertEquals(
			String.valueOf(contentLength),
			_mockHttpServletResponse.getHeader(HttpHeaders.CONTENT_LENGTH));

		Assert.assertEquals(
			content.substring(0, contentLength),
			_mockHttpServletResponse.getContentAsString());
	}

	private final MockHttpServletResponse _mockHttpServletResponse =
		new MockHttpServletResponse();

}
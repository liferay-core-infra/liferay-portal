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
	public void testContentLengthCompareWithBufferSizeOfStreamUtil()
		throws Exception {

		_assertContentLength(
			StreamUtil.BUFFER_SIZE + 1, StreamUtil.BUFFER_SIZE + 1);

		_assertContentLength(StreamUtil.BUFFER_SIZE, StreamUtil.BUFFER_SIZE);

		_assertContentLength(
			StreamUtil.BUFFER_SIZE - 1, StreamUtil.BUFFER_SIZE);
	}

	@Test
	public void testContentLengthGreatorThanInputStreamLength()
		throws Exception {

		_assertContentLength(100, 50);
	}

	@Test
	public void testContentLengthLowerThanOrEqualsToZero() throws Exception {
		_assertContentLength(0, 100);
		_assertContentLength(-1, 100);
	}

	private void _assertContentLength(
			int expectedContentLength, int arrayLength)
		throws Exception {

		byte[] bytes = new byte[arrayLength];

		Arrays.fill(bytes, (byte)48);

		try {
			ServletResponseUtil.write(
				_mockHttpServletResponse, new ByteArrayInputStream(bytes),
				expectedContentLength);

			String content = new String(bytes);

			if (expectedContentLength <= 0) {
				Assert.assertNull(
					_mockHttpServletResponse.getHeader(
						HttpHeaders.CONTENT_LENGTH));
				Assert.assertEquals(
					content, _mockHttpServletResponse.getContentAsString());
			}
			else {
				Assert.assertEquals(
					String.valueOf(expectedContentLength),
					_mockHttpServletResponse.getHeader(
						HttpHeaders.CONTENT_LENGTH));

				if (expectedContentLength >= arrayLength) {
					Assert.assertEquals(
						content, _mockHttpServletResponse.getContentAsString());
				}
				else {
					Assert.assertEquals(
						content.substring(0, expectedContentLength),
						_mockHttpServletResponse.getContentAsString());
				}
			}
		}
		finally {
			_mockHttpServletResponse.setCommitted(false);
			_mockHttpServletResponse.reset();
		}
	}

	private final MockHttpServletResponse _mockHttpServletResponse =
		new MockHttpServletResponse();

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upload;

import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.File;
import java.io.InputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Preston Crary
 */
public class LiferayInputStreamTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_fileUtilMockedStatic.when(
			FileUtil::createTempFile
		).thenReturn(
			FileUtil.createTempFile("temp", String.valueOf(System.nanoTime()))
		);
	}

	@AfterClass
	public static void tearDownClass() {
		_fileUtilMockedStatic.close();
	}

	@After
	public void tearDown() {
		_liferayInputStream.cleanUp();
	}

	@Test
	public void testGetCachedInputStreamFromFile() throws Exception {
		testGetCachedInputStream(true, _UNCACHEABLE_BYTES);
	}

	@Test
	public void testGetCachedInputStreamFromMemory() throws Exception {
		testGetCachedInputStream(true, _CACHEABLE_BYTES);
	}

	@Test
	public void testGetCachedInputStreamWithoutCopying() throws Exception {
		testGetCachedInputStream(false, _UNCACHEABLE_BYTES);
	}

	@Test
	public void testInitialReadWithCacheableBytes() throws Exception {
		testInitialRead(_CACHEABLE_BYTES);
	}

	@Test
	public void testInitialReadWithUncacheableBytes() throws Exception {
		testInitialRead(_UNCACHEABLE_BYTES);
	}

	@Test
	public void testInitialReadWithUncacheableBytesWithCopying()
		throws Exception {

		_mockHttpServletRequest.setAttribute(
			LiferayInputStream.COPY_MULTIPART_STREAM_TO_FILE, Boolean.FALSE);

		testInitialRead(_UNCACHEABLE_BYTES);
	}

	@Test
	public void testTempFileCreation() throws Exception {
		testTempFile(_UNCACHEABLE_BYTES);

		Assert.assertTrue(_file.exists());
	}

	@Test
	public void testTempFileDeletion() throws Exception {
		testTempFile(_UNCACHEABLE_BYTES);

		_liferayInputStream.cleanUp();

		Assert.assertFalse(_file.exists());
	}

	@Test
	public void testTempFileNoCreation() throws Exception {
		testTempFile(_CACHEABLE_BYTES);

		Assert.assertNull(_file);
	}

	protected void assertCachedInputStreamReadability(
			boolean readable, byte[] expectedBytes)
		throws Exception {

		InputStream inputStream = _liferayInputStream.getCachedInputStream();

		if (readable) {
			byte[] bytes = new byte[expectedBytes.length];

			Assert.assertEquals(expectedBytes.length, inputStream.read(bytes));

			Assert.assertArrayEquals(expectedBytes, bytes);
		}
		else {
			Assert.assertEquals(
				-1, inputStream.read(new byte[expectedBytes.length]));
		}
	}

	protected void testGetCachedInputStream(boolean readable, byte[] content)
		throws Exception {

		_mockHttpServletRequest.setAttribute(
			LiferayInputStream.COPY_MULTIPART_STREAM_TO_FILE, readable);
		_mockHttpServletRequest.setContent(content);

		_liferayInputStream = new LiferayInputStream(_mockHttpServletRequest);

		_liferayInputStream.read(new byte[content.length], 0, content.length);

		assertCachedInputStreamReadability(readable, content);
	}

	protected void testInitialRead(byte[] content) throws Exception {
		_mockHttpServletRequest.setContent(content);

		_liferayInputStream = new LiferayInputStream(_mockHttpServletRequest);

		byte[] bytes = new byte[content.length];

		Assert.assertEquals(
			content.length, _liferayInputStream.read(bytes, 0, bytes.length));

		Assert.assertArrayEquals(content, bytes);
	}

	protected void testTempFile(byte[] content) throws Exception {
		_mockHttpServletRequest.setContent(content);

		_liferayInputStream = new LiferayInputStream(_mockHttpServletRequest);

		_liferayInputStream.read(new byte[content.length], 0, content.length);
	}

	private static final byte[] _CACHEABLE_BYTES =
		new byte[(int)LiferayInputStream.THRESHOLD_SIZE - 1];

	private static final byte[] _UNCACHEABLE_BYTES =
		new byte[(int)LiferayInputStream.THRESHOLD_SIZE];

	private static final MockedStatic<FileUtil> _fileUtilMockedStatic =
		Mockito.mockStatic(FileUtil.class);

	static {
		for (int i = 0; i < _CACHEABLE_BYTES.length; i++) {
			_CACHEABLE_BYTES[i] = (byte)i;
		}

		for (int i = 0; i < _UNCACHEABLE_BYTES.length; i++) {
			_UNCACHEABLE_BYTES[i] = (byte)i;
		}
	}

	private File _file;
	private LiferayInputStream _liferayInputStream;
	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();

}
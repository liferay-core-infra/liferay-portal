/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.io;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Shuyang Zhou
 */
public class AutoDeleteFileInputStreamTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@Before
	public void setUp() throws IOException {
		Files.deleteIfExists(_tempFile.toPath());

		Assert.assertTrue(_tempFile.createNewFile());
	}

	@After
	public void tearDown() throws IOException {
		Files.deleteIfExists(_tempFile.toPath());
	}

	@Test
	public void testCloseWithFileChannel() throws IOException {
		try (AutoDeleteFileInputStream autoDeleteFileInputStream =
				new AutoDeleteFileInputStream(_tempFile)) {

			Assert.assertNotNull(autoDeleteFileInputStream.getChannel());

			Assert.assertTrue(_tempFile.exists());
		}

		Assert.assertFalse(_tempFile.exists());
	}

	@Test
	public void testFileNotExistOnClose() throws IOException {
		try (AutoDeleteFileInputStream autoDeleteFileInputStream =
				new AutoDeleteFileInputStream(_tempFile)) {

			ReflectionTestUtil.setFieldValue(
				autoDeleteFileInputStream, "_file", new File("NotExist"));
		}

		Assert.assertTrue(_tempFile.exists());
	}

	@Test
	public void testNormalClose() throws IOException {
		try (AutoDeleteFileInputStream autoDeleteFileInputStream =
				new AutoDeleteFileInputStream(_tempFile)) {

			Assert.assertTrue(_tempFile.exists());
		}

		Assert.assertFalse(_tempFile.exists());
	}

	@Test
	public void testUnableToDeleteOnClose() throws IOException {
		Exception exception = new IOException();

		try (MockedStatic<Files> mockedStatic = Mockito.mockStatic(
				Files.class)) {

			mockedStatic.when(
				() -> Files.deleteIfExists(_tempFile.toPath())
			).thenThrow(
				exception
			);

			try (AutoDeleteFileInputStream autoDeleteFileInputStream =
					new AutoDeleteFileInputStream(_tempFile)) {

				Assert.assertTrue(_tempFile.exists());
			}

			Assert.fail();
		}
		catch (IOException ioException) {
			Assert.assertSame(exception, ioException);
		}

		Assert.assertTrue(_tempFile.exists());
	}

	private final File _tempFile = new File("tempFile");

}
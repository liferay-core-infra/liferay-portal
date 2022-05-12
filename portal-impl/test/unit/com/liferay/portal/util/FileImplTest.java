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

package com.liferay.portal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.OSDetector;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.File;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Brian Wing Shun Chan
 * @author Roberto Díaz
 */
public class FileImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		FastDateFormatFactoryUtil fastDateFormatFactoryUtil =
			new FastDateFormatFactoryUtil();

		fastDateFormatFactoryUtil.setFastDateFormatFactory(
			new FastDateFormatFactoryImpl());

		_tempFolder = _fileImpl.createTempFolder();
	}

	@After
	public void tearDown() throws Exception {
		_fileImpl.delete(_tempFolder);
	}

	@Test
	public void testAppendParentheticalSuffixWhenFileNameHasParenthesis() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test(1).jsp", "1");

		Assert.assertEquals("test(1) (1).jsp", fileName);
	}

	@Test
	public void testAppendParentheticalSuffixWithMultipleCharacterValue() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "1!$eae1");

		Assert.assertEquals("test (1!$eae1).jsp", fileName);
	}

	@Test
	public void testAppendParentheticalSuffixWithMultipleNumericalValue() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "1111111");

		Assert.assertEquals("test (1111111).jsp", fileName);
	}

	@Test
	public void testAppendParentheticalSuffixWithMultipleStringValue() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "AAAAAAA");

		Assert.assertEquals("test (AAAAAAA).jsp", fileName);
	}

	@Test
	public void testAppendParentheticalSuffixWithMultipleStringWithSpaceValue() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "A B");

		Assert.assertEquals("test (A B).jsp", fileName);
	}

	@Test
	public void testAppendParentheticalSuffixWithSingleNumericalValue() {
		String fileName = _fileImpl.appendParentheticalSuffix("test.jsp", "1");

		Assert.assertEquals("test (1).jsp", fileName);
	}

	@Test
	public void testAppendParentheticalSuffixWithSingleStringValue() {
		String fileName = _fileImpl.appendParentheticalSuffix("test.jsp", "A");

		Assert.assertEquals("test (A).jsp", fileName);
	}

	@Test
	public void testAppendSuffix() {
		Assert.assertEquals("test_rtl", _fileImpl.appendSuffix("test", "_rtl"));
		Assert.assertEquals(
			"test_rtl.css", _fileImpl.appendSuffix("test.css", "_rtl"));
		Assert.assertEquals(
			"/folder/test_rtl.css",
			_fileImpl.appendSuffix("/folder/test.css", "_rtl"));
	}

	@Test
	public void testGetPathBackSlashForwardSlash() {
		Assert.assertEquals(
			"aaa\\bbb/ccc\\ddd",
			_fileImpl.getPath("aaa\\bbb/ccc\\ddd/eee.fff"));
	}

	@Test
	public void testGetPathForwardSlashBackSlash() {
		Assert.assertEquals(
			"aaa/bbb\\ccc/ddd", _fileImpl.getPath("aaa/bbb\\ccc/ddd\\eee.fff"));
	}

	@Test
	public void testGetPathNoPath() {
		Assert.assertEquals(StringPool.SLASH, _fileImpl.getPath("aaa.bbb"));
	}

	@Test
	public void testGetShortFileNameBackSlashForwardSlash() {
		Assert.assertEquals(
			"eee.fff", _fileImpl.getShortFileName("aaa\\bbb/ccc\\ddd/eee.fff"));
	}

	@Test
	public void testGetShortFileNameForwardSlashBackSlash() {
		Assert.assertEquals(
			"eee.fff", _fileImpl.getShortFileName("aaa/bbb\\ccc/ddd\\eee.fff"));
	}

	@Test
	public void testGetShortFileNameNoPath() {
		Assert.assertEquals("aaa.bbb", _fileImpl.getShortFileName("aaa.bbb"));
	}

	@Test
	public void testStripSuffixAppendedWhenFileNameHasParenthesis() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test(1).jsp", "1");

		Assert.assertEquals(
			"test(1).jsp", _fileImpl.stripParentheticalSuffix(fileName));
	}

	@Test
	public void testStripSuffixAppendedWithMultipleCharacterValue() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "1!$eae1");

		Assert.assertEquals(
			"test.jsp", _fileImpl.stripParentheticalSuffix(fileName));
	}

	@Test
	public void testStripSuffixAppendedWithMultipleNumericalValue() {
		String fileName2 = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "1111111");

		Assert.assertEquals(
			"test.jsp", _fileImpl.stripParentheticalSuffix(fileName2));
	}

	@Test
	public void testStripSuffixAppendedWithMultipleStringValue() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "AAAAAAA");

		Assert.assertEquals(
			"test.jsp", _fileImpl.stripParentheticalSuffix(fileName));
	}

	@Test
	public void testStripSuffixAppendedWithMultipleStringWithSpaceValue() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "A B");

		Assert.assertEquals(
			"test.jsp", _fileImpl.stripParentheticalSuffix(fileName));
	}

	@Test
	public void testStripSuffixAppendedWithSingleNumericalValue() {
		String fileName = _fileImpl.appendParentheticalSuffix("test.jsp", "1");

		Assert.assertEquals(
			"test.jsp", _fileImpl.stripParentheticalSuffix(fileName));
	}

	@Test
	public void testStripSuffixAppendedWithSingleStringValue() {
		String fileName = _fileImpl.appendParentheticalSuffix("test.jsp", "A");

		Assert.assertEquals(
			"test.jsp", _fileImpl.stripParentheticalSuffix(fileName));
	}

	@Test
	public void testStripSuffixWhenFileNameHasInvertedParenthesis() {
		Assert.assertEquals(
			"test)1(.jsp", _fileImpl.stripParentheticalSuffix("test)1(.jsp"));
	}

	@Test
	public void testStripSuffixWhenFileNameHasNoCloseParenthesis() {
		Assert.assertEquals(
			"test(1.jsp", _fileImpl.stripParentheticalSuffix("test(1.jsp"));
	}

	@Test
	public void testStripSuffixWhenFileNameHasNoExtension() {
		Assert.assertEquals(
			"test", _fileImpl.stripParentheticalSuffix("test (1)"));
	}

	@Test
	public void testStripSuffixWhenFileNameHasNoParentheticalSuffix() {
		Assert.assertEquals(
			"test.jsp", _fileImpl.stripParentheticalSuffix("test.jsp"));
	}

	@Test
	public void testStripSuffixWhenFileNameHasParenthesisAtStart() {
		Assert.assertEquals(
			"()test.jsp", _fileImpl.stripParentheticalSuffix("()test.jsp"));
	}

	@Test
	public void testUnzip() throws Exception {
		_fileImpl.unzip(
			_getResourcePath("testForFileImplTest.zip"), _tempFolder);

		_assertExists("zip/test/directory");
		_assertExists("zip/test/entry/entry.txt");

		Assert.assertTrue(
			_canFileReadWrite(_getTempTestFilePath("zip/test/directory")));

		Assert.assertTrue(
			_canFileReadWrite(
				_getTempTestFilePath("zip/test/entry/entry.txt")));
	}

	@Test
	public void testUnzipZipSlipVulnerable() throws Exception {
		_fileImpl.unzip(
			_getResourcePath("test_slipForFileImplTest.zip"), _tempFolder);

		_assertExists("good.txt");
		_assertDoesNotExist("tmp/evil.txt");
	}

	private void _assertDoesNotExist(String name) throws Exception {
		Path fullPath = _getTempTestFilePath(name);

		Assert.assertFalse(Files.exists(fullPath));
	}

	private void _assertExists(String name) throws Exception {
		Path fullPath = _getTempTestFilePath(name);

		Assert.assertTrue(Files.exists(fullPath));
	}

	private boolean _canFileReadWrite(Path path) {
		if (OSDetector.isWindows()) {
			File file = path.toFile();

			if (file.canExecute() && file.canRead() && file.canWrite()) {
				return true;
			}

			return false;
		}

		if (Files.isReadable(path) && Files.isWritable(path)) {
			return true;
		}

		return false;
	}

	private File _getResourcePath(String fileName) throws Exception {
		Class<? extends FileImplTest> clazz = getClass();

		URL url = clazz.getResource("dependencies");

		Path path = Paths.get(url.toURI());

		Path pathOfFile = path.resolve(fileName);

		return pathOfFile.toFile();
	}

	private Path _getTempTestFilePath(String path) throws Exception {
		Path pathToTempFolder = _tempFolder.toPath();

		return pathToTempFolder.resolve(path);
	}

	private final FileImpl _fileImpl = new FileImpl();
	private File _tempFolder;

}
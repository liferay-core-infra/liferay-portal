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

package com.liferay.portal.lpkg.deployer.internal;

import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.lpkg.deployer.test.util.LPKGTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.FileImpl;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Jiefeng Wu
 */
public class ContainerLPKGUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testDeploy() throws Exception {
		Path targetPath = Paths.get(
			PropsValues.MODULE_FRAMEWORK_MARKETPLACE_DIR);

		Path testPath = Files.createDirectories(targetPath);

		File lpkgContainerFile = _createLPKGContainerFile(
			testPath, "outside.lpkg", "inside.lpkg");

		try {
			ContainerLPKGUtil.deploy(lpkgContainerFile, null);

			Assert.assertTrue(Files.exists(testPath.resolve("inside.lpkg")));
		}
		finally {
			_fileImpl.deltree(testPath.toFile());
		}
	}

	@Test
	public void testDeployWithZipSlip() throws Exception {
		Path targetPath = Paths.get(
			PropsValues.MODULE_FRAMEWORK_MARKETPLACE_DIR);

		Path testPath = Files.createDirectories(targetPath);

		File lpkgContainerFile = _createLPKGContainerFile(
			testPath, "outside.lpkg", "good.lpkg", "../bad.lpkg");

		try (LogCapture logCapture = LoggerTestUtil.configureJDKLogger(
				ContainerLPKGUtil.class.getName(), Level.WARNING)) {

			ContainerLPKGUtil.deploy(lpkgContainerFile, null);

			Path parentOfTestPath = testPath.getParent();

			Assert.assertTrue(Files.exists(testPath.resolve("good.lpkg")));
			Assert.assertFalse(
				Files.exists(parentOfTestPath.resolve("bad.lpkg")));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Invalid LPKG File name: ../bad.lpkg", logEntry.getMessage());
		}
		finally {
			_fileImpl.deltree(testPath.toFile());
		}
	}

	private File _createLPKGContainerFile(
			Path destinationPath, String fileName, String... entries)
		throws Exception {

		File lpkgFile = new File(destinationPath.toFile(), fileName);

		try (ZipOutputStream zipOutputStream = new ZipOutputStream(
				new FileOutputStream(lpkgFile))) {

			for (String entry : entries) {
				Path path = Paths.get(destinationPath.toString(), entry);

				Files.deleteIfExists(path);

				Files.createFile(path);

				LPKGTestUtil.createLPKG(path, _SYMBOLIC_NAME, true);

				zipOutputStream.putNextEntry(new ZipEntry(entry));

				try (InputStream inputStream = new FileInputStream(
						path.toFile());
					OutputStream outputStream = StreamUtil.uncloseable(
						zipOutputStream)) {

					StreamUtil.transfer(inputStream, outputStream);
				}

				Files.delete(path);
			}
		}

		return lpkgFile;
	}

	private static final String _SYMBOLIC_NAME = "container.lpkg.test";

	private final FileImpl _fileImpl = new FileImpl();

}
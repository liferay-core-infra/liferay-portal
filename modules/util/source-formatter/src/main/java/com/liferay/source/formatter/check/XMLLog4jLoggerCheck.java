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

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.io.File;
import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author Kevin Lee
 */
public class XMLLog4jLoggerCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (isPortalSource() &&
			(fileName.endsWith("-log4j-ext.xml") ||
			 fileName.endsWith("-log4j.xml"))) {

			_checkLoggers(fileName, absolutePath, content);
		}

		return content;
	}

	private void _checkLoggers(
			String fileName, String absolutePath, String content)
		throws Exception {

		Document document = SourceUtil.readXML(content);

		Element rootElement = document.getRootElement();

		List<Path> srcPaths = _getSrcPaths(absolutePath);

		for (Element loggersElement :
				(List<Element>)rootElement.elements("Loggers")) {

			for (Element loggerElement :
					(List<Element>)loggersElement.elements("Logger")) {

				String name = loggerElement.attributeValue("name");

				if (!name.startsWith("com.liferay")) {
					continue;
				}

				boolean exists = false;

				String pathName = StringUtil.replace(
					name, CharPool.PERIOD, CharPool.SLASH);

				for (Path srcPath : srcPaths) {
					Path path = srcPath.resolve(pathName);

					if (Files.exists(path)) {
						exists = true;

						break;
					}

					path = srcPath.resolve(pathName + ".java");

					if (Files.exists(path)) {
						exists = true;

						break;
					}
				}

				if (!exists) {
					String message = String.format(
						"Package/class does not exist in base module: '%s'",
						name);

					addMessage(fileName, message);
				}
			}
		}
	}

	private List<Path> _getSrcPaths(String absolutePath) throws Exception {
		List<Path> srcPaths = new ArrayList<>();

		if (!(absolutePath.contains("/src/") ||
			  absolutePath.contains("/test/"))) {

			return srcPaths;
		}

		int x = absolutePath.indexOf("/src/");

		if (x == -1) {
			x = absolutePath.indexOf("/test/");
		}

		File file = new File(absolutePath.substring(0, x));

		if (!isModulesFile(absolutePath) || isModulesApp(absolutePath, false)) {
			file = file.getParentFile();
		}

		Path filePath = file.toPath();

		Files.walkFileTree(
			filePath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					Path mainSrcPath;
					Path testSrcPath;

					if (isModulesFile(absolutePath)) {
						mainSrcPath = path.resolve("src/main/java");
						testSrcPath = path.resolve("src/test/java");
					}
					else {
						mainSrcPath = path.resolve("src");
						testSrcPath = path.resolve("test/unit");
					}

					if (Files.exists(mainSrcPath)) {
						srcPaths.add(mainSrcPath);
					}

					if (Files.exists(testSrcPath)) {
						srcPaths.add(testSrcPath);
					}

					if (Objects.equals(path, filePath)) {
						return FileVisitResult.CONTINUE;
					}

					return FileVisitResult.SKIP_SUBTREE;
				}

			});

		return srcPaths;
	}

}
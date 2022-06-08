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

package com.liferay.portal.osgi.web.wab.generator.internal.artifact;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.file.install.FileInstaller;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.framework.Constants;

/**
 * @author Gregory Amerson
 */
public class ClientExtensionArtifactURLTransformer implements FileInstaller {

	@Override
	public boolean canTransformURL(File artifact) {
		String name = artifact.getName();

		if (!name.endsWith(".zip")) {
			return false;
		}

		if (_isClientExtensionZip(artifact)) {
			return true;
		}

		return false;
	}

	@Override
	public URL transformURL(File artifact) throws Exception {
		String fileName = artifact.getName();

		String symbolicName = fileName;

		Matcher matcher = _symbolicNamePattern.matcher(fileName);

		if (matcher.matches()) {
			symbolicName = matcher.group(1);
		}

		return new URL(
			"clientextension", null,
			StringBundler.concat(
				artifact.getPath(), "?", Constants.BUNDLE_SYMBOLICNAME, "=",
				symbolicName, "&Web-ContextPath=/", symbolicName,
				"&protocol=file"));
	}

	@Override
	public void uninstall(File file) {
	}

	private boolean _isClientExtensionZip(File artifact) {
		try (ZipFile zipFile = new ZipFile(artifact)) {
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

			boolean foundConfigJson = false;
			boolean foundStatic = false;

			while (enumeration.hasMoreElements() &&
				   (!foundConfigJson || !foundStatic)) {

				ZipEntry zipEntry = enumeration.nextElement();

				String name = zipEntry.getName();

				if (name.endsWith("config.json") && (name.indexOf("/") == -1)) {
					foundConfigJson = true;
				}
				else if (name.startsWith("static/")) {
					foundStatic = true;
				}
			}

			if (foundConfigJson && foundStatic) {
				return true;
			}

			return false;
		}
		catch (IOException ioException) {
			_log.error("Unable to check resources in " + artifact, ioException);
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ClientExtensionArtifactURLTransformer.class);

	private static final Pattern _symbolicNamePattern = Pattern.compile(
		"(.*)[-0-9\\.]*\\.zip$", Pattern.DOTALL);

}
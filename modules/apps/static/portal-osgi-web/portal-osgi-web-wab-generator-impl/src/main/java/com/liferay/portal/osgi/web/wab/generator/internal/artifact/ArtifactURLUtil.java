/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.wab.generator.internal.artifact;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.Enumeration;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.framework.Constants;

/**
 * @author Matthew Tambara
 * @author Raymond Augé
 * @author Gregory Amerson
 */
public class ArtifactURLUtil {

	public static String getClientExtensionSymbolicName(String path) {
		int x = path.lastIndexOf('/');
		int y = path.lastIndexOf(CharPool.PERIOD);

		return path.substring(x + 1, y);
	}

	public static String getSymbolicName(String path) {
		int x = path.lastIndexOf('/');
		int y = path.lastIndexOf(CharPool.PERIOD);

		String symbolicName = path.substring(x + 1, y);

		Matcher matcher = _pattern.matcher(symbolicName);

		if (matcher.matches()) {
			symbolicName = matcher.group(1);
		}

		return symbolicName;
	}

	public static String getVirtualInstanceId(String path) {
		File file = new File(path);

		File parentDirectory = file.getParentFile();

		if (Objects.equals(
				parentDirectory,
				new File(PropsValues.MODULE_FRAMEWORK_CLIENT_EXTENSIONS_DIR))) {

			return null;
		}

		String virtualInstanceId = parentDirectory.getName();

		if (Objects.equals(
				virtualInstanceId,
				PortalInstancePool.getWebId(
					PortalInstancePool.getDefaultCompanyId())) ||
			Objects.equals(virtualInstanceId, "default")) {

			return null;
		}

		return virtualInstanceId;
	}

	public static URL transform(URL artifact) throws Exception {
		String contextName = null;
		boolean clientExtension = false;

		String path = artifact.getPath();

		String fileExtension = path.substring(
			path.lastIndexOf(CharPool.PERIOD) + 1);

		if (fileExtension.equals("war")) {
			try (ZipFile zipFile = new ZipFile(new File(artifact.toURI()))) {
				contextName = _readServletContextName(zipFile);
			}
		}

		String virtualInstanceId = null;

		String symbolicName = getSymbolicName(path);

		if (fileExtension.equals("zip") && _isClientExtensionZip(path)) {
			symbolicName = getClientExtensionSymbolicName(path);
			clientExtension = true;

			virtualInstanceId = getVirtualInstanceId(path);

			if (Validator.isNotNull(virtualInstanceId)) {
				symbolicName = symbolicName + "_" + virtualInstanceId;
			}
		}

		if (contextName == null) {
			contextName = symbolicName;
		}

		StringBuilder sb = new StringBuilder();

		sb.append(artifact.getPath());
		sb.append(StringPool.QUESTION);
		sb.append(Constants.BUNDLE_SYMBOLICNAME);
		sb.append(StringPool.EQUAL);
		sb.append(symbolicName);
		sb.append("&Web-ContextPath=/");
		sb.append(contextName);
		sb.append("&fileExtension=");
		sb.append(fileExtension);
		sb.append("&protocol=file");

		if (clientExtension && Validator.isNotNull(virtualInstanceId)) {
			sb.append("&virtualInstanceId=");
			sb.append(virtualInstanceId);
		}

		return new URL("webbundle", null, sb.toString());
	}

	private static boolean _isClientExtensionZip(String path) {
		try (ZipFile zipFile = new ZipFile(path)) {
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = enumeration.nextElement();

				String name = zipEntry.getName();

				if (name.endsWith(".client-extension-config.json") &&
					(name.indexOf("/") == -1)) {

					return true;
				}
			}
		}
		catch (IOException ioException) {
			_log.error("Path " + path + " is not a valid ZIP", ioException);
		}

		return false;
	}

	private static String _readServletContextName(ZipFile zipFile)
		throws Exception {

		ZipEntry zipEntry = zipFile.getEntry(
			"WEB-INF/liferay-plugin-package.properties");

		if (zipEntry == null) {
			return null;
		}

		Properties properties = new Properties();

		try (InputStream inputStream = zipFile.getInputStream(zipEntry)) {
			properties.load(inputStream);
		}

		return properties.getProperty("servlet-context-name");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ArtifactURLUtil.class);

	private static final Pattern _pattern = Pattern.compile(
		"(.*?)(-[0-9\\.]+)");

}
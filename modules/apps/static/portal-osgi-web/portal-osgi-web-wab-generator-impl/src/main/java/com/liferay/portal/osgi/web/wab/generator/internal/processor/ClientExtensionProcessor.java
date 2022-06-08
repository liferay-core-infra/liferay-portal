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

package com.liferay.portal.osgi.web.wab.generator.internal.processor;

import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.Jar;
import aQute.bnd.version.Version;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;
import com.liferay.whip.util.ReflectionUtil;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.text.Format;

import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Gregory Amerson
 */
public class ClientExtensionProcessor {

	public ClientExtensionProcessor(
		File file, Map<String, String[]> parameters) {

		_file = file;
		_parameters = parameters;
	}

	public File getProcessedFile() throws IOException {
		_clientExtensionBundleDir = _convertToClientExtensionBundleDir();

		if ((_clientExtensionBundleDir == null) ||
			!_clientExtensionBundleDir.exists() ||
			!_clientExtensionBundleDir.isDirectory()) {

			return null;
		}

		File outputFile = null;

		try (Jar jar = new Jar(_clientExtensionBundleDir)) {
			if (jar.getBsn() == null) {
				outputFile = _transformToOSGiBundle(jar);
			}
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}

		if (PropsValues.MODULE_FRAMEWORK_WEB_GENERATOR_GENERATED_WABS_STORE) {
			_writeGeneratedClientExtension(outputFile);
		}

		return outputFile;
	}

	private File _convertToClientExtensionBundleDir() {
		Path clientExtensionBundlePath = null;

		try (ZipFile zipFile = new ZipFile(_file)) {
			clientExtensionBundlePath = Files.createTempDirectory(
				"clientextension");

			Path metatInfResourcesPath = _takePath(
				clientExtensionBundlePath, "META-INF/resources");
			Path osgiInfConfiguratorPath = _takePath(
				clientExtensionBundlePath, "OSGI-INF/configurator");

			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = enumeration.nextElement();

				String name = zipEntry.getName();

				if (zipEntry.isDirectory()) {
					if (name.startsWith("static/")) {
						Path destPath = metatInfResourcesPath.resolve(
							name.replaceAll("^static/", ""));

						Files.createDirectories(destPath);
					}
				}
				else {
					if (!name.contains("/") && name.endsWith(".config.json")) {
						Files.copy(
							zipFile.getInputStream(zipEntry),
							osgiInfConfiguratorPath.resolve(name));
					}
					else if (name.startsWith("static/")) {
						Path destPath = metatInfResourcesPath.resolve(
							name.replaceAll("^static/", ""));

						Files.copy(zipFile.getInputStream(zipEntry), destPath);
					}
				}
			}
		}
		catch (IOException ioException) {
			_log.error(ioException);
		}

		if (_CLIENTEXTENSIONS_STORE) {
			_writeClientExtensionBundleDir(clientExtensionBundlePath.toFile());
		}

		return clientExtensionBundlePath.toFile();
	}

	private String _getWebContextPath() {
		String webContextpath = MapUtil.getString(
			_parameters, "Web-ContextPath");

		if (!webContextpath.startsWith(StringPool.SLASH)) {
			webContextpath = StringPool.SLASH.concat(webContextpath);
		}

		return webContextpath;
	}

	private void _processBundleManifestVersion(Analyzer analyzer) {
		String bundleManifestVersion = MapUtil.getString(
			_parameters, Constants.BUNDLE_MANIFESTVERSION);

		if (Validator.isNull(bundleManifestVersion)) {
			bundleManifestVersion = "2";
		}

		analyzer.setProperty(
			Constants.BUNDLE_MANIFESTVERSION, bundleManifestVersion);
	}

	private void _processBundleSymbolicName(Analyzer analyzer) {
		String bundleSymbolicName = MapUtil.getString(
			_parameters, Constants.BUNDLE_SYMBOLICNAME);

		if (Validator.isNull(bundleSymbolicName)) {
			bundleSymbolicName = _context.substring(1);
		}

		analyzer.setProperty(Constants.BUNDLE_SYMBOLICNAME, bundleSymbolicName);
	}

	private void _processBundleVersion(Analyzer analyzer) {
		_bundleVersion = MapUtil.getString(
			_parameters, Constants.BUNDLE_VERSION);

		if (Validator.isNull(_bundleVersion)) {
			_bundleVersion = "1.0.0";
		}

		if (!Version.isVersion(_bundleVersion)) {

			// Convert from the Maven format to the OSGi format

			Matcher matcher = _versionMavenPattern.matcher(_bundleVersion);

			if (matcher.matches()) {
				_bundleVersion = StringBundler.concat(
					matcher.group(1), ".", matcher.group(3), ".",
					matcher.group(5), ".", matcher.group(7));
			}
			else {
				_bundleVersion =
					"0.0.0." + StringUtil.replace(_bundleVersion, '.', '_');
			}
		}

		analyzer.setProperty(Constants.BUNDLE_VERSION, _bundleVersion);
	}

	private void _processRequireCapability(Builder analyzer) {
		String requireCapability = MapUtil.getString(
			_parameters, Constants.REQUIRE_CAPABILITY);

		if (Validator.isNull(requireCapability)) {
			requireCapability =
				"osgi.extender;filter:=\"(&(osgi.extender=osgi.configurator)" +
					"(version>=1.0)(!(version>=2.0)))\"";
		}

		analyzer.setProperty(Constants.REQUIRE_CAPABILITY, requireCapability);
	}

	private Path _takePath(Path parentPath, String take) throws IOException {
		Path path = parentPath.resolve(take);

		Files.createDirectories(path);

		return path;
	}

	private File _transformToOSGiBundle(Jar jar) throws IOException {
		try (Builder analyzer = new Builder()) {
			analyzer.setBase(_clientExtensionBundleDir);
			analyzer.setJar(jar);
			analyzer.setProperty("Web-ContextPath", _getWebContextPath());

			_processBundleVersion(analyzer);
			_processBundleSymbolicName(analyzer);
			_processBundleManifestVersion(analyzer);
			_processRequireCapability(analyzer);

			try {
				jar = analyzer.build();

				File outputFile = analyzer.getOutputFile(null);

				jar.write(outputFile);

				return outputFile;
			}
			catch (Exception exception) {
				throw new IOException(
					"Unable to calculate the manifest", exception);
			}
		}
	}

	private void _writeClientExtensionBundleDir(File clientExtensionBundleDir) {
		File dir = new File(
			PropsValues.
				MODULE_FRAMEWORK_WEB_GENERATOR_GENERATED_WABS_STORE_DIR);

		dir.mkdirs();

		StringBundler sb = new StringBundler(5);

		String name = _file.getName();

		sb.append(name.substring(0, name.lastIndexOf(StringPool.PERIOD)));

		sb.append(StringPool.DASH);

		Format format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			PropsValues.INDEX_DATE_FORMAT_PATTERN);

		sb.append(format.format(new Date()));

		sb.append(".clientextension.");

		sb.append(FileUtil.getExtension(name));

		try (Jar jar = new Jar(clientExtensionBundleDir)) {
			jar.write(new File(dir, sb.toString()));
		}
		catch (Exception exception) {
			_log.error(
				"Unable to write JAR file for " + clientExtensionBundleDir,
				exception);
		}
	}

	private void _writeGeneratedClientExtension(File file) throws IOException {
		File dir = new File(
			PropsValues.
				MODULE_FRAMEWORK_WEB_GENERATOR_GENERATED_WABS_STORE_DIR);

		dir.mkdirs();

		StringBundler sb = new StringBundler(5);

		String name = _file.getName();

		sb.append(name.substring(0, name.lastIndexOf(StringPool.PERIOD)));

		sb.append(StringPool.DASH);

		Format format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			PropsValues.INDEX_DATE_FORMAT_PATTERN);

		sb.append(format.format(new Date()));

		sb.append(".clientextension.");

		sb.append(FileUtil.getExtension(name));

		FileUtil.copyFile(file, new File(dir, sb.toString()));
	}

	/**
	 * Used diagnostic testing.
	 */
	private static final boolean _CLIENTEXTENSIONS_STORE =
		GetterUtil.getBoolean(
			PropsUtil.get(
				"module.framework.web.generator.clientextensions.store"));

	private static final Log _log = LogFactoryUtil.getLog(
		ClientExtensionProcessor.class);

	private static final Pattern _versionMavenPattern = Pattern.compile(
		"(\\d{1,9})(\\.(\\d{1,9})(\\.(\\d{1,9})(-([-_\\da-zA-Z]+))?)?)?");

	private String _bundleVersion;
	private File _clientExtensionBundleDir;
	private String _context;
	private final File _file;
	private final Map<String, String[]> _parameters;

}
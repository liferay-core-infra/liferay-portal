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

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @author Shuyang Zhou
 */
public class PortalClassPathUtil {

	public static ProcessConfig getPortalProcessConfig() {
		return _portalProcessConfig;
	}

	public static void initializeClassPaths(ServletContext servletContext) {
		ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

		if (classLoader == null) {
			Thread currentThread = Thread.currentThread();

			classLoader = currentThread.getContextClassLoader();
		}

		File[] files = _listClassPathFiles(
			ServletException.class, CentralizedThreadLocal.class);

		if (files.length == 0) {
			throw new IllegalStateException(
				"Class path files could not be loaded");
		}

		StringBundler runtimeClassPathSB = new StringBundler(
			(files.length * 2) + 3);
		StringBundler bootstrapClassPathSB = new StringBundler(
			files.length * 2);

		for (File file : files) {
			if (_isPetraJar(file)) {
				bootstrapClassPathSB.append(file.getAbsolutePath());
				bootstrapClassPathSB.append(File.pathSeparator);
			}

			runtimeClassPathSB.append(file.getAbsolutePath());
			runtimeClassPathSB.append(File.pathSeparator);
		}

		runtimeClassPathSB.setIndex(runtimeClassPathSB.index() - 1);

		if (bootstrapClassPathSB.index() > 0) {
			bootstrapClassPathSB.setIndex(bootstrapClassPathSB.index() - 1);
		}

		if (servletContext != null) {
			runtimeClassPathSB.append(File.pathSeparator);
			runtimeClassPathSB.append(servletContext.getRealPath(""));
			runtimeClassPathSB.append("/WEB-INF/classes");
		}

		ProcessConfig.Builder builder = new ProcessConfig.Builder();

		builder.setArguments(_processArgs);
		builder.setBootstrapClassPath(bootstrapClassPathSB.toString());
		builder.setReactClassLoader(classLoader);
		builder.setRuntimeClassPath(runtimeClassPathSB.toString());

		_portalProcessConfig = builder.build();
	}

	private static boolean _isPetraJar(File file) {
		String filePath = file.getAbsolutePath();

		if (filePath.contains("petra")) {
			try (JarFile jarFile = new JarFile(new File(filePath))) {
				Manifest manifest = jarFile.getManifest();

				if (manifest == null) {
					return false;
				}

				Attributes attributes = manifest.getMainAttributes();

				if (attributes.containsKey(
						new Attributes.Name("Liferay-Releng-App-Title"))) {

					return false;
				}

				return true;
			}
			catch (IOException ioException) {
				_log.error(
					"Unable to resolve bootstrap entry: " + file.getName() +
						" from bundle",
					ioException);
			}
		}

		return false;
	}

	private static File[] _listClassPathFiles(Class<?> clazz) {
		File dir = new File(
			PropsUtil.getLibDir(clazz.getClassLoader(), clazz.getName()));

		if (!dir.isDirectory()) {
			_log.error(dir.toString() + " is not a directory");

			return null;
		}

		return dir.listFiles(
			new FileFilter() {

				@Override
				public boolean accept(File file) {
					if (file.isDirectory()) {
						return false;
					}

					String name = file.getName();

					if (name.equals("bundleFile") || name.endsWith(".jar")) {
						return true;
					}

					return false;
				}

			});
	}

	private static File[] _listClassPathFiles(Class<?>... classes) {
		Set<File> filesSet = new HashSet<>();

		for (Class<?> clazz : classes) {
			File[] files = _listClassPathFiles(clazz);

			if (files != null) {
				Collections.addAll(filesSet, files);
			}
		}

		File[] files = filesSet.toArray(new File[0]);

		Arrays.sort(files);

		return files;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalClassPathUtil.class);

	private static ProcessConfig _portalProcessConfig;
	private static final List<String> _processArgs = Arrays.asList(
		"-Dconfiguration.impl.quiet=true", "-Djava.awt.headless=true",
		"-Dserver.detector.quiet=true", "-Dsystem.properties.quiet=true");

}
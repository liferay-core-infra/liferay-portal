/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.file.install.internal;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.file.install.FileInstaller;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.File;
import java.io.IOException;

import java.net.URI;
import java.net.URL;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedService;

/**
 * @author Matthew Tambara
 */
public class DefaultJarInstaller implements FileInstaller, ManagedService {

	public DefaultJarInstaller(ConfigurationAdmin configurationAdmin) {
		try {
			Configuration configuration = configurationAdmin.getConfiguration(
				"com.liferay.portal.bundle.blacklist.internal.configuration." +
					"BundleBlacklistConfiguration",
				StringPool.QUESTION);

			Dictionary<String, ?> dictionary = configuration.getProperties();

			if (dictionary != null) {
				_atomicReference.set(
					SetUtil.fromArray(
						(String[])dictionary.get(
							"blacklistBundleSymbolicNames")));
			}
		}
		catch (IOException ioException) {
			ReflectionUtil.throwException(ioException);
		}
	}

	@Override
	public boolean canTransformURL(File artifact) {
		String name = artifact.getName();

		if (!name.endsWith(".jar") || _blacklistedFiles.contains(artifact)) {
			return false;
		}

		Set<String> blacklistBundleSymbolicNames = _atomicReference.get();

		if (blacklistBundleSymbolicNames.isEmpty()) {
			return true;
		}

		try (JarFile jarFile = new JarFile(artifact)) {
			Manifest manifest = jarFile.getManifest();

			Attributes attributes = manifest.getMainAttributes();

			String bundleSymbolicName = attributes.getValue(
				"Bundle-SymbolicName");

			if (bundleSymbolicName != null) {
				int index = bundleSymbolicName.indexOf(CharPool.SEMICOLON);

				if (index != -1) {
					bundleSymbolicName = bundleSymbolicName.substring(0, index);
				}

				if (blacklistBundleSymbolicNames.contains(bundleSymbolicName)) {
					if (_log.isInfoEnabled()) {
						_log.info(
							"Skipping blacklisted bundle " +
								bundleSymbolicName);
					}

					_blacklistedFiles.add(artifact);

					return false;
				}
			}
		}
		catch (IOException ioException) {
			ReflectionUtil.throwException(ioException);
		}

		return true;
	}

	@Override
	public URL transformURL(File file) throws Exception {
		URI uri = file.toURI();

		return uri.toURL();
	}

	@Override
	public void uninstall(File file) {
	}

	@Override
	public void updated(Dictionary<String, ?> dictionary) {
		_blacklistedFiles.clear();

		if (dictionary == null) {
			_atomicReference.set(Collections.emptySet());

			return;
		}

		_atomicReference.set(
			SetUtil.fromArray(
				(String[])dictionary.get("blacklistBundleSymbolicNames")));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultJarInstaller.class);

	private final AtomicReference<Set<String>> _atomicReference =
		new AtomicReference<>(Collections.emptySet());
	private final Set<File> _blacklistedFiles = Collections.newSetFromMap(
		new ConcurrentHashMap<>());

}
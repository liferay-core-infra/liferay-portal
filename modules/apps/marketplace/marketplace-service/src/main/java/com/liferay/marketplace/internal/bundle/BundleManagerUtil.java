/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.internal.bundle;

import com.liferay.marketplace.bundle.BundleManager;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.io.File;

import java.util.List;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;

/**
 * @author Ryan Park
 */
public class BundleManagerUtil {

	public static Bundle getBundle(String symbolicName, String version) {
		BundleManager bundleManager = _bundleManagerSnapshot.get();

		return bundleManager.getBundle(symbolicName, version);
	}

	public static List<Bundle> getBundles() {
		BundleManager bundleManager = _bundleManagerSnapshot.get();

		return bundleManager.getBundles();
	}

	public static List<Bundle> getInstalledBundles() {
		BundleManager bundleManager = _bundleManagerSnapshot.get();

		return bundleManager.getInstalledBundles();
	}

	public static Manifest getManifest(File file) {
		BundleManager bundleManager = _bundleManagerSnapshot.get();

		return bundleManager.getManifest(file);
	}

	public static void installLPKG(File file) throws Exception {
		BundleManager bundleManager = _bundleManagerSnapshot.get();

		bundleManager.installLPKG(file);
	}

	public static boolean isInstalled(Bundle bundle) {
		BundleManager bundleManager = _bundleManagerSnapshot.get();

		return bundleManager.isInstalled(bundle);
	}

	public static boolean isInstalled(String symbolicName, String version) {
		BundleManager bundleManager = _bundleManagerSnapshot.get();

		return bundleManager.isInstalled(symbolicName, version);
	}

	public static void uninstallBundle(Bundle bundle) {
		BundleManager bundleManager = _bundleManagerSnapshot.get();

		bundleManager.uninstallBundle(bundle);
	}

	public static void uninstallBundle(String symbolicName, String version) {
		BundleManager bundleManager = _bundleManagerSnapshot.get();

		bundleManager.uninstallBundle(symbolicName, version);
	}

	private static final Snapshot<BundleManager> _bundleManagerSnapshot =
		new Snapshot<>(BundleManagerUtil.class, BundleManager.class);

}
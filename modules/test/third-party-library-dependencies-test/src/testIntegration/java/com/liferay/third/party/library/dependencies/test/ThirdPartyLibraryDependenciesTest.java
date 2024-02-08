/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.third.party.library.dependencies.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.resource.Capability;

/**
 * @author Jiefeng Wu
 */
@RunWith(Arquillian.class)
public class ThirdPartyLibraryDependenciesTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testIfExistedUnusedExportPackageFromSharedDependencies() {
		Bundle bundle = FrameworkUtil.getBundle(
			ThirdPartyLibraryDependenciesTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		for (Bundle curBundle : bundleContext.getBundles()) {
			String curBundleSymbolicName = curBundle.getSymbolicName();

			if (!curBundleSymbolicName.contains(
					"com.liferay.shared.dependencies.")) {

				continue;
			}

			Set<String> inUseExportPackage = _getInUseExportPackages(curBundle);

			for (String exportPackageFromManifest :
					_getExportPackagesFromManifest(curBundle)) {

				Assert.assertTrue(
					curBundle.getSymbolicName() +
						" contains unused export-package: " +
							exportPackageFromManifest,
					inUseExportPackage.contains(exportPackageFromManifest));
			}
		}
	}

	private List<String> _getExportPackagesFromManifest(Bundle bundle) {
		List<String> exportPackageFromManifest = new ArrayList<>();

		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		String rawExportPackages = headers.get("Export-Package");

		for (String rawExportPackage : rawExportPackages.split("\",")) {
			String[] filteredExportPackage = rawExportPackage.split(";");

			exportPackageFromManifest.add(filteredExportPackage[0]);
		}

		return exportPackageFromManifest;
	}

	private Set<String> _getInUseExportPackages(Bundle bundle) {
		Set<String> inUseExportPackages = new HashSet<>();

		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		for (BundleWire bundleWire :
				bundleWiring.getProvidedWires(
					BundleRevision.PACKAGE_NAMESPACE)) {

			Capability capability = bundleWire.getCapability();

			Map<String, Object> attributes = capability.getAttributes();

			String inUseExportPackage = (String)attributes.get(
				BundleRevision.PACKAGE_NAMESPACE);

			inUseExportPackages.add(inUseExportPackage);
		}

		return inUseExportPackages;
	}

}
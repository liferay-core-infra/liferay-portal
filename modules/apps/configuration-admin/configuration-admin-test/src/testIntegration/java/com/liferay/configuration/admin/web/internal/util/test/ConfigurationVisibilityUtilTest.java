/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.configuration.admin.web.internal.configuration.TestConfiguration;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.namespace.PackageNamespace;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class ConfigurationVisibilityUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_bundle = FrameworkUtil.getBundle(
			ConfigurationVisibilityUtilTest.class);

		_bundleContext = _bundle.getBundleContext();

		Bundle webBundle = BundleUtil.getBundle(
			_bundleContext, "com.liferay.configuration.admin.web");

		_configurationVisibilityUtilClass = webBundle.loadClass(
			"com.liferay.configuration.admin.web.internal.util." +
				"ConfigurationVisibilityUtil");
	}

	@Test
	public void testGetClassDoesNotWireConfigurationPackage() throws Exception {
		String packageName = TestConfiguration.class.getPackageName();

		InputStream exporterInputStream = _createBundle(
			StringPool.BLANK,
			HashMapBuilder.put(
				"Export-Package", packageName
			).build());

		Bundle exporterBundle = _bundleContext.installBundle(
			RandomTestUtil.randomString(), exporterInputStream);

		InputStream probeInputStream = _createBundle(
			null,
			HashMapBuilder.put(
				"DynamicImport-Package", "*"
			).build());

		Bundle probeBundle = _bundleContext.installBundle(
			RandomTestUtil.randomString(), probeInputStream);

		try {
			exporterBundle.start();
			probeBundle.start();

			String pid = TestConfiguration.class.getName();

			Class<?> clazz = ReflectionTestUtil.invoke(
				_configurationVisibilityUtilClass, "_getClass",
				new Class<?>[] {Bundle.class, String.class}, probeBundle, pid);

			Assert.assertNull(clazz);

			List<BundleWire> bundleWires = _getBundleWires(packageName);

			Assert.assertEquals(bundleWires.toString(), 0, bundleWires.size());
		}
		finally {
			exporterBundle.uninstall();
			probeBundle.uninstall();

			com.liferay.osgi.util.BundleUtil.refreshBundles(
				_bundleContext, Arrays.asList(exporterBundle, probeBundle));
		}
	}

	@Test
	public void testGetClassWhenClassIsInBundleClassPath() throws Exception {
		String bundleClassPath = RandomTestUtil.randomString();

		InputStream inputStream = _createBundle(
			bundleClassPath + StringPool.SLASH,
			HashMapBuilder.put(
				"Bundle-ClassPath", bundleClassPath
			).build());

		Bundle classPathBundle = _bundleContext.installBundle(
			RandomTestUtil.randomString(), inputStream);

		try {
			classPathBundle.start();

			String pid = TestConfiguration.class.getName();

			Class<?> clazz = ReflectionTestUtil.invoke(
				_configurationVisibilityUtilClass, "_getClass",
				new Class<?>[] {Bundle.class, String.class}, classPathBundle,
				pid);

			Assert.assertEquals(
				classPathBundle, FrameworkUtil.getBundle(clazz));
		}
		finally {
			classPathBundle.uninstall();
		}
	}

	private InputStream _createBundle(
			String dirName, Map<String, String> headers)
		throws Exception {

		Manifest manifest = new Manifest();

		Attributes mainAttributes = manifest.getMainAttributes();

		mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
		mainAttributes.putValue("Bundle-ManifestVersion", "2");
		mainAttributes.putValue(
			"Bundle-SymbolicName", RandomTestUtil.randomString());

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			mainAttributes.putValue(entry.getKey(), entry.getValue());
		}

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		try (JarOutputStream jarOutputStream = new JarOutputStream(
				unsyncByteArrayOutputStream, manifest)) {

			if (dirName != null) {
				_writeClass(dirName, jarOutputStream);
			}
		}

		return new UnsyncByteArrayInputStream(
			unsyncByteArrayOutputStream.unsafeGetByteArray(), 0,
			unsyncByteArrayOutputStream.size());
	}

	private List<BundleWire> _getBundleWires(String packageName) {
		List<BundleWire> bundleWires = new ArrayList<>();

		for (Bundle bundle : _bundleContext.getBundles()) {
			BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

			if (bundleWiring == null) {
				continue;
			}

			for (BundleWire bundleWire :
					bundleWiring.getRequiredWires(
						PackageNamespace.PACKAGE_NAMESPACE)) {

				BundleCapability bundleCapability = bundleWire.getCapability();

				Map<String, Object> attributes =
					bundleCapability.getAttributes();

				if (!packageName.equals(
						attributes.get(PackageNamespace.PACKAGE_NAMESPACE))) {

					continue;
				}

				bundleWires.add(bundleWire);
			}
		}

		return bundleWires;
	}

	private void _writeClass(String dirName, JarOutputStream jarOutputStream)
		throws Exception {

		String className = TestConfiguration.class.getName();

		String path = StringUtil.replace(
			className, CharPool.PERIOD, CharPool.SLASH);

		String name = path + ".class";

		String entryName = dirName + name;

		int index = entryName.indexOf(CharPool.SLASH);

		while (index != -1) {
			jarOutputStream.putNextEntry(
				new JarEntry(entryName.substring(0, index + 1)));

			jarOutputStream.closeEntry();

			index = entryName.indexOf(CharPool.SLASH, index + 1);
		}

		jarOutputStream.putNextEntry(new JarEntry(entryName));

		URL url = _bundle.getEntry(name);

		try (InputStream inputStream = url.openStream()) {
			inputStream.transferTo(jarOutputStream);
		}

		jarOutputStream.closeEntry();
	}

	private Bundle _bundle;
	private BundleContext _bundleContext;
	private Class<?> _configurationVisibilityUtilClass;

}
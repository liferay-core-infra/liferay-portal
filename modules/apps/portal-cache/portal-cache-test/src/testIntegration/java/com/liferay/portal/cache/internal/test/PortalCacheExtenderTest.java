/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.lang.management.ManagementFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Kyle Miho
 */
@RunWith(Arquillian.class)
public class PortalCacheExtenderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpdateConfig() throws Exception {
		CacheConfig multiVMCacheConfig = new CacheConfig(
			1001, _TEST_CACHE_MULTI, 51L);
		CacheConfig singleVMCacheConfig = new CacheConfig(
			1001, _TEST_CACHE_SINGLE, 51L);

		Bundle bundle = _installBundle(
			_BUNDLE_SYMBOLIC_NAME, _generateXMLContent(multiVMCacheConfig),
			_generateXMLContent(singleVMCacheConfig));

		_assertCacheConfig(
			PortalCacheManagerNames.MULTI_VM, multiVMCacheConfig);
		_assertCacheConfig(
			PortalCacheManagerNames.SINGLE_VM, singleVMCacheConfig);

		try {
			_updateAndAssertConfig(
				new CacheConfig[] {
					new CacheConfig(2001, _TEST_CACHE_MULTI, 101L)
				},
				new CacheConfig[] {
					new CacheConfig(2001, _TEST_CACHE_SINGLE, 101L)
				});
		}
		finally {
			if (bundle.getState() != Bundle.UNINSTALLED) {
				bundle.uninstall();
			}
		}
	}

	@Test
	public void testUpdateConfigMultipleRelatedCaches() throws Exception {
		List<CacheConfig> cacheConfigs = new ArrayList<>();

		for (String cacheName :
				(String[])ReflectionTestUtil.invoke(
					(Object)ReflectionTestUtil.getFieldValue(
						_portalCacheManager, "_cacheManager"),
					"getCacheNames", new Class<?>[0])) {

			cacheConfigs.add(new CacheConfig(1000, cacheName, 200L));
		}

		_updateAndAssertConfig(
			cacheConfigs.toArray(new CacheConfig[0]), new CacheConfig[0]);
	}

	private void _assertCacheConfig(
			String cacheManagerName, CacheConfig cacheConfig)
		throws Exception {

		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

		ObjectName objectName = new ObjectName(
			StringBundler.concat(
				"net.sf.ehcache:type=CacheConfiguration,CacheManager=",
				cacheManagerName, ",name=", cacheConfig._name));

		Assert.assertEquals(
			cacheConfig._maxElementsInMemory,
			mBeanServer.getAttribute(objectName, "MaxElementsInMemory"));
		Assert.assertEquals(
			cacheConfig._name, mBeanServer.getAttribute(objectName, "Name"));
		Assert.assertEquals(
			cacheConfig._timeToIdleSeconds,
			mBeanServer.getAttribute(objectName, "TimeToIdleSeconds"));
	}

	private InputStream _createBundle(
			String bundleSymbolicName, String multiCacheConfigContent,
			String singleCacheConfigContent)
		throws Exception {

		try (UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream()) {

			try (JarOutputStream jarOutputStream = new JarOutputStream(
					unsyncByteArrayOutputStream)) {

				_writeManifest(bundleSymbolicName, "1.0.0", jarOutputStream);

				_writeClass(jarOutputStream);

				if (multiCacheConfigContent != null) {
					_writeResource(
						jarOutputStream, multiCacheConfigContent,
						"META-INF/module-multi-vm.xml");
				}

				if (singleCacheConfigContent != null) {
					_writeResource(
						jarOutputStream, singleCacheConfigContent,
						"META-INF/module-single-vm.xml");
				}
			}

			return new UnsyncByteArrayInputStream(
				unsyncByteArrayOutputStream.unsafeGetByteArray(), 0,
				unsyncByteArrayOutputStream.size());
		}
	}

	private String _generateXMLContent(CacheConfig... cacheConfigs) {
		StringBundler sb = new StringBundler(5 + (cacheConfigs.length * 7));

		sb.append("<ehcache dynamicConfig=\"true\" monitoring=\"off\" ");
		sb.append("updateCheck=\"false\" xmlns:xsi=\"http://www.w3.org/2001");
		sb.append("/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"");
		sb.append("http://www.ehcache.org/ehcache.xsd\">");

		for (CacheConfig cacheConfig : cacheConfigs) {
			sb.append("<cache maxElementsInMemory=\"");
			sb.append(cacheConfig._maxElementsInMemory);
			sb.append("\" name=\"");
			sb.append(cacheConfig._name);
			sb.append("\" timeToIdleSeconds=\"");
			sb.append(cacheConfig._timeToIdleSeconds);
			sb.append("\"> </cache>");
		}

		sb.append("</ehcache>");

		return sb.toString();
	}

	private Bundle _installBundle(
			String bundleSymbolicName, String multiCacheConfigContent,
			String singleCacheConfigContent)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(PortalCacheExtenderTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Bundle newBundle = bundleContext.installBundle(
			bundleSymbolicName,
			_createBundle(
				bundleSymbolicName, multiCacheConfigContent,
				singleCacheConfigContent));

		newBundle.start();

		return newBundle;
	}

	private void _updateAndAssertConfig(
			CacheConfig[] multiVMCacheConfigs,
			CacheConfig[] singleVMCacheConfigs)
		throws Exception {

		String multiVmXMLUpdated = null;
		String singleVmXMLUpdated = null;

		if ((multiVMCacheConfigs != null) && (multiVMCacheConfigs.length > 0)) {
			multiVmXMLUpdated = _generateXMLContent(multiVMCacheConfigs);
		}

		if ((singleVMCacheConfigs != null) &&
			(singleVMCacheConfigs.length > 0)) {

			singleVmXMLUpdated = _generateXMLContent(singleVMCacheConfigs);
		}

		Bundle overridingBundle = null;

		try {
			overridingBundle = _installBundle(
				_BUNDLE_SYMBOLIC_NAME.concat(".updated"), multiVmXMLUpdated,
				singleVmXMLUpdated);

			for (CacheConfig cacheConfig : multiVMCacheConfigs) {
				_assertCacheConfig(
					PortalCacheManagerNames.MULTI_VM, cacheConfig);
			}

			for (CacheConfig cacheConfig : singleVMCacheConfigs) {
				_assertCacheConfig(
					PortalCacheManagerNames.SINGLE_VM, cacheConfig);
			}
		}
		finally {
			if ((overridingBundle != null) &&
				(overridingBundle.getState() != Bundle.UNINSTALLED)) {

				overridingBundle.uninstall();
			}
		}
	}

	private void _writeClass(JarOutputStream jarOutputStream)
		throws IOException {

		String className = PortalCacheExtenderTest.class.getName();

		String path = StringUtil.replace(
			className, CharPool.PERIOD, CharPool.SLASH);

		String resourcePath = path.concat(".class");

		jarOutputStream.putNextEntry(new ZipEntry(resourcePath));

		ClassLoader classLoader =
			PortalCacheExtenderTest.class.getClassLoader();

		StreamUtil.transfer(
			classLoader.getResourceAsStream(resourcePath), jarOutputStream,
			false);

		jarOutputStream.closeEntry();
	}

	private void _writeManifest(
			String bundleSymbolicName, String bundleVersion,
			JarOutputStream jarOutputStream)
		throws IOException {

		Manifest manifest = new Manifest();

		Attributes attributes = manifest.getMainAttributes();

		attributes.putValue(Constants.BUNDLE_MANIFESTVERSION, "2");
		attributes.putValue(Constants.BUNDLE_SYMBOLICNAME, bundleSymbolicName);
		attributes.putValue(Constants.BUNDLE_VERSION, bundleVersion);
		attributes.putValue("Manifest-Version", "1");

		jarOutputStream.putNextEntry(new ZipEntry(JarFile.MANIFEST_NAME));

		manifest.write(jarOutputStream);

		jarOutputStream.closeEntry();
	}

	private void _writeResource(
			JarOutputStream jarOutputStream, String content, String outputPath)
		throws IOException {

		jarOutputStream.putNextEntry(new ZipEntry(outputPath));

		StreamUtil.transfer(
			new UnsyncByteArrayInputStream(content.getBytes()), jarOutputStream,
			false);

		jarOutputStream.closeEntry();
	}

	private static final String _BUNDLE_SYMBOLIC_NAME =
		"com.liferay.portal.cache.internal.test.PortalCacheTestModule";

	private static final String _TEST_CACHE_MULTI = "test.cache.multi";

	private static final String _TEST_CACHE_SINGLE = "test.cache.single";

	@Inject(
		filter = "portal.cache.manager.name=" + PortalCacheManagerNames.MULTI_VM
	)
	private PortalCacheManager<? extends Serializable, ?> _portalCacheManager;

	private static class CacheConfig {

		public CacheConfig(
			int maxElementsInMemory, String name, long timeToIdleSeconds) {

			_maxElementsInMemory = maxElementsInMemory;
			_name = name;
			_timeToIdleSeconds = timeToIdleSeconds;
		}

		private final int _maxElementsInMemory;
		private final String _name;
		private final long _timeToIdleSeconds;

	}

}
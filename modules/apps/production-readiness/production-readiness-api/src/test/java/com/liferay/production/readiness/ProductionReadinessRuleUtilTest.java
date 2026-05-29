/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.File;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;

import java.nio.file.Files;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lily Chi
 */
public class ProductionReadinessRuleUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCheck() throws Exception {
		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class);
			MockedStatic<ServerDetector> serverDetectorMockedStatic =
				Mockito.mockStatic(ServerDetector.class);
			MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class);
			AutoCloseable closeable1 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "DL_FILE_ENTRY_PREVIEW_DOCUMENT_DPI",
					75);
			AutoCloseable closeable2 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class,
					"DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED", true);
			AutoCloseable closeable3 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "DL_STORE_IMPL",
					"com.liferay.portal.store.s3.S3Store");
			AutoCloseable closeable4 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LOCALES", new String[] {"en_US"});
			AutoCloseable closeable5 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LOCALES_BETA", new String[0]);
			AutoCloseable closeable6 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LOCALES_ENABLED",
					new String[] {"en_US"});
			AutoCloseable closeable7 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LIFERAY_HOME",
					temporaryFolder.getRoot(
					).getAbsolutePath())) {

			_stubPropsUtilDefaults(propsUtilMockedStatic);

			serverDetectorMockedStatic.when(
				ServerDetector::isTomcat
			).thenReturn(
				false
			);

			MemoryMXBean memoryMXBean = Mockito.mock(MemoryMXBean.class);

			Mockito.when(
				memoryMXBean.getHeapMemoryUsage()
			).thenReturn(
				_memoryUsage(2L * 1024 * 1024 * 1024, 2L * 1024 * 1024 * 1024)
			);

			managementFactoryMockedStatic.when(
				ManagementFactory::getMemoryMXBean
			).thenReturn(
				memoryMXBean
			);

			RuntimeMXBean runtimeMXBean = Mockito.mock(RuntimeMXBean.class);

			Mockito.when(
				runtimeMXBean.getInputArguments()
			).thenReturn(
				Collections.emptyList()
			);

			managementFactoryMockedStatic.when(
				ManagementFactory::getRuntimeMXBean
			).thenReturn(
				runtimeMXBean
			);

			GarbageCollectorMXBean garbageCollectorMXBean = Mockito.mock(
				GarbageCollectorMXBean.class);

			Mockito.when(
				garbageCollectorMXBean.getName()
			).thenReturn(
				"G1 Young Generation"
			);

			managementFactoryMockedStatic.when(
				ManagementFactory::getGarbageCollectorMXBeans
			).thenReturn(
				Collections.singletonList(garbageCollectorMXBean)
			);

			_stubMBeanServerMaxThreads(managementFactoryMockedStatic, 200);

			Collection<Result> results = ProductionReadinessRuleUtil.check();

			Assert.assertFalse(results.isEmpty());

			for (Result result : results) {
				if (result == null) {
					continue;
				}

				Assert.assertNotNull(result.getKey());
				Assert.assertNotNull(result.getStatus());
				Assert.assertNotNull(result.getCategory());
				Assert.assertNotNull(result.getSeverity());
			}
		}
	}

	@Test
	public void testCheckCounterIncrementFail() {
		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.get("counter.increment")
			).thenReturn(
				"1000"
			);

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class, "_checkCounterIncrement",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals("counter-increment", result.getKey());
			Assert.assertEquals("1000", result.getCurrentValue());
		}
	}

	@Test
	public void testCheckCounterIncrementPass() {
		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.get("counter.increment")
			).thenReturn(
				"2000"
			);

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class, "_checkCounterIncrement",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(Result.Status.PASS, result.getStatus());
			Assert.assertEquals("counter-increment", result.getKey());
		}
	}

	@Test
	public void testCheckDatabaseConfigurationFail() throws Exception {
		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class);
			MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.get("jdbc.default.maximumPoolSize")
			).thenReturn(
				"10"
			);

			_stubMBeanServerMaxThreads(managementFactoryMockedStatic, 200);

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class,
				"_checkDatabaseConfiguration", new Class<?>[] {String.class},
				"category");

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals("pool-vs-thread-ratio", result.getKey());
		}
	}

	@Test
	public void testCheckDatabaseConfigurationNull() throws Exception {
		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class);
			MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.get("jdbc.default.maximumPoolSize")
			).thenReturn(
				"0"
			);

			_stubMBeanServerMaxThreads(managementFactoryMockedStatic, 200);

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class,
				"_checkDatabaseConfiguration", new Class<?>[] {String.class},
				"category");

			Assert.assertNull(result);
		}
	}

	@Test
	public void testCheckDatabaseConfigurationPass() throws Exception {
		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class);
			MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.get("jdbc.default.maximumPoolSize")
			).thenReturn(
				"60"
			);

			_stubMBeanServerMaxThreads(managementFactoryMockedStatic, 200);

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class,
				"_checkDatabaseConfiguration", new Class<?>[] {String.class},
				"category");

			Assert.assertEquals(Result.Status.PASS, result.getStatus());
			Assert.assertEquals("pool-vs-thread-ratio", result.getKey());
		}
	}

	@Test
	public void testCheckDLImagePreviewDPIFail() throws Exception {
		try (AutoCloseable closeable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "DL_FILE_ENTRY_PREVIEW_DOCUMENT_DPI",
					100)) {

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class, "_checkDLImagePreviewDPI",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals("dl-image-preview-dpi", result.getKey());
			Assert.assertEquals("100", result.getCurrentValue());
		}
	}

	@Test
	public void testCheckDLImagePreviewDPIPass() throws Exception {
		try (AutoCloseable closeable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "DL_FILE_ENTRY_PREVIEW_DOCUMENT_DPI",
					75)) {

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class, "_checkDLImagePreviewDPI",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(Result.Status.PASS, result.getStatus());
			Assert.assertEquals("dl-image-preview-dpi", result.getKey());
		}
	}

	@Test
	public void testCheckDLPreviewForkingFail() throws Exception {
		try (AutoCloseable closeable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class,
					"DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED", false)) {

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class, "_checkDLPreviewForking",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals("dl-preview-forking", result.getKey());
		}
	}

	@Test
	public void testCheckDLPreviewForkingPass() throws Exception {
		try (AutoCloseable closeable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class,
					"DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED", true)) {

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class, "_checkDLPreviewForking",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(Result.Status.PASS, result.getStatus());
			Assert.assertEquals("dl-preview-forking", result.getKey());
		}
	}

	@Test
	public void testCheckExplicitGCDisabledFail() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			_stubRuntimeInputArguments(
				managementFactoryMockedStatic, Collections.emptyList());

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class, "_checkExplicitGCDisabled",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals("explicit-gc-disabled", result.getKey());
			Assert.assertEquals(
				"-XX:+DisableExplicitGC", result.getRecommendedValue());
		}
	}

	@Test
	public void testCheckExplicitGCDisabledPass() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			_stubRuntimeInputArguments(
				managementFactoryMockedStatic,
				Collections.singletonList("-XX:+DisableExplicitGC"));

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class, "_checkExplicitGCDisabled",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(Result.Status.PASS, result.getStatus());
			Assert.assertEquals("explicit-gc-disabled", result.getKey());
		}
	}

	@Test
	public void testCheckFileStoreImplementationFail() throws Exception {
		try (AutoCloseable closeable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "DL_STORE_IMPL",
					"com.liferay.portal.store.db.DBStore")) {

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class,
				"_checkFileStoreImplementation", new Class<?>[] {String.class},
				"category");

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals("file-store-implementation", result.getKey());
		}
	}

	@Test
	public void testCheckFileStoreImplementationPass() throws Exception {
		String[] validImpls = {
			"com.liferay.portal.store.file.system.AdvancedFileSystemStore",
			"com.liferay.portal.store.s3.S3Store",
			"com.liferay.portal.store.s3.IBMS3Store",
			"com.liferay.portal.store.gcs.GCSStore",
			"com.liferay.portal.store.azure.AzureStore"
		};

		for (String impl : validImpls) {
			try (AutoCloseable closeable =
					ReflectionTestUtil.setFieldValueWithAutoCloseable(
						PropsValues.class, "DL_STORE_IMPL", impl)) {

				Result result = ReflectionTestUtil.invoke(
					ProductionReadinessRuleUtil.class,
					"_checkFileStoreImplementation",
					new Class<?>[] {String.class}, "category");

				Assert.assertEquals(
					"Expected PASS for " + impl, Result.Status.PASS,
					result.getStatus());
				Assert.assertEquals(impl, result.getCurrentValue());
			}
		}
	}

	@Test
	public void testCheckGarbageCollectorTypeFail() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			GarbageCollectorMXBean garbageCollectorMXBean = Mockito.mock(
				GarbageCollectorMXBean.class);

			Mockito.when(
				garbageCollectorMXBean.getName()
			).thenReturn(
				"PS Scavenge"
			);

			managementFactoryMockedStatic.when(
				ManagementFactory::getGarbageCollectorMXBeans
			).thenReturn(
				Collections.singletonList(garbageCollectorMXBean)
			);

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class, "_checkGarbageCollectorType",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals("garbage-collector-type", result.getKey());
		}
	}

	@Test
	public void testCheckGarbageCollectorTypePass() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			GarbageCollectorMXBean garbageCollectorMXBean = Mockito.mock(
				GarbageCollectorMXBean.class);

			Mockito.when(
				garbageCollectorMXBean.getName()
			).thenReturn(
				"G1 Young Generation"
			);

			managementFactoryMockedStatic.when(
				ManagementFactory::getGarbageCollectorMXBeans
			).thenReturn(
				Collections.singletonList(garbageCollectorMXBean)
			);

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class, "_checkGarbageCollectorType",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(Result.Status.PASS, result.getStatus());
			Assert.assertEquals("garbage-collector-type", result.getKey());
		}
	}

	@Test
	public void testCheckHeapAllocationConsistencyFail() {
		MemoryUsage memoryUsage = _memoryUsage(
			2L * 1024 * 1024 * 1024, 4L * 1024 * 1024 * 1024);

		Result result = ReflectionTestUtil.invoke(
			ProductionReadinessRuleUtil.class,
			"_checkHeapAllocationConsistency",
			new Class<?>[] {String.class, MemoryUsage.class}, "category",
			memoryUsage);

		Assert.assertEquals(Result.Status.FAIL, result.getStatus());
		Assert.assertEquals("heap-allocation-consistency", result.getKey());
	}

	@Test
	public void testCheckHeapAllocationConsistencyPass() {
		MemoryUsage memoryUsage = _memoryUsage(
			4L * 1024 * 1024 * 1024, 4L * 1024 * 1024 * 1024);

		Result result = ReflectionTestUtil.invoke(
			ProductionReadinessRuleUtil.class,
			"_checkHeapAllocationConsistency",
			new Class<?>[] {String.class, MemoryUsage.class}, "category",
			memoryUsage);

		Assert.assertEquals(Result.Status.PASS, result.getStatus());
		Assert.assertEquals("heap-allocation-consistency", result.getKey());
	}

	@Test
	public void testCheckHeapSizeUpperLimitFail() {
		MemoryUsage memoryUsage = _memoryUsage(
			64L * 1024 * 1024 * 1024, 64L * 1024 * 1024 * 1024);

		Result result = ReflectionTestUtil.invoke(
			ProductionReadinessRuleUtil.class, "_checkHeapSizeUpperLimit",
			new Class<?>[] {String.class, MemoryUsage.class}, "category",
			memoryUsage);

		Assert.assertEquals(Result.Status.FAIL, result.getStatus());
		Assert.assertEquals("heap-size-upper-limit", result.getKey());
	}

	@Test
	public void testCheckHeapSizeUpperLimitPass() {
		MemoryUsage memoryUsage = _memoryUsage(
			16L * 1024 * 1024 * 1024, 16L * 1024 * 1024 * 1024);

		Result result = ReflectionTestUtil.invoke(
			ProductionReadinessRuleUtil.class, "_checkHeapSizeUpperLimit",
			new Class<?>[] {String.class, MemoryUsage.class}, "category",
			memoryUsage);

		Assert.assertEquals(Result.Status.PASS, result.getStatus());
		Assert.assertEquals("heap-size-upper-limit", result.getKey());
	}

	@Test
	public void testCheckHugePagesConfigurationConfiguredPass() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class);
			MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(
				FileUtil.class)) {

			_stubRuntimeInputArguments(
				managementFactoryMockedStatic,
				Arrays.asList(
					"-XX:+UseLargePages", "-XX:LargePageSizeInBytes=2048k"));

			fileUtilMockedStatic.when(
				() -> FileUtil.read(ArgumentMatchers.any(File.class))
			).thenReturn(
				"Hugepagesize:    2048 kB\n"
			);

			MemoryUsage memoryUsage = _memoryUsage(
				8L * 1024 * 1024 * 1024, 8L * 1024 * 1024 * 1024);

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class,
				"_checkHugePagesConfiguration",
				new Class<?>[] {String.class, MemoryUsage.class}, "category",
				memoryUsage);

			Assert.assertEquals(Result.Status.PASS, result.getStatus());
			Assert.assertEquals("huge-pages-configuration", result.getKey());
		}
	}

	@Test
	public void testCheckHugePagesConfigurationMissingSizeFail() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			_stubRuntimeInputArguments(
				managementFactoryMockedStatic,
				Collections.singletonList("-XX:+UseLargePages"));

			MemoryUsage memoryUsage = _memoryUsage(
				8L * 1024 * 1024 * 1024, 8L * 1024 * 1024 * 1024);

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class,
				"_checkHugePagesConfiguration",
				new Class<?>[] {String.class, MemoryUsage.class}, "category",
				memoryUsage);

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals("huge-pages-configuration", result.getKey());
		}
	}

	@Test
	public void testCheckHugePagesConfigurationNoLargePagesFail() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			_stubRuntimeInputArguments(
				managementFactoryMockedStatic, Collections.emptyList());

			MemoryUsage memoryUsage = _memoryUsage(
				8L * 1024 * 1024 * 1024, 8L * 1024 * 1024 * 1024);

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class,
				"_checkHugePagesConfiguration",
				new Class<?>[] {String.class, MemoryUsage.class}, "category",
				memoryUsage);

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals("huge-pages-configuration", result.getKey());
			Assert.assertEquals(
				"-XX:+UseLargePages", result.getRecommendedValue());
		}
	}

	@Test
	public void testCheckHugePagesConfigurationSmallHeapPass() {
		MemoryUsage memoryUsage = _memoryUsage(
			2L * 1024 * 1024 * 1024, 2L * 1024 * 1024 * 1024);

		Result result = ReflectionTestUtil.invoke(
			ProductionReadinessRuleUtil.class, "_checkHugePagesConfiguration",
			new Class<?>[] {String.class, MemoryUsage.class}, "category",
			memoryUsage);

		Assert.assertEquals(Result.Status.PASS, result.getStatus());
		Assert.assertEquals("huge-pages-configuration", result.getKey());
	}

	@Test
	public void testCheckJMXConfigurationDisabledFail() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			_stubRuntimeInputArguments(
				managementFactoryMockedStatic,
				Collections.singletonList(
					"-Dcom.sun.management.jmxremote.port=9000"));

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class,
				"_checkJMXConfigurationDisabled", new Class<?>[] {String.class},
				"category");

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals("jmx-configuration-disabled", result.getKey());
			Assert.assertTrue(
				result.getCurrentValue(),
				result.getCurrentValue(
				).contains(
					"-Dcom.sun.management.jmxremote.port=9000"
				));
		}
	}

	@Test
	public void testCheckJMXConfigurationDisabledPass() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			_stubRuntimeInputArguments(
				managementFactoryMockedStatic, Collections.emptyList());

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class,
				"_checkJMXConfigurationDisabled", new Class<?>[] {String.class},
				"category");

			Assert.assertEquals(Result.Status.PASS, result.getStatus());
			Assert.assertEquals("jmx-configuration-disabled", result.getKey());
		}
	}

	@Test
	public void testCheckJSPEngineSettingsFail() throws Exception {
		File catalinaBase = temporaryFolder.newFolder("catalina");

		File confDir = new File(catalinaBase, "conf");

		confDir.mkdirs();

		File webXml = new File(confDir, "web.xml");

		Files.writeString(webXml.toPath(), "<web-app/>");

		String originalCatalinaBase = System.getProperty("catalina.base");
		String originalCatalinaHome = System.getProperty("catalina.home");

		System.setProperty("catalina.base", catalinaBase.getAbsolutePath());

		try (MockedStatic<ServerDetector> serverDetectorMockedStatic =
				Mockito.mockStatic(ServerDetector.class);
			MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(
				FileUtil.class);
			MockedStatic<SAXReaderUtil> saxReaderUtilMockedStatic =
				Mockito.mockStatic(SAXReaderUtil.class)) {

			serverDetectorMockedStatic.when(
				ServerDetector::isTomcat
			).thenReturn(
				true
			);

			fileUtilMockedStatic.when(
				() -> FileUtil.read(ArgumentMatchers.any(File.class))
			).thenReturn(
				"<web-app/>"
			);

			Document document = _mockJspWebXmlDocument("true", "false");

			saxReaderUtilMockedStatic.when(
				() -> SAXReaderUtil.read(ArgumentMatchers.anyString())
			).thenReturn(
				document
			);

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class, "_checkJSPEngineSettings",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals("jsp-engine-settings", result.getKey());
		}
		finally {
			_restoreSystemProperty("catalina.base", originalCatalinaBase);
			_restoreSystemProperty("catalina.home", originalCatalinaHome);
		}
	}

	@Test
	public void testCheckJSPEngineSettingsNoCatalinaBase() {
		String originalCatalinaBase = System.getProperty("catalina.base");
		String originalCatalinaHome = System.getProperty("catalina.home");

		System.clearProperty("catalina.base");
		System.clearProperty("catalina.home");

		try (MockedStatic<ServerDetector> serverDetectorMockedStatic =
				Mockito.mockStatic(ServerDetector.class)) {

			serverDetectorMockedStatic.when(
				ServerDetector::isTomcat
			).thenReturn(
				true
			);

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class, "_checkJSPEngineSettings",
				new Class<?>[] {String.class}, "category");

			Assert.assertNull(result);
		}
		finally {
			_restoreSystemProperty("catalina.base", originalCatalinaBase);
			_restoreSystemProperty("catalina.home", originalCatalinaHome);
		}
	}

	@Test
	public void testCheckJSPEngineSettingsNotTomcat() {
		try (MockedStatic<ServerDetector> serverDetectorMockedStatic =
				Mockito.mockStatic(ServerDetector.class)) {

			serverDetectorMockedStatic.when(
				ServerDetector::isTomcat
			).thenReturn(
				false
			);

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class, "_checkJSPEngineSettings",
				new Class<?>[] {String.class}, "category");

			Assert.assertNull(result);
		}
	}

	@Test
	public void testCheckJSPEngineSettingsPass() throws Exception {
		File catalinaBase = temporaryFolder.newFolder("catalina");

		File confDir = new File(catalinaBase, "conf");

		confDir.mkdirs();

		File webXml = new File(confDir, "web.xml");

		Files.writeString(webXml.toPath(), "<web-app/>");

		String originalCatalinaBase = System.getProperty("catalina.base");
		String originalCatalinaHome = System.getProperty("catalina.home");

		System.setProperty("catalina.base", catalinaBase.getAbsolutePath());

		try (MockedStatic<ServerDetector> serverDetectorMockedStatic =
				Mockito.mockStatic(ServerDetector.class);
			MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(
				FileUtil.class);
			MockedStatic<SAXReaderUtil> saxReaderUtilMockedStatic =
				Mockito.mockStatic(SAXReaderUtil.class)) {

			serverDetectorMockedStatic.when(
				ServerDetector::isTomcat
			).thenReturn(
				true
			);

			fileUtilMockedStatic.when(
				() -> FileUtil.read(ArgumentMatchers.any(File.class))
			).thenReturn(
				"<web-app/>"
			);

			Document document = _mockJspWebXmlDocument("false", "false");

			saxReaderUtilMockedStatic.when(
				() -> SAXReaderUtil.read(ArgumentMatchers.anyString())
			).thenReturn(
				document
			);

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class, "_checkJSPEngineSettings",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(Result.Status.PASS, result.getStatus());
			Assert.assertEquals("jsp-engine-settings", result.getKey());
		}
		finally {
			_restoreSystemProperty("catalina.base", originalCatalinaBase);
			_restoreSystemProperty("catalina.home", originalCatalinaHome);
		}
	}

	@Test
	public void testCheckJSPReloadingFail() {
		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.get("direct.servlet.context.reload")
			).thenReturn(
				"true"
			);

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class, "_checkJSPReloading",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals("jsp-reloading", result.getKey());
			Assert.assertEquals(Result.Severity.MEDIUM, result.getSeverity());
		}
	}

	@Test
	public void testCheckJSPReloadingPass() {
		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.get("direct.servlet.context.reload")
			).thenReturn(
				"false"
			);

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class, "_checkJSPReloading",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(Result.Status.PASS, result.getStatus());
			Assert.assertEquals("jsp-reloading", result.getKey());
		}
	}

	@Test
	public void testCheckLanguagesBetaFail() throws Exception {
		try (AutoCloseable closeable1 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LOCALES",
					new String[] {"en_US", "fr_FR"});
			AutoCloseable closeable2 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LOCALES_BETA", new String[] {"fr_FR"});
			AutoCloseable closeable3 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LOCALES_ENABLED",
					new String[] {"en_US", "fr_FR"})) {

			Collection<Result> results = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class, "_checkLanguages",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(results.toString(), 1, results.size());

			Result result = results.iterator(
			).next();

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals("languages", result.getKey());
		}
	}

	@Test
	public void testCheckLanguagesPass() throws Exception {
		try (AutoCloseable closeable1 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LOCALES", new String[] {"en_US"});
			AutoCloseable closeable2 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LOCALES_BETA", new String[0]);
			AutoCloseable closeable3 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LOCALES_ENABLED",
					new String[] {"en_US"})) {

			Collection<Result> results = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class, "_checkLanguages",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(results.toString(), 1, results.size());

			Result result = results.iterator(
			).next();

			Assert.assertEquals(Result.Status.PASS, result.getStatus());
			Assert.assertEquals("languages", result.getKey());
		}
	}

	@Test
	public void testCheckLanguagesUnusedFail() throws Exception {
		try (AutoCloseable closeable1 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LOCALES",
					new String[] {"en_US", "de_DE"});
			AutoCloseable closeable2 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LOCALES_BETA", new String[0]);
			AutoCloseable closeable3 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LOCALES_ENABLED",
					new String[] {"en_US"})) {

			Collection<Result> results = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class, "_checkLanguages",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(results.toString(), 1, results.size());

			Result result = results.iterator(
			).next();

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals("languages", result.getKey());
			Assert.assertEquals("de_DE", result.getCurrentValue());
		}
	}

	@Test
	public void testCheckPasswordEncryptionBCRYPTPass() {
		_assertPasswordEncryption("BCRYPT", Result.Status.PASS);
	}

	@Test
	public void testCheckPasswordEncryptionMD5Fail() {
		_assertPasswordEncryption("MD5", Result.Status.FAIL);
	}

	@Test
	public void testCheckPasswordEncryptionPBKDF2StrongPass() {
		_assertPasswordEncryption(
			"PBKDF2WithHmacSHA1/160/1300000", Result.Status.PASS);
	}

	@Test
	public void testCheckPasswordEncryptionPBKDF2WeakFail() {
		Result result = _assertPasswordEncryption(
			"PBKDF2WithHmacSHA1/160/1000", Result.Status.FAIL);

		Assert.assertEquals(Result.Severity.HIGH, result.getSeverity());
	}

	@Test
	public void testCheckPortalDeveloperPropertiesFail() {
		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.getArray("include-and-override")
			).thenReturn(
				new String[] {"portal-developer.properties"}
			);

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class,
				"_checkPortalDeveloperProperties",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals("portal-developer-properties", result.getKey());
			Assert.assertEquals(Result.Severity.MEDIUM, result.getSeverity());
		}
	}

	@Test
	public void testCheckPortalDeveloperPropertiesPass() {
		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.getArray("include-and-override")
			).thenReturn(
				new String[0]
			);

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class,
				"_checkPortalDeveloperProperties",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(Result.Status.PASS, result.getStatus());
			Assert.assertEquals("portal-developer-properties", result.getKey());
		}
	}

	@Test
	public void testCheckPreventDiagnosticOverheadFail() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			_stubRuntimeInputArguments(
				managementFactoryMockedStatic,
				Collections.singletonList("-XX:+UnlockDiagnosticVMOptions"));

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class,
				"_checkPreventDiagnosticOverhead",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals("prevent-diagnostic-overhead", result.getKey());
		}
	}

	@Test
	public void testCheckPreventDiagnosticOverheadPass() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			_stubRuntimeInputArguments(
				managementFactoryMockedStatic, Collections.emptyList());

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class,
				"_checkPreventDiagnosticOverhead",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(Result.Status.PASS, result.getStatus());
			Assert.assertEquals("prevent-diagnostic-overhead", result.getKey());
		}
	}

	@Test
	public void testCheckSidecarDetectionMissingFileFail() throws Exception {
		File liferayHome = temporaryFolder.newFolder("liferayHomeMissing");

		try (AutoCloseable closeable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LIFERAY_HOME",
					liferayHome.getAbsolutePath())) {

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class, "_checkSidecarDetection",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals("sidecar-detection", result.getKey());
			Assert.assertEquals(Result.Severity.HIGH, result.getSeverity());
		}
	}

	@Test
	public void testCheckSidecarDetectionPass() throws Exception {
		File liferayHome = temporaryFolder.newFolder("liferayHomePass");

		_writeSidecarConfig(liferayHome, "productionModeEnabled=B\"true\"\n");

		try (AutoCloseable closeable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LIFERAY_HOME",
					liferayHome.getAbsolutePath())) {

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class, "_checkSidecarDetection",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(Result.Status.PASS, result.getStatus());
			Assert.assertEquals("sidecar-detection", result.getKey());
		}
	}

	@Test
	public void testCheckSidecarDetectionProductionModeDisabledFail()
		throws Exception {

		File liferayHome = temporaryFolder.newFolder("liferayHomeDisabled");

		_writeSidecarConfig(liferayHome, "productionModeEnabled=B\"false\"\n");

		try (AutoCloseable closeable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LIFERAY_HOME",
					liferayHome.getAbsolutePath())) {

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class, "_checkSidecarDetection",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals("sidecar-detection", result.getKey());
		}
	}

	@Test
	public void testIsStrongerThanPBKDF2BCRYPT() {
		Assert.assertTrue(_invokeIsStrongerThanPBKDF2("BCRYPT"));
	}

	@Test
	public void testIsStrongerThanPBKDF2BCRYPTVariant() {
		Assert.assertTrue(_invokeIsStrongerThanPBKDF2("BCRYPT/12"));
	}

	@Test
	public void testIsStrongerThanPBKDF2MD5() {
		Assert.assertFalse(_invokeIsStrongerThanPBKDF2("MD5"));
	}

	@Test
	public void testIsStrongerThanPBKDF2Null() {
		Assert.assertFalse(_invokeIsStrongerThanPBKDF2(null));
	}

	@Test
	public void testIsStrongerThanPBKDF2SCRYPT() {
		Assert.assertTrue(_invokeIsStrongerThanPBKDF2("SCRYPT"));
	}

	@Test
	public void testIsStrongerThanPBKDF2Strong() {
		Assert.assertTrue(
			_invokeIsStrongerThanPBKDF2("PBKDF2WithHmacSHA1/160/1300000"));
	}

	@Test
	public void testIsStrongerThanPBKDF2Weak() {
		Assert.assertFalse(
			_invokeIsStrongerThanPBKDF2("PBKDF2WithHmacSHA1/160/1000"));
	}

	@Test
	public void testParseSizeGigabytes() {
		Assert.assertEquals(1073741824L, _invokeParseSize("1g"));
	}

	@Test
	public void testParseSizeKilobytes() {
		Assert.assertEquals(2097152L, _invokeParseSize("2048k"));
	}

	@Test
	public void testParseSizeMegabytes() {
		Assert.assertEquals(2097152L, _invokeParseSize("2m"));
	}

	@Test
	public void testParseSizeNull() {
		Assert.assertEquals(-1L, _invokeParseSize(null));
	}

	@Test
	public void testParseSizePlainNumber() {
		Assert.assertEquals(100L, _invokeParseSize("100"));
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private Result _assertPasswordEncryption(
		String algorithm, Result.Status expectedStatus) {

		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.get("passwords.encryption.algorithm")
			).thenReturn(
				algorithm
			);

			Result result = ReflectionTestUtil.invoke(
				ProductionReadinessRuleUtil.class, "_checkPasswordEncryption",
				new Class<?>[] {String.class}, "category");

			Assert.assertEquals(expectedStatus, result.getStatus());
			Assert.assertEquals("password-encryption", result.getKey());

			return result;
		}
	}

	private boolean _invokeIsStrongerThanPBKDF2(String algorithm) {
		return ReflectionTestUtil.invoke(
			ProductionReadinessRuleUtil.class, "_isStrongerThanPBKDF2",
			new Class<?>[] {String.class}, algorithm);
	}

	private long _invokeParseSize(String sizeStr) {
		return ReflectionTestUtil.invoke(
			ProductionReadinessRuleUtil.class, "_parseSize",
			new Class<?>[] {String.class}, sizeStr);
	}

	private MemoryUsage _memoryUsage(long init, long max) {
		return new MemoryUsage(init, 0, max, max);
	}

	private Document _mockJspWebXmlDocument(
		String developmentValue, String mappedFileValue) {

		Document document = Mockito.mock(Document.class);
		Element rootElement = Mockito.mock(Element.class);
		Element servletElement = Mockito.mock(Element.class);
		Element developmentParam = Mockito.mock(Element.class);
		Element mappedFileParam = Mockito.mock(Element.class);

		Mockito.when(
			developmentParam.elementText("param-name")
		).thenReturn(
			"development"
		);

		Mockito.when(
			developmentParam.elementText("param-value")
		).thenReturn(
			developmentValue
		);

		Mockito.when(
			mappedFileParam.elementText("param-name")
		).thenReturn(
			"mappedFile"
		);

		Mockito.when(
			mappedFileParam.elementText("param-value")
		).thenReturn(
			mappedFileValue
		);

		Mockito.when(
			servletElement.getName()
		).thenReturn(
			"servlet"
		);

		Mockito.when(
			servletElement.elementText("servlet-name")
		).thenReturn(
			"jsp"
		);

		Mockito.when(
			servletElement.elements("init-param")
		).thenReturn(
			Arrays.asList(developmentParam, mappedFileParam)
		);

		Mockito.when(
			rootElement.elements()
		).thenReturn(
			Collections.singletonList(servletElement)
		);

		Mockito.when(
			document.getRootElement()
		).thenReturn(
			rootElement
		);

		return document;
	}

	private void _restoreSystemProperty(String key, String previousValue) {
		if (previousValue == null) {
			System.clearProperty(key);
		}
		else {
			System.setProperty(key, previousValue);
		}
	}

	private void _stubMBeanServerMaxThreads(
			MockedStatic<ManagementFactory> managementFactoryMockedStatic,
			int maxThreads)
		throws Exception {

		MBeanServer mBeanServer = Mockito.mock(MBeanServer.class);

		ObjectName objectName = new ObjectName(
			"Catalina:type=ThreadPool,name=http-nio-8080");

		Mockito.when(
			mBeanServer.queryNames(
				ArgumentMatchers.any(ObjectName.class),
				ArgumentMatchers.isNull())
		).thenReturn(
			Collections.singleton(objectName)
		);

		Mockito.when(
			mBeanServer.getAttribute(
				ArgumentMatchers.any(ObjectName.class),
				ArgumentMatchers.eq("maxThreads"))
		).thenReturn(
			maxThreads
		);

		managementFactoryMockedStatic.when(
			ManagementFactory::getPlatformMBeanServer
		).thenReturn(
			mBeanServer
		);
	}

	private void _stubPropsUtilDefaults(
		MockedStatic<PropsUtil> propsUtilMockedStatic) {

		propsUtilMockedStatic.when(
			() -> PropsUtil.get(ArgumentMatchers.anyString())
		).thenReturn(
			""
		);

		propsUtilMockedStatic.when(
			() -> PropsUtil.getArray(ArgumentMatchers.anyString())
		).thenReturn(
			new String[0]
		);

		propsUtilMockedStatic.when(
			() -> PropsUtil.get("counter.increment")
		).thenReturn(
			"2000"
		);

		propsUtilMockedStatic.when(
			() -> PropsUtil.get("jdbc.default.maximumPoolSize")
		).thenReturn(
			"60"
		);

		propsUtilMockedStatic.when(
			() -> PropsUtil.get("direct.servlet.context.reload")
		).thenReturn(
			"false"
		);

		propsUtilMockedStatic.when(
			() -> PropsUtil.get("passwords.encryption.algorithm")
		).thenReturn(
			"BCRYPT"
		);
	}

	private void _stubRuntimeInputArguments(
		MockedStatic<ManagementFactory> managementFactoryMockedStatic,
		List<String> inputArguments) {

		RuntimeMXBean runtimeMXBean = Mockito.mock(RuntimeMXBean.class);

		Mockito.when(
			runtimeMXBean.getInputArguments()
		).thenReturn(
			inputArguments
		);

		managementFactoryMockedStatic.when(
			ManagementFactory::getRuntimeMXBean
		).thenReturn(
			runtimeMXBean
		);
	}

	private void _writeSidecarConfig(File liferayHome, String content)
		throws Exception {

		File configsDir = new File(liferayHome, "osgi/configs");

		configsDir.mkdirs();

		File configFile = new File(
			configsDir,
			"com.liferay.portal.search.elasticsearch8.configuration." +
				"ElasticsearchConfiguration.config");

		Files.writeString(configFile.toPath(), content);
	}

}
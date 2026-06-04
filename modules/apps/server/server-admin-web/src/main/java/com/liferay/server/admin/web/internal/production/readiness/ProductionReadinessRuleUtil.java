/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.production.readiness;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.io.File;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

/**
 * @author Lily Chi
 */
public class ProductionReadinessRuleUtil {

	public static Collection<ProductionReadinessResult> check() {
		Collection<ProductionReadinessResult> productionReadinessResults =
			new ArrayList<>();

		productionReadinessResults.addAll(
			_checkJVMConfigurations("jvm-and-infrastructure-validation"));

		productionReadinessResults.add(
			_checkDatabaseConfiguration("database-configuration"));

		productionReadinessResults.addAll(
			_checkPortalPropertiesConfigurations(
				"portal-properties-configuration"));

		productionReadinessResults.add(
			_checkSidecarDetection("search-engine-connectivity-validation"));

		return productionReadinessResults;
	}

	private static ProductionReadinessResult _checkCounterIncrement(
		String category) {

		int counterIncrement = GetterUtil.getInteger(
			PropsUtil.get("counter.increment"));

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				category, "counter-increment"
			).currentValue(
				String.valueOf(counterIncrement)
			);

		if (counterIncrement < 2000) {
			return builder.fail();
		}

		return builder.pass();
	}

	private static ProductionReadinessResult _checkDatabaseConfiguration(
		String category) {

		int jdbcMaxPoolSize = GetterUtil.getInteger(
			PropsUtil.get("jdbc.default.maximumPoolSize"));

		int tomcatMaxThreads = _getMaxThreads();

		if ((jdbcMaxPoolSize <= 0) || (tomcatMaxThreads <= 0)) {
			return null;
		}

		double ratio = (double)jdbcMaxPoolSize / tomcatMaxThreads;

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				category, "pool-vs-thread-ratio"
			).currentValue(
				StringBundler.concat(
					"DB Pool Size=", jdbcMaxPoolSize, ", Tomcat Threads=",
					tomcatMaxThreads, " (Ratio=",
					Math.round(ratio * 100) / 100.0, ")")
			);

		if ((ratio >= 0.3) && (ratio <= 0.4)) {
			return builder.pass();
		}

		return builder.fail();
	}

	private static ProductionReadinessResult _checkDLImagePreviewDPI(
		String category) {

		int dpi = PropsValues.DL_FILE_ENTRY_PREVIEW_DOCUMENT_DPI;

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				category, "dl-image-preview-dpi"
			).currentValue(
				String.valueOf(dpi)
			);

		if (dpi > 75) {
			return builder.recommendedValue(
				"75"
			).fail();
		}

		return builder.pass();
	}

	private static ProductionReadinessResult _checkDLPreviewForking(
		String category) {

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				category, "dl-preview-forking"
			).currentValue(
				PropsKeys.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED + "=" +
					PropsValues.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED
			).messageParameters(
				PropsKeys.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED
			).recommendedValue(
				PropsKeys.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED + "=true"
			);

		if (PropsValues.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED) {
			return builder.pass();
		}

		return builder.fail();
	}

	private static ProductionReadinessResult _checkExplicitGCDisabled(
		String category) {

		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

		List<String> inputArguments = runtimeMXBean.getInputArguments();

		boolean disabled = false;

		for (String inputArgument : inputArguments) {
			if (inputArgument.equals("-XX:+DisableExplicitGC")) {
				disabled = true;

				break;
			}
		}

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(category, "explicit-gc-disabled");

		if (disabled) {
			return builder.currentValue(
				"-XX:+DisableExplicitGC"
			).pass();
		}

		return builder.recommendedValue(
			"-XX:+DisableExplicitGC"
		).fail();
	}

	private static ProductionReadinessResult _checkFileStoreImplementation(
		String category) {

		String dlStoreImpl = PropsValues.DL_STORE_IMPL;

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				category, "file-store-implementation"
			).currentValue(
				dlStoreImpl
			);

		if (dlStoreImpl.equals(
				"com.liferay.portal.store.file.system." +
					"AdvancedFileSystemStore") ||
			dlStoreImpl.equals("com.liferay.portal.store.s3.S3Store") ||
			dlStoreImpl.equals("com.liferay.portal.store.s3.IBMS3Store") ||
			dlStoreImpl.equals("com.liferay.portal.store.gcs.GCSStore") ||
			dlStoreImpl.equals("com.liferay.portal.store.azure.AzureStore")) {

			return builder.pass();
		}

		return builder.recommendedValue(
			"AdvancedFileSystemStore or Cloud Store"
		).fail();
	}

	private static ProductionReadinessResult _checkGarbageCollectorType(
		String category) {

		List<GarbageCollectorMXBean> garbageCollectorMXBeans =
			ManagementFactory.getGarbageCollectorMXBeans();

		List<String> gcNames = new ArrayList<>();

		boolean pass = false;

		for (GarbageCollectorMXBean garbageCollectorMXBean :
				garbageCollectorMXBeans) {

			String name = garbageCollectorMXBean.getName();

			gcNames.add(name);

			if (name.contains("G1") || name.contains("Shenandoah") ||
				name.contains("ZGC")) {

				pass = true;
			}
		}

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				category, "garbage-collector-type"
			).currentValue(
				String.join(", ", gcNames)
			).recommendedValue(
				"G1, Shenandoah, or ZGC"
			);

		if (pass) {
			return builder.pass();
		}

		return builder.fail();
	}

	private static ProductionReadinessResult _checkHeapAllocationConsistency(
		String category, MemoryUsage heapUsage) {

		long xmsBytes = heapUsage.getInit();
		long xmxBytes = heapUsage.getMax();

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				category, "heap-allocation-consistency"
			).currentValue(
				StringBundler.concat(
					"Xms=", xmsBytes / 1024 / 1024, "MB, Xmx=",
					xmxBytes / 1024 / 1024, "MB")
			);

		if ((xmsBytes > 0) && (xmsBytes == xmxBytes)) {
			return builder.pass();
		}

		return builder.fail();
	}

	private static ProductionReadinessResult _checkHeapSizeUpperLimit(
		String category, MemoryUsage heapUsage) {

		long xmxBytes = heapUsage.getMax();

		double maxMemoryGB = xmxBytes / (1024.0 * 1024.0 * 1024.0);

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				category, "heap-size-upper-limit"
			).currentValue(
				maxMemoryGB + "GB"
			);

		if (maxMemoryGB <= 32.0) {
			return builder.pass();
		}

		return builder.fail();
	}

	private static ProductionReadinessResult _checkHugePagesConfiguration(
		String category, MemoryUsage heapUsage) {

		long xmxBytes = heapUsage.getMax();

		double maxMemoryGB = xmxBytes / (1024.0 * 1024.0 * 1024.0);

		if (maxMemoryGB <= 4.0) {
			return ProductionReadinessResult.builder(
				category, "huge-pages-configuration"
			).messageKeySuffix(
				"heap-under-4gb"
			).pass();
		}

		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

		List<String> inputArguments = runtimeMXBean.getInputArguments();

		boolean useLargePages = false;
		String largePageSizeArg = null;

		for (String inputArgument : inputArguments) {
			if (inputArgument.equals("-XX:+UseLargePages")) {
				useLargePages = true;
			}
			else if (inputArgument.startsWith(
						_PREFIX_LARGE_PAGE_SIZE_IN_BYTES)) {

				largePageSizeArg = inputArgument.substring(
					_PREFIX_LARGE_PAGE_SIZE_IN_BYTES.length());
			}
		}

		if (!useLargePages) {
			return ProductionReadinessResult.builder(
				category, "huge-pages-configuration"
			).messageKeySuffix(
				"no-large-pages"
			).recommendedValue(
				"-XX:+UseLargePages"
			).severity(
				ProductionReadinessResult.Severity.MEDIUM
			).fail();
		}

		if (largePageSizeArg == null) {
			return ProductionReadinessResult.builder(
				category, "huge-pages-configuration"
			).messageKeySuffix(
				"missing-large-page-size"
			).severity(
				ProductionReadinessResult.Severity.MEDIUM
			).fail();
		}

		long osHugePageSize = _getOSHugePageSize();

		if (osHugePageSize > 0) {
			long configLargePageSize = _parseSize(largePageSizeArg);

			if (configLargePageSize != osHugePageSize) {
				return ProductionReadinessResult.builder(
					category, "huge-pages-configuration"
				).currentValue(
					StringBundler.concat(
						"-XX:LargePageSizeInBytes = ", largePageSizeArg,
						", OS's huge page size = ", osHugePageSize / 1024, "kB")
				).messageKeySuffix(
					"size-mismatch"
				).severity(
					ProductionReadinessResult.Severity.MEDIUM
				).fail();
			}
		}

		return ProductionReadinessResult.builder(
			category, "huge-pages-configuration"
		).messageKeySuffix(
			"configured"
		).pass();
	}

	private static ProductionReadinessResult _checkJMXConfigurationDisabled(
		String category) {

		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

		List<String> inputArguments = runtimeMXBean.getInputArguments();

		boolean jmxEnabled = false;
		String enabledArg = null;

		for (String inputArgument : inputArguments) {
			if (inputArgument.startsWith("-Dcom.sun.management.jmxremote")) {
				jmxEnabled = true;
				enabledArg = inputArgument;

				break;
			}
		}

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				category, "jmx-configuration-disabled");

		if (jmxEnabled) {
			return builder.currentValue(
				"JMX Configuration has been enabled (" + enabledArg + ")"
			).fail();
		}

		return builder.pass();
	}

	private static ProductionReadinessResult _checkJSPEngineSettings(
		String category) {

		if (!ServerDetector.isTomcat()) {
			return null;
		}

		String catalinaBase = System.getProperty("catalina.base");

		if (Validator.isNull(catalinaBase)) {
			catalinaBase = System.getProperty("catalina.home");
		}

		if (Validator.isNull(catalinaBase)) {
			return null;
		}

		File webXmlFile = new File(catalinaBase, "conf/web.xml");

		if (!webXmlFile.exists()) {
			return null;
		}

		try {
			String content = FileUtil.read(webXmlFile);

			Document document = SAXReaderUtil.read(content);

			Element rootElement = document.getRootElement();

			Object development = null;
			Object mappedFile = null;

			List<Element> allElements = rootElement.elements();

			for (Element element : allElements) {
				String elementName = element.getName();

				if (!elementName.equals("servlet")) {
					continue;
				}

				String servletName = element.elementText("servlet-name");

				if (!servletName.equals("jsp")) {
					continue;
				}

				List<Element> initParams = element.elements("init-param");

				for (Element param : initParams) {
					String paramName = param.elementText("param-name");
					String paramValue = param.elementText("param-value");

					if (paramName.equals("development")) {
						development = GetterUtil.getBoolean(paramValue);
					}
					else if (paramName.equals("mappedFile")) {
						mappedFile = GetterUtil.getBoolean(paramValue);
					}
				}
			}

			if (Validator.isNotNull(development) &&
				Validator.isNotNull(mappedFile)) {

				ProductionReadinessResult.Builder builder =
					ProductionReadinessResult.builder(
						category, "jsp-engine-settings"
					).currentValue(
						StringBundler.concat(
							"development=", development, ", mappedfile=",
							mappedFile)
					).recommendedValue(
						"development=false, mappedfile=false"
					);

				if (!(boolean)development && !(boolean)mappedFile) {
					return builder.pass();
				}

				return builder.fail();
			}
			else if (Validator.isNull(development) ||
					 Validator.isNull(mappedFile)) {

				return ProductionReadinessResult.builder(
					category, "jsp-engine-settings"
				).currentValue(
					"development or mappedfile is not set, Tomcat will use " +
						"the default value development=true or mappedfile=true"
				).recommendedValue(
					"development=false, mappedfile=false"
				).fail();
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return null;
	}

	private static ProductionReadinessResult _checkJSPReloading(
		String category) {

		boolean directServletContextReload = GetterUtil.getBoolean(
			PropsUtil.get("direct.servlet.context.reload"));

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				category, "jsp-reloading"
			).currentValue(
				"direct.servlet.context.reload=" + directServletContextReload
			).recommendedValue(
				"direct.servlet.context.reload=false"
			);

		if (directServletContextReload) {
			return builder.severity(
				ProductionReadinessResult.Severity.MEDIUM
			).fail();
		}

		return builder.pass();
	}

	private static Collection<ProductionReadinessResult>
		_checkJVMConfigurations(String category) {

		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

		MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();

		Collection<ProductionReadinessResult> productionReadinessResults =
			new ArrayList<>();

		productionReadinessResults.add(
			_checkHeapAllocationConsistency(category, heapUsage));

		productionReadinessResults.add(
			_checkHeapSizeUpperLimit(category, heapUsage));

		productionReadinessResults.add(
			_checkHugePagesConfiguration(category, heapUsage));

		productionReadinessResults.add(_checkJSPEngineSettings(category));

		productionReadinessResults.add(_checkGarbageCollectorType(category));

		productionReadinessResults.add(_checkExplicitGCDisabled(category));

		productionReadinessResults.add(
			_checkPreventDiagnosticOverhead(category));

		productionReadinessResults.add(
			_checkJMXConfigurationDisabled(category));

		return productionReadinessResults;
	}

	private static Collection<ProductionReadinessResult> _checkLanguages(
		String category) {

		List<String> availableLocales = List.of(PropsValues.LOCALES);
		List<String> betaLocales = List.of(PropsValues.LOCALES_BETA);
		List<String> enabledLocales = List.of(PropsValues.LOCALES_ENABLED);

		List<String> enabledBetaLocales = new ArrayList<>();

		for (String locale : enabledLocales) {
			if (betaLocales.contains(locale)) {
				enabledBetaLocales.add(locale);
			}
		}

		List<String> unusedLocales = new ArrayList<>();

		for (String locale : availableLocales) {
			if (!enabledLocales.contains(locale)) {
				unusedLocales.add(locale);
			}
		}

		if (enabledBetaLocales.isEmpty() && unusedLocales.isEmpty()) {
			return Collections.singletonList(
				ProductionReadinessResult.builder(
					category, "languages"
				).pass());
		}

		List<ProductionReadinessResult> productionReadinessResults =
			new ArrayList<>(2);

		if (!enabledBetaLocales.isEmpty()) {
			productionReadinessResults.add(
				ProductionReadinessResult.builder(
					category, "languages"
				).currentValue(
					StringUtil.merge(enabledBetaLocales)
				).messageKeySuffix(
					"beta"
				).messageParameters(
					StringUtil.merge(enabledBetaLocales)
				).fail());
		}

		if (!unusedLocales.isEmpty()) {
			productionReadinessResults.add(
				ProductionReadinessResult.builder(
					category, "languages"
				).currentValue(
					StringUtil.merge(unusedLocales)
				).messageKeySuffix(
					"unused"
				).messageParameters(
					StringUtil.merge(unusedLocales)
				).fail());
		}

		return productionReadinessResults;
	}

	private static ProductionReadinessResult _checkPasswordEncryption(
		String category) {

		String algorithm = PropsUtil.get("passwords.encryption.algorithm");

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				category, "password-encryption"
			).currentValue(
				algorithm
			).recommendedValue(
				"PBKDF2WithHmacSHA1/160/1300000 (or stronger)"
			);

		if (_isStrongerThanPBKDF2(algorithm)) {
			return builder.pass();
		}

		return builder.severity(
			ProductionReadinessResult.Severity.HIGH
		).fail();
	}

	private static ProductionReadinessResult _checkPortalDeveloperProperties(
		String category) {

		String[] includeAndOverrides = PropsUtil.getArray(
			"include-and-override");

		boolean hasDeveloperProperties = false;

		for (String includeAndOverride : includeAndOverrides) {
			if (includeAndOverride.equals("portal-developer.properties")) {
				hasDeveloperProperties = true;

				break;
			}
		}

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				category, "portal-developer-properties");

		if (hasDeveloperProperties) {
			return builder.currentValue(
				"portal-developer.properties included"
			).severity(
				ProductionReadinessResult.Severity.MEDIUM
			).fail();
		}

		return builder.currentValue(
			"portal-developer.properties is not included"
		).pass();
	}

	private static Collection<ProductionReadinessResult>
		_checkPortalPropertiesConfigurations(String category) {

		Collection<ProductionReadinessResult> productionReadinessResults =
			new ArrayList<>();

		productionReadinessResults.add(_checkJSPReloading(category));

		productionReadinessResults.add(_checkCounterIncrement(category));

		productionReadinessResults.add(_checkDLPreviewForking(category));

		productionReadinessResults.add(_checkDLImagePreviewDPI(category));

		productionReadinessResults.add(_checkFileStoreImplementation(category));

		productionReadinessResults.add(_checkPasswordEncryption(category));

		productionReadinessResults.addAll(_checkLanguages(category));

		productionReadinessResults.add(
			_checkPortalDeveloperProperties(category));

		return productionReadinessResults;
	}

	private static ProductionReadinessResult _checkPreventDiagnosticOverhead(
		String category) {

		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

		List<String> inputArguments = runtimeMXBean.getInputArguments();

		boolean unlocked = false;

		for (String inputArgument : inputArguments) {
			if (inputArgument.equals("-XX:+UnlockDiagnosticVMOptions")) {
				unlocked = true;

				break;
			}
		}

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				category, "prevent-diagnostic-overhead");

		if (unlocked) {
			return builder.currentValue(
				"-XX:+UnlockDiagnosticVMOptions"
			).fail();
		}

		return builder.pass();
	}

	private static ProductionReadinessResult _checkSidecarDetection(
		String category) {

		File file = new File(
			PropsValues.LIFERAY_HOME,
			"osgi/configs/com.liferay.portal.search.elasticsearch8." +
				"configuration.ElasticsearchConfiguration.config");

		ProductionReadinessResult.Builder builder =
			ProductionReadinessResult.builder(
				category, "sidecar-detection"
			).severity(
				ProductionReadinessResult.Severity.HIGH
			);

		if (!file.exists() || !_isProductionModeEnabled(file)) {
			return builder.fail();
		}

		return builder.pass();
	}

	private static int _getMaxThreads() {
		try {
			MBeanServer mBeanServer =
				ManagementFactory.getPlatformMBeanServer();

			ObjectName objectName = new ObjectName(
				"Catalina:type=ThreadPool,name=*");

			Set<ObjectName> objectNames = mBeanServer.queryNames(
				objectName, null);

			int maxThreads = 0;

			for (ObjectName name : objectNames) {
				int threads = (int)mBeanServer.getAttribute(name, "maxThreads");

				if (threads > maxThreads) {
					maxThreads = threads;
				}
			}

			return maxThreads;
		}
		catch (AttributeNotFoundException | InstanceNotFoundException |
			   MalformedObjectNameException | MBeanException |
			   ReflectionException exception) {

			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return 200;
		}
	}

	private static long _getOSHugePageSize() {
		File file = new File("/proc/meminfo");

		if (!file.exists()) {
			return -1;
		}

		try {
			String content = FileUtil.read(file);

			for (String line : StringUtil.splitLines(content)) {
				if (line.startsWith(_PREFIX_HUGEPAGESIZE)) {
					String sizeStr = line.substring(
						_PREFIX_HUGEPAGESIZE.length()
					).trim();

					return _parseSize(StringUtil.removeSubstring(sizeStr, " "));
				}
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return -1;
	}

	private static boolean _isProductionModeEnabled(File file) {
		try {
			String content = FileUtil.read(file);

			return content.contains("productionModeEnabled=B\"true\"");
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return false;
		}
	}

	private static boolean _isStrongerThanPBKDF2(String algorithm) {
		if (algorithm == null) {
			return false;
		}

		if (algorithm.equals("BCRYPT") || algorithm.startsWith("BCRYPT/") ||
			algorithm.equals("SCRYPT")) {

			return true;
		}

		if (algorithm.startsWith("PBKDF2WithHmacSHA1/")) {
			String[] parts = algorithm.split("/");

			if (parts.length >= 3) {
				int rounds = GetterUtil.getInteger(parts[2]);

				if (rounds >= 1300000) {
					return true;
				}
			}
		}

		return false;
	}

	private static long _parseSize(String sizeStr) {
		if (sizeStr == null) {
			return -1;
		}

		sizeStr = StringUtil.toLowerCase(sizeStr);

		long multiplier = 1;

		if (sizeStr.endsWith("k") || sizeStr.endsWith("kb")) {
			multiplier = 1024;
			sizeStr = sizeStr.replaceAll("[^0-9]", "");
		}
		else if (sizeStr.endsWith("m") || sizeStr.endsWith("mb")) {
			multiplier = 1024 * 1024;
			sizeStr = sizeStr.replaceAll("[^0-9]", "");
		}
		else if (sizeStr.endsWith("g") || sizeStr.endsWith("gb")) {
			multiplier = 1024 * 1024 * 1024;
			sizeStr = sizeStr.replaceAll("[^0-9]", "");
		}

		return GetterUtil.getLong(sizeStr) * multiplier;
	}

	private static final String _PREFIX_HUGEPAGESIZE = "Hugepagesize:";

	private static final String _PREFIX_LARGE_PAGE_SIZE_IN_BYTES =
		"-XX:LargePageSizeInBytes=";

	private static final Log _log = LogFactoryUtil.getLog(
		ProductionReadinessRuleUtil.class);

}
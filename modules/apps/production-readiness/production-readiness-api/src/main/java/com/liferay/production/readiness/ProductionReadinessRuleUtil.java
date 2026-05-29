/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness;

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

	public static Collection<Result> check() {
		Collection<Result> results = new ArrayList<>();

		results.addAll(
			_checkJVMConfigurations("jvm-and-infrastructure-validation"));

		results.add(_checkDatabaseConfiguration("database-configuration"));

		results.addAll(
			_checkPortalPropertiesConfigurations(
				"portal-properties-configuration"));

		results.add(
			_checkSidecarDetection("search-engine-connectivity-validation"));

		return results;
	}

	private static Result _checkCounterIncrement(String category) {
		int counterIncrement = GetterUtil.getInteger(
			PropsUtil.get("counter.increment"));

		if (counterIncrement < 2000) {
			return new Result(
				category, String.valueOf(counterIncrement), null,
				"counter-increment",
				"production-readiness-rule-counter-increment-fail",
				new Object[0], null, Result.Severity.LOW, Result.Status.FAIL);
		}

		return new Result(
			category, String.valueOf(counterIncrement), null,
			"counter-increment",
			"production-readiness-rule-counter-increment-pass", new Object[0],
			null, Result.Severity.LOW, Result.Status.PASS);
	}

	private static Result _checkDatabaseConfiguration(String category) {
		int jdbcMaxPoolSize = GetterUtil.getInteger(
			PropsUtil.get("jdbc.default.maximumPoolSize"));

		int tomcatMaxThreads = _getMaxThreads();

		if ((jdbcMaxPoolSize <= 0) || (tomcatMaxThreads <= 0)) {
			return null;
		}

		double ratio = (double)jdbcMaxPoolSize / tomcatMaxThreads;

		boolean pass = false;

		if ((ratio >= 0.3) && (ratio <= 0.4)) {
			pass = true;
		}

		String currentValue = StringBundler.concat(
			"DB Pool Size=", jdbcMaxPoolSize, ", Tomcat Threads=",
			tomcatMaxThreads, " (Ratio=", String.format("%.2f", ratio), ")");

		if (pass) {
			return new Result(
				category, String.valueOf(ratio), null, "pool-vs-thread-ratio",
				"production-readiness-rule-pool-vs-thread-ratio-pass",
				new Object[0], null, Result.Severity.LOW, Result.Status.PASS);
		}

		return new Result(
			category, currentValue, null, "pool-vs-thread-ratio",
			"production-readiness-rule-pool-vs-thread-ratio-fail",
			new Object[0], null, Result.Severity.LOW, Result.Status.FAIL);
	}

	private static Result _checkDLImagePreviewDPI(String category) {
		int dpi = PropsValues.DL_FILE_ENTRY_PREVIEW_DOCUMENT_DPI;

		if (dpi > 75) {
			return new Result(
				category, String.valueOf(dpi), null, "dl-image-preview-dpi",
				"production-readiness-rule-dl-image-preview-dpi-fail",
				new Object[0],
				"Sizes greater than 75 increase the load on the background " +
					"task that generates previews and make the preview " +
						"images larger.",
				Result.Severity.LOW, Result.Status.FAIL);
		}

		return new Result(
			category, String.valueOf(dpi), null, "dl-image-preview-dpi",
			"production-readiness-rule-dl-image-preview-dpi-pass",
			new Object[0], null, Result.Severity.LOW, Result.Status.PASS);
	}

	private static Result _checkDLPreviewForking(String category) {
		if (PropsValues.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED) {
			return new Result(
				category,
				PropsKeys.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED + "=" +
					PropsValues.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED,
				null, "dl-preview-forking",
				"production-readiness-rule-dl-preview-forking-pass",
				new Object[] {
					PropsKeys.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED
				},
				PropsKeys.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED + "=true",
				Result.Severity.LOW, Result.Status.PASS);
		}

		return new Result(
			category,
			PropsKeys.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED + "=" +
				PropsValues.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED,
			null, "dl-preview-forking",
			"production-readiness-rule-dl-preview-forking-fail",
			new Object[] {PropsKeys.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED},
			PropsKeys.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED + "=true",
			Result.Severity.LOW, Result.Status.FAIL);
	}

	private static Result _checkExplicitGCDisabled(String category) {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

		List<String> inputArguments = runtimeMXBean.getInputArguments();

		boolean disabled = false;

		for (String arg : inputArguments) {
			if (arg.equals("-XX:+DisableExplicitGC")) {
				disabled = true;

				break;
			}
		}

		if (disabled) {
			return new Result(
				category, "-XX:+DisableExplicitGC", null,
				"explicit-gc-disabled",
				"production-readiness-rule-explicit-gc-disabled-pass",
				new Object[0], null, Result.Severity.LOW, Result.Status.PASS);
		}

		return new Result(
			category, null, null, "explicit-gc-disabled",
			"production-readiness-rule-explicit-gc-disabled-fail",
			new Object[0], "-XX:+DisableExplicitGC", Result.Severity.LOW,
			Result.Status.FAIL);
	}

	private static Result _checkFileStoreImplementation(String category) {
		String dlStoreImpl = PropsValues.DL_STORE_IMPL;

		boolean pass = false;

		if (dlStoreImpl.equals(
				"com.liferay.portal.store.file.system." +
					"AdvancedFileSystemStore") ||
			dlStoreImpl.equals("com.liferay.portal.store.s3.S3Store") ||
			dlStoreImpl.equals("com.liferay.portal.store.s3.IBMS3Store") ||
			dlStoreImpl.equals("com.liferay.portal.store.gcs.GCSStore") ||
			dlStoreImpl.equals("com.liferay.portal.store.azure.AzureStore")) {

			pass = true;
		}

		if (pass) {
			return new Result(
				category, dlStoreImpl, null, "file-store-implementation",
				"production-readiness-rule-file-store-implementation-pass",
				new Object[0], null, Result.Severity.LOW, Result.Status.PASS);
		}

		return new Result(
			category, dlStoreImpl, null, "file-store-implementation",
			"production-readiness-rule-file-store-implementation-fail",
			new Object[0], "AdvancedFileSystemStore or Cloud Store",
			Result.Severity.LOW, Result.Status.FAIL);
	}

	private static Result _checkGarbageCollectorType(String category) {
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

		String currentGCs = String.join(", ", gcNames);

		if (pass) {
			return new Result(
				category, currentGCs, null, "garbage-collector-type",
				"production-readiness-rule-garbage-collector-type-pass",
				new Object[0], "G1, Shenandoah, or ZGC", Result.Severity.LOW,
				Result.Status.PASS);
		}

		return new Result(
			category, currentGCs, null, "garbage-collector-type",
			"production-readiness-rule-garbage-collector-type-fail",
			new Object[0], "G1, Shenandoah, or ZGC", Result.Severity.LOW,
			Result.Status.FAIL);
	}

	private static Result _checkHeapAllocationConsistency(
		String category, MemoryUsage heapUsage) {

		long xmsBytes = heapUsage.getInit();
		long xmxBytes = heapUsage.getMax();

		if ((xmsBytes > 0) && (xmsBytes == xmxBytes)) {
			return new Result(
				category,
				StringBundler.concat(
					"Xms=", xmsBytes / 1024 / 1024, "MB, Xmx=",
					xmxBytes / 1024 / 1024, "MB"),
				null, "heap-allocation-consistency",
				"production-readiness-rule-heap-allocation-consistency-pass",
				new Object[0], null, Result.Severity.LOW, Result.Status.PASS);
		}

		return new Result(
			category,
			StringBundler.concat(
				"Xms=", xmsBytes / 1024 / 1024, "MB, Xmx=",
				xmxBytes / 1024 / 1024, "MB"),
			null, "heap-allocation-consistency",
			"production-readiness-rule-heap-allocation-consistency-fail",
			new Object[0], null, Result.Severity.LOW, Result.Status.FAIL);
	}

	private static Result _checkHeapSizeUpperLimit(
		String category, MemoryUsage heapUsage) {

		long xmxBytes = heapUsage.getMax();

		double maxMemoryGB = xmxBytes / (1024.0 * 1024.0 * 1024.0);

		if (maxMemoryGB <= 32.0) {
			return new Result(
				category, maxMemoryGB + "GB", null, "heap-size-upper-limit",
				"production-readiness-rule-heap-size-upper-limit-pass",
				new Object[0], null, Result.Severity.LOW, Result.Status.PASS);
		}

		return new Result(
			category, maxMemoryGB + "GB", null, "heap-size-upper-limit",
			"production-readiness-rule-heap-size-upper-limit-fail",
			new Object[0], null, Result.Severity.LOW, Result.Status.FAIL);
	}

	private static Result _checkHugePagesConfiguration(
		String category, MemoryUsage heapUsage) {

		long xmxBytes = heapUsage.getMax();

		double maxMemoryGB = xmxBytes / (1024.0 * 1024.0 * 1024.0);

		if (maxMemoryGB <= 4.0) {
			return new Result(
				category, null, null, "huge-pages-configuration",
				"production-readiness-rule-huge-pages-configuration-heap-" +
					"under-4gb-pass",
				new Object[0], null, Result.Severity.LOW, Result.Status.PASS);
		}

		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

		List<String> inputArguments = runtimeMXBean.getInputArguments();

		boolean useLargePages = false;
		String largePageSizeArg = null;

		for (String arg : inputArguments) {
			if (arg.equals("-XX:+UseLargePages")) {
				useLargePages = true;
			}
			else if (arg.startsWith("-XX:LargePageSizeInBytes=")) {
				largePageSizeArg = arg.substring(25);
			}
		}

		if (!useLargePages) {
			return new Result(
				category, null, null, "huge-pages-configuration",
				"production-readiness-rule-huge-pages-configuration-no-large-" +
					"pages-fail",
				new Object[0], "-XX:+UseLargePages", Result.Severity.MEDIUM,
				Result.Status.FAIL);
		}

		if (largePageSizeArg == null) {
			return new Result(
				category, null, null, "huge-pages-configuration",
				"production-readiness-rule-huge-pages-configuration-missing-" +
					"large-page-size-fail",
				new Object[0], null, Result.Severity.MEDIUM,
				Result.Status.FAIL);
		}

		long osHugePageSize = _getOSHugePageSize();

		if (osHugePageSize > 0) {
			long configLargePageSize = _parseSize(largePageSizeArg);

			if (configLargePageSize != osHugePageSize) {
				return new Result(
					category,
					StringBundler.concat(
						"-XX:LargePageSizeInBytes = ", largePageSizeArg,
						", OS’s huge page size = ", osHugePageSize / 1024,
						"kB"),
					null, "huge-pages-configuration",
					"production-readiness-rule-huge-pages-configuration-size-" +
						"mismatch-fail",
					new Object[0], null, Result.Severity.MEDIUM,
					Result.Status.FAIL);
			}
		}

		return new Result(
			category, null, null, "huge-pages-configuration",
			"production-readiness-rule-huge-pages-configuration-configured-" +
				"pass",
			new Object[0], null, Result.Severity.LOW, Result.Status.PASS);
	}

	private static Result _checkJMXConfigurationDisabled(String category) {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

		List<String> inputArguments = runtimeMXBean.getInputArguments();

		boolean jmxEnabled = false;
		String enabledArg = null;

		for (String arg : inputArguments) {
			if (arg.startsWith("-Dcom.sun.management.jmxremote")) {
				jmxEnabled = true;
				enabledArg = arg;

				break;
			}
		}

		if (jmxEnabled) {
			return new Result(
				category,
				"JMX Configuration has been enabled (" + enabledArg + ")", null,
				"jmx-configuration-disabled",
				"production-readiness-rule-jmx-configuration-disabled-fail",
				new Object[0], null, Result.Severity.LOW, Result.Status.FAIL);
		}

		return new Result(
			category, null, null, "jmx-configuration-disabled",
			"production-readiness-rule-jmx-configuration-disabled-pass",
			new Object[0], null, Result.Severity.LOW, Result.Status.PASS);
	}

	private static Result _checkJSPEngineSettings(String category) {
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

				if (!(boolean)development && !(boolean)mappedFile) {
					return new Result(
						category,
						StringBundler.concat(
							"development=", development, ", mappedfile=",
							mappedFile),
						null, "jsp-engine-settings",
						"production-readiness-rule-jsp-engine-settings-pass",
						new Object[0], "development=false, mappedfile=false",
						Result.Severity.LOW, Result.Status.PASS);
				}

				return new Result(
					category,
					StringBundler.concat(
						"development=", development, ", mappedfile=",
						mappedFile),
					null, "jsp-engine-settings",
					"production-readiness-rule-jsp-engine-settings-fail",
					new Object[0], "development=false, mappedfile=false",
					Result.Severity.LOW, Result.Status.FAIL);
			}
			else if (Validator.isNull(development) ||
					 Validator.isNull(mappedFile)) {

				return new Result(
					category,
					"development or mappedfile is not set, Tomcat will use " +
						"the default value development=true or mappedfile=true",
					null, "jsp-engine-settings",
					"production-readiness-rule-jsp-engine-settings-fail",
					new Object[0], "development=false, mappedfile=false",
					Result.Severity.LOW, Result.Status.FAIL);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return null;
	}

	private static Result _checkJSPReloading(String category) {
		boolean directServletContextReload = GetterUtil.getBoolean(
			PropsUtil.get("direct.servlet.context.reload"));

		if (directServletContextReload) {
			return new Result(
				category,
				"direct.servlet.context.reload=" + directServletContextReload,
				null, "jsp-reloading",
				"production-readiness-rule-jsp-reloading-fail", new Object[0],
				"direct.servlet.context.reload=false", Result.Severity.MEDIUM,
				Result.Status.FAIL);
		}

		return new Result(
			category,
			"direct.servlet.context.reload=" + directServletContextReload, null,
			"jsp-reloading", "production-readiness-rule-jsp-reloading-pass",
			new Object[0], "direct.servlet.context.reload=false",
			Result.Severity.LOW, Result.Status.PASS);
	}

	private static Collection<Result> _checkJVMConfigurations(String category) {
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

		MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();

		Collection<Result> results = new ArrayList<>();

		results.add(_checkHeapAllocationConsistency(category, heapUsage));

		results.add(_checkHeapSizeUpperLimit(category, heapUsage));

		results.add(_checkHugePagesConfiguration(category, heapUsage));

		results.add(_checkJSPEngineSettings(category));

		results.add(_checkGarbageCollectorType(category));

		results.add(_checkExplicitGCDisabled(category));

		results.add(_checkPreventDiagnosticOverhead(category));

		results.add(_checkJMXConfigurationDisabled(category));

		return results;
	}

	private static Collection<Result> _checkLanguages(String category) {
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
				new Result(
					category, null, null, "languages",
					"production-readiness-rule-languages-pass", new Object[0],
					null, Result.Severity.LOW, Result.Status.PASS));
		}

		List<Result> results = new ArrayList<>(2);

		if (!enabledBetaLocales.isEmpty()) {
			results.add(
				new Result(
					category, StringUtil.merge(enabledBetaLocales), null,
					"languages",
					"production-readiness-rule-languages-beta-fail",
					new Object[] {
						"You are using Beta locale in production:" +
							StringUtil.merge(enabledBetaLocales)
					},
					null, Result.Severity.LOW, Result.Status.FAIL));
		}

		if (!unusedLocales.isEmpty()) {
			results.add(
				new Result(
					category, StringUtil.merge(unusedLocales), null,
					"languages",
					"production-readiness-rule-languages-unused-fail",
					new Object[] {
						"Unused languages add overhead to the XML’s stored " +
							"in the database" + StringUtil.merge(unusedLocales)
					},
					"Remove unused locales from LOCALES " +
						"(portal-ext.properties)",
					Result.Severity.LOW, Result.Status.FAIL));
		}

		return results;
	}

	private static Result _checkPasswordEncryption(String category) {
		String algorithm = PropsUtil.get("passwords.encryption.algorithm");

		if (_isStrongerThanPBKDF2(algorithm)) {
			return new Result(
				category, algorithm, null, "password-encryption",
				"production-readiness-rule-password-encryption-pass",
				new Object[0], "PBKDF2WithHmacSHA1/160/1300000 (or stronger)",
				Result.Severity.LOW, Result.Status.PASS);
		}

		return new Result(
			category, algorithm, null, "password-encryption",
			"production-readiness-rule-password-encryption-fail", new Object[0],
			"PBKDF2WithHmacSHA1/160/1300000 (or stronger), If you are using " +
				"External IdP provider, this can be safely ignored.",
			Result.Severity.HIGH, Result.Status.FAIL);
	}

	private static Result _checkPortalDeveloperProperties(String category) {
		String[] includeAndOverrides = PropsUtil.getArray(
			"include-and-override");

		boolean hasDeveloperProperties = false;

		for (String includeAndOverride : includeAndOverrides) {
			if (includeAndOverride.equals("portal-developer.properties")) {
				hasDeveloperProperties = true;

				break;
			}
		}

		if (hasDeveloperProperties) {
			return new Result(
				category, "portal-developer.properties included", null,
				"portal-developer-properties",
				"production-readiness-rule-portal-developer-properties-fail",
				new Object[0], null, Result.Severity.MEDIUM,
				Result.Status.FAIL);
		}

		return new Result(
			category, "portal-developer.properties is not included", null,
			"portal-developer-properties",
			"production-readiness-rule-portal-developer-properties-pass",
			new Object[0], null, Result.Severity.LOW, Result.Status.PASS);
	}

	private static Collection<Result> _checkPortalPropertiesConfigurations(
		String category) {

		Collection<Result> results = new ArrayList<>();

		results.add(_checkJSPReloading(category));

		results.add(_checkCounterIncrement(category));

		results.add(_checkDLPreviewForking(category));

		results.add(_checkDLImagePreviewDPI(category));

		results.add(_checkFileStoreImplementation(category));

		results.add(_checkPasswordEncryption(category));

		results.addAll(_checkLanguages(category));

		results.add(_checkPortalDeveloperProperties(category));

		return results;
	}

	private static Result _checkPreventDiagnosticOverhead(String category) {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

		List<String> inputArguments = runtimeMXBean.getInputArguments();

		boolean unlocked = false;

		for (String arg : inputArguments) {
			if (arg.equals("-XX:+UnlockDiagnosticVMOptions")) {
				unlocked = true;

				break;
			}
		}

		if (unlocked) {
			return new Result(
				category, "-XX:+UnlockDiagnosticVMOptions", null,
				"prevent-diagnostic-overhead",
				"production-readiness-rule-prevent-diagnostic-overhead-fail",
				new Object[0], null, Result.Severity.LOW, Result.Status.FAIL);
		}

		return new Result(
			category, null, null, "prevent-diagnostic-overhead",
			"production-readiness-rule-prevent-diagnostic-overhead-pass",
			new Object[0], null, Result.Severity.LOW, Result.Status.PASS);
	}

	private static Result _checkSidecarDetection(String category) {
		File file = new File(
			PropsValues.LIFERAY_HOME,
			"osgi/configs/com.liferay.portal.search.elasticsearch8." +
				"configuration.ElasticsearchConfiguration.config");

		if (!file.exists() || !_isProductionModeEnabled(file)) {
			return new Result(
				category, null, null, "sidecar-detection",
				"production-readiness-rule-sidecar-detection-fail",
				new Object[0], null, Result.Severity.HIGH, Result.Status.FAIL);
		}

		return new Result(
			category, null, null, "sidecar-detection",
			"production-readiness-rule-sidecar-detection-pass", new Object[0],
			null, Result.Severity.HIGH, Result.Status.PASS);
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
				if (line.startsWith("Hugepagesize:")) {
					String sizeStr = line.substring(
						13
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

	private static final Log _log = LogFactoryUtil.getLog(
		ProductionReadinessRuleUtil.class);

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.io.File;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lily Chi
 */
@Component(service = ProductionReadinessRule.class)
public class HugePagesConfigurationRuleImpl implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

		MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();

		long xmxBytes = heapUsage.getMax();

		double maxMemoryGB = xmxBytes / (1024.0 * 1024.0 * 1024.0);

		if (maxMemoryGB <= 4.0) {
			return Collections.singletonList(
				new Result(
					getCategory(), null, null, getKey(),
					"production-readiness-rule-huge-pages-configuration-heap-" +
						"under-4gb-pass",
					new Object[0], null, Result.Severity.LOW,
					Result.Status.PASS));
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
			return Collections.singletonList(
				new Result(
					getCategory(), null, null, getKey(),
					"production-readiness-rule-huge-pages-configuration-no-" +
						"large-pages-fail",
					new Object[0], "-XX:+UseLargePages", Result.Severity.MEDIUM,
					Result.Status.FAIL));
		}

		if (largePageSizeArg == null) {
			return Collections.singletonList(
				new Result(
					getCategory(), null, null, getKey(),
					"production-readiness-rule-huge-pages-configuration-" +
						"missing-large-page-size-fail",
					new Object[0], null, Result.Severity.MEDIUM,
					Result.Status.FAIL));
		}

		long osHugePageSize = _getOSHugePageSize();

		if (osHugePageSize > 0) {
			long configLargePageSize = _parseSize(largePageSizeArg);

			if (configLargePageSize != osHugePageSize) {
				return Collections.singletonList(
					new Result(
						getCategory(),
						StringBundler.concat(
							"-XX:LargePageSizeInBytes = ", largePageSizeArg,
							", OS’s huge page size = ", osHugePageSize / 1024,
							"kB"),
						null, getKey(),
						"production-readiness-rule-huge-pages-configuration-" +
							"size-mismatch-fail",
						new Object[0], null, Result.Severity.MEDIUM,
						Result.Status.FAIL));
			}
		}

		return Collections.singletonList(
			new Result(
				getCategory(), null, null, getKey(),
				"production-readiness-rule-huge-pages-configuration-" +
					"configured-pass",
				new Object[0], null, Result.Severity.LOW, Result.Status.PASS));
	}

	@Override
	public String getCategory() {
		return "jvm-and-infrastructure-validation";
	}

	@Override
	public String getKey() {
		return "huge-pages-configuration";
	}

	private long _getOSHugePageSize() {
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

	private long _parseSize(String sizeStr) {
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
		HugePagesConfigurationRuleImpl.class);

}
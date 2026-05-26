/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.production.readiness.Result;

import java.io.File;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lily Chi
 */
public class HugePagesConfigurationRuleImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCheckReturnsFailWhenHeapExceeds4GBWithoutUseLargePages() {
		MemoryMXBean mockMemoryMXBean = _mockMemoryMXBean(_HEAP_OVER_4GB);
		RuntimeMXBean mockRuntimeMXBean = _mockRuntimeMXBean(
			Collections.emptyList());

		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			managementFactoryMockedStatic.when(
				ManagementFactory::getMemoryMXBean
			).thenReturn(
				mockMemoryMXBean
			);

			managementFactoryMockedStatic.when(
				ManagementFactory::getRuntimeMXBean
			).thenReturn(
				mockRuntimeMXBean
			);

			Collection<Result> results = _hugePagesConfigurationRuleImpl.check(
				0L);

			Assert.assertEquals(results.toString(), 1, results.size());

			Result result = results.iterator(
			).next();

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals(
				_MESSAGE_KEY_NO_LARGE_PAGES_FAIL, result.getMessageKey());
		}
	}

	@Test
	public void testCheckReturnsFailWhenLargePageSizeMismatchesOSHugePageSize() {
		Assume.assumeTrue(
			"Test requires /proc/meminfo (Linux only)",
			new File(
				"/proc/meminfo"
			).exists());

		MemoryMXBean mockMemoryMXBean = _mockMemoryMXBean(_HEAP_OVER_4GB);
		RuntimeMXBean mockRuntimeMXBean = _mockRuntimeMXBean(
			Arrays.asList("-XX:+UseLargePages", "-XX:LargePageSizeInBytes=4m"));

		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class);
			MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(
				FileUtil.class)) {

			managementFactoryMockedStatic.when(
				ManagementFactory::getMemoryMXBean
			).thenReturn(
				mockMemoryMXBean
			);

			managementFactoryMockedStatic.when(
				ManagementFactory::getRuntimeMXBean
			).thenReturn(
				mockRuntimeMXBean
			);

			fileUtilMockedStatic.when(
				() -> FileUtil.read(Mockito.any(File.class))
			).thenReturn(
				"MemTotal:       16384000 kB\nHugepagesize:    2048 kB\n"
			);

			Collection<Result> results = _hugePagesConfigurationRuleImpl.check(
				0L);

			Assert.assertEquals(results.toString(), 1, results.size());

			Result result = results.iterator(
			).next();

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals(
				_MESSAGE_KEY_SIZE_MISMATCH_FAIL, result.getMessageKey());
		}
	}

	private MemoryMXBean _mockMemoryMXBean(long xmxBytes) {
		MemoryUsage mockHeapMemoryUsage = new MemoryUsage(
			xmxBytes / 2, xmxBytes / 4, xmxBytes / 2, xmxBytes);

		MemoryMXBean mockMemoryMXBean = Mockito.mock(MemoryMXBean.class);

		Mockito.when(
			mockMemoryMXBean.getHeapMemoryUsage()
		).thenReturn(
			mockHeapMemoryUsage
		);

		return mockMemoryMXBean;
	}

	private RuntimeMXBean _mockRuntimeMXBean(List<String> inputArguments) {
		RuntimeMXBean mockRuntimeMXBean = Mockito.mock(RuntimeMXBean.class);

		Mockito.when(
			mockRuntimeMXBean.getInputArguments()
		).thenReturn(
			inputArguments
		);

		return mockRuntimeMXBean;
	}

	private static final long _HEAP_OVER_4GB = 8L * 1024 * 1024 * 1024;

	private static final String _MESSAGE_KEY_NO_LARGE_PAGES_FAIL =
		"production-readiness-rule-huge-pages-configuration-no-large-pages-" +
			"fail";

	private static final String _MESSAGE_KEY_SIZE_MISMATCH_FAIL =
		"production-readiness-rule-huge-pages-configuration-size-mismatch-fail";

	private final HugePagesConfigurationRuleImpl
		_hugePagesConfigurationRuleImpl = new HugePagesConfigurationRuleImpl();

}
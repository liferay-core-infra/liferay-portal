/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.production.readiness.Result;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import java.util.Collection;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lily Chi
 */
public class HeapAllocationConsistencyRuleImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCheckReturnsFailWhenXmsNotEqualXmx() {
		long xmsBytes = 100L * 1024 * 1024;
		long xmxBytes = 200L * 1024 * 1024;

		MemoryUsage mockHeapMemoryUsage = new MemoryUsage(
			xmsBytes, xmsBytes / 2, xmsBytes, xmxBytes);

		MemoryMXBean mockMemoryMXBean = Mockito.mock(MemoryMXBean.class);

		Mockito.when(
			mockMemoryMXBean.getHeapMemoryUsage()
		).thenReturn(
			mockHeapMemoryUsage
		);

		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			managementFactoryMockedStatic.when(
				ManagementFactory::getMemoryMXBean
			).thenReturn(
				mockMemoryMXBean
			);

			Collection<Result> results =
				_heapAllocationConsistencyRuleImpl.check(0L);

			Assert.assertEquals(results.toString(), 1, results.size());

			Result result = results.iterator(
			).next();

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals(
				"production-readiness-rule-heap-allocation-consistency-fail",
				result.getMessageKey());
		}
	}

	private final HeapAllocationConsistencyRuleImpl
		_heapAllocationConsistencyRuleImpl =
			new HeapAllocationConsistencyRuleImpl();

}
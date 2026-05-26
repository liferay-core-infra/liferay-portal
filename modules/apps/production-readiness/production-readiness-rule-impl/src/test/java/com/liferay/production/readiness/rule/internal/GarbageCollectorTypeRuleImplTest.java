/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.production.readiness.Result;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lily Chi
 */
public class GarbageCollectorTypeRuleImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCheckReturnsFailWhenNoModernGarbageCollectorIsPresent() {
		_assertCheckResult(
			Arrays.asList("PS Scavenge", "PS MarkSweep"), Result.Status.FAIL,
			_MESSAGE_KEY_FAIL, "PS Scavenge, PS MarkSweep");
	}

	@Test
	public void testCheckReturnsPassWhenG1IsPresent() {
		_assertCheckResult(
			Arrays.asList("G1 Young Generation", "G1 Old Generation"),
			Result.Status.PASS, _MESSAGE_KEY_PASS,
			"G1 Young Generation, G1 Old Generation");
	}

	@Test
	public void testCheckReturnsPassWhenShenandoahIsPresent() {
		_assertCheckResult(
			Arrays.asList("Shenandoah Cycles", "Shenandoah Pauses"),
			Result.Status.PASS, _MESSAGE_KEY_PASS,
			"Shenandoah Cycles, Shenandoah Pauses");
	}

	@Test
	public void testCheckReturnsPassWhenZGCIsPresent() {
		_assertCheckResult(
			Arrays.asList("ZGC Cycles", "ZGC Pauses"), Result.Status.PASS,
			_MESSAGE_KEY_PASS, "ZGC Cycles, ZGC Pauses");
	}

	private void _assertCheckResult(
		List<String> gcNames, Result.Status expectedStatus,
		String expectedMessageKey, String expectedCurrentValue) {

		List<GarbageCollectorMXBean> garbageCollectorMXBeans =
			_mockGarbageCollectorMXBeans(gcNames);

		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			managementFactoryMockedStatic.when(
				ManagementFactory::getGarbageCollectorMXBeans
			).thenReturn(
				garbageCollectorMXBeans
			);

			Collection<Result> results = _garbageCollectorTypeRuleImpl.check(
				0L);

			Assert.assertEquals(results.toString(), 1, results.size());

			Result result = results.iterator(
			).next();

			Assert.assertEquals(expectedStatus, result.getStatus());
			Assert.assertEquals(Result.Severity.LOW, result.getSeverity());
			Assert.assertEquals(
				"jvm-and-infrastructure-validation", result.getCategory());
			Assert.assertEquals(expectedMessageKey, result.getMessageKey());
			Assert.assertEquals(expectedCurrentValue, result.getCurrentValue());
			Assert.assertEquals(
				"G1, Shenandoah, or ZGC", result.getRecommendedValue());
			Assert.assertEquals(0, result.getMessageParameters().length);
		}
	}

	private List<GarbageCollectorMXBean> _mockGarbageCollectorMXBeans(
		List<String> gcNames) {

		List<GarbageCollectorMXBean> garbageCollectorMXBeans =
			new ArrayList<>();

		for (String gcName : gcNames) {
			GarbageCollectorMXBean garbageCollectorMXBean = Mockito.mock(
				GarbageCollectorMXBean.class);

			Mockito.when(
				garbageCollectorMXBean.getName()
			).thenReturn(
				gcName
			);

			garbageCollectorMXBeans.add(garbageCollectorMXBean);
		}

		return garbageCollectorMXBeans;
	}

	private static final String _MESSAGE_KEY_FAIL =
		"production-readiness-rule-garbage-collector-type-fail";

	private static final String _MESSAGE_KEY_PASS =
		"production-readiness-rule-garbage-collector-type-pass";

	private final GarbageCollectorTypeRuleImpl _garbageCollectorTypeRuleImpl =
		new GarbageCollectorTypeRuleImpl();

}
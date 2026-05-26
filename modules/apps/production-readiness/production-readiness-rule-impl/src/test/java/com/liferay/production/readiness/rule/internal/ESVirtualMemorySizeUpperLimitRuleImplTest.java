/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.production.readiness.Result;

import java.util.Collection;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Lily Chi
 */
public class ESVirtualMemorySizeUpperLimitRuleImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCheckReturnsFail() {
		Collection<Result> results =
			_esVirtualMemorySizeUpperLimitRuleImpl.check(0L);

		Assert.assertEquals(results.toString(), 1, results.size());

		Result result = results.iterator(
		).next();

		Assert.assertEquals(Result.Status.FAIL, result.getStatus());
		Assert.assertEquals(Result.Severity.HIGH, result.getSeverity());
		Assert.assertEquals(
			"search-engine-settings-validation", result.getCategory());
		Assert.assertEquals(
			"production-readiness-rule-es-virtual-memory-size-upper-limit-" +
				"message",
			result.getMessageKey());
		Assert.assertNull(result.getCurrentValue());
		Assert.assertEquals(
			"vm.max_map_count >= 262144", result.getRecommendedValue());
		Assert.assertEquals(0, result.getMessageParameters().length);
		Assert.assertEquals(
			"https://www.elastic.co/docs/deploy-manage/deploy/self-" +
				"managed/vm-max-map-count",
			result.getDocsLink());
	}

	private final ESVirtualMemorySizeUpperLimitRuleImpl
		_esVirtualMemorySizeUpperLimitRuleImpl =
			new ESVirtualMemorySizeUpperLimitRuleImpl();

}
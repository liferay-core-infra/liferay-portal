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
public class AnalyzerPluginsRuleImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCheckReturnsFail() {
		Collection<Result> results = _analyzerPluginsRuleImpl.check(0L);

		Assert.assertEquals(results.toString(), 1, results.size());

		Result result = results.iterator(
		).next();

		Assert.assertEquals(Result.Status.FAIL, result.getStatus());
		Assert.assertEquals(Result.Severity.HIGH, result.getSeverity());
		Assert.assertEquals(
			"search-engine-settings-validation", result.getCategory());
		Assert.assertEquals(
			"production-readiness-rule-analyzer-plugins-message",
			result.getMessageKey());
		Assert.assertNull(result.getCurrentValue());
		Assert.assertEquals(
			"analysis-icu, analysis-kuromoji, analysis-smartcn, " +
				"analysis-stempel",
			result.getRecommendedValue());
		Assert.assertEquals(0, result.getMessageParameters().length);
	}

	private final AnalyzerPluginsRuleImpl _analyzerPluginsRuleImpl =
		new AnalyzerPluginsRuleImpl();

}
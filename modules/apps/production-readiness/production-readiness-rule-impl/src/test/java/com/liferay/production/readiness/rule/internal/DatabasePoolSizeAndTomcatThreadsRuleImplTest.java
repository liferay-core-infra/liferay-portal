/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rule.internal;

import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.production.readiness.Result;

import java.lang.management.ManagementFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lily Chi
 */
public class DatabasePoolSizeAndTomcatThreadsRuleImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCheckReturnsEmptyWhenJDBCMaxPoolSizeIsNotPositive()
		throws Exception {

		Collection<Result> results = _check("0", 200);

		Assert.assertTrue(results.toString(), results.isEmpty());
	}

	@Test
	public void testCheckReturnsEmptyWhenTomcatMaxThreadsIsNotPositive()
		throws Exception {

		Collection<Result> results = _check("60", 0);

		Assert.assertTrue(results.toString(), results.isEmpty());
	}

	@Test
	public void testCheckReturnsFailWhenRatioIsAboveRange() throws Exception {
		_assertCheckResult("100", 200, Result.Status.FAIL, _MESSAGE_KEY_FAIL);
	}

	@Test
	public void testCheckReturnsFailWhenRatioIsBelowRange() throws Exception {
		_assertCheckResult("40", 200, Result.Status.FAIL, _MESSAGE_KEY_FAIL);
	}

	@Test
	public void testCheckReturnsPassWhenRatioIsInRange() throws Exception {
		_assertCheckResult("70", 200, Result.Status.PASS, _MESSAGE_KEY_PASS);
	}

	private void _assertCheckResult(
			String jdbcMaxPoolSize, int tomcatMaxThreads,
			Result.Status expectedStatus, String expectedMessageKey)
		throws Exception {

		Collection<Result> results = _check(jdbcMaxPoolSize, tomcatMaxThreads);

		Assert.assertEquals(results.toString(), 1, results.size());

		Result result = results.iterator(
		).next();

		Assert.assertEquals(expectedStatus, result.getStatus());
		Assert.assertEquals(Result.Severity.LOW, result.getSeverity());
		Assert.assertEquals("database-configuration", result.getCategory());
		Assert.assertEquals(expectedMessageKey, result.getMessageKey());
		Assert.assertNull(result.getRecommendedValue());
		Assert.assertEquals(0, result.getMessageParameters().length);
	}

	private Collection<Result> _check(
			String jdbcMaxPoolSize, int tomcatMaxThreads)
		throws Exception {

		ObjectName objectName = new ObjectName(
			"Catalina:type=ThreadPool,name=http-nio-8080");

		Set<ObjectName> objectNames = Collections.singleton(objectName);

		MBeanServer mBeanServer = Mockito.mock(MBeanServer.class);

		Mockito.when(
			mBeanServer.queryNames(
				Mockito.any(ObjectName.class), Mockito.isNull())
		).thenReturn(
			objectNames
		);

		Mockito.when(
			mBeanServer.getAttribute(objectName, "maxThreads")
		).thenReturn(
			tomcatMaxThreads
		);

		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class);
			MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class)) {

			managementFactoryMockedStatic.when(
				ManagementFactory::getPlatformMBeanServer
			).thenReturn(
				mBeanServer
			);

			propsUtilMockedStatic.when(
				() -> PropsUtil.get("jdbc.default.maximumPoolSize")
			).thenReturn(
				jdbcMaxPoolSize
			);

			return _databasePoolSizeAndTomcatThreadsRuleImpl.check(0L);
		}
	}

	private static final String _MESSAGE_KEY_FAIL =
		"production-readiness-rule-pool-vs-thread-ratio-fail";

	private static final String _MESSAGE_KEY_PASS =
		"production-readiness-rule-pool-vs-thread-ratio-pass";

	private final DatabasePoolSizeAndTomcatThreadsRuleImpl
		_databasePoolSizeAndTomcatThreadsRuleImpl =
			new DatabasePoolSizeAndTomcatThreadsRuleImpl();

}
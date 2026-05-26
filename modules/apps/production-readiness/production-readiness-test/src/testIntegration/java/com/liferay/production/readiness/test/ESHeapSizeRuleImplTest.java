/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Lily Chi
 */
@RunWith(Arquillian.class)
public class ESHeapSizeRuleImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testESHeapSizeRuleCheckReturnsSingleResult() throws Exception {
		BundleContext bundleContext = _getBundleContext();

		ServiceReference<ProductionReadinessRule> serviceReference =
			_getESHeapSizeRuleServiceReference(bundleContext);

		try {
			ProductionReadinessRule productionReadinessRule =
				bundleContext.getService(serviceReference);

			Collection<Result> results = productionReadinessRule.check(
				PortalUtil.getDefaultCompanyId());

			Assert.assertEquals(results.toString(), 1, results.size());

			Result result = results.iterator(
			).next();

			Assert.assertEquals(Result.Status.FAIL, result.getStatus());
			Assert.assertEquals(Result.Severity.HIGH, result.getSeverity());
			Assert.assertEquals(
				"search-engine-settings-validation", result.getCategory());
			Assert.assertEquals(
				"production-readiness-rule-es-heap-size-message",
				result.getMessageKey());
			Assert.assertEquals(0, result.getMessageParameters().length);
		}
		finally {
			bundleContext.ungetService(serviceReference);
		}
	}

	@Test
	public void testESHeapSizeRuleIsRegistered() throws Exception {
		BundleContext bundleContext = _getBundleContext();

		ServiceReference<ProductionReadinessRule> serviceReference =
			_getESHeapSizeRuleServiceReference(bundleContext);

		try {
			ProductionReadinessRule productionReadinessRule =
				bundleContext.getService(serviceReference);

			Assert.assertEquals(_RULE_KEY, productionReadinessRule.getKey());
			Assert.assertEquals(
				"search-engine-settings-validation",
				productionReadinessRule.getCategory());
		}
		finally {
			bundleContext.ungetService(serviceReference);
		}
	}

	private BundleContext _getBundleContext() {
		return FrameworkUtil.getBundle(
			ESHeapSizeRuleImplTest.class
		).getBundleContext();
	}

	private ServiceReference<ProductionReadinessRule>
			_getESHeapSizeRuleServiceReference(BundleContext bundleContext)
		throws Exception {

		Collection<ServiceReference<ProductionReadinessRule>>
			serviceReferences = bundleContext.getServiceReferences(
				ProductionReadinessRule.class, null);

		Iterator<ServiceReference<ProductionReadinessRule>> iterator =
			serviceReferences.iterator();

		while (iterator.hasNext()) {
			ServiceReference<ProductionReadinessRule> serviceReference =
				iterator.next();

			ProductionReadinessRule productionReadinessRule =
				bundleContext.getService(serviceReference);

			try {
				if (_RULE_KEY.equals(productionReadinessRule.getKey())) {
					return serviceReference;
				}
			}
			finally {
				bundleContext.ungetService(serviceReference);
			}
		}

		throw new AssertionError(
			"The production-readiness-rule-impl bundle is not deployed: no " +
				"ProductionReadinessRule was registered with key " + _RULE_KEY);
	}

	private static final String _RULE_KEY = "es-heap-size";

}
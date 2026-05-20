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
public class ProductionReadinessRuleSPITest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testRulesAreDiscoverableViaOSGi() throws Exception {
		BundleContext bundleContext = FrameworkUtil.getBundle(
			ProductionReadinessRuleSPITest.class
		).getBundleContext();

		Collection<ServiceReference<ProductionReadinessRule>>
			serviceReferences = bundleContext.getServiceReferences(
				ProductionReadinessRule.class, null);

		Assert.assertFalse(
			"No ProductionReadinessRule services registered — the rule-impl " +
				"bundle is not deployed",
			serviceReferences.isEmpty());

		long companyId = PortalUtil.getDefaultCompanyId();

		for (ServiceReference<ProductionReadinessRule> serviceReference :
				serviceReferences) {

			ProductionReadinessRule productionReadinessRule =
				bundleContext.getService(serviceReference);

			try {
				Assert.assertNotNull(productionReadinessRule.getKey());
				Assert.assertNotNull(productionReadinessRule.getCategory());

				Collection<Result> results = productionReadinessRule.check(
					companyId);

				Assert.assertNotNull(results);

				for (Result result : results) {
					Assert.assertNotNull(
						"Result.status must be set", result.getStatus());
					Assert.assertNotNull(
						"Result.severity must be set", result.getSeverity());
					Assert.assertNotNull(
						"Result.category must be set", result.getCategory());
					Assert.assertNotNull(
						"Result.messageKey must be set",
						result.getMessageKey());
				}
			}
			finally {
				bundleContext.ungetService(serviceReference);
			}
		}
	}

}
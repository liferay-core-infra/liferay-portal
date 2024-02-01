/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.company.creation.performance.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PortalInstances;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lily Chi
 */
@DataGuard(scope = DataGuard.Scope.NONE)
@RunWith(Arquillian.class)
public class CompanyCreationPerformanceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		Assume.assumeTrue(Validator.isNull(System.getenv("JENKINS_HOME")));
	}

	@Test
	public void testSingleThreadAddCompanies() throws Exception {
		int originalCompaniesCount = _companyLocalService.getCompaniesCount();

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			PortalInstances.initCompany(
				_companyLocalService.addCompany(
					null, "liferay1.com", "liferay1.com", "liferay.com", 0,
					true, null, null, null, null, null, null));
		}

		Assert.assertEquals(
			"Company count should be " + originalCompaniesCount + 1,
			originalCompaniesCount + 1,
			_companyLocalService.getCompaniesCount());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

}
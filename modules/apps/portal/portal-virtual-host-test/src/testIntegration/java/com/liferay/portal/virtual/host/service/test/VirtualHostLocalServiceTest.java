/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.virtual.host.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class VirtualHostLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetVirtualHost() throws Exception {
		Company company = _companyLocalService.addCompany(
			null, "::1", "::1", "test.com", 0, true, true, null, null, null,
			null, null, null);

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			VirtualHost virtualHost = _virtualHostLocalService.getVirtualHost(
				"::1");

			Assert.assertEquals(
				company.getCompanyId(), virtualHost.getCompanyId());

			virtualHost = _virtualHostLocalService.getVirtualHost(
				"0:0:0:0:0:0:0:1");

			Assert.assertEquals(
				company.getCompanyId(), virtualHost.getCompanyId());
		}
		finally {
			_companyLocalService.deleteCompany(company);
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private VirtualHostLocalService _virtualHostLocalService;

}
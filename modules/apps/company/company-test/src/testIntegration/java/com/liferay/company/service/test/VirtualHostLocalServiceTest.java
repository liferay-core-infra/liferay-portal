/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.company.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
public class VirtualHostLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testReloadVirtualHosts() throws Exception {
		Assume.assumeTrue(PropsValues.DATABASE_PARTITION_ENABLED);

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Assert.assertNotEquals(
			PortalInstancePool.getDefaultCompanyId(), company.getCompanyId());

		String virtualHostname = company.getVirtualHostname();

		Assert.assertEquals(
			company.getCompanyId(),
			_fetchVirtualHostCompanyId(virtualHostname));

		_virtualHostLocalService.unregisterVirtualHost(virtualHostname);

		Assert.assertEquals(0, _fetchVirtualHostCompanyId(virtualHostname));

		_virtualHostLocalService.reloadVirtualHosts();

		Assert.assertEquals(
			company.getCompanyId(),
			_fetchVirtualHostCompanyId(virtualHostname));
	}

	private long _fetchVirtualHostCompanyId(String hostname) {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId())) {

			VirtualHost virtualHost = _virtualHostLocalService.fetchVirtualHost(
				hostname);

			if (virtualHost == null) {
				return 0;
			}

			return virtualHost.getCompanyId();
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private VirtualHostLocalService _virtualHostLocalService;

}
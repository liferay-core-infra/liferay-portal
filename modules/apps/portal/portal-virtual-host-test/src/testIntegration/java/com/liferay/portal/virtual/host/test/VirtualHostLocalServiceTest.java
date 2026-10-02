/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.virtual.host.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.LayoutSetVirtualHostException;
import com.liferay.portal.kernel.exception.NoSuchVirtualHostException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.net.IDN;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
	public void testDeleteLayoutSetDeletesVirtualHosts() throws Exception {
		Group group = GroupTestUtil.addGroup();

		String hostname = _randomHostname();

		_virtualHostLocalService.updateVirtualHosts(
			group.getPublicLayoutSet(),
			TreeMapBuilder.put(
				hostname, StringPool.BLANK
			).build());

		Assert.assertNotNull(
			_virtualHostLocalService.fetchVirtualHost(hostname));

		_groupLocalService.deleteGroup(group);

		Assert.assertNull(_virtualHostLocalService.fetchVirtualHost(hostname));
	}

	@Test
	public void testGetVirtualHost() throws Exception {
		Group group = GroupTestUtil.addGroup();

		try {
			String hexString = Integer.toHexString(
				RandomTestUtil.randomInt(1, 0xFFFF));

			String unicodeHostname = StringUtil.toLowerCase(
				"bücher-" + RandomTestUtil.randomString() + ".test");

			Map<String, String> alternateHostnames = HashMapBuilder.put(
				unicodeHostname, IDN.toASCII(unicodeHostname)
			).put(
				"fd00:0:0:0:0:0:0:" + hexString, "fd00::" + hexString
			).build();

			TreeMap<String, String> hostnames = new TreeMap<>();

			for (String hostname : alternateHostnames.keySet()) {
				hostnames.put(hostname, StringPool.BLANK);
			}

			_virtualHostLocalService.updateVirtualHosts(
				group.getPublicLayoutSet(), hostnames);

			for (Map.Entry<String, String> entry :
					alternateHostnames.entrySet()) {

				VirtualHost virtualHost =
					_virtualHostLocalService.fetchVirtualHost(entry.getKey());

				Assert.assertNotNull(virtualHost);

				Assert.assertEquals(
					virtualHost,
					_virtualHostLocalService.fetchVirtualHost(
						entry.getValue()));
				Assert.assertEquals(
					virtualHost,
					_virtualHostLocalService.getVirtualHost(entry.getKey()));
				Assert.assertEquals(
					virtualHost,
					_virtualHostLocalService.getVirtualHost(entry.getValue()));
			}

			String hostname = _randomHostname();

			Assert.assertNull(
				_virtualHostLocalService.fetchVirtualHost(hostname));

			try {
				_virtualHostLocalService.getVirtualHost(hostname);

				Assert.fail();
			}
			catch (NoSuchVirtualHostException noSuchVirtualHostException) {
				Assert.assertEquals(
					"{hostname=" + hostname + "}",
					noSuchVirtualHostException.getMessage());
			}
		}
		finally {
			_groupLocalService.deleteGroup(group);
		}
	}

	@Test
	public void testUpdateCompanyVirtualHost() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		String originalHostname = company.getVirtualHostname();

		try {
			VirtualHost extraVirtualHost =
				_virtualHostLocalService.createVirtualHost(
					RandomTestUtil.nextLong());

			extraVirtualHost.setCompanyId(company.getCompanyId());
			extraVirtualHost.setHostname(_randomHostname());

			_virtualHostLocalService.addVirtualHost(extraVirtualHost);

			List<VirtualHost> virtualHosts =
				_virtualHostLocalService.getVirtualHosts(
					company.getCompanyId(), 0);

			Assert.assertEquals(
				virtualHosts.toString(), 2, virtualHosts.size());

			String hostname = _randomHostname();

			VirtualHost virtualHost =
				_virtualHostLocalService.updateCompanyVirtualHost(
					company.getCompanyId(), hostname);

			Assert.assertEquals(
				List.of(virtualHost),
				_virtualHostLocalService.getVirtualHosts(
					company.getCompanyId(), 0));

			Assert.assertEquals(
				company.getCompanyId(), virtualHost.getCompanyId());
			Assert.assertEquals(hostname, virtualHost.getHostname());
			Assert.assertEquals(StringPool.BLANK, virtualHost.getLanguageId());
			Assert.assertEquals(0, virtualHost.getLayoutSetId());
			Assert.assertTrue(virtualHost.isDefaultVirtualHost());

			Assert.assertNull(
				_virtualHostLocalService.fetchVirtualHost(originalHostname));
			Assert.assertNull(
				_virtualHostLocalService.fetchVirtualHost(
					extraVirtualHost.getHostname()));

			company = _companyLocalService.getCompany(company.getCompanyId());

			Assert.assertEquals(hostname, company.getVirtualHostname());
		}
		finally {
			_companyLocalService.deleteCompany(company);
		}
	}

	@Test
	public void testUpdateVirtualHosts() throws Exception {
		Group group1 = GroupTestUtil.addGroup();
		Group group2 = GroupTestUtil.addGroup();

		try {
			LayoutSet layoutSet1 = group1.getPublicLayoutSet();

			String hostname = _randomHostname();

			_virtualHostLocalService.updateVirtualHosts(
				layoutSet1,
				TreeMapBuilder.put(
					hostname, StringPool.BLANK
				).build());

			LayoutSet layoutSet2 = group2.getPublicLayoutSet();

			try {
				_virtualHostLocalService.updateVirtualHosts(
					layoutSet2,
					TreeMapBuilder.put(
						hostname, StringPool.BLANK
					).build());

				Assert.fail();
			}
			catch (LayoutSetVirtualHostException
						layoutSetVirtualHostException) {

				Assert.assertNotNull(layoutSetVirtualHostException);
			}

			Company company = _companyLocalService.getCompany(
				group2.getCompanyId());

			try {
				_virtualHostLocalService.updateVirtualHosts(
					layoutSet2,
					TreeMapBuilder.put(
						company.getVirtualHostname(), StringPool.BLANK
					).build());

				Assert.fail();
			}
			catch (LayoutSetVirtualHostException
						layoutSetVirtualHostException) {

				Assert.assertNotNull(layoutSetVirtualHostException);
			}

			Assert.assertEquals(
				Collections.emptyList(),
				_virtualHostLocalService.getVirtualHosts(
					layoutSet2.getCompanyId(), layoutSet2.getLayoutSetId()));

			Assert.assertNull(
				_virtualHostLocalService.updateVirtualHosts(
					layoutSet1, new TreeMap<>()));
			Assert.assertNull(
				_virtualHostLocalService.fetchVirtualHost(hostname));
		}
		finally {
			_groupLocalService.deleteGroup(group1);
			_groupLocalService.deleteGroup(group2);
		}
	}

	private String _randomHostname() {
		return StringUtil.toLowerCase(RandomTestUtil.randomString()) + ".test";
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private VirtualHostLocalService _virtualHostLocalService;

}
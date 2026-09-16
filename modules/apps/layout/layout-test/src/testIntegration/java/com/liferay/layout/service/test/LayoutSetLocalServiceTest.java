/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.LayoutSetJavaScriptException;
import com.liferay.portal.kernel.exception.LayoutSetVirtualHostException;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.NavigableMap;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class LayoutSetLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testDeleteGroupDeletesVirtualHosts() throws Exception {
		String virtualHostname = StringUtil.toLowerCase(
			RandomTestUtil.randomString() + StringPool.PERIOD +
				RandomTestUtil.randomString(3));

		_layoutSetLocalService.updateVirtualHosts(
			_group.getGroupId(), false,
			TreeMapBuilder.put(
				virtualHostname, StringPool.BLANK
			).build());

		Assert.assertEquals(
			_group.getCompanyId(), _fetchVirtualHostCompanyId(virtualHostname));

		_groupLocalService.deleteGroup(_group);

		_company1 = CompanyTestUtil.addCompany();

		_companyLocalService.updateCompany(
			_company1.getCompanyId(), virtualHostname, _company1.getMx(),
			_company1.getMaxUsers(), _company1.isActive());

		Assert.assertEquals(
			_company1.getCompanyId(),
			_fetchVirtualHostCompanyId(virtualHostname));
	}

	@Test(expected = LayoutSetJavaScriptException.class)
	public void testUpdateLayoutSetWithJavaScriptIvalidValue1()
		throws Exception {

		_layoutSetLocalService.updateSettings(
			_group.getGroupId(), false,
			UnicodePropertiesBuilder.put(
				"javascript", "<script>"
			).buildString());
	}

	@Test(expected = LayoutSetJavaScriptException.class)
	public void testUpdateLayoutSetWithJavaScriptIvalidValue2()
		throws Exception {

		_layoutSetLocalService.updateSettings(
			_group.getGroupId(), false,
			UnicodePropertiesBuilder.put(
				"javascript", "</script>"
			).buildString());
	}

	@Test
	public void testUpdateVirtualHostsInvalidatesCachedVirtualHostnames()
		throws Exception {

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			_group.getGroupId(), false);

		Assert.assertTrue(MapUtil.isEmpty(layoutSet.getVirtualHostnames()));

		String virtualHostname = StringUtil.toLowerCase(
			RandomTestUtil.randomString() + StringPool.PERIOD +
				RandomTestUtil.randomString(3));

		_layoutSetLocalService.updateVirtualHosts(
			_group.getGroupId(), false,
			TreeMapBuilder.put(
				virtualHostname, StringPool.BLANK
			).build());

		layoutSet = _layoutSetLocalService.getLayoutSet(
			_group.getGroupId(), false);

		NavigableMap<String, String> virtualHostnames =
			layoutSet.getVirtualHostnames();

		Assert.assertEquals(
			virtualHostnames.toString(), 1, virtualHostnames.size());
		Assert.assertTrue(virtualHostnames.containsKey(virtualHostname));
	}

	@Test
	public void testUpdateVirtualHostsWithDuplicateVirtualHostname()
		throws Exception {

		_company1 = CompanyTestUtil.addCompany();

		String virtualHostname1 = StringUtil.toLowerCase(
			StringBundler.concat(
				"a", RandomTestUtil.randomString(), StringPool.PERIOD,
				RandomTestUtil.randomString(3)));

		String virtualHostname2 = StringUtil.toLowerCase(
			StringBundler.concat(
				"b", RandomTestUtil.randomString(), StringPool.PERIOD,
				RandomTestUtil.randomString(3)));

		_companyLocalService.updateCompany(
			_company1.getCompanyId(), virtualHostname2, _company1.getMx(),
			_company1.getMaxUsers(), _company1.isActive());

		try {
			_layoutSetLocalService.updateVirtualHosts(
				_group.getGroupId(), false,
				TreeMapBuilder.put(
					virtualHostname1, StringPool.BLANK
				).put(
					virtualHostname2, StringPool.BLANK
				).build());

			Assert.fail();
		}
		catch (LayoutSetVirtualHostException layoutSetVirtualHostException) {
		}

		_company2 = CompanyTestUtil.addCompany();

		_companyLocalService.updateCompany(
			_company2.getCompanyId(), virtualHostname1, _company2.getMx(),
			_company2.getMaxUsers(), _company2.isActive());

		Assert.assertEquals(
			_company2.getCompanyId(),
			_fetchVirtualHostCompanyId(virtualHostname1));
	}

	@Test
	public void testUpdateVirtualHostsWithEmptyVirtualHostnames()
		throws Exception {

		String virtualHostname = StringUtil.toLowerCase(
			RandomTestUtil.randomString() + StringPool.PERIOD +
				RandomTestUtil.randomString(3));

		_layoutSetLocalService.updateVirtualHosts(
			_group.getGroupId(), false,
			TreeMapBuilder.put(
				virtualHostname, StringPool.BLANK
			).build());

		Assert.assertEquals(
			_group.getCompanyId(), _fetchVirtualHostCompanyId(virtualHostname));

		_layoutSetLocalService.updateVirtualHosts(
			_group.getGroupId(), false, new TreeMap<String, String>());

		_company1 = CompanyTestUtil.addCompany();

		_companyLocalService.updateCompany(
			_company1.getCompanyId(), virtualHostname, _company1.getMx(),
			_company1.getMaxUsers(), _company1.isActive());

		Assert.assertEquals(
			_company1.getCompanyId(),
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

	@DeleteAfterTestRun
	private Company _company1;

	@DeleteAfterTestRun
	private Company _company2;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private VirtualHostLocalService _virtualHostLocalService;

}
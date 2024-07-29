/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.internal.configuration.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Raymond Augé
 */
@RunWith(Arquillian.class)
public class PortalInstancesConfigurationFactoryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), SynchronousMailTestRule.INSTANCE);

	@Test
	public void test() throws Exception {
		String webId = RandomTestUtil.randomString();

		Configuration configuration =
			_configurationAdmin.getFactoryConfiguration(
				"com.liferay.portal.instances.internal.configuration." +
					"PortalInstancesConfiguration",
				webId, StringPool.QUESTION);

		ConfigurationTestUtil.saveConfiguration(
			configuration,
			HashMapDictionaryBuilder.<String, Object>put(
				"mx", webId.concat(".foo.bar")
			).put(
				"virtualHostname", webId.concat(".foo.bar")
			).build());

		_company = _companyLocalService.getCompanyByWebId(webId);

		Assert.assertNotNull(_company);

		ConfigurationTestUtil.deleteConfiguration(configuration);
	}

	@Test
	public void testAdminUserCreationWithAllProperties() throws Exception {
		String webId = RandomTestUtil.randomString();
		String tld = ".foo.bar";

		String adminEmailAddress = "testAdminEmailAddress@" + webId.concat(tld);

		Configuration configuration =
			_configurationAdmin.getFactoryConfiguration(
				"com.liferay.portal.instances.internal.configuration." +
					"PortalInstancesConfiguration",
				webId, StringPool.QUESTION);

		ConfigurationTestUtil.saveConfiguration(
			configuration,
			HashMapDictionaryBuilder.<String, Object>put(
				"adminEmailAddress", adminEmailAddress
			).put(
				"adminFirstName", "testAdminFirstName"
			).put(
				"adminLastName", "testAdminLastName"
			).put(
				"adminMiddleName", "testAdminMiddleName"
			).put(
				"adminPassword", RandomTestUtil.randomString()
			).put(
				"adminScreenName", "testAdminScreenName"
			).put(
				"mx", webId.concat(tld)
			).put(
				"virtualHostname", webId.concat(tld)
			).build());

		_company = _companyLocalService.getCompanyByWebId(webId);

		Assert.assertNotNull(_company);

		_adminUser = _userLocalService.getUserByEmailAddress(
			_company.getCompanyId(), adminEmailAddress);

		Assert.assertNotNull(_adminUser);

		Assert.assertEquals(
			StringUtil.toLowerCase(adminEmailAddress),
			_adminUser.getEmailAddress());

		ConfigurationTestUtil.deleteConfiguration(configuration);
	}

	@Test
	public void testAdminUserCreationWithDefaultProperties() throws Exception {
		String webId = RandomTestUtil.randomString();
		String tld = ".foo.bar";
		String defaultAdminScreenName = PropsUtil.get(
			PropsKeys.DEFAULT_ADMIN_SCREEN_NAME);
		String defaultAdminEmailAddress =
			PropsUtil.get(PropsKeys.DEFAULT_ADMIN_EMAIL_ADDRESS_PREFIX) + "@" +
				webId.concat(tld);
		String defaultAdminFirstName = PropsUtil.get(
			PropsKeys.DEFAULT_ADMIN_FIRST_NAME);
		String defaultAdminMiddleName = PropsUtil.get(
			PropsKeys.DEFAULT_ADMIN_MIDDLE_NAME);
		String defaultAdminLastName = PropsUtil.get(
			PropsKeys.DEFAULT_ADMIN_LAST_NAME);

		Configuration configuration =
			_configurationAdmin.getFactoryConfiguration(
				"com.liferay.portal.instances.internal.configuration." +
					"PortalInstancesConfiguration",
				webId, StringPool.QUESTION);

		ConfigurationTestUtil.saveConfiguration(
			configuration,
			HashMapDictionaryBuilder.<String, Object>put(
				"mx", webId.concat(tld)
			).put(
				"virtualHostname", webId.concat(tld)
			).build());

		_company = _companyLocalService.getCompanyByWebId(webId);

		Assert.assertNotNull(_company);

		_adminUser = _userLocalService.getUserByEmailAddress(
			_company.getCompanyId(), defaultAdminEmailAddress);

		Assert.assertNotNull(_adminUser);

		Assert.assertEquals(
			StringUtil.toLowerCase(defaultAdminScreenName),
			_adminUser.getScreenName());

		Assert.assertEquals(
			StringUtil.toLowerCase(defaultAdminEmailAddress),
			_adminUser.getEmailAddress());

		Assert.assertEquals(defaultAdminFirstName, _adminUser.getFirstName());

		Assert.assertEquals(defaultAdminMiddleName, _adminUser.getMiddleName());

		Assert.assertEquals(defaultAdminLastName, _adminUser.getLastName());

		ConfigurationTestUtil.deleteConfiguration(configuration);
	}

	@Test
	public void testAdminUserCreationWithPartialProperties() throws Exception {
		String webId = RandomTestUtil.randomString();
		String tld = ".foo.bar";
		String defaultAdminScreenName = PropsUtil.get(
			PropsKeys.DEFAULT_ADMIN_SCREEN_NAME);
		String defaultAdminEmailAddress =
			PropsUtil.get(PropsKeys.DEFAULT_ADMIN_EMAIL_ADDRESS_PREFIX) + "@" +
				webId.concat(tld);

		Configuration configuration =
			_configurationAdmin.getFactoryConfiguration(
				"com.liferay.portal.instances.internal.configuration." +
					"PortalInstancesConfiguration",
				webId, StringPool.QUESTION);

		ConfigurationTestUtil.saveConfiguration(
			configuration,
			HashMapDictionaryBuilder.<String, Object>put(
				"adminFirstName", "testAdminFirstName"
			).put(
				"adminLastName", "testAdminLastName"
			).put(
				"adminPassword", RandomTestUtil.randomString()
			).put(
				"mx", webId.concat(tld)
			).put(
				"virtualHostname", webId.concat(tld)
			).build());

		_company = _companyLocalService.getCompanyByWebId(webId);

		Assert.assertNotNull(_company);

		_adminUser = _userLocalService.getUserByEmailAddress(
			_company.getCompanyId(), defaultAdminEmailAddress);

		Assert.assertNotNull(_adminUser);

		Assert.assertEquals(
			StringUtil.toLowerCase(defaultAdminScreenName),
			_adminUser.getScreenName());

		Assert.assertEquals(
			StringUtil.toLowerCase(defaultAdminEmailAddress),
			_adminUser.getEmailAddress());

		ConfigurationTestUtil.deleteConfiguration(configuration);
	}

	@DeleteAfterTestRun
	private User _adminUser;

	@DeleteAfterTestRun
	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private UserLocalService _userLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.production.readiness.ignore.model.IgnoredRule;
import com.liferay.production.readiness.ignore.service.IgnoredRuleLocalService;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lily Chi
 */
@RunWith(Arquillian.class)
public class IgnoredRuleLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_company = CompanyTestUtil.addCompany();

		_adminUser = UserTestUtil.addCompanyAdminUser(_company);
		_regularUser = UserTestUtil.addUser(_company);
	}

	@After
	public void tearDown() throws Exception {
		for (IgnoredRule ignoredRule :
				_ignoredRuleLocalService.getIgnoredRules(
					_company.getCompanyId())) {

			_ignoredRuleLocalService.deleteIgnoredRule(ignoredRule);
		}
	}

	@Test
	public void testAddAndFetch() throws Exception {
		String ruleKey = RandomTestUtil.randomString();
		String reason = RandomTestUtil.randomString();

		IgnoredRule ignoredRule = _ignoredRuleLocalService.addIgnoredRule(
			_adminUser.getUserId(), _company.getCompanyId(), ruleKey, reason);

		Assert.assertEquals(
			_company.getCompanyId(), ignoredRule.getCompanyId());
		Assert.assertEquals(_adminUser.getUserId(), ignoredRule.getUserId());
		Assert.assertEquals(
			_adminUser.getFullName(), ignoredRule.getUserName());
		Assert.assertEquals(ruleKey, ignoredRule.getRuleKey());
		Assert.assertEquals(reason, ignoredRule.getReason());
		Assert.assertNotNull(ignoredRule.getCreateDate());

		IgnoredRule fetchedIgnoredRule =
			_ignoredRuleLocalService.fetchIgnoredRule(
				_company.getCompanyId(), ruleKey);

		Assert.assertNotNull(fetchedIgnoredRule);
		Assert.assertEquals(
			ignoredRule.getIgnoredRuleId(),
			fetchedIgnoredRule.getIgnoredRuleId());
	}

	@Test
	public void testDeleteRemovesRow() throws Exception {
		String ruleKey = RandomTestUtil.randomString();

		_ignoredRuleLocalService.addIgnoredRule(
			_adminUser.getUserId(), _company.getCompanyId(), ruleKey, null);

		_ignoredRuleLocalService.deleteIgnoredRule(
			_adminUser.getUserId(), _company.getCompanyId(), ruleKey);

		Assert.assertNull(
			_ignoredRuleLocalService.fetchIgnoredRule(
				_company.getCompanyId(), ruleKey));
	}

	@Test
	public void testGetIgnoredRulesByCompany() throws Exception {
		_ignoredRuleLocalService.addIgnoredRule(
			_adminUser.getUserId(), _company.getCompanyId(), "rule-a", null);
		_ignoredRuleLocalService.addIgnoredRule(
			_adminUser.getUserId(), _company.getCompanyId(), "rule-b", null);

		List<IgnoredRule> ignoredRules =
			_ignoredRuleLocalService.getIgnoredRules(_company.getCompanyId());

		Assert.assertEquals(ignoredRules.toString(), 2, ignoredRules.size());
	}

	@Test(expected = PrincipalException.MustBeCompanyAdmin.class)
	public void testNonadminCannotAdd() throws Exception {
		_ignoredRuleLocalService.addIgnoredRule(
			_regularUser.getUserId(), _company.getCompanyId(),
			RandomTestUtil.randomString(), null);
	}

	@Test(expected = PrincipalException.MustBeCompanyAdmin.class)
	public void testNonadminCannotDelete() throws Exception {
		String ruleKey = RandomTestUtil.randomString();

		_ignoredRuleLocalService.addIgnoredRule(
			_adminUser.getUserId(), _company.getCompanyId(), ruleKey, null);

		_ignoredRuleLocalService.deleteIgnoredRule(
			_regularUser.getUserId(), _company.getCompanyId(), ruleKey);
	}

	private User _adminUser;
	private Company _company;

	@Inject
	private IgnoredRuleLocalService _ignoredRuleLocalService;

	private User _regularUser;

}
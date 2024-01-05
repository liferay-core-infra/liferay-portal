/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.membership.policy.organization.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.security.membershippolicy.OrganizationMembershipPolicy;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.security.membership.policy.organization.BaseOrganizationMembershipPolicyTestCase;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class OrganizationMembershipPolicyBasicTest
	extends BaseOrganizationMembershipPolicyTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testIsMembershipAllowed() throws Exception {
		long[] userIds = addUsers();
		long[] standardOrganizationIds = addStandardOrganizations();

		Assert.assertTrue(
			_organizationMembershipPolicy.isMembershipAllowed(
				userIds[0], standardOrganizationIds[0]));
	}

	@Test
	public void testIsMembershipNotAllowed() throws Exception {
		long[] userIds = addUsers();
		long[] forbiddenOrganizationIds = addForbiddenOrganizations();

		Assert.assertFalse(
			_organizationMembershipPolicy.isMembershipAllowed(
				userIds[0], forbiddenOrganizationIds[0]));
	}

	@Test
	public void testIsMembershipNotRequired() throws Exception {
		long[] userIds = addUsers();
		long[] standardOrganizationIds = addStandardOrganizations();

		Assert.assertFalse(
			_organizationMembershipPolicy.isMembershipRequired(
				userIds[0], standardOrganizationIds[0]));
	}

	@Test
	public void testIsMembershipRequired() throws Exception {
		long[] userIds = addUsers();
		long[] requiredOrganizationIds = addRequiredOrganizations();

		Assert.assertTrue(
			_organizationMembershipPolicy.isMembershipRequired(
				userIds[0], requiredOrganizationIds[0]));
	}

	@Test
	public void testIsRoleAllowed() throws Exception {
		long[] userIds = addUsers();
		long[] standardOrganizationIds = addStandardOrganizations();
		long[] standardRoleIds = addStandardRoles();

		Assert.assertTrue(
			_organizationMembershipPolicy.isRoleAllowed(
				userIds[0], standardOrganizationIds[0], standardRoleIds[0]));
	}

	@Test
	public void testIsRoleNotAllowed() throws Exception {
		long[] userIds = addUsers();
		long[] standardOrganizationIds = addStandardOrganizations();
		long[] forbiddenRoleIds = addForbiddenRoles();

		Assert.assertFalse(
			_organizationMembershipPolicy.isRoleAllowed(
				userIds[0], standardOrganizationIds[0], forbiddenRoleIds[0]));
	}

	@Test
	public void testIsRoleNotRequired() throws Exception {
		long[] userIds = addUsers();
		long[] standardOrganizationIds = addStandardOrganizations();
		long[] standardRoleIds = addStandardRoles();

		Assert.assertFalse(
			_organizationMembershipPolicy.isRoleRequired(
				userIds[0], standardOrganizationIds[0], standardRoleIds[0]));
	}

	@Test
	public void testIsRoleRequired() throws Exception {
		long[] userIds = addUsers();
		long[] standardOrganizationIds = addStandardOrganizations();
		long[] requiredRoleIds = addRequiredRoles();

		Assert.assertTrue(
			_organizationMembershipPolicy.isRoleRequired(
				userIds[0], standardOrganizationIds[0], requiredRoleIds[0]));
	}

	@Inject
	private OrganizationMembershipPolicy _organizationMembershipPolicy;

}
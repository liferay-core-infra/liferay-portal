/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.membershippolicy;

import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.membershippolicy.OrganizationMembershipPolicy;
import com.liferay.portal.kernel.security.membershippolicy.RoleMembershipPolicy;
import com.liferay.portal.kernel.security.membershippolicy.SiteMembershipPolicy;
import com.liferay.portal.kernel.security.membershippolicy.UserGroupMembershipPolicy;
import com.liferay.portal.kernel.util.ProxyFactory;

/**
 * @author Janis Zhang
 */
public class MembershipPolicyUtil {

	public static OrganizationMembershipPolicy
		getOrganizationMembershipPolicy() {

		OrganizationMembershipPolicy organizationMembershipPolicy =
			_organizationMembershipPolicySnapshot.get();

		if (organizationMembershipPolicy != null) {
			return organizationMembershipPolicy;
		}

		return _dummyOrganizationMembershipPolicy;
	}

	public static RoleMembershipPolicy getRoleMembershipPolicy() {
		RoleMembershipPolicy roleMembershipPolicy =
			_roleMembershipPolicySnapshot.get();

		if (roleMembershipPolicy != null) {
			return roleMembershipPolicy;
		}

		return _dummyRoleMembershipPolicy;
	}

	public static SiteMembershipPolicy getSiteMembershipPolicy() {
		SiteMembershipPolicy siteMembershipPolicy =
			_siteMembershipPolicySnapshot.get();

		if (siteMembershipPolicy != null) {
			return siteMembershipPolicy;
		}

		return _defaultSiteMembershipPolicy;
	}

	public static UserGroupMembershipPolicy getUserGroupMembershipPolicy() {
		UserGroupMembershipPolicy userGroupMembershipPolicy =
			_userGroupMembershipPolicySnapshot.get();

		if (userGroupMembershipPolicy != null) {
			return userGroupMembershipPolicy;
		}

		return _dummyUserGroupMembershipPolicy;
	}

	private static final SiteMembershipPolicy _defaultSiteMembershipPolicy =
		new DefaultSiteMembershipPolicy();
	private static final OrganizationMembershipPolicy
		_dummyOrganizationMembershipPolicy = ProxyFactory.newDummyInstance(
			OrganizationMembershipPolicy.class);
	private static final RoleMembershipPolicy _dummyRoleMembershipPolicy =
		ProxyFactory.newDummyInstance(RoleMembershipPolicy.class);
	private static final UserGroupMembershipPolicy
		_dummyUserGroupMembershipPolicy = ProxyFactory.newDummyInstance(
			UserGroupMembershipPolicy.class);
	private static final Snapshot<OrganizationMembershipPolicy>
		_organizationMembershipPolicySnapshot = new Snapshot<>(
			MembershipPolicyUtil.class, OrganizationMembershipPolicy.class,
			null, true);
	private static final Snapshot<RoleMembershipPolicy>
		_roleMembershipPolicySnapshot = new Snapshot<>(
			MembershipPolicyUtil.class, RoleMembershipPolicy.class, null, true);
	private static final Snapshot<SiteMembershipPolicy>
		_siteMembershipPolicySnapshot = new Snapshot<>(
			MembershipPolicyUtil.class, SiteMembershipPolicy.class, null, true);
	private static final Snapshot<UserGroupMembershipPolicy>
		_userGroupMembershipPolicySnapshot = new Snapshot<>(
			MembershipPolicyUtil.class, UserGroupMembershipPolicy.class, null,
			true);

}
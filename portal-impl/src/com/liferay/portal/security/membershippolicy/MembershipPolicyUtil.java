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

/**
 * @author Janis Zhang
 */
public class MembershipPolicyUtil {

	public static OrganizationMembershipPolicy
		getOrganizationMembershipPolicy() {

		return _organizationMembershipPolicySnapshot.get();
	}

	public static RoleMembershipPolicy getRoleMembershipPolicy() {
		return _roleMembershipPolicySnapshot.get();
	}

	public static SiteMembershipPolicy getSiteMembershipPolicy() {
		return _siteMembershipPolicySnapshot.get();
	}

	public static UserGroupMembershipPolicy getUserGroupMembershipPolicy() {
		return _userGroupMembershipPolicySnapshot.get();
	}

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
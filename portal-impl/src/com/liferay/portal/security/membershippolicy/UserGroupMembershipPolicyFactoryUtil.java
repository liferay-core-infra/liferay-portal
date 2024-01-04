/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.membershippolicy;

import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.membershippolicy.UserGroupMembershipPolicy;

/**
 * @author Roberto Díaz
 */
public class UserGroupMembershipPolicyFactoryUtil {

	public static UserGroupMembershipPolicy getUserGroupMembershipPolicy() {
		return _userGroupMembershipPolicySnapshot.get();
	}

	private static final Snapshot<UserGroupMembershipPolicy>
		_userGroupMembershipPolicySnapshot = new Snapshot<>(
			UserGroupMembershipPolicyFactoryUtil.class,
			UserGroupMembershipPolicy.class, null, true);

}
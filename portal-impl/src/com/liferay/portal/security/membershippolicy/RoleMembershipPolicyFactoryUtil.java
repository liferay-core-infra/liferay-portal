/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.membershippolicy;

import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.membershippolicy.RoleMembershipPolicy;

/**
 * @author Roberto Díaz
 */
public class RoleMembershipPolicyFactoryUtil {

	public static RoleMembershipPolicy getRoleMembershipPolicy() {
		return _roleMembershipPolicySnapshot.get();
	}

	private static final Snapshot<RoleMembershipPolicy>
		_roleMembershipPolicySnapshot = new Snapshot<>(
			RoleMembershipPolicyFactoryUtil.class, RoleMembershipPolicy.class,
			null, true);

}
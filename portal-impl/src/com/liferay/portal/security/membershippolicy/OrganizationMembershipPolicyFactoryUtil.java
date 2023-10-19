/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.membershippolicy;

import com.liferay.portal.kernel.security.membershippolicy.OrganizationMembershipPolicy;
import com.liferay.portal.kernel.util.ServiceProxyFactory;

/**
 * @author Roberto Díaz
 * @author Sergio González
 * @author Shuyang Zhou
 * @author Peter Fellwock
 */
public class OrganizationMembershipPolicyFactoryUtil {

	public static OrganizationMembershipPolicy
		getOrganizationMembershipPolicy() {

		return _organizationMembershipPolicy;
	}

	private static volatile OrganizationMembershipPolicy
		_organizationMembershipPolicy =
			ServiceProxyFactory.newServiceTrackedInstance(
				OrganizationMembershipPolicy.class,
				OrganizationMembershipPolicyFactoryUtil.class,
				"_organizationMembershipPolicy", false, true);

}
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.account.admin.web.internal.security.permission.helper;

import com.liferay.account.admin.web.internal.security.permission.resource.AccountEntryPermission;
import com.liferay.account.admin.web.internal.security.permission.resource.AccountGroupPermission;
import com.liferay.account.admin.web.internal.security.permission.resource.AccountPermission;
import com.liferay.account.admin.web.internal.security.permission.resource.AccountRolePermission;
import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.model.AccountRole;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.OrganizationLocalService;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Lily Chi
 */
@Component(service = {})
public class AccountPermissionHelper {

	@Activate
	protected void activate() {
		AccountEntryPermission.setModelResourcePermission(
			_accountEntryModelResourcePermission);
		AccountGroupPermission.setModelResourcePermission(
			_accountGroupModelResourcePermission);
		AccountPermission.setOrganizationLocalService(
			_organizationLocalService);
		AccountPermission.setPortletResourcePermission(
			_portletResourcePermission);
		AccountRolePermission.setModelResourcePermission(
			_accountRoleModelResourcePermission);
	}

	@Reference(
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.account.model.AccountGroup)"
	)
	private ModelResourcePermission<AccountGroup>
		_accountGroupModelResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.account.model.AccountRole)"
	)
	private ModelResourcePermission<AccountRole>
		_accountRoleModelResourcePermission;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference(
		target = "(resource.name=" + AccountConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}
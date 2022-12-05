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

package com.liferay.account.admin.web.internal.helper;

import com.liferay.account.admin.web.internal.dao.search.AssignableAccountUserDisplaySearchContainerFactory;
import com.liferay.account.retriever.AccountUserRetriever;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lily Chi
 */
@Component(service = {})
public class AssignableAccountUserDisplaySearchContainerFactoryHelper {

	@Activate
	protected void activate() {
		AssignableAccountUserDisplaySearchContainerFactory.
			setAccountEntryLocalService(_accountEntryLocalService);
		AssignableAccountUserDisplaySearchContainerFactory.
			setAccountEntryUserRelLocalService(
				_accountEntryUserRelLocalService);
		AssignableAccountUserDisplaySearchContainerFactory.
			setAccountRoleLocalService(_accountRoleLocalService);
		AssignableAccountUserDisplaySearchContainerFactory.
			setAccountUserRetriever(_accountUserRetriever);
		AssignableAccountUserDisplaySearchContainerFactory.
			setUserGroupRoleLocalService(_userGroupRoleLocalService);
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Reference
	private AccountRoleLocalService _accountRoleLocalService;

	@Reference
	private AccountUserRetriever _accountUserRetriever;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}
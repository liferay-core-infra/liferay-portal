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

import com.liferay.account.admin.web.internal.dao.search.AccountOrganizationSearchContainerFactory;
import com.liferay.account.retriever.AccountOrganizationRetriever;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lily Chi
 */
@Component(service = {})
public class AccountOrganizationSearchContainerFactoryHelper {

	@Activate
	protected void activate() {
		AccountOrganizationSearchContainerFactory.
			setAccountEntryOrganizationRelLocalService(
				_accountEntryOrganizationRelLocalService);

		AccountOrganizationSearchContainerFactory.
			setAccountOrganizationRetriever(_accountOrganizationRetriever);
	}

	@Reference
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Reference
	private AccountOrganizationRetriever _accountOrganizationRetriever;

}
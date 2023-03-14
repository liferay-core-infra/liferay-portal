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

package com.liferay.headless.admin.user.internal.dto.v1_0.converter.util;

import com.liferay.account.model.AccountEntry;
import com.liferay.headless.admin.user.dto.v1_0.Account;
import com.liferay.headless.admin.user.dto.v1_0.UserAccount;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;

/**
 * @author Joao Victor Alves
 */
public class DTOConverterUtil {

	public static long getAccountEntryId(
			DTOConverter<AccountEntry, Account> dtoConverter,
			String externalReferenceCode)
		throws Exception {

		AccountEntry accountEntry = dtoConverter.getObject(
			externalReferenceCode);

		return accountEntry.getAccountEntryId();
	}

	public static long getUserGroupId(
			DTOConverter
				<UserGroup, com.liferay.headless.admin.user.dto.v1_0.UserGroup>
					dtoConverter,
			String externalReferenceCode)
		throws Exception {

		UserGroup userGroup = dtoConverter.getObject(externalReferenceCode);

		return userGroup.getUserGroupId();
	}

	public static long getUserId(
			DTOConverter<User, UserAccount> dtoConverter,
			String externalReferenceCode)
		throws Exception {

		User user = dtoConverter.getObject(externalReferenceCode);

		return user.getUserId();
	}

}
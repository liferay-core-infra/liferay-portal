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

package com.liferay.account.admin.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.account.admin.web.internal.constants.AccountScreenNavigationEntryConstants;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Joao Victor Alves
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class AccountRoleDefineGroupScopePermissionsScreenNavigationEntry
	extends BaseAccountRoleDefinePermissionsScreenNavigationEntry {

	@Override
	public String getCategoryKey() {
		return AccountScreenNavigationEntryConstants.
			CATEGORY_KEY_DEFINE_GROUP_SCOPE_PERMISSIONS;
	}

	@Override
	public String getEntryKey() {
		return AccountScreenNavigationEntryConstants.
			ENTRY_KEY_DEFINE_GROUP_SCOPE_PERMISSIONS;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(getResourceBundle(locale), getCategoryKey());
	}

	@Override
	public String getScreenNavigationKey() {
		return AccountScreenNavigationEntryConstants.
			SCREEN_NAVIGATION_KEY_ACCOUNT_ROLE;
	}

	@Override
	protected String doGetTabs1() {
		return "define-group-scope-permissions";
	}

	@Override
	protected boolean doIsAccountRoleGroupScope() {
		return true;
	}

	protected ResourceBundle getResourceBundle(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return new AggregateResourceBundle(
			resourceBundle, portal.getResourceBundle(locale));
	}

	@Reference
	private Language _language;

}
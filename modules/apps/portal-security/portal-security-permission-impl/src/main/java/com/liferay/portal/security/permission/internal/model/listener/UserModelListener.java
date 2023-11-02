/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission.internal.model.listener;

import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.security.permission.PermissionCacheUtil;

import org.osgi.service.component.annotations.Component;

/**
 * @author Preston Crary
 */
@Component(service = ModelListener.class)
public class UserModelListener extends BaseModelListener<User> {

	@Override
	public void onAfterAddAssociation(
		Object classPK, String associationClassName,
		Object associationClassPK) {

		PermissionCacheUtil.clearCache((long)classPK);
	}

	@Override
	public void onAfterRemove(User user) {
		if (user != null) {
			PermissionCacheUtil.clearCache(user.getUserId());
		}
	}

	@Override
	public void onAfterRemoveAssociation(
		Object classPK, String associationClassName,
		Object associationClassPK) {

		PermissionCacheUtil.clearCache((long)classPK);
	}

}
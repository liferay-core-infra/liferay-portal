/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.sender.internal.model.listener;

import com.liferay.analytics.message.sender.model.listener.EntityModel;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ContactLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rachael Koestartyo
 */
@Component(service = ModelListener.class)
public class ContactModelListener extends BaseAnalyticsModelListener<Contact> {

	@Override
	protected EntityModel<Contact> getEntityModelListener() {
		return _contactEntityModel;
	}

	@Override
	protected Contact getModel(long id) throws Exception {
		return _contactLocalService.getContact(id);
	}

	@Override
	protected String getPrimaryKeyName() {
		return "contactId";
	}

	@Override
	protected boolean isExcluded(Contact contact) {
		User user = userLocalService.fetchUser(contact.getClassPK());

		if (!isUserActive(user)) {
			return true;
		}

		return isUserExcluded(user);
	}

	@Reference(target = "(entity.model.type=contact)")
	private EntityModel<Contact> _contactEntityModel;

	@Reference
	private ContactLocalService _contactLocalService;

}
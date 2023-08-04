/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.sender.internal.entity.model;

import com.liferay.analytics.message.sender.internal.model.listener.BaseModelListener;
import com.liferay.analytics.message.sender.model.AnalyticsMessage;
import com.liferay.analytics.message.sender.model.listener.EntityModel;
import com.liferay.expando.kernel.model.ExpandoRow;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ShardedModel;
import com.liferay.portal.kernel.model.User;

import java.nio.charset.Charset;

import java.util.List;

/**
 * @author Joao Victor Alves
 */
public abstract class BaseEntityModel<T extends BaseModel<T>>
	extends BaseModelListener<T> implements EntityModel<T> {

	@Override
	public void addAnalyticsMessage(
		String eventType, List<String> includeAttributeNames, T model) {

		String modelClassName = model.getModelClassName();

		if (modelClassName.equals(Contact.class.getName())) {
			Contact contact = (Contact)model;

			User user = userLocalService.fetchUser(contact.getClassPK());

			if ((!StringUtil.equalsIgnoreCase(eventType, "delete") &&
				 !isUserActive(user)) ||
				isUserExcluded(user)) {

				return;
			}
		}
		else if (modelClassName.equals(User.class.getName())) {
			User user = (User)model;

			if ((!StringUtil.equalsIgnoreCase(eventType, "delete") &&
				 !isUserActive(user)) ||
				isUserExcluded(user)) {

				return;
			}
		}
		else if (isExcluded(model)) {
			return;
		}

		JSONObject jsonObject = serialize(model, includeAttributeNames);

		ShardedModel shardedModel = (ShardedModel)model;

		if (modelClassName.equals(ExpandoRow.class.getName())) {
			ExpandoRow expandoRow = (ExpandoRow)model;

			if (isCustomField(
					Organization.class.getName(), expandoRow.getTableId())) {

				modelClassName = Organization.class.getName();
			}
			else {
				modelClassName = User.class.getName();
			}
		}

		try {
			AnalyticsMessage.Builder analyticsMessageBuilder =
				AnalyticsMessage.builder(modelClassName);

			analyticsMessageBuilder.action(eventType);
			analyticsMessageBuilder.object(jsonObject);

			String analyticsMessageJSON =
				analyticsMessageBuilder.buildJSONString();

			analyticsMessageLocalService.addAnalyticsMessage(
				shardedModel.getCompanyId(),
				userLocalService.getGuestUserId(shardedModel.getCompanyId()),
				analyticsMessageJSON.getBytes(Charset.defaultCharset()));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to add analytics message " + jsonObject.toString(),
					exception);
			}
		}
	}

	@Override
	public long[] getMembershipIds(User user) throws Exception {
		return new long[0];
	}

	@Override
	public String getModelClassName() {
		return null;
	}

	@Override
	public void syncAll(long companyId) throws Exception {
		ActionableDynamicQuery actionableDynamicQuery =
			getActionableDynamicQuery();

		if (actionableDynamicQuery == null) {
			return;
		}

		actionableDynamicQuery.setCompanyId(companyId);
		actionableDynamicQuery.setPerformActionMethod(
			(T model) -> addAnalyticsMessage(
				"add", getAttributeNames(companyId), model));

		actionableDynamicQuery.performActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseEntityModel.class);

}
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.sender.internal.entity.model;

import com.liferay.analytics.message.sender.internal.helper.AnalyticsModelHelper;
import com.liferay.analytics.message.sender.model.listener.EntityModel;
import com.liferay.expando.kernel.model.ExpandoRow;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.OrganizationLocalService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Joao Victor Alves
 */
@Component(
	property = "entity.model.type=expandorow", service = EntityModel.class
)
public class ExpandoRowEntityModel extends BaseEntityModel<ExpandoRow> {

	@Override
	public List<String> getAttributeNames(long companyId) {
		return Collections.singletonList("modifiedDate");
	}

	@Override
	protected ExpandoRow getModel(long id) throws Exception {
		return _expandoRowLocalService.getExpandoRow(id);
	}

	@Override
	protected String getPrimaryKeyName() {
		return "classPK";
	}

	@Override
	protected boolean isExcluded(ExpandoRow expandoRow) {
		if (analyticsModelHelper.isCustomField(
				Organization.class.getName(), expandoRow.getTableId())) {

			return false;
		}

		if (analyticsModelHelper.isCustomField(
				User.class.getName(), expandoRow.getTableId())) {

			User user = userLocalService.fetchUser(expandoRow.getClassPK());

			if (!analyticsModelHelper.isUserActive(user)) {
				return true;
			}

			return analyticsModelHelper.isUserExcluded(user);
		}

		return true;
	}

	@Override
	protected JSONObject serialize(
		BaseModel<?> baseModel, List<String> includeAttributeNames) {

		ExpandoRow expandoRow = (ExpandoRow)baseModel;

		if (_analyticsModelHelper.isCustomField(
				Organization.class.getName(), expandoRow.getTableId())) {

			Organization organization =
				_organizationLocalService.fetchOrganization(
					expandoRow.getClassPK());

			if (organization != null) {
				JSONObject jsonObject = super.serialize(
					organization, _organizationAttributeNames);

				jsonObject.remove(getPrimaryKeyName());

				return jsonObject.put(
					"organizationId", organization.getPrimaryKeyObj());
			}
		}
		else if (_analyticsModelHelper.isCustomField(
					User.class.getName(), expandoRow.getTableId())) {

			User user = userLocalService.fetchUser(expandoRow.getClassPK());

			if (user != null) {
				JSONObject jsonObject = super.serialize(
					user,
					_analyticsModelHelper.getUserAttributeNames(
						user.getCompanyId()));

				jsonObject.remove(getPrimaryKeyName());

				return jsonObject.put("userId", user.getPrimaryKeyObj());
			}
		}

		return _jsonFactory.createJSONObject();
	}

	private static final List<String> _organizationAttributeNames =
		Arrays.asList(
			"expando", "modifiedDate", "name", "parentOrganizationId",
			"treePath", "type");

	@Reference
	private AnalyticsModelHelper _analyticsModelHelper;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private OrganizationLocalService _organizationLocalService;

}
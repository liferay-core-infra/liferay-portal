/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.instance.lifecycle;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tina Tian
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class AddDefaultPermissionPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		long companyId = company.getCompanyId();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMS_DEFAULT_PERMISSION", companyId);

		if (objectDefinition == null) {
			return;
		}

		Role role = _roleLocalService.fetchRole(companyId, RoleConstants.USER);

		if (role == null) {
			return;
		}

		String className = objectDefinition.getClassName();

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				companyId, className, ResourceConstants.SCOPE_COMPANY,
				String.valueOf(companyId), role.getRoleId());

		if (resourcePermission != null) {
			return;
		}

		_resourcePermissionLocalService.addResourcePermission(
			companyId, className, ResourceConstants.SCOPE_COMPANY,
			String.valueOf(companyId), role.getRoleId(), ActionKeys.VIEW);
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}
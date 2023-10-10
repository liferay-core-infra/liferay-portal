/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.internal.settings;

import com.liferay.organizations.internal.configuration.OrganizationTypeConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.users.admin.kernel.organization.types.OrganizationTypesSettings;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Marco Leo
 */
@Component(
	configurationPid = "com.liferay.organizations.internal.configuration.OrganizationTypeConfiguration",
	service = OrganizationTypesSettings.class
)
public class OrganizationTypesSettingsImpl
	implements OrganizationTypesSettings {

	@Override
	public String[] getChildrenTypes(String type) {
		return ArrayUtil.filter(
			_organizationTypeConfiguration.childrenTypes(),
			Validator::isNotNull);
	}

	@Override
	public String[] getTypes() {
		return new String[] {_organizationTypeConfiguration.name()};
	}

	@Override
	public boolean isCountryEnabled(String type) {
		return _organizationTypeConfiguration.countryEnabled();
	}

	@Override
	public boolean isCountryRequired(String type) {
		return _organizationTypeConfiguration.countryRequired();
	}

	@Override
	public boolean isRootable(String type) {
		return _organizationTypeConfiguration.rootable();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_organizationTypeConfiguration = ConfigurableUtil.createConfigurable(
			OrganizationTypeConfiguration.class, properties);
	}

	private volatile OrganizationTypeConfiguration
		_organizationTypeConfiguration;

}
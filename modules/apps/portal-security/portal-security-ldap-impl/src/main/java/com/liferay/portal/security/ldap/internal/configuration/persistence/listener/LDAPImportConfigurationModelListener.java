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

package com.liferay.portal.security.ldap.internal.configuration.persistence.listener;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.ldap.exportimport.configuration.LDAPImportConfiguration;

import java.util.Dictionary;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mariano Álvaro Sáiz
 */
@Component(
	enabled = true,
	property = "model.class.name=com.liferay.portal.security.ldap.exportimport.configuration.LDAPImportConfiguration",
	service = ConfigurationModelListener.class
)
public class LDAPImportConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onAfterDelete(String pid) {
		if (!StringUtil.equals(pid, _LDAP_IMPORT_CONFIGURATION)) {
			return;
		}

		_updateComponentInstance(_EMPTY_PROPERTIES);
	}

	@Override
	public void onAfterSave(String pid, Dictionary<String, Object> properties) {
		if (!StringUtil.equals(pid, _LDAP_IMPORT_CONFIGURATION)) {
			return;
		}

		_updateComponentInstance(properties);
	}

	@Activate
	protected void activate(BundleContext bundleContext) throws Exception {
		Dictionary<String, Object> properties = _EMPTY_PROPERTIES;

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			StringBundler.concat(
				"(", Constants.SERVICE_PID, "=", _LDAP_IMPORT_CONFIGURATION,
				")"));

		if (configurations != null) {
			properties = configurations[0].getProperties();
		}

		_updateComponentInstance(properties);
	}

	@Deactivate
	protected void deactivate() {
		if (_componentInstance != null) {
			_componentInstance.dispose();
		}
	}

	private void _updateComponentInstance(
		Dictionary<String, Object> properties) {

		if (_componentInstance != null) {
			_componentInstance.dispose();
		}

		_componentInstance = _componentFactory.newInstance(
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", CompanyConstants.SYSTEM
			).put(
				"configuration",
				ConfigurableUtil.createConfigurable(
					LDAPImportConfiguration.class, properties)
			).build());
	}

	private static final Dictionary<String, Object> _EMPTY_PROPERTIES =
		new HashMapDictionary<>();

	private static final String _LDAP_IMPORT_CONFIGURATION =
		"com.liferay.portal.security.ldap.exportimport.configuration." +
			"LDAPImportConfiguration";

	@Reference(
		target = "(component.factory=com.liferay.portal.security.ldap.internal.scheduler.UserImportSchedulerJobConfiguration)"
	)
	private ComponentFactory _componentFactory;

	private ComponentInstance<?> _componentInstance;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

}
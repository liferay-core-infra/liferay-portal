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

package com.liferay.journal.web.internal.scheduler;

import com.liferay.journal.configuration.JournalServiceConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Dictionary;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
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
@Component(service = {})
public class CheckArticleSchedulerComponentFactory {

	@Activate
	protected void activate(BundleContext bundleContext) throws Exception {
		_serviceRegistration = bundleContext.registerService(
			ConfigurationModelListener.class,
			new JournalServiceConfigurationModelListener(),
			HashMapDictionaryBuilder.<String, Object>put(
				"model.class.name", _JOURNAL_SERVICE_CONFIGURATION
			).build());

		Dictionary<String, Object> properties = _EMPTY_PROPERTIES;

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			StringBundler.concat(
				"(", Constants.SERVICE_PID, "=", _JOURNAL_SERVICE_CONFIGURATION,
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

		_serviceRegistration.unregister();
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
					JournalServiceConfiguration.class, properties)
			).build());
	}

	private static final Dictionary<String, Object> _EMPTY_PROPERTIES =
		new HashMapDictionary<>();

	private static final String _JOURNAL_SERVICE_CONFIGURATION =
		"com.liferay.journal.configuration.JournalServiceConfiguration";

	@Reference(
		target = "(component.factory=com.liferay.journal.web.internal.scheduler.CheckArticleSchedulerJobConfiguration)"
	)
	private ComponentFactory _componentFactory;

	private ComponentInstance<?> _componentInstance;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	private ServiceRegistration<ConfigurationModelListener>
		_serviceRegistration;

	private class JournalServiceConfigurationModelListener
		implements ConfigurationModelListener {

		@Override
		public void onAfterDelete(String pid) {
			if (!StringUtil.equals(pid, _JOURNAL_SERVICE_CONFIGURATION)) {
				return;
			}

			_updateComponentInstance(_EMPTY_PROPERTIES);
		}

		@Override
		public void onAfterSave(
			String pid, Dictionary<String, Object> properties) {

			if (!StringUtil.equals(pid, _JOURNAL_SERVICE_CONFIGURATION)) {
				return;
			}

			_updateComponentInstance(properties);
		}

	}

}
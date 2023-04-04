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

package com.liferay.portal.configuration.settings.internal;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.settings.internal.scoped.configuration.admin.service.ScopedConfigurationManagedServiceFactory;
import com.liferay.portal.configuration.settings.internal.util.ConfigurationPidUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.resource.manager.ClassLoaderResourceManager;
import com.liferay.portal.kernel.settings.ConfigurationBeanSettings;
import com.liferay.portal.kernel.settings.LocationVariableResolver;
import com.liferay.portal.kernel.settings.PropertiesSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.settings.definition.ConfigurationPidMapping;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.Props;

import java.io.Serializable;

import java.lang.reflect.Method;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * @author Rafael Praxedes
 */
@Component(service = ConfigurationBeanClassSettingsRegistry.class)
public class ConfigurationBeanClassSettingsRegistry {

	public Settings getConfigurationBeanSettings(String configurationPid) {
		Class<?> configurationBeanClass = _configurationBeanClasses.get(
			configurationPid);

		if (configurationBeanClass == null) {
			return _portalPropertiesSettings;
		}

		Settings configurationBeanSettings = _configurationBeanSettings.get(
			configurationBeanClass);

		if (configurationBeanSettings == null) {
			return _portalPropertiesSettings;
		}

		return configurationBeanSettings;
	}

	public Settings getScopedConfigurationBeanSettings(
		ExtendedObjectClassDefinition.Scope scope, Serializable scopePK,
		String configurationPid, Settings parentSettings) {

		ScopedConfigurationManagedServiceFactory
			scopedConfigurationManagedServiceFactory =
				_scopedConfigurationManagedServiceFactories.get(
					configurationPid);

		if (scopedConfigurationManagedServiceFactory == null) {
			return parentSettings;
		}

		Object configurationBean =
			scopedConfigurationManagedServiceFactory.getConfiguration(
				scope, scopePK);

		if (configurationBean == null) {
			return parentSettings;
		}

		return new ConfigurationBeanSettings(
			scopedConfigurationManagedServiceFactory.
				getLocationVariableResolver(),
			configurationBean, parentSettings);
	}

	public SafeCloseable registerConfigurationBeanClass(
		Class<?> configurationBeanClass) {

		if (configurationBeanClass.getAnnotation(Meta.OCD.class) == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Skipping registration for class because Meta.OCD is " +
						"missing: " + configurationBeanClass.getName());
			}

			return null;
		}

		for (Method methods : configurationBeanClass.getMethods()) {
			Meta.AD annotation = methods.getAnnotation(Meta.AD.class);

			if (annotation == null) {
				continue;
			}

			if (annotation.required()) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Skipping registration for class because Meta.AD is " +
							"required: " + configurationBeanClass.getName());
				}

				return null;
			}
		}

		String configurationPid = ConfigurationPidUtil.getConfigurationPid(
			configurationBeanClass);

		if (_configurationBeanClasses.containsKey(configurationPid)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Skipping registration for class because it is already " +
						"registered: " + configurationPid);
			}

			return null;
		}

		LocationVariableResolver locationVariableResolver =
			new LocationVariableResolver(
				new ClassLoaderResourceManager(
					configurationBeanClass.getClassLoader()),
				serviceName -> getConfigurationBeanSettings(serviceName));

		ConfigurationBeanManagedService configurationBeanManagedService =
			new ConfigurationBeanManagedService(
				_bundleContext, configurationBeanClass,
				configurationBean -> _configurationBeanSettings.put(
					configurationBeanClass,
					new ConfigurationBeanSettings(
						locationVariableResolver, configurationBean,
						_portalPropertiesSettings)));

		configurationBeanManagedService.register();

		ScopedConfigurationManagedServiceFactory
			scopedConfigurationManagedServiceFactory =
				new ScopedConfigurationManagedServiceFactory(
					_bundleContext, configurationBeanClass,
					locationVariableResolver);

		scopedConfigurationManagedServiceFactory.register();

		_scopedConfigurationManagedServiceFactories.put(
			scopedConfigurationManagedServiceFactory.getName(),
			scopedConfigurationManagedServiceFactory);

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Registering configuration class: " +
					configurationBeanClass.getName());
		}

		_configurationBeanClasses.put(
			configurationBeanManagedService.getConfigurationPid(),
			configurationBeanClass);

		_settingsFactoryImpl.registerConfigurationBeanClass(
			configurationBeanClass);

		return () -> {
			_settingsFactoryImpl.unregisterConfigurationBeanClass(
				configurationBeanClass);

			_configurationBeanClasses.remove(configurationPid);

			_scopedConfigurationManagedServiceFactories.remove(
				configurationPid);
			scopedConfigurationManagedServiceFactory.unregister();

			_configurationBeanSettings.remove(configurationBeanClass);
			configurationBeanManagedService.unregister();
		};
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Deactivate
	protected void deactivate() {
		_bundleContext = null;
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC
	)
	protected void setConfigurationPidMapping(
		ConfigurationPidMapping configurationPidMapping) {

		_configurationBeanClasses.put(
			configurationPidMapping.getConfigurationPid(),
			configurationPidMapping.getConfigurationBeanClass());
	}

	@Reference(unbind = "-")
	protected void setProps(Props props) {
		_portalPropertiesSettings = new PropertiesSettings(
			new LocationVariableResolver(
				new ClassLoaderResourceManager(
					PortalClassLoaderUtil.getClassLoader()),
				serviceName -> getConfigurationBeanSettings(serviceName)),
			props.getProperties());
	}

	protected void unsetConfigurationPidMapping(
		ConfigurationPidMapping configurationPidMapping) {

		_configurationBeanClasses.remove(
			configurationPidMapping.getConfigurationPid());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationBeanClassSettingsRegistry.class);

	private BundleContext _bundleContext;
	private final ConcurrentMap<String, Class<?>> _configurationBeanClasses =
		new ConcurrentHashMap<>();
	private final Map<Class<?>, Settings> _configurationBeanSettings =
		new ConcurrentHashMap<>();
	private Settings _portalPropertiesSettings;
	private final Map<String, ScopedConfigurationManagedServiceFactory>
		_scopedConfigurationManagedServiceFactories = new ConcurrentHashMap<>();

	@Reference
	private SettingsFactoryImpl _settingsFactoryImpl;

}
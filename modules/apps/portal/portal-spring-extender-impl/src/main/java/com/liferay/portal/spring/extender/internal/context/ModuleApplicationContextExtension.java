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

package com.liferay.portal.spring.extender.internal.context;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.spring.configurator.ConfigurableApplicationContextConfigurator;
import com.liferay.portal.spring.extender.internal.LiferayPortalServiceExtension;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.net.URL;

import java.util.Dictionary;

import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyManager;
import org.apache.felix.dm.ServiceDependency;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Miguel Pastor
 */
public class ModuleApplicationContextExtension
	implements LiferayPortalServiceExtension {

	public ModuleApplicationContextExtension(
		Bundle bundle, BundleContext extenderBundleContext,
		ConfigurableApplicationContextConfigurator
			configurableApplicationContextConfigurator) {

		_bundle = bundle;
		_configurableApplicationContextConfigurator =
			configurableApplicationContextConfigurator;

		_bundleContext = extenderBundleContext;

		_dependencyManager = new DependencyManager(bundle.getBundleContext());
	}

	@Override
	public void destroy() {
		if (_component != null) {
			_dependencyManager.remove(_component);
		}
	}

	@Override
	public void start() throws Exception {
		_component = _dependencyManager.createComponent();

		BundleContext bundleContext = _bundleContext;

		_component.setImplementation(
			new ModuleApplicationContextRegistrator(
				_configurableApplicationContextConfigurator, _bundle,
				bundleContext.getBundle()));

		BundleWiring bundleWiring = _bundle.adapt(BundleWiring.class);

		ClassLoader classLoader = bundleWiring.getClassLoader();

		_processServiceReferences(classLoader);

		Dictionary<String, String> headers = _bundle.getHeaders(
			StringPool.BLANK);

		String liferayService = headers.get("Liferay-Service");

		if (liferayService != null) {
			_generateConfigurationDependency(classLoader, "portlet");
			_generateConfigurationDependency(classLoader, "service");
		}

		String requireSchemaVersion = headers.get(
			"Liferay-Require-SchemaVersion");

		if (Validator.isNull(requireSchemaVersion)) {
			_generateReleaseDependency();
		}

		_dependencyManager.add(_component);
	}

	private void _generateConfigurationDependency(
		ClassLoader classLoader, String name) {

		if (ConfigurationFactoryUtil.getConfiguration(classLoader, name) !=
				null) {

			ServiceDependency serviceDependency =
				_dependencyManager.createServiceDependency();

			serviceDependency.setRequired(true);

			serviceDependency.setService(
				Configuration.class,
				StringBundler.concat(
					"(&(origin.bundle.symbolic.name=",
					_bundle.getSymbolicName(), ")(name=", name, "))"));

			_component.add(serviceDependency);
		}
	}

	private void _generateReleaseDependency() {
		ServiceDependency serviceDependency =
			_dependencyManager.createServiceDependency();

		serviceDependency.setRequired(true);

		serviceDependency.setService(
			Release.class,
			StringBundler.concat(
				"(&(release.bundle.symbolic.name=", _bundle.getSymbolicName(),
				")(release.schema.version=", _bundle.getVersion(), "))"));

		_component.add(serviceDependency);
	}

	private void _processServiceReferences(ClassLoader classLoader)
		throws Exception {

		URL url = _bundle.getEntry("OSGI-INF/context/context.dependencies");

		if (url == null) {
			return;
		}

		try (InputStream inputStream = url.openStream();
			Reader reader = new InputStreamReader(inputStream);
			UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(reader)) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				line = line.trim();

				int index = line.indexOf(CharPool.SPACE);

				String serviceClassName = line;

				String filterString = null;

				if (index != -1) {
					serviceClassName = line.substring(0, index);
					filterString = line.substring(index + 1);
				}

				ServiceDependency serviceDependency =
					_dependencyManager.createServiceDependency();

				serviceDependency.setRequired(true);

				Class<?> serviceClass = Class.forName(
					serviceClassName, false, classLoader);

				serviceDependency.setService(serviceClass, filterString);

				_component.add(serviceDependency);
			}
		}
	}

	private final Bundle _bundle;
	private final BundleContext _bundleContext;
	private Component _component;
	private final ConfigurableApplicationContextConfigurator
		_configurableApplicationContextConfigurator;
	private final DependencyManager _dependencyManager;

}
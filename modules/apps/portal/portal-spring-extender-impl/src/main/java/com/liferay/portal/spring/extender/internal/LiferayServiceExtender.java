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

package com.liferay.portal.spring.extender.internal;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.dao.jdbc.DataSourceProvider;
import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceComponentLocalService;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.module.util.BundleUtil;
import com.liferay.portal.spring.extender.internal.configuration.PortletConfigurationExtension;
import com.liferay.portal.spring.extender.internal.configuration.ServiceConfigurationExtension;
import com.liferay.portal.spring.extender.internal.configuration.ServiceConfigurationInitializer;
import com.liferay.portal.spring.extender.internal.upgrade.InitialUpgradeExtension;

import java.io.IOException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.FutureTask;

import javax.sql.DataSource;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * @author Preston Crary
 */
@Component(immediate = true, service = {})
public class LiferayServiceExtender
	implements BundleTrackerCustomizer<LiferayPortalServiceExtension> {

	@Override
	public LiferayPortalServiceExtension addingBundle(
		Bundle bundle, BundleEvent bundleEvent) {

		if (!BundleUtil.isLiferayServiceBundle(bundle)) {
			return null;
		}

		try {
			_startLiferayServiceExtension(bundle);
			_startInitialUpgradeExtension(bundle);
			_startPortletConfigurationExtension(bundle);
			_startServiceConfigurationExtension(bundle);
			_registerLiferayPortalServiceExtension(bundle);

			return new LiferayPortalServiceExtension() {
			};
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return null;
	}

	@Override
	public void modifiedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		LiferayPortalServiceExtension liferayPortalServiceExtension) {
	}

	@Override
	public void removedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		LiferayPortalServiceExtension liferayPortalServiceExtension) {

		String bundleSymbolicName = bundle.getSymbolicName();

		if (_liferayServiceExtensions != null) {
			LiferayServiceExtension liferayServiceExtension =
				_liferayServiceExtensions.remove(bundleSymbolicName);

			if (liferayServiceExtension != null) {
				liferayServiceExtension.destroy();
			}
		}

		if (_initialUpgradeExtensions != null) {
			InitialUpgradeExtension initialUpgradeExtension =
				_initialUpgradeExtensions.remove(bundleSymbolicName);

			if (initialUpgradeExtension != null) {
				initialUpgradeExtension.destroy();
			}
		}

		if (_portletConfigurationExtensions != null) {
			PortletConfigurationExtension portletConfigurationExtension =
				_portletConfigurationExtensions.remove(bundleSymbolicName);

			if (portletConfigurationExtension != null) {
				portletConfigurationExtension.destroy();
			}
		}

		if (_serviceConfigurationExtensions != null) {
			ServiceConfigurationExtension serviceConfigurationExtension =
				_serviceConfigurationExtensions.remove(bundleSymbolicName);

			if (serviceConfigurationExtension != null) {
				serviceConfigurationExtension.destroy();
			}
		}

		if (_liferayPortalServiceExtensionServiceRegistrations != null) {
			ServiceRegistration<LiferayPortalServiceExtension>
				serviceRegistration =
					_liferayPortalServiceExtensionServiceRegistrations.remove(
						bundleSymbolicName);

			if (serviceRegistration != null) {
				serviceRegistration.unregister();
			}
		}
	}

	public static class ModuleAggregareClassLoader extends ClassLoader {

		public ModuleAggregareClassLoader(
			ClassLoader moduleClassLoader, String symbolicName) {

			super(null);

			_moduleClassLoader = moduleClassLoader;

			int index = symbolicName.lastIndexOf('.');

			if (index == -1) {
				_namespace = symbolicName;
			}
			else {
				_namespace = symbolicName.substring(0, index);
			}
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			}

			if (!(object instanceof ModuleAggregareClassLoader)) {
				return false;
			}

			ModuleAggregareClassLoader moduleAggregareClassLoader =
				(ModuleAggregareClassLoader)object;

			if (Objects.equals(
					_moduleClassLoader,
					moduleAggregareClassLoader._moduleClassLoader)) {

				return true;
			}

			return false;
		}

		@Override
		public URL getResource(String name) {
			URL url = _moduleClassLoader.getResource(name);

			if (url != null) {
				return url;
			}

			return _extenderClassLoader.getResource(name);
		}

		@Override
		public Enumeration<URL> getResources(String name) throws IOException {
			List<URL> urls = new ArrayList<>();

			urls.addAll(
				Collections.list(_moduleClassLoader.getResources(name)));

			urls.addAll(
				Collections.list(_extenderClassLoader.getResources(name)));

			return Collections.enumeration(urls);
		}

		@Override
		public int hashCode() {
			return _moduleClassLoader.hashCode();
		}

		@Override
		public Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {

			if (name.startsWith(_namespace)) {
				return _moduleClassLoader.loadClass(name);
			}

			try {
				return _extenderClassLoader.loadClass(name);
			}
			catch (ClassNotFoundException classNotFoundException) {
				if (_log.isDebugEnabled()) {
					_log.debug(classNotFoundException);
				}

				return _moduleClassLoader.loadClass(name);
			}
		}

		@Override
		protected Class<?> findClass(String name)
			throws ClassNotFoundException {

			try {
				return (Class<?>)_FIND_CLASS_METHOD.invoke(
					_moduleClassLoader, name);
			}
			catch (InvocationTargetException invocationTargetException) {
				throw new ClassNotFoundException(
					"Unable to find class " + name,
					invocationTargetException.getTargetException());
			}
			catch (Exception exception) {
				throw new ClassNotFoundException(
					"Unable to find class " + name, exception);
			}
		}

		private static final Method _FIND_CLASS_METHOD;

		private static final ClassLoader _extenderClassLoader =
			LiferayServiceExtender.class.getClassLoader();

		static {
			try {
				_FIND_CLASS_METHOD = ReflectionUtil.getDeclaredMethod(
					ClassLoader.class, "findClass", String.class);
			}
			catch (Exception exception) {
				throw new ExceptionInInitializerError(exception);
			}
		}

		private final ClassLoader _moduleClassLoader;
		private final String _namespace;

	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleTracker = new BundleTracker<>(
			bundleContext, Bundle.ACTIVE | Bundle.STARTING, this);

		FutureTask<Void> futureTask = new FutureTask<>(
			() -> {
				_bundleTracker.open();

				return null;
			});

		Thread bundleTrackerOpenerThread = new Thread(
			futureTask,
			LiferayServiceExtender.class.getName() + "-BundleTrackerOpener");

		bundleTrackerOpenerThread.setDaemon(true);

		bundleTrackerOpenerThread.start();

		DependencyManagerSyncUtil.registerSyncFuture(futureTask);
	}

	@Deactivate
	protected void deactivate() {
		_bundleTracker.close();
	}

	private DataSource _getDataSource(Bundle extendeeBundle) {
		BundleWiring extendeeBundleWiring = extendeeBundle.adapt(
			BundleWiring.class);

		ServiceLoader<DataSourceProvider> serviceLoader = ServiceLoader.load(
			DataSourceProvider.class, extendeeBundleWiring.getClassLoader());

		Iterator<DataSourceProvider> iterator = serviceLoader.iterator();

		if (iterator.hasNext()) {
			DataSourceProvider dataSourceProvider = iterator.next();

			return dataSourceProvider.getDataSource();
		}

		return InfrastructureUtil.getDataSource();
	}

	private ClassLoader _getModuleAggregareClassLoader(Bundle extendeeBundle) {
		BundleWiring extendeeBundleWiring = extendeeBundle.adapt(
			BundleWiring.class);

		ClassLoader extendeeClassLoader = extendeeBundleWiring.getClassLoader();

		return new ModuleAggregareClassLoader(
			extendeeClassLoader, extendeeBundle.getSymbolicName());
	}

	private void _registerLiferayPortalServiceExtension(Bundle bundle) {
		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		if (headers.get("Liferay-Spring-Context") == null) {
			return;
		}

		BundleContext bundleContext = bundle.getBundleContext();

		LiferayPortalServiceExtension liferayPortalServiceExtension =
			new LiferayPortalServiceExtension() {

				public DataSource getDataSource() {
					return _getDataSource(bundle);
				}

				public ClassLoader getModuleAggregareClassLoader() {
					return _getModuleAggregareClassLoader(bundle);
				}

			};

		_liferayPortalServiceExtensionServiceRegistrations.put(
			bundle.getSymbolicName(),
			bundleContext.registerService(
				LiferayPortalServiceExtension.class,
				liferayPortalServiceExtension, null));
	}

	private void _startInitialUpgradeExtension(Bundle bundle) {
		InitialUpgradeExtension initialUpgradeExtension =
			new InitialUpgradeExtension(bundle);

		initialUpgradeExtension.start();

		_initialUpgradeExtensions.put(
			bundle.getSymbolicName(), initialUpgradeExtension);
	}

	private void _startLiferayServiceExtension(Bundle bundle) throws Exception {
		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		if (headers.get("Liferay-Spring-Context") != null) {
			return;
		}

		LiferayServiceExtension liferayServiceExtension =
			new LiferayServiceExtension(
				bundle, _getModuleAggregareClassLoader(bundle),
				_getDataSource(bundle));

		liferayServiceExtension.start();

		_liferayServiceExtensions.put(
			bundle.getSymbolicName(), liferayServiceExtension);
	}

	private void _startPortletConfigurationExtension(Bundle bundle) {
		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		ClassLoader classLoader = bundleWiring.getClassLoader();

		Configuration portletConfiguration =
			ConfigurationFactoryUtil.getConfiguration(classLoader, "portlet");

		if (portletConfiguration == null) {
			return;
		}

		PortletConfigurationExtension portletConfigurationExtension =
			new PortletConfigurationExtension(
				bundle, classLoader, portletConfiguration);

		portletConfigurationExtension.start();

		_portletConfigurationExtensions.put(
			bundle.getSymbolicName(), portletConfigurationExtension);
	}

	private void _startServiceConfigurationExtension(Bundle bundle) {
		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		ClassLoader classLoader = bundleWiring.getClassLoader();

		Configuration serviceConfiguration =
			ConfigurationFactoryUtil.getConfiguration(classLoader, "service");

		if (serviceConfiguration == null) {
			return;
		}

		String requireSchemaVersion = headers.get(
			"Liferay-Require-SchemaVersion");

		ServiceConfigurationInitializer serviceConfigurationInitializer =
			new ServiceConfigurationInitializer(
				bundle, classLoader, serviceConfiguration,
				_serviceComponentLocalService);

		ServiceConfigurationExtension serviceConfigurationExtension =
			new ServiceConfigurationExtension(
				bundle, requireSchemaVersion, serviceConfigurationInitializer);

		serviceConfigurationExtension.start();

		_serviceConfigurationExtensions.put(
			bundle.getSymbolicName(), serviceConfigurationExtension);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayServiceExtender.class);

	private BundleTracker<?> _bundleTracker;
	private final Map<String, InitialUpgradeExtension>
		_initialUpgradeExtensions = new HashMap<>();
	private final Map
		<String, ServiceRegistration<LiferayPortalServiceExtension>>
			_liferayPortalServiceExtensionServiceRegistrations =
				new HashMap<>();
	private final Map<String, LiferayServiceExtension>
		_liferayServiceExtensions = new HashMap<>();
	private final Map<String, PortletConfigurationExtension>
		_portletConfigurationExtensions = new HashMap<>();

	@Reference
	private ServiceComponentLocalService _serviceComponentLocalService;

	private final Map<String, ServiceConfigurationExtension>
		_serviceConfigurationExtensions = new HashMap<>();

}
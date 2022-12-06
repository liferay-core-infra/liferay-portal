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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.hibernate.SessionFactoryImpl;
import com.liferay.portal.dao.orm.hibernate.VerifySessionFactoryWrapper;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.dao.jdbc.DataSourceFactoryUtil;
import com.liferay.portal.kernel.dao.jdbc.DataSourceProvider;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceComponentLocalService;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.module.util.BundleUtil;
import com.liferay.portal.spring.extender.internal.configuration.PortletConfigurationExtension;
import com.liferay.portal.spring.extender.internal.configuration.ServiceConfigurationExtension;
import com.liferay.portal.spring.extender.internal.configuration.ServiceConfigurationInitializer;
import com.liferay.portal.spring.extender.internal.loader.ModuleAggregareClassLoader;
import com.liferay.portal.spring.extender.internal.upgrade.InitialUpgradeExtension;
import com.liferay.portal.spring.hibernate.PortletHibernateConfiguration;
import com.liferay.portal.spring.hibernate.PortletTransactionManager;
import com.liferay.portal.spring.transaction.DefaultTransactionExecutor;
import com.liferay.portal.spring.transaction.TransactionExecutor;
import com.liferay.portal.spring.transaction.TransactionHandler;
import com.liferay.portal.spring.transaction.TransactionManagerFactory;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.FutureTask;

import javax.sql.DataSource;

import org.hibernate.engine.spi.SessionFactoryImplementor;

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

import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

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

	public class LiferayServiceExtension
		implements LiferayPortalServiceExtension {

		@Override
		public void destroy() {
			for (ServiceRegistration<?> serviceRegistration :
					_serviceRegistrations) {

				serviceRegistration.unregister();
			}

			_sessionFactoryImplementor.close();

			if (InfrastructureUtil.getDataSource() != _dataSource) {
				try {
					DataSourceFactoryUtil.destroyDataSource(_dataSource);
				}
				catch (Exception exception) {
					_log.error(
						"Unable to destroy external data source " + _dataSource,
						exception);
				}
			}
		}

		@Override
		public void start() throws Exception {
			_dataSource = _getDataSource(_extendeeBundle);

			BundleContext extendeeBundleContext =
				_extendeeBundle.getBundleContext();

			_serviceRegistrations.add(
				extendeeBundleContext.registerService(
					DataSource.class, _dataSource,
					MapUtil.singletonDictionary(
						"origin.bundle.symbolic.name",
						_extendeeBundle.getSymbolicName())));

			BundleWiring extendeeBundleWiring = _extendeeBundle.adapt(
				BundleWiring.class);

			ClassLoader extendeeClassLoader =
				extendeeBundleWiring.getClassLoader();

			ClassLoader classLoader = new ModuleAggregareClassLoader(
				extendeeClassLoader, _extendeeBundle.getSymbolicName());

			PortletHibernateConfiguration portletHibernateConfiguration =
				new PortletHibernateConfiguration(classLoader, _dataSource);

			portletHibernateConfiguration.afterPropertiesSet();

			_sessionFactoryImplementor =
				(SessionFactoryImplementor)
					portletHibernateConfiguration.getObject();

			SessionFactoryImpl sessionFactoryImpl = new SessionFactoryImpl();

			sessionFactoryImpl.setSessionFactoryClassLoader(classLoader);
			sessionFactoryImpl.setSessionFactoryImplementor(
				_sessionFactoryImplementor);

			SessionFactory sessionFactory =
				VerifySessionFactoryWrapper.createVerifySessionFactoryWrapper(
					sessionFactoryImpl);

			_serviceRegistrations.add(
				extendeeBundleContext.registerService(
					SessionFactory.class, sessionFactory,
					MapUtil.singletonDictionary(
						"origin.bundle.symbolic.name",
						_extendeeBundle.getSymbolicName())));

			DefaultTransactionExecutor defaultTransactionExecutor =
				_getTransactionExecutor(
					_dataSource, _sessionFactoryImplementor);

			_serviceRegistrations.add(
				extendeeBundleContext.registerService(
					new String[] {
						TransactionExecutor.class.getName(),
						TransactionHandler.class.getName()
					},
					defaultTransactionExecutor,
					MapUtil.singletonDictionary(
						"origin.bundle.symbolic.name",
						_extendeeBundle.getSymbolicName())));
		}

		private LiferayServiceExtension(Bundle extendeeBundle) {
			_extendeeBundle = extendeeBundle;
		}

		private DefaultTransactionExecutor _getTransactionExecutor(
			DataSource liferayDataSource,
			SessionFactoryImplementor sessionFactoryImplementor) {

			PlatformTransactionManager platformTransactionManager = null;

			if (InfrastructureUtil.getDataSource() == liferayDataSource) {
				platformTransactionManager = new PortletTransactionManager(
					(HibernateTransactionManager)
						InfrastructureUtil.getTransactionManager(),
					sessionFactoryImplementor);
			}
			else {
				platformTransactionManager =
					TransactionManagerFactory.createTransactionManager(
						liferayDataSource, sessionFactoryImplementor);
			}

			return new DefaultTransactionExecutor(platformTransactionManager);
		}

		private DataSource _dataSource;
		private final Bundle _extendeeBundle;
		private final List<ServiceRegistration<?>> _serviceRegistrations =
			new ArrayList<>();
		private SessionFactoryImplementor _sessionFactoryImplementor;

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
			new LiferayServiceExtension(bundle);

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
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
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.module.util.BundleUtil;
import com.liferay.portal.spring.extender.internal.configuration.PortletConfigurationExtension;
import com.liferay.portal.spring.extender.internal.jdbc.DataSourceUtil;
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
import java.util.List;
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
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Preston Crary
 */
@Component(service = {})
public class LiferayServiceExtender
	implements BundleTrackerCustomizer<List<LiferayServiceExtension>> {

	@Override
	public List<LiferayServiceExtension> addingBundle(
		Bundle bundle, BundleEvent bundleEvent) {

		if (!BundleUtil.isLiferayServiceBundle(bundle)) {
			return null;
		}

		List<LiferayServiceExtension> liferayServiceExtensions =
			new ArrayList<>();

		try {
			_startDefaultServiceExtension(bundle, liferayServiceExtensions);
			_startInitialUpgradeExtension(bundle, liferayServiceExtensions);
			_startPortletConfigurationExtension(
				bundle, liferayServiceExtensions);

			return liferayServiceExtensions;
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return null;
	}

	@Override
	public void modifiedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		List<LiferayServiceExtension> liferayServiceExtensions) {
	}

	@Override
	public void removedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		List<LiferayServiceExtension> liferayServiceExtensions) {

		for (LiferayServiceExtension liferayPortalServiceExtension :
				liferayServiceExtensions) {

			liferayPortalServiceExtension.destroy();
		}
	}

	public class DefaultServiceExtension implements LiferayServiceExtension {

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
			BundleWiring extendeeBundleWiring = _extendeeBundle.adapt(
				BundleWiring.class);

			ClassLoader extendeeClassLoader =
				extendeeBundleWiring.getClassLoader();

			_dataSource = DataSourceUtil.getDataSource(extendeeClassLoader);

			BundleContext extendeeBundleContext =
				_extendeeBundle.getBundleContext();

			_serviceRegistrations.add(
				extendeeBundleContext.registerService(
					DataSource.class, _dataSource,
					MapUtil.singletonDictionary(
						"origin.bundle.symbolic.name",
						_extendeeBundle.getSymbolicName())));

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

		private DefaultServiceExtension(Bundle extendeeBundle) {
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

	private void _startDefaultServiceExtension(
			Bundle bundle,
			List<LiferayServiceExtension> liferayServiceExtensions)
		throws Exception {

		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		if (headers.get("Liferay-Spring-Context") != null) {
			return;
		}

		LiferayServiceExtension defaultServiceExtension =
			new DefaultServiceExtension(bundle);

		liferayServiceExtensions.add(defaultServiceExtension);

		defaultServiceExtension.start();
	}

	private void _startInitialUpgradeExtension(
		Bundle bundle, List<LiferayServiceExtension> liferayServiceExtensions) {

		InitialUpgradeExtension initialUpgradeExtension =
			new InitialUpgradeExtension(bundle);

		liferayServiceExtensions.add(initialUpgradeExtension);

		initialUpgradeExtension.start();
	}

	private void _startPortletConfigurationExtension(
		Bundle bundle, List<LiferayServiceExtension> liferayServiceExtensions) {

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

		liferayServiceExtensions.add(portletConfigurationExtension);

		portletConfigurationExtension.start();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayServiceExtender.class);

	private BundleTracker<?> _bundleTracker;

}
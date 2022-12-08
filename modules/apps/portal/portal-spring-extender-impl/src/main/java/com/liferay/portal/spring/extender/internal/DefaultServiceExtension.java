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

import com.liferay.portal.dao.orm.hibernate.SessionFactoryImpl;
import com.liferay.portal.dao.orm.hibernate.VerifySessionFactoryWrapper;
import com.liferay.portal.kernel.dao.jdbc.DataSourceFactoryUtil;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.spring.extender.internal.jdbc.DataSourceUtil;
import com.liferay.portal.spring.extender.internal.loader.ModuleAggregareClassLoader;
import com.liferay.portal.spring.hibernate.PortletHibernateConfiguration;
import com.liferay.portal.spring.hibernate.PortletTransactionManager;
import com.liferay.portal.spring.transaction.DefaultTransactionExecutor;
import com.liferay.portal.spring.transaction.TransactionExecutor;
import com.liferay.portal.spring.transaction.TransactionHandler;
import com.liferay.portal.spring.transaction.TransactionManagerFactory;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.hibernate.engine.spi.SessionFactoryImplementor;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWiring;

import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Hai Yu
 */
public class DefaultServiceExtension implements LiferayServiceExtension {

	public DefaultServiceExtension(Bundle extendeeBundle) {
		_extendeeBundle = extendeeBundle;
	}

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

		ClassLoader extendeeClassLoader = extendeeBundleWiring.getClassLoader();

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
			_getTransactionExecutor(_dataSource, _sessionFactoryImplementor);

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

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultServiceExtension.class);

	private DataSource _dataSource;
	private final Bundle _extendeeBundle;
	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();
	private SessionFactoryImplementor _sessionFactoryImplementor;

}
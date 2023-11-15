/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth.verifier.internal.registry;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifier;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierConfiguration;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.auth.AuthVerifierPipeline;
import com.liferay.portal.security.auth.registry.AuthVerifierRegistry;

import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Carlos Sierra Andrés
 */
@Component(service = AuthVerifierRegistry.class)
public class AuthVerifierRegistryImpl implements AuthVerifierRegistry {

	@Override
	public AuthVerifier getAuthVerifier(String simpleClassName) {
		Tracked tracked = _serviceTrackerMap.getService(simpleClassName);

		if (tracked == null) {
			return null;
		}

		return tracked.getAuthVerifier();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, AuthVerifier.class, null,
			(serviceReference, emitter) -> {
				String authVerifierClassName = GetterUtil.getString(
					serviceReference.getProperty("auth.verifier.class.name"));

				if (Validator.isNotNull(authVerifierClassName)) {
					emitter.emit(authVerifierClassName);
				}
				else {
					AuthVerifier authVerifier = bundleContext.getService(
						serviceReference);

					Class<? extends AuthVerifier> clazz =
						authVerifier.getClass();

					emitter.emit(clazz.getSimpleName());

					bundleContext.ungetService(serviceReference);
				}
			},
			new AuthVerifierRegistryServiceTrackerCustomizer(bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private AuthVerifierConfiguration _buildAuthVerifierConfiguration(
		ServiceReference<AuthVerifier> serviceReference,
		AuthVerifier authVerifier) {

		Class<?> clazz = authVerifier.getClass();

		String authVerifierPropertyName =
			AuthVerifierPipeline.getAuthVerifierPropertyName(clazz.getName());

		Properties properties = new Properties();

		for (String propertyKey : serviceReference.getPropertyKeys()) {
			if (!propertyKey.startsWith(authVerifierPropertyName)) {
				continue;
			}

			properties.put(
				propertyKey.substring(authVerifierPropertyName.length()),
				serviceReference.getProperty(propertyKey));
		}

		if (properties.isEmpty()) {
			return null;
		}

		AuthVerifierConfiguration authVerifierConfiguration =
			new AuthVerifierConfiguration();

		authVerifierConfiguration.setAuthVerifierClassName(clazz.getName());
		authVerifierConfiguration.setProperties(properties);

		return authVerifierConfiguration;
	}

	private ServiceTrackerMap<String, Tracked> _serviceTrackerMap;

	private static class Tracked {

		public AuthVerifier getAuthVerifier() {
			return _authVerifier;
		}

		public ServiceRegistration<AuthVerifierConfiguration>
			getServiceRegistration() {

			return _serviceRegistration;
		}

		public void setAuthVerifier(AuthVerifier authVerifier) {
			_authVerifier = authVerifier;
		}

		public void setServiceRegistration(
			ServiceRegistration<AuthVerifierConfiguration>
				serviceRegistration) {

			_serviceRegistration = serviceRegistration;
		}

		private Tracked(
			AuthVerifier authVerifier,
			ServiceRegistration<AuthVerifierConfiguration>
				serviceRegistration) {

			_authVerifier = authVerifier;
			_serviceRegistration = serviceRegistration;
		}

		private AuthVerifier _authVerifier;
		private ServiceRegistration<AuthVerifierConfiguration>
			_serviceRegistration;

	}

	private class AuthVerifierRegistryServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<AuthVerifier, Tracked> {

		public AuthVerifierRegistryServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		@Override
		public Tracked addingService(
			ServiceReference<AuthVerifier> serviceReference) {

			AuthVerifier authVerifier = _bundleContext.getService(
				serviceReference);

			AuthVerifierConfiguration authVerifierConfiguration =
				_buildAuthVerifierConfiguration(serviceReference, authVerifier);

			ServiceRegistration<AuthVerifierConfiguration> serviceRegistration =
				null;

			if (authVerifierConfiguration != null) {
				serviceRegistration = _bundleContext.registerService(
					AuthVerifierConfiguration.class, authVerifierConfiguration,
					null);
			}

			return new Tracked(authVerifier, serviceRegistration);
		}

		@Override
		public void modifiedService(
			ServiceReference<AuthVerifier> serviceReference, Tracked tracked) {

			ServiceRegistration<AuthVerifierConfiguration> serviceRegistration =
				tracked.getServiceRegistration();

			if (serviceRegistration != null) {
				serviceRegistration.unregister();
			}

			AuthVerifierConfiguration authVerifierConfiguration =
				_buildAuthVerifierConfiguration(
					serviceReference, tracked.getAuthVerifier());

			if (authVerifierConfiguration != null) {
				_bundleContext.registerService(
					AuthVerifierConfiguration.class, authVerifierConfiguration,
					null);
			}

			tracked.setServiceRegistration(serviceRegistration);
		}

		@Override
		public void removedService(
			ServiceReference<AuthVerifier> serviceReference, Tracked tracked) {

			ServiceRegistration<AuthVerifierConfiguration> serviceRegistration =
				tracked.getServiceRegistration();

			if (serviceRegistration != null) {
				serviceRegistration.unregister();
			}

			_bundleContext.ungetService(serviceReference);
		}

		private final BundleContext _bundleContext;

	}

}
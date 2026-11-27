/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.repository.registry;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.cache.CacheRegistryItem;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.repository.RepositoryFactory;
import com.liferay.portal.kernel.repository.registry.RepositoryDefiner;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsValues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Adolfo Pérez
 */
public class RepositoryClassDefinitionCatalogImpl
	implements CacheRegistryItem, RepositoryClassDefinitionCatalog {

	public void afterPropertiesSet() {
		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			_bundleContext, RepositoryDefiner.class, null,
			(serviceReference, emitter) -> {
				long companyId = GetterUtil.getLong(
					serviceReference.getProperty("companyId"));

				emitter.emit(companyId);
			},
			new ServiceTrackerCustomizer
				<RepositoryDefiner, RepositoryClassDefinitionHolder>() {

				@Override
				public RepositoryClassDefinitionHolder addingService(
					ServiceReference<RepositoryDefiner> serviceReference) {

					RepositoryDefiner repositoryDefiner =
						_bundleContext.getService(serviceReference);

					if (repositoryDefiner == null) {
						return null;
					}

					RepositoryClassDefinition repositoryClassDefinition =
						RepositoryClassDefinition.fromRepositoryDefiner(
							repositoryDefiner);

					ServiceRegistration<RepositoryFactory> serviceRegistration =
						_bundleContext.registerService(
							RepositoryFactory.class, repositoryClassDefinition,
							HashMapDictionaryBuilder.<String, Object>put(
								"class.name", repositoryDefiner.getClassName()
							).put(
								"companyId",
								GetterUtil.getLong(
									serviceReference.getProperty("companyId"))
							).build());

					return new RepositoryClassDefinitionHolder(
						repositoryClassDefinition,
						repositoryDefiner.isExternalRepository(),
						serviceRegistration);
				}

				@Override
				public void modifiedService(
					ServiceReference<RepositoryDefiner> serviceReference,
					RepositoryClassDefinitionHolder
						repositoryClassDefinitionHolder) {
				}

				@Override
				public void removedService(
					ServiceReference<RepositoryDefiner> serviceReference,
					RepositoryClassDefinitionHolder
						repositoryClassDefinitionHolder) {

					_bundleContext.ungetService(serviceReference);

					ServiceRegistration<RepositoryFactory> serviceRegistration =
						repositoryClassDefinitionHolder.
							getServiceRegistration();

					serviceRegistration.unregister();
				}

			});
	}

	public void destroy() {
		_serviceTrackerMap.close();
	}

	@Override
	public Iterable<RepositoryClassDefinition>
		getExternalRepositoryClassDefinitions(long companyId) {

		Collection<RepositoryClassDefinition>
			externalRepositoryClassDefinitions =
				_getSystemExternalRepositoryData(Function.identity());

		if (companyId != CompanyConstants.SYSTEM) {
			List<RepositoryClassDefinitionHolder> holders =
				_serviceTrackerMap.getService(companyId);

			if (holders != null) {
				for (RepositoryClassDefinitionHolder holder : holders) {
					if (holder.isExternalRepository()) {
						externalRepositoryClassDefinitions.add(
							holder.getRepositoryClassDefinition());
					}
				}
			}
		}

		return externalRepositoryClassDefinitions;
	}

	@Override
	public Collection<String> getExternalRepositoryClassNames(long companyId) {
		Collection<String> externalRepositoryClassNames =
			_getSystemExternalRepositoryData(
				repositoryClassDefinition ->
					repositoryClassDefinition.getClassName());

		if (companyId != CompanyConstants.SYSTEM) {
			List<RepositoryClassDefinitionHolder> holders =
				_serviceTrackerMap.getService(companyId);

			if (holders != null) {
				for (RepositoryClassDefinitionHolder holder : holders) {
					if (holder.isExternalRepository()) {
						externalRepositoryClassNames.add(
							holder.getRepositoryClassDefinition(
							).getClassName());
					}
				}
			}
		}

		return externalRepositoryClassNames;
	}

	@Override
	public String getRegistryName() {
		Class<?> clazz = getClass();

		return clazz.getName();
	}

	@Override
	public RepositoryClassDefinition getRepositoryClassDefinition(
		long companyId, String className) {

		List<RepositoryClassDefinitionHolder> holders =
			_serviceTrackerMap.getService(companyId);

		if (holders != null) {
			for (RepositoryClassDefinitionHolder holder : holders) {
				RepositoryClassDefinition repositoryClassDefinition =
					holder.getRepositoryClassDefinition();

				if (className.equals(
						repositoryClassDefinition.getClassName())) {

					return repositoryClassDefinition;
				}
			}
		}

		return _getSystemRepositoryClassDefinition(className);
	}

	@Override
	public void invalidate() {
		Collection<List<RepositoryClassDefinitionHolder>> holdersCollection =
			null;

		if (PropsValues.DATABASE_PARTITION_ENABLED &&
			(CompanyThreadLocal.getCompanyId() != CompanyConstants.SYSTEM)) {

			List<RepositoryClassDefinitionHolder> holders =
				_serviceTrackerMap.getService(
					CompanyThreadLocal.getCompanyId());

			if (holders == null) {
				return;
			}

			holdersCollection = Collections.singletonList(holders);
		}
		else {
			holdersCollection = _serviceTrackerMap.values();
		}

		for (List<RepositoryClassDefinitionHolder> holders :
				holdersCollection) {

			for (RepositoryClassDefinitionHolder holder : holders) {
				RepositoryClassDefinition repositoryClassDefinition =
					holder.getRepositoryClassDefinition();

				repositoryClassDefinition.invalidateCache();
			}
		}
	}

	private <T> Collection<T> _getSystemExternalRepositoryData(
		Function<RepositoryClassDefinition, T> function) {

		List<RepositoryClassDefinitionHolder> holders =
			_serviceTrackerMap.getService(CompanyConstants.SYSTEM);

		if (holders == null) {
			return new ArrayList<>();
		}

		Collection<T> collection = new ArrayList<>();

		for (RepositoryClassDefinitionHolder holder : holders) {
			if (holder.isExternalRepository()) {
				collection.add(
					function.apply(holder.getRepositoryClassDefinition()));
			}
		}

		return collection;
	}

	private RepositoryClassDefinition _getSystemRepositoryClassDefinition(
		String className) {

		List<RepositoryClassDefinitionHolder> holders =
			_serviceTrackerMap.getService(CompanyConstants.SYSTEM);

		if (holders == null) {
			return null;
		}

		for (RepositoryClassDefinitionHolder holder : holders) {
			RepositoryClassDefinition repositoryClassDefinition =
				holder.getRepositoryClassDefinition();

			if (className.equals(repositoryClassDefinition.getClassName())) {
				return repositoryClassDefinition;
			}
		}

		return null;
	}

	private final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private ServiceTrackerMap<Long, List<RepositoryClassDefinitionHolder>>
		_serviceTrackerMap;

	private static class RepositoryClassDefinitionHolder {

		public RepositoryClassDefinitionHolder(
			RepositoryClassDefinition repositoryClassDefinition,
			boolean externalRepository,
			ServiceRegistration<RepositoryFactory> serviceRegistration) {

			_repositoryClassDefinition = repositoryClassDefinition;
			_externalRepository = externalRepository;
			_serviceRegistration = serviceRegistration;
		}

		public RepositoryClassDefinition getRepositoryClassDefinition() {
			return _repositoryClassDefinition;
		}

		public ServiceRegistration<RepositoryFactory> getServiceRegistration() {
			return _serviceRegistration;
		}

		public boolean isExternalRepository() {
			return _externalRepository;
		}

		private final boolean _externalRepository;
		private final RepositoryClassDefinition _repositoryClassDefinition;
		private final ServiceRegistration<RepositoryFactory>
			_serviceRegistration;

	}

}
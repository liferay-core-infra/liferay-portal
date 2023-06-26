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

package com.liferay.object.internal.helper;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.object.deployer.ObjectDefinitionDeployer;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Lily Chi
 */
@Component(service = ObjectDefinitionDeployerHelper.class)
public class ObjectDefinitionDeployerHelper {

	public ObjectDefinitionDeployer addingObjectDefinitionDeployer(
		ObjectDefinitionDeployer objectDefinitionDeployer,
		ObjectDefinitionLocalService objectDefinitionLocalService) {

		Map<Long, List<ServiceRegistration<?>>> serviceRegistrationsMap =
			new ConcurrentHashMap<>();

		for (ObjectDefinition objectDefinition :
				_getObjectDefinitions(objectDefinitionLocalService)) {

			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setWithSafeCloseable(
						objectDefinition.getCompanyId())) {

				serviceRegistrationsMap.put(
					objectDefinition.getObjectDefinitionId(),
					objectDefinitionDeployer.deploy(objectDefinition));
			}
		}

		_serviceRegistrationsMaps.put(
			objectDefinitionDeployer, serviceRegistrationsMap);

		return objectDefinitionDeployer;
	}

	public void closeObjectDefinitionDeployerServiceTracker() {
		if (_objectDefinitionDeployerServiceTracker != null) {
			_objectDefinitionDeployerServiceTracker.close();
		}
	}

	public void deployObjectDefinition(ObjectDefinition objectDefinition) {
		undeployObjectDefinition(objectDefinition);

		for (Map.Entry
				<ObjectDefinitionDeployer,
				 Map<Long, List<ServiceRegistration<?>>>> entry :
					_serviceRegistrationsMaps.entrySet()) {

			ObjectDefinitionDeployer objectDefinitionDeployer = entry.getKey();
			Map<Long, List<ServiceRegistration<?>>> serviceRegistrationsMap =
				entry.getValue();

			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setWithSafeCloseable(
						objectDefinition.getCompanyId())) {

				serviceRegistrationsMap.computeIfAbsent(
					objectDefinition.getObjectDefinitionId(),
					objectDefinitionId -> objectDefinitionDeployer.deploy(
						objectDefinition));
			}
		}
	}

	public void openObjectDefinitionDeployerServiceTracker(
		BundleContext bundleContext,
		ObjectDefinitionLocalService objectDefinitionLocalService) {

		_objectDefinitionDeployerServiceTracker = new ServiceTracker<>(
			bundleContext, ObjectDefinitionDeployer.class,
			new ServiceTrackerCustomizer
				<ObjectDefinitionDeployer, ObjectDefinitionDeployer>() {

				@Override
				public ObjectDefinitionDeployer addingService(
					ServiceReference<ObjectDefinitionDeployer>
						serviceReference) {

					return addingObjectDefinitionDeployer(
						bundleContext.getService(serviceReference),
						objectDefinitionLocalService);
				}

				@Override
				public void modifiedService(
					ServiceReference<ObjectDefinitionDeployer> serviceReference,
					ObjectDefinitionDeployer objectDefinitionDeployer) {
				}

				@Override
				public void removedService(
					ServiceReference<ObjectDefinitionDeployer> serviceReference,
					ObjectDefinitionDeployer objectDefinitionDeployer) {

					for (ObjectDefinition objectDefinition :
							_getObjectDefinitions(
								objectDefinitionLocalService)) {

						objectDefinitionDeployer.undeploy(objectDefinition);
					}

					Map<Long, List<ServiceRegistration<?>>>
						serviceRegistrationsMap =
							_serviceRegistrationsMaps.remove(
								objectDefinitionDeployer);

					for (List<ServiceRegistration<?>> serviceRegistrations :
							serviceRegistrationsMap.values()) {

						for (ServiceRegistration<?> serviceRegistration :
								serviceRegistrations) {

							serviceRegistration.unregister();
						}
					}

					bundleContext.ungetService(serviceReference);
				}

			});

		DependencyManagerSyncUtil.registerSyncCallable(
			() -> {
				_objectDefinitionDeployerServiceTracker.open();

				return null;
			});
	}

	public void undeployObjectDefinition(ObjectDefinition objectDefinition) {
		if (objectDefinition.isUnmodifiableSystemObject()) {
			return;
		}

		for (Map.Entry
				<ObjectDefinitionDeployer,
				 Map<Long, List<ServiceRegistration<?>>>> entry :
					_serviceRegistrationsMaps.entrySet()) {

			ObjectDefinitionDeployer objectDefinitionDeployer = entry.getKey();

			objectDefinitionDeployer.undeploy(objectDefinition);

			Map<Long, List<ServiceRegistration<?>>> serviceRegistrationsMap =
				entry.getValue();

			List<ServiceRegistration<?>> serviceRegistrations =
				serviceRegistrationsMap.remove(
					objectDefinition.getObjectDefinitionId());

			if (serviceRegistrations != null) {
				for (ServiceRegistration<?> serviceRegistration :
						serviceRegistrations) {

					serviceRegistration.unregister();
				}
			}
		}

		_invalidatePortalCache(objectDefinition);
	}

	private List<ObjectDefinition> _getObjectDefinitions(
		ObjectDefinitionLocalService objectDefinitionLocalService) {

		List<ObjectDefinition> objectDefinitions = new ArrayList<>();

		_companyLocalService.forEachCompanyId(
			companyId -> objectDefinitions.addAll(
				objectDefinitionLocalService.getObjectDefinitions(
					companyId, true, WorkflowConstants.STATUS_APPROVED)));

		return objectDefinitions;
	}

	private void _invalidatePortalCache(ObjectDefinition objectDefinition) {
		PortalCache<String, String> portalCache =
			(PortalCache<String, String>)_multiVMPool.getPortalCache(
				FragmentEntryLink.class.getName());

		List<LayoutClassedModelUsage> layoutClassedModelUsages =
			_layoutClassedModelUsageLocalService.getLayoutClassedModelUsages(
				objectDefinition.getCompanyId(),
				_classNameLocalService.getClassNameId(
					objectDefinition.getClassName()),
				_portal.getClassNameId(FragmentEntryLink.class));

		for (LayoutClassedModelUsage layoutClassedModelUsage :
				layoutClassedModelUsages) {

			Set<Locale> availableLocales = _language.getAvailableLocales(
				layoutClassedModelUsage.getGroupId());

			for (Locale locale : availableLocales) {
				portalCache.remove(
					StringBundler.concat(
						layoutClassedModelUsage.getContainerKey(),
						StringPool.DASH, locale, StringPool.DASH, 0));
			}
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Reference
	private MultiVMPool _multiVMPool;

	private ServiceTracker<ObjectDefinitionDeployer, ObjectDefinitionDeployer>
		_objectDefinitionDeployerServiceTracker;

	@Reference
	private Portal _portal;

	private final Map
		<ObjectDefinitionDeployer, Map<Long, List<ServiceRegistration<?>>>>
			_serviceRegistrationsMaps = Collections.synchronizedMap(
				new LinkedHashMap<>());

}
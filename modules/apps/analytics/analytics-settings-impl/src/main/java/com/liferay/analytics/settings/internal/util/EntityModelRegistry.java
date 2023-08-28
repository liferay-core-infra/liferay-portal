/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.internal.util;

import com.liferay.analytics.message.sender.model.listener.EntityModel;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.model.BaseModel;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Rachael Koestartyo
 */
@Component(service = EntityModelRegistry.class)
public class EntityModelRegistry {

	public EntityModel<?> getEntityModel(String className) {
		return _serviceTrackerMap.getService(className);
	}

	public Collection<EntityModel<?>> getEntityModels() {
		return _serviceTrackerMap.values();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, (Class<EntityModel<?>>)(Class<?>)EntityModel.class,
			null, new EntityModelServiceReferenceMapper());
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private BundleContext _bundleContext;
	private ServiceTrackerMap<String, EntityModel<?>> _serviceTrackerMap;

	private class EntityModelServiceReferenceMapper<T extends BaseModel<T>>
		implements ServiceReferenceMapper<String, EntityModel<T>> {

		@Override
		public void map(
			ServiceReference<EntityModel<T>> serviceReference,
			Emitter<String> emitter) {

			EntityModel<?> entityModel = _bundleContext.getService(
				serviceReference);

			Class<?> clazz = _getParameterizedClass(entityModel.getClass());

			try {
				emitter.emit(clazz.getName());
			}
			finally {
				_bundleContext.ungetService(serviceReference);
			}
		}

		private Class<?> _getParameterizedClass(Class<?> clazz) {
			ParameterizedType parameterizedType =
				(ParameterizedType)clazz.getGenericSuperclass();

			Type[] types = parameterizedType.getActualTypeArguments();

			return (Class<?>)types[0];
		}

	}

}
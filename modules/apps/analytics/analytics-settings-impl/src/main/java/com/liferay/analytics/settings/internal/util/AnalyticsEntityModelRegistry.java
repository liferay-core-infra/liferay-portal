/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.internal.util;

import com.liferay.analytics.message.sender.model.listener.AnalyticsEntityModel;
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
@Component(service = AnalyticsEntityModelRegistry.class)
public class AnalyticsEntityModelRegistry {

	public AnalyticsEntityModel<?> getAnalyticsEntityModel(String className) {
		return _serviceTrackerMap.getService(className);
	}

	public Collection<AnalyticsEntityModel<?>> getAnalyticsEntityModels() {
		return _serviceTrackerMap.values();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext,
			(Class<AnalyticsEntityModel<?>>)
				(Class<?>)AnalyticsEntityModel.class,
			null, new AnalyticsEntityModelServiceReferenceMapper());
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private BundleContext _bundleContext;
	private ServiceTrackerMap<String, AnalyticsEntityModel<?>>
		_serviceTrackerMap;

	private class AnalyticsEntityModelServiceReferenceMapper
		<T extends BaseModel<T>>
			implements ServiceReferenceMapper<String, AnalyticsEntityModel<T>> {

		@Override
		public void map(
			ServiceReference<AnalyticsEntityModel<T>> serviceReference,
			Emitter<String> emitter) {

			AnalyticsEntityModel<?> analyticsEntityModel =
				_bundleContext.getService(serviceReference);

			Class<?> clazz = _getParameterizedClass(
				analyticsEntityModel.getClass());

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
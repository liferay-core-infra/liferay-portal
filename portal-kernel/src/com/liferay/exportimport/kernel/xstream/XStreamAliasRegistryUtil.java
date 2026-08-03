/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.xstream;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Máté Thurzó
 */
public class XStreamAliasRegistryUtil {

	public static Map<Class<?>, String> getAliases() {
		Map<Class<?>, String> aliases = new HashMap<>();

		for (Class<?> clazz : _serviceTrackerMap.keySet()) {
			aliases.put(clazz, _serviceTrackerMap.getService(clazz));
		}

		return aliases;
	}

	private XStreamAliasRegistryUtil() {
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static final ServiceTrackerMap<Class<?>, String> _serviceTrackerMap;

	static {
		_serviceTrackerMap =
			ServiceTrackerMapFactory.
				<Class<?>, XStreamAlias, String>openSingleValueMap(
					_bundleContext, XStreamAlias.class, null,
					new ServiceReferenceMapper<Class<?>, XStreamAlias>() {

						@Override
						public void map(
							ServiceReference<XStreamAlias> serviceReference,
							Emitter<Class<?>> emitter) {

							XStreamAlias xStreamAlias =
								_bundleContext.getService(serviceReference);

							if (xStreamAlias != null) {
								emitter.emit(xStreamAlias.getClazz());
							}
						}

					},
					new ServiceTrackerCustomizer<XStreamAlias, String>() {

						@Override
						public String addingService(
							ServiceReference<XStreamAlias> serviceReference) {

							XStreamAlias xStreamAlias =
								_bundleContext.getService(serviceReference);

							if (xStreamAlias != null) {
								return xStreamAlias.getName();
							}

							return null;
						}

						@Override
						public void modifiedService(
							ServiceReference<XStreamAlias> serviceReference,
							String name) {
						}

						@Override
						public void removedService(
							ServiceReference<XStreamAlias> serviceReference,
							String name) {

							_bundleContext.ungetService(serviceReference);
						}

					});
	}

}
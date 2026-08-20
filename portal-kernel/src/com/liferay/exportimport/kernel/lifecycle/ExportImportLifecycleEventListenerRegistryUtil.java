/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.lifecycle;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Daniel Kocsis
 */
public class ExportImportLifecycleEventListenerRegistryUtil {

	public static Set<ExportImportLifecycleListener>
		getAsyncExportImportLifecycleListeners() {

		List<ExportImportLifecycleListener> listeners =
			_serviceTrackerMap.getService(true);

		if (listeners == null) {
			return Collections.emptySet();
		}

		return Collections.unmodifiableSet(new HashSet<>(listeners));
	}

	public static Set<ExportImportLifecycleListener>
		getSyncExportImportLifecycleListeners() {

		List<ExportImportLifecycleListener> listeners =
			_serviceTrackerMap.getService(false);

		if (listeners == null) {
			return Collections.emptySet();
		}

		return Collections.unmodifiableSet(new HashSet<>(listeners));
	}

	private static ExportImportLifecycleListener _wrapListener(
		ExportImportLifecycleListener listener) {

		if (listener instanceof ProcessAwareExportImportLifecycleListener) {
			return ExportImportLifecycleListenerFactoryUtil.create(
				(ProcessAwareExportImportLifecycleListener)listener);
		}

		if (listener instanceof EventAwareExportImportLifecycleListener) {
			return ExportImportLifecycleListenerFactoryUtil.create(
				(EventAwareExportImportLifecycleListener)listener);
		}

		return listener;
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	private static final ServiceTrackerMap
		<Boolean, List<ExportImportLifecycleListener>> _serviceTrackerMap =
			ServiceTrackerMapFactory.openMultiValueMap(
				_bundleContext, ExportImportLifecycleListener.class, null,
				(serviceReference, emitter) -> {
					ExportImportLifecycleListener listener =
						_bundleContext.getService(serviceReference);

					if (listener != null) {
						try {
							ExportImportLifecycleListener wrappedListener =
								_wrapListener(listener);

							emitter.emit(wrappedListener.isParallel());
						}
						finally {
							_bundleContext.ungetService(serviceReference);
						}
					}
				},
				new ServiceTrackerCustomizer
					<ExportImportLifecycleListener,
					 ExportImportLifecycleListener>() {

					@Override
					public ExportImportLifecycleListener addingService(
						ServiceReference<ExportImportLifecycleListener>
							serviceReference) {

						ExportImportLifecycleListener listener =
							_bundleContext.getService(serviceReference);

						return _wrapListener(listener);
					}

					@Override
					public void modifiedService(
						ServiceReference<ExportImportLifecycleListener>
							serviceReference,
						ExportImportLifecycleListener listener) {
					}

					@Override
					public void removedService(
						ServiceReference<ExportImportLifecycleListener>
							serviceReference,
						ExportImportLifecycleListener listener) {

						_bundleContext.ungetService(serviceReference);
					}

				});

}
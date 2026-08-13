/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.osgi.util.tracker;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.configuration.ReindexConfiguration;
import com.liferay.portal.search.internal.instance.lifecycle.IndexOnStartupPortalInstanceLifecycleListener;

import java.io.Serializable;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Michael C. Han
 */
@Component(
	configurationPid = "com.liferay.portal.search.configuration.ReindexConfiguration",
	service = {}
)
public class IndexOnStartupExecutor {

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundleContext = bundleContext;

		_reindexConfiguration = ConfigurableUtil.createConfigurable(
			ReindexConfiguration.class, properties);

		if (PropsValues.INDEX_ON_STARTUP) {
			ScheduledExecutorService scheduledExecutorService =
				Executors.newSingleThreadScheduledExecutor();

			scheduledExecutorService.schedule(
				() -> {
					if (_bundleContext != null) {
						_serviceTrackerMap =
							ServiceTrackerMapFactory.openSingleValueMap(
								_bundleContext,
								(Class<Indexer<?>>)(Class<?>)Indexer.class,
								null,
								(serviceReference, emitter) -> {
									boolean indexOnStartup =
										GetterUtil.getBoolean(
											serviceReference.getProperty(
												PropsKeys.INDEX_ON_STARTUP),
											true);

									Indexer<?> indexer =
										_bundleContext.getService(
											serviceReference);

									if (indexer == null) {
										return;
									}

									String className = indexer.getClassName();

									if (indexOnStartup &&
										Validator.isNotNull(className)) {

										emitter.emit(className);
									}
								},
								new IndexerServiceTrackerCustomizer());
					}
				},
				PropsValues.INDEX_ON_STARTUP_DELAY, TimeUnit.SECONDS);

			scheduledExecutorService.shutdown();
		}
	}

	@Deactivate
	protected void deactivate() {
		_bundleContext = null;

		if (_serviceTrackerMap != null) {
			_serviceTrackerMap.close();
		}
	}

	private BundleContext _bundleContext;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	private volatile ReindexConfiguration _reindexConfiguration;
	private ServiceTrackerMap
		<String, ServiceRegistration<PortalInstanceLifecycleListener>>
			_serviceTrackerMap;

	private class IndexerServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<Indexer<?>, ServiceRegistration<PortalInstanceLifecycleListener>> {

		@Override
		public ServiceRegistration<PortalInstanceLifecycleListener>
			addingService(ServiceReference<Indexer<?>> serviceReference) {

			Indexer<?> indexer = _bundleContext.getService(serviceReference);

			PortalInstanceLifecycleListener portalInstanceLifecycleListener =
				new IndexOnStartupPortalInstanceLifecycleListener(
					_indexWriterHelper, indexer.getClassName(),
					HashMapBuilder.<String, Serializable>put(
						"executionMode",
						_reindexConfiguration.defaultReindexExecutionMode()
					).build());

			return _bundleContext.registerService(
				PortalInstanceLifecycleListener.class,
				portalInstanceLifecycleListener, null);
		}

		@Override
		public void modifiedService(
			ServiceReference<Indexer<?>> serviceReference,
			ServiceRegistration<PortalInstanceLifecycleListener>
				serviceRegistration) {
		}

		@Override
		public void removedService(
			ServiceReference<Indexer<?>> serviceReference,
			ServiceRegistration<PortalInstanceLifecycleListener>
				serviceRegistration) {

			if (serviceRegistration != null) {
				serviceRegistration.unregister();
			}
		}

	}

}
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

package com.liferay.portal.search.internal.buffer;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.configuration.IndexerRegistryConfiguration;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	configurationPid = "com.liferay.portal.search.configuration.IndexerRegistryConfiguration",
	immediate = true, service = IndexerRequestBufferExecutorWatcher.class
)
public class IndexerRequestBufferExecutorWatcher {

	public IndexerRequestBufferExecutor getIndexerRequestBufferExecutor() {
		String bufferedExecutionMode =
			_indexerRegistryConfiguration.bufferedExecutionMode();

		IndexerRequestBufferExecutor indexerRequestBufferExecutor =
			_serviceTrackerMap.getService(bufferedExecutionMode);

		if (indexerRequestBufferExecutor == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Using default indexer request buffered executor for " +
						bufferedExecutionMode);
			}

			indexerRequestBufferExecutor = _defaultIndexerRequestBufferExecutor;
		}

		return indexerRequestBufferExecutor;
	}

	@Activate
	@Modified
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_indexerRegistryConfiguration = ConfigurableUtil.createConfigurable(
			IndexerRegistryConfiguration.class, properties);

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, IndexerRequestBufferExecutor.class, null,
			(serviceReference, emitter) -> {
				String bufferedExecutionMode = GetterUtil.getString(
					serviceReference.getProperty("buffered.execution.mode"));

				if (Validator.isNull(bufferedExecutionMode)) {
					try {
						IndexerRequestBufferExecutor
							indexerRequestBufferExecutor =
								bundleContext.getService(serviceReference);

						throw new IllegalArgumentException(
							"The property \"buffered.execution.mode\" is invalid for " +
								ClassUtil.getClassName(
									indexerRequestBufferExecutor));
					}
					finally {
						bundleContext.ungetService(serviceReference);
					}
				}

				emitter.emit(bufferedExecutionMode);
			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		IndexerRequestBufferExecutorWatcher.class);

	@Reference(target = "(buffered.execution.mode=DEFAULT)")
	private IndexerRequestBufferExecutor _defaultIndexerRequestBufferExecutor;

	private volatile IndexerRegistryConfiguration _indexerRegistryConfiguration;
	private volatile ServiceTrackerMap<String, IndexerRequestBufferExecutor>
		_serviceTrackerMap;

}
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

package com.liferay.portal.search.elasticsearch7.internal.index;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index.CreateIndexRequestExecutor;
import com.liferay.portal.search.elasticsearch7.spi.index.IndexRegistrar;
import com.liferay.portal.search.elasticsearch7.spi.index.helper.IndexSettingsDefinition;
import com.liferay.portal.search.engine.adapter.index.CreateIndexRequest;
import com.liferay.portal.search.engine.adapter.index.CreateIndexResponse;
import com.liferay.portal.search.spi.index.IndexDefinition;

import java.util.function.Consumer;

import org.elasticsearch.ElasticsearchStatusException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author André de Oliveira
 */
@Component(immediate = true, service = IndexSynchronizer.class)
public class IndexSynchronizerImpl implements IndexSynchronizer {

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED, unbind = "-")
	public void setModuleServiceLifecycle(
		ModuleServiceLifecycle moduleServiceLifecycle) {

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Portal is initialized and indexes will be synchronized");
		}
	}

	@Override
	public void synchronizeIndexDefinition(
		IndexDefinitionData indexDefinitionData) {

		String index = indexDefinitionData.getIndex();

		createIndex(
			index,
			createIndexRequest -> {
				if (_log.isDebugEnabled()) {
					_log.debug("Synchronizing index " + index);
				}

				createIndexRequest.setSource(indexDefinitionData.getSource());
			});
	}

	@Override
	public void synchronizeIndexRegistrar(IndexRegistrar indexRegistrar) {
		indexRegistrar.register(
			(indexName, indexSettingsDefinitionConsumer) -> createIndex(
				indexName,
				createIndexRequest -> indexSettingsDefinitionConsumer.accept(
					new IndexSettingsDefinition() {

						@Override
						public void setIndexSettingsResourceName(
							String indexSettingsResourceName) {

							createIndexRequest.setSource(
								StringUtil.read(
									indexSettingsDefinitionConsumer.getClass(),
									indexSettingsResourceName));
						}

						@Override
						public void setSource(String source) {
							createIndexRequest.setSource(source);
						}

					})));
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_indexDefinitionServiceTracker = ServiceTrackerFactory.open(
			bundleContext, IndexDefinition.class,
			new ServiceTrackerCustomizer<IndexDefinition, IndexDefinition>() {

				@Override
				public IndexDefinition addingService(
					ServiceReference<IndexDefinition> serviceReference) {

					IndexDefinition indexDefinition = bundleContext.getService(
						serviceReference);

					synchronizeIndexDefinition(
						new IndexDefinitionData(
							indexDefinition,
							HashMapBuilder.put(
								IndexDefinition.PROPERTY_KEY_INDEX_NAME,
								serviceReference.getProperty(
									IndexDefinition.PROPERTY_KEY_INDEX_NAME)
							).put(
								IndexDefinition.
									PROPERTY_KEY_INDEX_SETTINGS_RESOURCE_NAME,
								serviceReference.getProperty(
									IndexDefinition.
										PROPERTY_KEY_INDEX_SETTINGS_RESOURCE_NAME)
							).build()));

					return indexDefinition;
				}

				@Override
				public void modifiedService(
					ServiceReference<IndexDefinition> serviceReference,
					IndexDefinition indexDefinition) {
				}

				@Override
				public void removedService(
					ServiceReference<IndexDefinition> serviceReference,
					IndexDefinition indexDefinition) {

					bundleContext.ungetService(serviceReference);
				}

			});

		_indexRegistrarServiceTracker = ServiceTrackerFactory.open(
			bundleContext, IndexRegistrar.class,
			new ServiceTrackerCustomizer<IndexRegistrar, IndexRegistrar>() {

				@Override
				public IndexRegistrar addingService(
					ServiceReference<IndexRegistrar> serviceReference) {

					IndexRegistrar indexRegistrar = bundleContext.getService(
						serviceReference);

					synchronizeIndexRegistrar(indexRegistrar);

					return indexRegistrar;
				}

				@Override
				public void modifiedService(
					ServiceReference<IndexRegistrar> serviceReference,
					IndexRegistrar indexRegistrar) {
				}

				@Override
				public void removedService(
					ServiceReference<IndexRegistrar> serviceReference,
					IndexRegistrar indexRegistrar) {

					bundleContext.ungetService(serviceReference);
				}

			});
	}

	protected void createIndex(
		String index, Consumer<CreateIndexRequest> createIndexRequestConsumer) {

		CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);

		createIndexRequestConsumer.accept(createIndexRequest);

		try {
			CreateIndexResponse createIndexResponse =
				_createIndexRequestExecutor.execute(createIndexRequest);

			if (_log.isInfoEnabled()) {
				_log.info(
					"Index created: " + createIndexResponse.getIndexName());
			}
		}
		catch (ElasticsearchStatusException elasticsearchStatusException) {
			String message = elasticsearchStatusException.getMessage();

			if ((message != null) &&
				message.contains("resource_already_exists_exception")) {

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Skipping index creation because it already exists: " +
							createIndexRequest.getIndexName(),
						elasticsearchStatusException);
				}
			}
			else {
				throw elasticsearchStatusException;
			}
		}
	}

	@Deactivate
	protected void deactivate() {
		if (_indexDefinitionServiceTracker != null) {
			_indexDefinitionServiceTracker.close();
		}

		if (_indexRegistrarServiceTracker != null) {
			_indexRegistrarServiceTracker.close();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		IndexSynchronizerImpl.class);

	@Reference
	private CreateIndexRequestExecutor _createIndexRequestExecutor;

	private ServiceTracker<IndexDefinition, IndexDefinition>
		_indexDefinitionServiceTracker;
	private ServiceTracker<IndexRegistrar, IndexRegistrar>
		_indexRegistrarServiceTracker;

}
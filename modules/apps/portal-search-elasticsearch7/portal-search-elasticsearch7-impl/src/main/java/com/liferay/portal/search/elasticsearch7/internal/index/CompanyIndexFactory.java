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

import com.liferay.osgi.service.tracker.collections.EagerServiceTrackerCustomizer;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.ccr.CrossClusterReplicationHelper;
import com.liferay.portal.search.elasticsearch7.internal.configuration.ElasticsearchConfigurationObserver;
import com.liferay.portal.search.elasticsearch7.internal.configuration.ElasticsearchConfigurationWrapper;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnectionManager;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnectionNotInitializedException;
import com.liferay.portal.search.elasticsearch7.internal.helper.SearchLogHelperUtil;
import com.liferay.portal.search.elasticsearch7.internal.settings.SettingsBuilder;
import com.liferay.portal.search.elasticsearch7.internal.util.ResourceUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.UpdateIndexSettingsIndexRequest;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.spi.model.index.contributor.IndexContributor;
import com.liferay.portal.search.spi.settings.IndexSettingsContributor;

import java.io.IOException;

import java.util.HashSet;
import java.util.Set;

import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.xcontent.XContentType;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Michael C. Han
 */
@Component(service = IndexFactory.class)
public class CompanyIndexFactory
	implements ElasticsearchConfigurationObserver, IndexFactory {

	@Override
	public int compareTo(
		ElasticsearchConfigurationObserver elasticsearchConfigurationObserver) {

		return elasticsearchConfigurationWrapper.compare(
			this, elasticsearchConfigurationObserver);
	}

	@Override
	public void createIndices(IndicesClient indicesClient, long companyId) {
		String indexName = getIndexName(companyId);

		if (hasIndex(indicesClient, indexName)) {
			return;
		}

		createIndex(indexName, indicesClient);
	}

	@Override
	public void deleteIndices(IndicesClient indicesClient, long companyId) {
		String indexName = getIndexName(companyId);

		if (FeatureFlagManagerUtil.isEnabled("LPS-177664")) {
			Company company = companyLocalService.fetchCompany(companyId);

			if ((company != null) &&
				!Validator.isBlank(company.getIndexNameCurrent())) {

				indexName = company.getIndexNameCurrent();
			}
		}

		if (!hasIndex(indicesClient, indexName)) {
			return;
		}

		_executeIndexContributorsBeforeRemove(indexName);

		deleteIndex(indexName, indicesClient, companyId, true);
	}

	@Override
	public int getPriority() {
		return 3;
	}

	@Override
	public void onElasticsearchConfigurationUpdate() {
		_createCompanyIndexes();

		_updateMaxResultWindow();
	}

	@Override
	public synchronized void registerCompanyId(long companyId) {
		_companyIds.add(companyId);
	}

	@Override
	public synchronized void unregisterCompanyId(long companyId) {
		_companyIds.remove(companyId);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_indexContributorServiceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, IndexContributor.class);

		_indexSettingsContributorServiceTrackerList =
			ServiceTrackerListFactory.open(
				bundleContext, IndexSettingsContributor.class, null,
				new EagerServiceTrackerCustomizer
					<IndexSettingsContributor, IndexSettingsContributor>() {

					@Override
					public IndexSettingsContributor addingService(
						ServiceReference<IndexSettingsContributor>
							serviceReference) {

						IndexSettingsContributor indexSettingsContributor =
							bundleContext.getService(serviceReference);

						_processContributions(indexSettingsContributor);

						return indexSettingsContributor;
					}

					@Override
					public void modifiedService(
						ServiceReference<IndexSettingsContributor>
							serviceReference,
						IndexSettingsContributor indexSettingsContributor) {
					}

					@Override
					public void removedService(
						ServiceReference<IndexSettingsContributor>
							serviceReference,
						IndexSettingsContributor indexSettingsContributor) {

						bundleContext.ungetService(serviceReference);
					}

				});

		elasticsearchConfigurationWrapper.register(this);

		_createCompanyIndexes();
	}

	protected void createIndex(String indexName, IndicesClient indicesClient) {
		CreateIndexRequest createIndexRequest = new CreateIndexRequest(
			indexName);

		LiferayDocumentTypeFactory liferayDocumentTypeFactory =
			new LiferayDocumentTypeFactory(indicesClient, jsonFactory);

		_setSettings(createIndexRequest, liferayDocumentTypeFactory);

		_addLiferayDocumentTypeMappings(
			createIndexRequest, liferayDocumentTypeFactory);

		try {
			ActionResponse actionResponse = indicesClient.create(
				createIndexRequest, RequestOptions.DEFAULT);

			SearchLogHelperUtil.logActionResponse(_log, actionResponse);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		_updateLiferayDocumentType(indexName, liferayDocumentTypeFactory);

		_executeIndexContributorsAfterCreate(indexName);
	}

	@Deactivate
	protected void deactivate() {
		if (_indexContributorServiceTrackerList != null) {
			_indexContributorServiceTrackerList.close();
		}

		if (_indexSettingsContributorServiceTrackerList != null) {
			_indexSettingsContributorServiceTrackerList.close();
		}

		elasticsearchConfigurationWrapper.unregister(this);
	}

	protected void deleteIndex(
		String indexName, IndicesClient indicesClient, long companyId,
		boolean resetBothIndexNames) {

		DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(
			indexName);

		try {
			ActionResponse actionResponse = indicesClient.delete(
				deleteIndexRequest, RequestOptions.DEFAULT);

			SearchLogHelperUtil.logActionResponse(_log, actionResponse);

			if (FeatureFlagManagerUtil.isEnabled("LPS-177664") &&
				(companyId != CompanyConstants.SYSTEM)) {

				if (resetBothIndexNames) {
					companyLocalService.updateIndexNames(companyId, null, null);
				}
				else {
					companyLocalService.updateIndexNameNext(companyId, null);
				}
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	protected String getIndexName(long companyId) {
		return indexNameBuilder.getIndexName(companyId);
	}

	protected boolean hasIndex(IndicesClient indicesClient, String indexName) {
		GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);

		try {
			return indicesClient.exists(
				getIndexRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	protected void loadAdditionalTypeMappings(
		String indexName,
		LiferayDocumentTypeFactory liferayDocumentTypeFactory) {

		if (Validator.isNull(
				elasticsearchConfigurationWrapper.additionalTypeMappings())) {

			return;
		}

		liferayDocumentTypeFactory.addTypeMappings(
			indexName,
			elasticsearchConfigurationWrapper.additionalTypeMappings());
	}

	@Reference
	protected CompanyLocalService companyLocalService;

	@Reference(
		cardinality = ReferenceCardinality.OPTIONAL,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected volatile CrossClusterReplicationHelper
		crossClusterReplicationHelper;

	@Reference
	protected volatile ElasticsearchConfigurationWrapper
		elasticsearchConfigurationWrapper;

	@Reference
	protected ElasticsearchConnectionManager elasticsearchConnectionManager;

	@Reference
	protected IndexNameBuilder indexNameBuilder;

	@Reference
	protected JSONFactory jsonFactory;

	@Reference
	protected SearchEngineAdapter searchEngineAdapter;

	private void _addLiferayDocumentTypeMappings(
		CreateIndexRequest createIndexRequest,
		LiferayDocumentTypeFactory liferayDocumentTypeFactory) {

		if (Validator.isNotNull(
				elasticsearchConfigurationWrapper.overrideTypeMappings())) {

			liferayDocumentTypeFactory.createLiferayDocumentTypeMappings(
				createIndexRequest,
				elasticsearchConfigurationWrapper.overrideTypeMappings());
		}
		else {
			liferayDocumentTypeFactory.createRequiredDefaultTypeMappings(
				createIndexRequest);
		}
	}

	private synchronized void _createCompanyIndexes() {
		for (Long companyId : _companyIds) {
			try {
				RestHighLevelClient restHighLevelClient =
					elasticsearchConnectionManager.getRestHighLevelClient();

				createIndices(restHighLevelClient.indices(), companyId);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to reinitialize index for company " + companyId,
						exception);
				}
			}
		}
	}

	private void _executeIndexContributorAfterCreate(
		IndexContributor indexContributor, String indexName) {

		try {
			indexContributor.onAfterCreate(indexName);
		}
		catch (Throwable throwable) {
			_log.error(
				StringBundler.concat(
					"Unable to apply contributor ", indexContributor,
					"to index ", indexName),
				throwable);
		}
	}

	private void _executeIndexContributorBeforeRemove(
		IndexContributor indexContributor, String indexName) {

		try {
			indexContributor.onBeforeRemove(indexName);
		}
		catch (Throwable throwable) {
			_log.error(
				StringBundler.concat(
					"Unable to apply contributor ", indexContributor,
					" when removing index ", indexName),
				throwable);
		}
	}

	private void _executeIndexContributorsAfterCreate(String indexName) {
		for (IndexContributor indexContributor :
				_indexContributorServiceTrackerList) {

			_executeIndexContributorAfterCreate(indexContributor, indexName);
		}
	}

	private void _executeIndexContributorsBeforeRemove(String indexName) {
		for (IndexContributor indexContributor :
				_indexContributorServiceTrackerList) {

			_executeIndexContributorBeforeRemove(indexContributor, indexName);
		}
	}

	private void _loadAdditionalIndexConfigurations(
		SettingsBuilder settingsBuilder) {

		settingsBuilder.loadFromSource(
			elasticsearchConfigurationWrapper.additionalIndexConfigurations());
	}

	private void _loadDefaultIndexSettings(SettingsBuilder settingsBuilder) {
		Settings.Builder builder = settingsBuilder.getBuilder();

		String defaultIndexSettings = ResourceUtil.getResourceAsString(
			getClass(), "/META-INF/index-settings-defaults.json");

		builder.loadFromSource(defaultIndexSettings, XContentType.JSON);
	}

	private void _loadIndexConfigurations(SettingsBuilder settingsBuilder) {
		settingsBuilder.put(
			"index.number_of_replicas",
			elasticsearchConfigurationWrapper.indexNumberOfReplicas());
		settingsBuilder.put(
			"index.number_of_shards",
			elasticsearchConfigurationWrapper.indexNumberOfShards());
		settingsBuilder.put(
			"index.max_result_window",
			String.valueOf(
				elasticsearchConfigurationWrapper.indexMaxResultWindow()));
	}

	private void _loadIndexSettingsContributors(Settings.Builder builder) {
		for (IndexSettingsContributor indexSettingsContributor :
				_indexSettingsContributorServiceTrackerList) {

			indexSettingsContributor.populate(builder::put);
		}
	}

	private void _loadTestModeIndexSettings(SettingsBuilder settingsBuilder) {
		if (!PortalRunMode.isTestMode()) {
			return;
		}

		settingsBuilder.put("index.refresh_interval", "1ms");
		settingsBuilder.put("index.search.slowlog.threshold.fetch.warn", "-1");
		settingsBuilder.put("index.search.slowlog.threshold.query.warn", "-1");
		settingsBuilder.put("index.translog.sync_interval", "100ms");
	}

	private void _loadTypeMappingsContributors(
		String indexName,
		LiferayDocumentTypeFactory liferayDocumentTypeFactory) {

		for (IndexSettingsContributor indexSettingsContributor :
				_indexSettingsContributorServiceTrackerList) {

			indexSettingsContributor.contribute(
				indexName, liferayDocumentTypeFactory);
		}
	}

	private void _processContributions(
		IndexSettingsContributor indexSettingsContributor) {

		if (Validator.isNotNull(
				elasticsearchConfigurationWrapper.overrideTypeMappings())) {

			return;
		}

		RestHighLevelClient restHighLevelClient = null;

		try {
			restHighLevelClient =
				elasticsearchConnectionManager.getRestHighLevelClient();
		}
		catch (ElasticsearchConnectionNotInitializedException
					elasticsearchConnectionNotInitializedException) {

			if (_log.isInfoEnabled()) {
				_log.info("Skipping index settings contributor");
			}

			return;
		}

		LiferayDocumentTypeFactory liferayDocumentTypeFactory =
			new LiferayDocumentTypeFactory(
				restHighLevelClient.indices(), jsonFactory);

		for (Long companyId : _companyIds) {
			indexSettingsContributor.contribute(
				getIndexName(companyId), liferayDocumentTypeFactory);
		}
	}

	private void _setSettings(
		CreateIndexRequest createIndexRequest,
		LiferayDocumentTypeFactory liferayDocumentTypeFactory) {

		Settings.Builder builder = Settings.builder();

		liferayDocumentTypeFactory.createRequiredDefaultAnalyzers(builder);

		SettingsBuilder settingsBuilder = new SettingsBuilder(builder);

		_loadDefaultIndexSettings(settingsBuilder);

		_loadTestModeIndexSettings(settingsBuilder);

		_loadIndexConfigurations(settingsBuilder);

		_loadAdditionalIndexConfigurations(settingsBuilder);

		_loadIndexSettingsContributors(builder);

		if (Validator.isNotNull(builder.get("index.number_of_replicas"))) {
			builder.put("index.auto_expand_replicas", false);
		}

		createIndexRequest.settings(builder);
	}

	private void _updateLiferayDocumentType(
		String indexName,
		LiferayDocumentTypeFactory liferayDocumentTypeFactory) {

		if (Validator.isNotNull(
				elasticsearchConfigurationWrapper.overrideTypeMappings())) {

			return;
		}

		loadAdditionalTypeMappings(indexName, liferayDocumentTypeFactory);

		_loadTypeMappingsContributors(indexName, liferayDocumentTypeFactory);

		liferayDocumentTypeFactory.createOptionalDefaultTypeMappings(indexName);
	}

	private void _updateMaxResultWindow() {
		int maxResultWindow =
			elasticsearchConfigurationWrapper.indexMaxResultWindow();

		for (Long companyId : _companyIds) {
			String indexName = indexNameBuilder.getIndexName(companyId);

			UpdateIndexSettingsIndexRequest updateIndexSettingsIndexRequest =
				new UpdateIndexSettingsIndexRequest(indexName);

			updateIndexSettingsIndexRequest.setSettings(
				"{\"index.max_result_window\": " + maxResultWindow + "}");

			searchEngineAdapter.execute(updateIndexSettingsIndexRequest);

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Updated index.max_result_window to ", maxResultWindow,
						" for index ", indexName));
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompanyIndexFactory.class);

	private final Set<Long> _companyIds = new HashSet<>();
	private ServiceTrackerList<IndexContributor>
		_indexContributorServiceTrackerList;
	private ServiceTrackerList<IndexSettingsContributor>
		_indexSettingsContributorServiceTrackerList;

}
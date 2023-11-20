/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnectionManager;
import com.liferay.portal.search.engine.adapter.index.IndexRequestExecutor;

/**
 * @author Dylan Rebelak
 */
public class IndexRequestExecutorFixture {

	public IndexRequestExecutor getIndexRequestExecutor() {
		return _indexRequestExecutor;
	}

	public void setUp() {
		_indexRequestExecutor = new ElasticsearchIndexRequestExecutor();

		ReflectionTestUtil.setFieldValue(
			_indexRequestExecutor, "_analyzeIndexRequestExecutor",
			_createAnalyzeIndexRequestExecutor(
				_elasticsearchConnectionManager));

		IndicesOptionsTranslator indicesOptionsTranslator =
			new IndicesOptionsTranslatorImpl();

		ReflectionTestUtil.setFieldValue(
			_indexRequestExecutor, "_closeIndexRequestExecutor",
			_createCloseIndexRequestExecutor(
				_elasticsearchConnectionManager, indicesOptionsTranslator));

		ReflectionTestUtil.setFieldValue(
			_indexRequestExecutor, "_createIndexRequestExecutor",
			_createCreateIndexRequestExecutor(_elasticsearchConnectionManager));
		ReflectionTestUtil.setFieldValue(
			_indexRequestExecutor, "_deleteIndexRequestExecutor",
			_createDeleteIndexRequestExecutor(
				_elasticsearchConnectionManager, indicesOptionsTranslator));

		IndexRequestShardFailureTranslator indexRequestShardFailureTranslator =
			new IndexRequestShardFailureTranslatorImpl();

		ReflectionTestUtil.setFieldValue(
			_indexRequestExecutor, "_flushIndexRequestExecutor",
			_createFlushIndexRequestExecutor(
				_elasticsearchConnectionManager,
				indexRequestShardFailureTranslator));

		ReflectionTestUtil.setFieldValue(
			_indexRequestExecutor, "_getFieldMappingIndexRequestExecutor",
			_createGetFieldMappingIndexRequestExecutor(
				_elasticsearchConnectionManager));
		ReflectionTestUtil.setFieldValue(
			_indexRequestExecutor, "_getIndexIndexRequestExecutor",
			_createGetIndexIndexRequestExecutor(
				_elasticsearchConnectionManager));
		ReflectionTestUtil.setFieldValue(
			_indexRequestExecutor, "_getMappingIndexRequestExecutor",
			_createGetMappingIndexRequestExecutor(
				_elasticsearchConnectionManager));
		ReflectionTestUtil.setFieldValue(
			_indexRequestExecutor, "_indicesExistsIndexRequestExecutor",
			_createIndexExistsIndexRequestExecutor(
				_elasticsearchConnectionManager));
		ReflectionTestUtil.setFieldValue(
			_indexRequestExecutor, "_openIndexRequestExecutor",
			_createOpenIndexRequestExecutor(
				_elasticsearchConnectionManager, indicesOptionsTranslator));
		ReflectionTestUtil.setFieldValue(
			_indexRequestExecutor, "_putMappingIndexRequestExecutor",
			_createPutMappingIndexRequestExecutor(
				_elasticsearchConnectionManager));
		ReflectionTestUtil.setFieldValue(
			_indexRequestExecutor, "_refreshIndexRequestExecutor",
			_createRefreshIndexRequestExecutor(
				_elasticsearchConnectionManager,
				indexRequestShardFailureTranslator));
		ReflectionTestUtil.setFieldValue(
			_indexRequestExecutor, "_updateIndexSettingsIndexRequestExecutor",
			_createUpdateIndexSettingsIndexRequestExecutor(
				_elasticsearchConnectionManager, indicesOptionsTranslator));
	}

	protected void setElasticsearchClientResolver(
		ElasticsearchConnectionManager elasticsearchConnectionManager) {

		_elasticsearchConnectionManager = elasticsearchConnectionManager;
	}

	private AnalyzeIndexRequestExecutor _createAnalyzeIndexRequestExecutor(
		ElasticsearchConnectionManager elasticsearchConnectionManager) {

		AnalyzeIndexRequestExecutor analyzeIndexRequestExecutor =
			new AnalyzeIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			analyzeIndexRequestExecutor, "_elasticsearchConnectionManager",
			elasticsearchConnectionManager);

		return analyzeIndexRequestExecutor;
	}

	private CloseIndexRequestExecutor _createCloseIndexRequestExecutor(
		ElasticsearchConnectionManager elasticsearchConnectionManager,
		IndicesOptionsTranslator indicesOptionsTranslator) {

		CloseIndexRequestExecutor closeIndexRequestExecutor =
			new CloseIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			closeIndexRequestExecutor, "_elasticsearchConnectionManager",
			elasticsearchConnectionManager);
		ReflectionTestUtil.setFieldValue(
			closeIndexRequestExecutor, "_indicesOptionsTranslator",
			indicesOptionsTranslator);

		return closeIndexRequestExecutor;
	}

	private CreateIndexRequestExecutor _createCreateIndexRequestExecutor(
		ElasticsearchConnectionManager elasticsearchConnectionManager) {

		CreateIndexRequestExecutor createIndexRequestExecutor =
			new CreateIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			createIndexRequestExecutor, "_elasticsearchConnectionManager",
			elasticsearchConnectionManager);

		return createIndexRequestExecutor;
	}

	private DeleteIndexRequestExecutor _createDeleteIndexRequestExecutor(
		ElasticsearchConnectionManager elasticsearchConnectionManager,
		IndicesOptionsTranslator indicesOptionsTranslator) {

		DeleteIndexRequestExecutor deleteIndexRequestExecutor =
			new DeleteIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			deleteIndexRequestExecutor, "_elasticsearchConnectionManager",
			elasticsearchConnectionManager);
		ReflectionTestUtil.setFieldValue(
			deleteIndexRequestExecutor, "_indicesOptionsTranslator",
			indicesOptionsTranslator);

		return deleteIndexRequestExecutor;
	}

	private FlushIndexRequestExecutor _createFlushIndexRequestExecutor(
		ElasticsearchConnectionManager elasticsearchConnectionManager,
		IndexRequestShardFailureTranslator indexRequestShardFailureTranslator) {

		FlushIndexRequestExecutor flushIndexRequestExecutor =
			new FlushIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			flushIndexRequestExecutor, "_elasticsearchConnectionManager",
			elasticsearchConnectionManager);
		ReflectionTestUtil.setFieldValue(
			flushIndexRequestExecutor, "_indexRequestShardFailureTranslator",
			indexRequestShardFailureTranslator);

		return flushIndexRequestExecutor;
	}

	private GetFieldMappingIndexRequestExecutor
		_createGetFieldMappingIndexRequestExecutor(
			ElasticsearchConnectionManager elasticsearchConnectionManager) {

		GetFieldMappingIndexRequestExecutor
			getFieldMappingIndexRequestExecutor =
				new GetFieldMappingIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			getFieldMappingIndexRequestExecutor,
			"_elasticsearchConnectionManager", elasticsearchConnectionManager);

		ReflectionTestUtil.setFieldValue(
			getFieldMappingIndexRequestExecutor, "_jsonFactory",
			new JSONFactoryImpl());

		return getFieldMappingIndexRequestExecutor;
	}

	private GetIndexIndexRequestExecutor _createGetIndexIndexRequestExecutor(
		ElasticsearchConnectionManager elasticsearchConnectionManager) {

		GetIndexIndexRequestExecutor getIndexIndexRequestExecutor =
			new GetIndexIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			getIndexIndexRequestExecutor, "_elasticsearchConnectionManager",
			elasticsearchConnectionManager);

		return getIndexIndexRequestExecutor;
	}

	private GetMappingIndexRequestExecutor
		_createGetMappingIndexRequestExecutor(
			ElasticsearchConnectionManager elasticsearchConnectionManager) {

		GetMappingIndexRequestExecutor getMappingIndexRequestExecutor =
			new GetMappingIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			getMappingIndexRequestExecutor, "_elasticsearchConnectionManager",
			elasticsearchConnectionManager);

		return getMappingIndexRequestExecutor;
	}

	private IndicesExistsIndexRequestExecutor
		_createIndexExistsIndexRequestExecutor(
			ElasticsearchConnectionManager elasticsearchConnectionManager) {

		IndicesExistsIndexRequestExecutor indicesExistsIndexRequestExecutor =
			new IndicesExistsIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			indicesExistsIndexRequestExecutor,
			"_elasticsearchConnectionManager", elasticsearchConnectionManager);

		return indicesExistsIndexRequestExecutor;
	}

	private OpenIndexRequestExecutor _createOpenIndexRequestExecutor(
		ElasticsearchConnectionManager elasticsearchConnectionManager,
		IndicesOptionsTranslator indicesOptionsTranslator) {

		OpenIndexRequestExecutor openIndexRequestExecutor =
			new OpenIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			openIndexRequestExecutor, "_elasticsearchConnectionManager",
			elasticsearchConnectionManager);
		ReflectionTestUtil.setFieldValue(
			openIndexRequestExecutor, "_indicesOptionsTranslator",
			indicesOptionsTranslator);

		return openIndexRequestExecutor;
	}

	private PutMappingIndexRequestExecutor
		_createPutMappingIndexRequestExecutor(
			ElasticsearchConnectionManager elasticsearchConnectionManager) {

		PutMappingIndexRequestExecutor putMappingIndexRequestExecutor =
			new PutMappingIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			putMappingIndexRequestExecutor, "_elasticsearchConnectionManager",
			elasticsearchConnectionManager);

		return putMappingIndexRequestExecutor;
	}

	private RefreshIndexRequestExecutor _createRefreshIndexRequestExecutor(
		ElasticsearchConnectionManager elasticsearchConnectionManager,
		IndexRequestShardFailureTranslator indexRequestShardFailureTranslator) {

		RefreshIndexRequestExecutor refreshIndexRequestExecutor =
			new RefreshIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			refreshIndexRequestExecutor, "_elasticsearchConnectionManager",
			elasticsearchConnectionManager);
		ReflectionTestUtil.setFieldValue(
			refreshIndexRequestExecutor, "_indexRequestShardFailureTranslator",
			indexRequestShardFailureTranslator);

		return refreshIndexRequestExecutor;
	}

	private UpdateIndexSettingsIndexRequestExecutor
		_createUpdateIndexSettingsIndexRequestExecutor(
			ElasticsearchConnectionManager elasticsearchConnectionManager,
			IndicesOptionsTranslator indicesOptionsTranslator) {

		UpdateIndexSettingsIndexRequestExecutor
			updateIndexSettingsIndexRequestExecutor =
				new UpdateIndexSettingsIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			updateIndexSettingsIndexRequestExecutor,
			"_elasticsearchConnectionManager", elasticsearchConnectionManager);
		ReflectionTestUtil.setFieldValue(
			updateIndexSettingsIndexRequestExecutor,
			"_indicesOptionsTranslator", indicesOptionsTranslator);

		return updateIndexSettingsIndexRequestExecutor;
	}

	private ElasticsearchConnectionManager _elasticsearchConnectionManager;
	private IndexRequestExecutor _indexRequestExecutor;

}
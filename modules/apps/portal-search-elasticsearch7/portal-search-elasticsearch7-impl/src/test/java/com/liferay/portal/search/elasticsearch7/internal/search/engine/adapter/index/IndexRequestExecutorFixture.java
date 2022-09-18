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

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.index.IndexRequestExecutor;

/**
 * @author Dylan Rebelak
 */
public class IndexRequestExecutorFixture {

	public IndexRequestExecutor getIndexRequestExecutor() {
		return _indexRequestExecutor;
	}

	public void setUp() {
		IndexRequestShardFailureTranslator indexRequestShardFailureTranslator =
			new IndexRequestShardFailureTranslatorImpl();

		IndicesOptionsTranslator indicesOptionsTranslator =
			new IndicesOptionsTranslatorImpl();

		IndexRequestExecutor elasticsearchIndexRequestExecutor =
			new ElasticsearchIndexRequestExecutor();

		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexRequestExecutor, "_analyzeIndexRequestExecutor",
			_createAnalyzeIndexRequestExecutor(_elasticsearchClientResolver));
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexRequestExecutor, "_closeIndexRequestExecutor",
			_createCloseIndexRequestExecutor(
				indicesOptionsTranslator, _elasticsearchClientResolver));
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexRequestExecutor, "_createIndexRequestExecutor",
			_createCreateIndexRequestExecutor(_elasticsearchClientResolver));
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexRequestExecutor, "_deleteIndexRequestExecutor",
			_createDeleteIndexRequestExecutor(
				indicesOptionsTranslator, _elasticsearchClientResolver));
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexRequestExecutor, "_flushIndexRequestExecutor",
			_createFlushIndexRequestExecutor(
				indexRequestShardFailureTranslator,
				_elasticsearchClientResolver));
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexRequestExecutor,
			"_getFieldMappingIndexRequestExecutor",
			_createGetFieldMappingIndexRequestExecutor(
				_elasticsearchClientResolver));
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexRequestExecutor, "_getIndexIndexRequestExecutor",
			_createGetIndexIndexRequestExecutor(_elasticsearchClientResolver));
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexRequestExecutor,
			"_getMappingIndexRequestExecutor",
			_createGetMappingIndexRequestExecutor(
				_elasticsearchClientResolver));
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexRequestExecutor,
			"_indicesExistsIndexRequestExecutor",
			_createIndexExistsIndexRequestExecutor(
				_elasticsearchClientResolver));
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexRequestExecutor, "_openIndexRequestExecutor",
			_createOpenIndexRequestExecutor(
				indicesOptionsTranslator, _elasticsearchClientResolver));
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexRequestExecutor,
			"_putMappingIndexRequestExecutor",
			_createPutMappingIndexRequestExecutor(
				_elasticsearchClientResolver));
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexRequestExecutor, "_refreshIndexRequestExecutor",
			_createRefreshIndexRequestExecutor(
				indexRequestShardFailureTranslator,
				_elasticsearchClientResolver));
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexRequestExecutor,
			"_updateIndexSettingsIndexRequestExecutor",
			_createUpdateIndexSettingsIndexRequestExecutor(
				indicesOptionsTranslator, _elasticsearchClientResolver));

		_indexRequestExecutor = elasticsearchIndexRequestExecutor;
	}

	protected void setElasticsearchClientResolver(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	private AnalyzeIndexRequestExecutor _createAnalyzeIndexRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		AnalyzeIndexRequestExecutor analyzeIndexRequestExecutorImpl =
			new AnalyzeIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			analyzeIndexRequestExecutorImpl, "_elasticsearchClientResolver",
			elasticsearchClientResolver);

		return analyzeIndexRequestExecutorImpl;
	}

	private CloseIndexRequestExecutor _createCloseIndexRequestExecutor(
		IndicesOptionsTranslator indicesOptionsTranslator,
		ElasticsearchClientResolver elasticsearchClientResolver) {

		CloseIndexRequestExecutor closeIndexRequestExecutorImpl =
			new CloseIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			closeIndexRequestExecutorImpl, "_elasticsearchClientResolver",
			elasticsearchClientResolver);
		ReflectionTestUtil.setFieldValue(
			closeIndexRequestExecutorImpl, "_indicesOptionsTranslator",
			indicesOptionsTranslator);

		return closeIndexRequestExecutorImpl;
	}

	private CreateIndexRequestExecutor _createCreateIndexRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		CreateIndexRequestExecutor createIndexRequestExecutorImpl =
			new CreateIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			createIndexRequestExecutorImpl, "_elasticsearchClientResolver",
			elasticsearchClientResolver);

		return createIndexRequestExecutorImpl;
	}

	private DeleteIndexRequestExecutor _createDeleteIndexRequestExecutor(
		IndicesOptionsTranslator indicesOptionsTranslator,
		ElasticsearchClientResolver elasticsearchClientResolver) {

		DeleteIndexRequestExecutor deleteIndexRequestExecutorImpl =
			new DeleteIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			deleteIndexRequestExecutorImpl, "_elasticsearchClientResolver",
			elasticsearchClientResolver);
		ReflectionTestUtil.setFieldValue(
			deleteIndexRequestExecutorImpl, "_indicesOptionsTranslator",
			indicesOptionsTranslator);

		return deleteIndexRequestExecutorImpl;
	}

	private FlushIndexRequestExecutor _createFlushIndexRequestExecutor(
		IndexRequestShardFailureTranslator indexRequestShardFailureTranslator,
		ElasticsearchClientResolver elasticsearchClientResolver) {

		FlushIndexRequestExecutor flushIndexRequestExecutor =
			new FlushIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			flushIndexRequestExecutor, "_elasticsearchClientResolver",
			elasticsearchClientResolver);
		ReflectionTestUtil.setFieldValue(
			flushIndexRequestExecutor, "_indexRequestShardFailureTranslator",
			indexRequestShardFailureTranslator);

		return flushIndexRequestExecutor;
	}

	private GetFieldMappingIndexRequestExecutor
		_createGetFieldMappingIndexRequestExecutor(
			ElasticsearchClientResolver elasticsearchClientResolver) {

		GetFieldMappingIndexRequestExecutor
			getFieldMappingIndexRequestExecutor =
				new GetFieldMappingIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			getFieldMappingIndexRequestExecutor, "_elasticsearchClientResolver",
			elasticsearchClientResolver);

		return getFieldMappingIndexRequestExecutor;
	}

	private GetIndexIndexRequestExecutor _createGetIndexIndexRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		GetIndexIndexRequestExecutor getIndexIndexRequestExecutor =
			new GetIndexIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			getIndexIndexRequestExecutor, "_elasticsearchClientResolver",
			elasticsearchClientResolver);

		return getIndexIndexRequestExecutor;
	}

	private GetMappingIndexRequestExecutor
		_createGetMappingIndexRequestExecutor(
			ElasticsearchClientResolver elasticsearchClientResolver) {

		GetMappingIndexRequestExecutor getMappingIndexRequestExecutor =
			new GetMappingIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			getMappingIndexRequestExecutor, "_elasticsearchClientResolver",
			elasticsearchClientResolver);

		return getMappingIndexRequestExecutor;
	}

	private IndicesExistsIndexRequestExecutor
		_createIndexExistsIndexRequestExecutor(
			ElasticsearchClientResolver elasticsearchClientResolver) {

		IndicesExistsIndexRequestExecutor indicesExistsIndexRequestExecutor =
			new IndicesExistsIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			indicesExistsIndexRequestExecutor, "_elasticsearchClientResolver",
			elasticsearchClientResolver);

		return indicesExistsIndexRequestExecutor;
	}

	private OpenIndexRequestExecutor _createOpenIndexRequestExecutor(
		IndicesOptionsTranslator indicesOptionsTranslator,
		ElasticsearchClientResolver elasticsearchClientResolver) {

		OpenIndexRequestExecutor openIndexRequestExecutor =
			new OpenIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			openIndexRequestExecutor, "_elasticsearchClientResolver",
			elasticsearchClientResolver);
		ReflectionTestUtil.setFieldValue(
			openIndexRequestExecutor, "_indicesOptionsTranslator",
			indicesOptionsTranslator);

		return openIndexRequestExecutor;
	}

	private PutMappingIndexRequestExecutor
		_createPutMappingIndexRequestExecutor(
			ElasticsearchClientResolver elasticsearchClientResolver) {

		PutMappingIndexRequestExecutor putMappingIndexRequestExecutor =
			new PutMappingIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			putMappingIndexRequestExecutor, "_elasticsearchClientResolver",
			elasticsearchClientResolver);

		return putMappingIndexRequestExecutor;
	}

	private RefreshIndexRequestExecutor _createRefreshIndexRequestExecutor(
		IndexRequestShardFailureTranslator indexRequestShardFailureTranslator,
		ElasticsearchClientResolver elasticsearchClientResolver) {

		RefreshIndexRequestExecutor refreshIndexRequestExecutor =
			new RefreshIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			refreshIndexRequestExecutor, "_elasticsearchClientResolver",
			elasticsearchClientResolver);
		ReflectionTestUtil.setFieldValue(
			refreshIndexRequestExecutor, "_indexRequestShardFailureTranslator",
			indexRequestShardFailureTranslator);

		return refreshIndexRequestExecutor;
	}

	private UpdateIndexSettingsIndexRequestExecutor
		_createUpdateIndexSettingsIndexRequestExecutor(
			IndicesOptionsTranslator indicesOptionsTranslator,
			ElasticsearchClientResolver elasticsearchClientResolver) {

		UpdateIndexSettingsIndexRequestExecutor
			updateIndexSettingsIndexRequestExecutor =
				new UpdateIndexSettingsIndexRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			updateIndexSettingsIndexRequestExecutor,
			"_elasticsearchClientResolver", elasticsearchClientResolver);
		ReflectionTestUtil.setFieldValue(
			updateIndexSettingsIndexRequestExecutor,
			"_indicesOptionsTranslator", indicesOptionsTranslator);

		return updateIndexSettingsIndexRequestExecutor;
	}

	private ElasticsearchClientResolver _elasticsearchClientResolver;
	private IndexRequestExecutor _indexRequestExecutor;

}
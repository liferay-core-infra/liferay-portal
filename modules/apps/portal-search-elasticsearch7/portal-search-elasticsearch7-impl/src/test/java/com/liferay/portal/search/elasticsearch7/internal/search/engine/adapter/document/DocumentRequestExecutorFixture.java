/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.document;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnectionManager;
import com.liferay.portal.search.elasticsearch7.internal.document.ElasticsearchDocumentFactory;
import com.liferay.portal.search.elasticsearch7.internal.query.ElasticsearchQueryTranslatorFixture;
import com.liferay.portal.search.engine.adapter.document.DocumentRequestExecutor;
import com.liferay.portal.search.internal.document.DocumentBuilderFactoryImpl;
import com.liferay.portal.search.internal.geolocation.GeoBuildersImpl;
import com.liferay.portal.search.internal.script.ScriptsImpl;
import com.liferay.portal.search.script.Scripts;

/**
 * @author Dylan Rebelak
 */
public class DocumentRequestExecutorFixture {

	public DocumentRequestExecutor getDocumentRequestExecutor() {
		return _documentRequestExecutor;
	}

	public void setUp() {
		_documentRequestExecutor = _createDocumentRequestExecutor(
			_elasticsearchConnectionManager, _elasticsearchDocumentFactory);
	}

	protected void setElasticsearchClientResolver(
		ElasticsearchConnectionManager elasticsearchConnectionManager) {

		_elasticsearchConnectionManager = elasticsearchConnectionManager;
	}

	protected void setElasticsearchDocumentFactory(
		ElasticsearchDocumentFactory elasticsearchDocumentFactory) {

		_elasticsearchDocumentFactory = elasticsearchDocumentFactory;
	}

	private ElasticsearchBulkableDocumentRequestTranslator
		_createBulkableDocumentRequestTranslator(
			ElasticsearchDocumentFactory elasticsearchDocumentFactory) {

		ElasticsearchBulkableDocumentRequestTranslator
			elasticsearchBulkableDocumentRequestTranslator =
				new ElasticsearchBulkableDocumentRequestTranslatorImpl();

		ReflectionTestUtil.setFieldValue(
			elasticsearchBulkableDocumentRequestTranslator,
			"_elasticsearchDocumentFactory", elasticsearchDocumentFactory);

		return elasticsearchBulkableDocumentRequestTranslator;
	}

	private BulkDocumentRequestExecutor _createBulkDocumentRequestExecutor(
		ElasticsearchConnectionManager elasticsearchConnectionManager,
		ElasticsearchBulkableDocumentRequestTranslator
			elasticsearchBulkableDocumentRequestTranslator) {

		BulkDocumentRequestExecutor bulkDocumentRequestExecutor =
			new BulkDocumentRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			bulkDocumentRequestExecutor,
			"_elasticsearchBulkableDocumentRequestTranslator",
			elasticsearchBulkableDocumentRequestTranslator);
		ReflectionTestUtil.setFieldValue(
			bulkDocumentRequestExecutor, "_elasticsearchConnectionManager",
			elasticsearchConnectionManager);

		return bulkDocumentRequestExecutor;
	}

	private DeleteByQueryDocumentRequestExecutor
		_createDeleteByQueryDocumentRequestExecutor(
			ElasticsearchConnectionManager elasticsearchConnectionManager) {

		DeleteByQueryDocumentRequestExecutor
			deleteByQueryDocumentRequestExecutor =
				new DeleteByQueryDocumentRequestExecutorImpl();

		com.liferay.portal.search.elasticsearch7.internal.legacy.query.
			ElasticsearchQueryTranslatorFixture
				legacyElasticsearchQueryTranslatorFixture =
					new com.liferay.portal.search.elasticsearch7.internal.
						legacy.query.ElasticsearchQueryTranslatorFixture();

		ElasticsearchQueryTranslatorFixture
			elasticsearchQueryTranslatorFixture =
				new ElasticsearchQueryTranslatorFixture();

		ReflectionTestUtil.setFieldValue(
			deleteByQueryDocumentRequestExecutor,
			"_elasticsearchConnectionManager", elasticsearchConnectionManager);
		ReflectionTestUtil.setFieldValue(
			deleteByQueryDocumentRequestExecutor, "_legacyQueryTranslator",
			legacyElasticsearchQueryTranslatorFixture.
				getElasticsearchQueryTranslator());
		ReflectionTestUtil.setFieldValue(
			deleteByQueryDocumentRequestExecutor, "_queryTranslator",
			elasticsearchQueryTranslatorFixture.
				getElasticsearchQueryTranslator());

		return deleteByQueryDocumentRequestExecutor;
	}

	private DeleteDocumentRequestExecutor _createDeleteDocumentRequestExecutor(
		ElasticsearchConnectionManager elasticsearchConnectionManager,
		ElasticsearchBulkableDocumentRequestTranslator
			elasticsearchBulkableDocumentRequestTranslator) {

		DeleteDocumentRequestExecutor deleteDocumentRequestExecutor =
			new DeleteDocumentRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			deleteDocumentRequestExecutor,
			"_elasticsearchBulkableDocumentRequestTranslator",
			elasticsearchBulkableDocumentRequestTranslator);
		ReflectionTestUtil.setFieldValue(
			deleteDocumentRequestExecutor, "_elasticsearchConnectionManager",
			elasticsearchConnectionManager);

		return deleteDocumentRequestExecutor;
	}

	private DocumentRequestExecutor _createDocumentRequestExecutor(
		ElasticsearchConnectionManager elasticsearchConnectionManager,
		ElasticsearchDocumentFactory elasticsearchDocumentFactory) {

		ElasticsearchBulkableDocumentRequestTranslator
			elasticsearchBulkableDocumentRequestTranslator =
				_createBulkableDocumentRequestTranslator(
					elasticsearchDocumentFactory);

		DocumentRequestExecutor documentRequestExecutor =
			new ElasticsearchDocumentRequestExecutor();

		ReflectionTestUtil.setFieldValue(
			documentRequestExecutor, "_bulkDocumentRequestExecutor",
			_createBulkDocumentRequestExecutor(
				elasticsearchConnectionManager,
				elasticsearchBulkableDocumentRequestTranslator));
		ReflectionTestUtil.setFieldValue(
			documentRequestExecutor, "_deleteByQueryDocumentRequestExecutor",
			_createDeleteByQueryDocumentRequestExecutor(
				elasticsearchConnectionManager));
		ReflectionTestUtil.setFieldValue(
			documentRequestExecutor, "_deleteDocumentRequestExecutor",
			_createDeleteDocumentRequestExecutor(
				elasticsearchConnectionManager,
				elasticsearchBulkableDocumentRequestTranslator));
		ReflectionTestUtil.setFieldValue(
			documentRequestExecutor, "_getDocumentRequestExecutor",
			_createGetDocumentRequestExecutor(
				elasticsearchConnectionManager,
				elasticsearchBulkableDocumentRequestTranslator));
		ReflectionTestUtil.setFieldValue(
			documentRequestExecutor, "_indexDocumentRequestExecutor",
			_createIndexDocumentRequestExecutor(
				elasticsearchConnectionManager,
				elasticsearchBulkableDocumentRequestTranslator));
		ReflectionTestUtil.setFieldValue(
			documentRequestExecutor, "_updateByQueryDocumentRequestExecutor",
			_createUpdateByQueryDocumentRequestExecutor(
				elasticsearchConnectionManager));
		ReflectionTestUtil.setFieldValue(
			documentRequestExecutor, "_updateDocumentRequestExecutor",
			_createUpdateDocumentRequestExecutor(
				elasticsearchConnectionManager,
				elasticsearchBulkableDocumentRequestTranslator));

		return documentRequestExecutor;
	}

	private GetDocumentRequestExecutor _createGetDocumentRequestExecutor(
		ElasticsearchConnectionManager elasticsearchConnectionManager,
		ElasticsearchBulkableDocumentRequestTranslator
			elasticsearchBulkableDocumentRequestTranslator) {

		GetDocumentRequestExecutor getDocumentRequestExecutor =
			new GetDocumentRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			getDocumentRequestExecutor,
			"_elasticsearchBulkableDocumentRequestTranslator",
			elasticsearchBulkableDocumentRequestTranslator);
		ReflectionTestUtil.setFieldValue(
			getDocumentRequestExecutor, "_elasticsearchConnectionManager",
			elasticsearchConnectionManager);
		ReflectionTestUtil.setFieldValue(
			getDocumentRequestExecutor, "_documentBuilderFactory",
			new DocumentBuilderFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			getDocumentRequestExecutor, "_geoBuilders", new GeoBuildersImpl());

		return getDocumentRequestExecutor;
	}

	private IndexDocumentRequestExecutor _createIndexDocumentRequestExecutor(
		ElasticsearchConnectionManager elasticsearchConnectionManager,
		ElasticsearchBulkableDocumentRequestTranslator
			elasticsearchBulkableDocumentRequestTranslator) {

		IndexDocumentRequestExecutor indexDocumentRequestExecutor =
			new IndexDocumentRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			indexDocumentRequestExecutor,
			"_elasticsearchBulkableDocumentRequestTranslator",
			elasticsearchBulkableDocumentRequestTranslator);
		ReflectionTestUtil.setFieldValue(
			indexDocumentRequestExecutor, "_elasticsearchConnectionManager",
			elasticsearchConnectionManager);

		return indexDocumentRequestExecutor;
	}

	private UpdateByQueryDocumentRequestExecutor
		_createUpdateByQueryDocumentRequestExecutor(
			ElasticsearchConnectionManager elasticsearchConnectionManager) {

		UpdateByQueryDocumentRequestExecutor
			updateByQueryDocumentRequestExecutor =
				new UpdateByQueryDocumentRequestExecutorImpl();

		com.liferay.portal.search.elasticsearch7.internal.legacy.query.
			ElasticsearchQueryTranslatorFixture
				lecacyElasticsearchQueryTranslatorFixture =
					new com.liferay.portal.search.elasticsearch7.internal.
						legacy.query.ElasticsearchQueryTranslatorFixture();

		ElasticsearchQueryTranslatorFixture
			elasticsearchQueryTranslatorFixture =
				new ElasticsearchQueryTranslatorFixture();

		ReflectionTestUtil.setFieldValue(
			updateByQueryDocumentRequestExecutor,
			"_elasticsearchConnectionManager", elasticsearchConnectionManager);
		ReflectionTestUtil.setFieldValue(
			updateByQueryDocumentRequestExecutor, "_legacyQueryTranslator",
			lecacyElasticsearchQueryTranslatorFixture.
				getElasticsearchQueryTranslator());
		ReflectionTestUtil.setFieldValue(
			updateByQueryDocumentRequestExecutor, "_queryTranslator",
			elasticsearchQueryTranslatorFixture.
				getElasticsearchQueryTranslator());
		ReflectionTestUtil.setFieldValue(
			updateByQueryDocumentRequestExecutor, "_scripts", _scripts);

		return updateByQueryDocumentRequestExecutor;
	}

	private UpdateDocumentRequestExecutor _createUpdateDocumentRequestExecutor(
		ElasticsearchConnectionManager elasticsearchConnectionManager,
		ElasticsearchBulkableDocumentRequestTranslator
			elasticsearchBulkableDocumentRequestTranslator) {

		UpdateDocumentRequestExecutor updateDocumentRequestExecutor =
			new UpdateDocumentRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			updateDocumentRequestExecutor,
			"_elasticsearchBulkableDocumentRequestTranslator",
			elasticsearchBulkableDocumentRequestTranslator);
		ReflectionTestUtil.setFieldValue(
			updateDocumentRequestExecutor, "_elasticsearchConnectionManager",
			elasticsearchConnectionManager);

		return updateDocumentRequestExecutor;
	}

	private static final Scripts _scripts = new ScriptsImpl();

	private DocumentRequestExecutor _documentRequestExecutor;
	private ElasticsearchConnectionManager _elasticsearchConnectionManager;
	private ElasticsearchDocumentFactory _elasticsearchDocumentFactory;

}
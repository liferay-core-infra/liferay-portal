/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.connection.helper;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnectionManager;
import com.liferay.portal.search.elasticsearch7.internal.index.LiferayDocumentTypeFactory;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;

/**
 * @author André de Oliveira
 */
public class LiferayIndexCreationHelper implements IndexCreationHelper {

	public LiferayIndexCreationHelper(
		ElasticsearchConnectionManager elasticsearchConnectionManager) {

		_elasticsearchConnectionManager = elasticsearchConnectionManager;
	}

	@Override
	public void contribute(CreateIndexRequest createIndexRequest) {
		LiferayDocumentTypeFactory liferayDocumentTypeFactory =
			_getLiferayDocumentTypeFactory();

		liferayDocumentTypeFactory.createRequiredDefaultTypeMappings(
			createIndexRequest);
	}

	@Override
	public void contributeIndexSettings(Settings.Builder builder) {
		LiferayDocumentTypeFactory liferayDocumentTypeFactory =
			_getLiferayDocumentTypeFactory();

		liferayDocumentTypeFactory.createRequiredDefaultAnalyzers(builder);
	}

	@Override
	public void whenIndexCreated(String indexName) {
		LiferayDocumentTypeFactory liferayDocumentTypeFactory =
			_getLiferayDocumentTypeFactory();

		liferayDocumentTypeFactory.createOptionalDefaultTypeMappings(indexName);
	}

	private LiferayDocumentTypeFactory _getLiferayDocumentTypeFactory() {
		RestHighLevelClient restHighLevelClient =
			_elasticsearchConnectionManager.getRestHighLevelClient();

		return new LiferayDocumentTypeFactory(
			restHighLevelClient.indices(), new JSONFactoryImpl());
	}

	private final ElasticsearchConnectionManager
		_elasticsearchConnectionManager;

}
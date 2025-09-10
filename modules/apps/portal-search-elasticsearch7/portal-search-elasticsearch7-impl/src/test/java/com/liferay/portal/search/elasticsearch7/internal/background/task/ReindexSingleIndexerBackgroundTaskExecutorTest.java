/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.background.task;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch7.internal.index.FieldMappingAssert;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.ElasticsearchSearchEngineFixture;
import com.liferay.portal.search.test.util.background.task.BaseReindexSingleIndexerBackgroundTaskExecutorTestCase;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.elasticsearch.client.RestHighLevelClient;

import org.junit.ClassRule;
import org.junit.Rule;

/**
 * @author Adam Brandizzi
 */
public class ReindexSingleIndexerBackgroundTaskExecutorTest
	extends BaseReindexSingleIndexerBackgroundTaskExecutorTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	public ReindexSingleIndexerBackgroundTaskExecutorTest() {
		_elasticsearchFixture = new ElasticsearchFixture(
			ReindexSingleIndexerBackgroundTaskExecutorTest.class.
				getSimpleName(),
			null);

		ElasticsearchSearchEngineFixture elasticsearchSearchEngineFixture =
			new ElasticsearchSearchEngineFixture(_elasticsearchFixture);

		_elasticsearchSearchEngineFixture = elasticsearchSearchEngineFixture;
	}

	@Override
	protected void assertFieldType(String fieldName, String fieldType)
		throws Exception {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchFixture.getRestHighLevelClient();

		FieldMappingAssert.assertType(
			fieldType, fieldName, getIndexName(),
			restHighLevelClient.indices());
	}

	@Override
	protected ElasticsearchSearchEngineFixture getSearchEngineFixture() {
		return _elasticsearchSearchEngineFixture;
	}

	private final ElasticsearchFixture _elasticsearchFixture;
	private final ElasticsearchSearchEngineFixture
		_elasticsearchSearchEngineFixture;

}
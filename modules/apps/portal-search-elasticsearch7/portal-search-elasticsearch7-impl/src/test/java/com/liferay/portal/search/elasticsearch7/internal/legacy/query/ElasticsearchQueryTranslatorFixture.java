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

package com.liferay.portal.search.elasticsearch7.internal.legacy.query;

import com.liferay.osgi.util.service.Snapshot;
import com.liferay.portal.kernel.search.filter.FilterTranslator;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.elasticsearch7.internal.filter.ElasticsearchFilterTranslatorFixture;

import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author Michael C. Han
 */
public class ElasticsearchQueryTranslatorFixture {

	public ElasticsearchQueryTranslatorFixture() {
		_elasticsearchQueryTranslator = new ElasticsearchQueryTranslator() {
			{
				ElasticsearchFilterTranslatorFixture
					elasticsearchFilterTranslatorFixture =
						new ElasticsearchFilterTranslatorFixture(this);

				booleanQueryTranslator = new BooleanQueryTranslatorImpl();

				ReflectionTestUtil.setFieldValue(
					booleanQueryTranslator, "_filterTranslatorSnapshot",
					new Snapshot<FilterTranslator<QueryBuilder>>(null, null) {

						public FilterTranslator<QueryBuilder> get() {
							return elasticsearchFilterTranslatorFixture.
								getElasticsearchFilterTranslator();
						}

					});

				disMaxQueryTranslator = new DisMaxQueryTranslatorImpl();
				fuzzyQueryTranslator = new FuzzyQueryTranslatorImpl();
				matchAllQueryTranslator = new MatchAllQueryTranslatorImpl();
				matchQueryTranslator = new MatchQueryTranslatorImpl();
				moreLikeThisQueryTranslator =
					new MoreLikeThisQueryTranslatorImpl();
				multiMatchQueryTranslator = new MultiMatchQueryTranslatorImpl();
				nestedQueryTranslator = new NestedQueryTranslatorImpl();
				stringQueryTranslator = new StringQueryTranslatorImpl();
				termQueryTranslator = new TermQueryTranslatorImpl();
				termRangeQueryTranslator = new TermRangeQueryTranslatorImpl();
				wildcardQueryTranslator = new WildcardQueryTranslatorImpl();
			}
		};
	}

	public ElasticsearchQueryTranslator getElasticsearchQueryTranslator() {
		return _elasticsearchQueryTranslator;
	}

	private final ElasticsearchQueryTranslator _elasticsearchQueryTranslator;

}
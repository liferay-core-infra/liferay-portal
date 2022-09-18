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

package com.liferay.portal.search.elasticsearch7.internal.query;

import com.liferay.portal.kernel.test.ReflectionTestUtil;

/**
 * @author Michael C. Han
 */
public class ElasticsearchQueryTranslatorFixture {

	public ElasticsearchQueryTranslatorFixture() {
		ElasticsearchQueryTranslator elasticsearchQueryTranslator =
			new ElasticsearchQueryTranslator();

		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_booleanQueryTranslator",
			new BooleanQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_boostingQueryTranslator",
			new BoostingQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_commonTermsQueryTranslator",
			new CommonTermsQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_constantScoreQueryTranslator",
			new ConstantScoreQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_dateRangeTermQueryTranslator",
			new DateRangeTermQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_disMaxQueryTranslator",
			new DisMaxQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_existsQueryTranslator",
			new ExistsQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_functionScoreQueryTranslator",
			new FunctionScoreQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_fuzzyQueryTranslator",
			new FuzzyQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_geoBoundingBoxQueryTranslator",
			new GeoBoundingBoxQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_geoDistanceQueryTranslator",
			new GeoDistanceQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_geoDistanceRangeQueryTranslator",
			new GeoDistanceRangeQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_geoPolygonQueryTranslator",
			new GeoPolygonQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_geoShapeQueryTranslator",
			new GeoShapeQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_idsQueryTranslator",
			new IdsQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_matchAllQueryTranslator",
			new MatchAllQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_matchPhrasePrefixQueryTranslator",
			new MatchPhrasePrefixQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_matchPhraseQueryTranslator",
			new MatchPhraseQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_matchQueryTranslator",
			new MatchQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_moreLikeThisQueryTranslator",
			new MoreLikeThisQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_multiMatchQueryTranslator",
			new MultiMatchQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_nestedQueryTranslator",
			new NestedQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_percolateQueryTranslator",
			new PercolateQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_prefixQueryTranslator",
			new PrefixQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_rangeTermQueryTranslator",
			new RangeTermQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_regexQueryTranslator",
			new RegexQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_scriptQueryTranslator",
			new ScriptQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_simpleQueryStringQueryTranslator",
			new SimpleStringQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_stringQueryTranslator",
			new StringQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_termQueryTranslator",
			new TermQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_termsQueryTranslator",
			new TermsQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_termsSetQueryTranslator",
			new TermsSetQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_wildcardQueryTranslator",
			new WildcardQueryTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchQueryTranslator, "_wrapperQueryTranslator",
			new WrapperQueryTranslatorImpl());

		_elasticsearchQueryTranslator = elasticsearchQueryTranslator;
	}

	public ElasticsearchQueryTranslator getElasticsearchQueryTranslator() {
		return _elasticsearchQueryTranslator;
	}

	private final ElasticsearchQueryTranslator _elasticsearchQueryTranslator;

}
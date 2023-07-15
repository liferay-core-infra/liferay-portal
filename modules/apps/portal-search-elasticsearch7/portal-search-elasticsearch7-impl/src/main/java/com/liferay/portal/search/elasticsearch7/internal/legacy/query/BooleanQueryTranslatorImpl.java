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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.FilterTranslator;
import com.liferay.portal.kernel.search.query.QueryVisitor;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import org.osgi.service.component.annotations.Component;

/**
 * @author André de Oliveira
 * @author Miguel Angelo Caldas Gallindo
 */
@Component(service = BooleanQueryTranslator.class)
public class BooleanQueryTranslatorImpl implements BooleanQueryTranslator {

	@Override
	public QueryBuilder translate(
		BooleanQuery booleanQuery, QueryVisitor<QueryBuilder> queryVisitor) {

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

		List<BooleanClause<Query>> clauses = booleanQuery.clauses();

		for (BooleanClause<Query> clause : clauses) {
			_addClause(clause, boolQueryBuilder, queryVisitor);
		}

		if (!booleanQuery.isDefaultBoost()) {
			boolQueryBuilder.boost(booleanQuery.getBoost());
		}

		BooleanFilter booleanFilter = booleanQuery.getPreBooleanFilter();

		if (booleanFilter == null) {
			return boolQueryBuilder;
		}

		// LPS-86537 The following conversion is present for backwards
		// compatibility with how Liferay's Indexer frameworks handles queries.
		// Ideally, we do not wrap the BooleanQuery with another BooleanQuery.

		BoolQueryBuilder wrapperBoolQueryBuilder = QueryBuilders.boolQuery();

		if (!clauses.isEmpty()) {
			wrapperBoolQueryBuilder.must(boolQueryBuilder);
		}

		FilterTranslator<QueryBuilder> filterTranslator =
			_filterTranslatorSnapshot.get();

		if (filterTranslator == null) {
			_log.error(
				"Unable to translate boolean filter " + booleanFilter +
					" as filter translator is null");

			return boolQueryBuilder;
		}

		QueryBuilder filterQueryBuilder = filterTranslator.translate(
			booleanFilter, null);

		wrapperBoolQueryBuilder.filter(filterQueryBuilder);

		return wrapperBoolQueryBuilder;
	}

	private void _addClause(
		BooleanClause<Query> clause, BoolQueryBuilder boolQuery,
		QueryVisitor<QueryBuilder> queryVisitor) {

		BooleanClauseOccur booleanClauseOccur = clause.getBooleanClauseOccur();

		Query query = clause.getClause();

		QueryBuilder queryBuilder = query.accept(queryVisitor);

		if (booleanClauseOccur.equals(BooleanClauseOccur.MUST)) {
			boolQuery.must(queryBuilder);

			return;
		}

		if (booleanClauseOccur.equals(BooleanClauseOccur.MUST_NOT)) {
			boolQuery.mustNot(queryBuilder);

			return;
		}

		if (booleanClauseOccur.equals(BooleanClauseOccur.SHOULD)) {
			boolQuery.should(queryBuilder);

			return;
		}

		throw new IllegalArgumentException();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BooleanQueryTranslatorImpl.class);

	private static final Snapshot<FilterTranslator<QueryBuilder>>
		_filterTranslatorSnapshot = new Snapshot<>(
			BooleanQueryTranslatorImpl.class,
			Snapshot.cast(FilterTranslator.class),
			"(search.engine.impl=Elasticsearch)", true);

}
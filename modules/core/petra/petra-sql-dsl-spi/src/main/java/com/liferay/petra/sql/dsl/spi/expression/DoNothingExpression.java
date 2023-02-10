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

package com.liferay.petra.sql.dsl.spi.expression;

import com.liferay.petra.sql.dsl.ast.ASTNodeListener;
import com.liferay.petra.sql.dsl.expression.Alias;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.sort.OrderByExpression;

import java.util.function.Consumer;

/**
 * @author Tai Pham
 */
public class DoNothingExpression<T> implements Expression<T> {

	@Override
	public Alias<T> as(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public OrderByExpression ascending() {
		throw new UnsupportedOperationException();
	}

	@Override
	public OrderByExpression descending() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate eq(Expression<T> expression) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate eq(T value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate gt(Expression<T> expression) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate gt(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate gte(Expression<T> expression) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate gte(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate in(DSLQuery dslQuery) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate in(Object[] values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate isNotNull() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate isNull() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate like(Expression<String> expression) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate like(String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate lt(Expression<T> expression) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate lt(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate lte(Expression<T> expression) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate lte(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate neq(Expression<T> expression) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate neq(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate notIn(DSLQuery dslQuery) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate notIn(Object[] values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate notLike(Expression<String> expression) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate notLike(String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void toSQL(
		Consumer<String> consumer, ASTNodeListener astNodeListener) {
	}

}
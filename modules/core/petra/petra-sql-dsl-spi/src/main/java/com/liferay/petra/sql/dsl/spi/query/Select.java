/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.sql.dsl.spi.query;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.ast.ASTNodeListener;
import com.liferay.petra.sql.dsl.expression.Alias;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.ScalarDSLQueryAlias;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.spi.ast.BaseASTNode;
import com.liferay.petra.string.StringPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Preston Crary
 */
public class Select extends BaseASTNode implements DefaultFromStep {

	public Select(
		boolean distinct, Collection<? extends Expression<?>> expressions) {

		this(distinct, Objects.requireNonNull(expressions), null);
	}

	public Collection<? extends Expression<?>> getExpressions() {
		return _expressions;
	}

	public String getHints() {
		return _hints;
	}

	/**
	 * Returns the label each selected expression's value carries in the result
	 * set, in selection order, with <code>null</code> for an unnamed
	 * expression. A column whose name a preceding expression already took is
	 * labeled distinctly, because Hibernate maps a native query's result
	 * columns by label and rejects a duplicated one. Callers that read the
	 * result set by label have to use these rather than the column names.
	 *
	 * @return the labels, in selection order
	 */
	public List<String> getLabels() {
		List<String> labels = new ArrayList<>(_expressions.size());

		Set<String> names = new HashSet<>();

		for (Expression<?> expression : _expressions) {
			String name = _getName(expression);

			if ((name == null) || names.add(name)) {
				labels.add(name);

				continue;
			}

			for (int i = 2;; i++) {
				String label = name + StringPool.UNDERLINE + i;

				if (names.add(label)) {
					labels.add(label);

					break;
				}
			}
		}

		return labels;
	}

	@Override
	public FromStep hints(String hints) {
		return new Select(_distinct, _expressions, hints);
	}

	public boolean isDistinct() {
		return _distinct;
	}

	@Override
	protected void doToSQL(
		Consumer<String> consumer, ASTNodeListener astNodeListener) {

		consumer.accept("select ");

		if (_hints != null) {
			consumer.accept("/*+ ");
			consumer.accept(_hints);
			consumer.accept(" */ ");
		}

		if (_distinct) {
			consumer.accept("distinct ");
		}

		if (_expressions.isEmpty()) {
			consumer.accept("*");
		}
		else {
			Iterator<String> labelIterator = getLabels().iterator();

			Iterator<? extends Expression<?>> iterator =
				_expressions.iterator();

			while (iterator.hasNext()) {
				Expression<?> expression = iterator.next();

				String label = labelIterator.next();

				if (expression instanceof Alias) {
					Alias<?> alias = (Alias<?>)expression;

					Expression<?> unwrappedExpression = alias.getExpression();

					unwrappedExpression.toSQL(consumer, astNodeListener);

					consumer.accept(" ");
				}

				expression.toSQL(consumer, astNodeListener);

				if (!(expression instanceof Alias) &&
					!Objects.equals(label, _getName(expression))) {

					consumer.accept(" ");
					consumer.accept(label);
				}

				if (iterator.hasNext()) {
					consumer.accept(", ");
				}
			}
		}
	}

	private Select(
		boolean distinct, Collection<? extends Expression<?>> expressions,
		String hints) {

		_distinct = distinct;
		_expressions = expressions;
		_hints = hints;
	}

	private String _getName(Expression<?> expression) {
		if (expression instanceof Alias) {
			Alias<?> alias = (Alias<?>)expression;

			return alias.getName();
		}

		if (expression instanceof Column) {
			Column<?, ?> column = (Column<?, ?>)expression;

			return column.getName();
		}

		if (expression instanceof ScalarDSLQueryAlias) {
			ScalarDSLQueryAlias<?> scalarDSLQueryAlias =
				(ScalarDSLQueryAlias<?>)expression;

			return scalarDSLQueryAlias.getName();
		}

		return null;
	}

	private final boolean _distinct;
	private final Collection<? extends Expression<?>> _expressions;
	private final String _hints;

}
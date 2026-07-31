/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.address.internal.odata.filter.expression;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.filter.expression.BinaryExpression;
import com.liferay.portal.odata.filter.expression.Expression;
import com.liferay.portal.odata.filter.expression.ExpressionVisitException;
import com.liferay.portal.odata.filter.expression.ExpressionVisitor;
import com.liferay.portal.odata.filter.expression.ListExpression;
import com.liferay.portal.odata.filter.expression.LiteralExpression;
import com.liferay.portal.odata.filter.expression.MemberExpression;
import com.liferay.portal.odata.filter.expression.MethodExpression;
import com.liferay.portal.odata.filter.expression.PrimitivePropertyExpression;

import java.time.OffsetDateTime;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Balazs Breier
 */
public class PredicateExpressionVisitorImpl
	implements ExpressionVisitor<Object> {

	public PredicateExpressionVisitorImpl(Map<String, Column<?, ?>> columns) {
		_columns = columns;
	}

	@Override
	public Object visitBinaryExpressionOperation(
			BinaryExpression.Operation operation, Object left, Object right)
		throws ExpressionVisitException {

		if ((operation == BinaryExpression.Operation.AND) ||
			(operation == BinaryExpression.Operation.OR)) {

			Predicate leftPredicate = (Predicate)left;
			Predicate rightPredicate = (Predicate)right;

			if (operation == BinaryExpression.Operation.AND) {
				return leftPredicate.and(rightPredicate);
			}

			return leftPredicate.or(rightPredicate);
		}

		return _getPredicate((String)left, operation, right);
	}

	@Override
	public Object visitListExpressionOperation(
			ListExpression.Operation operation, Object left, List<Object> right)
		throws ExpressionVisitException {

		if (operation == ListExpression.Operation.IN) {
			Column<?, Object> column = _getColumn((String)left);

			return column.in(right.toArray());
		}

		throw new UnsupportedOperationException(
			"Unsupported method visitListExpressionOperation with operation " +
				operation);
	}

	@Override
	public Object visitLiteralExpression(LiteralExpression literalExpression) {
		String text = literalExpression.getText();

		LiteralExpression.Type type = literalExpression.getType();

		if (Objects.equals(LiteralExpression.Type.BOOLEAN, type)) {
			return GetterUtil.getBoolean(text);
		}

		if (Objects.equals(LiteralExpression.Type.DATE, type) ||
			Objects.equals(LiteralExpression.Type.DATE_TIME, type)) {

			OffsetDateTime offsetDateTime = OffsetDateTime.parse(text);

			return Date.from(offsetDateTime.toInstant());
		}

		if (Objects.equals(LiteralExpression.Type.DOUBLE, type)) {
			return GetterUtil.getDouble(text);
		}

		if (Objects.equals(LiteralExpression.Type.INTEGER, type)) {
			return GetterUtil.getLong(text);
		}

		if (Objects.equals(LiteralExpression.Type.NULL, type)) {
			return null;
		}

		if (Objects.equals(LiteralExpression.Type.STRING, type)) {
			return StringUtil.replace(
				StringUtil.unquote(text), StringPool.DOUBLE_APOSTROPHE,
				StringPool.APOSTROPHE);
		}

		return text;
	}

	@Override
	public Object visitMemberExpression(MemberExpression memberExpression)
		throws ExpressionVisitException {

		Expression expression = memberExpression.getExpression();

		return expression.accept(this);
	}

	@Override
	public Object visitMethodExpression(
		List<Object> expressions, MethodExpression.Type type) {

		if (expressions.size() != 2) {
			throw new UnsupportedOperationException(
				"Unsupported method visitMethodExpression with method type " +
					type);
		}

		Column<?, Object> column = _getColumn((String)expressions.get(0));
		Object value = expressions.get(1);

		if (type == MethodExpression.Type.CONTAINS) {
			return DSLFunctionFactoryUtil.lower(
				DSLFunctionFactoryUtil.castText(column)
			).like(
				StringPool.PERCENT +
					StringUtil.toLowerCase(String.valueOf(value)) +
						StringPool.PERCENT
			);
		}

		if (type == MethodExpression.Type.STARTS_WITH) {
			return DSLFunctionFactoryUtil.castText(
				column
			).like(
				value + StringPool.PERCENT
			);
		}

		throw new UnsupportedOperationException(
			"Unsupported method visitMethodExpression with method type " +
				type);
	}

	@Override
	public Object visitPrimitivePropertyExpression(
		PrimitivePropertyExpression primitivePropertyExpression) {

		return primitivePropertyExpression.getName();
	}

	@SuppressWarnings("unchecked")
	private Column<?, Object> _getColumn(String name) {
		Column<?, Object> column = (Column<?, Object>)_columns.get(name);

		if (column == null) {
			throw new UnsupportedOperationException(
				"Unsupported filter field " + name);
		}

		return column;
	}

	private Predicate _getPredicate(
		String name, BinaryExpression.Operation operation, Object value) {

		Column<?, Object> column = _getColumn(name);

		if (operation == BinaryExpression.Operation.EQ) {
			if (value == null) {
				return column.isNull();
			}

			return column.eq(value);
		}
		else if (operation == BinaryExpression.Operation.GE) {
			return column.gte(value);
		}
		else if (operation == BinaryExpression.Operation.GT) {
			return column.gt(value);
		}
		else if (operation == BinaryExpression.Operation.LE) {
			return column.lte(value);
		}
		else if (operation == BinaryExpression.Operation.LT) {
			return column.lt(value);
		}
		else if (operation == BinaryExpression.Operation.NE) {
			if (value == null) {
				return column.isNotNull();
			}

			return column.neq(value);
		}

		throw new UnsupportedOperationException(
			"Unsupported binary operation " + operation);
	}

	private final Map<String, Column<?, ?>> _columns;

}
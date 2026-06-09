/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Order;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.AbstractQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.criteria.Subquery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.sqm.tree.SqmCopyContext;
import org.hibernate.query.sqm.tree.select.SqmSelectableNode;
import org.hibernate.type.BasicTypeReference;

/**
 * @author Tina Tian
 */
public class HibernateTypedQueryUtil {

	public static TypedQuery<?> buildTypedQuery(
		DynamicQueryImpl dynamicQueryImpl, Session session) {

		HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

		Class<?> resultType = dynamicQueryImpl.getClazz();

		if (dynamicQueryImpl.getProjection() != null) {
			resultType = Object.class;
		}

		CriteriaQuery<?> criteriaQuery = criteriaBuilder.createQuery(
			resultType);

		_apply(criteriaBuilder, criteriaQuery, dynamicQueryImpl);

		return session.createQuery(criteriaQuery);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static void _apply(
		HibernateCriteriaBuilder criteriaBuilder,
		AbstractQuery<?> abstractQuery, DynamicQueryImpl dynamicQueryImpl) {

		String alias = dynamicQueryImpl.getAlias();

		Root<?> root = abstractQuery.from(dynamicQueryImpl.getClazz());

		if (alias != null) {
			root.alias(alias);
		}

		List<Criterion> criterions = dynamicQueryImpl.getCriterions();

		if (!criterions.isEmpty()) {
			Predicate[] predicates = new Predicate[criterions.size()];

			for (int i = 0; i < criterions.size(); i++) {
				predicates[i] = _toPredicate(
					criteriaBuilder, abstractQuery, root, alias,
					criterions.get(i));
			}

			abstractQuery.where(predicates);
		}

		List<Expression<?>> groupByExpressions = new ArrayList<>();

		Selection<?> selection = _toSelection(
			criteriaBuilder, abstractQuery, root, alias,
			dynamicQueryImpl.getProjection(), groupByExpressions);

		if (abstractQuery instanceof CriteriaQuery) {
			CriteriaQuery criteriaQuery = (CriteriaQuery)abstractQuery;

			criteriaQuery.select(selection);
		}
		else {
			Subquery subquery = (Subquery)abstractQuery;

			subquery.select((Expression)selection);
		}

		if (!groupByExpressions.isEmpty()) {
			abstractQuery.groupBy(groupByExpressions);
		}

		List<Order> orders = dynamicQueryImpl.getOrders();

		if (!orders.isEmpty() && (abstractQuery instanceof CriteriaQuery)) {
			List<jakarta.persistence.criteria.Order> jpaOrders =
				new ArrayList<>(orders.size());

			for (Order order : orders) {
				OrderImpl orderImpl = (OrderImpl)order;

				Path<?> path = _getPath(
					orderImpl.getPropertyName(), abstractQuery, root, alias);

				if (orderImpl.isAscending()) {
					jpaOrders.add(criteriaBuilder.asc(path));
				}
				else {
					jpaOrders.add(criteriaBuilder.desc(path));
				}
			}

			CriteriaQuery<?> criteriaQuery = (CriteriaQuery<?>)abstractQuery;

			criteriaQuery.orderBy(jpaOrders);
		}
	}

	private static Selection<?> _copySelection(
		AbstractQuery<?> abstractQuery, Selection<?> selection) {

		SqmCopyContext sqmCopyContext = SqmCopyContext.simpleContext();

		AbstractQuery<?> currentQuery = abstractQuery;

		while (currentQuery != null) {
			for (Root<?> root : currentQuery.getRoots()) {
				sqmCopyContext.registerCopy(root, root);
			}

			if (currentQuery instanceof Subquery) {
				Subquery<?> subquery = (Subquery<?>)currentQuery;

				currentQuery = (AbstractQuery<?>)subquery.getParent();
			}
			else {
				currentQuery = null;
			}
		}

		SqmSelectableNode<?> sqmSelectableNode = (SqmSelectableNode<?>)selection;

		return sqmSelectableNode.copy(sqmCopyContext);
	}

	private static Path<?> _getPath(
		String name, AbstractQuery<?> abstractQuery, From<?, ?> from,
		String alias) {

		String[] parts = StringUtil.split(name, CharPool.PERIOD);

		if (parts.length == 1) {
			return from.get(name);
		}

		String parsedAlias = parts[0];
		String columnName = parts[1];

		if (Objects.equals(parsedAlias, alias)) {
			return from.get(columnName);
		}

		AbstractQuery<?> currentQuery = abstractQuery;

		while (currentQuery instanceof Subquery) {
			Subquery<?> subquery = (Subquery<?>)currentQuery;

			currentQuery = (AbstractQuery<?>)subquery.getParent();

			List<Root<?>> roots = ListUtil.fromCollection(
				currentQuery.getRoots());

			if (roots.size() != 1) {
				throw new IllegalStateException(
					"Unable to resolve alias " + parsedAlias);
			}

			Root<?> root = roots.get(0);

			if (Objects.equals(parsedAlias, root.getAlias())) {
				return root.get(columnName);
			}
		}

		throw new IllegalArgumentException(
			StringBundler.concat(
				"Unable to resolve alias ", parsedAlias, " in ", name));
	}

	private static List<String> _splitTopLevelCommas(String sql) {
		List<String> parts = new ArrayList<>();

		int depth = 0;
		int start = 0;

		for (int i = 0; i < sql.length(); i++) {
			char c = sql.charAt(i);

			if (c == CharPool.OPEN_PARENTHESIS) {
				depth++;
			}
			else if (c == CharPool.CLOSE_PARENTHESIS) {
				depth--;
			}
			else if ((c == CharPool.COMMA) && (depth == 0)) {
				parts.add(sql.substring(start, i));

				start = i + 1;
			}
		}

		parts.add(sql.substring(start));

		return parts;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static <T> Expression<T> _sql(
		HibernateCriteriaBuilder criteriaBuilder, String sql,
		Class<?> javaType) {

		return criteriaBuilder.sql(sql, (Class)javaType);
	}

	private static BasicTypeReference<?>[] _toBasicTypeReferences(
		Type[] types) {

		if (types == null) {
			return null;
		}

		BasicTypeReference<?>[] basicTypeReferences =
			new BasicTypeReference<?>[types.length];

		for (int i = 0; i < types.length; i++) {
			basicTypeReferences[i] = TypeTranslator.translate(types[i]);
		}

		return basicTypeReferences;
	}

	private static Predicate _toPredicate(
		HibernateCriteriaBuilder criteriaBuilder,
		AbstractQuery<?> abstractQuery, From<?, ?> from, String alias,
		Criterion criterion) {

		if (criterion instanceof ConjunctionImpl) {
			ConjunctionImpl conjunctionImpl = (ConjunctionImpl)criterion;

			List<Criterion> criterions = conjunctionImpl.getCriterions();

			Predicate[] predicates = new Predicate[criterions.size()];

			for (int i = 0; i < criterions.size(); i++) {
				predicates[i] = _toPredicate(
					criteriaBuilder, abstractQuery, from, alias,
					criterions.get(i));
			}

			return criteriaBuilder.and(predicates);
		}

		if (criterion instanceof DisjunctionImpl) {
			DisjunctionImpl disjunctionImpl = (DisjunctionImpl)criterion;

			List<Criterion> criterions = disjunctionImpl.getCriterions();

			Predicate[] predicates = new Predicate[criterions.size()];

			for (int i = 0; i < criterions.size(); i++) {
				predicates[i] = _toPredicate(
					criteriaBuilder, abstractQuery, from, alias,
					criterions.get(i));
			}

			return criteriaBuilder.or(predicates);
		}

		CriterionImpl criterionImpl = (CriterionImpl)criterion;

		List<Criterion> criterions = criterionImpl.getCriterions();

		if (criterions != null) {
			return _toPredicate(
				criteriaBuilder, abstractQuery, from, alias,
				criterionImpl.getCriterionType(), criterions);
		}

		DynamicQuery dynamicQuery = criterionImpl.getDynamicQuery();

		if (dynamicQuery != null) {
			return _toPredicate(
				criteriaBuilder, abstractQuery, from, alias,
				criterionImpl.getCriterionType(), dynamicQuery,
				criterionImpl.getPropertyName());
		}

		Integer size = criterionImpl.getSize();

		if (size != null) {
			return _toPredicate(
				criteriaBuilder, abstractQuery, from, alias,
				criterionImpl.getCriterionType(),
				criterionImpl.getPropertyName(), size.intValue());
		}

		String targetPropertyName = criterionImpl.getTargetPropertyName();

		if (targetPropertyName != null) {
			return _toPredicate(
				criteriaBuilder, abstractQuery, from, alias,
				criterionImpl.getCriterionType(),
				criterionImpl.getPropertyName(), targetPropertyName);
		}

		Object[] values = criterionImpl.getValues();

		if (values != null) {
			return _toPredicate(
				criteriaBuilder, abstractQuery, from, alias, criterionImpl,
				criterionImpl.getCriterionType(),
				criterionImpl.getPropertyName(), values);
		}

		return _toPredicate(
			criteriaBuilder, abstractQuery, from, alias, criterionImpl,
			criterionImpl.getCriterionType(), criterionImpl.getPropertyName());
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Predicate _toPredicate(
		HibernateCriteriaBuilder criteriaBuilder,
		AbstractQuery<?> abstractQuery, From<?, ?> from, String alias,
		CriterionImpl criterionImpl, CriterionType criterionType,
		String propertyName) {

		if (criterionType == CriterionType.ALL_EQ) {
			Map<String, Criterion> propertyNameValues =
				criterionImpl.getPropertyNameValues();

			if ((propertyNameValues == null) || propertyNameValues.isEmpty()) {
				return criteriaBuilder.conjunction();
			}

			Predicate[] predicates = new Predicate[propertyNameValues.size()];

			int i = 0;

			for (Map.Entry<String, Criterion> entry :
					propertyNameValues.entrySet()) {

				predicates[i++] = _toPredicate(
					criteriaBuilder, abstractQuery, from, alias,
					entry.getValue());
			}

			return criteriaBuilder.and(predicates);
		}
		else if (criterionType == CriterionType.IS_EMPTY) {
			return criteriaBuilder.isEmpty(
				(Expression)_getPath(propertyName, abstractQuery, from, alias));
		}
		else if (criterionType == CriterionType.IS_NOT_EMPTY) {
			return criteriaBuilder.isNotEmpty(
				(Expression)_getPath(propertyName, abstractQuery, from, alias));
		}
		else if (criterionType == CriterionType.IS_NOT_NULL) {
			return criteriaBuilder.isNotNull(
				_getPath(propertyName, abstractQuery, from, alias));
		}
		else if (criterionType == CriterionType.IS_NULL) {
			return criteriaBuilder.isNull(
				_getPath(propertyName, abstractQuery, from, alias));
		}
		else if (criterionType == CriterionType.SQL_RESTRICTION) {
			return criteriaBuilder.wrap(
				criteriaBuilder.sql(criterionImpl.getSQL(), Boolean.class));
		}

		throw new IllegalStateException(
			"Unexpected criterion type: " + criterionType);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Predicate _toPredicate(
		HibernateCriteriaBuilder criteriaBuilder,
		AbstractQuery<?> abstractQuery, From<?, ?> from, String alias,
		CriterionImpl criterionImpl, CriterionType criterionType,
		String propertyName, Object[] values) {

		if (criterionType == CriterionType.BETWEEN) {
			return criteriaBuilder.between(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Comparable)values[0], (Comparable)values[1]);
		}
		else if (criterionType == CriterionType.EQ) {
			return criteriaBuilder.equal(
				_getPath(propertyName, abstractQuery, from, alias), values[0]);
		}
		else if (criterionType == CriterionType.GE) {
			return criteriaBuilder.greaterThanOrEqualTo(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Comparable)values[0]);
		}
		else if (criterionType == CriterionType.GT) {
			return criteriaBuilder.greaterThan(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Comparable)values[0]);
		}
		else if (criterionType == CriterionType.ILIKE) {
			Expression<String> expression = _getPath(
				propertyName, abstractQuery, from, alias
			).as(
				String.class
			);

			return criteriaBuilder.like(
				criteriaBuilder.lower(expression),
				StringUtil.toLowerCase(String.valueOf(values[0])));
		}
		else if (criterionType == CriterionType.IN) {
			Path<?> path = _getPath(propertyName, abstractQuery, from, alias);

			return path.in(values);
		}
		else if (criterionType == CriterionType.LE) {
			return criteriaBuilder.lessThanOrEqualTo(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Comparable)values[0]);
		}
		else if (criterionType == CriterionType.LIKE) {
			Expression<String> expression = _getPath(
				propertyName, abstractQuery, from, alias
			).as(
				String.class
			);

			return criteriaBuilder.like(expression, String.valueOf(values[0]));
		}
		else if (criterionType == CriterionType.LT) {
			return criteriaBuilder.lessThan(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Comparable)values[0]);
		}
		else if (criterionType == CriterionType.NE) {
			return criteriaBuilder.notEqual(
				_getPath(propertyName, abstractQuery, from, alias), values[0]);
		}
		else if (criterionType == CriterionType.SQL_RESTRICTION) {
			Expression<?>[] arguments = new Expression<?>[values.length];

			for (int i = 0; i < values.length; i++) {
				arguments[i] = criteriaBuilder.literal(values[i]);
			}

			return criteriaBuilder.wrap(
				criteriaBuilder.sql(
					criterionImpl.getSQL(), Boolean.class, arguments));
		}

		throw new IllegalStateException(
			"Unexpected criterion type: " + criterionType);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Predicate _toPredicate(
		HibernateCriteriaBuilder criteriaBuilder,
		AbstractQuery<?> abstractQuery, From<?, ?> from, String alias,
		CriterionType criterionType, DynamicQuery dynamicQuery,
		String propertyName) {

		Path<?> path = _getPath(propertyName, abstractQuery, from, alias);

		Subquery<?> subquery = _toSubquery(
			criteriaBuilder, abstractQuery, (DynamicQueryImpl)dynamicQuery,
			path.getJavaType());

		if (criterionType == CriterionType.EQ) {
			return criteriaBuilder.equal(path, subquery);
		}
		else if (criterionType == CriterionType.EQ_ALL) {
			return criteriaBuilder.equal(path, criteriaBuilder.all(subquery));
		}
		else if (criterionType == CriterionType.GE) {
			return criteriaBuilder.greaterThanOrEqualTo(
				(Expression)path, (Expression)subquery);
		}
		else if (criterionType == CriterionType.GE_ALL) {
			return criteriaBuilder.greaterThanOrEqualTo(
				(Expression)path, (Expression)criteriaBuilder.all(subquery));
		}
		else if (criterionType == CriterionType.GE_SOME) {
			return criteriaBuilder.greaterThanOrEqualTo(
				(Expression)path, (Expression)criteriaBuilder.some(subquery));
		}
		else if (criterionType == CriterionType.GT) {
			return criteriaBuilder.greaterThan(
				(Expression)path, (Expression)subquery);
		}
		else if (criterionType == CriterionType.GT_ALL) {
			return criteriaBuilder.greaterThan(
				(Expression)path, (Expression)criteriaBuilder.all(subquery));
		}
		else if (criterionType == CriterionType.GT_SOME) {
			return criteriaBuilder.greaterThan(
				(Expression)path, (Expression)criteriaBuilder.some(subquery));
		}
		else if (criterionType == CriterionType.IN) {
			return path.in(subquery);
		}
		else if (criterionType == CriterionType.LE) {
			return criteriaBuilder.lessThanOrEqualTo(
				(Expression)path, (Expression)subquery);
		}
		else if (criterionType == CriterionType.LE_ALL) {
			return criteriaBuilder.lessThanOrEqualTo(
				(Expression)path, (Expression)criteriaBuilder.all(subquery));
		}
		else if (criterionType == CriterionType.LE_SOME) {
			return criteriaBuilder.lessThanOrEqualTo(
				(Expression)path, (Expression)criteriaBuilder.some(subquery));
		}
		else if (criterionType == CriterionType.LT) {
			return criteriaBuilder.lessThan(
				(Expression)path, (Expression)subquery);
		}
		else if (criterionType == CriterionType.LT_ALL) {
			return criteriaBuilder.lessThan(
				(Expression)path, (Expression)criteriaBuilder.all(subquery));
		}
		else if (criterionType == CriterionType.LT_SOME) {
			return criteriaBuilder.lessThan(
				(Expression)path, (Expression)criteriaBuilder.some(subquery));
		}
		else if (criterionType == CriterionType.NE) {
			return criteriaBuilder.notEqual(path, subquery);
		}
		else if (criterionType == CriterionType.NOT_IN) {
			Predicate predicate = path.in(subquery);

			return predicate.not();
		}

		throw new IllegalStateException(
			"Unexpected criterion type: " + criterionType);
	}

	private static Predicate _toPredicate(
		HibernateCriteriaBuilder criteriaBuilder,
		AbstractQuery<?> abstractQuery, From<?, ?> from, String alias,
		CriterionType criterionType, List<Criterion> criterions) {

		if (criterions.size() == 1) {
			return criteriaBuilder.not(
				_toPredicate(
					criteriaBuilder, abstractQuery, from, alias,
					criterions.get(0)));
		}

		if (criterions.size() == 2) {
			if (criterionType == CriterionType.OR) {
				return criteriaBuilder.or(
					_toPredicate(
						criteriaBuilder, abstractQuery, from, alias,
						criterions.get(0)),
					_toPredicate(
						criteriaBuilder, abstractQuery, from, alias,
						criterions.get(1)));
			}

			if (criterionType == CriterionType.AND) {
				return criteriaBuilder.and(
					_toPredicate(
						criteriaBuilder, abstractQuery, from, alias,
						criterions.get(0)),
					_toPredicate(
						criteriaBuilder, abstractQuery, from, alias,
						criterions.get(1)));
			}
		}

		throw new IllegalStateException(
			"Unexpected criterion type: " + criterionType);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Predicate _toPredicate(
		HibernateCriteriaBuilder criteriaBuilder,
		AbstractQuery<?> abstractQuery, From<?, ?> from, String alias,
		CriterionType criterionType, String propertyName, int size) {

		Expression<Integer> sizeExpression = criteriaBuilder.size(
			(Expression)_getPath(propertyName, abstractQuery, from, alias));

		if (criterionType == CriterionType.EQ) {
			return criteriaBuilder.equal(sizeExpression, size);
		}
		else if (criterionType == CriterionType.GE) {
			return criteriaBuilder.greaterThanOrEqualTo(sizeExpression, size);
		}
		else if (criterionType == CriterionType.GT) {
			return criteriaBuilder.greaterThan(sizeExpression, size);
		}
		else if (criterionType == CriterionType.LE) {
			return criteriaBuilder.lessThanOrEqualTo(sizeExpression, size);
		}
		else if (criterionType == CriterionType.LT) {
			return criteriaBuilder.lessThan(sizeExpression, size);
		}
		else if (criterionType == CriterionType.NE) {
			return criteriaBuilder.notEqual(sizeExpression, size);
		}

		throw new IllegalStateException(
			"Unexpected criterion type: " + criterionType);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Predicate _toPredicate(
		HibernateCriteriaBuilder criteriaBuilder,
		AbstractQuery<?> abstractQuery, From<?, ?> from, String alias,
		CriterionType criterionType, String propertyName,
		String targetPropertyName) {

		Path<?> path = _getPath(propertyName, abstractQuery, from, alias);
		Path<?> targetPath = _getPath(
			targetPropertyName, abstractQuery, from, alias);

		if (criterionType == CriterionType.EQ) {
			return criteriaBuilder.equal(path, targetPath);
		}
		else if (criterionType == CriterionType.GE) {
			return criteriaBuilder.greaterThanOrEqualTo(
				(Expression)path, (Expression)targetPath);
		}
		else if (criterionType == CriterionType.GT) {
			return criteriaBuilder.greaterThan(
				(Expression)path, (Expression)targetPath);
		}
		else if (criterionType == CriterionType.LE) {
			return criteriaBuilder.lessThanOrEqualTo(
				(Expression)path, (Expression)targetPath);
		}
		else if (criterionType == CriterionType.LT) {
			return criteriaBuilder.lessThan(
				(Expression)path, (Expression)targetPath);
		}
		else if (criterionType == CriterionType.NE) {
			return criteriaBuilder.notEqual(path, targetPath);
		}

		throw new IllegalStateException(
			"Unexpected criterion type: " + criterionType);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Selection<?> _toSelection(
		HibernateCriteriaBuilder criteriaBuilder,
		AbstractQuery<?> abstractQuery, From<?, ?> from, String alias,
		Projection projection, List<Expression<?>> groupByExpressions) {

		if (projection == null) {
			return from;
		}

		if (projection instanceof ProjectionListImpl) {
			ProjectionListImpl projectionListImpl =
				(ProjectionListImpl)projection;

			List<Projection> projections = projectionListImpl.getProjections();

			List<Selection<?>> selections = new ArrayList<>(projections.size());

			for (Projection curProjection : projections) {
				selections.add(
					_toSelection(
						criteriaBuilder, abstractQuery, from, alias,
						curProjection, groupByExpressions));
			}

			if (selections.size() == 1) {
				return selections.get(0);
			}

			return criteriaBuilder.array(selections);
		}

		if (projection instanceof PropertyImpl) {
			PropertyImpl propertyImpl = (PropertyImpl)projection;

			Path<?> path = _getPath(
				propertyImpl.getPropertyName(), abstractQuery, from, alias);

			if (propertyImpl.isGroup()) {
				groupByExpressions.add(path);
			}

			return path;
		}

		ProjectionImpl projectionImpl = (ProjectionImpl)projection;

		if (projectionImpl.getAlias() != null) {
			Selection<?> selection = _toSelection(
				criteriaBuilder, abstractQuery, from, alias,
				projectionImpl.getProjection(), groupByExpressions);

			if (selection.getAlias() != null) {
				selection = _copySelection(abstractQuery, selection);
			}

			selection.alias(projectionImpl.getAlias());

			return selection;
		}

		if (projectionImpl.getProjection() != null) {
			abstractQuery.distinct(true);

			return _toSelection(
				criteriaBuilder, abstractQuery, from, alias,
				projectionImpl.getProjection(), groupByExpressions);
		}

		if (projectionImpl.getSQL() != null) {
			return _toSqlSelection(
				criteriaBuilder, projectionImpl, groupByExpressions);
		}

		return _toSelection(
			criteriaBuilder, abstractQuery, from, alias,
			projectionImpl.getProjectionType(),
			projectionImpl.getPropertyName());
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Selection<?> _toSelection(
		HibernateCriteriaBuilder criteriaBuilder,
		AbstractQuery<?> abstractQuery, From<?, ?> from, String alias,
		ProjectionType projectionType, String propertyName) {

		if (projectionType == ProjectionType.AVG) {
			return criteriaBuilder.avg(
				(Expression)_getPath(propertyName, abstractQuery, from, alias));
		}
		else if (projectionType == ProjectionType.COUNT) {
			return criteriaBuilder.count(
				_getPath(propertyName, abstractQuery, from, alias));
		}
		else if (projectionType == ProjectionType.COUNT_DISTINCT) {
			return criteriaBuilder.countDistinct(
				_getPath(propertyName, abstractQuery, from, alias));
		}
		else if (projectionType == ProjectionType.MAX) {
			return criteriaBuilder.max(
				(Expression)_getPath(propertyName, abstractQuery, from, alias));
		}
		else if (projectionType == ProjectionType.MIN) {
			return criteriaBuilder.min(
				(Expression)_getPath(propertyName, abstractQuery, from, alias));
		}
		else if (projectionType == ProjectionType.ROW_COUNT) {
			return criteriaBuilder.count(from);
		}
		else if (projectionType == ProjectionType.SUM) {
			return criteriaBuilder.sum(
				(Expression)_getPath(propertyName, abstractQuery, from, alias));
		}

		throw new IllegalStateException(
			"Unexpected projection type: " + projectionType);
	}

	private static Selection<?> _toSqlSelection(
		HibernateCriteriaBuilder criteriaBuilder, ProjectionImpl projectionImpl,
		List<Expression<?>> groupByExpressions) {

		BasicTypeReference<?>[] basicTypeReferences = _toBasicTypeReferences(
			projectionImpl.getTypes());

		String[] columnAliases = projectionImpl.getColumnAliases();

		List<String> columnSqls = _splitTopLevelCommas(projectionImpl.getSQL());

		Selection<?>[] selections = new Selection<?>[columnSqls.size()];

		for (int i = 0; i < columnSqls.size(); i++) {
			String columnSql = StringUtil.trim(columnSqls.get(i));

			Class<?> javaType = Object.class;

			if ((basicTypeReferences != null) &&
				(i < basicTypeReferences.length) &&
				(basicTypeReferences[i] != null)) {

				javaType = basicTypeReferences[i].getJavaType();
			}

			Selection<?> selection = _sql(criteriaBuilder, columnSql, javaType);

			if ((columnAliases != null) && (i < columnAliases.length) &&
				(columnAliases[i] != null)) {

				selection.alias(columnAliases[i]);
			}

			selections[i] = selection;
		}

		String groupBy = projectionImpl.getGroupBy();

		if ((groupBy != null) && !groupBy.isEmpty()) {
			for (String groupByPart : _splitTopLevelCommas(groupBy)) {
				groupByExpressions.add(
					_sql(
						criteriaBuilder, StringUtil.trim(groupByPart),
						Object.class));
			}
		}

		if (selections.length == 1) {
			return selections[0];
		}

		return criteriaBuilder.array(selections);
	}

	private static Subquery<?> _toSubquery(
		HibernateCriteriaBuilder criteriaBuilder,
		AbstractQuery<?> abstractQuery, DynamicQueryImpl dynamicQueryImpl,
		Class<?> expectedResultType) {

		Class<?> resultType = dynamicQueryImpl.getClazz();

		if (dynamicQueryImpl.getProjection() != null) {
			if (expectedResultType != null) {
				resultType = expectedResultType;
			}
			else {
				resultType = Object.class;
			}
		}

		Subquery<?> subquery = abstractQuery.subquery(resultType);

		_apply(criteriaBuilder, subquery, dynamicQueryImpl);

		return subquery;
	}

}
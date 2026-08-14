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
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.Type.PersistenceType;

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

		Class<?> resultType = dynamicQueryImpl.getClazz();

		if (dynamicQueryImpl.getProjection() != null) {
			resultType = Object.class;
		}

		HibernateCriteriaBuilder hibernateCriteriaBuilder =
			session.getCriteriaBuilder();

		CriteriaQuery<?> criteriaQuery = hibernateCriteriaBuilder.createQuery(
			resultType);

		String alias = dynamicQueryImpl.getAlias();

		Root<?> root = criteriaQuery.from(dynamicQueryImpl.getClazz());

		if (alias != null) {
			root.alias(alias);
		}

		_applyCriterions(
			criteriaQuery, dynamicQueryImpl.getCriterions(),
			hibernateCriteriaBuilder, root);

		_applyProjection(
			criteriaQuery, hibernateCriteriaBuilder,
			dynamicQueryImpl.getProjection(), root);

		_applyOrders(
			criteriaQuery, hibernateCriteriaBuilder,
			dynamicQueryImpl.getOrders(), root);

		return session.createQuery(criteriaQuery);
	}

	private static void _applyCriterions(
		AbstractQuery<?> abstractQuery, List<Criterion> criterions,
		HibernateCriteriaBuilder hibernateCriteriaBuilder, Root<?> root) {

		if (criterions.isEmpty()) {
			return;
		}

		Predicate[] predicates = new Predicate[criterions.size()];

		for (int i = 0; i < criterions.size(); i++) {
			predicates[i] = _toPredicate(
				abstractQuery, criterions.get(i), root,
				hibernateCriteriaBuilder);
		}

		abstractQuery.where(predicates);
	}

	private static void _applyOrders(
		CriteriaQuery<?> criteriaQuery,
		HibernateCriteriaBuilder hibernateCriteriaBuilder, List<Order> orders,
		Root<?> root) {

		if (orders.isEmpty()) {
			return;
		}

		List<jakarta.persistence.criteria.Order> jpaOrders = new ArrayList<>(
			orders.size());

		for (Order order : orders) {
			OrderImpl orderImpl = (OrderImpl)order;

			String propertyName = orderImpl.getPropertyName();

			Selection<?> selection = criteriaQuery.getSelection();

			Selection<?> aliasedSelection = null;

			if (Objects.equals(selection.getAlias(), propertyName)) {
				aliasedSelection = selection;
			}
			else if (selection.isCompoundSelection()) {
				for (Selection<?> curSelection :
						selection.getCompoundSelectionItems()) {

					if (Objects.equals(curSelection.getAlias(), propertyName)) {
						aliasedSelection = curSelection;

						break;
					}
				}
			}

			Expression<?> orderExpression = null;

			if (aliasedSelection instanceof Expression) {
				orderExpression = (Expression<?>)aliasedSelection;
			}
			else {
				orderExpression = _getPath(criteriaQuery, root, propertyName);
			}

			if (orderImpl.isAscending()) {
				jpaOrders.add(hibernateCriteriaBuilder.asc(orderExpression));
			}
			else {
				jpaOrders.add(hibernateCriteriaBuilder.desc(orderExpression));
			}
		}

		criteriaQuery.orderBy(jpaOrders);
	}

	private static void _applyProjection(
		AbstractQuery<?> abstractQuery,
		HibernateCriteriaBuilder hibernateCriteriaBuilder,
		Projection projection, Root<?> root) {

		if (projection == null) {
			_select(abstractQuery, root);

			return;
		}

		List<Expression<?>> groupByExpressions = new ArrayList<>();

		Selection<?> selection = _toSelection(
			abstractQuery, root, groupByExpressions, hibernateCriteriaBuilder,
			projection);

		if (!groupByExpressions.isEmpty()) {
			abstractQuery.groupBy(groupByExpressions);
		}

		_select(abstractQuery, selection);
	}

	private static List<String> _getColumnSqls(String sql) {
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

	private static Path<?> _getCompoundPrimaryKeyPath(
		From<?, ?> from, String[] parts) {

		if (!(from instanceof Root)) {
			return null;
		}

		Root<?> root = (Root<?>)from;

		EntityType<?> entityType = root.getModel();

		if (!entityType.hasSingleIdAttribute()) {
			return null;
		}

		if (entityType.getIdType(
			).getPersistenceType() != PersistenceType.EMBEDDABLE) {

			return null;
		}

		SingularAttribute<?, ?> singularAttribute = entityType.getId(
			entityType.getIdType(
			).getJavaType());

		String name = singularAttribute.getName();

		if (!parts[0].equals(name) && !parts[0].equals("id")) {
			return null;
		}

		Path<?> path = root.get(name);

		for (int i = 1; i < parts.length; i++) {
			path = path.get(parts[i]);
		}

		return path;
	}

	private static Path<?> _getPath(
		AbstractQuery<?> abstractQuery, From<?, ?> from, String name) {

		String[] parts = StringUtil.split(name, CharPool.PERIOD);

		if (parts.length == 1) {
			return from.get(name);
		}

		String parsedAlias = parts[0];
		String columnName = parts[1];

		String alias = from.getAlias();

		if (Objects.equals(parsedAlias, alias) ||
			((alias == null) && parsedAlias.equals("this"))) {

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

			String rootAlias = root.getAlias();

			if (Objects.equals(parsedAlias, rootAlias) ||
				((rootAlias == null) && parsedAlias.equals("this"))) {

				return root.get(columnName);
			}
		}

		Path<?> path = _getCompoundPrimaryKeyPath(from, parts);

		if (path != null) {
			return path;
		}

		throw new IllegalArgumentException(
			StringBundler.concat(
				"Unable to resolve alias ", parsedAlias, " in ", name));
	}

	private static String _resolveSQL(
		List<Expression<?>> arguments, From<?, ?> from,
		HibernateCriteriaBuilder hibernateCriteriaBuilder, String sql,
		Object[] values) {

		if ((sql.indexOf(CharPool.QUESTION) == -1) &&
			!sql.contains(_ROOT_ALIAS_PREFIX)) {

			return sql;
		}

		StringBundler sb = new StringBundler();

		int valueIndex = 0;

		int i = 0;

		while (i < sql.length()) {
			char c = sql.charAt(i);

			if ((c == CharPool.APOSTROPHE) || (c == CharPool.QUOTE)) {
				int end = sql.indexOf(c, i + 1);

				if (end == -1) {
					end = sql.length() - 1;
				}

				sb.append(sql.substring(i, end + 1));

				i = end + 1;
			}
			else if (c == CharPool.QUESTION) {
				arguments.add(
					hibernateCriteriaBuilder.literal(values[valueIndex++]));

				sb.append(CharPool.QUESTION);

				i++;
			}
			else if (sql.startsWith(_ROOT_ALIAS_PREFIX, i)) {
				int start = i + _ROOT_ALIAS_PREFIX.length();

				int end = start;

				while (end < sql.length()) {
					char columnNameChar = sql.charAt(end);

					if (!Character.isLetterOrDigit(columnNameChar) &&
						(columnNameChar != CharPool.UNDERLINE)) {

						break;
					}

					end++;
				}

				arguments.add(from.get(sql.substring(start, end)));

				sb.append(CharPool.QUESTION);

				i = end;
			}
			else {
				sb.append(c);

				i++;
			}
		}

		return sb.toString();
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static void _select(
		AbstractQuery<?> abstractQuery, Selection<?> selection) {

		if (abstractQuery instanceof Subquery) {
			Subquery subquery = (Subquery)abstractQuery;

			subquery.select((Expression)selection);
		}
		else {
			CriteriaQuery criteriaQuery = (CriteriaQuery)abstractQuery;

			criteriaQuery.select(selection);
		}
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
		AbstractQuery<?> abstractQuery, Criterion criterion, From<?, ?> from,
		HibernateCriteriaBuilder hibernateCriteriaBuilder) {

		if (criterion instanceof ConjunctionImpl) {
			ConjunctionImpl conjunctionImpl = (ConjunctionImpl)criterion;

			List<Criterion> criterions = conjunctionImpl.getCriterions();

			Predicate[] predicates = new Predicate[criterions.size()];

			for (int i = 0; i < criterions.size(); i++) {
				predicates[i] = _toPredicate(
					abstractQuery, criterions.get(i), from,
					hibernateCriteriaBuilder);
			}

			return hibernateCriteriaBuilder.and(predicates);
		}

		if (criterion instanceof DisjunctionImpl) {
			DisjunctionImpl disjunctionImpl = (DisjunctionImpl)criterion;

			List<Criterion> criterions = disjunctionImpl.getCriterions();

			Predicate[] predicates = new Predicate[criterions.size()];

			for (int i = 0; i < criterions.size(); i++) {
				predicates[i] = _toPredicate(
					abstractQuery, criterions.get(i), from,
					hibernateCriteriaBuilder);
			}

			return hibernateCriteriaBuilder.or(predicates);
		}

		CriterionImpl criterionImpl = (CriterionImpl)criterion;

		List<Criterion> criterions = criterionImpl.getCriterions();

		if (criterions != null) {
			return _toPredicate(
				abstractQuery, criterions, criterionImpl.getCriterionType(),
				from, hibernateCriteriaBuilder);
		}

		DynamicQuery dynamicQuery = criterionImpl.getDynamicQuery();

		if (dynamicQuery != null) {
			return _toPredicate(
				abstractQuery, criterionImpl.getCriterionType(), dynamicQuery,
				from, hibernateCriteriaBuilder,
				criterionImpl.getPropertyName());
		}

		Integer size = criterionImpl.getSize();

		if (size != null) {
			return _toPredicate(
				abstractQuery, criterionImpl.getCriterionType(), from,
				hibernateCriteriaBuilder, criterionImpl.getPropertyName(),
				size.intValue());
		}

		String targetPropertyName = criterionImpl.getTargetPropertyName();

		if (targetPropertyName != null) {
			return _toPredicate(
				abstractQuery, criterionImpl.getCriterionType(), from,
				hibernateCriteriaBuilder, criterionImpl.getPropertyName(),
				targetPropertyName);
		}

		Object[] values = criterionImpl.getValues();

		if (values != null) {
			return _toPredicate(
				abstractQuery, criterionImpl, criterionImpl.getCriterionType(),
				from, hibernateCriteriaBuilder, criterionImpl.getPropertyName(),
				values);
		}

		return _toPredicate(
			abstractQuery, criterionImpl, criterionImpl.getCriterionType(),
			from, hibernateCriteriaBuilder, criterionImpl.getPropertyName());
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Predicate _toPredicate(
		AbstractQuery<?> abstractQuery, CriterionImpl criterionImpl,
		CriterionType criterionType, From<?, ?> from,
		HibernateCriteriaBuilder hibernateCriteriaBuilder,
		String propertyName) {

		if (criterionType == CriterionType.ALL_EQ) {
			Map<String, Criterion> propertyNameValues =
				criterionImpl.getPropertyNameValues();

			if ((propertyNameValues == null) || propertyNameValues.isEmpty()) {
				return hibernateCriteriaBuilder.conjunction();
			}

			Predicate[] predicates = new Predicate[propertyNameValues.size()];

			int i = 0;

			for (Map.Entry<String, Criterion> entry :
					propertyNameValues.entrySet()) {

				predicates[i++] = _toPredicate(
					abstractQuery, entry.getValue(), from,
					hibernateCriteriaBuilder);
			}

			return hibernateCriteriaBuilder.and(predicates);
		}

		if (criterionType == CriterionType.SQL_RESTRICTION) {
			List<Expression<?>> arguments = new ArrayList<>();

			return hibernateCriteriaBuilder.wrap(
				hibernateCriteriaBuilder.sql(
					_resolveSQL(
						arguments, from, hibernateCriteriaBuilder,
						criterionImpl.getSQL(), new Object[0]),
					Boolean.class, arguments.toArray(new Expression<?>[0])));
		}

		Path<?> path = _getPath(abstractQuery, from, propertyName);

		if (criterionType == CriterionType.IS_EMPTY) {
			return hibernateCriteriaBuilder.isEmpty((Expression)path);
		}
		else if (criterionType == CriterionType.IS_NOT_EMPTY) {
			return hibernateCriteriaBuilder.isNotEmpty((Expression)path);
		}
		else if (criterionType == CriterionType.IS_NOT_NULL) {
			return hibernateCriteriaBuilder.isNotNull(path);
		}
		else if (criterionType == CriterionType.IS_NULL) {
			return hibernateCriteriaBuilder.isNull(path);
		}

		throw new IllegalStateException(
			"Unexpected criterion type: " + criterionType);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Predicate _toPredicate(
		AbstractQuery<?> abstractQuery, CriterionImpl criterionImpl,
		CriterionType criterionType, From<?, ?> from,
		HibernateCriteriaBuilder hibernateCriteriaBuilder, String propertyName,
		Object[] values) {

		if (criterionType == CriterionType.SQL_RESTRICTION) {
			List<Expression<?>> arguments = new ArrayList<>();

			return hibernateCriteriaBuilder.wrap(
				hibernateCriteriaBuilder.sql(
					_resolveSQL(
						arguments, from, hibernateCriteriaBuilder,
						criterionImpl.getSQL(), values),
					Boolean.class, arguments.toArray(new Expression<?>[0])));
		}

		Path<?> path = _getPath(abstractQuery, from, propertyName);

		if (criterionType == CriterionType.BETWEEN) {
			return hibernateCriteriaBuilder.between(
				(Expression)path, (Comparable)values[0], (Comparable)values[1]);
		}
		else if (criterionType == CriterionType.EQ) {
			return hibernateCriteriaBuilder.equal(path, values[0]);
		}
		else if (criterionType == CriterionType.GE) {
			return hibernateCriteriaBuilder.greaterThanOrEqualTo(
				(Expression)path, (Comparable)values[0]);
		}
		else if (criterionType == CriterionType.GT) {
			return hibernateCriteriaBuilder.greaterThan(
				(Expression)path, (Comparable)values[0]);
		}
		else if (criterionType == CriterionType.ILIKE) {
			Expression<String> expression = path.as(String.class);

			return hibernateCriteriaBuilder.like(
				hibernateCriteriaBuilder.lower(expression),
				StringUtil.toLowerCase(String.valueOf(values[0])));
		}
		else if (criterionType == CriterionType.IN) {
			return path.in(values);
		}
		else if (criterionType == CriterionType.LE) {
			return hibernateCriteriaBuilder.lessThanOrEqualTo(
				(Expression)path, (Comparable)values[0]);
		}
		else if (criterionType == CriterionType.LIKE) {
			Expression<String> expression = path.as(String.class);

			return hibernateCriteriaBuilder.like(
				expression, String.valueOf(values[0]));
		}
		else if (criterionType == CriterionType.LT) {
			return hibernateCriteriaBuilder.lessThan(
				(Expression)path, (Comparable)values[0]);
		}
		else if (criterionType == CriterionType.NE) {
			return hibernateCriteriaBuilder.notEqual(path, values[0]);
		}

		throw new IllegalStateException(
			"Unexpected criterion type: " + criterionType);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Predicate _toPredicate(
		AbstractQuery<?> abstractQuery, CriterionType criterionType,
		DynamicQuery dynamicQuery, From<?, ?> from,
		HibernateCriteriaBuilder hibernateCriteriaBuilder,
		String propertyName) {

		Path<?> path = _getPath(abstractQuery, from, propertyName);

		Subquery<?> subquery = _toSubquery(
			abstractQuery, (DynamicQueryImpl)dynamicQuery, path.getJavaType(),
			hibernateCriteriaBuilder);

		if (criterionType == CriterionType.EQ) {
			return hibernateCriteriaBuilder.equal(path, subquery);
		}
		else if (criterionType == CriterionType.EQ_ALL) {
			return hibernateCriteriaBuilder.equal(
				path, hibernateCriteriaBuilder.all(subquery));
		}
		else if (criterionType == CriterionType.GE) {
			return hibernateCriteriaBuilder.greaterThanOrEqualTo(
				(Expression)path, (Expression)subquery);
		}
		else if (criterionType == CriterionType.GE_ALL) {
			return hibernateCriteriaBuilder.greaterThanOrEqualTo(
				(Expression)path,
				(Expression)hibernateCriteriaBuilder.all(subquery));
		}
		else if (criterionType == CriterionType.GE_SOME) {
			return hibernateCriteriaBuilder.greaterThanOrEqualTo(
				(Expression)path,
				(Expression)hibernateCriteriaBuilder.some(subquery));
		}
		else if (criterionType == CriterionType.GT) {
			return hibernateCriteriaBuilder.greaterThan(
				(Expression)path, (Expression)subquery);
		}
		else if (criterionType == CriterionType.GT_ALL) {
			return hibernateCriteriaBuilder.greaterThan(
				(Expression)path,
				(Expression)hibernateCriteriaBuilder.all(subquery));
		}
		else if (criterionType == CriterionType.GT_SOME) {
			return hibernateCriteriaBuilder.greaterThan(
				(Expression)path,
				(Expression)hibernateCriteriaBuilder.some(subquery));
		}
		else if (criterionType == CriterionType.IN) {
			return path.in(subquery);
		}
		else if (criterionType == CriterionType.LE) {
			return hibernateCriteriaBuilder.lessThanOrEqualTo(
				(Expression)path, (Expression)subquery);
		}
		else if (criterionType == CriterionType.LE_ALL) {
			return hibernateCriteriaBuilder.lessThanOrEqualTo(
				(Expression)path,
				(Expression)hibernateCriteriaBuilder.all(subquery));
		}
		else if (criterionType == CriterionType.LE_SOME) {
			return hibernateCriteriaBuilder.lessThanOrEqualTo(
				(Expression)path,
				(Expression)hibernateCriteriaBuilder.some(subquery));
		}
		else if (criterionType == CriterionType.LT) {
			return hibernateCriteriaBuilder.lessThan(
				(Expression)path, (Expression)subquery);
		}
		else if (criterionType == CriterionType.LT_ALL) {
			return hibernateCriteriaBuilder.lessThan(
				(Expression)path,
				(Expression)hibernateCriteriaBuilder.all(subquery));
		}
		else if (criterionType == CriterionType.LT_SOME) {
			return hibernateCriteriaBuilder.lessThan(
				(Expression)path,
				(Expression)hibernateCriteriaBuilder.some(subquery));
		}
		else if (criterionType == CriterionType.NE) {
			return hibernateCriteriaBuilder.notEqual(path, subquery);
		}
		else if (criterionType == CriterionType.NOT_IN) {
			Predicate predicate = path.in(subquery);

			return predicate.not();
		}

		throw new IllegalStateException(
			"Unexpected criterion type: " + criterionType);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Predicate _toPredicate(
		AbstractQuery<?> abstractQuery, CriterionType criterionType,
		From<?, ?> from, HibernateCriteriaBuilder hibernateCriteriaBuilder,
		String propertyName, int size) {

		Expression<Integer> sizeExpression = hibernateCriteriaBuilder.size(
			(Expression)_getPath(abstractQuery, from, propertyName));

		if (criterionType == CriterionType.EQ) {
			return hibernateCriteriaBuilder.equal(sizeExpression, size);
		}
		else if (criterionType == CriterionType.GE) {
			return hibernateCriteriaBuilder.greaterThanOrEqualTo(
				sizeExpression, size);
		}
		else if (criterionType == CriterionType.GT) {
			return hibernateCriteriaBuilder.greaterThan(sizeExpression, size);
		}
		else if (criterionType == CriterionType.LE) {
			return hibernateCriteriaBuilder.lessThanOrEqualTo(
				sizeExpression, size);
		}
		else if (criterionType == CriterionType.LT) {
			return hibernateCriteriaBuilder.lessThan(sizeExpression, size);
		}
		else if (criterionType == CriterionType.NE) {
			return hibernateCriteriaBuilder.notEqual(sizeExpression, size);
		}

		throw new IllegalStateException(
			"Unexpected criterion type: " + criterionType);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Predicate _toPredicate(
		AbstractQuery<?> abstractQuery, CriterionType criterionType,
		From<?, ?> from, HibernateCriteriaBuilder hibernateCriteriaBuilder,
		String propertyName, String targetPropertyName) {

		Path<?> path = _getPath(abstractQuery, from, propertyName);
		Path<?> targetPath = _getPath(abstractQuery, from, targetPropertyName);

		if (criterionType == CriterionType.EQ) {
			return hibernateCriteriaBuilder.equal(path, targetPath);
		}
		else if (criterionType == CriterionType.GE) {
			return hibernateCriteriaBuilder.greaterThanOrEqualTo(
				(Expression)path, (Expression)targetPath);
		}
		else if (criterionType == CriterionType.GT) {
			return hibernateCriteriaBuilder.greaterThan(
				(Expression)path, (Expression)targetPath);
		}
		else if (criterionType == CriterionType.LE) {
			return hibernateCriteriaBuilder.lessThanOrEqualTo(
				(Expression)path, (Expression)targetPath);
		}
		else if (criterionType == CriterionType.LT) {
			return hibernateCriteriaBuilder.lessThan(
				(Expression)path, (Expression)targetPath);
		}
		else if (criterionType == CriterionType.NE) {
			return hibernateCriteriaBuilder.notEqual(path, targetPath);
		}

		throw new IllegalStateException(
			"Unexpected criterion type: " + criterionType);
	}

	private static Predicate _toPredicate(
		AbstractQuery<?> abstractQuery, List<Criterion> criterions,
		CriterionType criterionType, From<?, ?> from,
		HibernateCriteriaBuilder hibernateCriteriaBuilder) {

		if (criterions.size() == 1) {
			return hibernateCriteriaBuilder.not(
				_toPredicate(
					abstractQuery, criterions.get(0), from,
					hibernateCriteriaBuilder));
		}

		if (criterions.size() == 2) {
			if (criterionType == CriterionType.OR) {
				return hibernateCriteriaBuilder.or(
					_toPredicate(
						abstractQuery, criterions.get(0), from,
						hibernateCriteriaBuilder),
					_toPredicate(
						abstractQuery, criterions.get(1), from,
						hibernateCriteriaBuilder));
			}

			if (criterionType == CriterionType.AND) {
				return hibernateCriteriaBuilder.and(
					_toPredicate(
						abstractQuery, criterions.get(0), from,
						hibernateCriteriaBuilder),
					_toPredicate(
						abstractQuery, criterions.get(1), from,
						hibernateCriteriaBuilder));
			}
		}

		throw new IllegalStateException(
			"Unexpected criterion type: " + criterionType);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Selection<?> _toSelection(
		AbstractQuery<?> abstractQuery, From<?, ?> from,
		HibernateCriteriaBuilder hibernateCriteriaBuilder,
		ProjectionType projectionType, String propertyName) {

		if (projectionType == ProjectionType.ROW_COUNT) {
			return hibernateCriteriaBuilder.count(from);
		}

		Path<?> path = _getPath(abstractQuery, from, propertyName);

		if (projectionType == ProjectionType.AVG) {
			return hibernateCriteriaBuilder.avg((Expression)path);
		}
		else if (projectionType == ProjectionType.COUNT) {
			return hibernateCriteriaBuilder.count(path);
		}
		else if (projectionType == ProjectionType.COUNT_DISTINCT) {
			return hibernateCriteriaBuilder.countDistinct(path);
		}
		else if (projectionType == ProjectionType.MAX) {
			return hibernateCriteriaBuilder.max((Expression)path);
		}
		else if (projectionType == ProjectionType.MIN) {
			return hibernateCriteriaBuilder.min((Expression)path);
		}
		else if (projectionType == ProjectionType.SUM) {
			return hibernateCriteriaBuilder.sum((Expression)path);
		}

		throw new IllegalStateException(
			"Unexpected projection type: " + projectionType);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Selection<?> _toSelection(
		AbstractQuery<?> abstractQuery, From<?, ?> from,
		List<Expression<?>> groupByExpressions,
		HibernateCriteriaBuilder hibernateCriteriaBuilder,
		Projection projection) {

		if (projection instanceof ProjectionListImpl) {
			ProjectionListImpl projectionListImpl =
				(ProjectionListImpl)projection;

			List<Projection> projections = projectionListImpl.getProjections();

			List<Selection<?>> selections = new ArrayList<>(projections.size());

			for (Projection curProjection : projections) {
				selections.add(
					_toSelection(
						abstractQuery, from, groupByExpressions,
						hibernateCriteriaBuilder, curProjection));
			}

			if (selections.size() == 1) {
				return selections.get(0);
			}

			return hibernateCriteriaBuilder.array(selections);
		}

		if (projection instanceof PropertyImpl) {
			PropertyImpl propertyImpl = (PropertyImpl)projection;

			Path<?> path = _getPath(
				abstractQuery, from, propertyImpl.getPropertyName());

			if (propertyImpl.isGroup()) {
				groupByExpressions.add(path);
			}

			return path;
		}

		ProjectionImpl projectionImpl = (ProjectionImpl)projection;

		if (projectionImpl.getAlias() != null) {
			Selection<?> selection = _toSelection(
				abstractQuery, from, groupByExpressions,
				hibernateCriteriaBuilder, projectionImpl.getProjection());

			if (selection.getAlias() != null) {
				SqmSelectableNode<?> sqmSelectableNode =
					(SqmSelectableNode<?>)selection;

				selection = sqmSelectableNode.copy(
					new SqmCopyContext() {

						@Override
						public <T> T getCopy(T original) {
							if (original instanceof From) {
								return original;
							}

							return null;
						}

						@Override
						public <T> T registerCopy(T original, T copy) {
							return copy;
						}

					});
			}

			selection.alias(projectionImpl.getAlias());

			return selection;
		}

		if (projectionImpl.getProjection() != null) {
			abstractQuery.distinct(true);

			return _toSelection(
				abstractQuery, from, groupByExpressions,
				hibernateCriteriaBuilder, projectionImpl.getProjection());
		}

		if (projectionImpl.getSQL() != null) {
			return _toSqlSelection(
				from, groupByExpressions, hibernateCriteriaBuilder,
				projectionImpl);
		}

		return _toSelection(
			abstractQuery, from, hibernateCriteriaBuilder,
			projectionImpl.getProjectionType(),
			projectionImpl.getPropertyName());
	}

	private static Selection<?> _toSqlSelection(
		From<?, ?> from, List<Expression<?>> groupByExpressions,
		HibernateCriteriaBuilder hibernateCriteriaBuilder,
		ProjectionImpl projectionImpl) {

		BasicTypeReference<?>[] basicTypeReferences = _toBasicTypeReferences(
			projectionImpl.getTypes());

		String[] columnAliases = projectionImpl.getColumnAliases();

		List<String> columnSqls = _getColumnSqls(projectionImpl.getSQL());

		Selection<?>[] selections = new Selection<?>[columnSqls.size()];

		for (int i = 0; i < columnSqls.size(); i++) {
			String columnSql = StringUtil.trim(columnSqls.get(i));

			Class<?> javaType = Object.class;

			if ((basicTypeReferences != null) &&
				(i < basicTypeReferences.length) &&
				(basicTypeReferences[i] != null)) {

				javaType = basicTypeReferences[i].getJavaType();
			}

			List<Expression<?>> arguments = new ArrayList<>();

			Selection<?> selection = hibernateCriteriaBuilder.sql(
				_resolveSQL(
					arguments, from, hibernateCriteriaBuilder, columnSql,
					new Object[0]),
				javaType, arguments.toArray(new Expression<?>[0]));

			if ((columnAliases != null) && (i < columnAliases.length) &&
				(columnAliases[i] != null)) {

				selection.alias(columnAliases[i]);
			}

			selections[i] = selection;
		}

		String groupBy = projectionImpl.getGroupBy();

		if ((groupBy != null) && !groupBy.isEmpty()) {
			for (String groupByPart : _getColumnSqls(groupBy)) {
				List<Expression<?>> arguments = new ArrayList<>();

				groupByExpressions.add(
					hibernateCriteriaBuilder.sql(
						_resolveSQL(
							arguments, from, hibernateCriteriaBuilder,
							StringUtil.trim(groupByPart), new Object[0]),
						Object.class, arguments.toArray(new Expression<?>[0])));
			}
		}

		if (selections.length == 1) {
			return selections[0];
		}

		return hibernateCriteriaBuilder.array(selections);
	}

	private static Subquery<?> _toSubquery(
		AbstractQuery<?> abstractQuery, DynamicQueryImpl dynamicQueryImpl,
		Class<?> expectedResultType,
		HibernateCriteriaBuilder hibernateCriteriaBuilder) {

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

		String alias = dynamicQueryImpl.getAlias();

		Root<?> root = subquery.from(dynamicQueryImpl.getClazz());

		if (alias != null) {
			root.alias(alias);
		}

		_applyCriterions(
			subquery, dynamicQueryImpl.getCriterions(),
			hibernateCriteriaBuilder, root);

		_applyProjection(
			subquery, hibernateCriteriaBuilder,
			dynamicQueryImpl.getProjection(), root);

		return subquery;
	}

	private static final String _ROOT_ALIAS_PREFIX = "this_.";

}
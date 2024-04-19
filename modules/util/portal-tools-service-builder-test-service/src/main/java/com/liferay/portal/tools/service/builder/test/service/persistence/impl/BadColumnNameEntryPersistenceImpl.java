/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchBadColumnNameEntryException;
import com.liferay.portal.tools.service.builder.test.model.BadColumnNameEntry;
import com.liferay.portal.tools.service.builder.test.model.BadColumnNameEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.BadColumnNameEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.BadColumnNameEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.BadColumnNameEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.BadColumnNameEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The persistence implementation for the bad column name entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class BadColumnNameEntryPersistenceImpl
	extends BasePersistenceImpl<BadColumnNameEntry>
	implements BadColumnNameEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>BadColumnNameEntryUtil</code> to access the bad column name entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		BadColumnNameEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByType;
	private FinderPath _finderPathWithoutPaginationFindByType;
	private FinderPath _finderPathCountByType;

	/**
	 * Returns all the bad column name entries where type = &#63;.
	 *
	 * @param type the type
	 * @return the matching bad column name entries
	 */
	@Override
	public List<BadColumnNameEntry> findByType(String type) {
		return findByType(type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the bad column name entries where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BadColumnNameEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of bad column name entries
	 * @param end the upper bound of the range of bad column name entries (not inclusive)
	 * @return the range of matching bad column name entries
	 */
	@Override
	public List<BadColumnNameEntry> findByType(
		String type, int start, int end) {

		return findByType(type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the bad column name entries where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BadColumnNameEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of bad column name entries
	 * @param end the upper bound of the range of bad column name entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching bad column name entries
	 */
	@Override
	public List<BadColumnNameEntry> findByType(
		String type, int start, int end,
		OrderByComparator<BadColumnNameEntry> orderByComparator) {

		return findByType(type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the bad column name entries where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BadColumnNameEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of bad column name entries
	 * @param end the upper bound of the range of bad column name entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching bad column name entries
	 */
	@Override
	public List<BadColumnNameEntry> findByType(
		String type, int start, int end,
		OrderByComparator<BadColumnNameEntry> orderByComparator,
		boolean useFinderCache) {

		type = Objects.toString(type, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByType;
				finderArgs = new Object[] {type};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByType;
			finderArgs = new Object[] {type, start, end, orderByComparator};
		}

		List<BadColumnNameEntry> list = null;

		if (useFinderCache) {
			list = (List<BadColumnNameEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (BadColumnNameEntry badColumnNameEntry : list) {
					if (!type.equals(badColumnNameEntry.getType())) {
						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler sb = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					3 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(3);
			}

			sb.append(_SQL_SELECT_BADCOLUMNNAMEENTRY_WHERE);

			boolean bindType = false;

			if (type.isEmpty()) {
				sb.append(_FINDER_COLUMN_TYPE_TYPE_3);
			}
			else {
				bindType = true;

				sb.append(_FINDER_COLUMN_TYPE_TYPE_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(BadColumnNameEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindType) {
					queryPos.add(type);
				}

				list = (List<BadColumnNameEntry>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Returns the first bad column name entry in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bad column name entry
	 * @throws NoSuchBadColumnNameEntryException if a matching bad column name entry could not be found
	 */
	@Override
	public BadColumnNameEntry findByType_First(
			String type,
			OrderByComparator<BadColumnNameEntry> orderByComparator)
		throws NoSuchBadColumnNameEntryException {

		BadColumnNameEntry badColumnNameEntry = fetchByType_First(
			type, orderByComparator);

		if (badColumnNameEntry != null) {
			return badColumnNameEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchBadColumnNameEntryException(sb.toString());
	}

	/**
	 * Returns the first bad column name entry in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bad column name entry, or <code>null</code> if a matching bad column name entry could not be found
	 */
	@Override
	public BadColumnNameEntry fetchByType_First(
		String type, OrderByComparator<BadColumnNameEntry> orderByComparator) {

		List<BadColumnNameEntry> list = findByType(
			type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last bad column name entry in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bad column name entry
	 * @throws NoSuchBadColumnNameEntryException if a matching bad column name entry could not be found
	 */
	@Override
	public BadColumnNameEntry findByType_Last(
			String type,
			OrderByComparator<BadColumnNameEntry> orderByComparator)
		throws NoSuchBadColumnNameEntryException {

		BadColumnNameEntry badColumnNameEntry = fetchByType_Last(
			type, orderByComparator);

		if (badColumnNameEntry != null) {
			return badColumnNameEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchBadColumnNameEntryException(sb.toString());
	}

	/**
	 * Returns the last bad column name entry in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bad column name entry, or <code>null</code> if a matching bad column name entry could not be found
	 */
	@Override
	public BadColumnNameEntry fetchByType_Last(
		String type, OrderByComparator<BadColumnNameEntry> orderByComparator) {

		int count = countByType(type);

		if (count == 0) {
			return null;
		}

		List<BadColumnNameEntry> list = findByType(
			type, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the bad column name entries before and after the current bad column name entry in the ordered set where type = &#63;.
	 *
	 * @param badColumnNameEntryId the primary key of the current bad column name entry
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next bad column name entry
	 * @throws NoSuchBadColumnNameEntryException if a bad column name entry with the primary key could not be found
	 */
	@Override
	public BadColumnNameEntry[] findByType_PrevAndNext(
			long badColumnNameEntryId, String type,
			OrderByComparator<BadColumnNameEntry> orderByComparator)
		throws NoSuchBadColumnNameEntryException {

		type = Objects.toString(type, "");

		BadColumnNameEntry badColumnNameEntry = findByPrimaryKey(
			badColumnNameEntryId);

		Session session = null;

		try {
			session = openSession();

			BadColumnNameEntry[] array = new BadColumnNameEntryImpl[3];

			array[0] = getByType_PrevAndNext(
				session, badColumnNameEntry, type, orderByComparator, true);

			array[1] = badColumnNameEntry;

			array[2] = getByType_PrevAndNext(
				session, badColumnNameEntry, type, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BadColumnNameEntry getByType_PrevAndNext(
		Session session, BadColumnNameEntry badColumnNameEntry, String type,
		OrderByComparator<BadColumnNameEntry> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_BADCOLUMNNAMEENTRY_WHERE);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_TYPE_TYPE_3);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_TYPE_TYPE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(BadColumnNameEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						badColumnNameEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BadColumnNameEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the bad column name entries that the user has permission to view where type = &#63;.
	 *
	 * @param type the type
	 * @return the matching bad column name entries that the user has permission to view
	 */
	@Override
	public List<BadColumnNameEntry> filterFindByType(String type) {
		return filterFindByType(
			type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the bad column name entries that the user has permission to view where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BadColumnNameEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of bad column name entries
	 * @param end the upper bound of the range of bad column name entries (not inclusive)
	 * @return the range of matching bad column name entries that the user has permission to view
	 */
	@Override
	public List<BadColumnNameEntry> filterFindByType(
		String type, int start, int end) {

		return filterFindByType(type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the bad column name entries that the user has permissions to view where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BadColumnNameEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of bad column name entries
	 * @param end the upper bound of the range of bad column name entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching bad column name entries that the user has permission to view
	 */
	@Override
	public List<BadColumnNameEntry> filterFindByType(
		String type, int start, int end,
		OrderByComparator<BadColumnNameEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByType(type, start, end, orderByComparator);
		}

		type = Objects.toString(type, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				3 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BADCOLUMNNAMEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BADCOLUMNNAMEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_TYPE_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_TYPE_TYPE_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BADCOLUMNNAMEENTRY_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(BadColumnNameEntryModelImpl.ORDER_BY_JPQL);
			}
			else {
				sb.append(BadColumnNameEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BadColumnNameEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, BadColumnNameEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, BadColumnNameEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindType) {
				queryPos.add(type);
			}

			return (List<BadColumnNameEntry>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the bad column name entries before and after the current bad column name entry in the ordered set of bad column name entries that the user has permission to view where type = &#63;.
	 *
	 * @param badColumnNameEntryId the primary key of the current bad column name entry
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next bad column name entry
	 * @throws NoSuchBadColumnNameEntryException if a bad column name entry with the primary key could not be found
	 */
	@Override
	public BadColumnNameEntry[] filterFindByType_PrevAndNext(
			long badColumnNameEntryId, String type,
			OrderByComparator<BadColumnNameEntry> orderByComparator)
		throws NoSuchBadColumnNameEntryException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByType_PrevAndNext(
				badColumnNameEntryId, type, orderByComparator);
		}

		type = Objects.toString(type, "");

		BadColumnNameEntry badColumnNameEntry = findByPrimaryKey(
			badColumnNameEntryId);

		Session session = null;

		try {
			session = openSession();

			BadColumnNameEntry[] array = new BadColumnNameEntryImpl[3];

			array[0] = filterGetByType_PrevAndNext(
				session, badColumnNameEntry, type, orderByComparator, true);

			array[1] = badColumnNameEntry;

			array[2] = filterGetByType_PrevAndNext(
				session, badColumnNameEntry, type, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BadColumnNameEntry filterGetByType_PrevAndNext(
		Session session, BadColumnNameEntry badColumnNameEntry, String type,
		OrderByComparator<BadColumnNameEntry> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BADCOLUMNNAMEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BADCOLUMNNAMEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_TYPE_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_TYPE_TYPE_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BADCOLUMNNAMEENTRY_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(BadColumnNameEntryModelImpl.ORDER_BY_JPQL);
			}
			else {
				sb.append(BadColumnNameEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BadColumnNameEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, BadColumnNameEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, BadColumnNameEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						badColumnNameEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BadColumnNameEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the bad column name entries where type = &#63; from the database.
	 *
	 * @param type the type
	 */
	@Override
	public void removeByType(String type) {
		for (BadColumnNameEntry badColumnNameEntry :
				findByType(type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(badColumnNameEntry);
		}
	}

	/**
	 * Returns the number of bad column name entries where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching bad column name entries
	 */
	@Override
	public int countByType(String type) {
		type = Objects.toString(type, "");

		FinderPath finderPath = _finderPathCountByType;

		Object[] finderArgs = new Object[] {type};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_BADCOLUMNNAMEENTRY_WHERE);

			boolean bindType = false;

			if (type.isEmpty()) {
				sb.append(_FINDER_COLUMN_TYPE_TYPE_3);
			}
			else {
				bindType = true;

				sb.append(_FINDER_COLUMN_TYPE_TYPE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindType) {
					queryPos.add(type);
				}

				count = (Long)query.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	/**
	 * Returns the number of bad column name entries that the user has permission to view where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching bad column name entries that the user has permission to view
	 */
	@Override
	public int filterCountByType(String type) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByType(type);
		}

		type = Objects.toString(type, "");

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_BADCOLUMNNAMEENTRY_WHERE);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_TYPE_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_TYPE_TYPE_2_SQL);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BadColumnNameEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindType) {
				queryPos.add(type);
			}

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_TYPE_TYPE_2 =
		"badColumnNameEntry.type = ?";

	private static final String _FINDER_COLUMN_TYPE_TYPE_3 =
		"(badColumnNameEntry.type IS NULL OR badColumnNameEntry.type = '')";

	private static final String _FINDER_COLUMN_TYPE_TYPE_2_SQL =
		"badColumnNameEntry.type_ = ?";

	private static final String _FINDER_COLUMN_TYPE_TYPE_3_SQL =
		"(badColumnNameEntry.type_ IS NULL OR badColumnNameEntry.type_ = '')";

	public BadColumnNameEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(BadColumnNameEntry.class);

		setModelImplClass(BadColumnNameEntryImpl.class);
		setModelPKClass(long.class);

		setTable(BadColumnNameEntryTable.INSTANCE);
	}

	/**
	 * Caches the bad column name entry in the entity cache if it is enabled.
	 *
	 * @param badColumnNameEntry the bad column name entry
	 */
	@Override
	public void cacheResult(BadColumnNameEntry badColumnNameEntry) {
		entityCache.putResult(
			BadColumnNameEntryImpl.class, badColumnNameEntry.getPrimaryKey(),
			badColumnNameEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the bad column name entries in the entity cache if it is enabled.
	 *
	 * @param badColumnNameEntries the bad column name entries
	 */
	@Override
	public void cacheResult(List<BadColumnNameEntry> badColumnNameEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (badColumnNameEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (BadColumnNameEntry badColumnNameEntry : badColumnNameEntries) {
			if (entityCache.getResult(
					BadColumnNameEntryImpl.class,
					badColumnNameEntry.getPrimaryKey()) == null) {

				cacheResult(badColumnNameEntry);
			}
		}
	}

	/**
	 * Clears the cache for all bad column name entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(BadColumnNameEntryImpl.class);

		finderCache.clearCache(BadColumnNameEntryImpl.class);
	}

	/**
	 * Clears the cache for the bad column name entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(BadColumnNameEntry badColumnNameEntry) {
		entityCache.removeResult(
			BadColumnNameEntryImpl.class, badColumnNameEntry);
	}

	@Override
	public void clearCache(List<BadColumnNameEntry> badColumnNameEntries) {
		for (BadColumnNameEntry badColumnNameEntry : badColumnNameEntries) {
			entityCache.removeResult(
				BadColumnNameEntryImpl.class, badColumnNameEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(BadColumnNameEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(BadColumnNameEntryImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new bad column name entry with the primary key. Does not add the bad column name entry to the database.
	 *
	 * @param badColumnNameEntryId the primary key for the new bad column name entry
	 * @return the new bad column name entry
	 */
	@Override
	public BadColumnNameEntry create(long badColumnNameEntryId) {
		BadColumnNameEntry badColumnNameEntry = new BadColumnNameEntryImpl();

		badColumnNameEntry.setNew(true);
		badColumnNameEntry.setPrimaryKey(badColumnNameEntryId);

		return badColumnNameEntry;
	}

	/**
	 * Removes the bad column name entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param badColumnNameEntryId the primary key of the bad column name entry
	 * @return the bad column name entry that was removed
	 * @throws NoSuchBadColumnNameEntryException if a bad column name entry with the primary key could not be found
	 */
	@Override
	public BadColumnNameEntry remove(long badColumnNameEntryId)
		throws NoSuchBadColumnNameEntryException {

		return remove((Serializable)badColumnNameEntryId);
	}

	/**
	 * Removes the bad column name entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the bad column name entry
	 * @return the bad column name entry that was removed
	 * @throws NoSuchBadColumnNameEntryException if a bad column name entry with the primary key could not be found
	 */
	@Override
	public BadColumnNameEntry remove(Serializable primaryKey)
		throws NoSuchBadColumnNameEntryException {

		Session session = null;

		try {
			session = openSession();

			BadColumnNameEntry badColumnNameEntry =
				(BadColumnNameEntry)session.get(
					BadColumnNameEntryImpl.class, primaryKey);

			if (badColumnNameEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchBadColumnNameEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(badColumnNameEntry);
		}
		catch (NoSuchBadColumnNameEntryException noSuchEntityException) {
			throw noSuchEntityException;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	protected BadColumnNameEntry removeImpl(
		BadColumnNameEntry badColumnNameEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(badColumnNameEntry)) {
				badColumnNameEntry = (BadColumnNameEntry)session.get(
					BadColumnNameEntryImpl.class,
					badColumnNameEntry.getPrimaryKeyObj());
			}

			if (badColumnNameEntry != null) {
				session.delete(badColumnNameEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (badColumnNameEntry != null) {
			clearCache(badColumnNameEntry);
		}

		return badColumnNameEntry;
	}

	@Override
	public BadColumnNameEntry updateImpl(
		BadColumnNameEntry badColumnNameEntry) {

		boolean isNew = badColumnNameEntry.isNew();

		if (!(badColumnNameEntry instanceof BadColumnNameEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(badColumnNameEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					badColumnNameEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in badColumnNameEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom BadColumnNameEntry implementation " +
					badColumnNameEntry.getClass());
		}

		BadColumnNameEntryModelImpl badColumnNameEntryModelImpl =
			(BadColumnNameEntryModelImpl)badColumnNameEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(badColumnNameEntry);
			}
			else {
				badColumnNameEntry = (BadColumnNameEntry)session.merge(
					badColumnNameEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			BadColumnNameEntryImpl.class, badColumnNameEntryModelImpl, false,
			true);

		if (isNew) {
			badColumnNameEntry.setNew(false);
		}

		badColumnNameEntry.resetOriginalValues();

		return badColumnNameEntry;
	}

	/**
	 * Returns the bad column name entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the bad column name entry
	 * @return the bad column name entry
	 * @throws NoSuchBadColumnNameEntryException if a bad column name entry with the primary key could not be found
	 */
	@Override
	public BadColumnNameEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchBadColumnNameEntryException {

		BadColumnNameEntry badColumnNameEntry = fetchByPrimaryKey(primaryKey);

		if (badColumnNameEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchBadColumnNameEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return badColumnNameEntry;
	}

	/**
	 * Returns the bad column name entry with the primary key or throws a <code>NoSuchBadColumnNameEntryException</code> if it could not be found.
	 *
	 * @param badColumnNameEntryId the primary key of the bad column name entry
	 * @return the bad column name entry
	 * @throws NoSuchBadColumnNameEntryException if a bad column name entry with the primary key could not be found
	 */
	@Override
	public BadColumnNameEntry findByPrimaryKey(long badColumnNameEntryId)
		throws NoSuchBadColumnNameEntryException {

		return findByPrimaryKey((Serializable)badColumnNameEntryId);
	}

	/**
	 * Returns the bad column name entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param badColumnNameEntryId the primary key of the bad column name entry
	 * @return the bad column name entry, or <code>null</code> if a bad column name entry with the primary key could not be found
	 */
	@Override
	public BadColumnNameEntry fetchByPrimaryKey(long badColumnNameEntryId) {
		return fetchByPrimaryKey((Serializable)badColumnNameEntryId);
	}

	/**
	 * Returns all the bad column name entries.
	 *
	 * @return the bad column name entries
	 */
	@Override
	public List<BadColumnNameEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the bad column name entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BadColumnNameEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of bad column name entries
	 * @param end the upper bound of the range of bad column name entries (not inclusive)
	 * @return the range of bad column name entries
	 */
	@Override
	public List<BadColumnNameEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the bad column name entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BadColumnNameEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of bad column name entries
	 * @param end the upper bound of the range of bad column name entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of bad column name entries
	 */
	@Override
	public List<BadColumnNameEntry> findAll(
		int start, int end,
		OrderByComparator<BadColumnNameEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the bad column name entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BadColumnNameEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of bad column name entries
	 * @param end the upper bound of the range of bad column name entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of bad column name entries
	 */
	@Override
	public List<BadColumnNameEntry> findAll(
		int start, int end,
		OrderByComparator<BadColumnNameEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindAll;
				finderArgs = FINDER_ARGS_EMPTY;
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindAll;
			finderArgs = new Object[] {start, end, orderByComparator};
		}

		List<BadColumnNameEntry> list = null;

		if (useFinderCache) {
			list = (List<BadColumnNameEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_BADCOLUMNNAMEENTRY);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_BADCOLUMNNAMEENTRY;

				sql = sql.concat(BadColumnNameEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<BadColumnNameEntry>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Removes all the bad column name entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (BadColumnNameEntry badColumnNameEntry : findAll()) {
			remove(badColumnNameEntry);
		}
	}

	/**
	 * Returns the number of bad column name entries.
	 *
	 * @return the number of bad column name entries
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(
					_SQL_COUNT_BADCOLUMNNAMEENTRY);

				count = (Long)query.uniqueResult();

				finderCache.putResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "badColumnNameEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_BADCOLUMNNAMEENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return BadColumnNameEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the bad column name entry persistence.
	 */
	public void afterPropertiesSet() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathWithPaginationFindByType = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByType",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"type_"}, true);

		_finderPathWithoutPaginationFindByType = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByType",
			new String[] {String.class.getName()}, new String[] {"type_"},
			true);

		_finderPathCountByType = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByType",
			new String[] {String.class.getName()}, new String[] {"type_"},
			false);

		BadColumnNameEntryUtil.setPersistence(this);
	}

	public void destroy() {
		BadColumnNameEntryUtil.setPersistence(null);

		entityCache.removeCache(BadColumnNameEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_BADCOLUMNNAMEENTRY =
		"SELECT badColumnNameEntry FROM BadColumnNameEntry badColumnNameEntry";

	private static final String _SQL_SELECT_BADCOLUMNNAMEENTRY_WHERE =
		"SELECT badColumnNameEntry FROM BadColumnNameEntry badColumnNameEntry WHERE ";

	private static final String _SQL_COUNT_BADCOLUMNNAMEENTRY =
		"SELECT COUNT(badColumnNameEntry) FROM BadColumnNameEntry badColumnNameEntry";

	private static final String _SQL_COUNT_BADCOLUMNNAMEENTRY_WHERE =
		"SELECT COUNT(badColumnNameEntry) FROM BadColumnNameEntry badColumnNameEntry WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"badColumnNameEntry.badColumnNameEntryId";

	private static final String _FILTER_SQL_SELECT_BADCOLUMNNAMEENTRY_WHERE =
		"SELECT DISTINCT {badColumnNameEntry.*} FROM BadColumnNameEntry badColumnNameEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_BADCOLUMNNAMEENTRY_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {BadColumnNameEntry.*} FROM (SELECT DISTINCT badColumnNameEntry.badColumnNameEntryId FROM BadColumnNameEntry badColumnNameEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_BADCOLUMNNAMEENTRY_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN BadColumnNameEntry ON TEMP_TABLE.badColumnNameEntryId = BadColumnNameEntry.badColumnNameEntryId";

	private static final String _FILTER_SQL_COUNT_BADCOLUMNNAMEENTRY_WHERE =
		"SELECT COUNT(DISTINCT badColumnNameEntry.badColumnNameEntryId) AS COUNT_VALUE FROM BadColumnNameEntry badColumnNameEntry WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "badColumnNameEntry";

	private static final String _FILTER_ENTITY_TABLE = "BadColumnNameEntry";

	private static final String _ORDER_BY_ENTITY_ALIAS = "badColumnNameEntry.";

	private static final String _ORDER_BY_ENTITY_TABLE = "BadColumnNameEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No BadColumnNameEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No BadColumnNameEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		BadColumnNameEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
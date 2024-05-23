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
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchFilterFindEntryException;
import com.liferay.portal.tools.service.builder.test.model.FilterFindEntry;
import com.liferay.portal.tools.service.builder.test.model.FilterFindEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.FilterFindEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.FilterFindEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.FilterFindEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.FilterFindEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The persistence implementation for the filter find entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class FilterFindEntryPersistenceImpl
	extends BasePersistenceImpl<FilterFindEntry>
	implements FilterFindEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>FilterFindEntryUtil</code> to access the filter find entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		FilterFindEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByG_I_T;
	private FinderPath _finderPathWithoutPaginationFindByG_I_T;
	private FinderPath _finderPathCountByG_I_T;
	private FinderPath _finderPathWithPaginationCountByG_I_T;

	/**
	 * Returns all the filter find entries where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @return the matching filter find entries
	 */
	@Override
	public List<FilterFindEntry> findByG_I_T(
		long groupId, int integer, String type) {

		return findByG_I_T(
			groupId, integer, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the filter find entries where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @return the range of matching filter find entries
	 */
	@Override
	public List<FilterFindEntry> findByG_I_T(
		long groupId, int integer, String type, int start, int end) {

		return findByG_I_T(groupId, integer, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the filter find entries where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching filter find entries
	 */
	@Override
	public List<FilterFindEntry> findByG_I_T(
		long groupId, int integer, String type, int start, int end,
		OrderByComparator<FilterFindEntry> orderByComparator) {

		return findByG_I_T(
			groupId, integer, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the filter find entries where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching filter find entries
	 */
	@Override
	public List<FilterFindEntry> findByG_I_T(
		long groupId, int integer, String type, int start, int end,
		OrderByComparator<FilterFindEntry> orderByComparator,
		boolean useFinderCache) {

		type = Objects.toString(type, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByG_I_T;
				finderArgs = new Object[] {groupId, integer, type};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByG_I_T;
			finderArgs = new Object[] {
				groupId, integer, type, start, end, orderByComparator
			};
		}

		List<FilterFindEntry> list = null;

		if (useFinderCache) {
			list = (List<FilterFindEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (FilterFindEntry filterFindEntry : list) {
					if ((groupId != filterFindEntry.getGroupId()) ||
						(integer != filterFindEntry.getInteger()) ||
						!type.equals(filterFindEntry.getType())) {

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
					5 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(5);
			}

			sb.append(_SQL_SELECT_FILTERFINDENTRY_WHERE);

			sb.append(_FINDER_COLUMN_G_I_T_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_I_T_INTEGER_2);

			boolean bindType = false;

			if (type.isEmpty()) {
				sb.append(_FINDER_COLUMN_G_I_T_TYPE_3);
			}
			else {
				bindType = true;

				sb.append(_FINDER_COLUMN_G_I_T_TYPE_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(FilterFindEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(integer);

				if (bindType) {
					queryPos.add(type);
				}

				list = (List<FilterFindEntry>)QueryUtil.list(
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
	 * Returns the first filter find entry in the ordered set where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching filter find entry
	 * @throws NoSuchFilterFindEntryException if a matching filter find entry could not be found
	 */
	@Override
	public FilterFindEntry findByG_I_T_First(
			long groupId, int integer, String type,
			OrderByComparator<FilterFindEntry> orderByComparator)
		throws NoSuchFilterFindEntryException {

		FilterFindEntry filterFindEntry = fetchByG_I_T_First(
			groupId, integer, type, orderByComparator);

		if (filterFindEntry != null) {
			return filterFindEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", integer=");
		sb.append(integer);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchFilterFindEntryException(sb.toString());
	}

	/**
	 * Returns the first filter find entry in the ordered set where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching filter find entry, or <code>null</code> if a matching filter find entry could not be found
	 */
	@Override
	public FilterFindEntry fetchByG_I_T_First(
		long groupId, int integer, String type,
		OrderByComparator<FilterFindEntry> orderByComparator) {

		List<FilterFindEntry> list = findByG_I_T(
			groupId, integer, type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last filter find entry in the ordered set where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching filter find entry
	 * @throws NoSuchFilterFindEntryException if a matching filter find entry could not be found
	 */
	@Override
	public FilterFindEntry findByG_I_T_Last(
			long groupId, int integer, String type,
			OrderByComparator<FilterFindEntry> orderByComparator)
		throws NoSuchFilterFindEntryException {

		FilterFindEntry filterFindEntry = fetchByG_I_T_Last(
			groupId, integer, type, orderByComparator);

		if (filterFindEntry != null) {
			return filterFindEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", integer=");
		sb.append(integer);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchFilterFindEntryException(sb.toString());
	}

	/**
	 * Returns the last filter find entry in the ordered set where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching filter find entry, or <code>null</code> if a matching filter find entry could not be found
	 */
	@Override
	public FilterFindEntry fetchByG_I_T_Last(
		long groupId, int integer, String type,
		OrderByComparator<FilterFindEntry> orderByComparator) {

		int count = countByG_I_T(groupId, integer, type);

		if (count == 0) {
			return null;
		}

		List<FilterFindEntry> list = findByG_I_T(
			groupId, integer, type, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the filter find entries before and after the current filter find entry in the ordered set where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * @param filterFindEntryId the primary key of the current filter find entry
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next filter find entry
	 * @throws NoSuchFilterFindEntryException if a filter find entry with the primary key could not be found
	 */
	@Override
	public FilterFindEntry[] findByG_I_T_PrevAndNext(
			long filterFindEntryId, long groupId, int integer, String type,
			OrderByComparator<FilterFindEntry> orderByComparator)
		throws NoSuchFilterFindEntryException {

		type = Objects.toString(type, "");

		FilterFindEntry filterFindEntry = findByPrimaryKey(filterFindEntryId);

		Session session = null;

		try {
			session = openSession();

			FilterFindEntry[] array = new FilterFindEntryImpl[3];

			array[0] = getByG_I_T_PrevAndNext(
				session, filterFindEntry, groupId, integer, type,
				orderByComparator, true);

			array[1] = filterFindEntry;

			array[2] = getByG_I_T_PrevAndNext(
				session, filterFindEntry, groupId, integer, type,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected FilterFindEntry getByG_I_T_PrevAndNext(
		Session session, FilterFindEntry filterFindEntry, long groupId,
		int integer, String type,
		OrderByComparator<FilterFindEntry> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_FILTERFINDENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_I_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_I_T_INTEGER_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_I_T_TYPE_3);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_I_T_TYPE_2);
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
			sb.append(FilterFindEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(integer);

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						filterFindEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<FilterFindEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the filter find entries that the user has permission to view where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @return the matching filter find entries that the user has permission to view
	 */
	@Override
	public List<FilterFindEntry> filterFindByG_I_T(
		long groupId, int integer, String type) {

		return filterFindByG_I_T(
			groupId, integer, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the filter find entries that the user has permission to view where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @return the range of matching filter find entries that the user has permission to view
	 */
	@Override
	public List<FilterFindEntry> filterFindByG_I_T(
		long groupId, int integer, String type, int start, int end) {

		return filterFindByG_I_T(groupId, integer, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the filter find entries that the user has permissions to view where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching filter find entries that the user has permission to view
	 */
	@Override
	public List<FilterFindEntry> filterFindByG_I_T(
		long groupId, int integer, String type, int start, int end,
		OrderByComparator<FilterFindEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_I_T(
				groupId, integer, type, start, end, orderByComparator);
		}

		type = Objects.toString(type, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_FILTERFINDENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_FILTERFINDENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_I_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_I_T_INTEGER_2_SQL);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_I_T_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_I_T_TYPE_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_FILTERFINDENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(
					FilterFindEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(FilterFindEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), FilterFindEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, FilterFindEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, FilterFindEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(integer);

			if (bindType) {
				queryPos.add(type);
			}

			return (List<FilterFindEntry>)QueryUtil.list(
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
	 * Returns the filter find entries before and after the current filter find entry in the ordered set of filter find entries that the user has permission to view where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * @param filterFindEntryId the primary key of the current filter find entry
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next filter find entry
	 * @throws NoSuchFilterFindEntryException if a filter find entry with the primary key could not be found
	 */
	@Override
	public FilterFindEntry[] filterFindByG_I_T_PrevAndNext(
			long filterFindEntryId, long groupId, int integer, String type,
			OrderByComparator<FilterFindEntry> orderByComparator)
		throws NoSuchFilterFindEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_I_T_PrevAndNext(
				filterFindEntryId, groupId, integer, type, orderByComparator);
		}

		type = Objects.toString(type, "");

		FilterFindEntry filterFindEntry = findByPrimaryKey(filterFindEntryId);

		Session session = null;

		try {
			session = openSession();

			FilterFindEntry[] array = new FilterFindEntryImpl[3];

			array[0] = filterGetByG_I_T_PrevAndNext(
				session, filterFindEntry, groupId, integer, type,
				orderByComparator, true);

			array[1] = filterFindEntry;

			array[2] = filterGetByG_I_T_PrevAndNext(
				session, filterFindEntry, groupId, integer, type,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected FilterFindEntry filterGetByG_I_T_PrevAndNext(
		Session session, FilterFindEntry filterFindEntry, long groupId,
		int integer, String type,
		OrderByComparator<FilterFindEntry> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_FILTERFINDENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_FILTERFINDENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_I_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_I_T_INTEGER_2_SQL);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_I_T_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_I_T_TYPE_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_FILTERFINDENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(
					FilterFindEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(FilterFindEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), FilterFindEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, FilterFindEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, FilterFindEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(integer);

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						filterFindEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<FilterFindEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the filter find entries that the user has permission to view where groupId = &#63; and integer = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param types the types
	 * @return the matching filter find entries that the user has permission to view
	 */
	@Override
	public List<FilterFindEntry> filterFindByG_I_T(
		long groupId, int integer, String[] types) {

		return filterFindByG_I_T(
			groupId, integer, types, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the filter find entries that the user has permission to view where groupId = &#63; and integer = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param types the types
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @return the range of matching filter find entries that the user has permission to view
	 */
	@Override
	public List<FilterFindEntry> filterFindByG_I_T(
		long groupId, int integer, String[] types, int start, int end) {

		return filterFindByG_I_T(groupId, integer, types, start, end, null);
	}

	/**
	 * Returns an ordered range of all the filter find entries that the user has permission to view where groupId = &#63; and integer = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param types the types
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching filter find entries that the user has permission to view
	 */
	@Override
	public List<FilterFindEntry> filterFindByG_I_T(
		long groupId, int integer, String[] types, int start, int end,
		OrderByComparator<FilterFindEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_I_T(
				groupId, integer, types, start, end, orderByComparator);
		}

		if (types == null) {
			types = new String[0];
		}
		else if (types.length > 1) {
			for (int i = 0; i < types.length; i++) {
				types[i] = Objects.toString(types[i], "");
			}

			types = ArrayUtil.sortedUnique(types);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_FILTERFINDENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_FILTERFINDENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_I_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_I_T_INTEGER_2_SQL);

		if (types.length > 0) {
			sb.append("(");

			for (int i = 0; i < types.length; i++) {
				String type = types[i];

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_I_T_TYPE_3_SQL);
				}
				else {
					sb.append(_FINDER_COLUMN_G_I_T_TYPE_2_SQL);
				}

				if ((i + 1) < types.length) {
					sb.append(WHERE_OR);
				}
			}

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_FILTERFINDENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(
					FilterFindEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(FilterFindEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), FilterFindEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, FilterFindEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, FilterFindEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(integer);

			for (String type : types) {
				if ((type != null) && !type.isEmpty()) {
					queryPos.add(type);
				}
			}

			return (List<FilterFindEntry>)QueryUtil.list(
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
	 * Returns all the filter find entries where groupId = &#63; and integer = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param types the types
	 * @return the matching filter find entries
	 */
	@Override
	public List<FilterFindEntry> findByG_I_T(
		long groupId, int integer, String[] types) {

		return findByG_I_T(
			groupId, integer, types, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the filter find entries where groupId = &#63; and integer = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param types the types
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @return the range of matching filter find entries
	 */
	@Override
	public List<FilterFindEntry> findByG_I_T(
		long groupId, int integer, String[] types, int start, int end) {

		return findByG_I_T(groupId, integer, types, start, end, null);
	}

	/**
	 * Returns an ordered range of all the filter find entries where groupId = &#63; and integer = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param types the types
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching filter find entries
	 */
	@Override
	public List<FilterFindEntry> findByG_I_T(
		long groupId, int integer, String[] types, int start, int end,
		OrderByComparator<FilterFindEntry> orderByComparator) {

		return findByG_I_T(
			groupId, integer, types, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the filter find entries where groupId = &#63; and integer = &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param types the types
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching filter find entries
	 */
	@Override
	public List<FilterFindEntry> findByG_I_T(
		long groupId, int integer, String[] types, int start, int end,
		OrderByComparator<FilterFindEntry> orderByComparator,
		boolean useFinderCache) {

		if (types == null) {
			types = new String[0];
		}
		else if (types.length > 1) {
			for (int i = 0; i < types.length; i++) {
				types[i] = Objects.toString(types[i], "");
			}

			types = ArrayUtil.sortedUnique(types);
		}

		if (types.length == 1) {
			return findByG_I_T(
				groupId, integer, types[0], start, end, orderByComparator);
		}

		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, integer, StringUtil.merge(types)
				};
			}
		}
		else if (useFinderCache) {
			finderArgs = new Object[] {
				groupId, integer, StringUtil.merge(types), start, end,
				orderByComparator
			};
		}

		List<FilterFindEntry> list = null;

		if (useFinderCache) {
			list = (List<FilterFindEntry>)finderCache.getResult(
				_finderPathWithPaginationFindByG_I_T, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (FilterFindEntry filterFindEntry : list) {
					if ((groupId != filterFindEntry.getGroupId()) ||
						(integer != filterFindEntry.getInteger()) ||
						!ArrayUtil.contains(types, filterFindEntry.getType())) {

						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler sb = new StringBundler();

			sb.append(_SQL_SELECT_FILTERFINDENTRY_WHERE);

			sb.append(_FINDER_COLUMN_G_I_T_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_I_T_INTEGER_2);

			if (types.length > 0) {
				sb.append("(");

				for (int i = 0; i < types.length; i++) {
					String type = types[i];

					if (type.isEmpty()) {
						sb.append(_FINDER_COLUMN_G_I_T_TYPE_3);
					}
					else {
						sb.append(_FINDER_COLUMN_G_I_T_TYPE_2);
					}

					if ((i + 1) < types.length) {
						sb.append(WHERE_OR);
					}
				}

				sb.append(")");
			}

			sb.setStringAt(
				removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(FilterFindEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(integer);

				for (String type : types) {
					if ((type != null) && !type.isEmpty()) {
						queryPos.add(type);
					}
				}

				list = (List<FilterFindEntry>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(
						_finderPathWithPaginationFindByG_I_T, finderArgs, list);
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
	 * Removes all the filter find entries where groupId = &#63; and integer = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 */
	@Override
	public void removeByG_I_T(long groupId, int integer, String type) {
		for (FilterFindEntry filterFindEntry :
				findByG_I_T(
					groupId, integer, type, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(filterFindEntry);
		}
	}

	/**
	 * Returns the number of filter find entries where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @return the number of matching filter find entries
	 */
	@Override
	public int countByG_I_T(long groupId, int integer, String type) {
		type = Objects.toString(type, "");

		FinderPath finderPath = _finderPathCountByG_I_T;

		Object[] finderArgs = new Object[] {groupId, integer, type};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_FILTERFINDENTRY_WHERE);

			sb.append(_FINDER_COLUMN_G_I_T_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_I_T_INTEGER_2);

			boolean bindType = false;

			if (type.isEmpty()) {
				sb.append(_FINDER_COLUMN_G_I_T_TYPE_3);
			}
			else {
				bindType = true;

				sb.append(_FINDER_COLUMN_G_I_T_TYPE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(integer);

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
	 * Returns the number of filter find entries where groupId = &#63; and integer = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param types the types
	 * @return the number of matching filter find entries
	 */
	@Override
	public int countByG_I_T(long groupId, int integer, String[] types) {
		if (types == null) {
			types = new String[0];
		}
		else if (types.length > 1) {
			for (int i = 0; i < types.length; i++) {
				types[i] = Objects.toString(types[i], "");
			}

			types = ArrayUtil.sortedUnique(types);
		}

		Object[] finderArgs = new Object[] {
			groupId, integer, StringUtil.merge(types)
		};

		Long count = (Long)finderCache.getResult(
			_finderPathWithPaginationCountByG_I_T, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler();

			sb.append(_SQL_COUNT_FILTERFINDENTRY_WHERE);

			sb.append(_FINDER_COLUMN_G_I_T_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_I_T_INTEGER_2);

			if (types.length > 0) {
				sb.append("(");

				for (int i = 0; i < types.length; i++) {
					String type = types[i];

					if (type.isEmpty()) {
						sb.append(_FINDER_COLUMN_G_I_T_TYPE_3);
					}
					else {
						sb.append(_FINDER_COLUMN_G_I_T_TYPE_2);
					}

					if ((i + 1) < types.length) {
						sb.append(WHERE_OR);
					}
				}

				sb.append(")");
			}

			sb.setStringAt(
				removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(integer);

				for (String type : types) {
					if ((type != null) && !type.isEmpty()) {
						queryPos.add(type);
					}
				}

				count = (Long)query.uniqueResult();

				finderCache.putResult(
					_finderPathWithPaginationCountByG_I_T, finderArgs, count);
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
	 * Returns the number of filter find entries that the user has permission to view where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @return the number of matching filter find entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_I_T(long groupId, int integer, String type) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_I_T(groupId, integer, type);
		}

		type = Objects.toString(type, "");

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_FILTERFINDENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_I_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_I_T_INTEGER_2_SQL);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_I_T_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_I_T_TYPE_2_SQL);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), FilterFindEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(integer);

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

	/**
	 * Returns the number of filter find entries that the user has permission to view where groupId = &#63; and integer = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param types the types
	 * @return the number of matching filter find entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_I_T(long groupId, int integer, String[] types) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_I_T(groupId, integer, types);
		}

		if (types == null) {
			types = new String[0];
		}
		else if (types.length > 1) {
			for (int i = 0; i < types.length; i++) {
				types[i] = Objects.toString(types[i], "");
			}

			types = ArrayUtil.sortedUnique(types);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_FILTERFINDENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_I_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_I_T_INTEGER_2_SQL);

		if (types.length > 0) {
			sb.append("(");

			for (int i = 0; i < types.length; i++) {
				String type = types[i];

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_I_T_TYPE_3_SQL);
				}
				else {
					sb.append(_FINDER_COLUMN_G_I_T_TYPE_2_SQL);
				}

				if ((i + 1) < types.length) {
					sb.append(WHERE_OR);
				}
			}

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), FilterFindEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(integer);

			for (String type : types) {
				if ((type != null) && !type.isEmpty()) {
					queryPos.add(type);
				}
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

	private static final String _FINDER_COLUMN_G_I_T_GROUPID_2 =
		"filterFindEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_I_T_INTEGER_2 =
		"filterFindEntry.integer = ? AND ";

	private static final String _FINDER_COLUMN_G_I_T_INTEGER_2_SQL =
		"filterFindEntry.integer_ = ? AND ";

	private static final String _FINDER_COLUMN_G_I_T_TYPE_2 =
		"filterFindEntry.type = ?";

	private static final String _FINDER_COLUMN_G_I_T_TYPE_3 =
		"(filterFindEntry.type IS NULL OR filterFindEntry.type = '')";

	private static final String _FINDER_COLUMN_G_I_T_TYPE_2_SQL =
		"filterFindEntry.type_ = ?";

	private static final String _FINDER_COLUMN_G_I_T_TYPE_3_SQL =
		"(filterFindEntry.type_ IS NULL OR filterFindEntry.type_ = '')";

	public FilterFindEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");
		dbColumnNames.put("integer", "integer_");

		setDBColumnNames(dbColumnNames);

		setModelClass(FilterFindEntry.class);

		setModelImplClass(FilterFindEntryImpl.class);
		setModelPKClass(long.class);

		setTable(FilterFindEntryTable.INSTANCE);
	}

	/**
	 * Caches the filter find entry in the entity cache if it is enabled.
	 *
	 * @param filterFindEntry the filter find entry
	 */
	@Override
	public void cacheResult(FilterFindEntry filterFindEntry) {
		entityCache.putResult(
			FilterFindEntryImpl.class, filterFindEntry.getPrimaryKey(),
			filterFindEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the filter find entries in the entity cache if it is enabled.
	 *
	 * @param filterFindEntries the filter find entries
	 */
	@Override
	public void cacheResult(List<FilterFindEntry> filterFindEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (filterFindEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (FilterFindEntry filterFindEntry : filterFindEntries) {
			if (entityCache.getResult(
					FilterFindEntryImpl.class,
					filterFindEntry.getPrimaryKey()) == null) {

				cacheResult(filterFindEntry);
			}
		}
	}

	/**
	 * Clears the cache for all filter find entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(FilterFindEntryImpl.class);

		finderCache.clearCache(FilterFindEntryImpl.class);
	}

	/**
	 * Clears the cache for the filter find entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(FilterFindEntry filterFindEntry) {
		entityCache.removeResult(FilterFindEntryImpl.class, filterFindEntry);
	}

	@Override
	public void clearCache(List<FilterFindEntry> filterFindEntries) {
		for (FilterFindEntry filterFindEntry : filterFindEntries) {
			entityCache.removeResult(
				FilterFindEntryImpl.class, filterFindEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FilterFindEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(FilterFindEntryImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new filter find entry with the primary key. Does not add the filter find entry to the database.
	 *
	 * @param filterFindEntryId the primary key for the new filter find entry
	 * @return the new filter find entry
	 */
	@Override
	public FilterFindEntry create(long filterFindEntryId) {
		FilterFindEntry filterFindEntry = new FilterFindEntryImpl();

		filterFindEntry.setNew(true);
		filterFindEntry.setPrimaryKey(filterFindEntryId);

		return filterFindEntry;
	}

	/**
	 * Removes the filter find entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param filterFindEntryId the primary key of the filter find entry
	 * @return the filter find entry that was removed
	 * @throws NoSuchFilterFindEntryException if a filter find entry with the primary key could not be found
	 */
	@Override
	public FilterFindEntry remove(long filterFindEntryId)
		throws NoSuchFilterFindEntryException {

		return remove((Serializable)filterFindEntryId);
	}

	/**
	 * Removes the filter find entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the filter find entry
	 * @return the filter find entry that was removed
	 * @throws NoSuchFilterFindEntryException if a filter find entry with the primary key could not be found
	 */
	@Override
	public FilterFindEntry remove(Serializable primaryKey)
		throws NoSuchFilterFindEntryException {

		Session session = null;

		try {
			session = openSession();

			FilterFindEntry filterFindEntry = (FilterFindEntry)session.get(
				FilterFindEntryImpl.class, primaryKey);

			if (filterFindEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchFilterFindEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(filterFindEntry);
		}
		catch (NoSuchFilterFindEntryException noSuchEntityException) {
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
	protected FilterFindEntry removeImpl(FilterFindEntry filterFindEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(filterFindEntry)) {
				filterFindEntry = (FilterFindEntry)session.get(
					FilterFindEntryImpl.class,
					filterFindEntry.getPrimaryKeyObj());
			}

			if (filterFindEntry != null) {
				session.delete(filterFindEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (filterFindEntry != null) {
			clearCache(filterFindEntry);
		}

		return filterFindEntry;
	}

	@Override
	public FilterFindEntry updateImpl(FilterFindEntry filterFindEntry) {
		boolean isNew = filterFindEntry.isNew();

		if (!(filterFindEntry instanceof FilterFindEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(filterFindEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					filterFindEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in filterFindEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom FilterFindEntry implementation " +
					filterFindEntry.getClass());
		}

		FilterFindEntryModelImpl filterFindEntryModelImpl =
			(FilterFindEntryModelImpl)filterFindEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(filterFindEntry);
			}
			else {
				filterFindEntry = (FilterFindEntry)session.merge(
					filterFindEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			FilterFindEntryImpl.class, filterFindEntryModelImpl, false, true);

		if (isNew) {
			filterFindEntry.setNew(false);
		}

		filterFindEntry.resetOriginalValues();

		return filterFindEntry;
	}

	/**
	 * Returns the filter find entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the filter find entry
	 * @return the filter find entry
	 * @throws NoSuchFilterFindEntryException if a filter find entry with the primary key could not be found
	 */
	@Override
	public FilterFindEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchFilterFindEntryException {

		FilterFindEntry filterFindEntry = fetchByPrimaryKey(primaryKey);

		if (filterFindEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchFilterFindEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return filterFindEntry;
	}

	/**
	 * Returns the filter find entry with the primary key or throws a <code>NoSuchFilterFindEntryException</code> if it could not be found.
	 *
	 * @param filterFindEntryId the primary key of the filter find entry
	 * @return the filter find entry
	 * @throws NoSuchFilterFindEntryException if a filter find entry with the primary key could not be found
	 */
	@Override
	public FilterFindEntry findByPrimaryKey(long filterFindEntryId)
		throws NoSuchFilterFindEntryException {

		return findByPrimaryKey((Serializable)filterFindEntryId);
	}

	/**
	 * Returns the filter find entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param filterFindEntryId the primary key of the filter find entry
	 * @return the filter find entry, or <code>null</code> if a filter find entry with the primary key could not be found
	 */
	@Override
	public FilterFindEntry fetchByPrimaryKey(long filterFindEntryId) {
		return fetchByPrimaryKey((Serializable)filterFindEntryId);
	}

	/**
	 * Returns all the filter find entries.
	 *
	 * @return the filter find entries
	 */
	@Override
	public List<FilterFindEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the filter find entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @return the range of filter find entries
	 */
	@Override
	public List<FilterFindEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the filter find entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of filter find entries
	 */
	@Override
	public List<FilterFindEntry> findAll(
		int start, int end,
		OrderByComparator<FilterFindEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the filter find entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of filter find entries
	 */
	@Override
	public List<FilterFindEntry> findAll(
		int start, int end,
		OrderByComparator<FilterFindEntry> orderByComparator,
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

		List<FilterFindEntry> list = null;

		if (useFinderCache) {
			list = (List<FilterFindEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_FILTERFINDENTRY);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_FILTERFINDENTRY;

				sql = sql.concat(FilterFindEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<FilterFindEntry>)QueryUtil.list(
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
	 * Removes all the filter find entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (FilterFindEntry filterFindEntry : findAll()) {
			remove(filterFindEntry);
		}
	}

	/**
	 * Returns the number of filter find entries.
	 *
	 * @return the number of filter find entries
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_FILTERFINDENTRY);

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
		return "filterFindEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_FILTERFINDENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return FilterFindEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the filter find entry persistence.
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

		_finderPathWithPaginationFindByG_I_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_I_T",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "integer_", "type_"}, true);

		_finderPathWithoutPaginationFindByG_I_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_I_T",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "integer_", "type_"}, true);

		_finderPathCountByG_I_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_I_T",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "integer_", "type_"}, false);

		_finderPathWithPaginationCountByG_I_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_I_T",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "integer_", "type_"}, false);

		FilterFindEntryUtil.setPersistence(this);
	}

	public void destroy() {
		FilterFindEntryUtil.setPersistence(null);

		entityCache.removeCache(FilterFindEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_FILTERFINDENTRY =
		"SELECT filterFindEntry FROM FilterFindEntry filterFindEntry";

	private static final String _SQL_SELECT_FILTERFINDENTRY_WHERE =
		"SELECT filterFindEntry FROM FilterFindEntry filterFindEntry WHERE ";

	private static final String _SQL_COUNT_FILTERFINDENTRY =
		"SELECT COUNT(filterFindEntry) FROM FilterFindEntry filterFindEntry";

	private static final String _SQL_COUNT_FILTERFINDENTRY_WHERE =
		"SELECT COUNT(filterFindEntry) FROM FilterFindEntry filterFindEntry WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"filterFindEntry.filterFindEntryId";

	private static final String _FILTER_SQL_SELECT_FILTERFINDENTRY_WHERE =
		"SELECT DISTINCT {filterFindEntry.*} FROM FilterFindEntry filterFindEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_FILTERFINDENTRY_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {FilterFindEntry.*} FROM (SELECT DISTINCT filterFindEntry.filterFindEntryId FROM FilterFindEntry filterFindEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_FILTERFINDENTRY_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN FilterFindEntry ON TEMP_TABLE.filterFindEntryId = FilterFindEntry.filterFindEntryId";

	private static final String _FILTER_SQL_COUNT_FILTERFINDENTRY_WHERE =
		"SELECT COUNT(DISTINCT filterFindEntry.filterFindEntryId) AS COUNT_VALUE FROM FilterFindEntry filterFindEntry WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "filterFindEntry";

	private static final String _FILTER_ENTITY_TABLE = "FilterFindEntry";

	private static final String _ORDER_BY_ENTITY_ALIAS = "filterFindEntry.";

	private static final String _ORDER_BY_ENTITY_TABLE = "FilterFindEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No FilterFindEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No FilterFindEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		FilterFindEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type", "integer"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
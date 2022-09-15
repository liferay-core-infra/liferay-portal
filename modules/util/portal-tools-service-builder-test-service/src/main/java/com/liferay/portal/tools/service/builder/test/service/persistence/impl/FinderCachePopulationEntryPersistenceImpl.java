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

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchFinderCachePopulationEntryException;
import com.liferay.portal.tools.service.builder.test.model.FinderCachePopulationEntry;
import com.liferay.portal.tools.service.builder.test.model.FinderCachePopulationEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.FinderCachePopulationEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.FinderCachePopulationEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.FinderCachePopulationEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.FinderCachePopulationEntryUtil;

import java.io.Serializable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The persistence implementation for the finder cache population entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class FinderCachePopulationEntryPersistenceImpl
	extends BasePersistenceImpl<FinderCachePopulationEntry>
	implements FinderCachePopulationEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>FinderCachePopulationEntryUtil</code> to access the finder cache population entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		FinderCachePopulationEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathFetchByUniqueName;
	private FinderPath _finderPathCountByUniqueName;

	/**
	 * Returns the finder cache population entry where uniqueName = &#63; or throws a <code>NoSuchFinderCachePopulationEntryException</code> if it could not be found.
	 *
	 * @param uniqueName the unique name
	 * @return the matching finder cache population entry
	 * @throws NoSuchFinderCachePopulationEntryException if a matching finder cache population entry could not be found
	 */
	@Override
	public FinderCachePopulationEntry findByUniqueName(String uniqueName)
		throws NoSuchFinderCachePopulationEntryException {

		FinderCachePopulationEntry finderCachePopulationEntry =
			fetchByUniqueName(uniqueName);

		if (finderCachePopulationEntry == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("uniqueName=");
			sb.append(uniqueName);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchFinderCachePopulationEntryException(sb.toString());
		}

		return finderCachePopulationEntry;
	}

	/**
	 * Returns the finder cache population entry where uniqueName = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uniqueName the unique name
	 * @return the matching finder cache population entry, or <code>null</code> if a matching finder cache population entry could not be found
	 */
	@Override
	public FinderCachePopulationEntry fetchByUniqueName(String uniqueName) {
		return fetchByUniqueName(uniqueName, true);
	}

	/**
	 * Returns the finder cache population entry where uniqueName = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uniqueName the unique name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching finder cache population entry, or <code>null</code> if a matching finder cache population entry could not be found
	 */
	@Override
	public FinderCachePopulationEntry fetchByUniqueName(
		String uniqueName, boolean useFinderCache) {

		uniqueName = Objects.toString(uniqueName, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {uniqueName};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByUniqueName, finderArgs);
		}

		if (result instanceof FinderCachePopulationEntry) {
			FinderCachePopulationEntry finderCachePopulationEntry =
				(FinderCachePopulationEntry)result;

			if (!Objects.equals(
					uniqueName, finderCachePopulationEntry.getUniqueName())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_SELECT_FINDERCACHEPOPULATIONENTRY_WHERE);

			boolean bindUniqueName = false;

			if (uniqueName.isEmpty()) {
				sb.append(_FINDER_COLUMN_UNIQUENAME_UNIQUENAME_3);
			}
			else {
				bindUniqueName = true;

				sb.append(_FINDER_COLUMN_UNIQUENAME_UNIQUENAME_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindUniqueName) {
					queryPos.add(uniqueName);
				}

				List<FinderCachePopulationEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByUniqueName, finderArgs, list);
					}
				}
				else {
					FinderCachePopulationEntry finderCachePopulationEntry =
						list.get(0);

					result = finderCachePopulationEntry;

					cacheResult(finderCachePopulationEntry);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		if (result instanceof List<?>) {
			return null;
		}
		else {
			return (FinderCachePopulationEntry)result;
		}
	}

	/**
	 * Removes the finder cache population entry where uniqueName = &#63; from the database.
	 *
	 * @param uniqueName the unique name
	 * @return the finder cache population entry that was removed
	 */
	@Override
	public FinderCachePopulationEntry removeByUniqueName(String uniqueName)
		throws NoSuchFinderCachePopulationEntryException {

		FinderCachePopulationEntry finderCachePopulationEntry =
			findByUniqueName(uniqueName);

		return remove(finderCachePopulationEntry);
	}

	/**
	 * Returns the number of finder cache population entries where uniqueName = &#63;.
	 *
	 * @param uniqueName the unique name
	 * @return the number of matching finder cache population entries
	 */
	@Override
	public int countByUniqueName(String uniqueName) {
		uniqueName = Objects.toString(uniqueName, "");

		FinderPath finderPath = _finderPathCountByUniqueName;

		Object[] finderArgs = new Object[] {uniqueName};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_FINDERCACHEPOPULATIONENTRY_WHERE);

			boolean bindUniqueName = false;

			if (uniqueName.isEmpty()) {
				sb.append(_FINDER_COLUMN_UNIQUENAME_UNIQUENAME_3);
			}
			else {
				bindUniqueName = true;

				sb.append(_FINDER_COLUMN_UNIQUENAME_UNIQUENAME_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindUniqueName) {
					queryPos.add(uniqueName);
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

	private static final String _FINDER_COLUMN_UNIQUENAME_UNIQUENAME_2 =
		"finderCachePopulationEntry.uniqueName = ?";

	private static final String _FINDER_COLUMN_UNIQUENAME_UNIQUENAME_3 =
		"(finderCachePopulationEntry.uniqueName IS NULL OR finderCachePopulationEntry.uniqueName = '')";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the finder cache population entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching finder cache population entries
	 */
	@Override
	public List<FinderCachePopulationEntry> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the finder cache population entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FinderCachePopulationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of finder cache population entries
	 * @param end the upper bound of the range of finder cache population entries (not inclusive)
	 * @return the range of matching finder cache population entries
	 */
	@Override
	public List<FinderCachePopulationEntry> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the finder cache population entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FinderCachePopulationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of finder cache population entries
	 * @param end the upper bound of the range of finder cache population entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching finder cache population entries
	 */
	@Override
	public List<FinderCachePopulationEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FinderCachePopulationEntry> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the finder cache population entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FinderCachePopulationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of finder cache population entries
	 * @param end the upper bound of the range of finder cache population entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching finder cache population entries
	 */
	@Override
	public List<FinderCachePopulationEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FinderCachePopulationEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByGroupId;
				finderArgs = new Object[] {groupId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByGroupId;
			finderArgs = new Object[] {groupId, start, end, orderByComparator};
		}

		List<FinderCachePopulationEntry> list = null;

		if (useFinderCache) {
			list = (List<FinderCachePopulationEntry>)finderCache.getResult(
				finderPath, finderArgs);

			if ((list != null) && !list.isEmpty()) {
				for (FinderCachePopulationEntry finderCachePopulationEntry :
						list) {

					if (groupId != finderCachePopulationEntry.getGroupId()) {
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

			sb.append(_SQL_SELECT_FINDERCACHEPOPULATIONENTRY_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(FinderCachePopulationEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				list = (List<FinderCachePopulationEntry>)QueryUtil.list(
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
	 * Returns the first finder cache population entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder cache population entry
	 * @throws NoSuchFinderCachePopulationEntryException if a matching finder cache population entry could not be found
	 */
	@Override
	public FinderCachePopulationEntry findByGroupId_First(
			long groupId,
			OrderByComparator<FinderCachePopulationEntry> orderByComparator)
		throws NoSuchFinderCachePopulationEntryException {

		FinderCachePopulationEntry finderCachePopulationEntry =
			fetchByGroupId_First(groupId, orderByComparator);

		if (finderCachePopulationEntry != null) {
			return finderCachePopulationEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchFinderCachePopulationEntryException(sb.toString());
	}

	/**
	 * Returns the first finder cache population entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder cache population entry, or <code>null</code> if a matching finder cache population entry could not be found
	 */
	@Override
	public FinderCachePopulationEntry fetchByGroupId_First(
		long groupId,
		OrderByComparator<FinderCachePopulationEntry> orderByComparator) {

		List<FinderCachePopulationEntry> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last finder cache population entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching finder cache population entry
	 * @throws NoSuchFinderCachePopulationEntryException if a matching finder cache population entry could not be found
	 */
	@Override
	public FinderCachePopulationEntry findByGroupId_Last(
			long groupId,
			OrderByComparator<FinderCachePopulationEntry> orderByComparator)
		throws NoSuchFinderCachePopulationEntryException {

		FinderCachePopulationEntry finderCachePopulationEntry =
			fetchByGroupId_Last(groupId, orderByComparator);

		if (finderCachePopulationEntry != null) {
			return finderCachePopulationEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchFinderCachePopulationEntryException(sb.toString());
	}

	/**
	 * Returns the last finder cache population entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching finder cache population entry, or <code>null</code> if a matching finder cache population entry could not be found
	 */
	@Override
	public FinderCachePopulationEntry fetchByGroupId_Last(
		long groupId,
		OrderByComparator<FinderCachePopulationEntry> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<FinderCachePopulationEntry> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the finder cache population entries before and after the current finder cache population entry in the ordered set where groupId = &#63;.
	 *
	 * @param pinderCachePopulationEntryId the primary key of the current finder cache population entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next finder cache population entry
	 * @throws NoSuchFinderCachePopulationEntryException if a finder cache population entry with the primary key could not be found
	 */
	@Override
	public FinderCachePopulationEntry[] findByGroupId_PrevAndNext(
			long pinderCachePopulationEntryId, long groupId,
			OrderByComparator<FinderCachePopulationEntry> orderByComparator)
		throws NoSuchFinderCachePopulationEntryException {

		FinderCachePopulationEntry finderCachePopulationEntry =
			findByPrimaryKey(pinderCachePopulationEntryId);

		Session session = null;

		try {
			session = openSession();

			FinderCachePopulationEntry[] array =
				new FinderCachePopulationEntryImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, finderCachePopulationEntry, groupId, orderByComparator,
				true);

			array[1] = finderCachePopulationEntry;

			array[2] = getByGroupId_PrevAndNext(
				session, finderCachePopulationEntry, groupId, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected FinderCachePopulationEntry getByGroupId_PrevAndNext(
		Session session, FinderCachePopulationEntry finderCachePopulationEntry,
		long groupId,
		OrderByComparator<FinderCachePopulationEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_FINDERCACHEPOPULATIONENTRY_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

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
			sb.append(FinderCachePopulationEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						finderCachePopulationEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<FinderCachePopulationEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the finder cache population entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (FinderCachePopulationEntry finderCachePopulationEntry :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(finderCachePopulationEntry);
		}
	}

	/**
	 * Returns the number of finder cache population entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching finder cache population entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		FinderPath finderPath = _finderPathCountByGroupId;

		Object[] finderArgs = new Object[] {groupId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_FINDERCACHEPOPULATIONENTRY_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

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

	private static final String _FINDER_COLUMN_GROUPID_GROUPID_2 =
		"finderCachePopulationEntry.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByC_G;
	private FinderPath _finderPathWithoutPaginationFindByC_G;
	private FinderPath _finderPathCountByC_G;

	/**
	 * Returns all the finder cache population entries where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @return the matching finder cache population entries
	 */
	@Override
	public List<FinderCachePopulationEntry> findByC_G(
		long companyId, long groupId) {

		return findByC_G(
			companyId, groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the finder cache population entries where companyId = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FinderCachePopulationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param start the lower bound of the range of finder cache population entries
	 * @param end the upper bound of the range of finder cache population entries (not inclusive)
	 * @return the range of matching finder cache population entries
	 */
	@Override
	public List<FinderCachePopulationEntry> findByC_G(
		long companyId, long groupId, int start, int end) {

		return findByC_G(companyId, groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the finder cache population entries where companyId = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FinderCachePopulationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param start the lower bound of the range of finder cache population entries
	 * @param end the upper bound of the range of finder cache population entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching finder cache population entries
	 */
	@Override
	public List<FinderCachePopulationEntry> findByC_G(
		long companyId, long groupId, int start, int end,
		OrderByComparator<FinderCachePopulationEntry> orderByComparator) {

		return findByC_G(
			companyId, groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the finder cache population entries where companyId = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FinderCachePopulationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param start the lower bound of the range of finder cache population entries
	 * @param end the upper bound of the range of finder cache population entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching finder cache population entries
	 */
	@Override
	public List<FinderCachePopulationEntry> findByC_G(
		long companyId, long groupId, int start, int end,
		OrderByComparator<FinderCachePopulationEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_G;
				finderArgs = new Object[] {companyId, groupId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_G;
			finderArgs = new Object[] {
				companyId, groupId, start, end, orderByComparator
			};
		}

		List<FinderCachePopulationEntry> list = null;

		if (useFinderCache) {
			list = (List<FinderCachePopulationEntry>)finderCache.getResult(
				finderPath, finderArgs);

			if ((list != null) && !list.isEmpty()) {
				for (FinderCachePopulationEntry finderCachePopulationEntry :
						list) {

					if ((companyId !=
							finderCachePopulationEntry.getCompanyId()) ||
						(groupId != finderCachePopulationEntry.getGroupId())) {

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
					4 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(4);
			}

			sb.append(_SQL_SELECT_FINDERCACHEPOPULATIONENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_G_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_G_GROUPID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(FinderCachePopulationEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(groupId);

				list = (List<FinderCachePopulationEntry>)QueryUtil.list(
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
	 * Returns the first finder cache population entry in the ordered set where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder cache population entry
	 * @throws NoSuchFinderCachePopulationEntryException if a matching finder cache population entry could not be found
	 */
	@Override
	public FinderCachePopulationEntry findByC_G_First(
			long companyId, long groupId,
			OrderByComparator<FinderCachePopulationEntry> orderByComparator)
		throws NoSuchFinderCachePopulationEntryException {

		FinderCachePopulationEntry finderCachePopulationEntry =
			fetchByC_G_First(companyId, groupId, orderByComparator);

		if (finderCachePopulationEntry != null) {
			return finderCachePopulationEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchFinderCachePopulationEntryException(sb.toString());
	}

	/**
	 * Returns the first finder cache population entry in the ordered set where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder cache population entry, or <code>null</code> if a matching finder cache population entry could not be found
	 */
	@Override
	public FinderCachePopulationEntry fetchByC_G_First(
		long companyId, long groupId,
		OrderByComparator<FinderCachePopulationEntry> orderByComparator) {

		List<FinderCachePopulationEntry> list = findByC_G(
			companyId, groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last finder cache population entry in the ordered set where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching finder cache population entry
	 * @throws NoSuchFinderCachePopulationEntryException if a matching finder cache population entry could not be found
	 */
	@Override
	public FinderCachePopulationEntry findByC_G_Last(
			long companyId, long groupId,
			OrderByComparator<FinderCachePopulationEntry> orderByComparator)
		throws NoSuchFinderCachePopulationEntryException {

		FinderCachePopulationEntry finderCachePopulationEntry = fetchByC_G_Last(
			companyId, groupId, orderByComparator);

		if (finderCachePopulationEntry != null) {
			return finderCachePopulationEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchFinderCachePopulationEntryException(sb.toString());
	}

	/**
	 * Returns the last finder cache population entry in the ordered set where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching finder cache population entry, or <code>null</code> if a matching finder cache population entry could not be found
	 */
	@Override
	public FinderCachePopulationEntry fetchByC_G_Last(
		long companyId, long groupId,
		OrderByComparator<FinderCachePopulationEntry> orderByComparator) {

		int count = countByC_G(companyId, groupId);

		if (count == 0) {
			return null;
		}

		List<FinderCachePopulationEntry> list = findByC_G(
			companyId, groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the finder cache population entries before and after the current finder cache population entry in the ordered set where companyId = &#63; and groupId = &#63;.
	 *
	 * @param pinderCachePopulationEntryId the primary key of the current finder cache population entry
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next finder cache population entry
	 * @throws NoSuchFinderCachePopulationEntryException if a finder cache population entry with the primary key could not be found
	 */
	@Override
	public FinderCachePopulationEntry[] findByC_G_PrevAndNext(
			long pinderCachePopulationEntryId, long companyId, long groupId,
			OrderByComparator<FinderCachePopulationEntry> orderByComparator)
		throws NoSuchFinderCachePopulationEntryException {

		FinderCachePopulationEntry finderCachePopulationEntry =
			findByPrimaryKey(pinderCachePopulationEntryId);

		Session session = null;

		try {
			session = openSession();

			FinderCachePopulationEntry[] array =
				new FinderCachePopulationEntryImpl[3];

			array[0] = getByC_G_PrevAndNext(
				session, finderCachePopulationEntry, companyId, groupId,
				orderByComparator, true);

			array[1] = finderCachePopulationEntry;

			array[2] = getByC_G_PrevAndNext(
				session, finderCachePopulationEntry, companyId, groupId,
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

	protected FinderCachePopulationEntry getByC_G_PrevAndNext(
		Session session, FinderCachePopulationEntry finderCachePopulationEntry,
		long companyId, long groupId,
		OrderByComparator<FinderCachePopulationEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_FINDERCACHEPOPULATIONENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_G_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_G_GROUPID_2);

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
			sb.append(FinderCachePopulationEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						finderCachePopulationEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<FinderCachePopulationEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the finder cache population entries where companyId = &#63; and groupId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 */
	@Override
	public void removeByC_G(long companyId, long groupId) {
		for (FinderCachePopulationEntry finderCachePopulationEntry :
				findByC_G(
					companyId, groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(finderCachePopulationEntry);
		}
	}

	/**
	 * Returns the number of finder cache population entries where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @return the number of matching finder cache population entries
	 */
	@Override
	public int countByC_G(long companyId, long groupId) {
		FinderPath finderPath = _finderPathCountByC_G;

		Object[] finderArgs = new Object[] {companyId, groupId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_FINDERCACHEPOPULATIONENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_G_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_G_GROUPID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(groupId);

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

	private static final String _FINDER_COLUMN_C_G_COMPANYID_2 =
		"finderCachePopulationEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_G_GROUPID_2 =
		"finderCachePopulationEntry.groupId = ?";

	public FinderCachePopulationEntryPersistenceImpl() {
		setModelClass(FinderCachePopulationEntry.class);

		setModelImplClass(FinderCachePopulationEntryImpl.class);
		setModelPKClass(long.class);

		setTable(FinderCachePopulationEntryTable.INSTANCE);
	}

	/**
	 * Caches the finder cache population entry in the entity cache if it is enabled.
	 *
	 * @param finderCachePopulationEntry the finder cache population entry
	 */
	@Override
	public void cacheResult(
		FinderCachePopulationEntry finderCachePopulationEntry) {

		entityCache.putResult(
			FinderCachePopulationEntryImpl.class,
			finderCachePopulationEntry.getPrimaryKey(),
			finderCachePopulationEntry);

		finderCache.putResult(
			_finderPathFetchByUniqueName,
			new Object[] {finderCachePopulationEntry.getUniqueName()},
			finderCachePopulationEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the finder cache population entries in the entity cache if it is enabled.
	 *
	 * @param finderCachePopulationEntries the finder cache population entries
	 */
	@Override
	public void cacheResult(
		List<FinderCachePopulationEntry> finderCachePopulationEntries) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (finderCachePopulationEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (FinderCachePopulationEntry finderCachePopulationEntry :
				finderCachePopulationEntries) {

			if (entityCache.getResult(
					FinderCachePopulationEntryImpl.class,
					finderCachePopulationEntry.getPrimaryKey()) == null) {

				cacheResult(finderCachePopulationEntry);
			}
		}
	}

	/**
	 * Clears the cache for all finder cache population entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(FinderCachePopulationEntryImpl.class);

		finderCache.clearCache(FinderCachePopulationEntryImpl.class);
	}

	/**
	 * Clears the cache for the finder cache population entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(
		FinderCachePopulationEntry finderCachePopulationEntry) {

		entityCache.removeResult(
			FinderCachePopulationEntryImpl.class, finderCachePopulationEntry);
	}

	@Override
	public void clearCache(
		List<FinderCachePopulationEntry> finderCachePopulationEntries) {

		for (FinderCachePopulationEntry finderCachePopulationEntry :
				finderCachePopulationEntries) {

			entityCache.removeResult(
				FinderCachePopulationEntryImpl.class,
				finderCachePopulationEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FinderCachePopulationEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				FinderCachePopulationEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		FinderCachePopulationEntryModelImpl
			finderCachePopulationEntryModelImpl) {

		Object[] args = new Object[] {
			finderCachePopulationEntryModelImpl.getUniqueName()
		};

		finderCache.putResult(
			_finderPathCountByUniqueName, args, Long.valueOf(1));
		finderCache.putResult(
			_finderPathFetchByUniqueName, args,
			finderCachePopulationEntryModelImpl);
	}

	/**
	 * Creates a new finder cache population entry with the primary key. Does not add the finder cache population entry to the database.
	 *
	 * @param pinderCachePopulationEntryId the primary key for the new finder cache population entry
	 * @return the new finder cache population entry
	 */
	@Override
	public FinderCachePopulationEntry create(
		long pinderCachePopulationEntryId) {

		FinderCachePopulationEntry finderCachePopulationEntry =
			new FinderCachePopulationEntryImpl();

		finderCachePopulationEntry.setNew(true);
		finderCachePopulationEntry.setPrimaryKey(pinderCachePopulationEntryId);

		finderCachePopulationEntry.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return finderCachePopulationEntry;
	}

	/**
	 * Removes the finder cache population entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param pinderCachePopulationEntryId the primary key of the finder cache population entry
	 * @return the finder cache population entry that was removed
	 * @throws NoSuchFinderCachePopulationEntryException if a finder cache population entry with the primary key could not be found
	 */
	@Override
	public FinderCachePopulationEntry remove(long pinderCachePopulationEntryId)
		throws NoSuchFinderCachePopulationEntryException {

		return remove((Serializable)pinderCachePopulationEntryId);
	}

	/**
	 * Removes the finder cache population entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the finder cache population entry
	 * @return the finder cache population entry that was removed
	 * @throws NoSuchFinderCachePopulationEntryException if a finder cache population entry with the primary key could not be found
	 */
	@Override
	public FinderCachePopulationEntry remove(Serializable primaryKey)
		throws NoSuchFinderCachePopulationEntryException {

		Session session = null;

		try {
			session = openSession();

			FinderCachePopulationEntry finderCachePopulationEntry =
				(FinderCachePopulationEntry)session.get(
					FinderCachePopulationEntryImpl.class, primaryKey);

			if (finderCachePopulationEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchFinderCachePopulationEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(finderCachePopulationEntry);
		}
		catch (NoSuchFinderCachePopulationEntryException
					noSuchEntityException) {

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
	protected FinderCachePopulationEntry removeImpl(
		FinderCachePopulationEntry finderCachePopulationEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(finderCachePopulationEntry)) {
				finderCachePopulationEntry =
					(FinderCachePopulationEntry)session.get(
						FinderCachePopulationEntryImpl.class,
						finderCachePopulationEntry.getPrimaryKeyObj());
			}

			if (finderCachePopulationEntry != null) {
				session.delete(finderCachePopulationEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (finderCachePopulationEntry != null) {
			clearCache(finderCachePopulationEntry);
		}

		return finderCachePopulationEntry;
	}

	@Override
	public FinderCachePopulationEntry updateImpl(
		FinderCachePopulationEntry finderCachePopulationEntry) {

		boolean isNew = finderCachePopulationEntry.isNew();

		if (!(finderCachePopulationEntry instanceof
				FinderCachePopulationEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(finderCachePopulationEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					finderCachePopulationEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in finderCachePopulationEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom FinderCachePopulationEntry implementation " +
					finderCachePopulationEntry.getClass());
		}

		FinderCachePopulationEntryModelImpl
			finderCachePopulationEntryModelImpl =
				(FinderCachePopulationEntryModelImpl)finderCachePopulationEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(finderCachePopulationEntry);
			}
			else {
				finderCachePopulationEntry =
					(FinderCachePopulationEntry)session.merge(
						finderCachePopulationEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			FinderCachePopulationEntryImpl.class,
			finderCachePopulationEntryModelImpl, false, true);

		cacheUniqueFindersCache(finderCachePopulationEntryModelImpl);

		if (isNew) {
			finderCachePopulationEntry.setNew(false);
		}

		finderCachePopulationEntry.resetOriginalValues();

		return finderCachePopulationEntry;
	}

	/**
	 * Returns the finder cache population entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the finder cache population entry
	 * @return the finder cache population entry
	 * @throws NoSuchFinderCachePopulationEntryException if a finder cache population entry with the primary key could not be found
	 */
	@Override
	public FinderCachePopulationEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchFinderCachePopulationEntryException {

		FinderCachePopulationEntry finderCachePopulationEntry =
			fetchByPrimaryKey(primaryKey);

		if (finderCachePopulationEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchFinderCachePopulationEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return finderCachePopulationEntry;
	}

	/**
	 * Returns the finder cache population entry with the primary key or throws a <code>NoSuchFinderCachePopulationEntryException</code> if it could not be found.
	 *
	 * @param pinderCachePopulationEntryId the primary key of the finder cache population entry
	 * @return the finder cache population entry
	 * @throws NoSuchFinderCachePopulationEntryException if a finder cache population entry with the primary key could not be found
	 */
	@Override
	public FinderCachePopulationEntry findByPrimaryKey(
			long pinderCachePopulationEntryId)
		throws NoSuchFinderCachePopulationEntryException {

		return findByPrimaryKey((Serializable)pinderCachePopulationEntryId);
	}

	/**
	 * Returns the finder cache population entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param pinderCachePopulationEntryId the primary key of the finder cache population entry
	 * @return the finder cache population entry, or <code>null</code> if a finder cache population entry with the primary key could not be found
	 */
	@Override
	public FinderCachePopulationEntry fetchByPrimaryKey(
		long pinderCachePopulationEntryId) {

		return fetchByPrimaryKey((Serializable)pinderCachePopulationEntryId);
	}

	/**
	 * Returns all the finder cache population entries.
	 *
	 * @return the finder cache population entries
	 */
	@Override
	public List<FinderCachePopulationEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the finder cache population entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FinderCachePopulationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of finder cache population entries
	 * @param end the upper bound of the range of finder cache population entries (not inclusive)
	 * @return the range of finder cache population entries
	 */
	@Override
	public List<FinderCachePopulationEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the finder cache population entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FinderCachePopulationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of finder cache population entries
	 * @param end the upper bound of the range of finder cache population entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of finder cache population entries
	 */
	@Override
	public List<FinderCachePopulationEntry> findAll(
		int start, int end,
		OrderByComparator<FinderCachePopulationEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the finder cache population entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FinderCachePopulationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of finder cache population entries
	 * @param end the upper bound of the range of finder cache population entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of finder cache population entries
	 */
	@Override
	public List<FinderCachePopulationEntry> findAll(
		int start, int end,
		OrderByComparator<FinderCachePopulationEntry> orderByComparator,
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

		List<FinderCachePopulationEntry> list = null;

		if (useFinderCache) {
			list = (List<FinderCachePopulationEntry>)finderCache.getResult(
				finderPath, finderArgs);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_FINDERCACHEPOPULATIONENTRY);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_FINDERCACHEPOPULATIONENTRY;

				sql = sql.concat(
					FinderCachePopulationEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<FinderCachePopulationEntry>)QueryUtil.list(
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
	 * Removes all the finder cache population entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (FinderCachePopulationEntry finderCachePopulationEntry :
				findAll()) {

			remove(finderCachePopulationEntry);
		}
	}

	/**
	 * Returns the number of finder cache population entries.
	 *
	 * @return the number of finder cache population entries
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(
					_SQL_COUNT_FINDERCACHEPOPULATIONENTRY);

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
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "pinderCachePopulationEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_FINDERCACHEPOPULATIONENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return FinderCachePopulationEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the finder cache population entry persistence.
	 */
	public void afterPropertiesSet() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = new FinderPath(
			this, FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll",
			new String[0], new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			this, FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll",
			new String[0], new String[0], true);

		_finderPathCountAll = new FinderPath(
			this, FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathFetchByUniqueName = new FinderPath(
			this, FINDER_CLASS_NAME_ENTITY, "fetchByUniqueName",
			new String[] {String.class.getName()}, new String[] {"uniqueName"},
			true);

		_finderPathCountByUniqueName = new FinderPath(
			this, FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByUniqueName", new String[] {String.class.getName()},
			new String[] {"uniqueName"}, false);

		_finderPathWithPaginationFindByGroupId = new FinderPath(
			this, FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId"}, true);

		_finderPathWithoutPaginationFindByGroupId = new FinderPath(
			this, FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			true);

		_finderPathCountByGroupId = new FinderPath(
			this, FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			false);

		_finderPathWithPaginationFindByC_G = new FinderPath(
			this, FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_G",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "groupId"}, true);

		_finderPathWithoutPaginationFindByC_G = new FinderPath(
			this, FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_G",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "groupId"}, true);

		_finderPathCountByC_G = new FinderPath(
			this, FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_G",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "groupId"}, false);

		_finderPaths.put(
			"finderPathWithPaginationFindAll",
			_finderPathWithPaginationFindAll);
		_finderPaths.put(
			"finderPathWithoutPaginationFindAll",
			_finderPathWithoutPaginationFindAll);
		_finderPaths.put("finderPathCountAll", _finderPathCountAll);

		_finderPaths.put(
			"finderPathFetchByUniqueName", _finderPathFetchByUniqueName);

		_finderPaths.put(
			"finderPathCountByUniqueName", _finderPathCountByUniqueName);

		_finderPaths.put(
			"finderPathWithPaginationFindByGroupId",
			_finderPathWithPaginationFindByGroupId);

		_finderPaths.put(
			"finderPathWithoutPaginationFindByGroupId",
			_finderPathWithoutPaginationFindByGroupId);

		_finderPaths.put("finderPathCountByGroupId", _finderPathCountByGroupId);

		_finderPaths.put(
			"finderPathWithPaginationFindByC_G",
			_finderPathWithPaginationFindByC_G);

		_finderPaths.put(
			"finderPathWithoutPaginationFindByC_G",
			_finderPathWithoutPaginationFindByC_G);

		_finderPaths.put("finderPathCountByC_G", _finderPathCountByC_G);

		_setFinderCachePopulationEntryUtilPersistence(this);
	}

	public void destroy() {
		_setFinderCachePopulationEntryUtilPersistence(null);

		entityCache.removeCache(FinderCachePopulationEntryImpl.class.getName());
	}

	@Override
	public Map<String, FinderPath> getFinderPaths() {
		return _finderPaths;
	}

	@Override
	public void populateFinderCache(FinderPath... finderPaths) {
		List<FinderCachePopulationEntry> finderCachePopulationEntrys =
			findAll();

		for (FinderPath finderPath : finderPaths) {
			Map<List<Object>, List<FinderCachePopulationEntry>> resultMap =
				new HashMap<>();

			for (FinderCachePopulationEntry finderCachePopulationEntry :
					finderCachePopulationEntrys) {

				List<Object> arguments = new ArrayList<>();

				for (String columnName : finderPath.getColumnNames()) {
					FinderCachePopulationEntryModelImpl
						finderCachePopulationEntryModelImpl =
							(FinderCachePopulationEntryModelImpl)
								finderCachePopulationEntry;

					arguments.add(
						finderCachePopulationEntryModelImpl.getColumnValue(
							columnName));
				}

				if (Objects.equals(
						finderPath.getCacheName(), FINDER_CLASS_NAME_ENTITY)) {

					finderCache.putResult(
						finderPath, arguments.toArray(),
						finderCachePopulationEntry);
				}
				else {
					List<FinderCachePopulationEntry> resultList =
						resultMap.computeIfAbsent(
							arguments, key -> new ArrayList<>());

					resultList.add(finderCachePopulationEntry);
				}
			}

			for (Map.Entry<List<Object>, List<FinderCachePopulationEntry>>
					resultEntry : resultMap.entrySet()) {

				List<Object> key = resultEntry.getKey();
				List<FinderCachePopulationEntry> value = resultEntry.getValue();

				if (finderPath.isBaseModelResult()) {
					finderCache.putResult(finderPath, key.toArray(), value);
				}
				else {
					finderCache.putResult(
						finderPath, key.toArray(), value.size());
				}
			}
		}
	}

	private Map<String, FinderPath> _finderPaths = new HashMap<>();

	private void _setFinderCachePopulationEntryUtilPersistence(
		FinderCachePopulationEntryPersistence
			finderCachePopulationEntryPersistence) {

		try {
			Field field = FinderCachePopulationEntryUtil.class.getDeclaredField(
				"_persistence");

			field.setAccessible(true);

			field.set(null, finderCachePopulationEntryPersistence);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new RuntimeException(reflectiveOperationException);
		}
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_FINDERCACHEPOPULATIONENTRY =
		"SELECT finderCachePopulationEntry FROM FinderCachePopulationEntry finderCachePopulationEntry";

	private static final String _SQL_SELECT_FINDERCACHEPOPULATIONENTRY_WHERE =
		"SELECT finderCachePopulationEntry FROM FinderCachePopulationEntry finderCachePopulationEntry WHERE ";

	private static final String _SQL_COUNT_FINDERCACHEPOPULATIONENTRY =
		"SELECT COUNT(finderCachePopulationEntry) FROM FinderCachePopulationEntry finderCachePopulationEntry";

	private static final String _SQL_COUNT_FINDERCACHEPOPULATIONENTRY_WHERE =
		"SELECT COUNT(finderCachePopulationEntry) FROM FinderCachePopulationEntry finderCachePopulationEntry WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"finderCachePopulationEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No FinderCachePopulationEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No FinderCachePopulationEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		FinderCachePopulationEntryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
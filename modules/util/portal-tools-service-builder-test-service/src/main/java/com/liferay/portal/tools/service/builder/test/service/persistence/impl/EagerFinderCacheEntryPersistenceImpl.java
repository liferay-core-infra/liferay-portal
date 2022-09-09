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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchEagerFinderCacheEntryException;
import com.liferay.portal.tools.service.builder.test.model.EagerFinderCacheEntry;
import com.liferay.portal.tools.service.builder.test.model.EagerFinderCacheEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.EagerFinderCacheEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.EagerFinderCacheEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.EagerFinderCacheEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.EagerFinderCacheEntryUtil;

import java.io.Serializable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The persistence implementation for the eager finder cache entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class EagerFinderCacheEntryPersistenceImpl
	extends BasePersistenceImpl<EagerFinderCacheEntry>
	implements EagerFinderCacheEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>EagerFinderCacheEntryUtil</code> to access the eager finder cache entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		EagerFinderCacheEntryImpl.class.getName();

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
	 * Returns the eager finder cache entry where uniqueName = &#63; or throws a <code>NoSuchEagerFinderCacheEntryException</code> if it could not be found.
	 *
	 * @param uniqueName the unique name
	 * @return the matching eager finder cache entry
	 * @throws NoSuchEagerFinderCacheEntryException if a matching eager finder cache entry could not be found
	 */
	@Override
	public EagerFinderCacheEntry findByUniqueName(String uniqueName)
		throws NoSuchEagerFinderCacheEntryException {

		EagerFinderCacheEntry eagerFinderCacheEntry = fetchByUniqueName(
			uniqueName);

		if (eagerFinderCacheEntry == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("uniqueName=");
			sb.append(uniqueName);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchEagerFinderCacheEntryException(sb.toString());
		}

		return eagerFinderCacheEntry;
	}

	/**
	 * Returns the eager finder cache entry where uniqueName = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uniqueName the unique name
	 * @return the matching eager finder cache entry, or <code>null</code> if a matching eager finder cache entry could not be found
	 */
	@Override
	public EagerFinderCacheEntry fetchByUniqueName(String uniqueName) {
		return fetchByUniqueName(uniqueName, true);
	}

	/**
	 * Returns the eager finder cache entry where uniqueName = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uniqueName the unique name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching eager finder cache entry, or <code>null</code> if a matching eager finder cache entry could not be found
	 */
	@Override
	public EagerFinderCacheEntry fetchByUniqueName(
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

		if (result instanceof EagerFinderCacheEntry) {
			EagerFinderCacheEntry eagerFinderCacheEntry =
				(EagerFinderCacheEntry)result;

			if (!Objects.equals(
					uniqueName, eagerFinderCacheEntry.getUniqueName())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_SELECT_EAGERFINDERCACHEENTRY_WHERE);

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

				List<EagerFinderCacheEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByUniqueName, finderArgs, list);
					}
				}
				else {
					EagerFinderCacheEntry eagerFinderCacheEntry = list.get(0);

					result = eagerFinderCacheEntry;

					cacheResult(eagerFinderCacheEntry);
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
			return (EagerFinderCacheEntry)result;
		}
	}

	/**
	 * Removes the eager finder cache entry where uniqueName = &#63; from the database.
	 *
	 * @param uniqueName the unique name
	 * @return the eager finder cache entry that was removed
	 */
	@Override
	public EagerFinderCacheEntry removeByUniqueName(String uniqueName)
		throws NoSuchEagerFinderCacheEntryException {

		EagerFinderCacheEntry eagerFinderCacheEntry = findByUniqueName(
			uniqueName);

		return remove(eagerFinderCacheEntry);
	}

	/**
	 * Returns the number of eager finder cache entries where uniqueName = &#63;.
	 *
	 * @param uniqueName the unique name
	 * @return the number of matching eager finder cache entries
	 */
	@Override
	public int countByUniqueName(String uniqueName) {
		uniqueName = Objects.toString(uniqueName, "");

		FinderPath finderPath = _finderPathCountByUniqueName;

		Object[] finderArgs = new Object[] {uniqueName};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_EAGERFINDERCACHEENTRY_WHERE);

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
		"eagerFinderCacheEntry.uniqueName = ?";

	private static final String _FINDER_COLUMN_UNIQUENAME_UNIQUENAME_3 =
		"(eagerFinderCacheEntry.uniqueName IS NULL OR eagerFinderCacheEntry.uniqueName = '')";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the eager finder cache entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching eager finder cache entries
	 */
	@Override
	public List<EagerFinderCacheEntry> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the eager finder cache entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>EagerFinderCacheEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of eager finder cache entries
	 * @param end the upper bound of the range of eager finder cache entries (not inclusive)
	 * @return the range of matching eager finder cache entries
	 */
	@Override
	public List<EagerFinderCacheEntry> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the eager finder cache entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>EagerFinderCacheEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of eager finder cache entries
	 * @param end the upper bound of the range of eager finder cache entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching eager finder cache entries
	 */
	@Override
	public List<EagerFinderCacheEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<EagerFinderCacheEntry> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the eager finder cache entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>EagerFinderCacheEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of eager finder cache entries
	 * @param end the upper bound of the range of eager finder cache entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching eager finder cache entries
	 */
	@Override
	public List<EagerFinderCacheEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<EagerFinderCacheEntry> orderByComparator,
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

		List<EagerFinderCacheEntry> list = null;

		if (useFinderCache) {
			list = (List<EagerFinderCacheEntry>)finderCache.getResult(
				finderPath, finderArgs);

			if ((list != null) && !list.isEmpty()) {
				for (EagerFinderCacheEntry eagerFinderCacheEntry : list) {
					if (groupId != eagerFinderCacheEntry.getGroupId()) {
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

			sb.append(_SQL_SELECT_EAGERFINDERCACHEENTRY_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(EagerFinderCacheEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				list = (List<EagerFinderCacheEntry>)QueryUtil.list(
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
	 * Returns the first eager finder cache entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching eager finder cache entry
	 * @throws NoSuchEagerFinderCacheEntryException if a matching eager finder cache entry could not be found
	 */
	@Override
	public EagerFinderCacheEntry findByGroupId_First(
			long groupId,
			OrderByComparator<EagerFinderCacheEntry> orderByComparator)
		throws NoSuchEagerFinderCacheEntryException {

		EagerFinderCacheEntry eagerFinderCacheEntry = fetchByGroupId_First(
			groupId, orderByComparator);

		if (eagerFinderCacheEntry != null) {
			return eagerFinderCacheEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchEagerFinderCacheEntryException(sb.toString());
	}

	/**
	 * Returns the first eager finder cache entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching eager finder cache entry, or <code>null</code> if a matching eager finder cache entry could not be found
	 */
	@Override
	public EagerFinderCacheEntry fetchByGroupId_First(
		long groupId,
		OrderByComparator<EagerFinderCacheEntry> orderByComparator) {

		List<EagerFinderCacheEntry> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last eager finder cache entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching eager finder cache entry
	 * @throws NoSuchEagerFinderCacheEntryException if a matching eager finder cache entry could not be found
	 */
	@Override
	public EagerFinderCacheEntry findByGroupId_Last(
			long groupId,
			OrderByComparator<EagerFinderCacheEntry> orderByComparator)
		throws NoSuchEagerFinderCacheEntryException {

		EagerFinderCacheEntry eagerFinderCacheEntry = fetchByGroupId_Last(
			groupId, orderByComparator);

		if (eagerFinderCacheEntry != null) {
			return eagerFinderCacheEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchEagerFinderCacheEntryException(sb.toString());
	}

	/**
	 * Returns the last eager finder cache entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching eager finder cache entry, or <code>null</code> if a matching eager finder cache entry could not be found
	 */
	@Override
	public EagerFinderCacheEntry fetchByGroupId_Last(
		long groupId,
		OrderByComparator<EagerFinderCacheEntry> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<EagerFinderCacheEntry> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the eager finder cache entries before and after the current eager finder cache entry in the ordered set where groupId = &#63;.
	 *
	 * @param eagerFinderCacheEntryId the primary key of the current eager finder cache entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next eager finder cache entry
	 * @throws NoSuchEagerFinderCacheEntryException if a eager finder cache entry with the primary key could not be found
	 */
	@Override
	public EagerFinderCacheEntry[] findByGroupId_PrevAndNext(
			long eagerFinderCacheEntryId, long groupId,
			OrderByComparator<EagerFinderCacheEntry> orderByComparator)
		throws NoSuchEagerFinderCacheEntryException {

		EagerFinderCacheEntry eagerFinderCacheEntry = findByPrimaryKey(
			eagerFinderCacheEntryId);

		Session session = null;

		try {
			session = openSession();

			EagerFinderCacheEntry[] array = new EagerFinderCacheEntryImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, eagerFinderCacheEntry, groupId, orderByComparator,
				true);

			array[1] = eagerFinderCacheEntry;

			array[2] = getByGroupId_PrevAndNext(
				session, eagerFinderCacheEntry, groupId, orderByComparator,
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

	protected EagerFinderCacheEntry getByGroupId_PrevAndNext(
		Session session, EagerFinderCacheEntry eagerFinderCacheEntry,
		long groupId,
		OrderByComparator<EagerFinderCacheEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_EAGERFINDERCACHEENTRY_WHERE);

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
			sb.append(EagerFinderCacheEntryModelImpl.ORDER_BY_JPQL);
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
						eagerFinderCacheEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<EagerFinderCacheEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the eager finder cache entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (EagerFinderCacheEntry eagerFinderCacheEntry :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(eagerFinderCacheEntry);
		}
	}

	/**
	 * Returns the number of eager finder cache entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching eager finder cache entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		FinderPath finderPath = _finderPathCountByGroupId;

		Object[] finderArgs = new Object[] {groupId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_EAGERFINDERCACHEENTRY_WHERE);

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
		"eagerFinderCacheEntry.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByC_G;
	private FinderPath _finderPathWithoutPaginationFindByC_G;
	private FinderPath _finderPathCountByC_G;

	/**
	 * Returns all the eager finder cache entries where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @return the matching eager finder cache entries
	 */
	@Override
	public List<EagerFinderCacheEntry> findByC_G(long companyId, long groupId) {
		return findByC_G(
			companyId, groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the eager finder cache entries where companyId = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>EagerFinderCacheEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param start the lower bound of the range of eager finder cache entries
	 * @param end the upper bound of the range of eager finder cache entries (not inclusive)
	 * @return the range of matching eager finder cache entries
	 */
	@Override
	public List<EagerFinderCacheEntry> findByC_G(
		long companyId, long groupId, int start, int end) {

		return findByC_G(companyId, groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the eager finder cache entries where companyId = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>EagerFinderCacheEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param start the lower bound of the range of eager finder cache entries
	 * @param end the upper bound of the range of eager finder cache entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching eager finder cache entries
	 */
	@Override
	public List<EagerFinderCacheEntry> findByC_G(
		long companyId, long groupId, int start, int end,
		OrderByComparator<EagerFinderCacheEntry> orderByComparator) {

		return findByC_G(
			companyId, groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the eager finder cache entries where companyId = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>EagerFinderCacheEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param start the lower bound of the range of eager finder cache entries
	 * @param end the upper bound of the range of eager finder cache entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching eager finder cache entries
	 */
	@Override
	public List<EagerFinderCacheEntry> findByC_G(
		long companyId, long groupId, int start, int end,
		OrderByComparator<EagerFinderCacheEntry> orderByComparator,
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

		List<EagerFinderCacheEntry> list = null;

		if (useFinderCache) {
			list = (List<EagerFinderCacheEntry>)finderCache.getResult(
				finderPath, finderArgs);

			if ((list != null) && !list.isEmpty()) {
				for (EagerFinderCacheEntry eagerFinderCacheEntry : list) {
					if ((companyId != eagerFinderCacheEntry.getCompanyId()) ||
						(groupId != eagerFinderCacheEntry.getGroupId())) {

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

			sb.append(_SQL_SELECT_EAGERFINDERCACHEENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_G_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_G_GROUPID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(EagerFinderCacheEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(groupId);

				list = (List<EagerFinderCacheEntry>)QueryUtil.list(
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
	 * Returns the first eager finder cache entry in the ordered set where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching eager finder cache entry
	 * @throws NoSuchEagerFinderCacheEntryException if a matching eager finder cache entry could not be found
	 */
	@Override
	public EagerFinderCacheEntry findByC_G_First(
			long companyId, long groupId,
			OrderByComparator<EagerFinderCacheEntry> orderByComparator)
		throws NoSuchEagerFinderCacheEntryException {

		EagerFinderCacheEntry eagerFinderCacheEntry = fetchByC_G_First(
			companyId, groupId, orderByComparator);

		if (eagerFinderCacheEntry != null) {
			return eagerFinderCacheEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchEagerFinderCacheEntryException(sb.toString());
	}

	/**
	 * Returns the first eager finder cache entry in the ordered set where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching eager finder cache entry, or <code>null</code> if a matching eager finder cache entry could not be found
	 */
	@Override
	public EagerFinderCacheEntry fetchByC_G_First(
		long companyId, long groupId,
		OrderByComparator<EagerFinderCacheEntry> orderByComparator) {

		List<EagerFinderCacheEntry> list = findByC_G(
			companyId, groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last eager finder cache entry in the ordered set where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching eager finder cache entry
	 * @throws NoSuchEagerFinderCacheEntryException if a matching eager finder cache entry could not be found
	 */
	@Override
	public EagerFinderCacheEntry findByC_G_Last(
			long companyId, long groupId,
			OrderByComparator<EagerFinderCacheEntry> orderByComparator)
		throws NoSuchEagerFinderCacheEntryException {

		EagerFinderCacheEntry eagerFinderCacheEntry = fetchByC_G_Last(
			companyId, groupId, orderByComparator);

		if (eagerFinderCacheEntry != null) {
			return eagerFinderCacheEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchEagerFinderCacheEntryException(sb.toString());
	}

	/**
	 * Returns the last eager finder cache entry in the ordered set where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching eager finder cache entry, or <code>null</code> if a matching eager finder cache entry could not be found
	 */
	@Override
	public EagerFinderCacheEntry fetchByC_G_Last(
		long companyId, long groupId,
		OrderByComparator<EagerFinderCacheEntry> orderByComparator) {

		int count = countByC_G(companyId, groupId);

		if (count == 0) {
			return null;
		}

		List<EagerFinderCacheEntry> list = findByC_G(
			companyId, groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the eager finder cache entries before and after the current eager finder cache entry in the ordered set where companyId = &#63; and groupId = &#63;.
	 *
	 * @param eagerFinderCacheEntryId the primary key of the current eager finder cache entry
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next eager finder cache entry
	 * @throws NoSuchEagerFinderCacheEntryException if a eager finder cache entry with the primary key could not be found
	 */
	@Override
	public EagerFinderCacheEntry[] findByC_G_PrevAndNext(
			long eagerFinderCacheEntryId, long companyId, long groupId,
			OrderByComparator<EagerFinderCacheEntry> orderByComparator)
		throws NoSuchEagerFinderCacheEntryException {

		EagerFinderCacheEntry eagerFinderCacheEntry = findByPrimaryKey(
			eagerFinderCacheEntryId);

		Session session = null;

		try {
			session = openSession();

			EagerFinderCacheEntry[] array = new EagerFinderCacheEntryImpl[3];

			array[0] = getByC_G_PrevAndNext(
				session, eagerFinderCacheEntry, companyId, groupId,
				orderByComparator, true);

			array[1] = eagerFinderCacheEntry;

			array[2] = getByC_G_PrevAndNext(
				session, eagerFinderCacheEntry, companyId, groupId,
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

	protected EagerFinderCacheEntry getByC_G_PrevAndNext(
		Session session, EagerFinderCacheEntry eagerFinderCacheEntry,
		long companyId, long groupId,
		OrderByComparator<EagerFinderCacheEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_EAGERFINDERCACHEENTRY_WHERE);

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
			sb.append(EagerFinderCacheEntryModelImpl.ORDER_BY_JPQL);
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
						eagerFinderCacheEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<EagerFinderCacheEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the eager finder cache entries where companyId = &#63; and groupId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 */
	@Override
	public void removeByC_G(long companyId, long groupId) {
		for (EagerFinderCacheEntry eagerFinderCacheEntry :
				findByC_G(
					companyId, groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(eagerFinderCacheEntry);
		}
	}

	/**
	 * Returns the number of eager finder cache entries where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @return the number of matching eager finder cache entries
	 */
	@Override
	public int countByC_G(long companyId, long groupId) {
		FinderPath finderPath = _finderPathCountByC_G;

		Object[] finderArgs = new Object[] {companyId, groupId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_EAGERFINDERCACHEENTRY_WHERE);

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
		"eagerFinderCacheEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_G_GROUPID_2 =
		"eagerFinderCacheEntry.groupId = ?";

	public EagerFinderCacheEntryPersistenceImpl() {
		setModelClass(EagerFinderCacheEntry.class);

		setModelImplClass(EagerFinderCacheEntryImpl.class);
		setModelPKClass(long.class);

		setTable(EagerFinderCacheEntryTable.INSTANCE);
	}

	/**
	 * Caches the eager finder cache entry in the entity cache if it is enabled.
	 *
	 * @param eagerFinderCacheEntry the eager finder cache entry
	 */
	@Override
	public void cacheResult(EagerFinderCacheEntry eagerFinderCacheEntry) {
		entityCache.putResult(
			EagerFinderCacheEntryImpl.class,
			eagerFinderCacheEntry.getPrimaryKey(), eagerFinderCacheEntry);

		finderCache.putResult(
			_finderPathFetchByUniqueName,
			new Object[] {eagerFinderCacheEntry.getUniqueName()},
			eagerFinderCacheEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the eager finder cache entries in the entity cache if it is enabled.
	 *
	 * @param eagerFinderCacheEntries the eager finder cache entries
	 */
	@Override
	public void cacheResult(
		List<EagerFinderCacheEntry> eagerFinderCacheEntries) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (eagerFinderCacheEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (EagerFinderCacheEntry eagerFinderCacheEntry :
				eagerFinderCacheEntries) {

			if (entityCache.getResult(
					EagerFinderCacheEntryImpl.class,
					eagerFinderCacheEntry.getPrimaryKey()) == null) {

				cacheResult(eagerFinderCacheEntry);
			}
		}
	}

	/**
	 * Clears the cache for all eager finder cache entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(EagerFinderCacheEntryImpl.class);

		finderCache.clearCache(EagerFinderCacheEntryImpl.class);
	}

	/**
	 * Clears the cache for the eager finder cache entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(EagerFinderCacheEntry eagerFinderCacheEntry) {
		entityCache.removeResult(
			EagerFinderCacheEntryImpl.class, eagerFinderCacheEntry);
	}

	@Override
	public void clearCache(
		List<EagerFinderCacheEntry> eagerFinderCacheEntries) {

		for (EagerFinderCacheEntry eagerFinderCacheEntry :
				eagerFinderCacheEntries) {

			entityCache.removeResult(
				EagerFinderCacheEntryImpl.class, eagerFinderCacheEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(EagerFinderCacheEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				EagerFinderCacheEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		EagerFinderCacheEntryModelImpl eagerFinderCacheEntryModelImpl) {

		Object[] args = new Object[] {
			eagerFinderCacheEntryModelImpl.getUniqueName()
		};

		finderCache.putResult(
			_finderPathCountByUniqueName, args, Long.valueOf(1));
		finderCache.putResult(
			_finderPathFetchByUniqueName, args, eagerFinderCacheEntryModelImpl);
	}

	/**
	 * Creates a new eager finder cache entry with the primary key. Does not add the eager finder cache entry to the database.
	 *
	 * @param eagerFinderCacheEntryId the primary key for the new eager finder cache entry
	 * @return the new eager finder cache entry
	 */
	@Override
	public EagerFinderCacheEntry create(long eagerFinderCacheEntryId) {
		EagerFinderCacheEntry eagerFinderCacheEntry =
			new EagerFinderCacheEntryImpl();

		eagerFinderCacheEntry.setNew(true);
		eagerFinderCacheEntry.setPrimaryKey(eagerFinderCacheEntryId);

		eagerFinderCacheEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return eagerFinderCacheEntry;
	}

	/**
	 * Removes the eager finder cache entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param eagerFinderCacheEntryId the primary key of the eager finder cache entry
	 * @return the eager finder cache entry that was removed
	 * @throws NoSuchEagerFinderCacheEntryException if a eager finder cache entry with the primary key could not be found
	 */
	@Override
	public EagerFinderCacheEntry remove(long eagerFinderCacheEntryId)
		throws NoSuchEagerFinderCacheEntryException {

		return remove((Serializable)eagerFinderCacheEntryId);
	}

	/**
	 * Removes the eager finder cache entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the eager finder cache entry
	 * @return the eager finder cache entry that was removed
	 * @throws NoSuchEagerFinderCacheEntryException if a eager finder cache entry with the primary key could not be found
	 */
	@Override
	public EagerFinderCacheEntry remove(Serializable primaryKey)
		throws NoSuchEagerFinderCacheEntryException {

		Session session = null;

		try {
			session = openSession();

			EagerFinderCacheEntry eagerFinderCacheEntry =
				(EagerFinderCacheEntry)session.get(
					EagerFinderCacheEntryImpl.class, primaryKey);

			if (eagerFinderCacheEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchEagerFinderCacheEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(eagerFinderCacheEntry);
		}
		catch (NoSuchEagerFinderCacheEntryException noSuchEntityException) {
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
	protected EagerFinderCacheEntry removeImpl(
		EagerFinderCacheEntry eagerFinderCacheEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(eagerFinderCacheEntry)) {
				eagerFinderCacheEntry = (EagerFinderCacheEntry)session.get(
					EagerFinderCacheEntryImpl.class,
					eagerFinderCacheEntry.getPrimaryKeyObj());
			}

			if (eagerFinderCacheEntry != null) {
				session.delete(eagerFinderCacheEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (eagerFinderCacheEntry != null) {
			clearCache(eagerFinderCacheEntry);
		}

		return eagerFinderCacheEntry;
	}

	@Override
	public EagerFinderCacheEntry updateImpl(
		EagerFinderCacheEntry eagerFinderCacheEntry) {

		boolean isNew = eagerFinderCacheEntry.isNew();

		if (!(eagerFinderCacheEntry instanceof
				EagerFinderCacheEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(eagerFinderCacheEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					eagerFinderCacheEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in eagerFinderCacheEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom EagerFinderCacheEntry implementation " +
					eagerFinderCacheEntry.getClass());
		}

		EagerFinderCacheEntryModelImpl eagerFinderCacheEntryModelImpl =
			(EagerFinderCacheEntryModelImpl)eagerFinderCacheEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(eagerFinderCacheEntry);
			}
			else {
				eagerFinderCacheEntry = (EagerFinderCacheEntry)session.merge(
					eagerFinderCacheEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			EagerFinderCacheEntryImpl.class, eagerFinderCacheEntryModelImpl,
			false, true);

		cacheUniqueFindersCache(eagerFinderCacheEntryModelImpl);

		if (isNew) {
			eagerFinderCacheEntry.setNew(false);
		}

		eagerFinderCacheEntry.resetOriginalValues();

		return eagerFinderCacheEntry;
	}

	/**
	 * Returns the eager finder cache entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the eager finder cache entry
	 * @return the eager finder cache entry
	 * @throws NoSuchEagerFinderCacheEntryException if a eager finder cache entry with the primary key could not be found
	 */
	@Override
	public EagerFinderCacheEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchEagerFinderCacheEntryException {

		EagerFinderCacheEntry eagerFinderCacheEntry = fetchByPrimaryKey(
			primaryKey);

		if (eagerFinderCacheEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchEagerFinderCacheEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return eagerFinderCacheEntry;
	}

	/**
	 * Returns the eager finder cache entry with the primary key or throws a <code>NoSuchEagerFinderCacheEntryException</code> if it could not be found.
	 *
	 * @param eagerFinderCacheEntryId the primary key of the eager finder cache entry
	 * @return the eager finder cache entry
	 * @throws NoSuchEagerFinderCacheEntryException if a eager finder cache entry with the primary key could not be found
	 */
	@Override
	public EagerFinderCacheEntry findByPrimaryKey(long eagerFinderCacheEntryId)
		throws NoSuchEagerFinderCacheEntryException {

		return findByPrimaryKey((Serializable)eagerFinderCacheEntryId);
	}

	/**
	 * Returns the eager finder cache entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param eagerFinderCacheEntryId the primary key of the eager finder cache entry
	 * @return the eager finder cache entry, or <code>null</code> if a eager finder cache entry with the primary key could not be found
	 */
	@Override
	public EagerFinderCacheEntry fetchByPrimaryKey(
		long eagerFinderCacheEntryId) {

		return fetchByPrimaryKey((Serializable)eagerFinderCacheEntryId);
	}

	/**
	 * Returns all the eager finder cache entries.
	 *
	 * @return the eager finder cache entries
	 */
	@Override
	public List<EagerFinderCacheEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the eager finder cache entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>EagerFinderCacheEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of eager finder cache entries
	 * @param end the upper bound of the range of eager finder cache entries (not inclusive)
	 * @return the range of eager finder cache entries
	 */
	@Override
	public List<EagerFinderCacheEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the eager finder cache entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>EagerFinderCacheEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of eager finder cache entries
	 * @param end the upper bound of the range of eager finder cache entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of eager finder cache entries
	 */
	@Override
	public List<EagerFinderCacheEntry> findAll(
		int start, int end,
		OrderByComparator<EagerFinderCacheEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the eager finder cache entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>EagerFinderCacheEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of eager finder cache entries
	 * @param end the upper bound of the range of eager finder cache entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of eager finder cache entries
	 */
	@Override
	public List<EagerFinderCacheEntry> findAll(
		int start, int end,
		OrderByComparator<EagerFinderCacheEntry> orderByComparator,
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

		List<EagerFinderCacheEntry> list = null;

		if (useFinderCache) {
			list = (List<EagerFinderCacheEntry>)finderCache.getResult(
				finderPath, finderArgs);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_EAGERFINDERCACHEENTRY);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_EAGERFINDERCACHEENTRY;

				sql = sql.concat(EagerFinderCacheEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<EagerFinderCacheEntry>)QueryUtil.list(
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
	 * Removes all the eager finder cache entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (EagerFinderCacheEntry eagerFinderCacheEntry : findAll()) {
			remove(eagerFinderCacheEntry);
		}
	}

	/**
	 * Returns the number of eager finder cache entries.
	 *
	 * @return the number of eager finder cache entries
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
					_SQL_COUNT_EAGERFINDERCACHEENTRY);

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
		return "eagerFinderCacheEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_EAGERFINDERCACHEENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return EagerFinderCacheEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the eager finder cache entry persistence.
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

		_setEagerFinderCacheEntryUtilPersistence(this);

		_eagerCacheFinderPaths.put("UniqueName", _finderPathFetchByUniqueName);
		_eagerCacheFinderPaths.put(
			"GroupId", _finderPathWithoutPaginationFindByGroupId);
		_eagerCacheFinderPaths.put(
			"C_G", _finderPathWithoutPaginationFindByC_G);
	}

	protected Map<String, FinderPath> getEagerCacheFinderPaths() {
		return _eagerCacheFinderPaths;
	}

	private final Map<String, FinderPath> _eagerCacheFinderPaths =
		new HashMap<>();

	public void destroy() {
		_setEagerFinderCacheEntryUtilPersistence(null);

		entityCache.removeCache(EagerFinderCacheEntryImpl.class.getName());
	}

	private void _setEagerFinderCacheEntryUtilPersistence(
		EagerFinderCacheEntryPersistence eagerFinderCacheEntryPersistence) {

		try {
			Field field = EagerFinderCacheEntryUtil.class.getDeclaredField(
				"_persistence");

			field.setAccessible(true);

			field.set(null, eagerFinderCacheEntryPersistence);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new RuntimeException(reflectiveOperationException);
		}
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_EAGERFINDERCACHEENTRY =
		"SELECT eagerFinderCacheEntry FROM EagerFinderCacheEntry eagerFinderCacheEntry";

	private static final String _SQL_SELECT_EAGERFINDERCACHEENTRY_WHERE =
		"SELECT eagerFinderCacheEntry FROM EagerFinderCacheEntry eagerFinderCacheEntry WHERE ";

	private static final String _SQL_COUNT_EAGERFINDERCACHEENTRY =
		"SELECT COUNT(eagerFinderCacheEntry) FROM EagerFinderCacheEntry eagerFinderCacheEntry";

	private static final String _SQL_COUNT_EAGERFINDERCACHEENTRY_WHERE =
		"SELECT COUNT(eagerFinderCacheEntry) FROM EagerFinderCacheEntry eagerFinderCacheEntry WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"eagerFinderCacheEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No EagerFinderCacheEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No EagerFinderCacheEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		EagerFinderCacheEntryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
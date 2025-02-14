/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
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
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchSortedFinderEntryException;
import com.liferay.portal.tools.service.builder.test.model.SortedFinderEntry;
import com.liferay.portal.tools.service.builder.test.model.SortedFinderEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.SortedFinderEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.SortedFinderEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.SortedFinderEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.SortedFinderEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the sorted finder entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class SortedFinderEntryPersistenceImpl
	extends BasePersistenceImpl<SortedFinderEntry>
	implements SortedFinderEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SortedFinderEntryUtil</code> to access the sorted finder entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SortedFinderEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathFetchByGroupId;

	/**
	 * Returns the sorted finder entry where groupId = &#63; or throws a <code>NoSuchSortedFinderEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @return the matching sorted finder entry
	 * @throws NoSuchSortedFinderEntryException if a matching sorted finder entry could not be found
	 */
	@Override
	public SortedFinderEntry findByGroupId(long groupId)
		throws NoSuchSortedFinderEntryException {

		SortedFinderEntry sortedFinderEntry = fetchByGroupId(groupId);

		if (sortedFinderEntry == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchSortedFinderEntryException(sb.toString());
		}

		return sortedFinderEntry;
	}

	/**
	 * Returns the sorted finder entry where groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @return the matching sorted finder entry, or <code>null</code> if a matching sorted finder entry could not be found
	 */
	@Override
	public SortedFinderEntry fetchByGroupId(long groupId) {
		return fetchByGroupId(groupId, true);
	}

	/**
	 * Returns the sorted finder entry where groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching sorted finder entry, or <code>null</code> if a matching sorted finder entry could not be found
	 */
	@Override
	public SortedFinderEntry fetchByGroupId(
		long groupId, boolean useFinderCache) {

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {groupId};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByGroupId, finderArgs, this);
		}

		if (result instanceof SortedFinderEntry) {
			SortedFinderEntry sortedFinderEntry = (SortedFinderEntry)result;

			if (groupId != sortedFinderEntry.getGroupId()) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_SELECT_SORTEDFINDERENTRY_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				List<SortedFinderEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByGroupId, finderArgs, list);
					}
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							if (!useFinderCache) {
								finderArgs = new Object[] {groupId};
							}

							_log.warn(
								"SortedFinderEntryPersistenceImpl.fetchByGroupId(long, boolean) with parameters (" +
									StringUtil.merge(finderArgs) +
										") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					SortedFinderEntry sortedFinderEntry = list.get(0);

					result = sortedFinderEntry;

					cacheResult(sortedFinderEntry);
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
			return (SortedFinderEntry)result;
		}
	}

	/**
	 * Removes the sorted finder entry where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @return the sorted finder entry that was removed
	 */
	@Override
	public SortedFinderEntry removeByGroupId(long groupId)
		throws NoSuchSortedFinderEntryException {

		SortedFinderEntry sortedFinderEntry = findByGroupId(groupId);

		return remove(sortedFinderEntry);
	}

	/**
	 * Returns the number of sorted finder entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching sorted finder entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		SortedFinderEntry sortedFinderEntry = fetchByGroupId(groupId);

		if (sortedFinderEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_GROUPID_GROUPID_2 =
		"sortedFinderEntry.groupId = ?";

	public SortedFinderEntryPersistenceImpl() {
		setModelClass(SortedFinderEntry.class);

		setModelImplClass(SortedFinderEntryImpl.class);
		setModelPKClass(long.class);

		setTable(SortedFinderEntryTable.INSTANCE);
	}

	/**
	 * Caches the sorted finder entry in the entity cache if it is enabled.
	 *
	 * @param sortedFinderEntry the sorted finder entry
	 */
	@Override
	public void cacheResult(SortedFinderEntry sortedFinderEntry) {
		entityCache.putResult(
			SortedFinderEntryImpl.class, sortedFinderEntry.getPrimaryKey(),
			sortedFinderEntry);

		finderCache.putResult(
			_finderPathFetchByGroupId,
			new Object[] {sortedFinderEntry.getGroupId()}, sortedFinderEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the sorted finder entries in the entity cache if it is enabled.
	 *
	 * @param sortedFinderEntries the sorted finder entries
	 */
	@Override
	public void cacheResult(List<SortedFinderEntry> sortedFinderEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (sortedFinderEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (SortedFinderEntry sortedFinderEntry : sortedFinderEntries) {
			if (entityCache.getResult(
					SortedFinderEntryImpl.class,
					sortedFinderEntry.getPrimaryKey()) == null) {

				cacheResult(sortedFinderEntry);
			}
		}
	}

	/**
	 * Clears the cache for all sorted finder entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(SortedFinderEntryImpl.class);

		finderCache.clearCache(SortedFinderEntryImpl.class);
	}

	/**
	 * Clears the cache for the sorted finder entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(SortedFinderEntry sortedFinderEntry) {
		entityCache.removeResult(
			SortedFinderEntryImpl.class, sortedFinderEntry);
	}

	@Override
	public void clearCache(List<SortedFinderEntry> sortedFinderEntries) {
		for (SortedFinderEntry sortedFinderEntry : sortedFinderEntries) {
			entityCache.removeResult(
				SortedFinderEntryImpl.class, sortedFinderEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(SortedFinderEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(SortedFinderEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		SortedFinderEntryModelImpl sortedFinderEntryModelImpl) {

		Object[] args = new Object[] {sortedFinderEntryModelImpl.getGroupId()};

		finderCache.putResult(
			_finderPathFetchByGroupId, args, sortedFinderEntryModelImpl);
	}

	/**
	 * Creates a new sorted finder entry with the primary key. Does not add the sorted finder entry to the database.
	 *
	 * @param sortedFinderEntryId the primary key for the new sorted finder entry
	 * @return the new sorted finder entry
	 */
	@Override
	public SortedFinderEntry create(long sortedFinderEntryId) {
		SortedFinderEntry sortedFinderEntry = new SortedFinderEntryImpl();

		sortedFinderEntry.setNew(true);
		sortedFinderEntry.setPrimaryKey(sortedFinderEntryId);

		return sortedFinderEntry;
	}

	/**
	 * Removes the sorted finder entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param sortedFinderEntryId the primary key of the sorted finder entry
	 * @return the sorted finder entry that was removed
	 * @throws NoSuchSortedFinderEntryException if a sorted finder entry with the primary key could not be found
	 */
	@Override
	public SortedFinderEntry remove(long sortedFinderEntryId)
		throws NoSuchSortedFinderEntryException {

		return remove((Serializable)sortedFinderEntryId);
	}

	/**
	 * Removes the sorted finder entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the sorted finder entry
	 * @return the sorted finder entry that was removed
	 * @throws NoSuchSortedFinderEntryException if a sorted finder entry with the primary key could not be found
	 */
	@Override
	public SortedFinderEntry remove(Serializable primaryKey)
		throws NoSuchSortedFinderEntryException {

		Session session = null;

		try {
			session = openSession();

			SortedFinderEntry sortedFinderEntry =
				(SortedFinderEntry)session.get(
					SortedFinderEntryImpl.class, primaryKey);

			if (sortedFinderEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchSortedFinderEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(sortedFinderEntry);
		}
		catch (NoSuchSortedFinderEntryException noSuchEntityException) {
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
	protected SortedFinderEntry removeImpl(
		SortedFinderEntry sortedFinderEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(sortedFinderEntry)) {
				sortedFinderEntry = (SortedFinderEntry)session.get(
					SortedFinderEntryImpl.class,
					sortedFinderEntry.getPrimaryKeyObj());
			}

			if (sortedFinderEntry != null) {
				session.delete(sortedFinderEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (sortedFinderEntry != null) {
			clearCache(sortedFinderEntry);
		}

		return sortedFinderEntry;
	}

	@Override
	public SortedFinderEntry updateImpl(SortedFinderEntry sortedFinderEntry) {
		boolean isNew = sortedFinderEntry.isNew();

		if (!(sortedFinderEntry instanceof SortedFinderEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(sortedFinderEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					sortedFinderEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in sortedFinderEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SortedFinderEntry implementation " +
					sortedFinderEntry.getClass());
		}

		SortedFinderEntryModelImpl sortedFinderEntryModelImpl =
			(SortedFinderEntryModelImpl)sortedFinderEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(sortedFinderEntry);
			}
			else {
				sortedFinderEntry = (SortedFinderEntry)session.merge(
					sortedFinderEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			SortedFinderEntryImpl.class, sortedFinderEntryModelImpl, false,
			true);

		cacheUniqueFindersCache(sortedFinderEntryModelImpl);

		if (isNew) {
			sortedFinderEntry.setNew(false);
		}

		sortedFinderEntry.resetOriginalValues();

		return sortedFinderEntry;
	}

	/**
	 * Returns the sorted finder entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the sorted finder entry
	 * @return the sorted finder entry
	 * @throws NoSuchSortedFinderEntryException if a sorted finder entry with the primary key could not be found
	 */
	@Override
	public SortedFinderEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchSortedFinderEntryException {

		SortedFinderEntry sortedFinderEntry = fetchByPrimaryKey(primaryKey);

		if (sortedFinderEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchSortedFinderEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return sortedFinderEntry;
	}

	/**
	 * Returns the sorted finder entry with the primary key or throws a <code>NoSuchSortedFinderEntryException</code> if it could not be found.
	 *
	 * @param sortedFinderEntryId the primary key of the sorted finder entry
	 * @return the sorted finder entry
	 * @throws NoSuchSortedFinderEntryException if a sorted finder entry with the primary key could not be found
	 */
	@Override
	public SortedFinderEntry findByPrimaryKey(long sortedFinderEntryId)
		throws NoSuchSortedFinderEntryException {

		return findByPrimaryKey((Serializable)sortedFinderEntryId);
	}

	/**
	 * Returns the sorted finder entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param sortedFinderEntryId the primary key of the sorted finder entry
	 * @return the sorted finder entry, or <code>null</code> if a sorted finder entry with the primary key could not be found
	 */
	@Override
	public SortedFinderEntry fetchByPrimaryKey(long sortedFinderEntryId) {
		return fetchByPrimaryKey((Serializable)sortedFinderEntryId);
	}

	/**
	 * Returns all the sorted finder entries.
	 *
	 * @return the sorted finder entries
	 */
	@Override
	public List<SortedFinderEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the sorted finder entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SortedFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of sorted finder entries
	 * @param end the upper bound of the range of sorted finder entries (not inclusive)
	 * @return the range of sorted finder entries
	 */
	@Override
	public List<SortedFinderEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the sorted finder entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SortedFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of sorted finder entries
	 * @param end the upper bound of the range of sorted finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of sorted finder entries
	 */
	@Override
	public List<SortedFinderEntry> findAll(
		int start, int end,
		OrderByComparator<SortedFinderEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the sorted finder entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SortedFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of sorted finder entries
	 * @param end the upper bound of the range of sorted finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of sorted finder entries
	 */
	@Override
	public List<SortedFinderEntry> findAll(
		int start, int end,
		OrderByComparator<SortedFinderEntry> orderByComparator,
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

		List<SortedFinderEntry> list = null;

		if (useFinderCache) {
			list = (List<SortedFinderEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_SORTEDFINDERENTRY);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_SORTEDFINDERENTRY;

				sql = sql.concat(SortedFinderEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<SortedFinderEntry>)QueryUtil.list(
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
	 * Removes all the sorted finder entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (SortedFinderEntry sortedFinderEntry : findAll()) {
			remove(sortedFinderEntry);
		}
	}

	/**
	 * Returns the number of sorted finder entries.
	 *
	 * @return the number of sorted finder entries
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_SORTEDFINDERENTRY);

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
		return "sortedFinderEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SORTEDFINDERENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return SortedFinderEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the sorted finder entry persistence.
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

		_finderPathFetchByGroupId = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			true);

		SortedFinderEntryUtil.setPersistence(this);
	}

	public void destroy() {
		SortedFinderEntryUtil.setPersistence(null);

		entityCache.removeCache(SortedFinderEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_SORTEDFINDERENTRY =
		"SELECT sortedFinderEntry FROM SortedFinderEntry sortedFinderEntry";

	private static final String _SQL_SELECT_SORTEDFINDERENTRY_WHERE =
		"SELECT sortedFinderEntry FROM SortedFinderEntry sortedFinderEntry WHERE ";

	private static final String _SQL_COUNT_SORTEDFINDERENTRY =
		"SELECT COUNT(sortedFinderEntry) FROM SortedFinderEntry sortedFinderEntry";

	private static final String _SQL_COUNT_SORTEDFINDERENTRY_WHERE =
		"SELECT COUNT(sortedFinderEntry) FROM SortedFinderEntry sortedFinderEntry WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "sortedFinderEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No SortedFinderEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SortedFinderEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		SortedFinderEntryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
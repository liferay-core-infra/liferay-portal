/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.SortedFinderEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the sorted finder entry service. This utility wraps <code>com.liferay.portal.tools.service.builder.test.service.persistence.impl.SortedFinderEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see SortedFinderEntryPersistence
 * @generated
 */
public class SortedFinderEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(SortedFinderEntry sortedFinderEntry) {
		getPersistence().clearCache(sortedFinderEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, SortedFinderEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<SortedFinderEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<SortedFinderEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<SortedFinderEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<SortedFinderEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static SortedFinderEntry update(
		SortedFinderEntry sortedFinderEntry) {

		return getPersistence().update(sortedFinderEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static SortedFinderEntry update(
		SortedFinderEntry sortedFinderEntry, ServiceContext serviceContext) {

		return getPersistence().update(sortedFinderEntry, serviceContext);
	}

	/**
	 * Returns all the sorted finder entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching sorted finder entries
	 */
	public static List<SortedFinderEntry> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the sorted finder entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SortedFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of sorted finder entries
	 * @param end the upper bound of the range of sorted finder entries (not inclusive)
	 * @return the range of matching sorted finder entries
	 */
	public static List<SortedFinderEntry> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the sorted finder entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SortedFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of sorted finder entries
	 * @param end the upper bound of the range of sorted finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching sorted finder entries
	 */
	public static List<SortedFinderEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SortedFinderEntry> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the sorted finder entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SortedFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of sorted finder entries
	 * @param end the upper bound of the range of sorted finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching sorted finder entries
	 */
	public static List<SortedFinderEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SortedFinderEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first sorted finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sorted finder entry
	 * @throws NoSuchSortedFinderEntryException if a matching sorted finder entry could not be found
	 */
	public static SortedFinderEntry findByGroupId_First(
			long groupId,
			OrderByComparator<SortedFinderEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchSortedFinderEntryException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first sorted finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sorted finder entry, or <code>null</code> if a matching sorted finder entry could not be found
	 */
	public static SortedFinderEntry fetchByGroupId_First(
		long groupId, OrderByComparator<SortedFinderEntry> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns the last sorted finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching sorted finder entry
	 * @throws NoSuchSortedFinderEntryException if a matching sorted finder entry could not be found
	 */
	public static SortedFinderEntry findByGroupId_Last(
			long groupId,
			OrderByComparator<SortedFinderEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchSortedFinderEntryException {

		return getPersistence().findByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the last sorted finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching sorted finder entry, or <code>null</code> if a matching sorted finder entry could not be found
	 */
	public static SortedFinderEntry fetchByGroupId_Last(
		long groupId, OrderByComparator<SortedFinderEntry> orderByComparator) {

		return getPersistence().fetchByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the sorted finder entries before and after the current sorted finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param sortedFinderEntryId the primary key of the current sorted finder entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next sorted finder entry
	 * @throws NoSuchSortedFinderEntryException if a sorted finder entry with the primary key could not be found
	 */
	public static SortedFinderEntry[] findByGroupId_PrevAndNext(
			long sortedFinderEntryId, long groupId,
			OrderByComparator<SortedFinderEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchSortedFinderEntryException {

		return getPersistence().findByGroupId_PrevAndNext(
			sortedFinderEntryId, groupId, orderByComparator);
	}

	/**
	 * Removes all the sorted finder entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of sorted finder entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching sorted finder entries
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Caches the sorted finder entry in the entity cache if it is enabled.
	 *
	 * @param sortedFinderEntry the sorted finder entry
	 */
	public static void cacheResult(SortedFinderEntry sortedFinderEntry) {
		getPersistence().cacheResult(sortedFinderEntry);
	}

	/**
	 * Caches the sorted finder entries in the entity cache if it is enabled.
	 *
	 * @param sortedFinderEntries the sorted finder entries
	 */
	public static void cacheResult(
		List<SortedFinderEntry> sortedFinderEntries) {

		getPersistence().cacheResult(sortedFinderEntries);
	}

	/**
	 * Creates a new sorted finder entry with the primary key. Does not add the sorted finder entry to the database.
	 *
	 * @param sortedFinderEntryId the primary key for the new sorted finder entry
	 * @return the new sorted finder entry
	 */
	public static SortedFinderEntry create(long sortedFinderEntryId) {
		return getPersistence().create(sortedFinderEntryId);
	}

	/**
	 * Removes the sorted finder entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param sortedFinderEntryId the primary key of the sorted finder entry
	 * @return the sorted finder entry that was removed
	 * @throws NoSuchSortedFinderEntryException if a sorted finder entry with the primary key could not be found
	 */
	public static SortedFinderEntry remove(long sortedFinderEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchSortedFinderEntryException {

		return getPersistence().remove(sortedFinderEntryId);
	}

	public static SortedFinderEntry updateImpl(
		SortedFinderEntry sortedFinderEntry) {

		return getPersistence().updateImpl(sortedFinderEntry);
	}

	/**
	 * Returns the sorted finder entry with the primary key or throws a <code>NoSuchSortedFinderEntryException</code> if it could not be found.
	 *
	 * @param sortedFinderEntryId the primary key of the sorted finder entry
	 * @return the sorted finder entry
	 * @throws NoSuchSortedFinderEntryException if a sorted finder entry with the primary key could not be found
	 */
	public static SortedFinderEntry findByPrimaryKey(long sortedFinderEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchSortedFinderEntryException {

		return getPersistence().findByPrimaryKey(sortedFinderEntryId);
	}

	/**
	 * Returns the sorted finder entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param sortedFinderEntryId the primary key of the sorted finder entry
	 * @return the sorted finder entry, or <code>null</code> if a sorted finder entry with the primary key could not be found
	 */
	public static SortedFinderEntry fetchByPrimaryKey(
		long sortedFinderEntryId) {

		return getPersistence().fetchByPrimaryKey(sortedFinderEntryId);
	}

	/**
	 * Returns all the sorted finder entries.
	 *
	 * @return the sorted finder entries
	 */
	public static List<SortedFinderEntry> findAll() {
		return getPersistence().findAll();
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
	public static List<SortedFinderEntry> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
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
	public static List<SortedFinderEntry> findAll(
		int start, int end,
		OrderByComparator<SortedFinderEntry> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
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
	public static List<SortedFinderEntry> findAll(
		int start, int end,
		OrderByComparator<SortedFinderEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the sorted finder entries from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of sorted finder entries.
	 *
	 * @return the number of sorted finder entries
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static SortedFinderEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		SortedFinderEntryPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile SortedFinderEntryPersistence _persistence;

}
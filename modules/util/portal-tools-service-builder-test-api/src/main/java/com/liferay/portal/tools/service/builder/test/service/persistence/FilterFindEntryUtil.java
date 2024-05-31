/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.FilterFindEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the filter find entry service. This utility wraps <code>com.liferay.portal.tools.service.builder.test.service.persistence.impl.FilterFindEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see FilterFindEntryPersistence
 * @generated
 */
public class FilterFindEntryUtil {

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
	public static void clearCache(FilterFindEntry filterFindEntry) {
		getPersistence().clearCache(filterFindEntry);
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
	public static Map<Serializable, FilterFindEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<FilterFindEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<FilterFindEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<FilterFindEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<FilterFindEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static FilterFindEntry update(FilterFindEntry filterFindEntry) {
		return getPersistence().update(filterFindEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static FilterFindEntry update(
		FilterFindEntry filterFindEntry, ServiceContext serviceContext) {

		return getPersistence().update(filterFindEntry, serviceContext);
	}

	/**
	 * Returns all the filter find entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching filter find entries
	 */
	public static List<FilterFindEntry> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the filter find entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @return the range of matching filter find entries
	 */
	public static List<FilterFindEntry> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the filter find entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching filter find entries
	 */
	public static List<FilterFindEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FilterFindEntry> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the filter find entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching filter find entries
	 */
	public static List<FilterFindEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FilterFindEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first filter find entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching filter find entry
	 * @throws NoSuchFilterFindEntryException if a matching filter find entry could not be found
	 */
	public static FilterFindEntry findByGroupId_First(
			long groupId, OrderByComparator<FilterFindEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFilterFindEntryException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first filter find entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching filter find entry, or <code>null</code> if a matching filter find entry could not be found
	 */
	public static FilterFindEntry fetchByGroupId_First(
		long groupId, OrderByComparator<FilterFindEntry> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns the last filter find entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching filter find entry
	 * @throws NoSuchFilterFindEntryException if a matching filter find entry could not be found
	 */
	public static FilterFindEntry findByGroupId_Last(
			long groupId, OrderByComparator<FilterFindEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFilterFindEntryException {

		return getPersistence().findByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the last filter find entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching filter find entry, or <code>null</code> if a matching filter find entry could not be found
	 */
	public static FilterFindEntry fetchByGroupId_Last(
		long groupId, OrderByComparator<FilterFindEntry> orderByComparator) {

		return getPersistence().fetchByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the filter find entries before and after the current filter find entry in the ordered set where groupId = &#63;.
	 *
	 * @param filterFindEntryId the primary key of the current filter find entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next filter find entry
	 * @throws NoSuchFilterFindEntryException if a filter find entry with the primary key could not be found
	 */
	public static FilterFindEntry[] findByGroupId_PrevAndNext(
			long filterFindEntryId, long groupId,
			OrderByComparator<FilterFindEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFilterFindEntryException {

		return getPersistence().findByGroupId_PrevAndNext(
			filterFindEntryId, groupId, orderByComparator);
	}

	/**
	 * Returns all the filter find entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching filter find entries that the user has permission to view
	 */
	public static List<FilterFindEntry> filterFindByGroupId(long groupId) {
		return getPersistence().filterFindByGroupId(groupId);
	}

	/**
	 * Returns a range of all the filter find entries that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @return the range of matching filter find entries that the user has permission to view
	 */
	public static List<FilterFindEntry> filterFindByGroupId(
		long groupId, int start, int end) {

		return getPersistence().filterFindByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the filter find entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching filter find entries that the user has permission to view
	 */
	public static List<FilterFindEntry> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FilterFindEntry> orderByComparator) {

		return getPersistence().filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns the filter find entries before and after the current filter find entry in the ordered set of filter find entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param filterFindEntryId the primary key of the current filter find entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next filter find entry
	 * @throws NoSuchFilterFindEntryException if a filter find entry with the primary key could not be found
	 */
	public static FilterFindEntry[] filterFindByGroupId_PrevAndNext(
			long filterFindEntryId, long groupId,
			OrderByComparator<FilterFindEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFilterFindEntryException {

		return getPersistence().filterFindByGroupId_PrevAndNext(
			filterFindEntryId, groupId, orderByComparator);
	}

	/**
	 * Removes all the filter find entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of filter find entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching filter find entries
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns the number of filter find entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching filter find entries that the user has permission to view
	 */
	public static int filterCountByGroupId(long groupId) {
		return getPersistence().filterCountByGroupId(groupId);
	}

	/**
	 * Caches the filter find entry in the entity cache if it is enabled.
	 *
	 * @param filterFindEntry the filter find entry
	 */
	public static void cacheResult(FilterFindEntry filterFindEntry) {
		getPersistence().cacheResult(filterFindEntry);
	}

	/**
	 * Caches the filter find entries in the entity cache if it is enabled.
	 *
	 * @param filterFindEntries the filter find entries
	 */
	public static void cacheResult(List<FilterFindEntry> filterFindEntries) {
		getPersistence().cacheResult(filterFindEntries);
	}

	/**
	 * Creates a new filter find entry with the primary key. Does not add the filter find entry to the database.
	 *
	 * @param filterFindEntryId the primary key for the new filter find entry
	 * @return the new filter find entry
	 */
	public static FilterFindEntry create(long filterFindEntryId) {
		return getPersistence().create(filterFindEntryId);
	}

	/**
	 * Removes the filter find entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param filterFindEntryId the primary key of the filter find entry
	 * @return the filter find entry that was removed
	 * @throws NoSuchFilterFindEntryException if a filter find entry with the primary key could not be found
	 */
	public static FilterFindEntry remove(long filterFindEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFilterFindEntryException {

		return getPersistence().remove(filterFindEntryId);
	}

	public static FilterFindEntry updateImpl(FilterFindEntry filterFindEntry) {
		return getPersistence().updateImpl(filterFindEntry);
	}

	/**
	 * Returns the filter find entry with the primary key or throws a <code>NoSuchFilterFindEntryException</code> if it could not be found.
	 *
	 * @param filterFindEntryId the primary key of the filter find entry
	 * @return the filter find entry
	 * @throws NoSuchFilterFindEntryException if a filter find entry with the primary key could not be found
	 */
	public static FilterFindEntry findByPrimaryKey(long filterFindEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFilterFindEntryException {

		return getPersistence().findByPrimaryKey(filterFindEntryId);
	}

	/**
	 * Returns the filter find entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param filterFindEntryId the primary key of the filter find entry
	 * @return the filter find entry, or <code>null</code> if a filter find entry with the primary key could not be found
	 */
	public static FilterFindEntry fetchByPrimaryKey(long filterFindEntryId) {
		return getPersistence().fetchByPrimaryKey(filterFindEntryId);
	}

	/**
	 * Returns all the filter find entries.
	 *
	 * @return the filter find entries
	 */
	public static List<FilterFindEntry> findAll() {
		return getPersistence().findAll();
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
	public static List<FilterFindEntry> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
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
	public static List<FilterFindEntry> findAll(
		int start, int end,
		OrderByComparator<FilterFindEntry> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
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
	public static List<FilterFindEntry> findAll(
		int start, int end,
		OrderByComparator<FilterFindEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the filter find entries from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of filter find entries.
	 *
	 * @return the number of filter find entries
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static FilterFindEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(FilterFindEntryPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile FilterFindEntryPersistence _persistence;

}
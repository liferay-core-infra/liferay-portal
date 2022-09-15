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

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.FinderCachePopulationEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the finder cache population entry service. This utility wraps <code>com.liferay.portal.tools.service.builder.test.service.persistence.impl.FinderCachePopulationEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see FinderCachePopulationEntryPersistence
 * @generated
 */
public class FinderCachePopulationEntryUtil {

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
	public static void clearCache(
		FinderCachePopulationEntry finderCachePopulationEntry) {

		getPersistence().clearCache(finderCachePopulationEntry);
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
	public static Map<Serializable, FinderCachePopulationEntry>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<FinderCachePopulationEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<FinderCachePopulationEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<FinderCachePopulationEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<FinderCachePopulationEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static FinderCachePopulationEntry update(
		FinderCachePopulationEntry finderCachePopulationEntry) {

		return getPersistence().update(finderCachePopulationEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static FinderCachePopulationEntry update(
		FinderCachePopulationEntry finderCachePopulationEntry,
		ServiceContext serviceContext) {

		return getPersistence().update(
			finderCachePopulationEntry, serviceContext);
	}

	/**
	 * Returns the finder cache population entry where uniqueName = &#63; or throws a <code>NoSuchFinderCachePopulationEntryException</code> if it could not be found.
	 *
	 * @param uniqueName the unique name
	 * @return the matching finder cache population entry
	 * @throws NoSuchFinderCachePopulationEntryException if a matching finder cache population entry could not be found
	 */
	public static FinderCachePopulationEntry findByUniqueName(String uniqueName)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFinderCachePopulationEntryException {

		return getPersistence().findByUniqueName(uniqueName);
	}

	/**
	 * Returns the finder cache population entry where uniqueName = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uniqueName the unique name
	 * @return the matching finder cache population entry, or <code>null</code> if a matching finder cache population entry could not be found
	 */
	public static FinderCachePopulationEntry fetchByUniqueName(
		String uniqueName) {

		return getPersistence().fetchByUniqueName(uniqueName);
	}

	/**
	 * Returns the finder cache population entry where uniqueName = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uniqueName the unique name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching finder cache population entry, or <code>null</code> if a matching finder cache population entry could not be found
	 */
	public static FinderCachePopulationEntry fetchByUniqueName(
		String uniqueName, boolean useFinderCache) {

		return getPersistence().fetchByUniqueName(uniqueName, useFinderCache);
	}

	/**
	 * Removes the finder cache population entry where uniqueName = &#63; from the database.
	 *
	 * @param uniqueName the unique name
	 * @return the finder cache population entry that was removed
	 */
	public static FinderCachePopulationEntry removeByUniqueName(
			String uniqueName)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFinderCachePopulationEntryException {

		return getPersistence().removeByUniqueName(uniqueName);
	}

	/**
	 * Returns the number of finder cache population entries where uniqueName = &#63;.
	 *
	 * @param uniqueName the unique name
	 * @return the number of matching finder cache population entries
	 */
	public static int countByUniqueName(String uniqueName) {
		return getPersistence().countByUniqueName(uniqueName);
	}

	/**
	 * Returns all the finder cache population entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching finder cache population entries
	 */
	public static List<FinderCachePopulationEntry> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
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
	public static List<FinderCachePopulationEntry> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
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
	public static List<FinderCachePopulationEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FinderCachePopulationEntry> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
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
	public static List<FinderCachePopulationEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FinderCachePopulationEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first finder cache population entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder cache population entry
	 * @throws NoSuchFinderCachePopulationEntryException if a matching finder cache population entry could not be found
	 */
	public static FinderCachePopulationEntry findByGroupId_First(
			long groupId,
			OrderByComparator<FinderCachePopulationEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFinderCachePopulationEntryException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first finder cache population entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder cache population entry, or <code>null</code> if a matching finder cache population entry could not be found
	 */
	public static FinderCachePopulationEntry fetchByGroupId_First(
		long groupId,
		OrderByComparator<FinderCachePopulationEntry> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns the last finder cache population entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching finder cache population entry
	 * @throws NoSuchFinderCachePopulationEntryException if a matching finder cache population entry could not be found
	 */
	public static FinderCachePopulationEntry findByGroupId_Last(
			long groupId,
			OrderByComparator<FinderCachePopulationEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFinderCachePopulationEntryException {

		return getPersistence().findByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the last finder cache population entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching finder cache population entry, or <code>null</code> if a matching finder cache population entry could not be found
	 */
	public static FinderCachePopulationEntry fetchByGroupId_Last(
		long groupId,
		OrderByComparator<FinderCachePopulationEntry> orderByComparator) {

		return getPersistence().fetchByGroupId_Last(groupId, orderByComparator);
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
	public static FinderCachePopulationEntry[] findByGroupId_PrevAndNext(
			long pinderCachePopulationEntryId, long groupId,
			OrderByComparator<FinderCachePopulationEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFinderCachePopulationEntryException {

		return getPersistence().findByGroupId_PrevAndNext(
			pinderCachePopulationEntryId, groupId, orderByComparator);
	}

	/**
	 * Removes all the finder cache population entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of finder cache population entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching finder cache population entries
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns all the finder cache population entries where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @return the matching finder cache population entries
	 */
	public static List<FinderCachePopulationEntry> findByC_G(
		long companyId, long groupId) {

		return getPersistence().findByC_G(companyId, groupId);
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
	public static List<FinderCachePopulationEntry> findByC_G(
		long companyId, long groupId, int start, int end) {

		return getPersistence().findByC_G(companyId, groupId, start, end);
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
	public static List<FinderCachePopulationEntry> findByC_G(
		long companyId, long groupId, int start, int end,
		OrderByComparator<FinderCachePopulationEntry> orderByComparator) {

		return getPersistence().findByC_G(
			companyId, groupId, start, end, orderByComparator);
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
	public static List<FinderCachePopulationEntry> findByC_G(
		long companyId, long groupId, int start, int end,
		OrderByComparator<FinderCachePopulationEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_G(
			companyId, groupId, start, end, orderByComparator, useFinderCache);
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
	public static FinderCachePopulationEntry findByC_G_First(
			long companyId, long groupId,
			OrderByComparator<FinderCachePopulationEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFinderCachePopulationEntryException {

		return getPersistence().findByC_G_First(
			companyId, groupId, orderByComparator);
	}

	/**
	 * Returns the first finder cache population entry in the ordered set where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder cache population entry, or <code>null</code> if a matching finder cache population entry could not be found
	 */
	public static FinderCachePopulationEntry fetchByC_G_First(
		long companyId, long groupId,
		OrderByComparator<FinderCachePopulationEntry> orderByComparator) {

		return getPersistence().fetchByC_G_First(
			companyId, groupId, orderByComparator);
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
	public static FinderCachePopulationEntry findByC_G_Last(
			long companyId, long groupId,
			OrderByComparator<FinderCachePopulationEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFinderCachePopulationEntryException {

		return getPersistence().findByC_G_Last(
			companyId, groupId, orderByComparator);
	}

	/**
	 * Returns the last finder cache population entry in the ordered set where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching finder cache population entry, or <code>null</code> if a matching finder cache population entry could not be found
	 */
	public static FinderCachePopulationEntry fetchByC_G_Last(
		long companyId, long groupId,
		OrderByComparator<FinderCachePopulationEntry> orderByComparator) {

		return getPersistence().fetchByC_G_Last(
			companyId, groupId, orderByComparator);
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
	public static FinderCachePopulationEntry[] findByC_G_PrevAndNext(
			long pinderCachePopulationEntryId, long companyId, long groupId,
			OrderByComparator<FinderCachePopulationEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFinderCachePopulationEntryException {

		return getPersistence().findByC_G_PrevAndNext(
			pinderCachePopulationEntryId, companyId, groupId,
			orderByComparator);
	}

	/**
	 * Removes all the finder cache population entries where companyId = &#63; and groupId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 */
	public static void removeByC_G(long companyId, long groupId) {
		getPersistence().removeByC_G(companyId, groupId);
	}

	/**
	 * Returns the number of finder cache population entries where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @return the number of matching finder cache population entries
	 */
	public static int countByC_G(long companyId, long groupId) {
		return getPersistence().countByC_G(companyId, groupId);
	}

	/**
	 * Caches the finder cache population entry in the entity cache if it is enabled.
	 *
	 * @param finderCachePopulationEntry the finder cache population entry
	 */
	public static void cacheResult(
		FinderCachePopulationEntry finderCachePopulationEntry) {

		getPersistence().cacheResult(finderCachePopulationEntry);
	}

	/**
	 * Caches the finder cache population entries in the entity cache if it is enabled.
	 *
	 * @param finderCachePopulationEntries the finder cache population entries
	 */
	public static void cacheResult(
		List<FinderCachePopulationEntry> finderCachePopulationEntries) {

		getPersistence().cacheResult(finderCachePopulationEntries);
	}

	/**
	 * Creates a new finder cache population entry with the primary key. Does not add the finder cache population entry to the database.
	 *
	 * @param pinderCachePopulationEntryId the primary key for the new finder cache population entry
	 * @return the new finder cache population entry
	 */
	public static FinderCachePopulationEntry create(
		long pinderCachePopulationEntryId) {

		return getPersistence().create(pinderCachePopulationEntryId);
	}

	/**
	 * Removes the finder cache population entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param pinderCachePopulationEntryId the primary key of the finder cache population entry
	 * @return the finder cache population entry that was removed
	 * @throws NoSuchFinderCachePopulationEntryException if a finder cache population entry with the primary key could not be found
	 */
	public static FinderCachePopulationEntry remove(
			long pinderCachePopulationEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFinderCachePopulationEntryException {

		return getPersistence().remove(pinderCachePopulationEntryId);
	}

	public static FinderCachePopulationEntry updateImpl(
		FinderCachePopulationEntry finderCachePopulationEntry) {

		return getPersistence().updateImpl(finderCachePopulationEntry);
	}

	/**
	 * Returns the finder cache population entry with the primary key or throws a <code>NoSuchFinderCachePopulationEntryException</code> if it could not be found.
	 *
	 * @param pinderCachePopulationEntryId the primary key of the finder cache population entry
	 * @return the finder cache population entry
	 * @throws NoSuchFinderCachePopulationEntryException if a finder cache population entry with the primary key could not be found
	 */
	public static FinderCachePopulationEntry findByPrimaryKey(
			long pinderCachePopulationEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFinderCachePopulationEntryException {

		return getPersistence().findByPrimaryKey(pinderCachePopulationEntryId);
	}

	/**
	 * Returns the finder cache population entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param pinderCachePopulationEntryId the primary key of the finder cache population entry
	 * @return the finder cache population entry, or <code>null</code> if a finder cache population entry with the primary key could not be found
	 */
	public static FinderCachePopulationEntry fetchByPrimaryKey(
		long pinderCachePopulationEntryId) {

		return getPersistence().fetchByPrimaryKey(pinderCachePopulationEntryId);
	}

	/**
	 * Returns all the finder cache population entries.
	 *
	 * @return the finder cache population entries
	 */
	public static List<FinderCachePopulationEntry> findAll() {
		return getPersistence().findAll();
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
	public static List<FinderCachePopulationEntry> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
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
	public static List<FinderCachePopulationEntry> findAll(
		int start, int end,
		OrderByComparator<FinderCachePopulationEntry> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
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
	public static List<FinderCachePopulationEntry> findAll(
		int start, int end,
		OrderByComparator<FinderCachePopulationEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the finder cache population entries from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of finder cache population entries.
	 *
	 * @return the number of finder cache population entries
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static Map<String, com.liferay.portal.kernel.dao.orm.FinderPath>
		getFinderPaths() {

		return getPersistence().getFinderPaths();
	}

	public static void populateFinderCache(
		com.liferay.portal.kernel.dao.orm.FinderPath finderPaths) {

		getPersistence().populateFinderCache(finderPaths);
	}

	public static FinderCachePopulationEntryPersistence getPersistence() {
		return _persistence;
	}

	private static volatile FinderCachePopulationEntryPersistence _persistence;

}
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
import com.liferay.portal.tools.service.builder.test.model.EagerFinderCacheEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the eager finder cache entry service. This utility wraps <code>com.liferay.portal.tools.service.builder.test.service.persistence.impl.EagerFinderCacheEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see EagerFinderCacheEntryPersistence
 * @generated
 */
public class EagerFinderCacheEntryUtil {

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
	public static void clearCache(EagerFinderCacheEntry eagerFinderCacheEntry) {
		getPersistence().clearCache(eagerFinderCacheEntry);
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
	public static Map<Serializable, EagerFinderCacheEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<EagerFinderCacheEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<EagerFinderCacheEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<EagerFinderCacheEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<EagerFinderCacheEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static EagerFinderCacheEntry update(
		EagerFinderCacheEntry eagerFinderCacheEntry) {

		return getPersistence().update(eagerFinderCacheEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static EagerFinderCacheEntry update(
		EagerFinderCacheEntry eagerFinderCacheEntry,
		ServiceContext serviceContext) {

		return getPersistence().update(eagerFinderCacheEntry, serviceContext);
	}

	/**
	 * Returns the eager finder cache entry where uniqueName = &#63; or throws a <code>NoSuchEagerFinderCacheEntryException</code> if it could not be found.
	 *
	 * @param uniqueName the unique name
	 * @return the matching eager finder cache entry
	 * @throws NoSuchEagerFinderCacheEntryException if a matching eager finder cache entry could not be found
	 */
	public static EagerFinderCacheEntry findByUniqueName(String uniqueName)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchEagerFinderCacheEntryException {

		return getPersistence().findByUniqueName(uniqueName);
	}

	/**
	 * Returns the eager finder cache entry where uniqueName = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uniqueName the unique name
	 * @return the matching eager finder cache entry, or <code>null</code> if a matching eager finder cache entry could not be found
	 */
	public static EagerFinderCacheEntry fetchByUniqueName(String uniqueName) {
		return getPersistence().fetchByUniqueName(uniqueName);
	}

	/**
	 * Returns the eager finder cache entry where uniqueName = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uniqueName the unique name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching eager finder cache entry, or <code>null</code> if a matching eager finder cache entry could not be found
	 */
	public static EagerFinderCacheEntry fetchByUniqueName(
		String uniqueName, boolean useFinderCache) {

		return getPersistence().fetchByUniqueName(uniqueName, useFinderCache);
	}

	/**
	 * Removes the eager finder cache entry where uniqueName = &#63; from the database.
	 *
	 * @param uniqueName the unique name
	 * @return the eager finder cache entry that was removed
	 */
	public static EagerFinderCacheEntry removeByUniqueName(String uniqueName)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchEagerFinderCacheEntryException {

		return getPersistence().removeByUniqueName(uniqueName);
	}

	/**
	 * Returns the number of eager finder cache entries where uniqueName = &#63;.
	 *
	 * @param uniqueName the unique name
	 * @return the number of matching eager finder cache entries
	 */
	public static int countByUniqueName(String uniqueName) {
		return getPersistence().countByUniqueName(uniqueName);
	}

	/**
	 * Returns all the eager finder cache entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching eager finder cache entries
	 */
	public static List<EagerFinderCacheEntry> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
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
	public static List<EagerFinderCacheEntry> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
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
	public static List<EagerFinderCacheEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<EagerFinderCacheEntry> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
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
	public static List<EagerFinderCacheEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<EagerFinderCacheEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first eager finder cache entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching eager finder cache entry
	 * @throws NoSuchEagerFinderCacheEntryException if a matching eager finder cache entry could not be found
	 */
	public static EagerFinderCacheEntry findByGroupId_First(
			long groupId,
			OrderByComparator<EagerFinderCacheEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchEagerFinderCacheEntryException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first eager finder cache entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching eager finder cache entry, or <code>null</code> if a matching eager finder cache entry could not be found
	 */
	public static EagerFinderCacheEntry fetchByGroupId_First(
		long groupId,
		OrderByComparator<EagerFinderCacheEntry> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns the last eager finder cache entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching eager finder cache entry
	 * @throws NoSuchEagerFinderCacheEntryException if a matching eager finder cache entry could not be found
	 */
	public static EagerFinderCacheEntry findByGroupId_Last(
			long groupId,
			OrderByComparator<EagerFinderCacheEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchEagerFinderCacheEntryException {

		return getPersistence().findByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the last eager finder cache entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching eager finder cache entry, or <code>null</code> if a matching eager finder cache entry could not be found
	 */
	public static EagerFinderCacheEntry fetchByGroupId_Last(
		long groupId,
		OrderByComparator<EagerFinderCacheEntry> orderByComparator) {

		return getPersistence().fetchByGroupId_Last(groupId, orderByComparator);
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
	public static EagerFinderCacheEntry[] findByGroupId_PrevAndNext(
			long eagerFinderCacheEntryId, long groupId,
			OrderByComparator<EagerFinderCacheEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchEagerFinderCacheEntryException {

		return getPersistence().findByGroupId_PrevAndNext(
			eagerFinderCacheEntryId, groupId, orderByComparator);
	}

	/**
	 * Removes all the eager finder cache entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of eager finder cache entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching eager finder cache entries
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns all the eager finder cache entries where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @return the matching eager finder cache entries
	 */
	public static List<EagerFinderCacheEntry> findByC_G(
		long companyId, long groupId) {

		return getPersistence().findByC_G(companyId, groupId);
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
	public static List<EagerFinderCacheEntry> findByC_G(
		long companyId, long groupId, int start, int end) {

		return getPersistence().findByC_G(companyId, groupId, start, end);
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
	public static List<EagerFinderCacheEntry> findByC_G(
		long companyId, long groupId, int start, int end,
		OrderByComparator<EagerFinderCacheEntry> orderByComparator) {

		return getPersistence().findByC_G(
			companyId, groupId, start, end, orderByComparator);
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
	public static List<EagerFinderCacheEntry> findByC_G(
		long companyId, long groupId, int start, int end,
		OrderByComparator<EagerFinderCacheEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_G(
			companyId, groupId, start, end, orderByComparator, useFinderCache);
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
	public static EagerFinderCacheEntry findByC_G_First(
			long companyId, long groupId,
			OrderByComparator<EagerFinderCacheEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchEagerFinderCacheEntryException {

		return getPersistence().findByC_G_First(
			companyId, groupId, orderByComparator);
	}

	/**
	 * Returns the first eager finder cache entry in the ordered set where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching eager finder cache entry, or <code>null</code> if a matching eager finder cache entry could not be found
	 */
	public static EagerFinderCacheEntry fetchByC_G_First(
		long companyId, long groupId,
		OrderByComparator<EagerFinderCacheEntry> orderByComparator) {

		return getPersistence().fetchByC_G_First(
			companyId, groupId, orderByComparator);
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
	public static EagerFinderCacheEntry findByC_G_Last(
			long companyId, long groupId,
			OrderByComparator<EagerFinderCacheEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchEagerFinderCacheEntryException {

		return getPersistence().findByC_G_Last(
			companyId, groupId, orderByComparator);
	}

	/**
	 * Returns the last eager finder cache entry in the ordered set where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching eager finder cache entry, or <code>null</code> if a matching eager finder cache entry could not be found
	 */
	public static EagerFinderCacheEntry fetchByC_G_Last(
		long companyId, long groupId,
		OrderByComparator<EagerFinderCacheEntry> orderByComparator) {

		return getPersistence().fetchByC_G_Last(
			companyId, groupId, orderByComparator);
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
	public static EagerFinderCacheEntry[] findByC_G_PrevAndNext(
			long eagerFinderCacheEntryId, long companyId, long groupId,
			OrderByComparator<EagerFinderCacheEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchEagerFinderCacheEntryException {

		return getPersistence().findByC_G_PrevAndNext(
			eagerFinderCacheEntryId, companyId, groupId, orderByComparator);
	}

	/**
	 * Removes all the eager finder cache entries where companyId = &#63; and groupId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 */
	public static void removeByC_G(long companyId, long groupId) {
		getPersistence().removeByC_G(companyId, groupId);
	}

	/**
	 * Returns the number of eager finder cache entries where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @return the number of matching eager finder cache entries
	 */
	public static int countByC_G(long companyId, long groupId) {
		return getPersistence().countByC_G(companyId, groupId);
	}

	/**
	 * Caches the eager finder cache entry in the entity cache if it is enabled.
	 *
	 * @param eagerFinderCacheEntry the eager finder cache entry
	 */
	public static void cacheResult(
		EagerFinderCacheEntry eagerFinderCacheEntry) {

		getPersistence().cacheResult(eagerFinderCacheEntry);
	}

	/**
	 * Caches the eager finder cache entries in the entity cache if it is enabled.
	 *
	 * @param eagerFinderCacheEntries the eager finder cache entries
	 */
	public static void cacheResult(
		List<EagerFinderCacheEntry> eagerFinderCacheEntries) {

		getPersistence().cacheResult(eagerFinderCacheEntries);
	}

	/**
	 * Creates a new eager finder cache entry with the primary key. Does not add the eager finder cache entry to the database.
	 *
	 * @param eagerFinderCacheEntryId the primary key for the new eager finder cache entry
	 * @return the new eager finder cache entry
	 */
	public static EagerFinderCacheEntry create(long eagerFinderCacheEntryId) {
		return getPersistence().create(eagerFinderCacheEntryId);
	}

	/**
	 * Removes the eager finder cache entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param eagerFinderCacheEntryId the primary key of the eager finder cache entry
	 * @return the eager finder cache entry that was removed
	 * @throws NoSuchEagerFinderCacheEntryException if a eager finder cache entry with the primary key could not be found
	 */
	public static EagerFinderCacheEntry remove(long eagerFinderCacheEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchEagerFinderCacheEntryException {

		return getPersistence().remove(eagerFinderCacheEntryId);
	}

	public static EagerFinderCacheEntry updateImpl(
		EagerFinderCacheEntry eagerFinderCacheEntry) {

		return getPersistence().updateImpl(eagerFinderCacheEntry);
	}

	/**
	 * Returns the eager finder cache entry with the primary key or throws a <code>NoSuchEagerFinderCacheEntryException</code> if it could not be found.
	 *
	 * @param eagerFinderCacheEntryId the primary key of the eager finder cache entry
	 * @return the eager finder cache entry
	 * @throws NoSuchEagerFinderCacheEntryException if a eager finder cache entry with the primary key could not be found
	 */
	public static EagerFinderCacheEntry findByPrimaryKey(
			long eagerFinderCacheEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchEagerFinderCacheEntryException {

		return getPersistence().findByPrimaryKey(eagerFinderCacheEntryId);
	}

	/**
	 * Returns the eager finder cache entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param eagerFinderCacheEntryId the primary key of the eager finder cache entry
	 * @return the eager finder cache entry, or <code>null</code> if a eager finder cache entry with the primary key could not be found
	 */
	public static EagerFinderCacheEntry fetchByPrimaryKey(
		long eagerFinderCacheEntryId) {

		return getPersistence().fetchByPrimaryKey(eagerFinderCacheEntryId);
	}

	/**
	 * Returns all the eager finder cache entries.
	 *
	 * @return the eager finder cache entries
	 */
	public static List<EagerFinderCacheEntry> findAll() {
		return getPersistence().findAll();
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
	public static List<EagerFinderCacheEntry> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
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
	public static List<EagerFinderCacheEntry> findAll(
		int start, int end,
		OrderByComparator<EagerFinderCacheEntry> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
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
	public static List<EagerFinderCacheEntry> findAll(
		int start, int end,
		OrderByComparator<EagerFinderCacheEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the eager finder cache entries from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of eager finder cache entries.
	 *
	 * @return the number of eager finder cache entries
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static EagerFinderCacheEntryPersistence getPersistence() {
		return _persistence;
	}

	private static volatile EagerFinderCacheEntryPersistence _persistence;

}
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

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchFinderCachePopulationEntryException;
import com.liferay.portal.tools.service.builder.test.model.FinderCachePopulationEntry;

import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the finder cache population entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see FinderCachePopulationEntryUtil
 * @generated
 */
@ProviderType
public interface FinderCachePopulationEntryPersistence
	extends BasePersistence<FinderCachePopulationEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link FinderCachePopulationEntryUtil} to access the finder cache population entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns the finder cache population entry where uniqueName = &#63; or throws a <code>NoSuchFinderCachePopulationEntryException</code> if it could not be found.
	 *
	 * @param uniqueName the unique name
	 * @return the matching finder cache population entry
	 * @throws NoSuchFinderCachePopulationEntryException if a matching finder cache population entry could not be found
	 */
	public FinderCachePopulationEntry findByUniqueName(String uniqueName)
		throws NoSuchFinderCachePopulationEntryException;

	/**
	 * Returns the finder cache population entry where uniqueName = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uniqueName the unique name
	 * @return the matching finder cache population entry, or <code>null</code> if a matching finder cache population entry could not be found
	 */
	public FinderCachePopulationEntry fetchByUniqueName(String uniqueName);

	/**
	 * Returns the finder cache population entry where uniqueName = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uniqueName the unique name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching finder cache population entry, or <code>null</code> if a matching finder cache population entry could not be found
	 */
	public FinderCachePopulationEntry fetchByUniqueName(
		String uniqueName, boolean useFinderCache);

	/**
	 * Removes the finder cache population entry where uniqueName = &#63; from the database.
	 *
	 * @param uniqueName the unique name
	 * @return the finder cache population entry that was removed
	 */
	public FinderCachePopulationEntry removeByUniqueName(String uniqueName)
		throws NoSuchFinderCachePopulationEntryException;

	/**
	 * Returns the number of finder cache population entries where uniqueName = &#63;.
	 *
	 * @param uniqueName the unique name
	 * @return the number of matching finder cache population entries
	 */
	public int countByUniqueName(String uniqueName);

	/**
	 * Returns all the finder cache population entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching finder cache population entries
	 */
	public java.util.List<FinderCachePopulationEntry> findByGroupId(
		long groupId);

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
	public java.util.List<FinderCachePopulationEntry> findByGroupId(
		long groupId, int start, int end);

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
	public java.util.List<FinderCachePopulationEntry> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<FinderCachePopulationEntry> orderByComparator);

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
	public java.util.List<FinderCachePopulationEntry> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<FinderCachePopulationEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first finder cache population entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder cache population entry
	 * @throws NoSuchFinderCachePopulationEntryException if a matching finder cache population entry could not be found
	 */
	public FinderCachePopulationEntry findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<FinderCachePopulationEntry> orderByComparator)
		throws NoSuchFinderCachePopulationEntryException;

	/**
	 * Returns the first finder cache population entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder cache population entry, or <code>null</code> if a matching finder cache population entry could not be found
	 */
	public FinderCachePopulationEntry fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator
			<FinderCachePopulationEntry> orderByComparator);

	/**
	 * Returns the last finder cache population entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching finder cache population entry
	 * @throws NoSuchFinderCachePopulationEntryException if a matching finder cache population entry could not be found
	 */
	public FinderCachePopulationEntry findByGroupId_Last(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<FinderCachePopulationEntry> orderByComparator)
		throws NoSuchFinderCachePopulationEntryException;

	/**
	 * Returns the last finder cache population entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching finder cache population entry, or <code>null</code> if a matching finder cache population entry could not be found
	 */
	public FinderCachePopulationEntry fetchByGroupId_Last(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator
			<FinderCachePopulationEntry> orderByComparator);

	/**
	 * Returns the finder cache population entries before and after the current finder cache population entry in the ordered set where groupId = &#63;.
	 *
	 * @param pinderCachePopulationEntryId the primary key of the current finder cache population entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next finder cache population entry
	 * @throws NoSuchFinderCachePopulationEntryException if a finder cache population entry with the primary key could not be found
	 */
	public FinderCachePopulationEntry[] findByGroupId_PrevAndNext(
			long pinderCachePopulationEntryId, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<FinderCachePopulationEntry> orderByComparator)
		throws NoSuchFinderCachePopulationEntryException;

	/**
	 * Removes all the finder cache population entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of finder cache population entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching finder cache population entries
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns all the finder cache population entries where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @return the matching finder cache population entries
	 */
	public java.util.List<FinderCachePopulationEntry> findByC_G(
		long companyId, long groupId);

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
	public java.util.List<FinderCachePopulationEntry> findByC_G(
		long companyId, long groupId, int start, int end);

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
	public java.util.List<FinderCachePopulationEntry> findByC_G(
		long companyId, long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<FinderCachePopulationEntry> orderByComparator);

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
	public java.util.List<FinderCachePopulationEntry> findByC_G(
		long companyId, long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<FinderCachePopulationEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first finder cache population entry in the ordered set where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder cache population entry
	 * @throws NoSuchFinderCachePopulationEntryException if a matching finder cache population entry could not be found
	 */
	public FinderCachePopulationEntry findByC_G_First(
			long companyId, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<FinderCachePopulationEntry> orderByComparator)
		throws NoSuchFinderCachePopulationEntryException;

	/**
	 * Returns the first finder cache population entry in the ordered set where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder cache population entry, or <code>null</code> if a matching finder cache population entry could not be found
	 */
	public FinderCachePopulationEntry fetchByC_G_First(
		long companyId, long groupId,
		com.liferay.portal.kernel.util.OrderByComparator
			<FinderCachePopulationEntry> orderByComparator);

	/**
	 * Returns the last finder cache population entry in the ordered set where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching finder cache population entry
	 * @throws NoSuchFinderCachePopulationEntryException if a matching finder cache population entry could not be found
	 */
	public FinderCachePopulationEntry findByC_G_Last(
			long companyId, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<FinderCachePopulationEntry> orderByComparator)
		throws NoSuchFinderCachePopulationEntryException;

	/**
	 * Returns the last finder cache population entry in the ordered set where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching finder cache population entry, or <code>null</code> if a matching finder cache population entry could not be found
	 */
	public FinderCachePopulationEntry fetchByC_G_Last(
		long companyId, long groupId,
		com.liferay.portal.kernel.util.OrderByComparator
			<FinderCachePopulationEntry> orderByComparator);

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
	public FinderCachePopulationEntry[] findByC_G_PrevAndNext(
			long pinderCachePopulationEntryId, long companyId, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<FinderCachePopulationEntry> orderByComparator)
		throws NoSuchFinderCachePopulationEntryException;

	/**
	 * Removes all the finder cache population entries where companyId = &#63; and groupId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 */
	public void removeByC_G(long companyId, long groupId);

	/**
	 * Returns the number of finder cache population entries where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @return the number of matching finder cache population entries
	 */
	public int countByC_G(long companyId, long groupId);

	/**
	 * Caches the finder cache population entry in the entity cache if it is enabled.
	 *
	 * @param finderCachePopulationEntry the finder cache population entry
	 */
	public void cacheResult(
		FinderCachePopulationEntry finderCachePopulationEntry);

	/**
	 * Caches the finder cache population entries in the entity cache if it is enabled.
	 *
	 * @param finderCachePopulationEntries the finder cache population entries
	 */
	public void cacheResult(
		java.util.List<FinderCachePopulationEntry>
			finderCachePopulationEntries);

	/**
	 * Creates a new finder cache population entry with the primary key. Does not add the finder cache population entry to the database.
	 *
	 * @param pinderCachePopulationEntryId the primary key for the new finder cache population entry
	 * @return the new finder cache population entry
	 */
	public FinderCachePopulationEntry create(long pinderCachePopulationEntryId);

	/**
	 * Removes the finder cache population entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param pinderCachePopulationEntryId the primary key of the finder cache population entry
	 * @return the finder cache population entry that was removed
	 * @throws NoSuchFinderCachePopulationEntryException if a finder cache population entry with the primary key could not be found
	 */
	public FinderCachePopulationEntry remove(long pinderCachePopulationEntryId)
		throws NoSuchFinderCachePopulationEntryException;

	public FinderCachePopulationEntry updateImpl(
		FinderCachePopulationEntry finderCachePopulationEntry);

	/**
	 * Returns the finder cache population entry with the primary key or throws a <code>NoSuchFinderCachePopulationEntryException</code> if it could not be found.
	 *
	 * @param pinderCachePopulationEntryId the primary key of the finder cache population entry
	 * @return the finder cache population entry
	 * @throws NoSuchFinderCachePopulationEntryException if a finder cache population entry with the primary key could not be found
	 */
	public FinderCachePopulationEntry findByPrimaryKey(
			long pinderCachePopulationEntryId)
		throws NoSuchFinderCachePopulationEntryException;

	/**
	 * Returns the finder cache population entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param pinderCachePopulationEntryId the primary key of the finder cache population entry
	 * @return the finder cache population entry, or <code>null</code> if a finder cache population entry with the primary key could not be found
	 */
	public FinderCachePopulationEntry fetchByPrimaryKey(
		long pinderCachePopulationEntryId);

	/**
	 * Returns all the finder cache population entries.
	 *
	 * @return the finder cache population entries
	 */
	public java.util.List<FinderCachePopulationEntry> findAll();

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
	public java.util.List<FinderCachePopulationEntry> findAll(
		int start, int end);

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
	public java.util.List<FinderCachePopulationEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<FinderCachePopulationEntry> orderByComparator);

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
	public java.util.List<FinderCachePopulationEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<FinderCachePopulationEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the finder cache population entries from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of finder cache population entries.
	 *
	 * @return the number of finder cache population entries
	 */
	public int countAll();

	public Map<String, com.liferay.portal.kernel.dao.orm.FinderPath>
		getFinderPaths();

	public void populateFinderCache(
		com.liferay.portal.kernel.dao.orm.FinderPath... finderPaths);

}
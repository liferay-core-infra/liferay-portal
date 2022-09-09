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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchEagerFinderCacheEntryException;
import com.liferay.portal.tools.service.builder.test.model.EagerFinderCacheEntry;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the eager finder cache entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see EagerFinderCacheEntryUtil
 * @generated
 */
@ProviderType
public interface EagerFinderCacheEntryPersistence
	extends BasePersistence<EagerFinderCacheEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link EagerFinderCacheEntryUtil} to access the eager finder cache entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns the eager finder cache entry where uniqueName = &#63; or throws a <code>NoSuchEagerFinderCacheEntryException</code> if it could not be found.
	 *
	 * @param uniqueName the unique name
	 * @return the matching eager finder cache entry
	 * @throws NoSuchEagerFinderCacheEntryException if a matching eager finder cache entry could not be found
	 */
	public EagerFinderCacheEntry findByUniqueName(String uniqueName)
		throws NoSuchEagerFinderCacheEntryException;

	/**
	 * Returns the eager finder cache entry where uniqueName = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uniqueName the unique name
	 * @return the matching eager finder cache entry, or <code>null</code> if a matching eager finder cache entry could not be found
	 */
	public EagerFinderCacheEntry fetchByUniqueName(String uniqueName);

	/**
	 * Returns the eager finder cache entry where uniqueName = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uniqueName the unique name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching eager finder cache entry, or <code>null</code> if a matching eager finder cache entry could not be found
	 */
	public EagerFinderCacheEntry fetchByUniqueName(
		String uniqueName, boolean useFinderCache);

	/**
	 * Removes the eager finder cache entry where uniqueName = &#63; from the database.
	 *
	 * @param uniqueName the unique name
	 * @return the eager finder cache entry that was removed
	 */
	public EagerFinderCacheEntry removeByUniqueName(String uniqueName)
		throws NoSuchEagerFinderCacheEntryException;

	/**
	 * Returns the number of eager finder cache entries where uniqueName = &#63;.
	 *
	 * @param uniqueName the unique name
	 * @return the number of matching eager finder cache entries
	 */
	public int countByUniqueName(String uniqueName);

	/**
	 * Returns all the eager finder cache entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching eager finder cache entries
	 */
	public java.util.List<EagerFinderCacheEntry> findByGroupId(long groupId);

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
	public java.util.List<EagerFinderCacheEntry> findByGroupId(
		long groupId, int start, int end);

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
	public java.util.List<EagerFinderCacheEntry> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<EagerFinderCacheEntry>
			orderByComparator);

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
	public java.util.List<EagerFinderCacheEntry> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<EagerFinderCacheEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first eager finder cache entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching eager finder cache entry
	 * @throws NoSuchEagerFinderCacheEntryException if a matching eager finder cache entry could not be found
	 */
	public EagerFinderCacheEntry findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<EagerFinderCacheEntry> orderByComparator)
		throws NoSuchEagerFinderCacheEntryException;

	/**
	 * Returns the first eager finder cache entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching eager finder cache entry, or <code>null</code> if a matching eager finder cache entry could not be found
	 */
	public EagerFinderCacheEntry fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<EagerFinderCacheEntry>
			orderByComparator);

	/**
	 * Returns the last eager finder cache entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching eager finder cache entry
	 * @throws NoSuchEagerFinderCacheEntryException if a matching eager finder cache entry could not be found
	 */
	public EagerFinderCacheEntry findByGroupId_Last(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<EagerFinderCacheEntry> orderByComparator)
		throws NoSuchEagerFinderCacheEntryException;

	/**
	 * Returns the last eager finder cache entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching eager finder cache entry, or <code>null</code> if a matching eager finder cache entry could not be found
	 */
	public EagerFinderCacheEntry fetchByGroupId_Last(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<EagerFinderCacheEntry>
			orderByComparator);

	/**
	 * Returns the eager finder cache entries before and after the current eager finder cache entry in the ordered set where groupId = &#63;.
	 *
	 * @param eagerFinderCacheEntryId the primary key of the current eager finder cache entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next eager finder cache entry
	 * @throws NoSuchEagerFinderCacheEntryException if a eager finder cache entry with the primary key could not be found
	 */
	public EagerFinderCacheEntry[] findByGroupId_PrevAndNext(
			long eagerFinderCacheEntryId, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<EagerFinderCacheEntry> orderByComparator)
		throws NoSuchEagerFinderCacheEntryException;

	/**
	 * Removes all the eager finder cache entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of eager finder cache entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching eager finder cache entries
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns all the eager finder cache entries where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @return the matching eager finder cache entries
	 */
	public java.util.List<EagerFinderCacheEntry> findByC_G(
		long companyId, long groupId);

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
	public java.util.List<EagerFinderCacheEntry> findByC_G(
		long companyId, long groupId, int start, int end);

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
	public java.util.List<EagerFinderCacheEntry> findByC_G(
		long companyId, long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<EagerFinderCacheEntry>
			orderByComparator);

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
	public java.util.List<EagerFinderCacheEntry> findByC_G(
		long companyId, long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<EagerFinderCacheEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first eager finder cache entry in the ordered set where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching eager finder cache entry
	 * @throws NoSuchEagerFinderCacheEntryException if a matching eager finder cache entry could not be found
	 */
	public EagerFinderCacheEntry findByC_G_First(
			long companyId, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<EagerFinderCacheEntry> orderByComparator)
		throws NoSuchEagerFinderCacheEntryException;

	/**
	 * Returns the first eager finder cache entry in the ordered set where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching eager finder cache entry, or <code>null</code> if a matching eager finder cache entry could not be found
	 */
	public EagerFinderCacheEntry fetchByC_G_First(
		long companyId, long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<EagerFinderCacheEntry>
			orderByComparator);

	/**
	 * Returns the last eager finder cache entry in the ordered set where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching eager finder cache entry
	 * @throws NoSuchEagerFinderCacheEntryException if a matching eager finder cache entry could not be found
	 */
	public EagerFinderCacheEntry findByC_G_Last(
			long companyId, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<EagerFinderCacheEntry> orderByComparator)
		throws NoSuchEagerFinderCacheEntryException;

	/**
	 * Returns the last eager finder cache entry in the ordered set where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching eager finder cache entry, or <code>null</code> if a matching eager finder cache entry could not be found
	 */
	public EagerFinderCacheEntry fetchByC_G_Last(
		long companyId, long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<EagerFinderCacheEntry>
			orderByComparator);

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
	public EagerFinderCacheEntry[] findByC_G_PrevAndNext(
			long eagerFinderCacheEntryId, long companyId, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<EagerFinderCacheEntry> orderByComparator)
		throws NoSuchEagerFinderCacheEntryException;

	/**
	 * Removes all the eager finder cache entries where companyId = &#63; and groupId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 */
	public void removeByC_G(long companyId, long groupId);

	/**
	 * Returns the number of eager finder cache entries where companyId = &#63; and groupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @return the number of matching eager finder cache entries
	 */
	public int countByC_G(long companyId, long groupId);

	/**
	 * Caches the eager finder cache entry in the entity cache if it is enabled.
	 *
	 * @param eagerFinderCacheEntry the eager finder cache entry
	 */
	public void cacheResult(EagerFinderCacheEntry eagerFinderCacheEntry);

	/**
	 * Caches the eager finder cache entries in the entity cache if it is enabled.
	 *
	 * @param eagerFinderCacheEntries the eager finder cache entries
	 */
	public void cacheResult(
		java.util.List<EagerFinderCacheEntry> eagerFinderCacheEntries);

	/**
	 * Creates a new eager finder cache entry with the primary key. Does not add the eager finder cache entry to the database.
	 *
	 * @param eagerFinderCacheEntryId the primary key for the new eager finder cache entry
	 * @return the new eager finder cache entry
	 */
	public EagerFinderCacheEntry create(long eagerFinderCacheEntryId);

	/**
	 * Removes the eager finder cache entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param eagerFinderCacheEntryId the primary key of the eager finder cache entry
	 * @return the eager finder cache entry that was removed
	 * @throws NoSuchEagerFinderCacheEntryException if a eager finder cache entry with the primary key could not be found
	 */
	public EagerFinderCacheEntry remove(long eagerFinderCacheEntryId)
		throws NoSuchEagerFinderCacheEntryException;

	public EagerFinderCacheEntry updateImpl(
		EagerFinderCacheEntry eagerFinderCacheEntry);

	/**
	 * Returns the eager finder cache entry with the primary key or throws a <code>NoSuchEagerFinderCacheEntryException</code> if it could not be found.
	 *
	 * @param eagerFinderCacheEntryId the primary key of the eager finder cache entry
	 * @return the eager finder cache entry
	 * @throws NoSuchEagerFinderCacheEntryException if a eager finder cache entry with the primary key could not be found
	 */
	public EagerFinderCacheEntry findByPrimaryKey(long eagerFinderCacheEntryId)
		throws NoSuchEagerFinderCacheEntryException;

	/**
	 * Returns the eager finder cache entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param eagerFinderCacheEntryId the primary key of the eager finder cache entry
	 * @return the eager finder cache entry, or <code>null</code> if a eager finder cache entry with the primary key could not be found
	 */
	public EagerFinderCacheEntry fetchByPrimaryKey(
		long eagerFinderCacheEntryId);

	/**
	 * Returns all the eager finder cache entries.
	 *
	 * @return the eager finder cache entries
	 */
	public java.util.List<EagerFinderCacheEntry> findAll();

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
	public java.util.List<EagerFinderCacheEntry> findAll(int start, int end);

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
	public java.util.List<EagerFinderCacheEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<EagerFinderCacheEntry>
			orderByComparator);

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
	public java.util.List<EagerFinderCacheEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<EagerFinderCacheEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the eager finder cache entries from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of eager finder cache entries.
	 *
	 * @return the number of eager finder cache entries
	 */
	public int countAll();

}
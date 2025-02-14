/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchSortedFinderEntryException;
import com.liferay.portal.tools.service.builder.test.model.SortedFinderEntry;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the sorted finder entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see SortedFinderEntryUtil
 * @generated
 */
@ProviderType
public interface SortedFinderEntryPersistence
	extends BasePersistence<SortedFinderEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link SortedFinderEntryUtil} to access the sorted finder entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the sorted finder entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching sorted finder entries
	 */
	public java.util.List<SortedFinderEntry> findByGroupId(long groupId);

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
	public java.util.List<SortedFinderEntry> findByGroupId(
		long groupId, int start, int end);

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
	public java.util.List<SortedFinderEntry> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SortedFinderEntry>
			orderByComparator);

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
	public java.util.List<SortedFinderEntry> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SortedFinderEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first sorted finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sorted finder entry
	 * @throws NoSuchSortedFinderEntryException if a matching sorted finder entry could not be found
	 */
	public SortedFinderEntry findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<SortedFinderEntry>
				orderByComparator)
		throws NoSuchSortedFinderEntryException;

	/**
	 * Returns the first sorted finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sorted finder entry, or <code>null</code> if a matching sorted finder entry could not be found
	 */
	public SortedFinderEntry fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<SortedFinderEntry>
			orderByComparator);

	/**
	 * Returns the last sorted finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching sorted finder entry
	 * @throws NoSuchSortedFinderEntryException if a matching sorted finder entry could not be found
	 */
	public SortedFinderEntry findByGroupId_Last(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<SortedFinderEntry>
				orderByComparator)
		throws NoSuchSortedFinderEntryException;

	/**
	 * Returns the last sorted finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching sorted finder entry, or <code>null</code> if a matching sorted finder entry could not be found
	 */
	public SortedFinderEntry fetchByGroupId_Last(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<SortedFinderEntry>
			orderByComparator);

	/**
	 * Returns the sorted finder entries before and after the current sorted finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param sortedFinderEntryId the primary key of the current sorted finder entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next sorted finder entry
	 * @throws NoSuchSortedFinderEntryException if a sorted finder entry with the primary key could not be found
	 */
	public SortedFinderEntry[] findByGroupId_PrevAndNext(
			long sortedFinderEntryId, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<SortedFinderEntry>
				orderByComparator)
		throws NoSuchSortedFinderEntryException;

	/**
	 * Removes all the sorted finder entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of sorted finder entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching sorted finder entries
	 */
	public int countByGroupId(long groupId);

	/**
	 * Caches the sorted finder entry in the entity cache if it is enabled.
	 *
	 * @param sortedFinderEntry the sorted finder entry
	 */
	public void cacheResult(SortedFinderEntry sortedFinderEntry);

	/**
	 * Caches the sorted finder entries in the entity cache if it is enabled.
	 *
	 * @param sortedFinderEntries the sorted finder entries
	 */
	public void cacheResult(
		java.util.List<SortedFinderEntry> sortedFinderEntries);

	/**
	 * Creates a new sorted finder entry with the primary key. Does not add the sorted finder entry to the database.
	 *
	 * @param sortedFinderEntryId the primary key for the new sorted finder entry
	 * @return the new sorted finder entry
	 */
	public SortedFinderEntry create(long sortedFinderEntryId);

	/**
	 * Removes the sorted finder entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param sortedFinderEntryId the primary key of the sorted finder entry
	 * @return the sorted finder entry that was removed
	 * @throws NoSuchSortedFinderEntryException if a sorted finder entry with the primary key could not be found
	 */
	public SortedFinderEntry remove(long sortedFinderEntryId)
		throws NoSuchSortedFinderEntryException;

	public SortedFinderEntry updateImpl(SortedFinderEntry sortedFinderEntry);

	/**
	 * Returns the sorted finder entry with the primary key or throws a <code>NoSuchSortedFinderEntryException</code> if it could not be found.
	 *
	 * @param sortedFinderEntryId the primary key of the sorted finder entry
	 * @return the sorted finder entry
	 * @throws NoSuchSortedFinderEntryException if a sorted finder entry with the primary key could not be found
	 */
	public SortedFinderEntry findByPrimaryKey(long sortedFinderEntryId)
		throws NoSuchSortedFinderEntryException;

	/**
	 * Returns the sorted finder entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param sortedFinderEntryId the primary key of the sorted finder entry
	 * @return the sorted finder entry, or <code>null</code> if a sorted finder entry with the primary key could not be found
	 */
	public SortedFinderEntry fetchByPrimaryKey(long sortedFinderEntryId);

	/**
	 * Returns all the sorted finder entries.
	 *
	 * @return the sorted finder entries
	 */
	public java.util.List<SortedFinderEntry> findAll();

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
	public java.util.List<SortedFinderEntry> findAll(int start, int end);

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
	public java.util.List<SortedFinderEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SortedFinderEntry>
			orderByComparator);

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
	public java.util.List<SortedFinderEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SortedFinderEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the sorted finder entries from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of sorted finder entries.
	 *
	 * @return the number of sorted finder entries
	 */
	public int countAll();

}
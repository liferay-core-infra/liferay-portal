/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchFilterFindEntryException;
import com.liferay.portal.tools.service.builder.test.model.FilterFindEntry;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the filter find entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see FilterFindEntryUtil
 * @generated
 */
@ProviderType
public interface FilterFindEntryPersistence
	extends BasePersistence<FilterFindEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link FilterFindEntryUtil} to access the filter find entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the filter find entries where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @return the matching filter find entries
	 */
	public java.util.List<FilterFindEntry> findByG_I_T(
		long groupId, int integer, String type);

	/**
	 * Returns a range of all the filter find entries where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @return the range of matching filter find entries
	 */
	public java.util.List<FilterFindEntry> findByG_I_T(
		long groupId, int integer, String type, int start, int end);

	/**
	 * Returns an ordered range of all the filter find entries where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching filter find entries
	 */
	public java.util.List<FilterFindEntry> findByG_I_T(
		long groupId, int integer, String type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FilterFindEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the filter find entries where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching filter find entries
	 */
	public java.util.List<FilterFindEntry> findByG_I_T(
		long groupId, int integer, String type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FilterFindEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first filter find entry in the ordered set where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching filter find entry
	 * @throws NoSuchFilterFindEntryException if a matching filter find entry could not be found
	 */
	public FilterFindEntry findByG_I_T_First(
			long groupId, int integer, String type,
			com.liferay.portal.kernel.util.OrderByComparator<FilterFindEntry>
				orderByComparator)
		throws NoSuchFilterFindEntryException;

	/**
	 * Returns the first filter find entry in the ordered set where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching filter find entry, or <code>null</code> if a matching filter find entry could not be found
	 */
	public FilterFindEntry fetchByG_I_T_First(
		long groupId, int integer, String type,
		com.liferay.portal.kernel.util.OrderByComparator<FilterFindEntry>
			orderByComparator);

	/**
	 * Returns the last filter find entry in the ordered set where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching filter find entry
	 * @throws NoSuchFilterFindEntryException if a matching filter find entry could not be found
	 */
	public FilterFindEntry findByG_I_T_Last(
			long groupId, int integer, String type,
			com.liferay.portal.kernel.util.OrderByComparator<FilterFindEntry>
				orderByComparator)
		throws NoSuchFilterFindEntryException;

	/**
	 * Returns the last filter find entry in the ordered set where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching filter find entry, or <code>null</code> if a matching filter find entry could not be found
	 */
	public FilterFindEntry fetchByG_I_T_Last(
		long groupId, int integer, String type,
		com.liferay.portal.kernel.util.OrderByComparator<FilterFindEntry>
			orderByComparator);

	/**
	 * Returns the filter find entries before and after the current filter find entry in the ordered set where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * @param filterFindEntryId the primary key of the current filter find entry
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next filter find entry
	 * @throws NoSuchFilterFindEntryException if a filter find entry with the primary key could not be found
	 */
	public FilterFindEntry[] findByG_I_T_PrevAndNext(
			long filterFindEntryId, long groupId, int integer, String type,
			com.liferay.portal.kernel.util.OrderByComparator<FilterFindEntry>
				orderByComparator)
		throws NoSuchFilterFindEntryException;

	/**
	 * Returns all the filter find entries that the user has permission to view where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @return the matching filter find entries that the user has permission to view
	 */
	public java.util.List<FilterFindEntry> filterFindByG_I_T(
		long groupId, int integer, String type);

	/**
	 * Returns a range of all the filter find entries that the user has permission to view where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @return the range of matching filter find entries that the user has permission to view
	 */
	public java.util.List<FilterFindEntry> filterFindByG_I_T(
		long groupId, int integer, String type, int start, int end);

	/**
	 * Returns an ordered range of all the filter find entries that the user has permissions to view where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching filter find entries that the user has permission to view
	 */
	public java.util.List<FilterFindEntry> filterFindByG_I_T(
		long groupId, int integer, String type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FilterFindEntry>
			orderByComparator);

	/**
	 * Returns the filter find entries before and after the current filter find entry in the ordered set of filter find entries that the user has permission to view where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * @param filterFindEntryId the primary key of the current filter find entry
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next filter find entry
	 * @throws NoSuchFilterFindEntryException if a filter find entry with the primary key could not be found
	 */
	public FilterFindEntry[] filterFindByG_I_T_PrevAndNext(
			long filterFindEntryId, long groupId, int integer, String type,
			com.liferay.portal.kernel.util.OrderByComparator<FilterFindEntry>
				orderByComparator)
		throws NoSuchFilterFindEntryException;

	/**
	 * Returns all the filter find entries that the user has permission to view where groupId = &#63; and integer = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param types the types
	 * @return the matching filter find entries that the user has permission to view
	 */
	public java.util.List<FilterFindEntry> filterFindByG_I_T(
		long groupId, int integer, String[] types);

	/**
	 * Returns a range of all the filter find entries that the user has permission to view where groupId = &#63; and integer = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param types the types
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @return the range of matching filter find entries that the user has permission to view
	 */
	public java.util.List<FilterFindEntry> filterFindByG_I_T(
		long groupId, int integer, String[] types, int start, int end);

	/**
	 * Returns an ordered range of all the filter find entries that the user has permission to view where groupId = &#63; and integer = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param types the types
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching filter find entries that the user has permission to view
	 */
	public java.util.List<FilterFindEntry> filterFindByG_I_T(
		long groupId, int integer, String[] types, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FilterFindEntry>
			orderByComparator);

	/**
	 * Returns all the filter find entries where groupId = &#63; and integer = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param types the types
	 * @return the matching filter find entries
	 */
	public java.util.List<FilterFindEntry> findByG_I_T(
		long groupId, int integer, String[] types);

	/**
	 * Returns a range of all the filter find entries where groupId = &#63; and integer = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param types the types
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @return the range of matching filter find entries
	 */
	public java.util.List<FilterFindEntry> findByG_I_T(
		long groupId, int integer, String[] types, int start, int end);

	/**
	 * Returns an ordered range of all the filter find entries where groupId = &#63; and integer = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param types the types
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching filter find entries
	 */
	public java.util.List<FilterFindEntry> findByG_I_T(
		long groupId, int integer, String[] types, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FilterFindEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the filter find entries where groupId = &#63; and integer = &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param types the types
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching filter find entries
	 */
	public java.util.List<FilterFindEntry> findByG_I_T(
		long groupId, int integer, String[] types, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FilterFindEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the filter find entries where groupId = &#63; and integer = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 */
	public void removeByG_I_T(long groupId, int integer, String type);

	/**
	 * Returns the number of filter find entries where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @return the number of matching filter find entries
	 */
	public int countByG_I_T(long groupId, int integer, String type);

	/**
	 * Returns the number of filter find entries where groupId = &#63; and integer = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param types the types
	 * @return the number of matching filter find entries
	 */
	public int countByG_I_T(long groupId, int integer, String[] types);

	/**
	 * Returns the number of filter find entries that the user has permission to view where groupId = &#63; and integer = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param type the type
	 * @return the number of matching filter find entries that the user has permission to view
	 */
	public int filterCountByG_I_T(long groupId, int integer, String type);

	/**
	 * Returns the number of filter find entries that the user has permission to view where groupId = &#63; and integer = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param integer the integer
	 * @param types the types
	 * @return the number of matching filter find entries that the user has permission to view
	 */
	public int filterCountByG_I_T(long groupId, int integer, String[] types);

	/**
	 * Caches the filter find entry in the entity cache if it is enabled.
	 *
	 * @param filterFindEntry the filter find entry
	 */
	public void cacheResult(FilterFindEntry filterFindEntry);

	/**
	 * Caches the filter find entries in the entity cache if it is enabled.
	 *
	 * @param filterFindEntries the filter find entries
	 */
	public void cacheResult(java.util.List<FilterFindEntry> filterFindEntries);

	/**
	 * Creates a new filter find entry with the primary key. Does not add the filter find entry to the database.
	 *
	 * @param filterFindEntryId the primary key for the new filter find entry
	 * @return the new filter find entry
	 */
	public FilterFindEntry create(long filterFindEntryId);

	/**
	 * Removes the filter find entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param filterFindEntryId the primary key of the filter find entry
	 * @return the filter find entry that was removed
	 * @throws NoSuchFilterFindEntryException if a filter find entry with the primary key could not be found
	 */
	public FilterFindEntry remove(long filterFindEntryId)
		throws NoSuchFilterFindEntryException;

	public FilterFindEntry updateImpl(FilterFindEntry filterFindEntry);

	/**
	 * Returns the filter find entry with the primary key or throws a <code>NoSuchFilterFindEntryException</code> if it could not be found.
	 *
	 * @param filterFindEntryId the primary key of the filter find entry
	 * @return the filter find entry
	 * @throws NoSuchFilterFindEntryException if a filter find entry with the primary key could not be found
	 */
	public FilterFindEntry findByPrimaryKey(long filterFindEntryId)
		throws NoSuchFilterFindEntryException;

	/**
	 * Returns the filter find entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param filterFindEntryId the primary key of the filter find entry
	 * @return the filter find entry, or <code>null</code> if a filter find entry with the primary key could not be found
	 */
	public FilterFindEntry fetchByPrimaryKey(long filterFindEntryId);

	/**
	 * Returns all the filter find entries.
	 *
	 * @return the filter find entries
	 */
	public java.util.List<FilterFindEntry> findAll();

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
	public java.util.List<FilterFindEntry> findAll(int start, int end);

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
	public java.util.List<FilterFindEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FilterFindEntry>
			orderByComparator);

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
	public java.util.List<FilterFindEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FilterFindEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the filter find entries from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of filter find entries.
	 *
	 * @return the number of filter find entries
	 */
	public int countAll();

}
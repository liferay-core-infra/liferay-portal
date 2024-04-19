/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchBadColumnNameEntryException;
import com.liferay.portal.tools.service.builder.test.model.BadColumnNameEntry;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the bad column name entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see BadColumnNameEntryUtil
 * @generated
 */
@ProviderType
public interface BadColumnNameEntryPersistence
	extends BasePersistence<BadColumnNameEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link BadColumnNameEntryUtil} to access the bad column name entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the bad column name entries where type = &#63;.
	 *
	 * @param type the type
	 * @return the matching bad column name entries
	 */
	public java.util.List<BadColumnNameEntry> findByType(String type);

	/**
	 * Returns a range of all the bad column name entries where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BadColumnNameEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of bad column name entries
	 * @param end the upper bound of the range of bad column name entries (not inclusive)
	 * @return the range of matching bad column name entries
	 */
	public java.util.List<BadColumnNameEntry> findByType(
		String type, int start, int end);

	/**
	 * Returns an ordered range of all the bad column name entries where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BadColumnNameEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of bad column name entries
	 * @param end the upper bound of the range of bad column name entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching bad column name entries
	 */
	public java.util.List<BadColumnNameEntry> findByType(
		String type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<BadColumnNameEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the bad column name entries where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BadColumnNameEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of bad column name entries
	 * @param end the upper bound of the range of bad column name entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching bad column name entries
	 */
	public java.util.List<BadColumnNameEntry> findByType(
		String type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<BadColumnNameEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first bad column name entry in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bad column name entry
	 * @throws NoSuchBadColumnNameEntryException if a matching bad column name entry could not be found
	 */
	public BadColumnNameEntry findByType_First(
			String type,
			com.liferay.portal.kernel.util.OrderByComparator<BadColumnNameEntry>
				orderByComparator)
		throws NoSuchBadColumnNameEntryException;

	/**
	 * Returns the first bad column name entry in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bad column name entry, or <code>null</code> if a matching bad column name entry could not be found
	 */
	public BadColumnNameEntry fetchByType_First(
		String type,
		com.liferay.portal.kernel.util.OrderByComparator<BadColumnNameEntry>
			orderByComparator);

	/**
	 * Returns the last bad column name entry in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bad column name entry
	 * @throws NoSuchBadColumnNameEntryException if a matching bad column name entry could not be found
	 */
	public BadColumnNameEntry findByType_Last(
			String type,
			com.liferay.portal.kernel.util.OrderByComparator<BadColumnNameEntry>
				orderByComparator)
		throws NoSuchBadColumnNameEntryException;

	/**
	 * Returns the last bad column name entry in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bad column name entry, or <code>null</code> if a matching bad column name entry could not be found
	 */
	public BadColumnNameEntry fetchByType_Last(
		String type,
		com.liferay.portal.kernel.util.OrderByComparator<BadColumnNameEntry>
			orderByComparator);

	/**
	 * Returns the bad column name entries before and after the current bad column name entry in the ordered set where type = &#63;.
	 *
	 * @param badColumnNameEntryId the primary key of the current bad column name entry
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next bad column name entry
	 * @throws NoSuchBadColumnNameEntryException if a bad column name entry with the primary key could not be found
	 */
	public BadColumnNameEntry[] findByType_PrevAndNext(
			long badColumnNameEntryId, String type,
			com.liferay.portal.kernel.util.OrderByComparator<BadColumnNameEntry>
				orderByComparator)
		throws NoSuchBadColumnNameEntryException;

	/**
	 * Returns all the bad column name entries that the user has permission to view where type = &#63;.
	 *
	 * @param type the type
	 * @return the matching bad column name entries that the user has permission to view
	 */
	public java.util.List<BadColumnNameEntry> filterFindByType(String type);

	/**
	 * Returns a range of all the bad column name entries that the user has permission to view where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BadColumnNameEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of bad column name entries
	 * @param end the upper bound of the range of bad column name entries (not inclusive)
	 * @return the range of matching bad column name entries that the user has permission to view
	 */
	public java.util.List<BadColumnNameEntry> filterFindByType(
		String type, int start, int end);

	/**
	 * Returns an ordered range of all the bad column name entries that the user has permissions to view where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BadColumnNameEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of bad column name entries
	 * @param end the upper bound of the range of bad column name entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching bad column name entries that the user has permission to view
	 */
	public java.util.List<BadColumnNameEntry> filterFindByType(
		String type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<BadColumnNameEntry>
			orderByComparator);

	/**
	 * Returns the bad column name entries before and after the current bad column name entry in the ordered set of bad column name entries that the user has permission to view where type = &#63;.
	 *
	 * @param badColumnNameEntryId the primary key of the current bad column name entry
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next bad column name entry
	 * @throws NoSuchBadColumnNameEntryException if a bad column name entry with the primary key could not be found
	 */
	public BadColumnNameEntry[] filterFindByType_PrevAndNext(
			long badColumnNameEntryId, String type,
			com.liferay.portal.kernel.util.OrderByComparator<BadColumnNameEntry>
				orderByComparator)
		throws NoSuchBadColumnNameEntryException;

	/**
	 * Removes all the bad column name entries where type = &#63; from the database.
	 *
	 * @param type the type
	 */
	public void removeByType(String type);

	/**
	 * Returns the number of bad column name entries where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching bad column name entries
	 */
	public int countByType(String type);

	/**
	 * Returns the number of bad column name entries that the user has permission to view where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching bad column name entries that the user has permission to view
	 */
	public int filterCountByType(String type);

	/**
	 * Caches the bad column name entry in the entity cache if it is enabled.
	 *
	 * @param badColumnNameEntry the bad column name entry
	 */
	public void cacheResult(BadColumnNameEntry badColumnNameEntry);

	/**
	 * Caches the bad column name entries in the entity cache if it is enabled.
	 *
	 * @param badColumnNameEntries the bad column name entries
	 */
	public void cacheResult(
		java.util.List<BadColumnNameEntry> badColumnNameEntries);

	/**
	 * Creates a new bad column name entry with the primary key. Does not add the bad column name entry to the database.
	 *
	 * @param badColumnNameEntryId the primary key for the new bad column name entry
	 * @return the new bad column name entry
	 */
	public BadColumnNameEntry create(long badColumnNameEntryId);

	/**
	 * Removes the bad column name entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param badColumnNameEntryId the primary key of the bad column name entry
	 * @return the bad column name entry that was removed
	 * @throws NoSuchBadColumnNameEntryException if a bad column name entry with the primary key could not be found
	 */
	public BadColumnNameEntry remove(long badColumnNameEntryId)
		throws NoSuchBadColumnNameEntryException;

	public BadColumnNameEntry updateImpl(BadColumnNameEntry badColumnNameEntry);

	/**
	 * Returns the bad column name entry with the primary key or throws a <code>NoSuchBadColumnNameEntryException</code> if it could not be found.
	 *
	 * @param badColumnNameEntryId the primary key of the bad column name entry
	 * @return the bad column name entry
	 * @throws NoSuchBadColumnNameEntryException if a bad column name entry with the primary key could not be found
	 */
	public BadColumnNameEntry findByPrimaryKey(long badColumnNameEntryId)
		throws NoSuchBadColumnNameEntryException;

	/**
	 * Returns the bad column name entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param badColumnNameEntryId the primary key of the bad column name entry
	 * @return the bad column name entry, or <code>null</code> if a bad column name entry with the primary key could not be found
	 */
	public BadColumnNameEntry fetchByPrimaryKey(long badColumnNameEntryId);

	/**
	 * Returns all the bad column name entries.
	 *
	 * @return the bad column name entries
	 */
	public java.util.List<BadColumnNameEntry> findAll();

	/**
	 * Returns a range of all the bad column name entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BadColumnNameEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of bad column name entries
	 * @param end the upper bound of the range of bad column name entries (not inclusive)
	 * @return the range of bad column name entries
	 */
	public java.util.List<BadColumnNameEntry> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the bad column name entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BadColumnNameEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of bad column name entries
	 * @param end the upper bound of the range of bad column name entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of bad column name entries
	 */
	public java.util.List<BadColumnNameEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<BadColumnNameEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the bad column name entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BadColumnNameEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of bad column name entries
	 * @param end the upper bound of the range of bad column name entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of bad column name entries
	 */
	public java.util.List<BadColumnNameEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<BadColumnNameEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the bad column name entries from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of bad column name entries.
	 *
	 * @return the number of bad column name entries
	 */
	public int countAll();

}
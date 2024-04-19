/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.BadColumnNameEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the bad column name entry service. This utility wraps <code>com.liferay.portal.tools.service.builder.test.service.persistence.impl.BadColumnNameEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see BadColumnNameEntryPersistence
 * @generated
 */
public class BadColumnNameEntryUtil {

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
	public static void clearCache(BadColumnNameEntry badColumnNameEntry) {
		getPersistence().clearCache(badColumnNameEntry);
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
	public static Map<Serializable, BadColumnNameEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<BadColumnNameEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<BadColumnNameEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<BadColumnNameEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<BadColumnNameEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static BadColumnNameEntry update(
		BadColumnNameEntry badColumnNameEntry) {

		return getPersistence().update(badColumnNameEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static BadColumnNameEntry update(
		BadColumnNameEntry badColumnNameEntry, ServiceContext serviceContext) {

		return getPersistence().update(badColumnNameEntry, serviceContext);
	}

	/**
	 * Returns all the bad column name entries where type = &#63;.
	 *
	 * @param type the type
	 * @return the matching bad column name entries
	 */
	public static List<BadColumnNameEntry> findByType(String type) {
		return getPersistence().findByType(type);
	}

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
	public static List<BadColumnNameEntry> findByType(
		String type, int start, int end) {

		return getPersistence().findByType(type, start, end);
	}

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
	public static List<BadColumnNameEntry> findByType(
		String type, int start, int end,
		OrderByComparator<BadColumnNameEntry> orderByComparator) {

		return getPersistence().findByType(type, start, end, orderByComparator);
	}

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
	public static List<BadColumnNameEntry> findByType(
		String type, int start, int end,
		OrderByComparator<BadColumnNameEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByType(
			type, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first bad column name entry in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bad column name entry
	 * @throws NoSuchBadColumnNameEntryException if a matching bad column name entry could not be found
	 */
	public static BadColumnNameEntry findByType_First(
			String type,
			OrderByComparator<BadColumnNameEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchBadColumnNameEntryException {

		return getPersistence().findByType_First(type, orderByComparator);
	}

	/**
	 * Returns the first bad column name entry in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bad column name entry, or <code>null</code> if a matching bad column name entry could not be found
	 */
	public static BadColumnNameEntry fetchByType_First(
		String type, OrderByComparator<BadColumnNameEntry> orderByComparator) {

		return getPersistence().fetchByType_First(type, orderByComparator);
	}

	/**
	 * Returns the last bad column name entry in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bad column name entry
	 * @throws NoSuchBadColumnNameEntryException if a matching bad column name entry could not be found
	 */
	public static BadColumnNameEntry findByType_Last(
			String type,
			OrderByComparator<BadColumnNameEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchBadColumnNameEntryException {

		return getPersistence().findByType_Last(type, orderByComparator);
	}

	/**
	 * Returns the last bad column name entry in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bad column name entry, or <code>null</code> if a matching bad column name entry could not be found
	 */
	public static BadColumnNameEntry fetchByType_Last(
		String type, OrderByComparator<BadColumnNameEntry> orderByComparator) {

		return getPersistence().fetchByType_Last(type, orderByComparator);
	}

	/**
	 * Returns the bad column name entries before and after the current bad column name entry in the ordered set where type = &#63;.
	 *
	 * @param badColumnNameEntryId the primary key of the current bad column name entry
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next bad column name entry
	 * @throws NoSuchBadColumnNameEntryException if a bad column name entry with the primary key could not be found
	 */
	public static BadColumnNameEntry[] findByType_PrevAndNext(
			long badColumnNameEntryId, String type,
			OrderByComparator<BadColumnNameEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchBadColumnNameEntryException {

		return getPersistence().findByType_PrevAndNext(
			badColumnNameEntryId, type, orderByComparator);
	}

	/**
	 * Returns all the bad column name entries that the user has permission to view where type = &#63;.
	 *
	 * @param type the type
	 * @return the matching bad column name entries that the user has permission to view
	 */
	public static List<BadColumnNameEntry> filterFindByType(String type) {
		return getPersistence().filterFindByType(type);
	}

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
	public static List<BadColumnNameEntry> filterFindByType(
		String type, int start, int end) {

		return getPersistence().filterFindByType(type, start, end);
	}

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
	public static List<BadColumnNameEntry> filterFindByType(
		String type, int start, int end,
		OrderByComparator<BadColumnNameEntry> orderByComparator) {

		return getPersistence().filterFindByType(
			type, start, end, orderByComparator);
	}

	/**
	 * Returns the bad column name entries before and after the current bad column name entry in the ordered set of bad column name entries that the user has permission to view where type = &#63;.
	 *
	 * @param badColumnNameEntryId the primary key of the current bad column name entry
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next bad column name entry
	 * @throws NoSuchBadColumnNameEntryException if a bad column name entry with the primary key could not be found
	 */
	public static BadColumnNameEntry[] filterFindByType_PrevAndNext(
			long badColumnNameEntryId, String type,
			OrderByComparator<BadColumnNameEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchBadColumnNameEntryException {

		return getPersistence().filterFindByType_PrevAndNext(
			badColumnNameEntryId, type, orderByComparator);
	}

	/**
	 * Removes all the bad column name entries where type = &#63; from the database.
	 *
	 * @param type the type
	 */
	public static void removeByType(String type) {
		getPersistence().removeByType(type);
	}

	/**
	 * Returns the number of bad column name entries where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching bad column name entries
	 */
	public static int countByType(String type) {
		return getPersistence().countByType(type);
	}

	/**
	 * Returns the number of bad column name entries that the user has permission to view where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching bad column name entries that the user has permission to view
	 */
	public static int filterCountByType(String type) {
		return getPersistence().filterCountByType(type);
	}

	/**
	 * Caches the bad column name entry in the entity cache if it is enabled.
	 *
	 * @param badColumnNameEntry the bad column name entry
	 */
	public static void cacheResult(BadColumnNameEntry badColumnNameEntry) {
		getPersistence().cacheResult(badColumnNameEntry);
	}

	/**
	 * Caches the bad column name entries in the entity cache if it is enabled.
	 *
	 * @param badColumnNameEntries the bad column name entries
	 */
	public static void cacheResult(
		List<BadColumnNameEntry> badColumnNameEntries) {

		getPersistence().cacheResult(badColumnNameEntries);
	}

	/**
	 * Creates a new bad column name entry with the primary key. Does not add the bad column name entry to the database.
	 *
	 * @param badColumnNameEntryId the primary key for the new bad column name entry
	 * @return the new bad column name entry
	 */
	public static BadColumnNameEntry create(long badColumnNameEntryId) {
		return getPersistence().create(badColumnNameEntryId);
	}

	/**
	 * Removes the bad column name entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param badColumnNameEntryId the primary key of the bad column name entry
	 * @return the bad column name entry that was removed
	 * @throws NoSuchBadColumnNameEntryException if a bad column name entry with the primary key could not be found
	 */
	public static BadColumnNameEntry remove(long badColumnNameEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchBadColumnNameEntryException {

		return getPersistence().remove(badColumnNameEntryId);
	}

	public static BadColumnNameEntry updateImpl(
		BadColumnNameEntry badColumnNameEntry) {

		return getPersistence().updateImpl(badColumnNameEntry);
	}

	/**
	 * Returns the bad column name entry with the primary key or throws a <code>NoSuchBadColumnNameEntryException</code> if it could not be found.
	 *
	 * @param badColumnNameEntryId the primary key of the bad column name entry
	 * @return the bad column name entry
	 * @throws NoSuchBadColumnNameEntryException if a bad column name entry with the primary key could not be found
	 */
	public static BadColumnNameEntry findByPrimaryKey(long badColumnNameEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchBadColumnNameEntryException {

		return getPersistence().findByPrimaryKey(badColumnNameEntryId);
	}

	/**
	 * Returns the bad column name entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param badColumnNameEntryId the primary key of the bad column name entry
	 * @return the bad column name entry, or <code>null</code> if a bad column name entry with the primary key could not be found
	 */
	public static BadColumnNameEntry fetchByPrimaryKey(
		long badColumnNameEntryId) {

		return getPersistence().fetchByPrimaryKey(badColumnNameEntryId);
	}

	/**
	 * Returns all the bad column name entries.
	 *
	 * @return the bad column name entries
	 */
	public static List<BadColumnNameEntry> findAll() {
		return getPersistence().findAll();
	}

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
	public static List<BadColumnNameEntry> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

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
	public static List<BadColumnNameEntry> findAll(
		int start, int end,
		OrderByComparator<BadColumnNameEntry> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

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
	public static List<BadColumnNameEntry> findAll(
		int start, int end,
		OrderByComparator<BadColumnNameEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the bad column name entries from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of bad column name entries.
	 *
	 * @return the number of bad column name entries
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static BadColumnNameEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		BadColumnNameEntryPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile BadColumnNameEntryPersistence _persistence;

}
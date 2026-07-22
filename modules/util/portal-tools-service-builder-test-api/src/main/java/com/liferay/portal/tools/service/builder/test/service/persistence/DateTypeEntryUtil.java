/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.DateTypeEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the date type entry service. This utility wraps <code>com.liferay.portal.tools.service.builder.test.service.persistence.impl.DateTypeEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DateTypeEntryPersistence
 * @generated
 */
public class DateTypeEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<DateTypeEntry> dateTypeEntries) {
		getPersistence().cacheResult(dateTypeEntries);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(DateTypeEntry dateTypeEntry) {
		getPersistence().cacheResult(dateTypeEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(DateTypeEntry dateTypeEntry) {
		getPersistence().clearCache(dateTypeEntry);
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
	public static Map<Serializable, DateTypeEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<DateTypeEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<DateTypeEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<DateTypeEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<DateTypeEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static DateTypeEntry update(DateTypeEntry dateTypeEntry) {
		return getPersistence().update(dateTypeEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static DateTypeEntry update(
		DateTypeEntry dateTypeEntry, ServiceContext serviceContext) {

		return getPersistence().update(dateTypeEntry, serviceContext);
	}

	/**
	 * Creates a new date type entry with the primary key. Does not add the date type entry to the database.
	 *
	 * @param dateTypeEntryId the primary key for the new date type entry
	 * @return the new date type entry
	 */
	public static DateTypeEntry create(long dateTypeEntryId) {
		return getPersistence().create(dateTypeEntryId);
	}

	/**
	 * Removes the date type entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dateTypeEntryId the primary key of the date type entry
	 * @return the date type entry that was removed
	 * @throws NoSuchDateTypeEntryException if a date type entry with the primary key could not be found
	 */
	public static DateTypeEntry remove(long dateTypeEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchDateTypeEntryException {

		return getPersistence().remove(dateTypeEntryId);
	}

	public static DateTypeEntry updateImpl(DateTypeEntry dateTypeEntry) {
		return getPersistence().updateImpl(dateTypeEntry);
	}

	/**
	 * Returns the date type entry with the primary key or throws a <code>NoSuchDateTypeEntryException</code> if it could not be found.
	 *
	 * @param dateTypeEntryId the primary key of the date type entry
	 * @return the date type entry
	 * @throws NoSuchDateTypeEntryException if a date type entry with the primary key could not be found
	 */
	public static DateTypeEntry findByPrimaryKey(long dateTypeEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchDateTypeEntryException {

		return getPersistence().findByPrimaryKey(dateTypeEntryId);
	}

	/**
	 * Returns the date type entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param dateTypeEntryId the primary key of the date type entry
	 * @return the date type entry, or <code>null</code> if a date type entry with the primary key could not be found
	 */
	public static DateTypeEntry fetchByPrimaryKey(long dateTypeEntryId) {
		return getPersistence().fetchByPrimaryKey(dateTypeEntryId);
	}

	public static DateTypeEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(DateTypeEntryPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile DateTypeEntryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-107738323
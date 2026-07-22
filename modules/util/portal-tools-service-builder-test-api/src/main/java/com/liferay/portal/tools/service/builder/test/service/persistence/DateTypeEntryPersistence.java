/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchDateTypeEntryException;
import com.liferay.portal.tools.service.builder.test.model.DateTypeEntry;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the date type entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DateTypeEntryUtil
 * @generated
 */
@ProviderType
public interface DateTypeEntryPersistence
	extends BasePersistence<DateTypeEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link DateTypeEntryUtil} to access the date type entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Creates a new date type entry with the primary key. Does not add the date type entry to the database.
	 *
	 * @param dateTypeEntryId the primary key for the new date type entry
	 * @return the new date type entry
	 */
	public DateTypeEntry create(long dateTypeEntryId);

	/**
	 * Removes the date type entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dateTypeEntryId the primary key of the date type entry
	 * @return the date type entry that was removed
	 * @throws NoSuchDateTypeEntryException if a date type entry with the primary key could not be found
	 */
	public DateTypeEntry remove(long dateTypeEntryId)
		throws NoSuchDateTypeEntryException;

	public DateTypeEntry updateImpl(DateTypeEntry dateTypeEntry);

	/**
	 * Returns the date type entry with the primary key or throws a <code>NoSuchDateTypeEntryException</code> if it could not be found.
	 *
	 * @param dateTypeEntryId the primary key of the date type entry
	 * @return the date type entry
	 * @throws NoSuchDateTypeEntryException if a date type entry with the primary key could not be found
	 */
	public DateTypeEntry findByPrimaryKey(long dateTypeEntryId)
		throws NoSuchDateTypeEntryException;

	/**
	 * Returns the date type entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param dateTypeEntryId the primary key of the date type entry
	 * @return the date type entry, or <code>null</code> if a date type entry with the primary key could not be found
	 */
	public DateTypeEntry fetchByPrimaryKey(long dateTypeEntryId);

}
// LIFERAY-SERVICE-BUILDER-HASH:218068056
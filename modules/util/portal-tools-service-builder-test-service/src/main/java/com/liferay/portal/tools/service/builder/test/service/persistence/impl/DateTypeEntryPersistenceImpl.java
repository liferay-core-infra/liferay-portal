/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchDateTypeEntryException;
import com.liferay.portal.tools.service.builder.test.model.DateTypeEntry;
import com.liferay.portal.tools.service.builder.test.model.DateTypeEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.DateTypeEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.DateTypeEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.DateTypeEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.DateTypeEntryUtil;

import java.io.Serializable;

import java.util.Map;

/**
 * The persistence implementation for the date type entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DateTypeEntryPersistenceImpl
	extends BasePersistenceImpl<DateTypeEntry, NoSuchDateTypeEntryException>
	implements DateTypeEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DateTypeEntryUtil</code> to access the date type entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DateTypeEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	public DateTypeEntryPersistenceImpl() {
		setModelClass(DateTypeEntry.class);

		setModelImplClass(DateTypeEntryImpl.class);
		setModelPKClass(long.class);

		setTable(DateTypeEntryTable.INSTANCE);
	}

	/**
	 * Creates a new date type entry with the primary key. Does not add the date type entry to the database.
	 *
	 * @param dateTypeEntryId the primary key for the new date type entry
	 * @return the new date type entry
	 */
	@Override
	public DateTypeEntry create(long dateTypeEntryId) {
		DateTypeEntry dateTypeEntry = new DateTypeEntryImpl();

		dateTypeEntry.setNew(true);
		dateTypeEntry.setPrimaryKey(dateTypeEntryId);

		return dateTypeEntry;
	}

	/**
	 * Removes the date type entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dateTypeEntryId the primary key of the date type entry
	 * @return the date type entry that was removed
	 * @throws NoSuchDateTypeEntryException if a date type entry with the primary key could not be found
	 */
	@Override
	public DateTypeEntry remove(long dateTypeEntryId)
		throws NoSuchDateTypeEntryException {

		return remove((Serializable)dateTypeEntryId);
	}

	@Override
	protected DateTypeEntry removeImpl(DateTypeEntry dateTypeEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dateTypeEntry)) {
				dateTypeEntry = (DateTypeEntry)session.get(
					DateTypeEntryImpl.class, dateTypeEntry.getPrimaryKeyObj());
			}

			if (dateTypeEntry != null) {
				session.delete(dateTypeEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dateTypeEntry != null) {
			clearCache(dateTypeEntry);
		}

		return dateTypeEntry;
	}

	@Override
	public DateTypeEntry updateImpl(DateTypeEntry dateTypeEntry) {
		boolean isNew = dateTypeEntry.isNew();

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(dateTypeEntry);
			}
			else {
				dateTypeEntry = (DateTypeEntry)session.merge(dateTypeEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(dateTypeEntry, false);

		if (isNew) {
			dateTypeEntry.setNew(false);
		}

		dateTypeEntry.resetOriginalValues();

		return dateTypeEntry;
	}

	/**
	 * Returns the date type entry with the primary key or throws a <code>NoSuchDateTypeEntryException</code> if it could not be found.
	 *
	 * @param dateTypeEntryId the primary key of the date type entry
	 * @return the date type entry
	 * @throws NoSuchDateTypeEntryException if a date type entry with the primary key could not be found
	 */
	@Override
	public DateTypeEntry findByPrimaryKey(long dateTypeEntryId)
		throws NoSuchDateTypeEntryException {

		return findByPrimaryKey((Serializable)dateTypeEntryId);
	}

	/**
	 * Returns the date type entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param dateTypeEntryId the primary key of the date type entry
	 * @return the date type entry, or <code>null</code> if a date type entry with the primary key could not be found
	 */
	@Override
	public DateTypeEntry fetchByPrimaryKey(long dateTypeEntryId) {
		return fetchByPrimaryKey((Serializable)dateTypeEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "dateTypeEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DATETYPEENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return DateTypeEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the date type entry persistence.
	 */
	public void afterPropertiesSet() {
		DateTypeEntryUtil.setPersistence(this);
	}

	public void destroy() {
		DateTypeEntryUtil.setPersistence(null);

		entityCache.removeCache(DateTypeEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_DATETYPEENTRY =
		"SELECT dateTypeEntry FROM DateTypeEntry dateTypeEntry";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:128970251
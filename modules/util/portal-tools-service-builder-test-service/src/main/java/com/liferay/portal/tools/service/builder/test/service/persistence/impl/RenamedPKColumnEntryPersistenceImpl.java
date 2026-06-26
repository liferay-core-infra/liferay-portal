/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchRenamedPKColumnEntryException;
import com.liferay.portal.tools.service.builder.test.model.RenamedPKColumnEntry;
import com.liferay.portal.tools.service.builder.test.model.RenamedPKColumnEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.RenamedPKColumnEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.RenamedPKColumnEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.RenamedPKColumnEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.RenamedPKColumnEntryUtil;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the renamed pk column entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class RenamedPKColumnEntryPersistenceImpl
	extends BasePersistenceImpl
		<RenamedPKColumnEntry, NoSuchRenamedPKColumnEntryException>
	implements RenamedPKColumnEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>RenamedPKColumnEntryUtil</code> to access the renamed pk column entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		RenamedPKColumnEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	public RenamedPKColumnEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("renamedPKColumnEntryId", "rPKColumnEntryId");

		setDBColumnNames(dbColumnNames);

		setModelClass(RenamedPKColumnEntry.class);

		setModelImplClass(RenamedPKColumnEntryImpl.class);
		setModelPKClass(long.class);

		setTable(RenamedPKColumnEntryTable.INSTANCE);
	}

	/**
	 * Creates a new renamed pk column entry with the primary key. Does not add the renamed pk column entry to the database.
	 *
	 * @param renamedPKColumnEntryId the primary key for the new renamed pk column entry
	 * @return the new renamed pk column entry
	 */
	@Override
	public RenamedPKColumnEntry create(long renamedPKColumnEntryId) {
		RenamedPKColumnEntry renamedPKColumnEntry =
			new RenamedPKColumnEntryImpl();

		renamedPKColumnEntry.setNew(true);
		renamedPKColumnEntry.setPrimaryKey(renamedPKColumnEntryId);

		return renamedPKColumnEntry;
	}

	/**
	 * Removes the renamed pk column entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param renamedPKColumnEntryId the primary key of the renamed pk column entry
	 * @return the renamed pk column entry that was removed
	 * @throws NoSuchRenamedPKColumnEntryException if a renamed pk column entry with the primary key could not be found
	 */
	@Override
	public RenamedPKColumnEntry remove(long renamedPKColumnEntryId)
		throws NoSuchRenamedPKColumnEntryException {

		return remove((Serializable)renamedPKColumnEntryId);
	}

	@Override
	protected RenamedPKColumnEntry removeImpl(
		RenamedPKColumnEntry renamedPKColumnEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(renamedPKColumnEntry)) {
				renamedPKColumnEntry = (RenamedPKColumnEntry)session.get(
					RenamedPKColumnEntryImpl.class,
					renamedPKColumnEntry.getPrimaryKeyObj());
			}

			if (renamedPKColumnEntry != null) {
				session.delete(renamedPKColumnEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (renamedPKColumnEntry != null) {
			clearCache(renamedPKColumnEntry);
		}

		return renamedPKColumnEntry;
	}

	@Override
	public RenamedPKColumnEntry updateImpl(
		RenamedPKColumnEntry renamedPKColumnEntry) {

		boolean isNew = renamedPKColumnEntry.isNew();

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(renamedPKColumnEntry);
			}
			else {
				renamedPKColumnEntry = (RenamedPKColumnEntry)session.merge(
					renamedPKColumnEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(renamedPKColumnEntry, false);

		if (isNew) {
			renamedPKColumnEntry.setNew(false);
		}

		renamedPKColumnEntry.resetOriginalValues();

		return renamedPKColumnEntry;
	}

	/**
	 * Returns the renamed pk column entry with the primary key or throws a <code>NoSuchRenamedPKColumnEntryException</code> if it could not be found.
	 *
	 * @param renamedPKColumnEntryId the primary key of the renamed pk column entry
	 * @return the renamed pk column entry
	 * @throws NoSuchRenamedPKColumnEntryException if a renamed pk column entry with the primary key could not be found
	 */
	@Override
	public RenamedPKColumnEntry findByPrimaryKey(long renamedPKColumnEntryId)
		throws NoSuchRenamedPKColumnEntryException {

		return findByPrimaryKey((Serializable)renamedPKColumnEntryId);
	}

	/**
	 * Returns the renamed pk column entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param renamedPKColumnEntryId the primary key of the renamed pk column entry
	 * @return the renamed pk column entry, or <code>null</code> if a renamed pk column entry with the primary key could not be found
	 */
	@Override
	public RenamedPKColumnEntry fetchByPrimaryKey(long renamedPKColumnEntryId) {
		return fetchByPrimaryKey((Serializable)renamedPKColumnEntryId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return dummyEntityCache;
	}

	@Override
	protected String getPKDBName() {
		return "rPKColumnEntryId";
	}

	@Override
	protected String getPKFieldName() {
		return "renamedPKColumnEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_RENAMEDPKCOLUMNENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return RenamedPKColumnEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the renamed pk column entry persistence.
	 */
	public void afterPropertiesSet() {
		RenamedPKColumnEntryUtil.setPersistence(this);
	}

	public void destroy() {
		RenamedPKColumnEntryUtil.setPersistence(null);

		dummyEntityCache.removeCache(RenamedPKColumnEntryImpl.class.getName());
	}

	private static final String _SQL_SELECT_RENAMEDPKCOLUMNENTRY =
		"SELECT renamedPKColumnEntry FROM RenamedPKColumnEntry renamedPKColumnEntry";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"renamedPKColumnEntryId"});

	@Override
	protected FinderCache getFinderCache() {
		return dummyFinderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:151509058
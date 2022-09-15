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

package com.liferay.portal.tools.service.builder.test.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.FinderCachePopulationEntry;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for FinderCachePopulationEntry. This utility wraps
 * <code>com.liferay.portal.tools.service.builder.test.service.impl.FinderCachePopulationEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see FinderCachePopulationEntryLocalService
 * @generated
 */
public class FinderCachePopulationEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.tools.service.builder.test.service.impl.FinderCachePopulationEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the finder cache population entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FinderCachePopulationEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param finderCachePopulationEntry the finder cache population entry
	 * @return the finder cache population entry that was added
	 */
	public static FinderCachePopulationEntry addFinderCachePopulationEntry(
		FinderCachePopulationEntry finderCachePopulationEntry) {

		return getService().addFinderCachePopulationEntry(
			finderCachePopulationEntry);
	}

	/**
	 * Creates a new finder cache population entry with the primary key. Does not add the finder cache population entry to the database.
	 *
	 * @param pinderCachePopulationEntryId the primary key for the new finder cache population entry
	 * @return the new finder cache population entry
	 */
	public static FinderCachePopulationEntry createFinderCachePopulationEntry(
		long pinderCachePopulationEntryId) {

		return getService().createFinderCachePopulationEntry(
			pinderCachePopulationEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the finder cache population entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FinderCachePopulationEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param finderCachePopulationEntry the finder cache population entry
	 * @return the finder cache population entry that was removed
	 */
	public static FinderCachePopulationEntry deleteFinderCachePopulationEntry(
		FinderCachePopulationEntry finderCachePopulationEntry) {

		return getService().deleteFinderCachePopulationEntry(
			finderCachePopulationEntry);
	}

	/**
	 * Deletes the finder cache population entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FinderCachePopulationEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param pinderCachePopulationEntryId the primary key of the finder cache population entry
	 * @return the finder cache population entry that was removed
	 * @throws PortalException if a finder cache population entry with the primary key could not be found
	 */
	public static FinderCachePopulationEntry deleteFinderCachePopulationEntry(
			long pinderCachePopulationEntryId)
		throws PortalException {

		return getService().deleteFinderCachePopulationEntry(
			pinderCachePopulationEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderCachePopulationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderCachePopulationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static FinderCachePopulationEntry fetchFinderCachePopulationEntry(
		long pinderCachePopulationEntryId) {

		return getService().fetchFinderCachePopulationEntry(
			pinderCachePopulationEntryId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the finder cache population entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderCachePopulationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of finder cache population entries
	 * @param end the upper bound of the range of finder cache population entries (not inclusive)
	 * @return the range of finder cache population entries
	 */
	public static List<FinderCachePopulationEntry>
		getFinderCachePopulationEntries(int start, int end) {

		return getService().getFinderCachePopulationEntries(start, end);
	}

	/**
	 * Returns the number of finder cache population entries.
	 *
	 * @return the number of finder cache population entries
	 */
	public static int getFinderCachePopulationEntriesCount() {
		return getService().getFinderCachePopulationEntriesCount();
	}

	/**
	 * Returns the finder cache population entry with the primary key.
	 *
	 * @param pinderCachePopulationEntryId the primary key of the finder cache population entry
	 * @return the finder cache population entry
	 * @throws PortalException if a finder cache population entry with the primary key could not be found
	 */
	public static FinderCachePopulationEntry getFinderCachePopulationEntry(
			long pinderCachePopulationEntryId)
		throws PortalException {

		return getService().getFinderCachePopulationEntry(
			pinderCachePopulationEntryId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the finder cache population entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FinderCachePopulationEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param finderCachePopulationEntry the finder cache population entry
	 * @return the finder cache population entry that was updated
	 */
	public static FinderCachePopulationEntry updateFinderCachePopulationEntry(
		FinderCachePopulationEntry finderCachePopulationEntry) {

		return getService().updateFinderCachePopulationEntry(
			finderCachePopulationEntry);
	}

	public static FinderCachePopulationEntryLocalService getService() {
		return _service;
	}

	private static volatile FinderCachePopulationEntryLocalService _service;

}
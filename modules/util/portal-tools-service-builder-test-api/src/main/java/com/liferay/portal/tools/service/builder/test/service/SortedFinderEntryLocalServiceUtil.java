/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.SortedFinderEntry;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for SortedFinderEntry. This utility wraps
 * <code>com.liferay.portal.tools.service.builder.test.service.impl.SortedFinderEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see SortedFinderEntryLocalService
 * @generated
 */
public class SortedFinderEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.tools.service.builder.test.service.impl.SortedFinderEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the sorted finder entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SortedFinderEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sortedFinderEntry the sorted finder entry
	 * @return the sorted finder entry that was added
	 */
	public static SortedFinderEntry addSortedFinderEntry(
		SortedFinderEntry sortedFinderEntry) {

		return getService().addSortedFinderEntry(sortedFinderEntry);
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
	 * Creates a new sorted finder entry with the primary key. Does not add the sorted finder entry to the database.
	 *
	 * @param sortedFinderEntryId the primary key for the new sorted finder entry
	 * @return the new sorted finder entry
	 */
	public static SortedFinderEntry createSortedFinderEntry(
		long sortedFinderEntryId) {

		return getService().createSortedFinderEntry(sortedFinderEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the sorted finder entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SortedFinderEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sortedFinderEntryId the primary key of the sorted finder entry
	 * @return the sorted finder entry that was removed
	 * @throws PortalException if a sorted finder entry with the primary key could not be found
	 */
	public static SortedFinderEntry deleteSortedFinderEntry(
			long sortedFinderEntryId)
		throws PortalException {

		return getService().deleteSortedFinderEntry(sortedFinderEntryId);
	}

	/**
	 * Deletes the sorted finder entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SortedFinderEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sortedFinderEntry the sorted finder entry
	 * @return the sorted finder entry that was removed
	 */
	public static SortedFinderEntry deleteSortedFinderEntry(
		SortedFinderEntry sortedFinderEntry) {

		return getService().deleteSortedFinderEntry(sortedFinderEntry);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.SortedFinderEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.SortedFinderEntryModelImpl</code>.
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

	public static SortedFinderEntry fetchSortedFinderEntry(
		long sortedFinderEntryId) {

		return getService().fetchSortedFinderEntry(sortedFinderEntryId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
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
	 * Returns a range of all the sorted finder entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.SortedFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of sorted finder entries
	 * @param end the upper bound of the range of sorted finder entries (not inclusive)
	 * @return the range of sorted finder entries
	 */
	public static List<SortedFinderEntry> getSortedFinderEntries(
		int start, int end) {

		return getService().getSortedFinderEntries(start, end);
	}

	/**
	 * Returns the number of sorted finder entries.
	 *
	 * @return the number of sorted finder entries
	 */
	public static int getSortedFinderEntriesCount() {
		return getService().getSortedFinderEntriesCount();
	}

	/**
	 * Returns the sorted finder entry with the primary key.
	 *
	 * @param sortedFinderEntryId the primary key of the sorted finder entry
	 * @return the sorted finder entry
	 * @throws PortalException if a sorted finder entry with the primary key could not be found
	 */
	public static SortedFinderEntry getSortedFinderEntry(
			long sortedFinderEntryId)
		throws PortalException {

		return getService().getSortedFinderEntry(sortedFinderEntryId);
	}

	/**
	 * Updates the sorted finder entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SortedFinderEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sortedFinderEntry the sorted finder entry
	 * @return the sorted finder entry that was updated
	 */
	public static SortedFinderEntry updateSortedFinderEntry(
		SortedFinderEntry sortedFinderEntry) {

		return getService().updateSortedFinderEntry(sortedFinderEntry);
	}

	public static SortedFinderEntryLocalService getService() {
		return _service;
	}

	public static void setService(SortedFinderEntryLocalService service) {
		_service = service;
	}

	private static volatile SortedFinderEntryLocalService _service;

}
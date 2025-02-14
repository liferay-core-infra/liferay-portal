/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link SortedFinderEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see SortedFinderEntryLocalService
 * @generated
 */
public class SortedFinderEntryLocalServiceWrapper
	implements ServiceWrapper<SortedFinderEntryLocalService>,
			   SortedFinderEntryLocalService {

	public SortedFinderEntryLocalServiceWrapper() {
		this(null);
	}

	public SortedFinderEntryLocalServiceWrapper(
		SortedFinderEntryLocalService sortedFinderEntryLocalService) {

		_sortedFinderEntryLocalService = sortedFinderEntryLocalService;
	}

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
	@Override
	public com.liferay.portal.tools.service.builder.test.model.SortedFinderEntry
		addSortedFinderEntry(
			com.liferay.portal.tools.service.builder.test.model.
				SortedFinderEntry sortedFinderEntry) {

		return _sortedFinderEntryLocalService.addSortedFinderEntry(
			sortedFinderEntry);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sortedFinderEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new sorted finder entry with the primary key. Does not add the sorted finder entry to the database.
	 *
	 * @param sortedFinderEntryId the primary key for the new sorted finder entry
	 * @return the new sorted finder entry
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.SortedFinderEntry
		createSortedFinderEntry(long sortedFinderEntryId) {

		return _sortedFinderEntryLocalService.createSortedFinderEntry(
			sortedFinderEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sortedFinderEntryLocalService.deletePersistedModel(
			persistedModel);
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
	@Override
	public com.liferay.portal.tools.service.builder.test.model.SortedFinderEntry
			deleteSortedFinderEntry(long sortedFinderEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sortedFinderEntryLocalService.deleteSortedFinderEntry(
			sortedFinderEntryId);
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
	@Override
	public com.liferay.portal.tools.service.builder.test.model.SortedFinderEntry
		deleteSortedFinderEntry(
			com.liferay.portal.tools.service.builder.test.model.
				SortedFinderEntry sortedFinderEntry) {

		return _sortedFinderEntryLocalService.deleteSortedFinderEntry(
			sortedFinderEntry);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _sortedFinderEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _sortedFinderEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _sortedFinderEntryLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _sortedFinderEntryLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _sortedFinderEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _sortedFinderEntryLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _sortedFinderEntryLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _sortedFinderEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.SortedFinderEntry
		fetchSortedFinderEntry(long sortedFinderEntryId) {

		return _sortedFinderEntryLocalService.fetchSortedFinderEntry(
			sortedFinderEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _sortedFinderEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _sortedFinderEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _sortedFinderEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sortedFinderEntryLocalService.getPersistedModel(primaryKeyObj);
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
	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.SortedFinderEntry>
			getSortedFinderEntries(int start, int end) {

		return _sortedFinderEntryLocalService.getSortedFinderEntries(
			start, end);
	}

	/**
	 * Returns the number of sorted finder entries.
	 *
	 * @return the number of sorted finder entries
	 */
	@Override
	public int getSortedFinderEntriesCount() {
		return _sortedFinderEntryLocalService.getSortedFinderEntriesCount();
	}

	/**
	 * Returns the sorted finder entry with the primary key.
	 *
	 * @param sortedFinderEntryId the primary key of the sorted finder entry
	 * @return the sorted finder entry
	 * @throws PortalException if a sorted finder entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.SortedFinderEntry
			getSortedFinderEntry(long sortedFinderEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sortedFinderEntryLocalService.getSortedFinderEntry(
			sortedFinderEntryId);
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
	@Override
	public com.liferay.portal.tools.service.builder.test.model.SortedFinderEntry
		updateSortedFinderEntry(
			com.liferay.portal.tools.service.builder.test.model.
				SortedFinderEntry sortedFinderEntry) {

		return _sortedFinderEntryLocalService.updateSortedFinderEntry(
			sortedFinderEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _sortedFinderEntryLocalService.getBasePersistence();
	}

	@Override
	public SortedFinderEntryLocalService getWrappedService() {
		return _sortedFinderEntryLocalService;
	}

	@Override
	public void setWrappedService(
		SortedFinderEntryLocalService sortedFinderEntryLocalService) {

		_sortedFinderEntryLocalService = sortedFinderEntryLocalService;
	}

	private SortedFinderEntryLocalService _sortedFinderEntryLocalService;

}
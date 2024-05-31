/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link FilterFindEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see FilterFindEntryLocalService
 * @generated
 */
public class FilterFindEntryLocalServiceWrapper
	implements FilterFindEntryLocalService,
			   ServiceWrapper<FilterFindEntryLocalService> {

	public FilterFindEntryLocalServiceWrapper() {
		this(null);
	}

	public FilterFindEntryLocalServiceWrapper(
		FilterFindEntryLocalService filterFindEntryLocalService) {

		_filterFindEntryLocalService = filterFindEntryLocalService;
	}

	/**
	 * Adds the filter find entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FilterFindEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param filterFindEntry the filter find entry
	 * @return the filter find entry that was added
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.FilterFindEntry
		addFilterFindEntry(
			com.liferay.portal.tools.service.builder.test.model.FilterFindEntry
				filterFindEntry) {

		return _filterFindEntryLocalService.addFilterFindEntry(filterFindEntry);
	}

	/**
	 * Creates a new filter find entry with the primary key. Does not add the filter find entry to the database.
	 *
	 * @param filterFindEntryId the primary key for the new filter find entry
	 * @return the new filter find entry
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.FilterFindEntry
		createFilterFindEntry(long filterFindEntryId) {

		return _filterFindEntryLocalService.createFilterFindEntry(
			filterFindEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _filterFindEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the filter find entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FilterFindEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param filterFindEntry the filter find entry
	 * @return the filter find entry that was removed
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.FilterFindEntry
		deleteFilterFindEntry(
			com.liferay.portal.tools.service.builder.test.model.FilterFindEntry
				filterFindEntry) {

		return _filterFindEntryLocalService.deleteFilterFindEntry(
			filterFindEntry);
	}

	/**
	 * Deletes the filter find entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FilterFindEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param filterFindEntryId the primary key of the filter find entry
	 * @return the filter find entry that was removed
	 * @throws PortalException if a filter find entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.FilterFindEntry
			deleteFilterFindEntry(long filterFindEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _filterFindEntryLocalService.deleteFilterFindEntry(
			filterFindEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _filterFindEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _filterFindEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _filterFindEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _filterFindEntryLocalService.dynamicQuery();
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

		return _filterFindEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FilterFindEntryModelImpl</code>.
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

		return _filterFindEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FilterFindEntryModelImpl</code>.
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

		return _filterFindEntryLocalService.dynamicQuery(
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

		return _filterFindEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _filterFindEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.FilterFindEntry
		fetchFilterFindEntry(long filterFindEntryId) {

		return _filterFindEntryLocalService.fetchFilterFindEntry(
			filterFindEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _filterFindEntryLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the filter find entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FilterFindEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of filter find entries
	 * @param end the upper bound of the range of filter find entries (not inclusive)
	 * @return the range of filter find entries
	 */
	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.FilterFindEntry>
			getFilterFindEntries(int start, int end) {

		return _filterFindEntryLocalService.getFilterFindEntries(start, end);
	}

	/**
	 * Returns the number of filter find entries.
	 *
	 * @return the number of filter find entries
	 */
	@Override
	public int getFilterFindEntriesCount() {
		return _filterFindEntryLocalService.getFilterFindEntriesCount();
	}

	/**
	 * Returns the filter find entry with the primary key.
	 *
	 * @param filterFindEntryId the primary key of the filter find entry
	 * @return the filter find entry
	 * @throws PortalException if a filter find entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.FilterFindEntry
			getFilterFindEntry(long filterFindEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _filterFindEntryLocalService.getFilterFindEntry(
			filterFindEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _filterFindEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _filterFindEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _filterFindEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the filter find entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FilterFindEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param filterFindEntry the filter find entry
	 * @return the filter find entry that was updated
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.FilterFindEntry
		updateFilterFindEntry(
			com.liferay.portal.tools.service.builder.test.model.FilterFindEntry
				filterFindEntry) {

		return _filterFindEntryLocalService.updateFilterFindEntry(
			filterFindEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _filterFindEntryLocalService.getBasePersistence();
	}

	@Override
	public FilterFindEntryLocalService getWrappedService() {
		return _filterFindEntryLocalService;
	}

	@Override
	public void setWrappedService(
		FilterFindEntryLocalService filterFindEntryLocalService) {

		_filterFindEntryLocalService = filterFindEntryLocalService;
	}

	private FilterFindEntryLocalService _filterFindEntryLocalService;

}
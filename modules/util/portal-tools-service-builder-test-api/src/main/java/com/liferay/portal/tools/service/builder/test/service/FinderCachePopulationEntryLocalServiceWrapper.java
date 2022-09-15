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

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link FinderCachePopulationEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see FinderCachePopulationEntryLocalService
 * @generated
 */
public class FinderCachePopulationEntryLocalServiceWrapper
	implements FinderCachePopulationEntryLocalService,
			   ServiceWrapper<FinderCachePopulationEntryLocalService> {

	public FinderCachePopulationEntryLocalServiceWrapper() {
		this(null);
	}

	public FinderCachePopulationEntryLocalServiceWrapper(
		FinderCachePopulationEntryLocalService
			finderCachePopulationEntryLocalService) {

		_finderCachePopulationEntryLocalService =
			finderCachePopulationEntryLocalService;
	}

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
	@Override
	public com.liferay.portal.tools.service.builder.test.model.
		FinderCachePopulationEntry addFinderCachePopulationEntry(
			com.liferay.portal.tools.service.builder.test.model.
				FinderCachePopulationEntry finderCachePopulationEntry) {

		return _finderCachePopulationEntryLocalService.
			addFinderCachePopulationEntry(finderCachePopulationEntry);
	}

	/**
	 * Creates a new finder cache population entry with the primary key. Does not add the finder cache population entry to the database.
	 *
	 * @param pinderCachePopulationEntryId the primary key for the new finder cache population entry
	 * @return the new finder cache population entry
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.
		FinderCachePopulationEntry createFinderCachePopulationEntry(
			long pinderCachePopulationEntryId) {

		return _finderCachePopulationEntryLocalService.
			createFinderCachePopulationEntry(pinderCachePopulationEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _finderCachePopulationEntryLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public com.liferay.portal.tools.service.builder.test.model.
		FinderCachePopulationEntry deleteFinderCachePopulationEntry(
			com.liferay.portal.tools.service.builder.test.model.
				FinderCachePopulationEntry finderCachePopulationEntry) {

		return _finderCachePopulationEntryLocalService.
			deleteFinderCachePopulationEntry(finderCachePopulationEntry);
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
	@Override
	public com.liferay.portal.tools.service.builder.test.model.
		FinderCachePopulationEntry deleteFinderCachePopulationEntry(
				long pinderCachePopulationEntryId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _finderCachePopulationEntryLocalService.
			deleteFinderCachePopulationEntry(pinderCachePopulationEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _finderCachePopulationEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _finderCachePopulationEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _finderCachePopulationEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _finderCachePopulationEntryLocalService.dynamicQuery();
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

		return _finderCachePopulationEntryLocalService.dynamicQuery(
			dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _finderCachePopulationEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _finderCachePopulationEntryLocalService.dynamicQuery(
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

		return _finderCachePopulationEntryLocalService.dynamicQueryCount(
			dynamicQuery);
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

		return _finderCachePopulationEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.
		FinderCachePopulationEntry fetchFinderCachePopulationEntry(
			long pinderCachePopulationEntryId) {

		return _finderCachePopulationEntryLocalService.
			fetchFinderCachePopulationEntry(pinderCachePopulationEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _finderCachePopulationEntryLocalService.
			getActionableDynamicQuery();
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
	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.
			FinderCachePopulationEntry> getFinderCachePopulationEntries(
				int start, int end) {

		return _finderCachePopulationEntryLocalService.
			getFinderCachePopulationEntries(start, end);
	}

	/**
	 * Returns the number of finder cache population entries.
	 *
	 * @return the number of finder cache population entries
	 */
	@Override
	public int getFinderCachePopulationEntriesCount() {
		return _finderCachePopulationEntryLocalService.
			getFinderCachePopulationEntriesCount();
	}

	/**
	 * Returns the finder cache population entry with the primary key.
	 *
	 * @param pinderCachePopulationEntryId the primary key of the finder cache population entry
	 * @return the finder cache population entry
	 * @throws PortalException if a finder cache population entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.
		FinderCachePopulationEntry getFinderCachePopulationEntry(
				long pinderCachePopulationEntryId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _finderCachePopulationEntryLocalService.
			getFinderCachePopulationEntry(pinderCachePopulationEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _finderCachePopulationEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _finderCachePopulationEntryLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _finderCachePopulationEntryLocalService.getPersistedModel(
			primaryKeyObj);
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
	@Override
	public com.liferay.portal.tools.service.builder.test.model.
		FinderCachePopulationEntry updateFinderCachePopulationEntry(
			com.liferay.portal.tools.service.builder.test.model.
				FinderCachePopulationEntry finderCachePopulationEntry) {

		return _finderCachePopulationEntryLocalService.
			updateFinderCachePopulationEntry(finderCachePopulationEntry);
	}

	@Override
	public FinderCachePopulationEntryLocalService getWrappedService() {
		return _finderCachePopulationEntryLocalService;
	}

	@Override
	public void setWrappedService(
		FinderCachePopulationEntryLocalService
			finderCachePopulationEntryLocalService) {

		_finderCachePopulationEntryLocalService =
			finderCachePopulationEntryLocalService;
	}

	private FinderCachePopulationEntryLocalService
		_finderCachePopulationEntryLocalService;

}
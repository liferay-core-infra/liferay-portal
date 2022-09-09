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
 * Provides a wrapper for {@link EagerFinderCacheEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see EagerFinderCacheEntryLocalService
 * @generated
 */
public class EagerFinderCacheEntryLocalServiceWrapper
	implements EagerFinderCacheEntryLocalService,
			   ServiceWrapper<EagerFinderCacheEntryLocalService> {

	public EagerFinderCacheEntryLocalServiceWrapper() {
		this(null);
	}

	public EagerFinderCacheEntryLocalServiceWrapper(
		EagerFinderCacheEntryLocalService eagerFinderCacheEntryLocalService) {

		_eagerFinderCacheEntryLocalService = eagerFinderCacheEntryLocalService;
	}

	/**
	 * Adds the eager finder cache entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect EagerFinderCacheEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param eagerFinderCacheEntry the eager finder cache entry
	 * @return the eager finder cache entry that was added
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.
			EagerFinderCacheEntry addEagerFinderCacheEntry(
				com.liferay.portal.tools.service.builder.test.model.
					EagerFinderCacheEntry eagerFinderCacheEntry) {

		return _eagerFinderCacheEntryLocalService.addEagerFinderCacheEntry(
			eagerFinderCacheEntry);
	}

	/**
	 * Creates a new eager finder cache entry with the primary key. Does not add the eager finder cache entry to the database.
	 *
	 * @param eagerFinderCacheEntryId the primary key for the new eager finder cache entry
	 * @return the new eager finder cache entry
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.
			EagerFinderCacheEntry createEagerFinderCacheEntry(
				long eagerFinderCacheEntryId) {

		return _eagerFinderCacheEntryLocalService.createEagerFinderCacheEntry(
			eagerFinderCacheEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _eagerFinderCacheEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the eager finder cache entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect EagerFinderCacheEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param eagerFinderCacheEntry the eager finder cache entry
	 * @return the eager finder cache entry that was removed
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.
			EagerFinderCacheEntry deleteEagerFinderCacheEntry(
				com.liferay.portal.tools.service.builder.test.model.
					EagerFinderCacheEntry eagerFinderCacheEntry) {

		return _eagerFinderCacheEntryLocalService.deleteEagerFinderCacheEntry(
			eagerFinderCacheEntry);
	}

	/**
	 * Deletes the eager finder cache entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect EagerFinderCacheEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param eagerFinderCacheEntryId the primary key of the eager finder cache entry
	 * @return the eager finder cache entry that was removed
	 * @throws PortalException if a eager finder cache entry with the primary key could not be found
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.
			EagerFinderCacheEntry deleteEagerFinderCacheEntry(
					long eagerFinderCacheEntryId)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _eagerFinderCacheEntryLocalService.deleteEagerFinderCacheEntry(
			eagerFinderCacheEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _eagerFinderCacheEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _eagerFinderCacheEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _eagerFinderCacheEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _eagerFinderCacheEntryLocalService.dynamicQuery();
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

		return _eagerFinderCacheEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.EagerFinderCacheEntryModelImpl</code>.
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

		return _eagerFinderCacheEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.EagerFinderCacheEntryModelImpl</code>.
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

		return _eagerFinderCacheEntryLocalService.dynamicQuery(
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

		return _eagerFinderCacheEntryLocalService.dynamicQueryCount(
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

		return _eagerFinderCacheEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.
			EagerFinderCacheEntry fetchEagerFinderCacheEntry(
				long eagerFinderCacheEntryId) {

		return _eagerFinderCacheEntryLocalService.fetchEagerFinderCacheEntry(
			eagerFinderCacheEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _eagerFinderCacheEntryLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the eager finder cache entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.EagerFinderCacheEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of eager finder cache entries
	 * @param end the upper bound of the range of eager finder cache entries (not inclusive)
	 * @return the range of eager finder cache entries
	 */
	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.
			EagerFinderCacheEntry> getEagerFinderCacheEntries(
				int start, int end) {

		return _eagerFinderCacheEntryLocalService.getEagerFinderCacheEntries(
			start, end);
	}

	/**
	 * Returns the number of eager finder cache entries.
	 *
	 * @return the number of eager finder cache entries
	 */
	@Override
	public int getEagerFinderCacheEntriesCount() {
		return _eagerFinderCacheEntryLocalService.
			getEagerFinderCacheEntriesCount();
	}

	/**
	 * Returns the eager finder cache entry with the primary key.
	 *
	 * @param eagerFinderCacheEntryId the primary key of the eager finder cache entry
	 * @return the eager finder cache entry
	 * @throws PortalException if a eager finder cache entry with the primary key could not be found
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.
			EagerFinderCacheEntry getEagerFinderCacheEntry(
					long eagerFinderCacheEntryId)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _eagerFinderCacheEntryLocalService.getEagerFinderCacheEntry(
			eagerFinderCacheEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _eagerFinderCacheEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _eagerFinderCacheEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _eagerFinderCacheEntryLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the eager finder cache entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect EagerFinderCacheEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param eagerFinderCacheEntry the eager finder cache entry
	 * @return the eager finder cache entry that was updated
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.
			EagerFinderCacheEntry updateEagerFinderCacheEntry(
				com.liferay.portal.tools.service.builder.test.model.
					EagerFinderCacheEntry eagerFinderCacheEntry) {

		return _eagerFinderCacheEntryLocalService.updateEagerFinderCacheEntry(
			eagerFinderCacheEntry);
	}

	@Override
	public EagerFinderCacheEntryLocalService getWrappedService() {
		return _eagerFinderCacheEntryLocalService;
	}

	@Override
	public void setWrappedService(
		EagerFinderCacheEntryLocalService eagerFinderCacheEntryLocalService) {

		_eagerFinderCacheEntryLocalService = eagerFinderCacheEntryLocalService;
	}

	private EagerFinderCacheEntryLocalService
		_eagerFinderCacheEntryLocalService;

}
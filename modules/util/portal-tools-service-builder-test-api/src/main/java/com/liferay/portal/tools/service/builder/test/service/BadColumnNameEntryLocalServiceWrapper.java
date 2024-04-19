/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link BadColumnNameEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see BadColumnNameEntryLocalService
 * @generated
 */
public class BadColumnNameEntryLocalServiceWrapper
	implements BadColumnNameEntryLocalService,
			   ServiceWrapper<BadColumnNameEntryLocalService> {

	public BadColumnNameEntryLocalServiceWrapper() {
		this(null);
	}

	public BadColumnNameEntryLocalServiceWrapper(
		BadColumnNameEntryLocalService badColumnNameEntryLocalService) {

		_badColumnNameEntryLocalService = badColumnNameEntryLocalService;
	}

	/**
	 * Adds the bad column name entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BadColumnNameEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param badColumnNameEntry the bad column name entry
	 * @return the bad column name entry that was added
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.BadColumnNameEntry
			addBadColumnNameEntry(
				com.liferay.portal.tools.service.builder.test.model.
					BadColumnNameEntry badColumnNameEntry) {

		return _badColumnNameEntryLocalService.addBadColumnNameEntry(
			badColumnNameEntry);
	}

	/**
	 * Creates a new bad column name entry with the primary key. Does not add the bad column name entry to the database.
	 *
	 * @param badColumnNameEntryId the primary key for the new bad column name entry
	 * @return the new bad column name entry
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.BadColumnNameEntry
			createBadColumnNameEntry(long badColumnNameEntryId) {

		return _badColumnNameEntryLocalService.createBadColumnNameEntry(
			badColumnNameEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _badColumnNameEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the bad column name entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BadColumnNameEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param badColumnNameEntry the bad column name entry
	 * @return the bad column name entry that was removed
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.BadColumnNameEntry
			deleteBadColumnNameEntry(
				com.liferay.portal.tools.service.builder.test.model.
					BadColumnNameEntry badColumnNameEntry) {

		return _badColumnNameEntryLocalService.deleteBadColumnNameEntry(
			badColumnNameEntry);
	}

	/**
	 * Deletes the bad column name entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BadColumnNameEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param badColumnNameEntryId the primary key of the bad column name entry
	 * @return the bad column name entry that was removed
	 * @throws PortalException if a bad column name entry with the primary key could not be found
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.BadColumnNameEntry
				deleteBadColumnNameEntry(long badColumnNameEntryId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _badColumnNameEntryLocalService.deleteBadColumnNameEntry(
			badColumnNameEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _badColumnNameEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _badColumnNameEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _badColumnNameEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _badColumnNameEntryLocalService.dynamicQuery();
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

		return _badColumnNameEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.BadColumnNameEntryModelImpl</code>.
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

		return _badColumnNameEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.BadColumnNameEntryModelImpl</code>.
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

		return _badColumnNameEntryLocalService.dynamicQuery(
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

		return _badColumnNameEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _badColumnNameEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.BadColumnNameEntry
			fetchBadColumnNameEntry(long badColumnNameEntryId) {

		return _badColumnNameEntryLocalService.fetchBadColumnNameEntry(
			badColumnNameEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _badColumnNameEntryLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the bad column name entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.BadColumnNameEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of bad column name entries
	 * @param end the upper bound of the range of bad column name entries (not inclusive)
	 * @return the range of bad column name entries
	 */
	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.BadColumnNameEntry>
			getBadColumnNameEntries(int start, int end) {

		return _badColumnNameEntryLocalService.getBadColumnNameEntries(
			start, end);
	}

	/**
	 * Returns the number of bad column name entries.
	 *
	 * @return the number of bad column name entries
	 */
	@Override
	public int getBadColumnNameEntriesCount() {
		return _badColumnNameEntryLocalService.getBadColumnNameEntriesCount();
	}

	/**
	 * Returns the bad column name entry with the primary key.
	 *
	 * @param badColumnNameEntryId the primary key of the bad column name entry
	 * @return the bad column name entry
	 * @throws PortalException if a bad column name entry with the primary key could not be found
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.BadColumnNameEntry
				getBadColumnNameEntry(long badColumnNameEntryId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _badColumnNameEntryLocalService.getBadColumnNameEntry(
			badColumnNameEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _badColumnNameEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _badColumnNameEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _badColumnNameEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the bad column name entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BadColumnNameEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param badColumnNameEntry the bad column name entry
	 * @return the bad column name entry that was updated
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.BadColumnNameEntry
			updateBadColumnNameEntry(
				com.liferay.portal.tools.service.builder.test.model.
					BadColumnNameEntry badColumnNameEntry) {

		return _badColumnNameEntryLocalService.updateBadColumnNameEntry(
			badColumnNameEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _badColumnNameEntryLocalService.getBasePersistence();
	}

	@Override
	public BadColumnNameEntryLocalService getWrappedService() {
		return _badColumnNameEntryLocalService;
	}

	@Override
	public void setWrappedService(
		BadColumnNameEntryLocalService badColumnNameEntryLocalService) {

		_badColumnNameEntryLocalService = badColumnNameEntryLocalService;
	}

	private BadColumnNameEntryLocalService _badColumnNameEntryLocalService;

}
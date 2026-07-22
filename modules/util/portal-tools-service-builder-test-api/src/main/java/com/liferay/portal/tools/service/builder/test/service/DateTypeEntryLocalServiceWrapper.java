/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link DateTypeEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DateTypeEntryLocalService
 * @generated
 */
public class DateTypeEntryLocalServiceWrapper
	implements DateTypeEntryLocalService,
			   ServiceWrapper<DateTypeEntryLocalService> {

	public DateTypeEntryLocalServiceWrapper() {
		this(null);
	}

	public DateTypeEntryLocalServiceWrapper(
		DateTypeEntryLocalService dateTypeEntryLocalService) {

		_dateTypeEntryLocalService = dateTypeEntryLocalService;
	}

	/**
	 * Adds the date type entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DateTypeEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dateTypeEntry the date type entry
	 * @return the date type entry that was added
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.DateTypeEntry
		addDateTypeEntry(
			com.liferay.portal.tools.service.builder.test.model.DateTypeEntry
				dateTypeEntry) {

		return _dateTypeEntryLocalService.addDateTypeEntry(dateTypeEntry);
	}

	/**
	 * Creates a new date type entry with the primary key. Does not add the date type entry to the database.
	 *
	 * @param dateTypeEntryId the primary key for the new date type entry
	 * @return the new date type entry
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.DateTypeEntry
		createDateTypeEntry(long dateTypeEntryId) {

		return _dateTypeEntryLocalService.createDateTypeEntry(dateTypeEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dateTypeEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the date type entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DateTypeEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dateTypeEntry the date type entry
	 * @return the date type entry that was removed
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.DateTypeEntry
		deleteDateTypeEntry(
			com.liferay.portal.tools.service.builder.test.model.DateTypeEntry
				dateTypeEntry) {

		return _dateTypeEntryLocalService.deleteDateTypeEntry(dateTypeEntry);
	}

	/**
	 * Deletes the date type entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DateTypeEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dateTypeEntryId the primary key of the date type entry
	 * @return the date type entry that was removed
	 * @throws PortalException if a date type entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.DateTypeEntry
			deleteDateTypeEntry(long dateTypeEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dateTypeEntryLocalService.deleteDateTypeEntry(dateTypeEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dateTypeEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _dateTypeEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _dateTypeEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _dateTypeEntryLocalService.dynamicQuery();
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

		return _dateTypeEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.DateTypeEntryModelImpl</code>.
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

		return _dateTypeEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.DateTypeEntryModelImpl</code>.
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

		return _dateTypeEntryLocalService.dynamicQuery(
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

		return _dateTypeEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _dateTypeEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.DateTypeEntry
		fetchDateTypeEntry(long dateTypeEntryId) {

		return _dateTypeEntryLocalService.fetchDateTypeEntry(dateTypeEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _dateTypeEntryLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the date type entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.DateTypeEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of date type entries
	 * @param end the upper bound of the range of date type entries (not inclusive)
	 * @return the range of date type entries
	 */
	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.DateTypeEntry>
			getDateTypeEntries(int start, int end) {

		return _dateTypeEntryLocalService.getDateTypeEntries(start, end);
	}

	/**
	 * Returns the number of date type entries.
	 *
	 * @return the number of date type entries
	 */
	@Override
	public int getDateTypeEntriesCount() {
		return _dateTypeEntryLocalService.getDateTypeEntriesCount();
	}

	/**
	 * Returns the date type entry with the primary key.
	 *
	 * @param dateTypeEntryId the primary key of the date type entry
	 * @return the date type entry
	 * @throws PortalException if a date type entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.DateTypeEntry
			getDateTypeEntry(long dateTypeEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dateTypeEntryLocalService.getDateTypeEntry(dateTypeEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _dateTypeEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _dateTypeEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dateTypeEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the date type entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DateTypeEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dateTypeEntry the date type entry
	 * @return the date type entry that was updated
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.DateTypeEntry
		updateDateTypeEntry(
			com.liferay.portal.tools.service.builder.test.model.DateTypeEntry
				dateTypeEntry) {

		return _dateTypeEntryLocalService.updateDateTypeEntry(dateTypeEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _dateTypeEntryLocalService.getBasePersistence();
	}

	@Override
	public DateTypeEntryLocalService getWrappedService() {
		return _dateTypeEntryLocalService;
	}

	@Override
	public void setWrappedService(
		DateTypeEntryLocalService dateTypeEntryLocalService) {

		_dateTypeEntryLocalService = dateTypeEntryLocalService;
	}

	private DateTypeEntryLocalService _dateTypeEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-233532443
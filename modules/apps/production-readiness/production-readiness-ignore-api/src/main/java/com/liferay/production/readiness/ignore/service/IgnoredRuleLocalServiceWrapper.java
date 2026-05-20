/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link IgnoredRuleLocalService}.
 *
 * @author Lily Chi
 * @see IgnoredRuleLocalService
 * @generated
 */
public class IgnoredRuleLocalServiceWrapper
	implements IgnoredRuleLocalService,
			   ServiceWrapper<IgnoredRuleLocalService> {

	public IgnoredRuleLocalServiceWrapper() {
		this(null);
	}

	public IgnoredRuleLocalServiceWrapper(
		IgnoredRuleLocalService ignoredRuleLocalService) {

		_ignoredRuleLocalService = ignoredRuleLocalService;
	}

	/**
	 * Adds the ignored rule to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect IgnoredRuleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ignoredRule the ignored rule
	 * @return the ignored rule that was added
	 */
	@Override
	public com.liferay.production.readiness.ignore.model.IgnoredRule
		addIgnoredRule(
			com.liferay.production.readiness.ignore.model.IgnoredRule
				ignoredRule) {

		return _ignoredRuleLocalService.addIgnoredRule(ignoredRule);
	}

	@Override
	public com.liferay.production.readiness.ignore.model.IgnoredRule
			addIgnoredRule(
				long userId, long companyId, String ruleKey, String reason)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ignoredRuleLocalService.addIgnoredRule(
			userId, companyId, ruleKey, reason);
	}

	/**
	 * Creates a new ignored rule with the primary key. Does not add the ignored rule to the database.
	 *
	 * @param ignoredRuleId the primary key for the new ignored rule
	 * @return the new ignored rule
	 */
	@Override
	public com.liferay.production.readiness.ignore.model.IgnoredRule
		createIgnoredRule(long ignoredRuleId) {

		return _ignoredRuleLocalService.createIgnoredRule(ignoredRuleId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ignoredRuleLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the ignored rule from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect IgnoredRuleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ignoredRule the ignored rule
	 * @return the ignored rule that was removed
	 */
	@Override
	public com.liferay.production.readiness.ignore.model.IgnoredRule
		deleteIgnoredRule(
			com.liferay.production.readiness.ignore.model.IgnoredRule
				ignoredRule) {

		return _ignoredRuleLocalService.deleteIgnoredRule(ignoredRule);
	}

	/**
	 * Deletes the ignored rule with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect IgnoredRuleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ignoredRuleId the primary key of the ignored rule
	 * @return the ignored rule that was removed
	 * @throws PortalException if a ignored rule with the primary key could not be found
	 */
	@Override
	public com.liferay.production.readiness.ignore.model.IgnoredRule
			deleteIgnoredRule(long ignoredRuleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ignoredRuleLocalService.deleteIgnoredRule(ignoredRuleId);
	}

	@Override
	public com.liferay.production.readiness.ignore.model.IgnoredRule
			deleteIgnoredRule(long userId, long companyId, String ruleKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ignoredRuleLocalService.deleteIgnoredRule(
			userId, companyId, ruleKey);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ignoredRuleLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ignoredRuleLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ignoredRuleLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ignoredRuleLocalService.dynamicQuery();
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

		return _ignoredRuleLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.production.readiness.ignore.model.impl.IgnoredRuleModelImpl</code>.
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

		return _ignoredRuleLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.production.readiness.ignore.model.impl.IgnoredRuleModelImpl</code>.
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

		return _ignoredRuleLocalService.dynamicQuery(
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

		return _ignoredRuleLocalService.dynamicQueryCount(dynamicQuery);
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

		return _ignoredRuleLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.production.readiness.ignore.model.IgnoredRule
		fetchIgnoredRule(long ignoredRuleId) {

		return _ignoredRuleLocalService.fetchIgnoredRule(ignoredRuleId);
	}

	@Override
	public com.liferay.production.readiness.ignore.model.IgnoredRule
		fetchIgnoredRule(long companyId, String ruleKey) {

		return _ignoredRuleLocalService.fetchIgnoredRule(companyId, ruleKey);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ignoredRuleLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the ignored rule with the primary key.
	 *
	 * @param ignoredRuleId the primary key of the ignored rule
	 * @return the ignored rule
	 * @throws PortalException if a ignored rule with the primary key could not be found
	 */
	@Override
	public com.liferay.production.readiness.ignore.model.IgnoredRule
			getIgnoredRule(long ignoredRuleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ignoredRuleLocalService.getIgnoredRule(ignoredRuleId);
	}

	/**
	 * Returns a range of all the ignored rules.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.production.readiness.ignore.model.impl.IgnoredRuleModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ignored rules
	 * @param end the upper bound of the range of ignored rules (not inclusive)
	 * @return the range of ignored rules
	 */
	@Override
	public java.util.List
		<com.liferay.production.readiness.ignore.model.IgnoredRule>
			getIgnoredRules(int start, int end) {

		return _ignoredRuleLocalService.getIgnoredRules(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.production.readiness.ignore.model.IgnoredRule>
			getIgnoredRules(long companyId) {

		return _ignoredRuleLocalService.getIgnoredRules(companyId);
	}

	/**
	 * Returns the number of ignored rules.
	 *
	 * @return the number of ignored rules
	 */
	@Override
	public int getIgnoredRulesCount() {
		return _ignoredRuleLocalService.getIgnoredRulesCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ignoredRuleLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ignoredRuleLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ignoredRuleLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the ignored rule in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect IgnoredRuleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ignoredRule the ignored rule
	 * @return the ignored rule that was updated
	 */
	@Override
	public com.liferay.production.readiness.ignore.model.IgnoredRule
		updateIgnoredRule(
			com.liferay.production.readiness.ignore.model.IgnoredRule
				ignoredRule) {

		return _ignoredRuleLocalService.updateIgnoredRule(ignoredRule);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ignoredRuleLocalService.getBasePersistence();
	}

	@Override
	public IgnoredRuleLocalService getWrappedService() {
		return _ignoredRuleLocalService;
	}

	@Override
	public void setWrappedService(
		IgnoredRuleLocalService ignoredRuleLocalService) {

		_ignoredRuleLocalService = ignoredRuleLocalService;
	}

	private IgnoredRuleLocalService _ignoredRuleLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:517658164
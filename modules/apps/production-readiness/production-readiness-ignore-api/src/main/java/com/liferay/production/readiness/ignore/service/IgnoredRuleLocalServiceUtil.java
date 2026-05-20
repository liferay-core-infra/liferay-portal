/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.production.readiness.ignore.model.IgnoredRule;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for IgnoredRule. This utility wraps
 * <code>com.liferay.production.readiness.ignore.service.impl.IgnoredRuleLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Lily Chi
 * @see IgnoredRuleLocalService
 * @generated
 */
public class IgnoredRuleLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.production.readiness.ignore.service.impl.IgnoredRuleLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static IgnoredRule addIgnoredRule(IgnoredRule ignoredRule) {
		return getService().addIgnoredRule(ignoredRule);
	}

	public static IgnoredRule addIgnoredRule(
			long userId, long companyId, String ruleKey, String reason)
		throws PortalException {

		return getService().addIgnoredRule(userId, companyId, ruleKey, reason);
	}

	/**
	 * Creates a new ignored rule with the primary key. Does not add the ignored rule to the database.
	 *
	 * @param ignoredRuleId the primary key for the new ignored rule
	 * @return the new ignored rule
	 */
	public static IgnoredRule createIgnoredRule(long ignoredRuleId) {
		return getService().createIgnoredRule(ignoredRuleId);
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
	 * Deletes the ignored rule from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect IgnoredRuleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ignoredRule the ignored rule
	 * @return the ignored rule that was removed
	 */
	public static IgnoredRule deleteIgnoredRule(IgnoredRule ignoredRule) {
		return getService().deleteIgnoredRule(ignoredRule);
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
	public static IgnoredRule deleteIgnoredRule(long ignoredRuleId)
		throws PortalException {

		return getService().deleteIgnoredRule(ignoredRuleId);
	}

	public static IgnoredRule deleteIgnoredRule(
			long userId, long companyId, String ruleKey)
		throws PortalException {

		return getService().deleteIgnoredRule(userId, companyId, ruleKey);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.production.readiness.ignore.model.impl.IgnoredRuleModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.production.readiness.ignore.model.impl.IgnoredRuleModelImpl</code>.
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

	public static IgnoredRule fetchIgnoredRule(long ignoredRuleId) {
		return getService().fetchIgnoredRule(ignoredRuleId);
	}

	public static IgnoredRule fetchIgnoredRule(long companyId, String ruleKey) {
		return getService().fetchIgnoredRule(companyId, ruleKey);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the ignored rule with the primary key.
	 *
	 * @param ignoredRuleId the primary key of the ignored rule
	 * @return the ignored rule
	 * @throws PortalException if a ignored rule with the primary key could not be found
	 */
	public static IgnoredRule getIgnoredRule(long ignoredRuleId)
		throws PortalException {

		return getService().getIgnoredRule(ignoredRuleId);
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
	public static List<IgnoredRule> getIgnoredRules(int start, int end) {
		return getService().getIgnoredRules(start, end);
	}

	public static List<IgnoredRule> getIgnoredRules(long companyId) {
		return getService().getIgnoredRules(companyId);
	}

	/**
	 * Returns the number of ignored rules.
	 *
	 * @return the number of ignored rules
	 */
	public static int getIgnoredRulesCount() {
		return getService().getIgnoredRulesCount();
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
	 * Updates the ignored rule in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect IgnoredRuleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ignoredRule the ignored rule
	 * @return the ignored rule that was updated
	 */
	public static IgnoredRule updateIgnoredRule(IgnoredRule ignoredRule) {
		return getService().updateIgnoredRule(ignoredRule);
	}

	public static IgnoredRuleLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<IgnoredRuleLocalService> _serviceSnapshot =
		new Snapshot<>(
			IgnoredRuleLocalServiceUtil.class, IgnoredRuleLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-212196197
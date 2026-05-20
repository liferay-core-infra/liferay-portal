/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.production.readiness.ignore.model.IgnoredRule;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the ignored rule service. This utility wraps <code>com.liferay.production.readiness.ignore.service.persistence.impl.IgnoredRulePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Lily Chi
 * @see IgnoredRulePersistence
 * @generated
 */
public class IgnoredRuleUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<IgnoredRule> ignoredRules) {
		getPersistence().cacheResult(ignoredRules);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(IgnoredRule ignoredRule) {
		getPersistence().cacheResult(ignoredRule);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(IgnoredRule ignoredRule) {
		getPersistence().clearCache(ignoredRule);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, IgnoredRule> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<IgnoredRule> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<IgnoredRule> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<IgnoredRule> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<IgnoredRule> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static IgnoredRule update(IgnoredRule ignoredRule) {
		return getPersistence().update(ignoredRule);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static IgnoredRule update(
		IgnoredRule ignoredRule, ServiceContext serviceContext) {

		return getPersistence().update(ignoredRule, serviceContext);
	}

	/**
	 * Returns all the ignored rules where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching ignored rules
	 */
	public static List<IgnoredRule> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the ignored rules where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.production.readiness.ignore.model.impl.IgnoredRuleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ignored rules
	 * @param end the upper bound of the range of ignored rules (not inclusive)
	 * @return the range of matching ignored rules
	 */
	public static List<IgnoredRule> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the ignored rules where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.production.readiness.ignore.model.impl.IgnoredRuleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ignored rules
	 * @param end the upper bound of the range of ignored rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ignored rules
	 */
	public static List<IgnoredRule> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<IgnoredRule> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ignored rules where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.production.readiness.ignore.model.impl.IgnoredRuleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ignored rules
	 * @param end the upper bound of the range of ignored rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ignored rules
	 */
	public static List<IgnoredRule> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<IgnoredRule> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ignored rule in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ignored rule
	 * @throws NoSuchIgnoredRuleException if a matching ignored rule could not be found
	 */
	public static IgnoredRule findByCompanyId_First(
			long companyId, OrderByComparator<IgnoredRule> orderByComparator)
		throws com.liferay.production.readiness.ignore.exception.
			NoSuchIgnoredRuleException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first ignored rule in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ignored rule, or <code>null</code> if a matching ignored rule could not be found
	 */
	public static IgnoredRule fetchByCompanyId_First(
		long companyId, OrderByComparator<IgnoredRule> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Removes all the ignored rules where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of ignored rules where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching ignored rules
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns the ignored rule where companyId = &#63; and ruleKey = &#63; or throws a <code>NoSuchIgnoredRuleException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the matching ignored rule
	 * @throws NoSuchIgnoredRuleException if a matching ignored rule could not be found
	 */
	public static IgnoredRule findByC_R(long companyId, String ruleKey)
		throws com.liferay.production.readiness.ignore.exception.
			NoSuchIgnoredRuleException {

		return getPersistence().findByC_R(companyId, ruleKey);
	}

	/**
	 * Returns the ignored rule where companyId = &#63; and ruleKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the matching ignored rule, or <code>null</code> if a matching ignored rule could not be found
	 */
	public static IgnoredRule fetchByC_R(long companyId, String ruleKey) {
		return getPersistence().fetchByC_R(companyId, ruleKey);
	}

	/**
	 * Returns the ignored rule where companyId = &#63; and ruleKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ignored rule, or <code>null</code> if a matching ignored rule could not be found
	 */
	public static IgnoredRule fetchByC_R(
		long companyId, String ruleKey, boolean useFinderCache) {

		return getPersistence().fetchByC_R(companyId, ruleKey, useFinderCache);
	}

	/**
	 * Removes the ignored rule where companyId = &#63; and ruleKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the ignored rule that was removed
	 */
	public static IgnoredRule removeByC_R(long companyId, String ruleKey)
		throws com.liferay.production.readiness.ignore.exception.
			NoSuchIgnoredRuleException {

		return getPersistence().removeByC_R(companyId, ruleKey);
	}

	/**
	 * Returns the number of ignored rules where companyId = &#63; and ruleKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the number of matching ignored rules
	 */
	public static int countByC_R(long companyId, String ruleKey) {
		return getPersistence().countByC_R(companyId, ruleKey);
	}

	/**
	 * Creates a new ignored rule with the primary key. Does not add the ignored rule to the database.
	 *
	 * @param ignoredRuleId the primary key for the new ignored rule
	 * @return the new ignored rule
	 */
	public static IgnoredRule create(long ignoredRuleId) {
		return getPersistence().create(ignoredRuleId);
	}

	/**
	 * Removes the ignored rule with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ignoredRuleId the primary key of the ignored rule
	 * @return the ignored rule that was removed
	 * @throws NoSuchIgnoredRuleException if a ignored rule with the primary key could not be found
	 */
	public static IgnoredRule remove(long ignoredRuleId)
		throws com.liferay.production.readiness.ignore.exception.
			NoSuchIgnoredRuleException {

		return getPersistence().remove(ignoredRuleId);
	}

	public static IgnoredRule updateImpl(IgnoredRule ignoredRule) {
		return getPersistence().updateImpl(ignoredRule);
	}

	/**
	 * Returns the ignored rule with the primary key or throws a <code>NoSuchIgnoredRuleException</code> if it could not be found.
	 *
	 * @param ignoredRuleId the primary key of the ignored rule
	 * @return the ignored rule
	 * @throws NoSuchIgnoredRuleException if a ignored rule with the primary key could not be found
	 */
	public static IgnoredRule findByPrimaryKey(long ignoredRuleId)
		throws com.liferay.production.readiness.ignore.exception.
			NoSuchIgnoredRuleException {

		return getPersistence().findByPrimaryKey(ignoredRuleId);
	}

	/**
	 * Returns the ignored rule with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ignoredRuleId the primary key of the ignored rule
	 * @return the ignored rule, or <code>null</code> if a ignored rule with the primary key could not be found
	 */
	public static IgnoredRule fetchByPrimaryKey(long ignoredRuleId) {
		return getPersistence().fetchByPrimaryKey(ignoredRuleId);
	}

	public static IgnoredRulePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(IgnoredRulePersistence persistence) {
		_persistence = persistence;
	}

	private static volatile IgnoredRulePersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:158168489
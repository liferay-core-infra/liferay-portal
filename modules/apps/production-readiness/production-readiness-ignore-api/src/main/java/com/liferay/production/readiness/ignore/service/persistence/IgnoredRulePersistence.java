/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.production.readiness.ignore.exception.NoSuchIgnoredRuleException;
import com.liferay.production.readiness.ignore.model.IgnoredRule;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the ignored rule service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Lily Chi
 * @see IgnoredRuleUtil
 * @generated
 */
@ProviderType
public interface IgnoredRulePersistence extends BasePersistence<IgnoredRule> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link IgnoredRuleUtil} to access the ignored rule persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the ignored rules where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching ignored rules
	 */
	public java.util.List<IgnoredRule> findByCompanyId(long companyId);

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
	public java.util.List<IgnoredRule> findByCompanyId(
		long companyId, int start, int end);

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
	public java.util.List<IgnoredRule> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<IgnoredRule>
			orderByComparator);

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
	public java.util.List<IgnoredRule> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<IgnoredRule>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ignored rule in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ignored rule
	 * @throws NoSuchIgnoredRuleException if a matching ignored rule could not be found
	 */
	public IgnoredRule findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<IgnoredRule>
				orderByComparator)
		throws NoSuchIgnoredRuleException;

	/**
	 * Returns the first ignored rule in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ignored rule, or <code>null</code> if a matching ignored rule could not be found
	 */
	public IgnoredRule fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<IgnoredRule>
			orderByComparator);

	/**
	 * Removes all the ignored rules where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of ignored rules where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching ignored rules
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns the ignored rule where companyId = &#63; and ruleKey = &#63; or throws a <code>NoSuchIgnoredRuleException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the matching ignored rule
	 * @throws NoSuchIgnoredRuleException if a matching ignored rule could not be found
	 */
	public IgnoredRule findByC_R(long companyId, String ruleKey)
		throws NoSuchIgnoredRuleException;

	/**
	 * Returns the ignored rule where companyId = &#63; and ruleKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the matching ignored rule, or <code>null</code> if a matching ignored rule could not be found
	 */
	public IgnoredRule fetchByC_R(long companyId, String ruleKey);

	/**
	 * Returns the ignored rule where companyId = &#63; and ruleKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ignored rule, or <code>null</code> if a matching ignored rule could not be found
	 */
	public IgnoredRule fetchByC_R(
		long companyId, String ruleKey, boolean useFinderCache);

	/**
	 * Removes the ignored rule where companyId = &#63; and ruleKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the ignored rule that was removed
	 */
	public IgnoredRule removeByC_R(long companyId, String ruleKey)
		throws NoSuchIgnoredRuleException;

	/**
	 * Returns the number of ignored rules where companyId = &#63; and ruleKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the number of matching ignored rules
	 */
	public int countByC_R(long companyId, String ruleKey);

	/**
	 * Creates a new ignored rule with the primary key. Does not add the ignored rule to the database.
	 *
	 * @param ignoredRuleId the primary key for the new ignored rule
	 * @return the new ignored rule
	 */
	public IgnoredRule create(long ignoredRuleId);

	/**
	 * Removes the ignored rule with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ignoredRuleId the primary key of the ignored rule
	 * @return the ignored rule that was removed
	 * @throws NoSuchIgnoredRuleException if a ignored rule with the primary key could not be found
	 */
	public IgnoredRule remove(long ignoredRuleId)
		throws NoSuchIgnoredRuleException;

	public IgnoredRule updateImpl(IgnoredRule ignoredRule);

	/**
	 * Returns the ignored rule with the primary key or throws a <code>NoSuchIgnoredRuleException</code> if it could not be found.
	 *
	 * @param ignoredRuleId the primary key of the ignored rule
	 * @return the ignored rule
	 * @throws NoSuchIgnoredRuleException if a ignored rule with the primary key could not be found
	 */
	public IgnoredRule findByPrimaryKey(long ignoredRuleId)
		throws NoSuchIgnoredRuleException;

	/**
	 * Returns the ignored rule with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ignoredRuleId the primary key of the ignored rule
	 * @return the ignored rule, or <code>null</code> if a ignored rule with the primary key could not be found
	 */
	public IgnoredRule fetchByPrimaryKey(long ignoredRuleId);

}
// LIFERAY-SERVICE-BUILDER-HASH:-29397939
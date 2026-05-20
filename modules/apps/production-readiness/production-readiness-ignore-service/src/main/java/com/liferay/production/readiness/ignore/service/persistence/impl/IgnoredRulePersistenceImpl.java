/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.production.readiness.ignore.exception.NoSuchIgnoredRuleException;
import com.liferay.production.readiness.ignore.model.IgnoredRule;
import com.liferay.production.readiness.ignore.model.IgnoredRuleTable;
import com.liferay.production.readiness.ignore.model.impl.IgnoredRuleImpl;
import com.liferay.production.readiness.ignore.model.impl.IgnoredRuleModelImpl;
import com.liferay.production.readiness.ignore.service.persistence.IgnoredRulePersistence;
import com.liferay.production.readiness.ignore.service.persistence.IgnoredRuleUtil;
import com.liferay.production.readiness.ignore.service.persistence.impl.constants.ProductionReadinessPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the ignored rule service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Lily Chi
 * @generated
 */
@Component(service = IgnoredRulePersistence.class)
public class IgnoredRulePersistenceImpl
	extends BasePersistenceImpl<IgnoredRule, NoSuchIgnoredRuleException>
	implements IgnoredRulePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>IgnoredRuleUtil</code> to access the ignored rule persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		IgnoredRuleImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;
	private CollectionPersistenceFinder<IgnoredRule>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns all the ignored rules where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching ignored rules
	 */
	@Override
	public List<IgnoredRule> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ignored rules where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>IgnoredRuleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ignored rules
	 * @param end the upper bound of the range of ignored rules (not inclusive)
	 * @return the range of matching ignored rules
	 */
	@Override
	public List<IgnoredRule> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ignored rules where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>IgnoredRuleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ignored rules
	 * @param end the upper bound of the range of ignored rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ignored rules
	 */
	@Override
	public List<IgnoredRule> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<IgnoredRule> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ignored rules where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>IgnoredRuleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ignored rules
	 * @param end the upper bound of the range of ignored rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ignored rules
	 */
	@Override
	public List<IgnoredRule> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<IgnoredRule> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ignored rule in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ignored rule
	 * @throws NoSuchIgnoredRuleException if a matching ignored rule could not be found
	 */
	@Override
	public IgnoredRule findByCompanyId_First(
			long companyId, OrderByComparator<IgnoredRule> orderByComparator)
		throws NoSuchIgnoredRuleException {

		IgnoredRule ignoredRule = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (ignoredRule != null) {
			return ignoredRule;
		}

		throw new NoSuchIgnoredRuleException(
			_collectionPersistenceFinderByCompanyId.buildNoSuchKeyMessage(
				_NO_SUCH_ENTITY_WITH_KEY, new Object[] {companyId}));
	}

	/**
	 * Returns the first ignored rule in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ignored rule, or <code>null</code> if a matching ignored rule could not be found
	 */
	@Override
	public IgnoredRule fetchByCompanyId_First(
		long companyId, OrderByComparator<IgnoredRule> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the ignored rules where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of ignored rules where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching ignored rules
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private FinderPath _finderPathFetchByC_R;
	private UniquePersistenceFinder<IgnoredRule> _uniquePersistenceFinderByC_R;

	/**
	 * Returns the ignored rule where companyId = &#63; and ruleKey = &#63; or throws a <code>NoSuchIgnoredRuleException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the matching ignored rule
	 * @throws NoSuchIgnoredRuleException if a matching ignored rule could not be found
	 */
	@Override
	public IgnoredRule findByC_R(long companyId, String ruleKey)
		throws NoSuchIgnoredRuleException {

		IgnoredRule ignoredRule = fetchByC_R(companyId, ruleKey);

		if (ignoredRule == null) {
			String message =
				_uniquePersistenceFinderByC_R.buildNoSuchKeyMessage(
					_NO_SUCH_ENTITY_WITH_KEY,
					new Object[] {companyId, ruleKey});

			if (_log.isDebugEnabled()) {
				_log.debug(message);
			}

			throw new NoSuchIgnoredRuleException(message);
		}

		return ignoredRule;
	}

	/**
	 * Returns the ignored rule where companyId = &#63; and ruleKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the matching ignored rule, or <code>null</code> if a matching ignored rule could not be found
	 */
	@Override
	public IgnoredRule fetchByC_R(long companyId, String ruleKey) {
		return fetchByC_R(companyId, ruleKey, true);
	}

	/**
	 * Returns the ignored rule where companyId = &#63; and ruleKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ignored rule, or <code>null</code> if a matching ignored rule could not be found
	 */
	@Override
	public IgnoredRule fetchByC_R(
		long companyId, String ruleKey, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_R.fetch(
			finderCache, new Object[] {companyId, ruleKey}, useFinderCache);
	}

	/**
	 * Removes the ignored rule where companyId = &#63; and ruleKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the ignored rule that was removed
	 */
	@Override
	public IgnoredRule removeByC_R(long companyId, String ruleKey)
		throws NoSuchIgnoredRuleException {

		IgnoredRule ignoredRule = findByC_R(companyId, ruleKey);

		return remove(ignoredRule);
	}

	/**
	 * Returns the number of ignored rules where companyId = &#63; and ruleKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the number of matching ignored rules
	 */
	@Override
	public int countByC_R(long companyId, String ruleKey) {
		return _uniquePersistenceFinderByC_R.count(
			finderCache, new Object[] {companyId, ruleKey});
	}

	public IgnoredRulePersistenceImpl() {
		setModelClass(IgnoredRule.class);

		setModelImplClass(IgnoredRuleImpl.class);
		setModelPKClass(long.class);

		setTable(IgnoredRuleTable.INSTANCE);
	}

	/**
	 * Creates a new ignored rule with the primary key. Does not add the ignored rule to the database.
	 *
	 * @param ignoredRuleId the primary key for the new ignored rule
	 * @return the new ignored rule
	 */
	@Override
	public IgnoredRule create(long ignoredRuleId) {
		IgnoredRule ignoredRule = new IgnoredRuleImpl();

		ignoredRule.setNew(true);
		ignoredRule.setPrimaryKey(ignoredRuleId);

		ignoredRule.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ignoredRule;
	}

	/**
	 * Removes the ignored rule with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ignoredRuleId the primary key of the ignored rule
	 * @return the ignored rule that was removed
	 * @throws NoSuchIgnoredRuleException if a ignored rule with the primary key could not be found
	 */
	@Override
	public IgnoredRule remove(long ignoredRuleId)
		throws NoSuchIgnoredRuleException {

		return remove((Serializable)ignoredRuleId);
	}

	@Override
	protected IgnoredRule removeImpl(IgnoredRule ignoredRule) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ignoredRule)) {
				ignoredRule = (IgnoredRule)session.get(
					IgnoredRuleImpl.class, ignoredRule.getPrimaryKeyObj());
			}

			if (ignoredRule != null) {
				session.delete(ignoredRule);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ignoredRule != null) {
			clearCache(ignoredRule);
		}

		return ignoredRule;
	}

	@Override
	public IgnoredRule updateImpl(IgnoredRule ignoredRule) {
		boolean isNew = ignoredRule.isNew();

		if (!(ignoredRule instanceof IgnoredRuleModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ignoredRule.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(ignoredRule);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ignoredRule proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom IgnoredRule implementation " +
					ignoredRule.getClass());
		}

		IgnoredRuleModelImpl ignoredRuleModelImpl =
			(IgnoredRuleModelImpl)ignoredRule;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (ignoredRule.getCreateDate() == null)) {
			if (serviceContext == null) {
				ignoredRule.setCreateDate(date);
			}
			else {
				ignoredRule.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!ignoredRuleModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				ignoredRule.setModifiedDate(date);
			}
			else {
				ignoredRule.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(ignoredRule);
			}
			else {
				ignoredRule = (IgnoredRule)session.merge(ignoredRule);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ignoredRule, false);

		if (isNew) {
			ignoredRule.setNew(false);
		}

		ignoredRule.resetOriginalValues();

		return ignoredRule;
	}

	/**
	 * Returns the ignored rule with the primary key or throws a <code>NoSuchIgnoredRuleException</code> if it could not be found.
	 *
	 * @param ignoredRuleId the primary key of the ignored rule
	 * @return the ignored rule
	 * @throws NoSuchIgnoredRuleException if a ignored rule with the primary key could not be found
	 */
	@Override
	public IgnoredRule findByPrimaryKey(long ignoredRuleId)
		throws NoSuchIgnoredRuleException {

		return findByPrimaryKey((Serializable)ignoredRuleId);
	}

	/**
	 * Returns the ignored rule with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ignoredRuleId the primary key of the ignored rule
	 * @return the ignored rule, or <code>null</code> if a ignored rule with the primary key could not be found
	 */
	@Override
	public IgnoredRule fetchByPrimaryKey(long ignoredRuleId) {
		return fetchByPrimaryKey((Serializable)ignoredRuleId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "ignoredRuleId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_IGNOREDRULE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return IgnoredRuleModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the ignored rule persistence.
	 */
	@Activate
	public void activate() {
		_finderPathWithPaginationFindByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId"}, true);

		_finderPathWithoutPaginationFindByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCompanyId",
			new String[] {Long.class.getName()}, new String[] {"companyId"},
			true);

		_finderPathCountByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCompanyId",
			new String[] {Long.class.getName()}, new String[] {"companyId"},
			false);

		_collectionPersistenceFinderByCompanyId =
			new CollectionPersistenceFinder<>(
				this, _finderPathWithPaginationFindByCompanyId,
				_finderPathWithoutPaginationFindByCompanyId,
				_finderPathCountByCompanyId, _SQL_SELECT_IGNOREDRULE_WHERE,
				_SQL_COUNT_IGNOREDRULE_WHERE,
				IgnoredRuleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				new FinderColumn<>(
					"ignoredRule.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, IgnoredRule::getCompanyId));

		_finderPathFetchByC_R = createUniqueFinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_R",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "ruleKey"}, 0, 2, false,
			IgnoredRule::getCompanyId,
			convertNullFunction(IgnoredRule::getRuleKey));

		_uniquePersistenceFinderByC_R = new UniquePersistenceFinder<>(
			this, _finderPathFetchByC_R, _SQL_SELECT_IGNOREDRULE_WHERE, "",
			new FinderColumn<>(
				"ignoredRule.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, IgnoredRule::getCompanyId),
			new FinderColumn<>(
				"ignoredRule.", "ruleKey", FinderColumn.Type.STRING, "=", true,
				true, IgnoredRule::getRuleKey));

		IgnoredRuleUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		IgnoredRuleUtil.setPersistence(null);

		entityCache.removeCache(IgnoredRuleImpl.class.getName());
	}

	@Override
	@Reference(
		target = ProductionReadinessPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = ProductionReadinessPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = ProductionReadinessPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		IgnoredRuleModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_IGNOREDRULE =
		"SELECT ignoredRule FROM IgnoredRule ignoredRule";

	private static final String _SQL_SELECT_IGNOREDRULE_WHERE =
		"SELECT ignoredRule FROM IgnoredRule ignoredRule WHERE ";

	private static final String _SQL_COUNT_IGNOREDRULE_WHERE =
		"SELECT COUNT(ignoredRule) FROM IgnoredRule ignoredRule WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No IgnoredRule exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		IgnoredRulePersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:349594100
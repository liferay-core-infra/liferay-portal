/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.cluster.Clusterable;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.exception.AvailableLocaleException;
import com.liferay.portal.kernel.exception.NoSuchVirtualHostException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.CompanyPersistence;
import com.liferay.portal.kernel.service.persistence.GroupPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutSetPersistence;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.impl.LayoutSetImpl;
import com.liferay.portal.service.base.VirtualHostLocalServiceBaseImpl;

import java.net.IDN;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alexander Chow
 * @author Raymond Augé
 */
public class VirtualHostLocalServiceImpl
	extends VirtualHostLocalServiceBaseImpl {

	@Override
	public VirtualHost fetchCompanyDefaultVirtualHost(long companyId) {
		List<VirtualHost> virtualHosts = getVirtualHosts(companyId, 0);

		if (virtualHosts.isEmpty()) {
			return null;
		}

		if (virtualHosts.size() == 1) {
			VirtualHost virtualHost = virtualHosts.get(0);

			if (virtualHost.isDefaultVirtualHost()) {
				return virtualHost;
			}

			return null;
		}

		List<VirtualHost> defaultVirtualHosts = new ArrayList<>();

		for (VirtualHost virtualHost : virtualHosts) {
			if (virtualHost.isDefaultVirtualHost()) {
				defaultVirtualHosts.add(virtualHost);
			}
		}

		if (defaultVirtualHosts.isEmpty()) {
			return null;
		}

		if ((defaultVirtualHosts.size() > 1) && _log.isWarnEnabled()) {
			_log.error(
				"More than one default virtual host uses company ID " +
					companyId);
		}

		return defaultVirtualHosts.get(defaultVirtualHosts.size() - 1);
	}

	@Override
	public VirtualHost fetchVirtualHost(String hostname) {
		if (Validator.isIPv6Address(hostname)) {
			try {
				Inet6Address inet6Address = (Inet6Address)InetAddress.getByName(
					hostname);

				hostname = inet6Address.getHostAddress();
			}
			catch (UnknownHostException unknownHostException) {
				if (_log.isDebugEnabled()) {
					_log.debug(unknownHostException);
				}
			}
		}

		if (PropsValues.DATABASE_PARTITION_ENABLED &&
			CompanyThreadLocal.isDefaultCompany()) {

			long companyId = _fetchCompanyId(hostname);

			if (companyId != 0) {
				try (SafeCloseable safeCloseable =
						CompanyThreadLocal.setCompanyIdWithSafeCloseable(
							companyId,
							CTCollectionThreadLocal.getCTCollectionId())) {

					return _fetchVirtualHost(hostname);
				}
			}
		}

		return _fetchVirtualHost(hostname);
	}

	@Override
	public VirtualHost getVirtualHost(String hostname) throws PortalException {
		if (PropsValues.DATABASE_PARTITION_ENABLED &&
			CompanyThreadLocal.isDefaultCompany()) {

			long companyId = _fetchCompanyId(hostname);

			if (companyId != 0) {
				try (SafeCloseable safeCloseable =
						CompanyThreadLocal.setCompanyIdWithSafeCloseable(
							companyId,
							CTCollectionThreadLocal.getCTCollectionId())) {

					return _getVirtualHost(hostname);
				}
			}
		}

		return _getVirtualHost(hostname);
	}

	@Override
	public List<VirtualHost> getVirtualHosts(long companyId) {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					companyId, CTCollectionThreadLocal.getCTCollectionId())) {

			return virtualHostPersistence.findByCompanyId(companyId);
		}
	}

	@Override
	public List<VirtualHost> getVirtualHosts(long companyId, long layoutSetId) {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					companyId, CTCollectionThreadLocal.getCTCollectionId())) {

			return _getVirtualHosts(companyId, layoutSetId);
		}
	}

	@Override
	public long getVirtualHostsCount(
		long excludedLayoutSetId, String[] virtualHostNames) {

		return virtualHostPersistence.countByNotL_H(
			excludedLayoutSetId, virtualHostNames);
	}

	@Clusterable
	@Override
	@Transactional(enabled = false)
	public void registerVirtualHost(String hostname, long companyId) {
		if (Validator.isNull(hostname)) {
			return;
		}

		_companyIdsByHostnameMap.put(
			StringUtil.toLowerCase(hostname), companyId);
	}

	@Clusterable
	@Override
	@Transactional(enabled = false)
	public void unregisterVirtualHost(String hostname) {
		if (Validator.isNull(hostname)) {
			return;
		}

		_companyIdsByHostnameMap.remove(StringUtil.toLowerCase(hostname));
	}

	@Clusterable
	@Override
	@Transactional(enabled = false)
	public void unregisterVirtualHosts(long companyId) {
		Collection<Long> companyIds = _companyIdsByHostnameMap.values();

		companyIds.removeIf(curCompanyId -> curCompanyId == companyId);
	}

	@Override
	public List<VirtualHost> updateVirtualHosts(
		long companyId, long layoutSetId, TreeMap<String, String> hostnames) {

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					companyId, CTCollectionThreadLocal.getCTCollectionId())) {

			return _updateVirtualHosts(companyId, layoutSetId, hostnames);
		}
	}

	private long _fetchCompanyId(String hostname) {
		if (Validator.isNull(hostname)) {
			return 0;
		}

		Long companyId = _companyIdsByHostnameMap.get(
			StringUtil.toLowerCase(hostname));

		if ((companyId == null) && hostname.contains("xn--")) {
			companyId = _companyIdsByHostnameMap.get(
				StringUtil.toLowerCase(IDN.toUnicode(hostname)));
		}

		return GetterUtil.getLong(companyId);
	}

	private VirtualHost _fetchVirtualHost(String hostname) {
		VirtualHost virtualHost = virtualHostPersistence.fetchByHostname(
			hostname);

		if ((virtualHost == null) && hostname.contains("xn--")) {
			virtualHost = virtualHostPersistence.fetchByHostname(
				IDN.toUnicode(hostname));
		}

		return virtualHost;
	}

	private VirtualHost _getVirtualHost(String hostname)
		throws PortalException {

		try {
			return virtualHostPersistence.findByHostname(hostname);
		}
		catch (NoSuchVirtualHostException noSuchVirtualHostException) {
			if (hostname.contains("xn--")) {
				return virtualHostPersistence.findByHostname(
					IDN.toUnicode(hostname));
			}

			throw noSuchVirtualHostException;
		}
	}

	private List<VirtualHost> _getVirtualHosts(
		long companyId, long layoutSetId) {

		if (_cacheableQueryLimitLPD27353 <= 0) {
			return virtualHostPersistence.findByC_L(companyId, layoutSetId);
		}

		List<VirtualHost> virtualHosts = virtualHostPersistence.findByCompanyId(
			companyId);

		if (virtualHosts.size() > _cacheableQueryLimitLPD27353) {
			_cacheableQueryLimitLPD27353 = 0;
		}

		List<VirtualHost> filteredVirtualHosts = null;

		for (VirtualHost virtualHost : virtualHosts) {
			if (virtualHost.getLayoutSetId() != layoutSetId) {
				continue;
			}

			if (filteredVirtualHosts == null) {
				filteredVirtualHosts = new ArrayList<>(virtualHosts.size());
			}

			filteredVirtualHosts.add(virtualHost);
		}

		if (filteredVirtualHosts == null) {
			return Collections.emptyList();
		}

		return filteredVirtualHosts;
	}

	private List<VirtualHost> _updateVirtualHosts(
		long companyId, long layoutSetId, TreeMap<String, String> hostnames) {

		LayoutSet layoutSet = _layoutSetPersistence.fetchByPrimaryKey(
			layoutSetId);

		Set<Locale> availableLocales = LanguageUtil.getAvailableLocales();

		if (layoutSet != null) {
			availableLocales = LanguageUtil.getAvailableLocales(
				layoutSet.getGroupId());
		}

		List<VirtualHost> virtualHosts = new ArrayList<>(
			virtualHostPersistence.findByC_L(companyId, layoutSetId));

		List<String> reservedHostnames = new ArrayList<>();

		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			TransactionCallbackUtil.registerRollbackCallback(
				() -> {
					for (String reservedHostname : reservedHostnames) {
						virtualHostLocalService.unregisterVirtualHost(
							reservedHostname);
					}

					return null;
				});
		}

		boolean first = true;

		for (String curHostname : hostnames.navigableKeySet()) {
			VirtualHost virtualHost = null;

			for (VirtualHost curVirtualHost : virtualHosts) {
				if (curHostname.equals(curVirtualHost.getHostname())) {
					virtualHost = curVirtualHost;

					break;
				}
			}

			if (virtualHost == null) {
				if (PropsValues.DATABASE_PARTITION_ENABLED) {
					Long virtualHostCompanyId =
						_companyIdsByHostnameMap.putIfAbsent(
							StringUtil.toLowerCase(curHostname), companyId);

					if ((virtualHostCompanyId != null) &&
						(virtualHostCompanyId != companyId)) {

						throw new DuplicateVirtualHostnameException(
							curHostname);
					}

					if (virtualHostCompanyId == null) {
						reservedHostnames.add(curHostname);

						virtualHostLocalService.registerVirtualHost(
							curHostname, companyId);
					}
				}

				long virtualHostId = counterLocalService.increment();

				virtualHost = virtualHostPersistence.create(virtualHostId);

				virtualHost.setCompanyId(companyId);
				virtualHost.setLayoutSetId(layoutSetId);
				virtualHost.setHostname(curHostname);

				virtualHosts.add(virtualHost);
			}

			String languageId = hostnames.get(curHostname);

			Locale locale = LocaleUtil.fromLanguageId(languageId, true, false);

			if (locale == null) {
				locale = LocaleUtil.getSiteDefault();
			}

			if (!availableLocales.contains(locale)) {
				ReflectionUtil.throwException(
					new AvailableLocaleException(languageId));
			}

			virtualHost.setDefaultVirtualHost(first);
			virtualHost.setLanguageId(languageId);

			first = false;

			virtualHostPersistence.update(virtualHost);
		}

		List<String> removedHostnames = new ArrayList<>();

		Iterator<VirtualHost> iterator = virtualHosts.iterator();

		while (iterator.hasNext()) {
			VirtualHost virtualHost = iterator.next();

			if (!hostnames.containsKey(virtualHost.getHostname())) {
				iterator.remove();

				removedHostnames.add(virtualHost.getHostname());

				virtualHostPersistence.remove(virtualHost);
			}
		}

		virtualHostPersistence.cacheResult(virtualHosts);

		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			TransactionCallbackUtil.registerCommitCallback(
				() -> {
					for (String removedHostname : removedHostnames) {
						virtualHostLocalService.unregisterVirtualHost(
							removedHostname);
					}

					return null;
				});
		}

		Company company = _companyPersistence.fetchByPrimaryKey(companyId);

		if (company != null) {
			TransactionCallbackUtil.registerCommitCallback(
				() -> {
					EntityCacheUtil.removeResult(
						company.getClass(), company.getPrimaryKeyObj());

					return null;
				});

			_companyPersistence.clearCache(company);
		}

		if ((layoutSet == null) &&
			Validator.isNotNull(PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME)) {

			Group group = _groupPersistence.fetchByC_GK(
				companyId, PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME);

			if (group != null) {
				layoutSet = _layoutSetPersistence.fetchByG_P(
					group.getGroupId(), false);
			}
		}

		if (layoutSet != null) {
			_layoutSetPersistence.clearCache(layoutSet);

			TransactionCallbackUtil.registerCommitCallback(
				() -> {
					EntityCacheUtil.removeResult(
						LayoutSetImpl.class, layoutSetId);

					return null;
				});
		}

		return virtualHosts;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		VirtualHostLocalServiceImpl.class);

	private volatile int _cacheableQueryLimitLPD27353 = GetterUtil.getInteger(
		PropsUtil.get("cacheable.query.limit.LPD-27353"));
	private final Map<String, Long> _companyIdsByHostnameMap =
		new ConcurrentHashMap<>();

	@BeanReference(type = CompanyPersistence.class)
	private CompanyPersistence _companyPersistence;

	@BeanReference(type = GroupPersistence.class)
	private GroupPersistence _groupPersistence;

	@BeanReference(type = LayoutSetPersistence.class)
	private LayoutSetPersistence _layoutSetPersistence;

}
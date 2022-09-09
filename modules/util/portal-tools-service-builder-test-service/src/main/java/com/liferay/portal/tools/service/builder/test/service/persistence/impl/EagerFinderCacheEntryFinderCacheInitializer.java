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

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.model.EagerFinderCacheEntry;
import com.liferay.portal.tools.service.builder.test.model.impl.EagerFinderCacheEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.EagerFinderCacheEntryPersistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
public class EagerFinderCacheEntryFinderCacheInitializer {

	public void afterPropertiesSet() {
		try {
			TransactionInvokerUtil.invoke(
				TransactionConfig.Factory.create(
					Propagation.SUPPORTS, new Class<?>[] {Exception.class}),
				() -> {
					_initializeFinderCache(
						_eagerFinderCacheEntryPersistence, _finderCache);

					return null;
				});
		}
		catch (Throwable throwable) {
			_log.error("Unable to initialize finder cache", throwable);
		}
	}

	private void _initializeFinderCache(
		EagerFinderCacheEntryPersistence eagerFinderCacheEntryPersistence,
		FinderCache finderCache) {

		List<EagerFinderCacheEntry> eagerFinderCacheEntrys =
			eagerFinderCacheEntryPersistence.findAll();

		EagerFinderCacheEntryPersistenceImpl
			eagerFinderCacheEntryPersistenceImpl =
				(EagerFinderCacheEntryPersistenceImpl)
					eagerFinderCacheEntryPersistence;

		Map<String, FinderPath> eagerCacheFinderPaths =
			eagerFinderCacheEntryPersistenceImpl.getEagerCacheFinderPaths();

		Map<List<Object>, List<EagerFinderCacheEntry>> _findByGroupIdResultMap =
			new HashMap<>();
		Map<List<Object>, List<EagerFinderCacheEntry>> _findByC_GResultMap =
			new HashMap<>();

		for (EagerFinderCacheEntry eagerFinderCacheEntry :
				eagerFinderCacheEntrys) {

			EagerFinderCacheEntryModelImpl eagerFinderCacheEntryModelImpl =
				(EagerFinderCacheEntryModelImpl)eagerFinderCacheEntry;

			finderCache.putResult(
				eagerCacheFinderPaths.get("UniqueName"),
				new Object[] {eagerFinderCacheEntryModelImpl.getUniqueName()},
				eagerFinderCacheEntryModelImpl);

			List<EagerFinderCacheEntry> _findByGroupIdResultList =
				_findByGroupIdResultMap.computeIfAbsent(
					new ArrayList() {
						{
							add(eagerFinderCacheEntryModelImpl.getGroupId());
						}
					},
					key -> new ArrayList<>());

			_findByGroupIdResultList.add(eagerFinderCacheEntry);

			List<EagerFinderCacheEntry> _findByC_GResultList =
				_findByC_GResultMap.computeIfAbsent(
					new ArrayList() {
						{
							add(eagerFinderCacheEntryModelImpl.getCompanyId());
							add(eagerFinderCacheEntryModelImpl.getGroupId());
						}
					},
					key -> new ArrayList<>());

			_findByC_GResultList.add(eagerFinderCacheEntry);
		}

		for (Map.Entry<List<Object>, List<EagerFinderCacheEntry>> entry :
				_findByGroupIdResultMap.entrySet()) {

			List<Object> key = entry.getKey();

			finderCache.putResult(
				eagerCacheFinderPaths.get("GroupId"), key.toArray(),
				entry.getValue());
		}

		for (Map.Entry<List<Object>, List<EagerFinderCacheEntry>> entry :
				_findByC_GResultMap.entrySet()) {

			List<Object> key = entry.getKey();

			finderCache.putResult(
				eagerCacheFinderPaths.get("C_G"), key.toArray(),
				entry.getValue());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EagerFinderCacheEntryFinderCacheInitializer.class);

	@ServiceReference(type = FinderCache.class)
	private FinderCache _finderCache;

	@BeanReference(type = EagerFinderCacheEntryPersistence.class)
	private EagerFinderCacheEntryPersistence _eagerFinderCacheEntryPersistence;

}
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

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.module.util.ServiceLatch;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.persistence.ResourceActionPersistence;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.model.impl.ResourceActionModelImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ResourceActionFinderCacheInitializer {

	public void afterPropertiesSet() {
		ServiceLatch serviceLatch = SystemBundleUtil.newServiceLatch();

		serviceLatch.waitFor(
			FinderCache.class,
			finderCache -> {
				try {
					TransactionInvokerUtil.invoke(
						TransactionConfig.Factory.create(
							Propagation.SUPPORTS,
							new Class<?>[] {Exception.class}),
						() -> {
							_initializeFinderCache(
								_resourceActionPersistence, finderCache);

							return null;
						});
				}
				catch (Throwable throwable) {
					_log.error("Unable to initialize finder cache", throwable);
				}
			});

		serviceLatch.openOn(
			() -> {
			});
	}

	private void _initializeFinderCache(
		ResourceActionPersistence resourceActionPersistence,
		FinderCache finderCache) {

		List<ResourceAction> resourceActions =
			resourceActionPersistence.findAll();

		ResourceActionPersistenceImpl resourceActionPersistenceImpl =
			(ResourceActionPersistenceImpl)resourceActionPersistence;

		Map<String, FinderPath> eagerCacheFinderPaths =
			resourceActionPersistenceImpl.getEagerCacheFinderPaths();

		Map<List<Object>, List<ResourceAction>> _findByNameResultMap =
			new HashMap<>();

		for (ResourceAction resourceAction : resourceActions) {
			ResourceActionModelImpl resourceActionModelImpl =
				(ResourceActionModelImpl)resourceAction;

			List<ResourceAction> _findByNameResultList =
				_findByNameResultMap.computeIfAbsent(
					new ArrayList() {
						{
							add(resourceActionModelImpl.getName());
						}
					},
					key -> new ArrayList<>());

			_findByNameResultList.add(resourceAction);
		}

		for (Map.Entry<List<Object>, List<ResourceAction>> entry :
				_findByNameResultMap.entrySet()) {

			List<Object> key = entry.getKey();

			finderCache.putResult(
				eagerCacheFinderPaths.get("Name"), key.toArray(),
				entry.getValue());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ResourceActionFinderCacheInitializer.class);

	@BeanReference(type = ResourceActionPersistence.class)
	private ResourceActionPersistence _resourceActionPersistence;

}
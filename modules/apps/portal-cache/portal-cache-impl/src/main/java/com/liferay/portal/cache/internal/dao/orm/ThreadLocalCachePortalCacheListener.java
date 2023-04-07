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

package com.liferay.portal.cache.internal.dao.orm;

import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheException;
import com.liferay.portal.kernel.cache.PortalCacheListener;

import java.io.Serializable;

/**
 * @author Tina Tian
 */
public class ThreadLocalCachePortalCacheListener
	implements PortalCacheListener<Serializable, Serializable> {

	public ThreadLocalCachePortalCacheListener(ThreadLocal<?> threadLocal) {
		_threadLocal = threadLocal;
	}

	@Override
	public void dispose() {
		_threadLocal.remove();
	}

	@Override
	public void notifyEntryEvicted(
			PortalCache<Serializable, Serializable> portalCache,
			Serializable key, Serializable value, int timeToLive)
		throws PortalCacheException {

		_threadLocal.remove();
	}

	@Override
	public void notifyEntryExpired(
			PortalCache<Serializable, Serializable> portalCache,
			Serializable key, Serializable value, int timeToLive)
		throws PortalCacheException {

		_threadLocal.remove();
	}

	@Override
	public void notifyEntryPut(
			PortalCache<Serializable, Serializable> portalCache,
			Serializable key, Serializable value, int timeToLive)
		throws PortalCacheException {

		_threadLocal.remove();
	}

	@Override
	public void notifyEntryRemoved(
			PortalCache<Serializable, Serializable> portalCache,
			Serializable key, Serializable value, int timeToLive)
		throws PortalCacheException {

		_threadLocal.remove();
	}

	@Override
	public void notifyEntryUpdated(
			PortalCache<Serializable, Serializable> portalCache,
			Serializable key, Serializable value, int timeToLive)
		throws PortalCacheException {

		_threadLocal.remove();
	}

	@Override
	public void notifyRemoveAll(
			PortalCache<Serializable, Serializable> portalCache)
		throws PortalCacheException {

		_threadLocal.remove();
	}

	private final ThreadLocal<?> _threadLocal;

}
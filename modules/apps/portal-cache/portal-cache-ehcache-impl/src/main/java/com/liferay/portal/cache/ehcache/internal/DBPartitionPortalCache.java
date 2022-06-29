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

package com.liferay.portal.cache.ehcache.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.cache.LowLevelCache;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheListener;
import com.liferay.portal.kernel.cache.PortalCacheListenerScope;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tina Tian
 */
public class DBPartitionPortalCache<K extends Serializable, V>
	implements LowLevelCache<K, V>, PortalCache<K, V> {

	public DBPartitionPortalCache(
		EhcachePortalCacheManager<K, V> ehcachePortalCacheManager,
		EhcachePortalCacheConfiguration ehcachePortalCacheConfiguration) {

		_ehcachePortalCacheManager = ehcachePortalCacheManager;

		_ehcachePortalCacheConfiguration = ehcachePortalCacheConfiguration;

		_portalCacheName =
			_ehcachePortalCacheConfiguration.getPortalCacheName();
	}

	@Override
	public V get(K key) {
		PortalCache<K, V> portalCache = _getPortalCache();

		return portalCache.get(key);
	}

	@Override
	public List<K> getKeys() {
		PortalCache<K, V> portalCache = _getPortalCache();

		return portalCache.getKeys();
	}

	@Override
	public PortalCacheManager<K, V> getPortalCacheManager() {
		return _ehcachePortalCacheManager;
	}

	@Override
	public String getPortalCacheName() {
		return _portalCacheName;
	}

	@Override
	public boolean isMVCC() {
		return false;
	}

	@Override
	public void put(K key, V value) {
		PortalCache<K, V> portalCache = _getPortalCache();

		portalCache.put(key, value);
	}

	@Override
	public void put(K key, V value, int timeToLive) {
		PortalCache<K, V> portalCache = _getPortalCache();

		portalCache.put(key, value, timeToLive);
	}

	@Override
	public V putIfAbsent(K key, V value) {
		LowLevelCache<K, V> lowLevelCache = (LowLevelCache)_getPortalCache();

		return lowLevelCache.putIfAbsent(key, value);
	}

	@Override
	public V putIfAbsent(K key, V value, int timeToLive) {
		LowLevelCache<K, V> lowLevelCache = (LowLevelCache)_getPortalCache();

		return lowLevelCache.putIfAbsent(key, value, timeToLive);
	}

	@Override
	public void registerPortalCacheListener(
		PortalCacheListener<K, V> portalCacheListener) {

		_portalCacheListeners.put(
			portalCacheListener, PortalCacheListenerScope.ALL);

		for (PortalCache<K, V> portalCache : _portalCaches.values()) {
			portalCache.registerPortalCacheListener(portalCacheListener);
		}
	}

	@Override
	public void registerPortalCacheListener(
		PortalCacheListener<K, V> portalCacheListener,
		PortalCacheListenerScope portalCacheListenerScope) {

		_portalCacheListeners.put(
			portalCacheListener, portalCacheListenerScope);

		for (PortalCache<K, V> portalCache : _portalCaches.values()) {
			portalCache.registerPortalCacheListener(
				portalCacheListener, portalCacheListenerScope);
		}
	}

	@Override
	public void remove(K key) {
		PortalCache<K, V> portalCache = _getPortalCache();

		portalCache.remove(key);
	}

	@Override
	public boolean remove(K key, V value) {
		LowLevelCache<K, V> lowLevelCache = (LowLevelCache)_getPortalCache();

		return lowLevelCache.remove(key, value);
	}

	@Override
	public void removeAll() {
		PortalCache<K, V> portalCache = _getPortalCache();

		portalCache.removeAll();
	}

	@Override
	public V replace(K key, V value) {
		LowLevelCache<K, V> lowLevelCache = (LowLevelCache)_getPortalCache();

		return lowLevelCache.replace(key, value);
	}

	@Override
	public V replace(K key, V value, int timeToLive) {
		LowLevelCache<K, V> lowLevelCache = (LowLevelCache)_getPortalCache();

		return lowLevelCache.replace(key, value, timeToLive);
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		LowLevelCache<K, V> lowLevelCache = (LowLevelCache)_getPortalCache();

		return lowLevelCache.replace(key, oldValue, newValue);
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue, int timeToLive) {
		LowLevelCache<K, V> lowLevelCache = (LowLevelCache)_getPortalCache();

		return lowLevelCache.replace(key, oldValue, newValue, timeToLive);
	}

	@Override
	public void unregisterPortalCacheListener(
		PortalCacheListener<K, V> portalCacheListener) {

		_portalCacheListeners.remove(portalCacheListener);

		for (PortalCache<K, V> portalCache : _portalCaches.values()) {
			portalCache.unregisterPortalCacheListener(portalCacheListener);
		}
	}

	@Override
	public void unregisterPortalCacheListeners() {
		_portalCacheListeners.clear();

		for (PortalCache<K, V> portalCache : _portalCaches.values()) {
			portalCache.unregisterPortalCacheListeners();
		}
	}

	private PortalCache<K, V> _getPortalCache() {
		return _portalCaches.computeIfAbsent(
			CompanyThreadLocal.getCompanyId(),
			key -> {
				PortalCache<K, V> portalCache =
					_ehcachePortalCacheManager.createPortalCache(
						(EhcachePortalCacheConfiguration)
							_ehcachePortalCacheConfiguration.
								newPortalCacheConfiguration(
									_portalCacheName + StringPool.UNDERLINE +
										key),
						_portalCacheName);

				for (Map.Entry
						<PortalCacheListener<K, V>, PortalCacheListenerScope>
							entry : _portalCacheListeners.entrySet()) {

					portalCache.registerPortalCacheListener(
						entry.getKey(), entry.getValue());
				}

				return portalCache;
			});
	}

	private final EhcachePortalCacheConfiguration
		_ehcachePortalCacheConfiguration;
	private final EhcachePortalCacheManager<K, V> _ehcachePortalCacheManager;
	private final Map<PortalCacheListener<K, V>, PortalCacheListenerScope>
		_portalCacheListeners = new ConcurrentHashMap<>();
	private final String _portalCacheName;
	private final Map<Long, PortalCache<K, V>> _portalCaches =
		new ConcurrentHashMap<>();

}
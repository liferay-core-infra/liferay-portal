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
import com.liferay.portal.cache.AggregatedPortalCacheListener;
import com.liferay.portal.kernel.cache.PortalCacheListener;
import com.liferay.portal.kernel.cache.PortalCacheListenerScope;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.RegisteredEventListeners;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tina Tian
 */
public class DBPartitionPortalCacheTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() {
		Configuration configuration = new Configuration();

		configuration.addDefaultCache(
			new CacheConfiguration(null, _MAX_ENTRIES_LOCAL_HEAP_DEFAULT));

		configuration.addCache(
			new CacheConfiguration(
				_TEST_CACHE_NAME, _MAX_ENTRIES_LOCAL_HEAP_TEST_CACHE));

		configuration.addCache(
			new CacheConfiguration(
				_TEST_CACHE_NAME + StringPool.UNDERLINE + _TEST_COMPANY_ID_1,
				_MAX_ENTRIES_LOCAL_HEAP_TEST_CACHE_COMPANY_1));

		_cacheManager = CacheManager.newInstance(configuration);

		_ehcachePortalCacheManager = new EhcachePortalCacheManager();

		ReflectionTestUtil.setFieldValue(
			_ehcachePortalCacheManager, "_cacheManager", _cacheManager);

		_companyIdThreadLocal = ReflectionTestUtil.getFieldValue(
			CompanyThreadLocal.class, "_companyId");
	}

	@AfterClass
	public static void tearDownClass() {
		_cacheManager.shutdown();
	}

	@Before
	public void setUp() {
		_dbPartitionPortalCache = new DBPartitionPortalCache(
			_ehcachePortalCacheManager,
			new EhcachePortalCacheConfiguration(
				_TEST_CACHE_NAME, Collections.emptySet(), false));

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		_dbPartitionPortalCache.put(_TEST_KEY_1, _TEST_VALUE_1);

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_2);

		_dbPartitionPortalCache.put(_TEST_KEY_2, _TEST_VALUE_2);
	}

	@After
	public void tearDown() {
		_cacheManager.removeAllCaches();
	}

	@Test
	public void testCacheConfiguration() {
		_assertCacheConfiguration(
			"default cache", _MAX_ENTRIES_LOCAL_HEAP_DEFAULT);
		_assertCacheConfiguration(
			_TEST_CACHE_NAME, _MAX_ENTRIES_LOCAL_HEAP_TEST_CACHE);
		_assertCacheConfiguration(
			_TEST_CACHE_NAME + StringPool.UNDERLINE + _TEST_COMPANY_ID_1,
			_MAX_ENTRIES_LOCAL_HEAP_TEST_CACHE_COMPANY_1);
		_assertCacheConfiguration(
			_TEST_CACHE_NAME + StringPool.UNDERLINE + _TEST_COMPANY_ID_2,
			_MAX_ENTRIES_LOCAL_HEAP_TEST_CACHE);
	}

	@Test
	public void testGet() {
		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		Assert.assertSame(
			_TEST_VALUE_1, _dbPartitionPortalCache.get(_TEST_KEY_1));
		Assert.assertNull(_dbPartitionPortalCache.get(_TEST_KEY_2));

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_2);

		Assert.assertNull(_dbPartitionPortalCache.get(_TEST_KEY_1));
		Assert.assertSame(
			_TEST_VALUE_2, _dbPartitionPortalCache.get(_TEST_KEY_2));
	}

	@Test
	public void testGetKeys() {
		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		Assert.assertEquals(
			Collections.singletonList(_TEST_KEY_1),
			_dbPartitionPortalCache.getKeys());

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_2);

		Assert.assertEquals(
			Collections.singletonList(_TEST_KEY_2),
			_dbPartitionPortalCache.getKeys());
	}

	@Test
	public void testMisc() {
		Assert.assertSame(
			_ehcachePortalCacheManager,
			_dbPartitionPortalCache.getPortalCacheManager());

		Assert.assertEquals(
			_TEST_CACHE_NAME, _dbPartitionPortalCache.getPortalCacheName());

		Assert.assertFalse(_dbPartitionPortalCache.isMVCC());
	}

	@Test
	public void testPut() {
		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		Assert.assertNull(_dbPartitionPortalCache.get(_TEST_KEY_2));

		_dbPartitionPortalCache.put(_TEST_KEY_2, _TEST_VALUE_1);

		Assert.assertSame(
			_TEST_VALUE_1, _dbPartitionPortalCache.get(_TEST_KEY_2));

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_2);

		Assert.assertSame(
			_TEST_VALUE_2, _dbPartitionPortalCache.get(_TEST_KEY_2));

		Assert.assertNull(_dbPartitionPortalCache.get(_TEST_KEY_1));

		_dbPartitionPortalCache.put(_TEST_KEY_1, _TEST_VALUE_2, 1000);

		_assertTimeToLive(_TEST_COMPANY_ID_2, _TEST_KEY_1, _TEST_VALUE_2, 1000);

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		Assert.assertSame(
			_TEST_VALUE_1, _dbPartitionPortalCache.get(_TEST_KEY_1));
	}

	@Test
	public void testPutIfAbsent() {
		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		Assert.assertSame(
			_TEST_VALUE_1, _dbPartitionPortalCache.get(_TEST_KEY_1));

		_dbPartitionPortalCache.putIfAbsent(_TEST_KEY_1, _TEST_VALUE_2);

		Assert.assertSame(
			_TEST_VALUE_1, _dbPartitionPortalCache.get(_TEST_KEY_1));

		Assert.assertNull(_dbPartitionPortalCache.get(_TEST_KEY_2));

		_dbPartitionPortalCache.putIfAbsent(_TEST_KEY_2, _TEST_VALUE_1);

		Assert.assertSame(
			_TEST_VALUE_1, _dbPartitionPortalCache.get(_TEST_KEY_2));

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_2);

		Assert.assertNull(_dbPartitionPortalCache.get(_TEST_KEY_1));

		Assert.assertSame(
			_TEST_VALUE_2, _dbPartitionPortalCache.get(_TEST_KEY_2));

		_dbPartitionPortalCache.putIfAbsent(_TEST_KEY_1, _TEST_VALUE_2, 1000);

		_assertTimeToLive(_TEST_COMPANY_ID_2, _TEST_KEY_1, _TEST_VALUE_2, 1000);

		_dbPartitionPortalCache.putIfAbsent(_TEST_KEY_2, _TEST_VALUE_1, 1000);

		_assertTimeToLive(_TEST_COMPANY_ID_2, _TEST_KEY_2, _TEST_VALUE_2, 0);
	}

	@Test
	public void testRegisterPortalCacheListener() {
		_assertPortalCacheListener(_TEST_COMPANY_ID_1, null);
		_assertPortalCacheListener(_TEST_COMPANY_ID_2, null);

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		PortalCacheListener<?, ?> portalCacheListener1 =
			ProxyFactory.newDummyInstance(PortalCacheListener.class);

		_dbPartitionPortalCache.registerPortalCacheListener(
			portalCacheListener1);

		_assertPortalCacheListener(_TEST_COMPANY_ID_1, portalCacheListener1);
		_assertPortalCacheListener(_TEST_COMPANY_ID_2, portalCacheListener1);

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_2);

		PortalCacheListener<?, ?> portalCacheListener2 =
			ProxyFactory.newDummyInstance(PortalCacheListener.class);

		_dbPartitionPortalCache.registerPortalCacheListener(
			portalCacheListener2, PortalCacheListenerScope.LOCAL);

		Map<?, ?> portalCacheListeners = _getPortalCacheListeners(
			_TEST_COMPANY_ID_1,
			_TEST_CACHE_NAME + StringPool.UNDERLINE + _TEST_COMPANY_ID_1);

		Assert.assertEquals(
			PortalCacheListenerScope.ALL,
			portalCacheListeners.get(portalCacheListener1));

		Assert.assertEquals(
			PortalCacheListenerScope.LOCAL,
			portalCacheListeners.get(portalCacheListener2));

		_assertPortalCacheListener(
			_TEST_COMPANY_ID_2, portalCacheListener1, portalCacheListener2);

		_companyIdThreadLocal.set(3000L);

		_dbPartitionPortalCache.put(_TEST_KEY_1, _TEST_VALUE_1);

		_assertPortalCacheListener(
			3000L, portalCacheListener1, portalCacheListener2);
	}

	@Test
	public void testRemove() {
		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		Assert.assertSame(
			_TEST_VALUE_1, _dbPartitionPortalCache.get(_TEST_KEY_1));

		_dbPartitionPortalCache.remove(_TEST_KEY_1);

		Assert.assertNull(_dbPartitionPortalCache.get(_TEST_KEY_1));

		_dbPartitionPortalCache.put(_TEST_KEY_2, _TEST_VALUE_1);

		Assert.assertSame(
			_TEST_VALUE_1, _dbPartitionPortalCache.get(_TEST_KEY_2));

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_2);

		Assert.assertSame(
			_TEST_VALUE_2, _dbPartitionPortalCache.get(_TEST_KEY_2));

		_dbPartitionPortalCache.remove(_TEST_KEY_2);

		Assert.assertNull(_dbPartitionPortalCache.get(_TEST_KEY_2));

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		Assert.assertSame(
			_TEST_VALUE_1, _dbPartitionPortalCache.get(_TEST_KEY_2));

		_dbPartitionPortalCache.remove(_TEST_KEY_2, _TEST_VALUE_2);

		Assert.assertSame(
			_TEST_VALUE_1, _dbPartitionPortalCache.get(_TEST_KEY_2));

		_dbPartitionPortalCache.remove(_TEST_KEY_2, _TEST_VALUE_1);

		Assert.assertNull(_dbPartitionPortalCache.get(_TEST_KEY_2));
	}

	@Test
	public void testRemoveAll() {
		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		List<?> keys = _dbPartitionPortalCache.getKeys();

		Assert.assertFalse(keys.isEmpty());

		_dbPartitionPortalCache.removeAll();

		keys = _dbPartitionPortalCache.getKeys();

		Assert.assertTrue(keys.isEmpty());

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_2);

		keys = _dbPartitionPortalCache.getKeys();

		Assert.assertFalse(keys.isEmpty());
	}

	@Test
	public void testReplace() {
		_companyIdThreadLocal.set(_TEST_COMPANY_ID_2);

		_dbPartitionPortalCache.put(_TEST_KEY_1, _TEST_VALUE_2);

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		Assert.assertSame(
			_TEST_VALUE_1, _dbPartitionPortalCache.get(_TEST_KEY_1));

		_dbPartitionPortalCache.replace(_TEST_KEY_1, _TEST_VALUE_2);

		Assert.assertSame(
			_TEST_VALUE_2, _dbPartitionPortalCache.get(_TEST_KEY_1));

		_dbPartitionPortalCache.replace(
			_TEST_KEY_1, _TEST_VALUE_2, _TEST_VALUE_1);

		Assert.assertSame(
			_TEST_VALUE_1, _dbPartitionPortalCache.get(_TEST_KEY_1));

		_dbPartitionPortalCache.replace(_TEST_KEY_1, _TEST_VALUE_2, 1000);

		_assertTimeToLive(_TEST_COMPANY_ID_1, _TEST_KEY_1, _TEST_VALUE_2, 1000);

		_dbPartitionPortalCache.replace(
			_TEST_KEY_1, _TEST_VALUE_2, _TEST_VALUE_1, 1000);

		_assertTimeToLive(_TEST_COMPANY_ID_1, _TEST_KEY_1, _TEST_VALUE_1, 1000);

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_2);

		Assert.assertSame(
			_TEST_VALUE_2, _dbPartitionPortalCache.get(_TEST_KEY_1));
	}

	@Test
	public void testUnregisterPortalCacheListener() {
		_assertPortalCacheListener(_TEST_COMPANY_ID_1, null);
		_assertPortalCacheListener(_TEST_COMPANY_ID_2, null);

		PortalCacheListener<?, ?> portalCacheListener1 =
			ProxyFactory.newDummyInstance(PortalCacheListener.class);

		_dbPartitionPortalCache.registerPortalCacheListener(
			portalCacheListener1);

		PortalCacheListener<?, ?> portalCacheListener2 =
			ProxyFactory.newDummyInstance(PortalCacheListener.class);

		_dbPartitionPortalCache.registerPortalCacheListener(
			portalCacheListener2);

		_assertPortalCacheListener(
			_TEST_COMPANY_ID_1, portalCacheListener1, portalCacheListener2);
		_assertPortalCacheListener(
			_TEST_COMPANY_ID_2, portalCacheListener1, portalCacheListener2);

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		_dbPartitionPortalCache.unregisterPortalCacheListener(
			portalCacheListener1);

		_assertPortalCacheListener(_TEST_COMPANY_ID_1, portalCacheListener2);
		_assertPortalCacheListener(_TEST_COMPANY_ID_2, portalCacheListener2);

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_2);

		_dbPartitionPortalCache.unregisterPortalCacheListener(
			portalCacheListener2);

		_assertPortalCacheListener(_TEST_COMPANY_ID_1, null);
		_assertPortalCacheListener(_TEST_COMPANY_ID_2, null);
	}

	@Test
	public void testUnregisterPortalCacheListeners() {
		_assertPortalCacheListener(_TEST_COMPANY_ID_1, null);
		_assertPortalCacheListener(_TEST_COMPANY_ID_2, null);

		PortalCacheListener<?, ?> portalCacheListener1 =
			ProxyFactory.newDummyInstance(PortalCacheListener.class);

		_dbPartitionPortalCache.registerPortalCacheListener(
			portalCacheListener1);

		PortalCacheListener<?, ?> portalCacheListener2 =
			ProxyFactory.newDummyInstance(PortalCacheListener.class);

		_dbPartitionPortalCache.registerPortalCacheListener(
			portalCacheListener2);

		_assertPortalCacheListener(
			_TEST_COMPANY_ID_1, portalCacheListener1, portalCacheListener2);
		_assertPortalCacheListener(
			_TEST_COMPANY_ID_2, portalCacheListener1, portalCacheListener2);

		_dbPartitionPortalCache.unregisterPortalCacheListeners();

		_assertPortalCacheListener(_TEST_COMPANY_ID_1, null);
		_assertPortalCacheListener(_TEST_COMPANY_ID_2, null);
	}

	private void _assertCacheConfiguration(
		String cacheName, int maxEntriesLocalHeap) {

		Cache cache = _cacheManager.getCache(cacheName);

		if (cache == null) {
			_cacheManager.addCache(cacheName);

			cache = _cacheManager.getCache(cacheName);
		}

		CacheConfiguration cacheConfiguration = cache.getCacheConfiguration();

		Assert.assertEquals(
			maxEntriesLocalHeap, cacheConfiguration.getMaxEntriesLocalHeap());
	}

	private void _assertPortalCacheListener(
		long companyId,
		PortalCacheListener<?, ?>... registeredPortalCacheListeners) {

		_companyIdThreadLocal.set(companyId);

		for (String cacheName : _cacheManager.getCacheNames()) {
			if (!cacheName.startsWith(_TEST_CACHE_NAME)) {
				continue;
			}

			Map<?, ?> portalCacheListeners = _getPortalCacheListeners(
				companyId, cacheName);

			if (registeredPortalCacheListeners == null) {
				Assert.assertTrue(portalCacheListeners.isEmpty());
			}
			else {
				Assert.assertEquals(
					portalCacheListeners.toString(),
					registeredPortalCacheListeners.length,
					portalCacheListeners.size());

				for (PortalCacheListener<?, ?> portalCacheListener :
						registeredPortalCacheListeners) {

					Assert.assertNotNull(
						portalCacheListeners.get(portalCacheListener));
				}
			}
		}
	}

	private void _assertTimeToLive(
		long companyId, String key, String value, int timeToLive) {

		Cache cache = _cacheManager.getCache(
			_TEST_CACHE_NAME + StringPool.UNDERLINE + companyId);

		Element element = cache.get(key);

		Assert.assertEquals(key, element.getObjectKey());
		Assert.assertEquals(value, element.getObjectValue());
		Assert.assertEquals(timeToLive, element.getTimeToLive());
	}

	private Map<?, ?> _getPortalCacheListeners(
		long companyId, String cacheName) {

		_companyIdThreadLocal.set(companyId);

		Cache cache = _cacheManager.getCache(cacheName);

		RegisteredEventListeners registeredEventListeners =
			cache.getCacheEventNotificationService();

		Set<CacheEventListener> cacheEventListeners =
			registeredEventListeners.getCacheEventListeners();

		Assert.assertEquals(
			cacheEventListeners.toString(), 1, cacheEventListeners.size());

		Iterator<CacheEventListener> iterator = cacheEventListeners.iterator();

		AggregatedPortalCacheListener<?, ?> aggregatedPortalCacheListener =
			ReflectionTestUtil.getFieldValue(
				iterator.next(), "_aggregatedPortalCacheListener");

		return aggregatedPortalCacheListener.getPortalCacheListeners();
	}

	private static final int _MAX_ENTRIES_LOCAL_HEAP_DEFAULT = 100;

	private static final int _MAX_ENTRIES_LOCAL_HEAP_TEST_CACHE = 200;

	private static final int _MAX_ENTRIES_LOCAL_HEAP_TEST_CACHE_COMPANY_1 = 300;

	private static final String _TEST_CACHE_NAME = "TEST_CACHE_NAME";

	private static final long _TEST_COMPANY_ID_1 = 1000L;

	private static final long _TEST_COMPANY_ID_2 = 2000L;

	private static final String _TEST_KEY_1 = "TEST_KEY_1";

	private static final String _TEST_KEY_2 = "TEST_KEY_2";

	private static final String _TEST_VALUE_1 = "TEST_VALUE_1";

	private static final String _TEST_VALUE_2 = "TEST_VALUE_2";

	private static CacheManager _cacheManager;
	private static ThreadLocal<Long> _companyIdThreadLocal;
	private static EhcachePortalCacheManager _ehcachePortalCacheManager;

	private DBPartitionPortalCache _dbPartitionPortalCache;

}
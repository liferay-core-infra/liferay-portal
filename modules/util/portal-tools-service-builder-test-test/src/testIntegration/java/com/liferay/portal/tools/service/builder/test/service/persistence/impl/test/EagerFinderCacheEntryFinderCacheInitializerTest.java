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

package com.liferay.portal.tools.service.builder.test.service.persistence.impl.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.model.EagerFinderCacheEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.EagerFinderCacheEntryPersistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class EagerFinderCacheEntryFinderCacheInitializerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Test
	public void testPopulateWithoutPaginationFindersCache() throws Exception {
		_eagerFinderCacheEntries = new ArrayList<>();

		_eagerFinderCacheEntries.add(
			_addEagerFinderCacheEntry(1L, 10L, "test1"));
		_eagerFinderCacheEntries.add(
			_addEagerFinderCacheEntry(1L, 10L, "test2"));
		_eagerFinderCacheEntries.add(
			_addEagerFinderCacheEntry(1L, 10L, "test3"));
		_eagerFinderCacheEntries.add(
			_addEagerFinderCacheEntry(1L, 11L, "test4"));
		_eagerFinderCacheEntries.add(
			_addEagerFinderCacheEntry(1L, 11L, "test5"));

		_eagerFinderCacheEntryPersistence.clearCache();

		Assert.assertNull(
			_fetchFromFinderCache(
				"_finderPathFetchByUniqueName", new Object[] {"test1"}));

		Assert.assertNull(
			_fetchFromFinderCache(
				"_finderPathWithoutPaginationFindByGroupId",
				new Object[] {10L}));

		Assert.assertNull(
			_fetchFromFinderCache(
				"_finderPathWithoutPaginationFindByGroupId",
				new Object[] {11L}));

		Assert.assertNull(
			_fetchFromFinderCache(
				"_finderPathWithoutPaginationFindByC_G",
				new Object[] {1L, 10L}));

		Assert.assertNull(
			_fetchFromFinderCache(
				"_finderPathWithoutPaginationFindByC_G",
				new Object[] {1L, 11L}));

		Class<?> clazz = _eagerFinderCacheEntryPersistence.getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		Class<?> eagerFinderCacheEntryFinderCacheInitializerClass =
			classLoader.loadClass(
				"com.liferay.portal.tools.service.builder.test.service." +
					"persistence.impl." +
						"EagerFinderCacheEntryFinderCacheInitializer");

		ReflectionTestUtil.invoke(
			eagerFinderCacheEntryFinderCacheInitializerClass.newInstance(),
			"_initializeFinderCache",
			new Class<?>[] {
				EagerFinderCacheEntryPersistence.class, FinderCache.class
			},
			_eagerFinderCacheEntryPersistence, _finderCache);

		Assert.assertArrayEquals(
			new String[] {"test1"},
			_fetchFromFinderCache(
				"_finderPathFetchByUniqueName", new Object[] {"test1"}));

		Assert.assertArrayEquals(
			new String[] {"test1", "test2", "test3"},
			_fetchFromFinderCache(
				"_finderPathWithoutPaginationFindByGroupId",
				new Object[] {10L}));

		Assert.assertArrayEquals(
			new String[] {"test4", "test5"},
			_fetchFromFinderCache(
				"_finderPathWithoutPaginationFindByGroupId",
				new Object[] {11L}));

		Assert.assertArrayEquals(
			new String[] {"test1", "test2", "test3"},
			_fetchFromFinderCache(
				"_finderPathWithoutPaginationFindByC_G",
				new Object[] {1L, 10L}));

		Assert.assertArrayEquals(
			new String[] {"test4", "test5"},
			_fetchFromFinderCache(
				"_finderPathWithoutPaginationFindByC_G",
				new Object[] {1L, 11L}));
	}

	private EagerFinderCacheEntry _addEagerFinderCacheEntry(
		long companyId, long groupId, String uniqueName) {

		EagerFinderCacheEntry eagerFinderCacheEntry =
			_eagerFinderCacheEntryPersistence.create(RandomTestUtil.nextLong());

		eagerFinderCacheEntry.setCompanyId(companyId);
		eagerFinderCacheEntry.setGroupId(groupId);
		eagerFinderCacheEntry.setUniqueName(uniqueName);

		return _eagerFinderCacheEntryPersistence.update(eagerFinderCacheEntry);
	}

	private String[] _fetchFromFinderCache(
		String finderPathFieldName, Object[] arguments) {

		Object result = _finderCache.getResult(
			ReflectionTestUtil.getFieldValue(
				_eagerFinderCacheEntryPersistence, finderPathFieldName),
			arguments);

		if (result == null) {
			return null;
		}

		List<EagerFinderCacheEntry> eagerFinderCacheEntries;

		if (result instanceof List) {
			eagerFinderCacheEntries = (List<EagerFinderCacheEntry>)result;
		}
		else {
			eagerFinderCacheEntries = Collections.singletonList(
				(EagerFinderCacheEntry)result);
		}

		List<String> uniqueNames = new ArrayList<>();

		for (EagerFinderCacheEntry eagerFinderCacheEntry :
				eagerFinderCacheEntries) {

			uniqueNames.add(eagerFinderCacheEntry.getUniqueName());
		}

		Collections.sort(uniqueNames);

		return uniqueNames.toArray(new String[0]);
	}

	@DeleteAfterTestRun
	private List<EagerFinderCacheEntry> _eagerFinderCacheEntries;

	@Inject
	private EagerFinderCacheEntryPersistence _eagerFinderCacheEntryPersistence;

	@Inject
	private FinderCache _finderCache;

}
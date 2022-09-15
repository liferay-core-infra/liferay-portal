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

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.model.FinderCachePopulationEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.FinderCachePopulationEntryPersistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class FinderCachePopulationEntryTest {

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
		_finderCachePopulationEntries = new ArrayList<>();

		_finderCachePopulationEntries.add(
			_addFinderCachePopulationEntry(1L, 10L, "test1"));
		_finderCachePopulationEntries.add(
			_addFinderCachePopulationEntry(1L, 10L, "test2"));
		_finderCachePopulationEntries.add(
			_addFinderCachePopulationEntry(1L, 10L, "test3"));
		_finderCachePopulationEntries.add(
			_addFinderCachePopulationEntry(1L, 11L, "test4"));
		_finderCachePopulationEntries.add(
			_addFinderCachePopulationEntry(1L, 11L, "test5"));

		_finderCachePopulationEntryPersistence.clearCache();

		Map<String, FinderPath> finderPaths =
			_finderCachePopulationEntryPersistence.getFinderPaths();

		FinderPath finderPathFetchByUniqueName = finderPaths.get(
			"finderPathFetchByUniqueName");
		FinderPath finderPathWithoutPaginationFindByGroupId = finderPaths.get(
			"finderPathWithoutPaginationFindByGroupId");
		FinderPath finderPathWithoutPaginationFindByC_G = finderPaths.get(
			"finderPathWithoutPaginationFindByC_G");

		Assert.assertNull(
			_fetchFromFinderCache(
				finderPathFetchByUniqueName, new Object[] {"test1"}));

		Assert.assertNull(
			_fetchFromFinderCache(
				finderPathWithoutPaginationFindByGroupId, new Object[] {10L}));

		Assert.assertNull(
			_fetchFromFinderCache(
				finderPathWithoutPaginationFindByGroupId, new Object[] {11L}));

		Assert.assertNull(
			_fetchFromFinderCache(
				finderPathWithoutPaginationFindByC_G, new Object[] {1L, 10L}));

		Assert.assertNull(
			_fetchFromFinderCache(
				finderPathWithoutPaginationFindByC_G, new Object[] {1L, 11L}));

		_finderCachePopulationEntryPersistence.populateFinderCache(
			finderPathFetchByUniqueName,
			finderPathWithoutPaginationFindByGroupId,
			finderPathWithoutPaginationFindByC_G);

		Assert.assertArrayEquals(
			new String[] {"test1"},
			_fetchFromFinderCache(
				finderPathFetchByUniqueName, new Object[] {"test1"}));

		Assert.assertArrayEquals(
			new String[] {"test1", "test2", "test3"},
			_fetchFromFinderCache(
				finderPathWithoutPaginationFindByGroupId, new Object[] {10L}));

		Assert.assertArrayEquals(
			new String[] {"test4", "test5"},
			_fetchFromFinderCache(
				finderPathWithoutPaginationFindByGroupId, new Object[] {11L}));

		Assert.assertArrayEquals(
			new String[] {"test1", "test2", "test3"},
			_fetchFromFinderCache(
				finderPathWithoutPaginationFindByC_G, new Object[] {1L, 10L}));

		Assert.assertArrayEquals(
			new String[] {"test4", "test5"},
			_fetchFromFinderCache(
				finderPathWithoutPaginationFindByC_G, new Object[] {1L, 11L}));
	}

	private FinderCachePopulationEntry _addFinderCachePopulationEntry(
		long companyId, long groupId, String uniqueName) {

		FinderCachePopulationEntry finderCachePopulationEntry =
			_finderCachePopulationEntryPersistence.create(
				RandomTestUtil.nextLong());

		finderCachePopulationEntry.setCompanyId(companyId);
		finderCachePopulationEntry.setGroupId(groupId);
		finderCachePopulationEntry.setUniqueName(uniqueName);

		return _finderCachePopulationEntryPersistence.update(
			finderCachePopulationEntry);
	}

	private String[] _fetchFromFinderCache(
		FinderPath finderPath, Object[] arguments) {

		Object result = _finderCache.getResult(finderPath, arguments);

		if (result == null) {
			return null;
		}

		List<FinderCachePopulationEntry> finderCachePopulationEntries;

		if (result instanceof List) {
			finderCachePopulationEntries =
				(List<FinderCachePopulationEntry>)result;
		}
		else {
			finderCachePopulationEntries = Collections.singletonList(
				(FinderCachePopulationEntry)result);
		}

		List<String> uniqueNames = new ArrayList<>();

		for (FinderCachePopulationEntry finderCachePopulationEntry :
				finderCachePopulationEntries) {

			uniqueNames.add(finderCachePopulationEntry.getUniqueName());
		}

		Collections.sort(uniqueNames);

		return uniqueNames.toArray(new String[0]);
	}

	@Inject
	private FinderCache _finderCache;

	@DeleteAfterTestRun
	private List<FinderCachePopulationEntry> _finderCachePopulationEntries;

	@Inject
	private FinderCachePopulationEntryPersistence
		_finderCachePopulationEntryPersistence;

}
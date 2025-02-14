/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.service.builder.test.model.SortedFinderEntry;
import com.liferay.portal.tools.service.builder.test.service.SortedFinderEntryLocalService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kevin Lee
 */
@RunWith(Arquillian.class)
public class SortedFinderEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testFetchSortedFinderEntryTest() {
		SortedFinderEntry sortedFinderEntry1 = _addSortedFinderEntry("1");
		SortedFinderEntry sortedFinderEntry2 = _addSortedFinderEntry("2");

		Assert.assertTrue(sortedFinderEntry1.compareTo(sortedFinderEntry2) < 0);

		Assert.assertEquals(
			sortedFinderEntry2,
			_sortedFinderEntryLocalService.fetchSortedFinderEntryByGroupId(0));

		SortedFinderEntry sortedFinderEntry3 = _addSortedFinderEntry("3");

		Assert.assertTrue(sortedFinderEntry1.compareTo(sortedFinderEntry3) < 0);
		Assert.assertTrue(sortedFinderEntry2.compareTo(sortedFinderEntry3) < 0);

		Assert.assertEquals(
			sortedFinderEntry3,
			_sortedFinderEntryLocalService.fetchSortedFinderEntryByGroupId(0));
	}

	private SortedFinderEntry _addSortedFinderEntry(String name) {
		SortedFinderEntry sortedFinderEntry =
			_sortedFinderEntryLocalService.createSortedFinderEntry(
				RandomTestUtil.randomLong());

		sortedFinderEntry.setName(name);
		sortedFinderEntry.setGroupId(0);

		sortedFinderEntry =
			_sortedFinderEntryLocalService.updateSortedFinderEntry(
				sortedFinderEntry);

		_sortedFinderEntries.add(sortedFinderEntry);

		return sortedFinderEntry;
	}

	@DeleteAfterTestRun
	private final List<SortedFinderEntry> _sortedFinderEntries =
		new ArrayList<>();

	@Inject
	private SortedFinderEntryLocalService _sortedFinderEntryLocalService;

}
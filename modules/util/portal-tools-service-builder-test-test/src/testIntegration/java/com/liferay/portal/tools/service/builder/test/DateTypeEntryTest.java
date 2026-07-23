/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.tools.service.builder.test.model.DateTypeEntry;
import com.liferay.portal.tools.service.builder.test.service.DateTypeEntryLocalService;
import com.liferay.portal.tools.service.builder.test.service.persistence.DateTypeEntryPersistence;

import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class DateTypeEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE);

	@Test
	public void test() throws Exception {
		DateTypeEntry dateTypeEntry =
			_dateTypeEntryLocalService.createDateTypeEntry(
				RandomTestUtil.nextLong());

		Date date = new Date();

		dateTypeEntry.setDateValue(date);

		dateTypeEntry = _dateTypeEntryLocalService.addDateTypeEntry(
			dateTypeEntry);

		_assertDate(date, dateTypeEntry.getDateValue());

		dateTypeEntry = _dateTypeEntryLocalService.fetchDateTypeEntry(
			dateTypeEntry.getDateTypeEntryId());

		_assertDate(date, dateTypeEntry.getDateValue());

		_dateTypeEntryPersistence.clearCache();

		dateTypeEntry = _dateTypeEntryLocalService.fetchDateTypeEntry(
			dateTypeEntry.getDateTypeEntryId());

		_assertDate(date, dateTypeEntry.getDateValue());

		Date newDate = new Date(date.getTime() + 1000);

		dateTypeEntry.setDateValue(newDate);

		dateTypeEntry = _dateTypeEntryLocalService.updateDateTypeEntry(
			dateTypeEntry);

		_assertDate(newDate, dateTypeEntry.getDateValue());

		dateTypeEntry = _dateTypeEntryLocalService.getDateTypeEntry(
			dateTypeEntry.getDateTypeEntryId());

		_assertDate(newDate, dateTypeEntry.getDateValue());

		_dateTypeEntryPersistence.clearCache();

		dateTypeEntry = _dateTypeEntryLocalService.getDateTypeEntry(
			dateTypeEntry.getDateTypeEntryId());

		_assertDate(newDate, dateTypeEntry.getDateValue());
	}

	private void _assertDate(Date date1, Date date2) {
		Assert.assertEquals(Date.class, date1.getClass());
		Assert.assertEquals(Date.class, date2.getClass());

		Assert.assertEquals(date1, date2);
	}

	@Inject
	private DateTypeEntryLocalService _dateTypeEntryLocalService;

	@Inject
	private DateTypeEntryPersistence _dateTypeEntryPersistence;

}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.PortalPreferences;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.service.persistence.PortalPreferencesPersistence;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.service.persistence.impl.PortalPreferencesPersistenceImpl;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

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
public class PortalPreferencesLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			new TransactionalTestRule(Propagation.REQUIRED));

	@Test
	public void testFetchPortalPreferencesForCompanyWithDuplicatedEntries()
		throws Exception {

		long companyId = RandomTestUtil.randomLong();
		long portalPreferencesId = RandomTestUtil.randomLong();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				PortalPreferencesPersistenceImpl.class.getName(),
				LoggerTestUtil.WARN)) {

			PortalPreferences portalPreferences1 = _addPortalPreferences(
				companyId, portalPreferencesId);

			PortalPreferences portalPreferences2 = _addPortalPreferences(
				companyId, portalPreferencesId + 1);

			Assert.assertNotEquals(portalPreferences1, portalPreferences2);

			_portalPreferencesPersistence.clearCache();

			Assert.assertEquals(
				portalPreferences2,
				_portalPreferencesPersistence.fetchByO_O(
					companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"PortalPreferencesPersistenceImpl.fetchByO_O(long, int, ",
					"boolean) with parameters (", companyId,
					",1) yields a result set with more than 1 result. This ",
					"violates the logical unique restriction. There is no ",
					"order guarantee on which result is returned by this ",
					"finder."),
				logEntry.getMessage());

			_portalPreferencesPersistence.clearCache();

			Assert.assertEquals(
				portalPreferences2,
				_portalPreferencesLocalService.fetchPortalPreferences(
					companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY));
		}
		finally {
			_portalPreferencesPersistence.remove(portalPreferencesId);
			_portalPreferencesPersistence.remove(portalPreferencesId + 1);
		}
	}

	private PortalPreferences _addPortalPreferences(
		long companyId, long portalPreferencesId) {

		PortalPreferences portalPreferences =
			_portalPreferencesPersistence.create(portalPreferencesId);

		portalPreferences.setCompanyId(companyId);
		portalPreferences.setOwnerId(companyId);
		portalPreferences.setOwnerType(PortletKeys.PREFS_OWNER_TYPE_COMPANY);

		return _portalPreferencesPersistence.update(portalPreferences);
	}

	@Inject
	private PortalPreferencesLocalService _portalPreferencesLocalService;

	@Inject
	private PortalPreferencesPersistence _portalPreferencesPersistence;

}
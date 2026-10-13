/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry;
import com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntryVersion;
import com.liferay.portal.tools.service.builder.test.service.ERCVersionedEntryLocalService;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class ERCVersionedEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testPublishDraft() throws Exception {
		ERCVersionedEntry draftERCVersionedEntry =
			_ercVersionedEntryLocalService.updateDraft(
				_ercVersionedEntryLocalService.create());

		draftERCVersionedEntry.setStatus(WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, draftERCVersionedEntry.getStatus());

		ERCVersionedEntry ercVersionedEntry =
			_ercVersionedEntryLocalService.publishDraft(draftERCVersionedEntry);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, ercVersionedEntry.getStatus());

		ERCVersionedEntryVersion ercVersionedEntryVersion =
			_ercVersionedEntryLocalService.fetchLatestVersion(
				ercVersionedEntry);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			ercVersionedEntryVersion.getStatus());
	}

	@Inject
	private ERCVersionedEntryLocalService _ercVersionedEntryLocalService;

}
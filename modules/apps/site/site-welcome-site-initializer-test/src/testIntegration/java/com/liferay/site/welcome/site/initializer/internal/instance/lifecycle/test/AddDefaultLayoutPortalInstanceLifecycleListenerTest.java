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

package com.liferay.site.welcome.site.initializer.internal.instance.lifecycle.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Hai Yu
 */
@RunWith(Arquillian.class)
public class AddDefaultLayoutPortalInstanceLifecycleListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddDefaultGuestPublicPageWithChangeCompanyDefaultLocale()
		throws Exception {

		String originalLanguageId = PropsValues.COMPANY_DEFAULT_LOCALE;

		PropsValues.COMPANY_DEFAULT_LOCALE = "es_ES";

		try {
			_company = CompanyTestUtil.addCompany();

			Group guestGroup = _groupLocalService.getGroup(
				_company.getCompanyId(), GroupConstants.GUEST);

			Assert.assertNotNull(
				_layoutLocalService.fetchLayoutByFriendlyURL(
					guestGroup.getGroupId(), false,
					PropsValues.DEFAULT_GUEST_PUBLIC_LAYOUT_FRIENDLY_URL));
		}
		finally {
			PropsValues.COMPANY_DEFAULT_LOCALE = originalLanguageId;
		}
	}

	@DeleteAfterTestRun
	private Company _company;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

}
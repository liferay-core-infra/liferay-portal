/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import com.liferay.portal.model.impl.PortletAppImpl;
import com.liferay.portal.model.impl.PortletImpl;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.portlet.PortletConfig;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockServletContext;

/**
 * @author Kevin Lee
 */
@RunWith(Arquillian.class)
public class PortletConfigImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetResourceBundle() throws PortalException {
		String portletId = RandomTestUtil.randomString();

		Portlet portlet = new PortletImpl(0L, portletId) {
			{
				setPortletApp(new PortletAppImpl(null));
			}
		};

		PortletConfig portletConfig = PortletConfigFactoryUtil.create(
			portlet, new MockServletContext());

		Locale locale = LocaleUtil.getDefault();

		String languageId = _language.getLanguageId(locale);

		ResourceBundle resourceBundle = portletConfig.getResourceBundle(locale);

		Assert.assertEquals(
			portletId, _language.get(resourceBundle, portletId));

		PLOEntry ploEntry = _ploEntryLocalService.addOrUpdatePLOEntry(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			portletId, languageId, "value");

		try {
			resourceBundle = portletConfig.getResourceBundle(locale);

			Assert.assertEquals(
				"value", _language.get(resourceBundle, portletId));
		}
		finally {
			_ploEntryLocalService.deletePLOEntry(ploEntry);
		}
	}

	@Inject
	private Language _language;

	@Inject
	private PLOEntryLocalService _ploEntryLocalService;

}
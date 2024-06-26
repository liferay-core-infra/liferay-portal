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
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
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
import org.junit.Before;
import org.junit.BeforeClass;
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

	@BeforeClass
	public static void setUpClass() {
		_portlet = new PortletImpl(0L, RandomTestUtil.randomString()) {
			{
				setPortletApp(new PortletAppImpl(null));
			}
		};
	}

	@Before
	public void setUp() {
		_portletConfig = PortletConfigFactoryUtil.create(
			_portlet, new MockServletContext());

		_locale = LocaleUtil.getDefault();

		_languageId = _language.getLanguageId(_locale);
	}

	@Test
	public void testGetResourceBundle() throws PortalException {
		ResourceBundle resourceBundle = _portletConfig.getResourceBundle(
			_locale);

		Assert.assertEquals(
			_LANGUAGE_KEY, _language.get(resourceBundle, _LANGUAGE_KEY));

		_overrideLanguageKey("value");

		resourceBundle = _portletConfig.getResourceBundle(_locale);

		Assert.assertEquals(
			"value", _language.get(resourceBundle, _LANGUAGE_KEY));
	}

	private void _overrideLanguageKey(String value) throws PortalException {
		_ploEntry = _ploEntryLocalService.addOrUpdatePLOEntry(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			_LANGUAGE_KEY, _languageId, value);
	}

	private static final String _LANGUAGE_KEY = RandomTestUtil.randomString();

	private static Portlet _portlet;

	@Inject
	private Language _language;

	private String _languageId;
	private Locale _locale;

	@DeleteAfterTestRun
	private PLOEntry _ploEntry;

	@Inject
	private PLOEntryLocalService _ploEntryLocalService;

	private PortletConfig _portletConfig;

}
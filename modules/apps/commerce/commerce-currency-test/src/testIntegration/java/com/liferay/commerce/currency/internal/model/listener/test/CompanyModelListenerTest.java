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

package com.liferay.commerce.currency.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.portal.kernel.model.Company;
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
public class CompanyModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testImportDefaultCommerceCurrencyWithChangeCompanyDefaultLocale()
		throws Exception {

		String originalLanguageId = PropsValues.COMPANY_DEFAULT_LOCALE;

		PropsValues.COMPANY_DEFAULT_LOCALE = "es_ES";

		try {
			_company = CompanyTestUtil.addCompany();

			int count =
				_commerceCurrencyLocalService.getCommerceCurrenciesCount(
					_company.getCompanyId());

			Assert.assertTrue(count > 0);

			String[] commerceCurrencyCodes = {
				"USD", "AUD", "GBP", "CAD", "CNY", "EUR", "HKD", "JPY", "INR",
				"BRL"
			};

			for (String commerceCurrencyCode : commerceCurrencyCodes) {
				Assert.assertNotNull(
					_commerceCurrencyLocalService.getCommerceCurrency(
						_company.getCompanyId(), commerceCurrencyCode));
			}
		}
		finally {
			PropsValues.COMPANY_DEFAULT_LOCALE = originalLanguageId;
		}
	}

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@DeleteAfterTestRun
	private Company _company;

}
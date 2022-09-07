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

package com.liferay.commerce.currency.internal.model;

import com.liferay.commerce.currency.configuration.RoundingTypeConfiguration;
import com.liferay.commerce.currency.constants.CommerceCurrencyConstants;
import com.liferay.commerce.currency.constants.RoundingTypeConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.impl.CommerceCurrencyImpl;
import com.liferay.commerce.currency.util.ExchangeRateProvider;
import com.liferay.commerce.currency.util.ExchangeRateProviderRegistry;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.settings.SystemSettingsLocator;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.uuid.PortalUUID;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Janis Zhang
 */
public class DefaultCommerceCurrencyImporter {

	public DefaultCommerceCurrencyImporter(
		ServiceContext serviceContext,
		ConfigurationProvider configurationProvider,
		CounterLocalService counterLocalService,
		UserLocalService userLocalService, PortalUUID portalUUID,
		ExchangeRateProviderRegistry exchangeRateProviderRegistry) {

		_serviceContext = serviceContext;
		_configurationProvider = configurationProvider;
		_counterLocalService = counterLocalService;
		_userLocalService = userLocalService;
		_portalUUID = portalUUID;
		_exchangeRateProviderRegistry = exchangeRateProviderRegistry;
	}

	public List<CommerceCurrency> getCommerceCurrency(String countriesJSON)
		throws PortalException {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(countriesJSON);

		String firstPrimaryCommerceCurrencyCode = null;

		List<CommerceCurrency> commerceCurrencies = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			String code = jsonObject.getString("code");

			if (jsonObject.getBoolean("primary") &&
				(firstPrimaryCommerceCurrencyCode == null)) {

				firstPrimaryCommerceCurrencyCode = code;
			}

			boolean primary = jsonObject.getBoolean("primary");

			String symbol = jsonObject.getString("symbol");

			RoundingTypeConfiguration roundingTypeConfiguration =
				_configurationProvider.getConfiguration(
					RoundingTypeConfiguration.class,
					new SystemSettingsLocator(
						RoundingTypeConstants.SERVICE_NAME));

			Map<Locale, String> formatPatternMap = HashMapBuilder.put(
				_serviceContext.getLocale(),
				StringBundler.concat(
					symbol, StringPool.SPACE,
					CommerceCurrencyConstants.DECIMAL_FORMAT_PATTERN)
			).build();

			RoundingMode roundingMode =
				roundingTypeConfiguration.roundingMode();

			if (formatPatternMap.isEmpty()) {
				formatPatternMap.put(
					_serviceContext.getLocale(),
					CommerceCurrencyConstants.DECIMAL_FORMAT_PATTERN);
			}

			User user = _userLocalService.getUser(_serviceContext.getUserId());

			CommerceCurrency commerceCurrency = new CommerceCurrencyImpl();

			commerceCurrency.setUuid(_portalUUID.generate());
			commerceCurrency.setCommerceCurrencyId(
				_counterLocalService.increment());
			commerceCurrency.setCompanyId(_serviceContext.getCompanyId());
			commerceCurrency.setUserId(user.getUserId());
			commerceCurrency.setUserName(user.getFullName());

			Date date = new Date();

			commerceCurrency.setCreateDate(date);
			commerceCurrency.setModifiedDate(date);

			commerceCurrency.setCode(code);
			commerceCurrency.setNameMap(
				HashMapBuilder.put(
					_serviceContext.getLocale(), jsonObject.getString("name")
				).build());
			commerceCurrency.setFormatPatternMap(formatPatternMap);
			commerceCurrency.setSymbol(symbol);

			BigDecimal exchangeRate = BigDecimal.ONE;

			if (!primary) {
				exchangeRate = _getExchangeRate(
					firstPrimaryCommerceCurrencyCode, code);
			}

			commerceCurrency.setRate(exchangeRate);
			commerceCurrency.setMaxFractionDigits(
				roundingTypeConfiguration.maximumFractionDigits());
			commerceCurrency.setMinFractionDigits(
				roundingTypeConfiguration.minimumFractionDigits());
			commerceCurrency.setRoundingMode(roundingMode.name());
			commerceCurrency.setPrimary(primary);
			commerceCurrency.setPriority(jsonObject.getDouble("priority"));
			commerceCurrency.setActive(true);

			commerceCurrencies.add(commerceCurrency);
		}

		return commerceCurrencies;
	}

	private BigDecimal _getExchangeRate(
		String primaryCommerceCurrencyCode,
		String currentCommerceCurrencyCode) {

		CommerceCurrency primaryCommerceCurrency = new CommerceCurrencyImpl();

		primaryCommerceCurrency.setCode(primaryCommerceCurrencyCode);

		CommerceCurrency commerceCurrency = new CommerceCurrencyImpl();

		commerceCurrency.setCode(currentCommerceCurrencyCode);

		for (String exchangeRateProviderKey :
				_exchangeRateProviderRegistry.getExchangeRateProviderKeys()) {

			ExchangeRateProvider exchangeRateProvider =
				_exchangeRateProviderRegistry.getExchangeRateProvider(
					exchangeRateProviderKey);

			if (exchangeRateProvider == null) {
				return BigDecimal.ONE;
			}

			try {
				return exchangeRateProvider.getExchangeRate(
					primaryCommerceCurrency, commerceCurrency);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		return BigDecimal.ONE;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultCommerceCurrencyImporter.class);

	private final ConfigurationProvider _configurationProvider;
	private final CounterLocalService _counterLocalService;
	private final ExchangeRateProviderRegistry _exchangeRateProviderRegistry;
	private final PortalUUID _portalUUID;
	private final ServiceContext _serviceContext;
	private final UserLocalService _userLocalService;

}
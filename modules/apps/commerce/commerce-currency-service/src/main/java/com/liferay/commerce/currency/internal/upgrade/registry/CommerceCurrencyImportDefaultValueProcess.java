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

package com.liferay.commerce.currency.internal.upgrade.registry;

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
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.settings.SystemSettingsLocator;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.uuid.PortalUUID;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * @author Janis Zhang
 */
public class CommerceCurrencyImportDefaultValueProcess extends UpgradeProcess {

	public CommerceCurrencyImportDefaultValueProcess(
		CompanyLocalService companyLocalService,
		ConfigurationProvider configurationProvider,
		CounterLocalService counterLocalService,
		UserLocalService userLocalService, PortalUUID portalUUID,
		ExchangeRateProviderRegistry exchangeRateProviderRegistry) {

		_companyLocalService = companyLocalService;
		_configurationProvider = configurationProvider;
		_counterLocalService = counterLocalService;
		_userLocalService = userLocalService;
		_portalUUID = portalUUID;
		_exchangeRateProviderRegistry = exchangeRateProviderRegistry;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompany(
			company -> {
				try {
					if (connection == null) {
						connection = super.getConnection();
					}

					ResultSet resultSet = _fetchPrimaryCommerceCurrency(
						company);

					while (resultSet.next()) {
						return;
					}

					_importDefaultValues(company);
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			});
	}

	private CommerceCurrency _fetchByC_C(Company company, String code)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from CommerceCurrency where companyId = ? and " +
					"code_ = ?")) {

			preparedStatement.setLong(1, company.getCompanyId());

			preparedStatement.setString(2, code);

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				return new CommerceCurrencyImpl();
			}

			return null;
		}
	}

	private ResultSet _fetchPrimaryCommerceCurrency(Company company)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from CommerceCurrency where companyId = ? and " +
					"primary_ = true and active_ = true")) {

			preparedStatement.setLong(1, company.getCompanyId());

			return preparedStatement.executeQuery();
		}
	}

	private BigDecimal _getExchangeRate(
		CommerceCurrency primaryCommerceCurrency,
		CommerceCurrency commerceCurrency) {

		BigDecimal exchangeRate = BigDecimal.ONE;

		for (String exchangeRateProviderKey :
				_exchangeRateProviderRegistry.getExchangeRateProviderKeys()) {

			ExchangeRateProvider exchangeRateProvider =
				_exchangeRateProviderRegistry.getExchangeRateProvider(
					exchangeRateProviderKey);

			if (exchangeRateProvider == null) {
				return null;
			}

			try {
				exchangeRate = exchangeRateProvider.getExchangeRate(
					primaryCommerceCurrency, commerceCurrency);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				return null;
			}
		}

		return exchangeRate;
	}

	private void _importDefaultValues(Company company) throws Exception {
		Class<?> clazz = getClass();

		String currenciesPath =
			"com/liferay/commerce/currency/service/impl/dependencies" +
				"/currencies.json";

		String countriesJSON = StringUtil.read(
			clazz.getClassLoader(), currenciesPath, false);

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(countriesJSON);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			String code = jsonObject.getString("code");

			CommerceCurrency commerceCurrency = _fetchByC_C(company, code);

			if (commerceCurrency == null) {
				String symbol = jsonObject.getString("symbol");

				RoundingTypeConfiguration roundingTypeConfiguration =
					_configurationProvider.getConfiguration(
						RoundingTypeConfiguration.class,
						new SystemSettingsLocator(
							RoundingTypeConstants.SERVICE_NAME));

				Map<Locale, String> formatPatternMap = HashMapBuilder.put(
					company.getLocale(),
					StringBundler.concat(
						symbol, StringPool.SPACE,
						CommerceCurrencyConstants.DECIMAL_FORMAT_PATTERN)
				).build();

				RoundingMode roundingMode =
					roundingTypeConfiguration.roundingMode();

				User defaultUser = company.getDefaultUser();

				User user = _userLocalService.getUser(defaultUser.getUserId());

				if (formatPatternMap.isEmpty()) {
					formatPatternMap.put(
						user.getLocale(),
						CommerceCurrencyConstants.DECIMAL_FORMAT_PATTERN);
				}

				ServiceContext serviceContext =
					ServiceContextThreadLocal.getServiceContext();

				Date date = new Date();

				if (serviceContext != null) {
					date = serviceContext.getCreateDate(date);
				}

				try (PreparedStatement preparedStatement =
						connection.prepareStatement(
							StringBundler.concat(
								"insert into CommerceCurrency (uuid_,",
								"commerceCurrencyId,companyId,userId,",
								"userName,createDate,modifiedDate,code_,name,",
								"symbol,rate,formatPattern,maxFractionDigits,",
								"minFractionDigits,roundingMode,primary_,",
								"priority,active_,lastPublishDate) values",
								"(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,",
								"?,?) "))) {

					preparedStatement.setString(1, _portalUUID.generate());
					preparedStatement.setLong(
						2, _counterLocalService.increment());
					preparedStatement.setLong(3, user.getCompanyId());
					preparedStatement.setLong(4, user.getUserId());
					preparedStatement.setString(5, user.getFullName());
					preparedStatement.setDate(
						6, new java.sql.Date(date.getTime()));
					preparedStatement.setDate(
						7, new java.sql.Date(date.getTime()));
					preparedStatement.setString(8, code);
					preparedStatement.setString(
						9,
						LocalizationUtil.updateLocalization(
							HashMapBuilder.put(
								company.getLocale(),
								jsonObject.getString("name")
							).build(),
							"", "Name",
							UpgradeProcessUtil.getDefaultLanguageId(
								company.getCompanyId())));
					preparedStatement.setString(10, symbol);

					CommerceCurrency primaryCommerceCurrency =
						new CommerceCurrencyImpl();

					primaryCommerceCurrency.setCode("USD");

					commerceCurrency = new CommerceCurrencyImpl();

					commerceCurrency.setCode(code);

					preparedStatement.setBigDecimal(
						11,
						_getExchangeRate(
							primaryCommerceCurrency, commerceCurrency));

					preparedStatement.setString(
						12,
						LocalizationUtil.updateLocalization(
							formatPatternMap, "", "FormatPattern",
							UpgradeProcessUtil.getDefaultLanguageId(
								company.getCompanyId())));
					preparedStatement.setInt(
						13, roundingTypeConfiguration.maximumFractionDigits());
					preparedStatement.setInt(
						14, roundingTypeConfiguration.minimumFractionDigits());
					preparedStatement.setString(15, roundingMode.name());
					preparedStatement.setBoolean(
						16, jsonObject.getBoolean("primary"));
					preparedStatement.setDouble(
						17, jsonObject.getDouble("priority"));
					preparedStatement.setBoolean(18, true);
					preparedStatement.setDate(19, null);

					preparedStatement.executeUpdate();
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceCurrencyImportDefaultValueProcess.class);

	private final CompanyLocalService _companyLocalService;
	private final ConfigurationProvider _configurationProvider;
	private final CounterLocalService _counterLocalService;
	private final ExchangeRateProviderRegistry _exchangeRateProviderRegistry;
	private final PortalUUID _portalUUID;
	private final UserLocalService _userLocalService;

}
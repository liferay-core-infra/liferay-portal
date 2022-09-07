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

import com.liferay.commerce.currency.internal.model.DefaultCommerceCurrencyImporter;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.util.ExchangeRateProviderRegistry;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.uuid.PortalUUID;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Date;
import java.util.List;

/**
 * @author Janis Zhang
 */
public class CommerceCurrencyDefaultValueImportUpgradeProcess
	extends UpgradeProcess {

	public CommerceCurrencyDefaultValueImportUpgradeProcess(
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
	protected void doUpgrade() {
		_companyLocalService.forEachCompany(
			company -> {
				try {
					if (_hasPrimaryCommerceCurrency(company)) {
						return;
					}

					_importDefaultValues(company);
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			});
	}

	private Boolean _hasPrimaryCommerceCurrency(Company company)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from CommerceCurrency where companyId = ? and " +
					"primary_ = true and active_ = true")) {

			preparedStatement.setLong(1, company.getCompanyId());

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return true;
				}

				return false;
			}
		}
	}

	private void _importDefaultValues(Company company) throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(company.getCompanyId());
		serviceContext.setLanguageId(
			LocaleUtil.toLanguageId(company.getLocale()));

		User defaultUser = company.getDefaultUser();

		serviceContext.setUserId(defaultUser.getUserId());

		Class<?> clazz = getClass();

		String currenciesPath =
			"com/liferay/commerce/currency/service/impl/dependencies" +
				"/currencies.json";

		String countriesJSON = StringUtil.read(
			clazz.getClassLoader(), currenciesPath, false);

		DefaultCommerceCurrencyImporter defaultCommerceCurrencyImporter =
			new DefaultCommerceCurrencyImporter(
				serviceContext, _configurationProvider, _counterLocalService,
				_userLocalService, _portalUUID, _exchangeRateProviderRegistry);

		List<CommerceCurrency> commerceCurrencies =
			defaultCommerceCurrencyImporter.getCommerceCurrency(countriesJSON);

		for (CommerceCurrency commerceCurrency : commerceCurrencies) {
			if (_isCommerceCurrencyExisting(
					company, commerceCurrency.getCode())) {

				return;
			}

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						StringBundler.concat(
							"insert into CommerceCurrency (uuid_,",
							"commerceCurrencyId,companyId,userId,",
							"userName,createDate,modifiedDate,code_,name,",
							"symbol,rate,formatPattern,maxFractionDigits,",
							"minFractionDigits,roundingMode,primary_,",
							"priority,active_) values",
							"(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) "))) {

				preparedStatement.setString(1, commerceCurrency.getUuid());
				preparedStatement.setLong(
					2, commerceCurrency.getCommerceCurrencyId());
				preparedStatement.setLong(3, company.getCompanyId());
				preparedStatement.setLong(4, company.getUserId());
				preparedStatement.setString(5, company.getUserName());

				Date createDate = commerceCurrency.getCreateDate();
				Date modifiedDate = commerceCurrency.getModifiedDate();

				preparedStatement.setDate(
					6, new java.sql.Date(createDate.getTime()));
				preparedStatement.setDate(
					7, new java.sql.Date(modifiedDate.getTime()));

				preparedStatement.setString(8, commerceCurrency.getCode());
				preparedStatement.setString(
					9,
					LocalizationUtil.updateLocalization(
						commerceCurrency.getNameMap(), "", "Name",
						UpgradeProcessUtil.getDefaultLanguageId(
							company.getCompanyId())));
				preparedStatement.setString(10, commerceCurrency.getSymbol());

				preparedStatement.setBigDecimal(11, commerceCurrency.getRate());

				preparedStatement.setString(
					12,
					LocalizationUtil.updateLocalization(
						commerceCurrency.getFormatPatternMap(), "",
						"FormatPattern",
						UpgradeProcessUtil.getDefaultLanguageId(
							company.getCompanyId())));
				preparedStatement.setInt(
					13, commerceCurrency.getMaxFractionDigits());
				preparedStatement.setInt(
					14, commerceCurrency.getMinFractionDigits());
				preparedStatement.setString(
					15, commerceCurrency.getRoundingMode());
				preparedStatement.setBoolean(16, commerceCurrency.getPrimary());
				preparedStatement.setDouble(17, commerceCurrency.getPriority());
				preparedStatement.setBoolean(18, true);

				preparedStatement.executeUpdate();
			}
		}
	}

	private Boolean _isCommerceCurrencyExisting(Company company, String code)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from CommerceCurrency where companyId = ? and " +
					"code_ = ?")) {

			preparedStatement.setLong(1, company.getCompanyId());

			preparedStatement.setString(2, code);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return true;
				}

				return false;
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceCurrencyDefaultValueImportUpgradeProcess.class);

	private final CompanyLocalService _companyLocalService;
	private final ConfigurationProvider _configurationProvider;
	private final CounterLocalService _counterLocalService;
	private final ExchangeRateProviderRegistry _exchangeRateProviderRegistry;
	private final PortalUUID _portalUUID;
	private final UserLocalService _userLocalService;

}
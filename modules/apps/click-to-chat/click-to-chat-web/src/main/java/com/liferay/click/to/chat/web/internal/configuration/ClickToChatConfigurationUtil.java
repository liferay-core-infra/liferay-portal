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

package com.liferay.click.to.chat.web.internal.configuration;

import com.liferay.osgi.util.service.Snapshot;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.util.Validator;

import java.util.Objects;

/**
 * @author José Abelenda
 */
public class ClickToChatConfigurationUtil {

	public static ClickToChatConfiguration getClickToChatConfiguration(
		long companyId, long groupId) {

		ConfigurationProvider configurationProvider =
			_configurationProviderSnapshot.get();

		try {
			ClickToChatConfiguration companyClickToChatConfiguration =
				configurationProvider.getCompanyConfiguration(
					ClickToChatConfiguration.class, companyId);

			if ((groupId == 0) ||
				Objects.equals(
					companyClickToChatConfiguration.siteSettingsStrategy(),
					"always-inherit")) {

				return companyClickToChatConfiguration;
			}

			ClickToChatConfiguration groupClickToChatConfiguration =
				configurationProvider.getGroupConfiguration(
					ClickToChatConfiguration.class, groupId);

			if (Objects.equals(
					companyClickToChatConfiguration.siteSettingsStrategy(),
					"always-override")) {

				return groupClickToChatConfiguration;
			}

			if (Objects.equals(
					companyClickToChatConfiguration.siteSettingsStrategy(),
					"inherit-or-override")) {

				if (Validator.isNotNull(
						groupClickToChatConfiguration.
							chatProviderAccountId()) &&
					Validator.isNotNull(
						groupClickToChatConfiguration.chatProviderId())) {

					return groupClickToChatConfiguration;
				}

				return companyClickToChatConfiguration;
			}

			return companyClickToChatConfiguration;
		}
		catch (ConfigurationException configurationException) {
			return ReflectionUtil.throwException(configurationException);
		}
	}

	private static final Snapshot<ConfigurationProvider>
		_configurationProviderSnapshot = new Snapshot<>(
			ClickToChatConfigurationUtil.class, ConfigurationProvider.class);

}
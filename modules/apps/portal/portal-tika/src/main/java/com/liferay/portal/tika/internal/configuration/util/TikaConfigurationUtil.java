/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tika.internal.configuration.util;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.tika.internal.configuration.TikaConfiguration;

import java.io.InputStream;

import java.util.Map;

import org.apache.tika.config.TikaConfig;

/**
 * @author Tina Tian
 */
public class TikaConfigurationUtil {

	public static TikaConfig getTikaConfig() {
		return _tikaConfig;
	}

	public static void updateConfiguration(Map<String, Object> properties) {
		TikaConfiguration tikaConfiguration =
			ConfigurableUtil.createConfigurable(
				TikaConfiguration.class, properties);

		String tikaConfigXml = tikaConfiguration.tikaConfigXml();

		Class<?> clazz = TikaConfigurationUtil.class;

		InputStream inputStream = clazz.getResourceAsStream(tikaConfigXml);

		if (inputStream == null) {
			ClassLoader classLoader = clazz.getClassLoader();

			inputStream = classLoader.getResourceAsStream(tikaConfigXml);

			if (inputStream == null) {
				classLoader = PortalClassLoaderUtil.getClassLoader();

				inputStream = classLoader.getResourceAsStream(tikaConfigXml);

				if (inputStream == null) {
					throw new IllegalArgumentException(
						"Unable to read tika configuration " + tikaConfigXml);
				}
			}
		}

		try {
			_tikaConfig = new TikaConfig(inputStream);
		}
		catch (Exception exception) {
			throw new IllegalStateException(
				"Unable to create tika configuration", exception);
		}

		_tikaConfiguration = tikaConfiguration;
	}

	public static boolean useForkProcess(String mimeType) {
		if (_tikaConfiguration.textExtractionForkProcessEnabled() &&
			ArrayUtil.contains(
				_tikaConfiguration.textExtractionForkProcessMimeTypes(),
				mimeType)) {

			if (_log.isDebugEnabled()) {
				_log.debug("Fork process is enabled for " + mimeType);
			}

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TikaConfigurationUtil.class);

	private static volatile TikaConfig _tikaConfig;
	private static volatile TikaConfiguration _tikaConfiguration;

}
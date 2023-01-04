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

package com.liferay.portal.tika.internal.util;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.tika.internal.configuration.TikaConfiguration;

import java.io.IOException;
import java.io.InputStream;

import java.util.Map;

import org.apache.tika.config.TikaConfig;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Shuyang Zhou
 * @author Jorge Díaz
 */
@Component(
	configurationPid = "com.liferay.portal.tika.internal.configuration.TikaConfiguration",
	service = TikaConfigHelper.class
)
public class TikaConfigHelper {

	public TikaConfig getTikaConfig() {
		return _tikaConfig;
	}

	public InputStream getTikaConfigInputStream() throws IOException {
		InputStream inputStream = TikaConfigHelper.class.getResourceAsStream(
			_tikaConfiguration.tikaConfigXml());

		if (inputStream != null) {
			return inputStream;
		}

		ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

		return classLoader.getResourceAsStream(
			_tikaConfiguration.tikaConfigXml());
	}

	public boolean isTextExtractionForkProcessEnabled() {
		return _tikaConfiguration.textExtractionForkProcessEnabled();
	}

	public boolean isTextExtractionForkProcessEnabled(String mimeType) {
		if (_tikaConfiguration.textExtractionForkProcessEnabled() &&
			ArrayUtil.contains(
				_tikaConfiguration.textExtractionForkProcessMimeTypes(),
				mimeType)) {

			if (_log.isDebugEnabled()) {
				_log.debug("Fork is enabled for " + mimeType);
			}

			return true;
		}

		return false;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_tikaConfiguration = ConfigurableUtil.createConfigurable(
			TikaConfiguration.class, properties);

		try {
			_tikaConfig = new TikaConfig(getTikaConfigInputStream());
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TikaConfigHelper.class);

	private volatile TikaConfig _tikaConfig;
	private volatile TikaConfiguration _tikaConfiguration;

}
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

package com.liferay.portal.kernel.settings;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.portlet.PortletIdCodec;

/**
 * @author Raymond Augé
 * @author Jorge Ferrer
 */
public class SettingsProviderUtil {

	public static Settings getSettings(SettingsLocator settingsLocator)
		throws SettingsException {

		Settings settings = settingsLocator.getSettings();

		if (settings instanceof FallbackKeys) {
			return settings;
		}

		String settingsId = settingsLocator.getSettingsId();

		settingsId = PortletIdCodec.decodePortletName(settingsId);

		FallbackKeys fallbackKeys = _fallbackKeysServiceTrackerMap.getService(
			settingsId);

		if (fallbackKeys != null) {
			settings = new FallbackSettings(settings, fallbackKeys);
		}

		return settings;
	}

	private static final ServiceTrackerMap<String, FallbackKeys>
		_fallbackKeysServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				SystemBundleUtil.getBundleContext(), FallbackKeys.class,
				"settingsId");

}
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

package com.liferay.portal.configuration.settings.internal;

import com.liferay.portal.kernel.exception.NoSuchPortletItemException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PortletItem;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.PortletItemLocalService;
import com.liferay.portal.kernel.settings.ArchivedSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.settings.SettingsException;
import com.liferay.portal.kernel.settings.SettingsFactory;
import com.liferay.portal.kernel.settings.SettingsFactoryUtil;
import com.liferay.portal.kernel.settings.SettingsLocator;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 * @author Jorge Ferrer
 */
@Component(service = SettingsFactory.class)
public class SettingsFactoryImpl implements SettingsFactory {

	@Override
	public ArchivedSettings getPortletInstanceArchivedSettings(
			long groupId, String portletId, String name)
		throws SettingsException {

		try {
			return new ArchivedSettingsImpl(
				_getPortletItem(groupId, portletId, name));
		}
		catch (PortalException portalException) {
			throw new SettingsException(portalException);
		}
	}

	@Override
	public List<ArchivedSettings> getPortletInstanceArchivedSettingsList(
		long groupId, String portletId) {

		List<ArchivedSettings> archivedSettingsList = new ArrayList<>();

		List<PortletItem> portletItems =
			_portletItemLocalService.getPortletItems(
				groupId, portletId,
				com.liferay.portal.kernel.model.PortletPreferences.class.
					getName());

		for (PortletItem portletItem : portletItems) {
			archivedSettingsList.add(new ArchivedSettingsImpl(portletItem));
		}

		return archivedSettingsList;
	}

	@Override
	public Settings getSettings(SettingsLocator settingsLocator)
		throws SettingsException {

		return SettingsFactoryUtil.getSettings(settingsLocator);
	}

	private PortletItem _getPortletItem(
			long groupId, String portletId, String name)
		throws PortalException {

		PortletItem portletItem = null;

		try {
			portletItem = _portletItemLocalService.getPortletItem(
				groupId, name, portletId, PortletPreferences.class.getName());
		}
		catch (NoSuchPortletItemException noSuchPortletItemException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchPortletItemException);
			}

			portletItem = _portletItemLocalService.updatePortletItem(
				PrincipalThreadLocal.getUserId(), groupId, name, portletId,
				PortletPreferences.class.getName());
		}

		return portletItem;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SettingsFactoryImpl.class);

	@Reference
	private PortletItemLocalService _portletItemLocalService;

}
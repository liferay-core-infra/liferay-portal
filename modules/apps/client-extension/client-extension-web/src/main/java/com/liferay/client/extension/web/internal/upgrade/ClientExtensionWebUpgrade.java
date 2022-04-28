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

package com.liferay.client.extension.web.internal.upgrade;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.BasePortletIdUpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Iván Zaera Avellón
 * @author Brian Wing Shun Chan
 */
@Component(immediate = true, service = UpgradeStepRegistrator.class)
public class ClientExtensionWebUpgrade implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"0.0.0", "1.0.0",
			new BasePortletIdUpgradeProcess() {

				@Override
				protected String[][] getRenamePortletIdsArray() {
					return _getRenamePortletIdsArray(
						connection, "remote_app_",
						"com_liferay_remote_app_web_internal_portlet_" +
							"RemoteAppEntryPortlet_");
				}

			});

		registry.register(
			"1.0.0", "2.0.0",
			new BasePortletIdUpgradeProcess() {

				@Override
				protected String[][] getRenamePortletIdsArray() {
					return new String[][] {
						{
							"com_liferay_remote_app_admin_web_portlet_" +
								"RemoteAppAdminPortlet",
							"com_liferay_client_extension_web_internal_" +
								"portlet_ClientExtensionAdminPortlet"
						}
					};
				}

			});

		registry.register(
			"2.0.0", "2.0.1",
			new BasePortletIdUpgradeProcess() {

				@Override
				protected String[][] getRenamePortletIdsArray() {
					return _getRenamePortletIdsArray(
						connection,
						"com_liferay_remote_app_web_internal_portlet_" +
							"RemoteAppEntryPortlet_",
						"com_liferay_client_extension_web_internal_portlet_" +
							"ClientExtensionEntryPortlet_");
				}

			});
	}

	private String[][] _getRenamePortletIdsArray(
		Connection connection, String oldPortletIdPrefix,
		String newPortletIdPrefix) {

		List<String[]> portletIds = new ArrayList<>();

		try (Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(
				"select clientExtensionEntryId FROM ClientExtensionEntry ")) {

			while (resultSet.next()) {
				long clientExtensionEntryId = resultSet.getLong(
					"clientExtensionEntryId");

				portletIds.add(
					new String[] {
						oldPortletIdPrefix + clientExtensionEntryId,
						newPortletIdPrefix + clientExtensionEntryId
					});
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return portletIds.toArray(new String[0][0]);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ClientExtensionWebUpgrade.class);

}
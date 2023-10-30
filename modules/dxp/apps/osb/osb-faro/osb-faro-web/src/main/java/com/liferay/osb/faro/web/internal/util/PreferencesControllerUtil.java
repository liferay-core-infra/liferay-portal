/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.util;

import com.liferay.osb.faro.model.FaroPreferences;
import com.liferay.osb.faro.service.FaroPreferencesLocalService;
import com.liferay.osb.faro.web.internal.constants.FaroPreferencesConstants;
import com.liferay.osb.faro.web.internal.model.preferences.WorkspacePreferences;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Joao Victor Alves
 */
public class PreferencesControllerUtil {

	public static long getOwnerId(long groupId, String scope, long userId)
		throws Exception {

		if (StringUtil.equals(scope, FaroPreferencesConstants.SCOPE_GROUP)) {
			return groupId;
		}
		else if (StringUtil.equals(
					scope, FaroPreferencesConstants.SCOPE_USER)) {

			return userId;
		}

		throw new Exception("Invalid scope " + scope);
	}

	public static WorkspacePreferences getWorkspacePreferences(
			FaroPreferencesLocalService faroPreferencesLocalService,
			long groupId, long ownerId)
		throws Exception {

		FaroPreferences faroPreferences =
			faroPreferencesLocalService.fetchFaroPreferences(groupId, ownerId);

		if (faroPreferences == null) {
			return new WorkspacePreferences();
		}

		return JSONUtil.readValue(
			faroPreferences.getPreferences(), WorkspacePreferences.class);
	}

	public static void removeIndividualSegmentPreferences(
			FaroPreferencesLocalService faroPreferencesLocalService,
			long groupId, String individualSegmentId, String scope, long userId)
		throws Exception {

		long ownerId = getOwnerId(groupId, scope, userId);

		WorkspacePreferences workspacePreferences = getWorkspacePreferences(
			faroPreferencesLocalService, groupId, ownerId);

		workspacePreferences.removeIndividualSegmentPreference(
			individualSegmentId);

		faroPreferencesLocalService.savePreferences(
			userId, groupId, ownerId,
			JSONUtil.writeValueAsString(workspacePreferences));
	}

}
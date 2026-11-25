/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.util;

import com.liferay.feature.flag.web.internal.feature.flag.FeatureFlagsBag;
import com.liferay.feature.flag.web.internal.feature.flag.FeatureFlagsBagProvider;
import com.liferay.portal.kernel.feature.flag.FeatureFlag;
import com.liferay.portal.kernel.feature.flag.FeatureFlagType;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portlet.PortalPreferencesWrapper;

import java.util.List;

/**
 * @author Thiago Buarque
 */
public class DeprecationFeatureFlagsUtil {

	public static void processDeprecationFeatureFlags(
		long companyId, FeatureFlagsBagProvider featureFlagsBagProvider,
		PortalPreferencesLocalService portalPreferencesLocalService) {

		PortalPreferences portalPreferences = _getPortalPreferences(
			companyId, portalPreferencesLocalService);

		boolean processed = GetterUtil.getBoolean(
			portalPreferences.getValue(
				FeatureFlagConstants.PREFERENCE_NAMESPACE,
				FeatureFlagConstants.PREFERENCE_KEY_DEPRECATION_PROCESSED));

		if (processed) {
			return;
		}

		FeatureFlagsBag featureFlagsBag =
			featureFlagsBagProvider.getOrCreateFeatureFlagsBag(companyId);

		List<FeatureFlag> deprecationFeatureFlags =
			featureFlagsBag.getFeatureFlags(
				FeatureFlagType.DEPRECATION.getPredicate());

		for (FeatureFlag deprecationFeatureFlag : deprecationFeatureFlags) {
			featureFlagsBagProvider.setEnabled(
				companyId, deprecationFeatureFlag.getKey(), false);
		}

		portalPreferences = _getPortalPreferences(
			companyId, portalPreferencesLocalService);

		portalPreferences.setValue(
			FeatureFlagConstants.PREFERENCE_NAMESPACE,
			FeatureFlagConstants.PREFERENCE_KEY_DEPRECATION_PROCESSED, "true");

		portalPreferencesLocalService.updatePreferences(
			companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY, portalPreferences);
	}

	private static PortalPreferences _getPortalPreferences(
		long companyId,
		PortalPreferencesLocalService portalPreferencesLocalService) {

		PortalPreferencesWrapper portalPreferencesWrapper =
			(PortalPreferencesWrapper)
				portalPreferencesLocalService.getPreferences(
					companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY);

		return portalPreferencesWrapper.getPortalPreferencesImpl();
	}

}
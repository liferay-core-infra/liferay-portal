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

package com.liferay.feature.flag.web.internal.manager;

import com.liferay.feature.flag.web.internal.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.PortalPreferencesWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = FeatureFlagPreferencesManager.class)
public class FeatureFlagPreferencesManager {

	public void addSubscriber(
		long companyId, BiConsumer<String, Boolean> biConsumer) {

		_subscribersMap.compute(
			companyId,
			(key, value) -> {
				if (value == null) {
					value = new ArrayList<>();
				}

				value.add(biConsumer);

				return value;
			});
	}

	public Boolean isEnabled(long companyId, String key) {
		if (Validator.isNull(
				_portalPreferencesLocalService.fetchPortalPreferences(
					companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY))) {

			return null;
		}

		PortalPreferences portalPreferences = _getPortalPreferences(companyId);

		String value = portalPreferences.getValue(_NAMESPACE, key);

		if (value == null) {
			return null;
		}

		return GetterUtil.getBoolean(value);
	}

	public void setEnabled(long companyId, String key, boolean enabled) {
		List<BiConsumer<String, Boolean>> biConsumers =
			_subscribersMap.getOrDefault(companyId, Collections.emptyList());

		for (BiConsumer<String, Boolean> biConsumer : biConsumers) {
			biConsumer.accept(key, enabled);
		}

		PortalPreferences portalPreferences = _getPortalPreferences(companyId);

		portalPreferences.setValue(_NAMESPACE, key, String.valueOf(enabled));

		_portalPreferencesLocalService.updatePreferences(
			companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY, portalPreferences);
	}

	private PortalPreferences _getPortalPreferences(long companyId) {
		PortalPreferencesWrapper portalPreferencesWrapper =
			(PortalPreferencesWrapper)
				_portalPreferencesLocalService.getPreferences(
					companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY);

		return portalPreferencesWrapper.getPortalPreferencesImpl();
	}

	private static final String _NAMESPACE = FeatureFlagConstants.FEATURE_FLAG;

	@Reference
	private PortalPreferencesLocalService _portalPreferencesLocalService;

	private final Map<Long, List<BiConsumer<String, Boolean>>> _subscribersMap =
		new ConcurrentHashMap<>();

}
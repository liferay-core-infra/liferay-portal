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

package com.liferay.feature.flag.web.internal.model;

import com.liferay.feature.flag.web.internal.manager.FeatureFlagPreferencesManager;
import com.liferay.portal.kernel.util.GetterUtil;

/**
 * @author Drew Brokke
 */
public class PreferenceAwareFeatureFlag extends FeatureFlagWrapper {

	public PreferenceAwareFeatureFlag(
		long companyId, FeatureFlag featureFlag,
		FeatureFlagPreferencesManager featureFlagPreferencesManager) {

		super(featureFlag);

		featureFlagPreferencesManager.addSubscriber(
			companyId,
			(key, enabled) -> {
				if (key.equals(featureFlag.getKey())) {
					_setEnabled(enabled);
				}
			});

		Boolean enabled = featureFlagPreferencesManager.isEnabled(
			companyId, featureFlag.getKey());

		if (enabled != null) {
			_setEnabled(enabled);
		}
	}

	@Override
	public boolean isEnabled() {
		return GetterUtil.getBoolean(_enabled, super.isEnabled());
	}

	private void _setEnabled(boolean enabled) {
		_enabled = enabled;
	}

	private Boolean _enabled;

}
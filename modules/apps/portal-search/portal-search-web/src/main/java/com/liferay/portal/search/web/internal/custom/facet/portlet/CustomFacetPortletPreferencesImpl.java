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

package com.liferay.portal.search.web.internal.custom.facet.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Optional;

import javax.portlet.PortletPreferences;

/**
 * @author Wade Cao
 */
public class CustomFacetPortletPreferencesImpl
	implements CustomFacetPortletPreferences {

	public CustomFacetPortletPreferencesImpl(
		Optional<PortletPreferences> portletPreferencesOptional) {

		_portletPreferences = portletPreferencesOptional.orElseThrow(
			() -> new IllegalArgumentException(
				"PortletPreferences is not present"));
	}

	@Override
	public String getAggregationField() {
		return _portletPreferences.getValue(
			CustomFacetPortletPreferences.PREFERENCE_KEY_AGGREGATION_FIELD,
			StringPool.BLANK);
	}

	@Override
	public String getCustomHeading() {
		return _portletPreferences.getValue(
			CustomFacetPortletPreferences.PREFERENCE_KEY_CUSTOM_HEADING,
			StringPool.BLANK);
	}

	@Override
	public String getFederatedSearchKey() {
		return _portletPreferences.getValue(
			CustomFacetPortletPreferences.PREFERENCE_KEY_FEDERATED_SEARCH_KEY,
			StringPool.BLANK);
	}

	@Override
	public int getFrequencyThreshold() {
		String value = _portletPreferences.getValue(
			CustomFacetPortletPreferences.PREFERENCE_KEY_FREQUENCY_THRESHOLD,
			StringPool.BLANK);

		if (Validator.isNull(value)) {
			return 1;
		}

		return GetterUtil.getInteger(value);
	}

	@Override
	public int getMaxTerms() {
		String value = _portletPreferences.getValue(
			CustomFacetPortletPreferences.PREFERENCE_KEY_MAX_TERMS,
			StringPool.BLANK);

		if (Validator.isNull(value)) {
			return 10;
		}

		return GetterUtil.getInteger(value);
	}

	@Override
	public String getOrder() {
		return _portletPreferences.getValue(
			CustomFacetPortletPreferencesImpl.PREFERENCE_KEY_ORDER,
			"count:desc");
	}

	@Override
	public String getParameterName() {
		return _portletPreferences.getValue(
			CustomFacetPortletPreferences.PREFERENCE_KEY_PARAMETER_NAME,
			StringPool.BLANK);
	}

	@Override
	public boolean isFrequenciesVisible() {
		String value = _portletPreferences.getValue(
			CustomFacetPortletPreferences.PREFERENCE_KEY_FREQUENCIES_VISIBLE,
			StringPool.BLANK);

		if (Validator.isNull(value)) {
			return true;
		}

		return GetterUtil.getBoolean(value);
	}

	private final PortletPreferences _portletPreferences;

}
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

package com.liferay.portal.search.web.internal.custom.filter.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Optional;

import javax.portlet.PortletPreferences;

/**
 * @author Igor Nazar
 * @author Luan Maoski
 */
public class CustomFilterPortletPreferencesImpl
	implements CustomFilterPortletPreferences {

	public CustomFilterPortletPreferencesImpl(
		Optional<PortletPreferences> portletPreferencesOptional) {

		_portletPreferences = portletPreferencesOptional.orElseThrow(
			() -> new IllegalArgumentException(
				"PortletPreferences is not present"));
	}

	@Override
	public String getBoost() {
		if (_portletPreferences == null) {
			return StringPool.BLANK;
		}

		return _portletPreferences.getValue(
			CustomFilterPortletPreferences.PREFERENCE_KEY_BOOST,
			StringPool.BLANK);
	}

	@Override
	public String getCustomHeading() {
		if (_portletPreferences == null) {
			return StringPool.BLANK;
		}

		return _portletPreferences.getValue(
			CustomFilterPortletPreferences.PREFERENCE_KEY_CUSTOM_HEADING,
			StringPool.BLANK);
	}

	@Override
	public String getFederatedSearchKey() {
		if (_portletPreferences == null) {
			return StringPool.BLANK;
		}

		return _portletPreferences.getValue(
			CustomFilterPortletPreferences.PREFERENCE_KEY_FEDERATED_SEARCH_KEY,
			StringPool.BLANK);
	}

	@Override
	public String getFilterField() {
		if (_portletPreferences == null) {
			return StringPool.BLANK;
		}

		return _portletPreferences.getValue(
			CustomFilterPortletPreferences.PREFERENCE_KEY_FILTER_FIELD,
			StringPool.BLANK);
	}

	@Override
	public String getFilterQueryType() {
		if (_portletPreferences == null) {
			return "match";
		}

		return _portletPreferences.getValue(
			CustomFilterPortletPreferences.PREFERENCE_KEY_FILTER_QUERY_TYPE,
			"match");
	}

	@Override
	public String getFilterValue() {
		if (_portletPreferences == null) {
			return StringPool.BLANK;
		}

		return _portletPreferences.getValue(
			CustomFilterPortletPreferences.PREFERENCE_KEY_FILTER_VALUE,
			StringPool.BLANK);
	}

	@Override
	public String getOccur() {
		if (_portletPreferences == null) {
			return "filter";
		}

		return _portletPreferences.getValue(
			CustomFilterPortletPreferences.PREFERENCE_KEY_OCCUR, "filter");
	}

	@Override
	public String getParameterName() {
		if (_portletPreferences == null) {
			return StringPool.BLANK;
		}

		return _portletPreferences.getValue(
			CustomFilterPortletPreferences.PREFERENCE_KEY_PARAMETER_NAME,
			StringPool.BLANK);
	}

	@Override
	public String getParentQueryName() {
		if (_portletPreferences == null) {
			return StringPool.BLANK;
		}

		return _portletPreferences.getValue(
			CustomFilterPortletPreferences.PREFERENCE_KEY_PARENT_QUERY_NAME,
			StringPool.BLANK);
	}

	@Override
	public String getQueryName() {
		if (_portletPreferences == null) {
			return StringPool.BLANK;
		}

		return _portletPreferences.getValue(
			CustomFilterPortletPreferences.PREFERENCE_KEY_QUERY_NAME,
			StringPool.BLANK);
	}

	@Override
	public boolean isDisabled() {
		if (_portletPreferences == null) {
			return false;
		}

		String value = _portletPreferences.getValue(
			CustomFilterPortletPreferences.PREFERENCE_KEY_DISABLED,
			StringPool.BLANK);

		if (Validator.isNull(value)) {
			return false;
		}

		return GetterUtil.getBoolean(value);
	}

	@Override
	public boolean isImmutable() {
		if (_portletPreferences == null) {
			return false;
		}

		String value = _portletPreferences.getValue(
			CustomFilterPortletPreferences.PREFERENCE_KEY_IMMUTABLE,
			StringPool.BLANK);

		if (Validator.isNull(value)) {
			return false;
		}

		return GetterUtil.getBoolean(value);
	}

	@Override
	public boolean isInvisible() {
		if (_portletPreferences == null) {
			return false;
		}

		String value = _portletPreferences.getValue(
			CustomFilterPortletPreferences.PREFERENCE_KEY_INVISIBLE,
			StringPool.BLANK);

		if (Validator.isNull(value)) {
			return false;
		}

		return GetterUtil.getBoolean(value);
	}

	private final PortletPreferences _portletPreferences;

}
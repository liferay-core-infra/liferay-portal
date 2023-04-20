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

package com.liferay.portal.search.web.internal.search.bar.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.web.internal.display.context.SearchScopePreference;

import java.util.Optional;

import javax.portlet.PortletPreferences;

/**
 * @author André de Oliveira
 */
public class SearchBarPortletPreferencesImpl
	implements SearchBarPortletPreferences {

	public SearchBarPortletPreferencesImpl(
		Optional<PortletPreferences> portletPreferencesOptional) {

		_portletPreferences = portletPreferencesOptional.orElseThrow(
			() -> new IllegalArgumentException(
				"PortletPreferences is not present"));
	}

	@Override
	public String getDestination() {
		return _portletPreferences.getValue(
			SearchBarPortletPreferences.PREFERENCE_KEY_DESTINATION,
			StringPool.BLANK);
	}

	@Override
	public String getFederatedSearchKey() {
		return _portletPreferences.getValue(
			SearchBarPortletPreferences.PREFERENCE_KEY_FEDERATED_SEARCH_KEY,
			StringPool.BLANK);
	}

	@Override
	public String getKeywordsParameterName() {
		return _portletPreferences.getValue(
			SearchBarPortletPreferences.PREFERENCE_KEY_KEYWORDS_PARAMETER_NAME,
			"q");
	}

	@Override
	public String getScopeParameterName() {
		return _portletPreferences.getValue(
			SearchBarPortletPreferences.PREFERENCE_KEY_SCOPE_PARAMETER_NAME,
			"scope");
	}

	@Override
	public SearchScopePreference getSearchScopePreference() {
		String value = _portletPreferences.getValue(
			SearchBarPortletPreferences.PREFERENCE_KEY_SEARCH_SCOPE,
			StringPool.BLANK);

		if (Validator.isNull(value)) {
			return SearchScopePreference.THIS_SITE;
		}

		return SearchScopePreference.getSearchScopePreference(value);
	}

	@Override
	public String getSearchScopePreferenceString() {
		SearchScopePreference searchScopePreference =
			getSearchScopePreference();

		return searchScopePreference.getPreferenceString();
	}

	@Override
	public boolean isInvisible() {
		String value = _portletPreferences.getValue(
			SearchBarPortletPreferences.PREFERENCE_KEY_INVISIBLE,
			StringPool.BLANK);

		if (Validator.isNull(value)) {
			return false;
		}

		return GetterUtil.getBoolean(value);
	}

	@Override
	public boolean isShowStagedResults() {
		String value = _portletPreferences.getValue(
			SearchBarPortletPreferences.PREFERENCE_KEY_SHOW_STAGED_RESULTS,
			StringPool.BLANK);

		if (Validator.isNull(value)) {
			return false;
		}

		return GetterUtil.getBoolean(value);
	}

	@Override
	public boolean isSuggestionsEnabled() {
		String value = _portletPreferences.getValue(
			SearchBarPortletPreferences.PREFERENCE_KEY_SUGGESTIONS_ENABLED,
			StringPool.BLANK);

		if (Validator.isNull(value)) {
			return true;
		}

		return GetterUtil.getBoolean(value);
	}

	@Override
	public boolean isUseAdvancedSearchSyntax() {
		String value = _portletPreferences.getValue(
			SearchBarPortletPreferences.
				PREFERENCE_KEY_USE_ADVANCED_SEARCH_SYNTAX,
			StringPool.BLANK);

		if (Validator.isNull(value)) {
			return false;
		}

		return GetterUtil.getBoolean(value);
	}

	private final PortletPreferences _portletPreferences;

}
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

package com.liferay.portal.search.web.internal.search.results.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsUtil;

import java.util.Optional;

import javax.portlet.PortletPreferences;

/**
 * @author Lino Alves
 */
public class SearchResultsPortletPreferencesImpl
	implements SearchResultsPortletPreferences {

	public SearchResultsPortletPreferencesImpl(
		Optional<PortletPreferences> portletPreferencesOptional) {

		_portletPreferences = portletPreferencesOptional.orElseThrow(
			() -> new IllegalArgumentException(
				"PortletPreferences is not present"));
	}

	@Override
	public String getFederatedSearchKey() {
		return _portletPreferences.getValue(
			SearchResultsPortletPreferences.PREFERENCE_KEY_FEDERATED_SEARCH_KEY,
			StringPool.BLANK);
	}

	@Override
	public String getFieldsToDisplay() {
		return _portletPreferences.getValue(
			SearchResultsPortletPreferences.PREFERENCE_KEY_FIELDS_TO_DISPLAY,
			StringPool.BLANK);
	}

	@Override
	public int getPaginationDelta() {
		String value = _portletPreferences.getValue(
			SearchResultsPortletPreferences.PREFERENCE_KEY_PAGINATION_DELTA,
			StringPool.BLANK);

		if (Validator.isNull(value)) {
			return GetterUtil.getInteger(
				PropsUtil.get(PropsKeys.SEARCH_CONTAINER_PAGE_DEFAULT_DELTA),
				20);
		}

		return GetterUtil.getInteger(value);
	}

	@Override
	public String getPaginationDeltaParameterName() {
		return _portletPreferences.getValue(
			SearchResultsPortletPreferences.
				PREFERENCE_KEY_PAGINATION_DELTA_PARAMETER_NAME,
			"delta");
	}

	@Override
	public String getPaginationStartParameterName() {
		return _portletPreferences.getValue(
			SearchResultsPortletPreferences.
				PREFERENCE_KEY_PAGINATION_START_PARAMETER_NAME,
			"start");
	}

	@Override
	public boolean isDisplayInDocumentForm() {
		String value = _portletPreferences.getValue(
			SearchResultsPortletPreferences.
				PREFERENCE_KEY_DISPLAY_IN_DOCUMENT_FORM,
			StringPool.BLANK);

		if (Validator.isNull(value)) {
			return false;
		}

		return GetterUtil.getBoolean(value);
	}

	@Override
	public boolean isHighlightEnabled() {
		String value = _portletPreferences.getValue(
			SearchResultsPortletPreferences.PREFERENCE_KEY_HIGHLIGHT_ENABLED,
			StringPool.BLANK);

		if (Validator.isNull(value)) {
			return true;
		}

		return GetterUtil.getBoolean(value);
	}

	@Override
	public boolean isViewInContext() {
		String value = _portletPreferences.getValue(
			SearchResultsPortletPreferences.PREFERENCE_KEY_VIEW_IN_CONTEXT,
			StringPool.BLANK);

		if (Validator.isNull(value)) {
			return true;
		}

		return GetterUtil.getBoolean(value);
	}

	private final PortletPreferences _portletPreferences;

}
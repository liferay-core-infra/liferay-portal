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

package com.liferay.portal.search.internal.suggestions.spi;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.internal.configuration.AsahSearchKeywordsConfiguration;
import com.liferay.portal.search.rest.dto.v1_0.SuggestionsContributorConfiguration;
import com.liferay.portal.search.suggestions.Suggestion;
import com.liferay.portal.search.suggestions.SuggestionBuilderFactory;
import com.liferay.portal.search.suggestions.SuggestionsContributorResults;
import com.liferay.portal.search.suggestions.SuggestionsContributorResultsBuilderFactory;

import java.net.HttpURLConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
public abstract class BaseAsahKeywordsSuggestionsContributor {

	@Activate
	protected void activate(Map<String, Object> properties) {
		asahSearchKeywordsConfiguration = ConfigurableUtil.createConfigurable(
			AsahSearchKeywordsConfiguration.class, properties);

		_portalCache =
			(PortalCache<String, JSONObject>)multiVMPool.getPortalCache(
				getClass().getName());
	}

	@Deactivate
	protected void deactivate() {
		multiVMPool.removePortalCache(getClass().getName());
	}

	protected SuggestionsContributorResults getSuggestionsContributorResults(
		AnalyticsSettingsManager analyticsSettingsManager,
		SearchContext searchContext, String sort,
		SuggestionBuilderFactory suggestionBuilderFactory,
		SuggestionsContributorConfiguration suggestionsContributorConfiguration,
		SuggestionsContributorResultsBuilderFactory
			suggestionsContributorResultsBuilderFactory) {

		if (!_isEnabled(
				analyticsSettingsManager, searchContext.getCompanyId())) {

			return null;
		}

		AnalyticsConfiguration analyticsConfiguration =
			_getAnalyticsConfiguration(
				analyticsSettingsManager, searchContext.getCompanyId());

		if (analyticsConfiguration == null) {
			return null;
		}

		Map<String, Object> attributes =
			(Map<String, Object>)
				suggestionsContributorConfiguration.getAttributes();

		if (!_exceedsCharacterThreshold(
				attributes, searchContext.getKeywords())) {

			return null;
		}

		JSONArray jsonArray = JSONUtil.getValueAsJSONArray(
			_get(
				analyticsConfiguration, asahSearchKeywordsConfiguration,
				searchContext.getCompanyId(),
				_getDisplayLanguageId(attributes, searchContext.getLocale()),
				_getGroupId(searchContext), _getMinCounts(attributes),
				GetterUtil.getInteger(
					suggestionsContributorConfiguration.getSize(), 5),
				sort),
			"JSONObject/_embedded", "JSONArray/search-keywords");

		if (jsonArray.length() == 0) {
			return null;
		}

		return suggestionsContributorResultsBuilderFactory.builder(
		).displayGroupName(
			suggestionsContributorConfiguration.getDisplayGroupName()
		).suggestions(
			_getSuggestions(jsonArray, searchContext, suggestionBuilderFactory)
		).build();
	}

	protected volatile AsahSearchKeywordsConfiguration
		asahSearchKeywordsConfiguration;

	@Reference
	protected JSONFactory jsonFactory;

	@Reference
	protected MultiVMPool multiVMPool;

	private JSONObject _convert(
		AnalyticsConfiguration analyticsConfiguration, String displayLanguageId,
		long groupId, int minCounts, int size, String sort) {

		try {
			Http.Options options = new Http.Options();

			options.addHeader(
				"OSB-Asah-Faro-Backend-Security-Signature",
				analyticsConfiguration.
					liferayAnalyticsFaroBackendSecuritySignature());
			options.addHeader(
				"OSB-Asah-Project-ID",
				analyticsConfiguration.liferayAnalyticsProjectId());

			String url = _getURL(
				analyticsConfiguration, displayLanguageId, groupId, minCounts,
				size, sort);

			if (_log.isDebugEnabled()) {
				_log.debug("Reading " + url);
			}

			options.setLocation(url);

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				HttpUtil.URLtoString(options));

			_validateResponse(jsonObject, options.getResponse());

			return jsonObject;
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private boolean _exceedsCharacterThreshold(
		Map<String, Object> attributes, String keywords) {

		int characterThreshold = _getCharacterThreshold(attributes);

		if (Validator.isBlank(keywords)) {
			if (characterThreshold == 0) {
				return true;
			}
		}
		else if (keywords.length() >= characterThreshold) {
			return true;
		}

		return false;
	}

	private JSONObject _get(
		AnalyticsConfiguration analyticsConfiguration,
		AsahSearchKeywordsConfiguration asahSearchKeywordsConfiguration,
		long companyId, String displayLanguageId, long groupId, int minCounts,
		int size, String sort) {

		String key = StringBundler.concat(
			StringPool.POUND, companyId, StringPool.POUND, minCounts,
			StringPool.POUND, displayLanguageId, StringPool.POUND, groupId,
			StringPool.POUND, sort);

		JSONObject jsonObject = _portalCache.get(key);

		if (jsonObject != null) {
			return jsonObject;
		}

		jsonObject = _convert(
			analyticsConfiguration, displayLanguageId, groupId, minCounts, size,
			sort);

		_portalCache.put(
			key, jsonObject, asahSearchKeywordsConfiguration.cacheTimeout());

		return jsonObject;
	}

	private AnalyticsConfiguration _getAnalyticsConfiguration(
		AnalyticsSettingsManager analyticsSettingsManager, long companyId) {

		try {
			return analyticsSettingsManager.getAnalyticsConfiguration(
				companyId);
		}
		catch (ConfigurationException configurationException) {
			_log.error(configurationException);
		}

		return null;
	}

	private int _getCharacterThreshold(Map<String, Object> attributes) {
		if (attributes == null) {
			return _CHARACTER_THRESHOLD;
		}

		return MapUtil.getInteger(
			attributes, "characterThreshold", _CHARACTER_THRESHOLD);
	}

	private String _getDisplayLanguageId(
		Map<String, Object> attributes, Locale locale) {

		if ((attributes == null) ||
			MapUtil.getBoolean(attributes, "matchDisplayLanguageId", true)) {

			return LanguageUtil.getBCP47LanguageId(locale);
		}

		return StringPool.BLANK;
	}

	private long _getGroupId(SearchContext searchContext) {
		long[] groupIds = searchContext.getGroupIds();

		if ((groupIds == null) || (groupIds.length == 0)) {
			return 0;
		}

		return groupIds[0];
	}

	private int _getMinCounts(Map<String, Object> attributes) {
		if (attributes == null) {
			return _MIN_COUNTS;
		}

		return MapUtil.getInteger(attributes, "minCounts", _MIN_COUNTS);
	}

	private List<Suggestion> _getSuggestions(
		JSONArray jsonArray, SearchContext searchContext,
		SuggestionBuilderFactory suggestionBuilderFactory) {

		List<Suggestion> suggestions = new ArrayList<>();

		String destinationBaseURL = StringBundler.concat(
			GetterUtil.getString(
				searchContext.getAttribute(
					"search.suggestions.destination.friendly.url"),
				"/search"),
			"?",
			GetterUtil.getString(
				searchContext.getAttribute(
					"search.suggestions.keywords.parameter.name"),
				"q"),
			"=");

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject itemJSONObject = jsonArray.getJSONObject(i);

			String keywords = itemJSONObject.getString("keywords");

			suggestions.add(
				suggestionBuilderFactory.builder(
				).attribute(
					"assetURL", destinationBaseURL + keywords
				).score(
					1.0F
				).text(
					itemJSONObject.getString("keywords")
				).build());
		}

		return suggestions;
	}

	private String _getURL(
		AnalyticsConfiguration analyticsConfiguration, String displayLanguageId,
		long groupId, int minCounts, int size, String sort) {

		StringBundler sb = new StringBundler(11);

		sb.append(analyticsConfiguration.liferayAnalyticsFaroBackendURL());
		sb.append("/api/1.0/pages/search-keywords?minCounts=");
		sb.append(minCounts);

		if (!Validator.isBlank(displayLanguageId)) {
			sb.append("&displayLanguageId=");
			sb.append(displayLanguageId);
		}

		if (groupId > 0) {
			sb.append("&groupId=");
			sb.append(groupId);
		}

		sb.append("&size=");
		sb.append(size);
		sb.append("&sort=");
		sb.append(sort);

		return sb.toString();
	}

	private boolean _isEnabled(
		AnalyticsSettingsManager analyticsSettingsManager, long companyId) {

		try {
			if (FeatureFlagManagerUtil.isEnabled("LPS-159643") &&
				analyticsSettingsManager.isAnalyticsEnabled(companyId)) {

				return true;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	private void _validateResponse(
		JSONObject jsonObject, Http.Response response) {

		if ((response.getResponseCode() == HttpURLConnection.HTTP_OK) &&
			jsonObject.has("_embedded")) {

			return;
		}

		throw new RuntimeException(
			StringBundler.concat(
				"Response body: ", jsonObject, "\nResponse code: ",
				response.getResponseCode()));
	}

	private static final int _CHARACTER_THRESHOLD = 2;

	private static final int _MIN_COUNTS = 5;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseAsahKeywordsSuggestionsContributor.class);

	private PortalCache<String, JSONObject> _portalCache;

}
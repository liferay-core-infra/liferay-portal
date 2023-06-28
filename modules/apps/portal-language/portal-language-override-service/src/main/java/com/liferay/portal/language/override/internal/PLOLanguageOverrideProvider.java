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

package com.liferay.portal.language.override.internal;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.language.LanguageOverrideProvider;
import com.liferay.portal.language.override.internal.provider.PLOOriginalTranslationThreadLocal;
import com.liferay.portal.language.override.internal.util.PLOLanguageOverrideProviderUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = LanguageOverrideProvider.class)
public class PLOLanguageOverrideProvider implements LanguageOverrideProvider {

	@Override
	public String get(String key, Locale locale) {
		DCLSingleton<Map<String, HashMap<String, String>>>
			ploEntriesMapDCLSingleton =
				PLOLanguageOverrideProviderUtil.getPloEntriesMapDCLSingleton();

		Map<String, HashMap<String, String>> ploEntriesMap =
			ploEntriesMapDCLSingleton.getSingleton(
				PLOLanguageOverrideProviderUtil.getSupplier());

		if (ploEntriesMap.isEmpty() ||
			PLOOriginalTranslationThreadLocal.isUseOriginalTranslation()) {

			return null;
		}

		Map<String, String> overrideMap = _getOverrideMap(
			ploEntriesMap, CompanyThreadLocal.getCompanyId(), locale);

		return overrideMap.get(key);
	}

	@Override
	public Set<String> keySet(Locale locale) {
		DCLSingleton<Map<String, HashMap<String, String>>>
			ploEntriesMapDCLSingleton =
				PLOLanguageOverrideProviderUtil.getPloEntriesMapDCLSingleton();

		Map<String, HashMap<String, String>> ploEntriesMap =
			ploEntriesMapDCLSingleton.getSingleton(
				PLOLanguageOverrideProviderUtil.getSupplier());

		if (ploEntriesMap.isEmpty() ||
			PLOOriginalTranslationThreadLocal.isUseOriginalTranslation()) {

			return Collections.emptySet();
		}

		Map<String, String> overrideMap = _getOverrideMap(
			ploEntriesMap, CompanyThreadLocal.getCompanyId(), locale);

		return overrideMap.keySet();
	}

	private Map<String, String> _getOverrideMap(
		Map<String, HashMap<String, String>> ploEntriesMap, long companyId,
		Locale locale) {

		Map<String, String> overrideMap = ploEntriesMap.get(
			PLOLanguageOverrideProviderUtil.encodeKey(
				companyId, _language.getLanguageId(locale)));

		if (overrideMap == null) {
			return Collections.emptyMap();
		}

		return overrideMap;
	}

	@Reference
	private Language _language;

}
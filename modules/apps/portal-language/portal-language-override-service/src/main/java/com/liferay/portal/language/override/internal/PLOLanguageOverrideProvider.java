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

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.language.LanguageOverrideProvider;
import com.liferay.portal.language.override.internal.provider.PLOOriginalTranslationThreadLocal;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = LanguageOverrideProvider.class)
public class PLOLanguageOverrideProvider implements LanguageOverrideProvider {

	@Override
	public ResourceBundle getOverrideResourceBundle(Locale locale) {
		Map<String, PLOLanguageOverrideProviderHelper.OverrideResourceBundle>
			overrideResourceBundles =
				_ploLanguageOverrideProviderHelper.
					getOverrideResourceBundlesDCLSingleton();

		if (overrideResourceBundles.isEmpty() ||
			PLOOriginalTranslationThreadLocal.isUseOriginalTranslation()) {

			return null;
		}

		return overrideResourceBundles.get(
			_ploLanguageOverrideProviderHelper.encodeKey(
				CompanyThreadLocal.getCompanyId(),
				_language.getLanguageId(locale)));
	}

	@Reference
	private Language _language;

	@Reference
	private PLOLanguageOverrideProviderHelper
		_ploLanguageOverrideProviderHelper;

}
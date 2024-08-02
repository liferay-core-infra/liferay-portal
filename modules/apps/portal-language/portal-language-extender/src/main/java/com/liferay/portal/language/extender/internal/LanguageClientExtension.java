/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.extender.internal;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.language.override.exception.PLOEntryImportException;
import com.liferay.portal.language.override.service.PLOEntryLocalService;

import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Objects;

import org.osgi.framework.Bundle;

/**
 * @author Thiago Buarque
 */
public class LanguageClientExtension {

	public LanguageClientExtension(
		CompanyLocalService companyLocalService,
		PLOEntryLocalService ploEntryLocalService,
		UserLocalService userLocalService) {

		_companyLocalService = companyLocalService;
		_ploEntryLocalService = ploEntryLocalService;
		_userLocalService = userLocalService;
	}

	public void addingBundle(Bundle bundle) {
		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		if (Validator.isNull(
				headers.get("Liferay-Client-Extension-Language")) ||
			_isAlreadyProcessed(bundle)) {

			return;
		}

		Enumeration<URL> enumeration = bundle.findEntries(
			headers.get("Liferay-Client-Extension-Language"),
			"Language_*.properties", true);

		while ((enumeration != null) && enumeration.hasMoreElements()) {
			URL url = enumeration.nextElement();

			File file = new File(url.getFile());

			String fileName = file.getName();

			try {
				Company company = _companyLocalService.getCompanyByWebId(
					PropsUtil.get(PropsKeys.COMPANY_DEFAULT_WEB_ID));

				User user = _userLocalService.getUserByScreenName(
					company.getCompanyId(),
					PropsUtil.get(PropsKeys.DEFAULT_ADMIN_SCREEN_NAME));

				String languageId = StringUtil.removeSubstring(
					fileName, "Language_");

				languageId = StringUtil.removeSubstring(
					languageId, ".properties");

				URLConnection urlConnection = url.openConnection();

				_ploEntryLocalService.importPLOEntries(
					company.getCompanyId(), user.getUserId(), languageId,
					PropertiesUtil.load(
						urlConnection.getInputStream(), "UTF-8"));

				if (_log.isInfoEnabled()) {
					_log.info("Imported \"" + fileName + "\" successfully");
				}
			}
			catch (PLOEntryImportException.InvalidTranslations
						ploEntryImportException) {

				for (Throwable throwable :
						ploEntryImportException.getSuppressed()) {

					_log.error(
						StringBundler.concat(
							"Unable to import \"", fileName, "\": ",
							throwable.getMessage()));
				}
			}
			catch (IOException | PortalException exception) {
				_log.error("Unable to import \"" + fileName + "\"", exception);
			}
		}
	}

	private boolean _isAlreadyProcessed(Bundle bundle) {
		File file = bundle.getDataFile(".liferay-client-extension-language");
		String lastModifiedString = String.valueOf(bundle.getLastModified());

		try {
			if ((file != null) && file.exists() &&
				Objects.equals(FileUtil.read(file), lastModifiedString)) {

				return true;
			}

			if (!file.exists()) {
				file.createNewFile();
			}

			FileUtil.write(file, lastModifiedString, true);
		}
		catch (IOException ioException) {
			ReflectionUtil.throwException(ioException);
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LanguageClientExtension.class);

	private final CompanyLocalService _companyLocalService;
	private final PLOEntryLocalService _ploEntryLocalService;
	private final UserLocalService _userLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.util;

import com.liferay.portal.kernel.model.CompanyConstants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Renan Vasconcelos
 */
public class OpenIdConnectProviderUtil {

	public static long getOAuthClientEntryId(
		long companyId, String providerName) {

		Map<String, Long> oAuthClientEntryIds = _oAuthClientEntryIds.get(
			companyId);

		if (oAuthClientEntryIds == null) {
			oAuthClientEntryIds = _oAuthClientEntryIds.get(
				CompanyConstants.SYSTEM);
		}

		if (oAuthClientEntryIds == null) {
			return 0;
		}

		Long oAuthClientEntryId = oAuthClientEntryIds.get(providerName);

		if (oAuthClientEntryId == null) {
			return 0;
		}

		return oAuthClientEntryId;
	}

	public static Map<String, Long> getoAuthClientEntryIdsByCompanyId(
		long companyId) {

		return _oAuthClientEntryIds.get(companyId);
	}

	public static void setoAuthClientEntryIds(
		long companyId, Map<String, Long> oAuthClientEntryIds) {

		_oAuthClientEntryIds.put(companyId, oAuthClientEntryIds);
	}

	private static final Map<Long, Map<String, Long>> _oAuthClientEntryIds =
		new ConcurrentHashMap<>();

}
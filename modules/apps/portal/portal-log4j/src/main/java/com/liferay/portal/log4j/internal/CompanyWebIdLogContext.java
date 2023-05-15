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

package com.liferay.portal.log4j.internal;

import com.liferay.portal.kernel.log.LogContext;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;

import java.util.Collections;
import java.util.Map;

/**
 * @author Hai Yu
 */
public class CompanyWebIdLogContext implements LogContext {

	@Override
	public Map<String, String> getContext(String logName) {
		long companyId = CompanyThreadLocal.getCompanyId();

		if (companyId == 0) {
			return Collections.emptyMap();
		}

		String webId = CompanyLocalServiceUtil.getWebId(companyId);

		if (webId == null) {
			Company company = CompanyLocalServiceUtil.fetchCompany(companyId);

			webId = company.getWebId();
		}

		return Collections.singletonMap("webId", webId);
	}

	@Override
	public String getName() {
		return "company";
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.impl;

import com.liferay.portal.kernel.template.TemplateRestricted;

/**
 * @author Brian Wing Shun Chan
 */
public class CompanyInfoImpl extends CompanyInfoBaseImpl {

	@Override
	@TemplateRestricted
	public String getKey() {
		return super.getKey();
	}

}
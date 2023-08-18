/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link ExpandoColumnService}.
 *
 * @author Brian Wing Shun Chan
 * @see ExpandoColumnService
 * @generated
 */
public class ExpandoColumnServiceWrapper
	implements ExpandoColumnService, ServiceWrapper<ExpandoColumnService> {

	public ExpandoColumnServiceWrapper() {
		this(null);
	}

	public ExpandoColumnServiceWrapper(
		ExpandoColumnService expandoColumnService) {

		_expandoColumnService = expandoColumnService;
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _expandoColumnService.getOSGiServiceIdentifier();
	}

	@Override
	public ExpandoColumnService getWrappedService() {
		return _expandoColumnService;
	}

	@Override
	public void setWrappedService(ExpandoColumnService expandoColumnService) {
		_expandoColumnService = expandoColumnService;
	}

	private ExpandoColumnService _expandoColumnService;

}
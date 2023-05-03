/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upload;

import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.upload.UploadServletRequest;

import javax.portlet.PortletRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 * @author Eduardo Lundgren
 * @author Marco Leo
 */
public interface UploadPortal {

	public UploadPortletRequest getUploadPortletRequest(
		PortletRequest portletRequest);

	public UploadServletRequest getUploadServletRequest(
		HttpServletRequest httpServletRequest);

	public UploadServletRequest getUploadServletRequest(
		HttpServletRequest httpServletRequest, int fileSizeThreshold,
		String location);

}
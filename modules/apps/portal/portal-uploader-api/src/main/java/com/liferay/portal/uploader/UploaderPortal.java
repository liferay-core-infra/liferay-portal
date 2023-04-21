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

package com.liferay.portal.uploader;

import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.upload.UploadServletRequest;

import javax.portlet.PortletRequest;

import javax.servlet.http.HttpServletRequest;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 * @author Eduardo Lundgren
 * @author Marco Leo
 */
@ProviderType
public interface UploaderPortal {

	public UploadPortletRequest getUploadPortletRequest(
		PortletRequest portletRequest);

	public UploadServletRequest getUploadServletRequest(
		HttpServletRequest httpServletRequest);

	public UploadServletRequest getUploadServletRequest(
		HttpServletRequest httpServletRequest, int fileSizeThreshold,
		String location, long maxRequestSize, long maxFileSize);

}
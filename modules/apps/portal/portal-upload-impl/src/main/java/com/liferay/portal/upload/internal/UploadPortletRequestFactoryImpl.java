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

package com.liferay.portal.upload.internal;

import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.upload.UploadPortletRequestImpl;
import com.liferay.portal.upload.factory.UploadPortletRequestFactory;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jiefeng Wu
 */
@Component(service = UploadPortletRequestFactory.class)
public class UploadPortletRequestFactoryImpl
	implements UploadPortletRequestFactory {

	@Override
	public UploadPortletRequest create(
		UploadServletRequest uploadServletRequest,
		PortletRequest portletRequest, String namespace) {

		return new UploadPortletRequestImpl(
			uploadServletRequest, portletRequest, namespace);
	}

}
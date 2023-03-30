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

package com.liferay.portal.uploader.internal;

import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.uploader.UploadServletRequestFactory;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jiefeng Wu
 */
@Component(service = UploadServletRequestFactory.class)
public class UploadServletRequestFactoryImpl
	implements UploadServletRequestFactory {

	@Override
	public UploadServletRequest create(HttpServletRequest httpServletRequest) {
		return new UploadServletRequestImpl(httpServletRequest);
	}

	@Override
	public UploadServletRequest create(
		HttpServletRequest httpServletRequest, int fileSizeThreshold,
		String location, long maxRequestSize, long maxFileSize) {

		return new UploadServletRequestImpl(
			httpServletRequest, fileSizeThreshold, location, maxRequestSize,
			maxFileSize);
	}

	@Override
	public UploadServletRequest create(
		HttpServletRequest httpServletRequest,
		Map<String, FileItem[]> fileParameters,
		Map<String, List<String>> regularParameters) {

		return new UploadServletRequestImpl(
			httpServletRequest, fileParameters, regularParameters);
	}

}
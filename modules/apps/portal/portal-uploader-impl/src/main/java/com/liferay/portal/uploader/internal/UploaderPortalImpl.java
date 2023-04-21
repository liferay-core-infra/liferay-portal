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

import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.servlet.NonSerializableObjectRequestWrapper;
import com.liferay.portal.kernel.servlet.PersistentHttpServletRequestWrapper;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.uploader.UploaderPortal;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.LiferayPortletUtil;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Brian Myunghun Kim
 * @author Jorge Ferrer
 * @author Raymond Augé
 * @author Eduardo Lundgren
 * @author Wesley Gong
 * @author Hugo Huijser
 * @author Juan Fernández
 * @author Marco Leo
 * @author Neil Griffin
 */
@Component(service = UploaderPortal.class)
public class UploaderPortalImpl implements UploaderPortal {

	@Override
	public UploadPortletRequest getUploadPortletRequest(
		PortletRequest portletRequest) {

		LiferayPortletRequest liferayPortletRequest =
			LiferayPortletUtil.getLiferayPortletRequest(portletRequest);

		DynamicServletRequest dynamicRequest =
			(DynamicServletRequest)
				liferayPortletRequest.getHttpServletRequest();

		HttpServletRequestWrapper requestWrapper =
			(HttpServletRequestWrapper)dynamicRequest.getRequest();

		return new UploadPortletRequestImpl(
			getUploadServletRequest(requestWrapper), liferayPortletRequest,
			_portal.getPortletNamespace(
				liferayPortletRequest.getPortletName()));
	}

	@Override
	public UploadServletRequest getUploadServletRequest(
		HttpServletRequest httpServletRequest) {

		return getUploadServletRequest(httpServletRequest, 0, null, 0, 0);
	}

	@Override
	public UploadServletRequest getUploadServletRequest(
		HttpServletRequest httpServletRequest, int fileSizeThreshold,
		String location, long maxRequestSize, long maxFileSize) {

		List<PersistentHttpServletRequestWrapper>
			persistentHttpServletRequestWrappers = new ArrayList<>();

		HttpServletRequest currentHttpServletRequest = httpServletRequest;

		while (currentHttpServletRequest instanceof HttpServletRequestWrapper) {
			if (currentHttpServletRequest instanceof UploadServletRequest) {
				return (UploadServletRequest)currentHttpServletRequest;
			}

			Class<?> currentRequestClass = currentHttpServletRequest.getClass();

			String currentRequestClassName = currentRequestClass.getName();

			if (!isUnwrapRequest(currentRequestClassName)) {
				break;
			}

			if (currentHttpServletRequest instanceof
					PersistentHttpServletRequestWrapper) {

				PersistentHttpServletRequestWrapper
					persistentHttpServletRequestWrapper =
						(PersistentHttpServletRequestWrapper)
							currentHttpServletRequest;

				persistentHttpServletRequestWrappers.add(
					persistentHttpServletRequestWrapper.clone());
			}

			HttpServletRequestWrapper httpServletRequestWrapper =
				(HttpServletRequestWrapper)currentHttpServletRequest;

			currentHttpServletRequest =
				(HttpServletRequest)httpServletRequestWrapper.getRequest();
		}

		if (ServerDetector.isWebLogic()) {
			currentHttpServletRequest = new NonSerializableObjectRequestWrapper(
				currentHttpServletRequest);
		}

		for (int i = persistentHttpServletRequestWrappers.size() - 1; i >= 0;
			 i--) {

			HttpServletRequestWrapper httpServletRequestWrapper =
				persistentHttpServletRequestWrappers.get(i);

			httpServletRequestWrapper.setRequest(currentHttpServletRequest);

			currentHttpServletRequest = httpServletRequestWrapper;
		}

		return new UploadServletRequestImpl(
			currentHttpServletRequest, fileSizeThreshold, location,
			maxRequestSize, maxFileSize);
	}

	protected boolean isUnwrapRequest(String currentRequestClassName) {
		for (String packageName : PropsValues.REQUEST_UNWRAP_PACKAGES) {
			if (currentRequestClassName.startsWith(packageName)) {
				return true;
			}
		}

		return false;
	}

	@Reference
	private Portal _portal;

}
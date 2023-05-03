/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upload.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.servlet.NonSerializableObjectRequestWrapper;
import com.liferay.portal.kernel.servlet.PersistentHttpServletRequestWrapper;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.upload.UploadPortal;
import com.liferay.portal.upload.UploadPortletRequestImpl;
import com.liferay.portal.upload.UploadServletRequestImpl;
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
@Component(service = UploadPortal.class)
public class UploadPortalImpl implements UploadPortal {

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
			getPortletNamespace(liferayPortletRequest.getPortletName()));
	}

	@Override
	public UploadServletRequest getUploadServletRequest(
		HttpServletRequest httpServletRequest) {

		return getUploadServletRequest(httpServletRequest, 0, null);
	}

	@Override
	public UploadServletRequest getUploadServletRequest(
		HttpServletRequest httpServletRequest, int fileSizeThreshold,
		String location) {

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
			currentHttpServletRequest, fileSizeThreshold, location);
	}

	protected String getPortletNamespace(String portletId) {
		return StringBundler.concat(
			StringPool.UNDERLINE, portletId, StringPool.UNDERLINE);
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
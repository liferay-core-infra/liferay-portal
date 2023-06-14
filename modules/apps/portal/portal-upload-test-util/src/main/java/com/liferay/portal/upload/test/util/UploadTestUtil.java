/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upload.test.util;

import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.upload.UploadPortal;

import java.lang.reflect.Constructor;

import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;

import javax.servlet.http.HttpServletRequest;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Jiefeng Wu
 */
public class UploadTestUtil {

	public static UploadPortletRequest createUploadPortletRequest(
			UploadServletRequest uploadServletRequest,
			PortletRequest portletRequest, String namespace)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(UploadTestUtil.class);

		Bundle implBundle = BundleUtil.getBundle(
			bundle.getBundleContext(), "com.liferay.portal.upload.impl");

		BundleWiring bundleWiring = implBundle.adapt(BundleWiring.class);

		ClassLoader classLoader = bundleWiring.getClassLoader();

		Class<?> clazz = classLoader.loadClass(
			"com.liferay.portal.upload.internal.UploadPortletRequestImpl");

		Constructor<?> constructor = clazz.getConstructor(
			UploadServletRequest.class, PortletRequest.class, String.class);

		return (UploadPortletRequest)constructor.newInstance(
			uploadServletRequest, portletRequest, namespace);
	}

	public static UploadServletRequest createUploadServletRequest(
		HttpServletRequest httpServletRequest,
		Map<String, FileItem[]> fileParameters,
		Map<String, List<String>> regularParameters) {

		UploadPortal uploadPortal = _uploadPortalSnapshot.get();

		UploadServletRequest uploadServletRequest =
			uploadPortal.getUploadServletRequest(httpServletRequest);

		if (fileParameters != null) {
			ReflectionTestUtil.setFieldValue(
				uploadServletRequest, "_fileParameters", fileParameters);
		}

		ReflectionTestUtil.setFieldValue(
			uploadServletRequest, "_liferayServletRequest", null);

		if (regularParameters != null) {
			ReflectionTestUtil.setFieldValue(
				uploadServletRequest, "_regularParameters", regularParameters);
		}

		return uploadServletRequest;
	}

	private static final Snapshot<UploadPortal> _uploadPortalSnapshot =
		new Snapshot<>(UploadTestUtil.class, UploadPortal.class);

}
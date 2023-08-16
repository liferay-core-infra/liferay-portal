/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.upload.format;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Alejandro Tardín
 */
public class MBMessageFormatUploadHandlerRegistryUtil {

	public static MBMessageFormatUploadHandler get(String format) {
		return _serviceTrackerMap.getService(format);
	}

	private static final ServiceTrackerMap<String, MBMessageFormatUploadHandler>
		_serviceTrackerMap;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			MBMessageFormatUploadHandlerRegistryUtil.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, MBMessageFormatUploadHandler.class, "format");
	}

}